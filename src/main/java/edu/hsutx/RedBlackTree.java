package edu.hsutx;

/**
 * @author Todd Dole
 * @version 1.0
 * Starting Code for the CSCI-3323 Red-Black Tree assignment
 * Students must complete the TODOs and get the tests to pass
 */

/**
 * A Red-Black Tree that takes int key and String value for each node.
 * Follows the properties of a Red-Black Tree:
 * 1. Every node is either red or black.
 * 2. The root is always black.
 * 3. Every leaf (NIL node) is black.
 * 4. If a node is red, then both its children are black.
 * 5. For each node, all simple paths from the node to descendant leaves have
 * the same number of black nodes.
 */
public class RedBlackTree<E> {
    Node root;
    int size;

    protected class Node {
        public String key;
        public E value;
        public Node left;
        public Node right;
        public Node parent;
        public boolean color; // true = red, false = black

        public Node(String key, E value, Node parent, boolean color) {
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.left = null;
            this.right = null;
            this.color = color;
        }

        /**
         * Returns the depth of all nodes in the tree (distance from the root)
         * 
         * @return  the depth of all nodes
         */
        public int getDepth() {

            int counter = 1;
            Node current = this;

            while (current.parent != null) {
                counter++;
                current = current.parent;
            }

            return counter;
        }

        /**
         * Returns the depth of all the black nodes in the tree
         * 
         * @return the depth of all black nodes
         */
        public int getBlackDepth() {

            int counter = 0;
            Node current = this;

            while (current.parent != null) {
                if (current.color == false) {
                    counter++;
                }
                current = current.parent;
            }

            return counter;
        }
    }

    /**
     * Initializes an empty red black tree
     */
    public RedBlackTree() {
        root = null;                   
        size = 0;
    }

    /**
     * Insert a new node at bottom of the tree
     * 
     * @param key   the key of the new node
     * @param value the value of the new node
     */
    public void insert(String key, E value) {

        // If root is null, the tree is empty
        // Therefore set the head(root) to be the new Node
        if (root == null) {
            root = new Node(key, value, null, false);
            return;
        }

        Node previous = null;
        Node current = root;
        Node pointer = find(key);

        if (pointer != null)
            return;

        while (current != null) {
            int result = key.compareTo(current.key); 
                                                     
            previous = current;
            if (result > 0) {
                current = current.right;
            } else if (result < 0) {
                current = current.left;
            }
        }

        // Creating the new Node to be inserted
        // Parent set to previous Node, and set color to red
        pointer = new Node(key, value, previous, true);


        // If the new Node is less than the parent, set the left child of parent to be
        // the new Node
        // Otherwise do the opposite
        if (pointer.key.compareTo(previous.key) < 0) {
            previous.left = pointer;
        } else {
            previous.right = pointer;
        }

        pointer.left = null;
        pointer.right = null;
        size++;
        fixInsertion(pointer);
        return;
    }

    /**
     * Replaces a node with another in the tree
     * 
     * @param toDelete   the node to be replaced
     * @param newPointTo the node that will replace the deleted node
     */
    public void transplant(Node toDelete, Node newPointTo) {
        if (toDelete.parent == null) { // If toDelete is the root, update the root
            root = newPointTo;
        } else if (toDelete == toDelete.parent.left) { // If toDelete is a left child, update the left child
            toDelete.parent.left = newPointTo;
        } else {
            toDelete.parent.right = newPointTo; // If toDelete is a right child, update the right child
        }
        if (newPointTo != null) {
            newPointTo.parent = toDelete.parent; // Update the parent of the new node
        }
    }

