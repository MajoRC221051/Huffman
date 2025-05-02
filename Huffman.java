
/**
 * The Huffman class in Java provides methods for compressing and decompressing files using Huffman
 * coding algorithm.
 */
import java.io.*;
import java.util.*;

public class Huffman {

    /**
     * @param text
     * @return Map<Character, Integer>
     */
    private static Map<Character, Integer> buildFrequencyTable(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }
        return freq;
    }

    private static Node buildHuffmanTree(Map<Character, Integer> freqTable) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqTable.entrySet()) {
            pq.add(new LeafNode(entry.getValue(), entry.getKey()));
        }
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new InternalNode(left, right));
        }
        return pq.poll();
    }

    private static void buildCodeTable(Node node, String prefix, Map<Character, String> codeTable) {
        if (node instanceof LeafNode) {
            LeafNode leaf = (LeafNode) node;
            codeTable.put(leaf.character, prefix.length() > 0 ? prefix : "0");
        } else if (node instanceof InternalNode) {
            InternalNode internal = (InternalNode) node;
            buildCodeTable(internal.left, prefix + '0', codeTable);
            buildCodeTable(internal.right, prefix + '1', codeTable);
        }
    }

    private static void writeTree(Node node, BitOutputStream out) throws IOException {
        if (node instanceof LeafNode) {
            out.writeBit(1);
            out.writeByte((byte) ((LeafNode) node).character);
        } else if (node instanceof InternalNode) {
            out.writeBit(0);
            writeTree(((InternalNode) node).left, out);
            writeTree(((InternalNode) node).right, out);
        }
    }

    private static Node readTree(BitInputStream in) throws IOException {
        int bit = in.readBit();
        if (bit == 1) {
            int b = in.readByte();
            if (b == -1)
                throw new IOException("Unexpected EOF");
            return new LeafNode(0, (char) b);
        } else if (bit == 0) {
            Node left = readTree(in);
            Node right = readTree(in);
            return new InternalNode(left, right);
        } else {
            throw new IOException("Invalid bit in tree");
        }
    }

    public static void compress(File inputFile, File outputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        }
        String text = sb.toString();

        Map<Character, Integer> freqTable = buildFrequencyTable(text);
        Node root = buildHuffmanTree(freqTable);

        Map<Character, String> codeTable = new HashMap<>();
        buildCodeTable(root, "", codeTable);

        try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            writeTree(root, out);
            out.writeInt(text.length());
            for (char ch : text.toCharArray()) {
                String code = codeTable.get(ch);
                for (char bitChar : code.toCharArray()) {
                    out.writeBit(bitChar == '1' ? 1 : 0);
                }
            }
        }
    }

    public static void decompress(File inputFile, File outputFile) throws IOException {
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            Node root = readTree(in);
            int length = in.readInt();
            Node current = root;
            for (int i = 0; i < length; i++) {
                while (!(current instanceof LeafNode)) {
                    int bit = in.readBit();
                    if (bit == -1) {
                        throw new EOFException("Unexpected end of file during decoding");
                    } else if (bit == 0) {
                        current = ((InternalNode) current).left;
                    } else {
                        current = ((InternalNode) current).right;
                    }
                }
                writer.write(((LeafNode) current).character);
                current = root;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage:");
            System.err.println("  To compress:   java Huffman -c input.txt compressed.bin");
            System.err.println("  To decompress: java Huffman -d compressed.bin output.txt");
            System.exit(1);
        }

        String op = args[0];
        File inputFile = new File(args[1]);
        File outputFile = new File(args[2]);

        try {
            if ("-c".equals(op)) {
                compress(inputFile, outputFile);
                System.out.println("Compression completed.");
            } else if ("-d".equals(op)) {
                decompress(inputFile, outputFile);
                System.out.println("Decompression completed.");
            } else {
                System.err.println("Unknown operation: " + op);
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Error during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
