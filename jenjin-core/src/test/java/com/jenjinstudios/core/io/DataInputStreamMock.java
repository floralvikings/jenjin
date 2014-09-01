package com.jenjinstudios.core.io;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class DataInputStreamMock
{
	private final InputStream in = Mockito.mock(InputStream.class);
	private OngoingStubbing<Integer> when;

	public DataInputStreamMock() throws IOException {
		when(in.read(any(byte[].class), anyInt(), anyInt())).thenCallRealMethod();
		when = when(in.read());
	}

	public void mockReadShort(short s) {
		byte[] bytes = ByteBuffer.allocate(2).putShort(s).array();
		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff);
	}

	public void mockReadBoolean(boolean b) {
		when = when.thenReturn(b ? 1 : 0);
	}

	public void mockReadUtf(String s) throws IOException {
		byte[] bytes = ByteBuffer.allocate(2).putShort((short) s.length()).array();
		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff);
		bytes = s.getBytes("UTF-8");
		for (byte b : bytes)
		{
			when = when.thenReturn((int) b);
		}
	}

	public void mockReadInt(int i) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();

		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
				thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff);
	}

	public void mockReadLong(long l) {
		byte[] bytes = ByteBuffer.allocate(8).putLong(l).array();
		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
				thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff).
				thenReturn(bytes[4] & 0xff).thenReturn(bytes[5] & 0xff).
				thenReturn(bytes[6] & 0xff).thenReturn(bytes[7] & 0xff);
	}

	public void mockReadDouble(double d) {
		byte[] bytes = ByteBuffer.allocate(8).putDouble(d).array();
		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
				thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff).
				thenReturn(bytes[4] & 0xff).thenReturn(bytes[5] & 0xff).
				thenReturn(bytes[6] & 0xff).thenReturn(bytes[7] & 0xff);
	}

	public void mockReadFloat(float f) {
		byte[] bytes = ByteBuffer.allocate(4).putFloat(f).array();

		when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
				thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff);
	}

	public void mockReadByte(byte b) {
		when = when.thenReturn(b & 0xff);
	}

	public InputStream getIn() { return in; }
}
