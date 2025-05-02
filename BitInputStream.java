
/**
 * The `BitInputStream` class reads bits, bytes, and integers from an input stream in Java.
 */
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements Closeable {
    private InputStream in;
    private int currentByte;
    private int numBitsRemaining;

    public BitInputStream(InputStream in) {
        this.in = in;
        currentByte = 0;
        numBitsRemaining = 0;
    }

    /**
     * @return int
     * @throws IOException
     */
    public int readBit() throws IOException {
        if (numBitsRemaining == 0) {
            currentByte = in.read();
            if (currentByte == -1)
                return -1;
            numBitsRemaining = 8;
        }
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }

    public int readByte() throws IOException {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit();
            if (bit == -1)
                return -1;
            result = (result << 1) | bit;
        }
        return result;
    }

    public int readInt() throws IOException {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int b = readByte();
            if (b == -1)
                throw new EOFException("Unexpected EOF while reading int");
            result = (result << 8) | b;
        }
        return result;
    }

    public void close() throws IOException {
        in.close();
    }
}
