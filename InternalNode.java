/**
 * The `InternalNode` class represents a node in a binary tree with left and
 * right child nodes.
 */
public class InternalNode extends Node {
    final Node left, right;

    InternalNode(Node l, Node r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}
