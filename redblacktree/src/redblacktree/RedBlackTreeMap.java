package redblacktree;
import java.util.*;

// A Map ADT structure using a red-black tree, where keys must implement
// Comparable.
public class RedBlackTreeMap<TKey extends Comparable<TKey>, TValue> {
    // A Node class.
    private class Node {
        private TKey mKey;
        private TValue mValue;
        private Node mParent;
        private Node mLeft;
        private Node mRight;
        private boolean mIsRed;

        public Node(TKey key, TValue data, boolean isRed) {
            mKey = key;
            mValue = data;
            mIsRed = isRed;
            mLeft = NIL_NODE;
            mRight = NIL_NODE;
        }

        @Override
        public String toString() {
            return "(" + mKey + ", " + mValue + ")";
        }
    }
    private Node mRoot;
    private int mCount;

    // Rather than create a "blank" black Node for each NIL, we use one shared
    // node for all NIL leaves.
    private final Node NIL_NODE = new Node(null, null, false);

    // Get the # of keys in the tree.
    public int getCount() {
        return mCount;
    }

    // Finds the value associated with the given key.
    public TValue find(TKey key) {
        Node n = bstFind(key, mRoot); // find the Node containing the key if any
        if(n == null || n == NIL_NODE)
            throw new RuntimeException("Key not found");
        return n.mValue;
    }
    // Inserts a key/value pair into the tree, updating the red/black balance
    // of nodes as necessary. Starts with a normal BST insert, then adjusts.
    public void insert(TKey key, TValue data) {
        Node n = new Node(key, data, true); // nodes start red
        // normal BST insert; n will be placed into its initial position.
        // returns false if an existing node was updated (no rebalancing needed)
        boolean insertedNew = bstInsert(n, mRoot);
        //updated existing value
        if(!insertedNew){
            return;
        }
        // check cases 1-5 for balance violations.
        checkBalance(n);
    }

    // Applies rules 1-5 to check the balance of a tree with newly inserted
    // node n.  
    private void checkBalance(Node n) {
        Node grandParent = getGrandparent(n);
        Node uncle = getUncle(n);
        Node parent = n.mParent;
        //case 1 is root
        if(n == mRoot) {
            //System.err.println("Case 1");
            n.mIsRed = false;
            return;
        }
        //case 2, black parent, do nothing
        if(!parent.mIsRed){
            //System.err.println("Case 2");
            return;
        }
        //case 3, both P and U are red
        if(uncle.mIsRed && parent.mIsRed){
            uncle.mIsRed = false;
            parent.mIsRed = false;
            grandParent.mIsRed = true;
            checkBalance(grandParent);
            return;
        }
        //case 4, lr
        if(n == grandParent.mLeft.mRight){
            //System.err.println("case 4 lr");
            singleRotateLeft(parent);
            //parent now child of n, swap label for case 5 ll
            //make parent child of grandParent
            parent = grandParent.mLeft;
            //make n child of parent
            n = parent.mLeft;
        }//case 4, rl
        else if(n == grandParent.mRight.mLeft){
            //System.err.println("case 4 rl");
            singleRotateRight(parent);
            //parent now child of n, swap label for case 5 rr
            parent = grandParent.mRight;
            n = parent.mRight;
        }        
        //case 5, ll
        if(n == grandParent.mLeft.mLeft){
            //System.err.println("case 5 ll");
            singleRotateRight(grandParent);
            parent.mIsRed = false;
            grandParent.mIsRed = true;
        }//case 5. rr
        else if(n == grandParent.mRight.mRight){
            //System.err.println("case 5 rr");
            singleRotateLeft(grandParent);
            parent.mIsRed = false;
            grandParent.mIsRed = true;
        }
    }

    // Returns true if the given key is in the tree.
    public boolean containsKey(TKey key) {
        return bstFind(key, mRoot) != null;
    }

    // Prints a pre-order traversal of the tree's nodes, printing the key, value,
    // and color of each node.
    public void printStructure() {
        printPreorder(mRoot);
    }

