import java.io.*;
import java.time.chrono.HijrahEra;
import java.util.*;

public class Naloga8 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        int node_count = Integer.parseInt(br.readLine());
        ArrayList<Node> nodes = new ArrayList<>();

        // Read and store input (create nodes)
        for (int i = 0; i < node_count; i++) {
            String[] node = br.readLine().split(",");
            
            Node n = new Node();
            n.setId(Integer.parseInt(node[0]));
            n.setValue(Integer.parseInt(node[1]));
            n.setLeft(Integer.parseInt(node[2]));
            n.setRight(Integer.parseInt(node[3]));

            ///System.out.printf("%d %d %d %d\n", n.id(), n.value(), n.leftSon(), n.rightSon());

            nodes.add(n);
        }

        Node root = new Node();
        // Find root
        for (int i = 0; i < nodes.size(); i++) {
            int id = nodes.get(i).id();
            boolean foundRoot = true;
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j)
                    continue;
                Node other = nodes.get(j);
                if (other.leftSon() == id || other.rightSon() == id)
                    foundRoot = false;
            }
            if (foundRoot) {
                root = nodes.get(i);
            }
        }
        
        // Create tree
        Tree tree = new Tree(root);
        constructTree(nodes, tree.root(), 0);

        /*
        Start at leftmost node (node = node.left)        

        if solved
            return

        if father null
            solve right half of tree

        if leaf
            write xValue
                    
        if node.right != null
        write xValue
        solve right subtree (function(node.right))
        
        if father not null
            node = node.father
        */

        // Start x-tagging algorithm at leftmost node
        root = tree.root();
        while (root.left != null)
            root = root.left;

        tag(root, false);

        // Store the results
        ArrayList<StringBuilder> done = new ArrayList<>();
        printLevelOrder(tree.root(), done);
        
        // Write the results
        for (StringBuilder sb : done) {
            bw.write(sb.toString());
        }

        br.close();
        bw.close();
    }

    static int height = 0;

    public static void constructTree(ArrayList<Node> nodes, Node root, int depth) {

        int left_son = root.leftSon();
        int right_son = root.rightSon();

        if (depth > height)
            height = depth;

        root.y = depth;

        // We reached a leaf
        if (left_son == -1 && right_son == -1) {
            root.left = null;
            root.right = null;
            return;
        }

        if (left_son == -1)
            root.left = null;

        if (right_son == -1)
            root.right = null;

        for (int i = 0; i < nodes.size(); i++) {
            Node iter = nodes.get(i);
            if (iter.equals(root))
                continue;

            if (iter.id() == left_son) {
                root.left = iter;
                iter.father = root;
                constructTree(nodes, iter, depth+1);
            }

            if (iter.id() == right_son) {
                root.right = iter;
                iter.father = root;
                constructTree(nodes, iter, depth+1);
            }
        }
    }
  
    static boolean solved = false;
    static int xValue = 0;

    public static void tag(Node root, boolean returning) {
    
        if (root == null || root.x != -1)
            return;

            
        // Start with zero
        if (root.left == null || returning) {
            solved = true;
            root.x = xValue++;
        }
        
        //System.out.println("Cur root: " + root.value() + " cur root x: " + root.x);
            
        // go as left as you can again and return    
        if (root.left != null && root.left.x == -1)
            tag(root.left, false);
    
        
        // If node has a right subtree solve it
        if (root.right != null && root.right.x == -1) {
            tag(root.right, false);
        }
        // move a node up (return from subtree)
        if (root.father != null || returning) {
            solved = false;
            tag(root.father, true);
        }
        
            
        

        // And move a node up
    }

    
    /*
    public static void solveRightSubtree(Node root) {

        // If root is a leaf we tag it and return
        if (root.left == null && root.right == null) {
            root.x = xValue++;
            return;
        } else {
            Node leftmost = findLeftmost(root, 0);
            Node rightmost = findRightmost(nodes, 0);
            ArrayList<Node> leftmosts = new ArrayList<>();

            System.out.println("rightmost: " + rightmost.id());

            while (leftmost != rightmost) {
                System.out.println("    leftmost: " + leftmost.id());
                leftmost.x = xValue++;
                leftmosts = findIthLeft(nodes, ++leftmost.pos_count);
                for (Node n : leftmosts) {
                    leftmost = n;
                    leftmost.x = xValue++;
                }
                
                leftmosts.clear();
            }
        }
        return;
    }*/

    static void printLevelOrder(Node root, ArrayList<StringBuilder> nodes) {
        if (root == null)
            return;
         
        // Create an empty queue for level order traversal
        Queue<Node> q = new LinkedList<Node>();

        q.add(root);
          
        while (true) {
            int nodeCount = q.size();
            if (nodeCount == 0)
                break;
            
            // Print out all nodes at current level
            while (nodeCount > 0) {
                Node node = q.peek();
                
                StringBuilder sb = new StringBuilder();
                sb.append(node.value() + "," + node.x + "," + node.y + "\n");
                nodes.add(sb);

                q.remove();

                // Enqueue nodes from next level 
                if (node.left != null)
                    q.add(node.left);
                if (node.right != null)
                    q.add(node.right);
                nodeCount--;
            }
        }
    }
}

class Tree {
    private Node root;

    public Tree() {
        makenull();
    }

    public Tree(Node root) {
        this.root = root;
    }

    private void makenull() {
        this.root = null;
    }

    public Node root() {
        return this.root; 
    }
}

class Node {
    private int id, value, left_id, right_id;
    public int x, y;
    public int pos_count;
    Node left, right, father;

    public Node() {
        this.id = 0;
        this.value = 0;
        this.left_id = 0;
        this.right_id = 0;
        this.left = null;
        this.right = null;
        this.x = -1;
    }

    public int id() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int value() {
        return this.value;
    }

    public void setValue(int v) {
        this.value = v;
    }

    public int leftSon() {
        return this.left_id;
    }

    public void setLeft(int left) {
        this.left_id = left;
    } 

    public int rightSon() {
        return this.right_id;
    }

    public void setRight(int right) {
        this.right_id = right;
    }

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }
}

class SortNode implements Comparator<Node> {
    public int compare(Node a, Node b) {
        if (a.y < b.y)
            return -1;
        if (a.y > b.y)
            return 1;
        return 0;
    }
}
