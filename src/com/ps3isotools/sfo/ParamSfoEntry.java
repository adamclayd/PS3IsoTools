package com.ps3isotools.sfo;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ParamSfoEntry {
	private short KeyOffset;
	private short ValueFormat;
	private int ValueLength;
	private int ValueMaxLength;
	private int ValueOffset;
	private String Key;
	private byte[] BinaryValue;
	
	public ParamSfoEntry(RandomAccessFile raf, ParamSfo sfo, int index) throws IOException {
		final int indexOffset = 0x14;
		final int indexEntryLength = 0x10;
		
		raf.seek(indexOffset + index * indexEntryLength);
		
		KeyOffset = Short.reverseBytes(raf.readShort());
		ValueFormat = Short.reverseBytes(raf.readShort());
		ValueLength = Integer.reverseBytes(raf.readInt());
		ValueMaxLength = Integer.reverseBytes(raf.readInt());
		ValueOffset = Integer.reverseBytes(raf.readInt());
		
		raf.seek(sfo.getKeysOffset() + KeyOffset);
		
		byte tmp;
		StringBuilder sb = new StringBuilder(32);
		while((tmp = raf.readByte()) != 0)
			sb.append((char)tmp);
		
		Key = sb.toString();
		
		raf.seek(sfo.getValuesOffset() + ValueOffset);
		BinaryValue = new byte[ValueMaxLength];
		raf.read(BinaryValue);
	}
	
	public String getStringValue() throws SfoEntryValueFormatNotUTF8Exception {
		if(ValueFormat == EntryFormat.UTF8 || ValueFormat == EntryFormat.UTF8NULL) {
			String ret = new String(BinaryValue, 0, ValueLength, StandardCharsets.UTF_8);
			
			if(System.lineSeparator() != "\n")
				ret = ret.replaceAll("\n", System.lineSeparator());
			
			return ret;
		}
		
		throw new SfoEntryValueFormatNotUTF8Exception("Current value format is not a string");
	}
	
	public void setStringValue(String value) throws SfoEntryValueFormatNotUTF8Exception, SfoEntryMaxValueSizeExceededException {
		if(ValueFormat != EntryFormat.UTF8 && ValueFormat != EntryFormat.UTF8NULL)
			throw new SfoEntryValueFormatNotUTF8Exception("Current value format is not a string");
		
		String sane = (value != null ? value.strip() : "");
		
		if(System.lineSeparator() != "\n")
			sane = sane.replaceAll(System.lineSeparator(), "\n");
		
		byte[] tmp = sane.getBytes(StandardCharsets.UTF_8);
		int newLen = tmp.length;
		
		if (ValueFormat == EntryFormat.UTF8NULL)
            newLen++;
		
		if(newLen > ValueMaxLength)
			throw new SfoEntryMaxValueSizeExceededException("String value is too large to store in the sfo entry");
		
		System.arraycopy(tmp, 0, BinaryValue, 0, tmp.length);
		
		if(tmp.length < BinaryValue.length)
			Arrays.fill(BinaryValue, tmp.length, BinaryValue.length - tmp.length, (byte)0);
		
		ValueLength = newLen;
	}
	
	public short getValueFormat() {
		return ValueFormat;
	}
	
	public int getValueLength() {
		return ValueLength;
	}
	
	public int getValueMaxLength() {
		return ValueMaxLength;
	}
	
	public int getValueOffset() {
		return ValueOffset;
	}
	
	public String getKey() {
		return Key;
	}
	
	public byte[] getBinaryValue() {
		return BinaryValue;
	}
}
