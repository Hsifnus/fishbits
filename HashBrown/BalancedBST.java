/**
 * A balanced binary search tree using juicy leaves.
 * Created by bakafish on 3/30/17.
 */
import java.util.ArrayList;

public class BalancedBST<T extends Comparable> {

    private class Node {

        private class SimpleNode {
            T item;
            Node right, left;

            SimpleNode(T i) {
                item = i;
                right = left = null;
            }
        }

        ArrayList<SimpleNode> items;
        Node parent;

        Node(T initial, Node root) {
            items = new ArrayList<>();
            items.add(new SimpleNode(initial));
            parent = root;
        }

        void appendItem(SimpleNode item) {
            items.add(item);
        }

        void insertChild(int index, Node child) {
            assert (index >= 0 && index <= items.size());
            if (index < items.size()) {
                items.get(index).left = child;
            }
            if (index > 0) {
                items.get(index - 1).right = child;
            }
        }

        Node getChild(int index) {
            assert (index >= 0 && index <= items.size());
            if (index < items.size()) {
                return items.get(index).left;
            }
            else {
                return items.get(index - 1).right;
            }
        }

        Node subnode(int begin, int end) {
            assert (begin >= 0 && end <= items.size() && end > begin);
            Node result = new Node(items.get(begin).item, parent);
            for (int i = begin + 1; i < end; i++) {
                SimpleNode copy = new SimpleNode(items.get(i).item);
                copy.right = items.get(i).right;
                copy.left = items.get(i).left;
                result.appendItem(copy);
            }
            return result;
        }

        void split() {
            assert (items.size() > 2);
            Node leftBranch = subnode(0, 1);
            Node rightBranch = subnode(2, items.size());
            if (parent == null) {
                Node root = new Node(items.get(1).item, null);
                root.insertChild(0, leftBranch);
                root.insertChild(1, rightBranch);
                leftBranch.parent = rightBranch.parent = root;
                items = root.items;
                parent = null;
            } else {
                items = rightBranch.items;
                parent = null;
            }
        }

    }

    private Node mainRoot;
    private int nodeSizeLimit;

    public BalancedBST(T firstElement) {
        mainRoot = new Node(firstElement, null);
        nodeSizeLimit = 3;
    }
}
