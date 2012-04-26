/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.kshoji.driver.adkmidi.activity;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.kshoji.driver.adkmidi.device.MidiOutputDevice;
import jp.kshoji.driver.adkmidi.listener.OnMidiEventListener;
import jp.kshoji.driver.adkmidi.util.Constants;
import jp.kshoji.driver.adkmidi.util.MidiMessage;
import jp.kshoji.driver.adkmidi.util.MidiParser;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public abstract class AbstractMidiActivity extends Activity implements Runnable, OnMidiEventListener {
	private static final String ACTION_USB_PERMISSION = "jp.kshoji.driver.adkmidi.action.USB_PERMISSION";

	private UsbManager usbManager;
	private PendingIntent permissionIntent;
	boolean permissionRequestPending;

	UsbAccessory usbAccessory;
	ParcelFileDescriptor accessoryDescriptor;
	FileInputStream inputStream;
	FileOutputStream outputStream;

	private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(@SuppressWarnings("unused") Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(Constants.TAG, "permission denied for accessory " + accessory);
					}
					permissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(accessory)) {
					closeAccessory();
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		usbManager = UsbManager.getInstance(this);
		permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(usbBroadcastReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			usbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(usbAccessory);
		}

		midiOutputDevice = null;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (usbAccessory != null) {
			return usbAccessory;
		}
		return super.onRetainNonConfigurationInstance();
	}

	@Override
	public void onResume() {
		if (inputStream != null && outputStream != null) {
			super.onResume();
			return;
		}
		if (usbManager == null) {
			super.onResume();
			return;
		}

		UsbAccessory[] accessories = usbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (usbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (usbBroadcastReceiver) {
					if (!permissionRequestPending) {
						usbManager.requestPermission(accessory, permissionIntent);
						permissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(Constants.TAG, "accessory is null");
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		closeAccessory();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(usbBroadcastReceiver);
		super.onDestroy();
	}

	void openAccessory(UsbAccessory accessory) {
		accessoryDescriptor = usbManager.openAccessory(accessory);
		if (accessoryDescriptor != null) {
			usbAccessory = accessory;
			
			FileDescriptor descriptor = accessoryDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(descriptor);
			outputStream = new FileOutputStream(descriptor);

			Thread thread = new Thread(this);
			thread.start();
			midiOutputDevice = new MidiOutputDevice(outputStream);
			
			onDeviceAttached();
			
			Log.d(Constants.TAG, "accessory opened");
		} else {
			Log.d(Constants.TAG, "accessory open fail");
		}
	}

	void closeAccessory() {
		midiOutputDevice = null;

		try {
			if (accessoryDescriptor != null) {
				accessoryDescriptor.close();
			}
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		} finally {
			accessoryDescriptor = null;
			usbAccessory = null;
		}
		
		onDeviceDetached();
	}

	protected void onDeviceAttached() {
		// do nothing. must be override
	}
	
	protected void onDeviceDetached() {
		// do nothing. must be override
	}
	
	private MidiParser midiParser = new MidiParser();

	@Override
	public void run() {
		int readLength = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (readLength >= 0) {
			try {
				readLength = inputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			// MIDI signal received
			for (i = 0; i < readLength; i++) {
				MidiMessage midiEvent = midiParser.parseMidiEvent(buffer[i]);
				if (midiEvent != null) {
					Message message = Message.obtain(handler);
					message.obj = midiEvent;
					handler.sendMessage(message);
				}
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			MidiMessage o = (MidiMessage) msg.obj;
			handleMidiMessage(o);
		}
	};

	protected void handleMidiMessage(MidiMessage o) {
		switch (o.getByte1() & 0xf0) {
		case 0x80: // note off
			onMidiNoteOff(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0x90: // note on
			onMidiNoteOn(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xa0: // control polyphonic key pressure
			onMidiPolyphonicAftertouch(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xb0: // control change
			onMidiControlChange(o.getByte1() & 0xf, o.getByte2(), o.getByte3());
			break;
		case 0xc0: // program change:2bytes
			onMidiProgramChange(o.getByte1() & 0xf, o.getByte2());
			break;
		case 0xd0: // channel after-touch:2bytes
			onMidiChannelAftertouch(o.getByte1() & 0xf, o.getByte2());
			break;
		case 0xe0: // pitch bend
			onMidiPitchWheel(o.getByte1() & 0xf, o.getByte2() | (o.getByte3() << 8));
			break;
		case 0xf0: // sysex
			onMidiSystemExclusive(o.getSystemExclusive());
			break;
		default:
			// illegal state
			break;
		}
	}

	private MidiOutputDevice midiOutputDevice;

	/**
	 * get MIDI output device, if available.
	 * 
	 * @return
	 */
	public final MidiOutputDevice getMidiOutputDevice() {
		return midiOutputDevice;
	}
}
