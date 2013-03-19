package jp.kshoji.driver.adkmidi.listener;

/**
 * Listener for MIDI events
 * 
 * @author K.Shoji
 */
public interface OnMidiEventListener {
	
	/**
	 * SysEx
	 * 
	 * @param systemExclusive
	 */
	void onMidiSystemExclusive(byte[] systemExclusive);
	
	/**
	 * Note-off
	 * 
	 * @param channel
	 * @param note
	 * @param velocity
	 */
	void onMidiNoteOff(int channel, int note, int velocity);
	
	/**
	 * Note-on
	 * 
	 * @param channel
	 * @param note
	 * @param velocity
	 */
	void onMidiNoteOn(int channel, int note, int velocity);
	
	/**
	 * Poly-KeyPress
	 * 
	 * @param channel
	 * @param note
	 * @param pressure
	 */
	void onMidiPolyphonicAftertouch(int channel, int note, int pressure);
	
	/**
	 * Control Change
	 * 
	 * @param channel
	 * @param function
	 * @param value
	 */
	void onMidiControlChange(int channel, int function, int value);
	
	/**
	 * Program Change
	 * 
	 * @param channel
	 * @param program
	 */
	void onMidiProgramChange(int channel, int program);
	
	/**
	 * Channel Pressure
	 * 
	 * @param channel
	 * @param pressure
	 */
	void onMidiChannelAftertouch(int channel, int pressure);
	
	/**
	 * PitchBend Change
	 * 
	 * @param channel
	 * @param lsb
	 * @param msb
	 */
	void onMidiPitchWheel(int channel, int amount);
}
