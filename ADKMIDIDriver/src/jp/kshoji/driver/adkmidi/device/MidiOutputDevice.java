package jp.kshoji.driver.adkmidi.device;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import jp.kshoji.driver.adkmidi.listener.OnMidiEventListener;
import jp.kshoji.driver.adkmidi.util.Constants;

/**
 * MIDI Output Device
 * 
 * @author K.Shoji
 */
public class MidiOutputDevice implements OnMidiEventListener {

	private OutputStream outputStream;

	public MidiOutputDevice(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void onMidiSystemExclusive(byte[] systemExclusive) {
		try {
			outputStream.write(systemExclusive);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiNoteOff(int channel, int note, int velocity) {
		try {
			outputStream.write(0x80 | (channel & 0xf));
			outputStream.write(note);
			outputStream.write(velocity);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiNoteOn(int channel, int note, int velocity) {
		try {
			outputStream.write(0x90 | (channel & 0xf));
			outputStream.write(note);
			outputStream.write(velocity);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiPolyphonicAftertouch(int channel, int note, int pressure) {
		try {
			outputStream.write(0xa0 | (channel & 0xf));
			outputStream.write(note);
			outputStream.write(pressure);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiControlChange(int channel, int function, int value) {
		try {
			outputStream.write(0xb0 | (channel & 0xf));
			outputStream.write(function);
			outputStream.write(value);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiProgramChange(int channel, int program) {
		try {
			outputStream.write(0xc0 | (channel & 0xf));
			outputStream.write(program);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiChannelAftertouch(int channel, int pressure) {
		try {
			outputStream.write(0xd0 | (channel & 0xf));
			outputStream.write(pressure);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}

	@Override
	public void onMidiPitchWheel(int channel, int amount) {
		try {
			outputStream.write(0xe0 | (channel & 0xf));
			outputStream.write(amount & 0xff);
			outputStream.write((amount >> 7) & 0xff);
		} catch (IOException e) {
			Log.d(Constants.TAG, "IOException", e);
		}
	}
}
