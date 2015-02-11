package com.jenjinstudios.core.io;

import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;

/**
 * Used to mock a {@code DataInputStream}.
 *
 * @author Caleb Brinkman
 */
public class DataInputStreamMock
{
    public static final int HEX_CONVERSION_CONSTANT = 0xff;
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
        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT);
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
     * @throws UnsupportedEncodingException If there's an exception.
     */
    public void mockReadUtf(String s) throws UnsupportedEncodingException {
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) s.length()).array();
        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT);
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

        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[2] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[3] & HEX_CONVERSION_CONSTANT);
    }

    /**
     * Mock reading a long from the stream.
     *
     * @param l The long to mock.
     */
    public void mockReadLong(long l) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(l).array();
        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[2] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[3] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[4] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[5] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[6] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[7] & HEX_CONVERSION_CONSTANT);
    }

    /**
     * Mock reading a double from the stream.
     *
     * @param d The double to mock.
     */
    public void mockReadDouble(double d) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(d).array();
        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[2] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[3] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[4] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[5] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[6] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[7] & HEX_CONVERSION_CONSTANT);
    }

    /**
     * Mock reading a float from the stream.
     *
     * @param f The float to mock.
     */
    public void mockReadFloat(float f) {
        byte[] bytes = ByteBuffer.allocate(4).putFloat(f).array();

        when = when.thenReturn(bytes[0] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[1] & HEX_CONVERSION_CONSTANT).
              thenReturn(bytes[2] & HEX_CONVERSION_CONSTANT).thenReturn(bytes[3] & HEX_CONVERSION_CONSTANT);
    }

    /**
     * Mock reading a byte from the stream.
     *
     * @param b The byte to mock.
     */
    public void mockReadByte(byte b) {
        when = when.thenReturn(b & HEX_CONVERSION_CONSTANT);
    }

    /**
     * Get the inputstream being mocked.
     *
     * @return The inputstream being mocked.
     */
    public InputStream getIn() { return in; }
}
