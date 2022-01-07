import java.io.*;
import java.util.*;
import java.util.Stack;

public class Naloga6 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        StringTokenizer st = new StringTokenizer(br.readLine(), "[ */.-/+()]", true);

        String expression = InfixToPrefix(st).toString();
        
        BinaryTree expressionTree = new BinaryTree(expression.split(" ")[0]);
        String[] xpr = expression.split(" ");
        
        build(xpr, expressionTree.root(), 1);

        bw.write(String.join(",", xpr) + "\n" + height + "\n");

           
        br.close();
        bw.close();
    }

    static int i = 0;
    static int height = 0;
    static boolean prevNot = false;

    public static void build(String[] prefix, Node n, int depth) { // int depth to determine height
        // Return if operand or end of expression
        if (i >= prefix.length || priority((String) n.value()) < 0)
        return;
        
        // Record height
        if (depth > height)
            height = depth;
        

        // Unary operand - only has left son
        if (n.value().equals("NOT")) {
            n.left = new Node(prefix[i++]);
            if (priority((String) n.left.value()) < 0)
                return;
            else
                build(prefix, n.left, depth+1);
        } else {

            if (n.left == null) {
                n.left = new Node(prefix[i++]);
                build(prefix, n.left, depth+1);
            }
    
            if (n.right == null && i < prefix.length) {// && !prevNot) {
                n.right = new Node(prefix[i++]);
                build(prefix, n.right, depth+1);
            }
        }

        //System.out.println(prefix[i] + " depth: " + depth);

    }

    static void printLevelOrder(Node root)
    {
        // Base Case
        if(root == null)
            return;
         
        // Create an empty queue for level order traversal
        Queue<Node> q = new LinkedList<Node>();
         
        // Enqueue Root and initialize height
        q.add(root);
         
         
        while(true)
        {
             
            // nodeCount (queue size) indicates number of nodes
            // at current level.
            int nodeCount = q.size();
            if(nodeCount == 0)
                break;
             
            // Dequeue all nodes of current level and Enqueue all
            // nodes of next level
            while(nodeCount > 0)
            {
                Node node = q.peek();
                System.out.print(node.value() + " ");
                q.remove();
                if(node.left != null)
                    q.add(node.left);
                if(node.right != null)
                    q.add(node.right);
                nodeCount--;
            }
            System.out.println();
        }
    }

    public static void print(int offset, Node n, boolean l) {
		
        if (n == null) {
            //System.out.println("n is null");
            return;
        }

		for (int i = 0; i < offset+5; i++) {
            System.out.print("  ");
		}
        System.out.printf("%d %s\n", offset, n.value());

        if (n.left != null) {
            print(offset+1, n.left, true);
        }
        if (n.right != null) {
            print(offset, n.right, false);
        }
	}

	// Metoda za izpis druzinskega drevesa
	public static void print(Node root)
	{
		print(0, root, true);
	}

    public static StringBuilder InfixToPrefix(StringTokenizer st) {
        StringBuilder prefix = new StringBuilder();
    
        // Use stack to convert to prefix (go IRZ!)
        Stack<String> stack = new Stack<String>();

        ArrayList<String> infix = new ArrayList<>();
        
        // Invert the expression
        while (st.hasMoreTokens()) {
            String token = st.nextToken(); 
            if (!token.equals(" ")) {
                infix.add(token);
            }
        }

        for (int i = infix.size()-1; i >= 0;  --i) {
            String s = infix.get(i);
            
            // If s is an operator
            if (priority(s) > 0) {
                s = s.toUpperCase();

                // We pop every token with higher priority (including operands)
                while (!stack.isEmpty() && priority(stack.peek()) > priority(s)) {
                    prefix.append(stack.pop() + " ");
                }
                // And push the current operator
                stack.push(s);
            } else if (s.equals("(")) {
                // Expression in brackets
                String iter = stack.pop();
                while (!iter.equals(")")) {
                    prefix.append(iter + " ");
                    iter = stack.pop();
                }
            } else if (s.equals(")")) {
                stack.push(s);
            } else {
                prefix.append(s + " ");
            }
        }

        // Add back all the operators
        while (!stack.isEmpty()) {
            prefix.append(stack.pop() + " ");
        }

        String[] r = prefix.toString().split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = r.length - 1; i >= 0; i--) {
            result.append(r[i] + " ");
        }

        return result;
    }

    public static int priority(String operator) {
        operator = operator.toUpperCase();
        if (operator.equals("NOT"))
            return 3;
        if (operator.equals("AND"))
            return 2;
        if (operator.equals("OR"))
            return 1;
        else 
            return -1;   // Not an operator
    } 
}

class BinaryTree {
    private Node root;

    public BinaryTree() {
        this.root = null;
    }

    public BinaryTree(Object value) {
        this.root = new Node(value);
    }

    public Node root() {
        return this.root;
    }

    public void print(int offset, Node n) {
		
        if (n == null) {
            System.out.println("n is null");
            return;
        }
        
        for (int i = 0; i < offset; i++) {
			System.out.print(" ");
		}
		System.out.println(n.value());
		
        print(offset+1, n.left);
        print(offset+1, n.right);
	}

	public void print(Node root)
	{
		print(0, root);
	}
}

class Node {
    private Object value;
    public Node left;
    public Node right;
    public int depth;

    public Node() {
        this.value = null;  
    }

    public Node(Object val) {
        this.value = val;
        this.left = null;
        this.right = null;
        this.depth = 0;
    }

    public Node(Object val, Node left, Node right) {
        this.value = val;
        this.left = left;
        this.right = right;
    }

    public Object value() {
        return this.value;
    }

}