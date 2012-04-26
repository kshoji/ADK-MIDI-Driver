package jp.kshoji.driver.adkmidi.listener;

/**
 * Listener for MIDI events
 * 
 * @author K.Shoji
 */
public interface OnMidiEventListener {
	
	/**
	 * Code Index Number : 0x4, 0x5, 0x6, 0x7
	 * @param systemExclusive
	 */
	void onMidiSystemExclusive(byte[] systemExclusive);
	
	/**
	 * Code Index Number : 0x8
	 * @param channel
	 * @param note
	 * @param velocity
	 */
	void onMidiNoteOff(int channel, int note, int velocity);
	
	/**
	 * Code Index Number : 0x9
	 * @param channel
	 * @param note
	 * @param velocity
	 */
	void onMidiNoteOn(int channel, int note, int velocity);
	
	/**
	 * Code Index Number : 0xa
	 * @param channel
	 * @param note
	 * @param pressure
	 */
	void onMidiPolyphonicAftertouch(int channel, int note, int pressure);
	
	/**
	 * Code Index Number : 0xb
	 * @param channel
	 * @param function
	 * @param value
	 */
	void onMidiControlChange(int channel, int function, int value);
	
	/**
	 * Code Index Number : 0xc
	 * @param channel
	 * @param program
	 */
	void onMidiProgramChange(int channel, int program);
	
	/**
	 * Code Index Number : 0xd
	 * @param channel
	 * @param pressure
	 */
	void onMidiChannelAftertouch(int channel, int pressure);
	
	/**
	 * Code Index Number : 0xe
	 * @param channel
	 * @param lsb
	 * @param msb
	 */
	void onMidiPitchWheel(int channel, int amount);
}
