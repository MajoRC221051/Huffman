/**
 * The abstract class Node in Java represents a node with a frequency and
 * implements the Comparable
 * interface based on frequency.
 */
public abstract class Node implements Comparable<Node> {
    final int frequency;

    Node(int freq) {
        frequency = freq;
    }

    public int compareTo(Node other) {
        return frequency - other.frequency;
    }
}
