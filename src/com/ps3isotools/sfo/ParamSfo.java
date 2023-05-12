package com.ps3isotools.sfo;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ParamSfo implements AutoCloseable {
	private File file = null;
	private RandomAccessFile raf = null;
	
	private String magic;
	private byte majorVersion;
	private byte minorVersion;
	private short reserved1;
	private int keysOffset;
	private int valuesOffset;
	private int itemCount;
	private ArrayList<ParamSfoEntry> items;
	
	public ParamSfo(String path) throws IOException {
		open(path);
	}
	
	@Override
	public void close() throws IOException {
		raf.close();
		raf = null;
		file = null;
		
		magic = null;
		majorVersion = (byte)0;
		minorVersion = (byte)0;
		reserved1 = 0;
		keysOffset = 0;
		valuesOffset = 0;
		itemCount = 0;
		
		items.clear();
		items = null;
	}
	
	public void open(String path) throws IOException {
		if(isOpen())
			close();
		
		file = new File(path);
		
		String mode = "rw";
		if(!file.canWrite())
			mode = "r";
		
		raf = new RandomAccessFile(path, mode);
		
		byte[] mgc = new byte[4];
		raf.read(mgc);
		magic = new String(mgc, StandardCharsets.UTF_8);
		
		if(magic.compareTo("\0PSF") != 0)
			throw new IOException("Not a valid param.sfo file");
		
		majorVersion = raf.readByte();
		minorVersion = raf.readByte();
		reserved1 = Short.reverseBytes(raf.readShort());
		keysOffset = Integer.reverseBytes(raf.readInt());
		valuesOffset = Integer.reverseBytes(raf.readInt());
		itemCount = Integer.reverseBytes(raf.readInt());
		
		items = new ArrayList<ParamSfoEntry>();
		
		for(int i = 0; i < itemCount; i++)
			items.add(new ParamSfoEntry(raf, this, i));
	}
	
	public boolean isOpen() {
		return raf != null;
	}
	
	public String getMagic() {
		return magic;
	}
	
	public byte getMajorVersion() {
		return majorVersion;
	}
	
	public byte getMinorVersion() {
		return minorVersion;
	}
	
	public short getReserved1() {
		return reserved1;
	}
	
	public int getKeysOffset() {
		return keysOffset;
	}
	
	public int getValuesOffset() {
		return valuesOffset;
	}
	
	public ArrayList<ParamSfoEntry> getItems() {
		return items;
	}
	
	public String getItemValue(String key) {
		String ret = null;
		
		try {
			for(int i = 0; i < items.size(); i++) {
				ParamSfoEntry item = items.get(i);
			
				short valueFormat = item.getValueFormat();
			
				if(item.getKey().compareTo(key) == 0 && (valueFormat == EntryFormat.UTF8 || valueFormat == EntryFormat.UTF8NULL))
					return item.getStringValue();
			}
		}
		catch(Exception e) {}
		
		return ret;
	}
	
	public void save() throws IOException {
		if(!file.canWrite())
			throw new IOException(file.getAbsolutePath() + " no write permissions");
		
		raf.write(magic.getBytes(StandardCharsets.UTF_8));
		raf.writeByte(majorVersion);
		raf.writeByte(minorVersion);
		raf.writeShort(Short.reverseBytes(majorVersion));
		
		keysOffset = 0x14 + items.size() * 0x10;
		raf.writeInt(Integer.reverseBytes(keysOffset));
		
		int sum = 0;
		for(int i = 0; i < items.size(); i++) {
			sum += items.get(i).getKey().length() + 1;
		}
		
		valuesOffset = keysOffset + sum;
		if(valuesOffset % 4 != 0)
			valuesOffset = ((valuesOffset / 4) + 1) * 4;
		
		raf.writeInt(Integer.reverseBytes(valuesOffset));
		
		itemCount = items.size();
		raf.writeInt(Integer.reverseBytes(itemCount));
		
		int lastKeyOffset = keysOffset;
		int lastValueOffset = valuesOffset;
		for(int i = 0; i < items.size(); i++) {
			ParamSfoEntry item = items.get(i);
			
			raf.seek(0x14 + i * 0x10);
			raf.writeShort(Short.reverseBytes((short)(lastKeyOffset - keysOffset)));
			raf.writeShort(Integer.reverseBytes(item.getValueFormat()));
			raf.writeInt(Integer.reverseBytes(item.getValueLength()));
			raf.writeInt(Integer.reverseBytes(item.getValueMaxLength()));
			raf.writeInt(Integer.reverseBytes(lastValueOffset - valuesOffset));
			
			
			raf.seek(lastKeyOffset);
			raf.write(item.getKey().getBytes(StandardCharsets.UTF_8));
			raf.writeByte((byte)0);
			lastKeyOffset = (int)raf.getFilePointer();
			
			raf.seek(lastValueOffset);
			raf.write(item.getBinaryValue());
			lastValueOffset = (int)raf.getFilePointer();
		}
	}
}
