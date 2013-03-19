package jp.kshoji.driver.adkmidi.activity;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.kshoji.driver.adkmidi.device.MidiInputDevice;
import jp.kshoji.driver.adkmidi.device.MidiOutputDevice;
import jp.kshoji.driver.adkmidi.listener.OnMidiEventListener;
import jp.kshoji.driver.adkmidi.thread.AccessoryListeningThread;
import jp.kshoji.driver.adkmidi.util.Constants;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * base Activity for using ADK and MIDI Shield
 * 
 * @author K.Shoji
 */
public abstract class AbstractMidiActivity extends Activity implements OnMidiEventListener {
	private static final String ACTION_USB_PERMISSION = "jp.kshoji.driver.adkmidi.action.USB_PERMISSION";

	private UsbManager usbManager;
	private PendingIntent permissionIntent;
	boolean permissionRequestPending;

	UsbAccessory usbAccessory;
	AccessoryListeningThread accessoryListeningThread;
	ParcelFileDescriptor accessoryDescriptor;
	FileInputStream inputStream;
	FileOutputStream outputStream;

	private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
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

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (usbAccessory != null) {
			return usbAccessory;
		}
		return super.onRetainNonConfigurationInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		closeAccessory();
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterReceiver(usbBroadcastReceiver);
		super.onDestroy();
	}

	/**
	 * Opens the UsbAccesory
	 * 
	 * @param accessory
	 */
	private void openAccessory(UsbAccessory accessory) {
		accessoryDescriptor = usbManager.openAccessory(accessory);
		if (accessoryDescriptor != null) {
			usbAccessory = accessory;
			
			FileDescriptor descriptor = accessoryDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(descriptor);
			outputStream = new FileOutputStream(descriptor);

			if (accessoryListeningThread != null) {
				accessoryListeningThread.stopThread();
			}
			accessoryListeningThread = new AccessoryListeningThread(inputStream, new MidiInputDevice(this));
			accessoryListeningThread.start();
			midiOutputDevice = new MidiOutputDevice(outputStream);
			
			onDeviceAttached();
			
			Log.d(Constants.TAG, "accessory opened");
		} else {
			Log.d(Constants.TAG, "accessory open fail");
		}
	}

	/**
	 * Closes the UsbAccesory
	 */
	private void closeAccessory() {
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

	/**
	 * Will be called when accessory is attached.
	 */
	protected void onDeviceAttached() {
		// do nothing. must be override
	}

	/**
	 * Will be called when accessory is removed.
	 */
	protected void onDeviceDetached() {
		// do nothing. must be override
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
