package com.jenjinstudios.core.io;

import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

/**
 * Used to mock a {@code DataInputStream}.
 *
 * @author Caleb Brinkman
 */
public class DataInputStreamMock
{
    private final InputStream in = mock(InputStream.class);
    private OngoingStubbing<Integer> when;

    /**
     * Construct a new {@code DataInputStreamMock}.
     *
     * @throws IOException If there's an exception.
     */
    public DataInputStreamMock() throws IOException {
        when(in.read(any(byte[].class), anyInt(), anyInt())).thenCallRealMethod();
        when = when(in.read());
    }

    /**
     * Mock reading a short from the stream.
     *
     * @param s The short to mock.
     */
    public void mockReadShort(short s) {
        byte[] bytes = ByteBuffer.allocate(2).putShort(s).array();
        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff);
    }

    /**
     * Mock reading a boolean from the stream.
     *
     * @param b The boolean to mock.
     */
    public void mockReadBoolean(boolean b) {
        when = when.thenReturn(b ? 1 : 0);
    }

    /**
     * Mock reading a stream from the stream.
     *
     * @param s The string to mock.
     *
     * @throws IOException If there's an exception.
     */
    public void mockReadUtf(String s) throws IOException {
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) s.length()).array();
        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff);
        byte[] encodedBytes = s.getBytes("UTF-8");
        for (byte b : encodedBytes)
        {
            when = when.thenReturn((int) b);
        }
    }

    /**
     * Mock reading an integer from the stream.
     *
     * @param i The integer to mock.
     */
    public void mockReadInt(int i) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();

        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
              thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff);
    }

    /**
     * Mock reading a long from the stream.
     *
     * @param l The long to mock.
     */
    public void mockReadLong(long l) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(l).array();
        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
              thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff).
              thenReturn(bytes[4] & 0xff).thenReturn(bytes[5] & 0xff).
              thenReturn(bytes[6] & 0xff).thenReturn(bytes[7] & 0xff);
    }

    /**
     * Mock reading a double from the stream.
     *
     * @param d The double to mock.
     */
    public void mockReadDouble(double d) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(d).array();
        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
              thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff).
              thenReturn(bytes[4] & 0xff).thenReturn(bytes[5] & 0xff).
              thenReturn(bytes[6] & 0xff).thenReturn(bytes[7] & 0xff);
    }

    /**
     * Mock reading a float from the stream.
     *
     * @param f The float to mock.
     */
    public void mockReadFloat(float f) {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(f).array();

        when = when.thenReturn(bytes[0] & 0xff).thenReturn(bytes[1] & 0xff).
              thenReturn(bytes[2] & 0xff).thenReturn(bytes[3] & 0xff);
    }

    /**
     * Mock reading a byte from the stream.
     *
     * @param b The byte to mock.
     */
    public void mockReadByte(byte b) {
        when = when.thenReturn(b & 0xff);
    }

    /**
     * Get the inputstream being mocked.
     *
     * @return The inputstream being mocked.
     */
    public InputStream getIn() { return in; }
}