    private void printPreorder(Node currentNode){
        if(currentNode != null){
            String color = currentNode.mIsRed == true ? "Red" : "Black";
            System.out.println(currentNode.toString() + "[" + color + "]");
            if(currentNode.mLeft != NIL_NODE){
                printPreorder(currentNode.mLeft);
            }
            if(currentNode.mRight != NIL_NODE){
                printPreorder(currentNode.mRight);
            }
        }
    }
    // Retuns the Node containing the given key. Recursive.
    private Node bstFind(TKey key, Node currentNode) {
        if(currentNode != null && currentNode != NIL_NODE){
            //current node is the key
            if(currentNode.mKey.equals(key)){
                return currentNode;
            }
            else{
                //nts compareTo returns positive if mKey > key [go left]
                int compare = currentNode.mKey.compareTo(key);
                //go left if smaller
                if(currentNode.mLeft != NIL_NODE && compare > 0){
                    return bstFind(key, currentNode.mLeft);
                }//else right
                else if(currentNode.mRight != NIL_NODE && compare < 0){
                    return bstFind(key, currentNode.mRight);
                }
            }
        }
        return null;//not found
    }
    //////////////// These functions are needed for insertion cases.
    // Gets the grandparent of n.
    private Node getGrandparent(Node n) {
        // TODO: return the grandparent of n
        if(n.mParent != null && n.mParent.mParent != null){
            return n.mParent.mParent;
        }
        //no grandparents
        return null;
    }

    // Gets the uncle (parent's sibling) of n.
    private Node getUncle(Node n) {
        Node grandParent = getGrandparent(n);
        if(grandParent != null){
            if(grandParent.mLeft == n.mParent){
                return grandParent.mRight;
            }
            else if(grandParent.mRight == n.mParent){
                return grandParent.mLeft;
            }
        }
        return null;
    }

    // Rotate the tree right at the given node.
    private void singleRotateRight(Node n) {
        Node l = n.mLeft;
        Node lr = l.mRight;
        Node p = n.mParent;

        n.mLeft = lr;
        lr.mParent = n;
        l.mRight = n;

        if (n == mRoot) {
            mRoot = l;
            l.mParent = null;
        }
        else if (p.mLeft == n) {
            p.mLeft = l;
            l.mParent = p;
        }
        else {
            p.mRight = l;
            l.mParent = p;
        }
        n.mParent = l;

    }

    // Rotate the tree left at the given node.
    private void singleRotateLeft(Node n) {
        Node r = n.mRight;
        Node rl = r.mLeft;
        Node p = n.mParent;

        n.mRight = rl;
        rl.mParent = n;
        r.mLeft = n;
        
        if(n == mRoot){
            mRoot = r;
            r.mParent = null;
        }
        else if(p.mRight == n){
            p.mRight = r;
            r.mParent = p;
        }
        else{
            p.mLeft = r;
            r.mParent = p;
        }
        n.mParent = r;
    }
    // This method is used by insert. It is complete.
    // Inserts the key/value into the BST, and returns true if the key wasn't 
    // previously in the tree.
    private boolean bstInsert(Node newNode, Node currentNode) {
        if (mRoot == null) {
            // case 1
            mRoot = newNode;
            mCount = 1;
            return true;
        }
        else{
            int compare = currentNode.mKey.compareTo(newNode.mKey);
            if (compare < 0) {
            // newNode is larger; go right.
                if (currentNode.mRight != NIL_NODE)
                    return bstInsert(newNode, currentNode.mRight);
                else {
                    currentNode.mRight = newNode;
                    newNode.mParent = currentNode;
                    mCount++;
                    return true;
                }
            }
            else if (compare > 0) {
                if (currentNode.mLeft != NIL_NODE)
                    return bstInsert(newNode, currentNode.mLeft);
                else {
                    currentNode.mLeft = newNode;
                    newNode.mParent = currentNode;
                    mCount++;
                    return true;
                }
            }
            else {
                // found a node with the given key; update value.
                currentNode.mValue = newNode.mValue;
                return false; // did NOT insert a new node.
            }
        }
    }
}
