package com.yahoo.ycsb;

public class RegionByteIterator extends ByteIterator {
	
	int _regionid;
	int off;
    byte[] buf;

	public RegionByteIterator(int regionid) {
		_regionid = regionid;
		buf = String.valueOf(regionid).getBytes();
		off = 0;
	}

	@Override
	public boolean hasNext() {
		return off < buf.length;
	}

	@Override
	public byte nextByte() {
		return buf[off++];
	}

	@Override
	public long bytesLeft() {
		return buf.length - off;
	}
	
	public byte[] toArray() {
		return buf;
	}
	
	/*public static void main(String[] args) {
		ByteIterator r = new RegionByteIterator(10);
		
		while(r.hasNext()) {
			byte newbyte = r.nextByte();
			System.out.println("printing byte by byte: " + newbyte );
		}
		
		byte[] a = r.toArray();
		System.out.println(a);
	}*/

}
