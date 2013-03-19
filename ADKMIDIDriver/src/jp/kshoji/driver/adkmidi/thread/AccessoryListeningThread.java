package jp.kshoji.driver.adkmidi.thread;

import java.io.IOException;
import java.io.InputStream;

import jp.kshoji.driver.adkmidi.util.MidiMessage;
import jp.kshoji.driver.adkmidi.util.MidiParser;
import android.os.Handler;
import android.os.Message;

/**
 * Listen the accessory incoming data
 * 
 * @author K.Shoji
 *
 */
public class AccessoryListeningThread extends Thread {
	private MidiParser midiParser = new MidiParser();
	private final Handler handler;
	private final InputStream inputStream;
	private boolean isStop = false;
	
	public AccessoryListeningThread(InputStream inputStream, Handler handler) {
		this.handler = handler;
		this.inputStream = inputStream;
	}
	
	@Override
	public void run() {
		int readLength = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (readLength >= 0) {
			try {
				readLength = inputStream.read(buffer);
			} catch (IOException e) {
				// maybe disconnected
				break;
			}
			
			if (isStop) {
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
	
	public void stopThread() {
		isStop = true;
		while (true) {
			if (!isAlive()) {
				break;
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// ignore exception
			}
		}
	}
}