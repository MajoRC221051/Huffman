
/**
 * The HuffmanTest class contains JUnit test methods to verify the compression and decompression
 * functionality of Huffman encoding.
 */
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;

public class HuffmanTest {

    private static final String ORIGINAL_TEXT = "This is a test of Huffman compression and decompression.";

    private File originalFile;
    private File compressedFile;
    private File decompressedFile;

    /**
     * @throws IOException
     */
    @BeforeEach
    public void setUp() throws IOException {
        originalFile = File.createTempFile("original", ".txt");
        compressedFile = File.createTempFile("compressed", ".bin");
        decompressedFile = File.createTempFile("decompressed", ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalFile))) {
            writer.write(ORIGINAL_TEXT);
        }
    }

    @AfterEach
    public void tearDown() {
        originalFile.delete();
        compressedFile.delete();
        decompressedFile.delete();
    }

    @Test
    public void testCompressDecompress() throws IOException {
        Huffman.compress(originalFile, compressedFile);
        Huffman.decompress(compressedFile, decompressedFile);

        String decompressedText = Files.readString(decompressedFile.toPath());

        assertEquals(ORIGINAL_TEXT, decompressedText, "The decompressed text should match the original text");
    }
}
