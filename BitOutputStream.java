
/**
 * The `BitOutputStream` class in Java provides methods to write individual bits, bytes, and integers
 * to an output stream while handling bit-level operations.
 */
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements Closeable {
    private OutputStream out;
    private int currentByte;
    private int numBitsFilled;

    public BitOutputStream(OutputStream out) {
        this.out = out;
        currentByte = 0;
        numBitsFilled = 0;
    }

    /**
     * @param b
     * @throws IOException
     */
    public void writeBit(int b) throws IOException {
        if (!(b == 0 || b == 1))
            throw new IllegalArgumentException("Argument must be 0 or 1");
        currentByte = (currentByte << 1) | b;
        numBitsFilled++;
        if (numBitsFilled == 8) {
            out.write(currentByte);
            numBitsFilled = 0;
            currentByte = 0;
        }
    }

    public void writeByte(byte b) throws IOException {
        if (numBitsFilled == 0) {
            out.write(b & 0xFF);
        } else {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >>> i) & 1;
                writeBit(bit);
            }
        }
    }

    public void writeInt(int x) throws IOException {
        writeByte((byte) ((x >>> 24) & 0xFF));
        writeByte((byte) ((x >>> 16) & 0xFF));
        writeByte((byte) ((x >>> 8) & 0xFF));
        writeByte((byte) (x & 0xFF));
    }

    public void close() throws IOException {
        while (numBitsFilled != 0)
            writeBit(0);
        out.close();
    }
}
