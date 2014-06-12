package com.jenjinstudios.io;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Caleb Brinkman
 */
public class DataInputStreamMock
{
	private InputStream in = Mockito.mock(InputStream.class);
	private OngoingStubbing<Integer> when;

	public DataInputStreamMock() throws IOException {
		when(in.read(any(byte[].class), anyInt(), anyInt())).thenCallRealMethod();
		when = when(in.read());
	}

	public void mockReadShort(short s) throws IOException {
		int b1 = s & 0xFF;
		int b2 = s >> 8 & 0xFF;
		when = when.thenReturn(b1).thenReturn(b2);
	}

	public void mockReadBoolean(boolean b) throws IOException {
		when = when.thenReturn(b ? 1 : 0);
	}

	public void mockReadUtf(String s) throws IOException {
		// Mock length of string
		char l = (char) s.length();
		int b1 = l >> 8 & 0xFF;
		int b2 = l & 0xFF;
		when = when.thenReturn(b1).thenReturn(b2);
		byte[] bytes = s.getBytes("UTF-8");
		for (byte b : bytes)
		{
			when = when.thenReturn((int) b);
		}
	}

	public OngoingStubbing<Integer> getWhen() { return when; }
}
