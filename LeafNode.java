/**
 * The `LeafNode` class represents a leaf node in a tree structure with a
 * character and frequency.
 */
public class LeafNode extends Node {
    final char character;

    LeafNode(int freq, char ch) {
        super(freq);
        character = ch;
    }
}