    /**
     * Returns the node with the smallest key starting from the given node
     * 
     * @param node the node to start from
     * @return     the node with the smallest key
     */
    public Node treeMinimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Deletes a node from the tree based on the given key
     * 
     * @param key the key of the node to delete
     */
    public void delete(String key) {
            
            Node nodeToDelete = find(key);

            if (nodeToDelete == null) {
                return;
            }
    
            Node replacementNode = nodeToDelete;
            Node childNode;
            boolean originalColor = replacementNode.color; // Store the original color of the replacement node
    
            if (nodeToDelete.left == null) {
                // Case 1 - Node has no left child
                childNode = nodeToDelete.right;
                transplant(nodeToDelete, nodeToDelete.right); // Replace nodeToDelete with its right child
            } else if (nodeToDelete.right == null) {
                // Case 2 - Node has no right child
                childNode = nodeToDelete.left;
                transplant(nodeToDelete, nodeToDelete.left); // Replace nodeToDelete with its left child
            } else {
                // Case 3 - Node has two children
                replacementNode = treeMinimum(nodeToDelete.right);
                originalColor = replacementNode.color; // Store the original color of the replacement node
                childNode = replacementNode.right; // Get the right child of the replacement node
                if (replacementNode.parent == nodeToDelete) {
                    if (childNode != null) {
                        childNode.parent = replacementNode; // Update the parent of the child node
                    }
                } else {
                    transplant(replacementNode, replacementNode.right); // Replace the replacement node with its right child
                    replacementNode.right = nodeToDelete.right; // Update the right child of the replacement node
                    replacementNode.right.parent = replacementNode; // Update the parent of the right child
                }
                transplant(nodeToDelete, replacementNode); // Replace the node to delete with the replacement node
                replacementNode.left = nodeToDelete.left; // Update the left child of the replacement node
                replacementNode.left.parent = replacementNode; // Update the parent of the left child
                replacementNode.color = nodeToDelete.color; // Update the color of the replacement node
            }
            if (originalColor == false && childNode != null) {
                fixDeletion(childNode); // Fix the tree if the original color was black
            }
            size--;
    }

