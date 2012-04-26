package jp.kshoji.driver.adkmidi.util;

public class MidiMessage {

	private int byte1;
	private int byte2;
	private int byte3;
	private byte[] systemExclusive;

	public MidiMessage(int byte1, int byte2, int byte3) {
		this.byte1 = byte1;
		this.byte2 = byte2;
		this.byte3 = byte3;
	}

	public MidiMessage(int byte1, byte[]bytes) {
		this.byte1 = byte1;
		this.systemExclusive = bytes;
	}
	
	/**
	 * @return the byte1
	 */
	public int getByte1() {
		return byte1;
	}

	/**
	 * @return the byte2
	 */
	public int getByte2() {
		return byte2;
	}

	/**
	 * @return the byte3
	 */
	public int getByte3() {
		return byte3;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getSystemExclusive() {
		return systemExclusive;
	}
}
