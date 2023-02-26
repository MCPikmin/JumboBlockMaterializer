package binarytree;

public class BinaryBlockTree {

    private Node root;

    public BinaryBlockTree() {
        root = null;
    }

    public Node search(Integer key) {
        Node p = root;
        while (p != null) {
            if (key.compareTo(p.data) == 0) {
                return p;
            } else {
                int dl = Math.abs(key - p.left.data);
                int dr = Math.abs(key - p.right.data);
                p = dl < dr ? p.left : p.right;
            }
        }
        return null;
    }

    public Node insert(Integer key) {
        Node p = root;
        Node parent = null;
        boolean isLeftChild = false;

        while (p != null) {
            int result = key.compareTo(p.data);
            if (result == 0) {
                return null;
            } else if (result < 0) {
                parent = p;
                isLeftChild = true;
                p = p.left;
            } else {
                parent = p;
                isLeftChild = false;
                p = p.right;
            }
        }

        Node newNode = new Node(key);
        if (parent == null) {
            root = newNode;
        } else if (isLeftChild) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        return newNode;
    }

    class Node {
        Integer data;
        Node left;
        Node right;

        Node(Integer data) {
            left = null;
            right = null;
            this.data = data;
        }
    }
}