    /**
     * Fixes the tree after insertion to follow red black tree rules
     * 
     * @param node the mew node that was inserted
     */
    private void fixInsertion(Node node) {
        Node uncle;
        
        if (node.parent == null) {
            node.color = false;
            return;
        }

        while (node.parent != null && node.parent.color == true) {
            if (node.parent == node.parent.parent.left) {
                uncle = node.parent.parent.right;
                if (uncle != null && uncle.color == true) {
                    // Case 1 - Uncle is red
                    node.parent.color = false;
                    uncle.color = false;
                    node.parent.parent.color = true;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        // Case 2 - Uncle is black and node is right child
                        node = node.parent;
                        rotateLeft(node);
                    }
                    // Case 3 - Uncle is black and node is left child
                    node.parent.color = false;
                    node.parent.parent.color = true;
                    rotateRight(node.parent.parent);
                }
            } else {
                uncle = node.parent.parent.left;
                if (uncle != null && uncle.color == true) {
                    // Case 1 - Uncle is red
                    node.parent.color = false;
                    uncle.color = false;
                    node.parent.parent.color = true;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        // Case 2 - Uncle is black and node is left child
                        node = node.parent;
                        rotateRight(node);
                    }
                    // Case 3 - Uncle is black and node is right child
                    node.parent.color = false;
                    node.parent.parent.color = true;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = false;
    }

    /**
     * Fixes the tree after deletion to follow red black tree rules
     * 
     * @param node the node to start fixing from
     */
    private void fixDeletion(Node node) {
        Node sibling;
        while (node != root && isBlack(node)) { // Loop until node is root or node is red
            if (node == node.parent.left) {
                sibling = node.parent.right; // Get the sibling node
                if (isRed(sibling)) {
                    // Case 1 - Sibling is red
                    sibling.color = false; 
                    node.parent.color = true; 
                    rotateLeft(node.parent); 
                    sibling = node.parent.right; 
                }
                if (isBlack(sibling.left) && isBlack(sibling.right)) {
                    // Case 2 - Sibling is black and both children are black
                    sibling.color = true; 
                    node = node.parent; // Move up the tree
                } else {
                    if (isBlack(sibling.right)) {
                        // Case 3 - Sibling is black, left child is red, right child is black
                        sibling.left.color = false; 
                        sibling.color = true; 
                        rotateRight(sibling); 
                        sibling = node.parent.right; 
                    }
                    // Case 4 - Sibling is black, right child is red
                    sibling.color = node.parent.color; // Match sibling color to parent
                    node.parent.color = false; 
                    sibling.right.color = false; 
                    rotateLeft(node.parent); 
                    node = root; 
                }
            } else { // Mirror of the above code
                sibling = node.parent.left; 
                if (isRed(sibling)) {
                    // Case 1 - Sibling is red
                    sibling.color = false; 
                    node.parent.color = true; 
                    rotateRight(node.parent); 
                    sibling = node.parent.left; 
                }
                if (isBlack(sibling.right) && isBlack(sibling.left)) {
                    // Case 2 - Sibling is black and both children are black
                    sibling.color = true; 
                    node = node.parent; 
                } else {
                    if (isBlack(sibling.left)) {
                        // Case 3 - Sibling is black, right child is red, left child is black
                        sibling.right.color = false; 
                        sibling.color = true; 
                        rotateLeft(sibling); 
                        sibling = node.parent.left; 
                    }
                    // Case 4 - Sibling is black, left child is red
                    sibling.color = node.parent.color; 
                    node.parent.color = false; 
                    sibling.left.color = false; 
                    rotateRight(node.parent); 
                    node = root; 
                }
            }
        }
        node.color = false; // Ensure the node is black
    }

    /**
     * Does a left rotation at the given node
     * 
     * @param node the node to be rotated
     */
    private void rotateLeft(Node node) {

        Node newHead = node.right;
        node.right = newHead.left;

        if (newHead.left != null) {
            newHead.left.parent = node;
        }

        newHead.parent = node.parent;

        if (node.parent == null) {
            root = newHead;
        } else if (node == node.parent.left) {
            node.parent.left = newHead;
        } else {
            node.parent.right = newHead;
        }

        newHead.left = node;
        node.parent = newHead;
    }

    /**
     * Does a right rotation at the given node
     * 
     * @param node the node to be rotated
     */
    private void rotateRight(Node node) {

        Node newHead = node.left;
        node.left = newHead.right;

        if (newHead.right != null) {
            newHead.right.parent = node;
        }

        newHead.parent = node.parent;

        if (node.parent == null) {
            root = newHead;
        } else if (node == node.parent.right) {
            node.parent.right = newHead;
        } else {
            node.parent.left = newHead;
        }

        newHead.right = node;
        node.parent = newHead;
    }

    /**
     * Finds a node in the tree based on a given key
     * 
     * @param key the key to find
     * @return    the node corresponding to the key, or null if not found
     */
    Node find(String key) {

        Node current = root;

        while (current != null) {
            int result = key.compareTo(current.key);

            if (result > 0) { // key > curreny.key
                current = current.right;
            } else if (result < 0) { // key < current.key
                current = current.left;
            } else { // key = current.key
                return current;
            }
        }

        return null;
    }

    /**
     * Gets the value of a node in the tree based on a given key
     * 
     * @param key the key to find
     * @return    the value of the node, or null if not found
     */
    public E getValue(String key) {
        Node current = find(key);
        if (current != null) {
            return current.value;
        }
        return null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // returns the depth of the node with key, or 0 if it doesn't exist
    public int getDepth(String key) {
        Node node = find(key);
        if (node != null)
            return node.getDepth();
        return 0;
    }

    // Helper methods to check the color of a node
    private boolean isRed(Node node) {
        return node != null && node.color == true; // Red is true
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == false; // Black is false, and null nodes are black
    }

    public int getSize() {
        return size;
    }

    // Do not alter this method
    public boolean validateRedBlackTree() {
        // Rule 2: Root must be black
        if (root == null) {
            return true; // An empty tree is trivially a valid Red-Black Tree
        }
        if (isRed(root)) {
            return false; // Root must be black
        }

        // Start recursive check from the root
        return validateNode(root, 0, -1);
    }

    // Do not alter this method
    // Helper method to check if the current node maintains Red-Black properties
    private boolean validateNode(Node node, int blackCount, int expectedBlackCount) {
        // Rule 3: Null nodes (leaves) are black
        if (node == null) {
            if (expectedBlackCount == -1) {
                expectedBlackCount = blackCount; // Set the black count for the first path
            }
            return blackCount == expectedBlackCount; // Ensure every path has the same black count
        }

        // Rule 1: Node is either red or black (implicit since we use a boolean color
        // field)

        // Rule 4: If a node is red, its children must be black
        if (isRed(node)) {
            if (isRed(node.left) || isRed(node.right)) {
                return false; // Red node cannot have red children
            }
        } else {
            blackCount++; // Increment black node count on this path
        }

        // Recurse on left and right subtrees, ensuring they maintain the Red-Black
        // properties
        return validateNode(node.left, blackCount, expectedBlackCount) &&
                validateNode(node.right, blackCount, expectedBlackCount);
    }
}
