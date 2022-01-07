import java.io.*;
import java.util.*;

public class Naloga9 {
    
    public static void main(String[] args) throws IOException {
        
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        String[] nm = br.readLine().split(",");
        int path_count = Integer.parseInt(nm[0]);
        int fact_count = Integer.parseInt(nm[1]);   

        TreeMap<Road, Integer> cities = new TreeMap<Road, Integer>(new sortRoad());
        HashSet<Integer> allCities = new HashSet<>();

        // Create connections
        for (int i = 0; i < path_count; i++) {
            String[] line = br.readLine().split(",");
            int l1 = Integer.parseInt(line[0]);
            int l2 = Integer.parseInt(line[1]);
            allCities.add(l1);
            allCities.add(l2);
            Road connection = null;

            if (l1 < l2)
                connection = new Road(l1, l2);
            else 
                connection = new Road(l2, l1);
            
            try {
                stop2road.get(l1).add(connection);
            } catch (Exception e)  {
                ArrayList<Road> rds = new ArrayList<>();
                rds.add(connection);
                stop2road.put(l1, rds);
            }

            try {
                stop2road.get(l2).add(connection);
            } catch (Exception e)  {
                ArrayList<Road> rds = new ArrayList<>();
                rds.add(connection);
                stop2road.put(l2, rds);
            }

            cities.put(connection, 0);
        }
        long t1 = System.nanoTime();

        //System.out.println(stop2road.size());

        HashMap<Road, ArrayList<Road>> memo = new HashMap<>();

        for (int i = 0; i < fact_count; i++) {
            String[] line = br.readLine().split(",");
            int start = Integer.parseInt(line[0]);
            int dest = Integer.parseInt(line[1]);
            int cost = Integer.parseInt(line[2]);

            if (!allCities.contains(start) || !allCities.contains(dest))
                continue;
            
            ArrayList<Road> path = null;
            ArrayList<Road> cons = new ArrayList<>();

            if (memo.get(new Road(start, dest)) != null) {
                path = memo.get(new Road(start,dest));
            }

            cons.addAll(path(cities, start, dest, memo));

            // If more than one path 
            if (cons.size() > 1 && path == null) {
                // Sort roads by paths increasing
                for (Road r : cons) {
                    // Check if the path is valid
                    if (r.contains(dest)) {
                        // Remove duplicates from path
                        ArrayList<Road> temp = new ArrayList<>(new LinkedHashSet<>(r.path()));
                        r.path().clear();
                        r.path().addAll(temp);
                        //System.out.println(temp);
                    } else {
                        // Leave out invalid paths
                        r.path().clear();
                        continue;
                    }
                }
            } else {
                Road rd = cons.get(0);
                ArrayList<Road> temp = new ArrayList<>(new LinkedHashSet<>(rd.path()));
                
                rd.path().clear();
                rd.path().addAll(temp); 
            } 
            Collections.sort(cons, new sortPath());    
            
            if (path == null)
                path = cons.get(0).path();

            // Adjust connection costs
            for (Road r : path) {
                int diff = cities.get(r);
                cities.put(r, cost+diff);
            }

            memo.put(new Road(start, dest), path);
            //System.out.println(memo);
            clearPath(cities);            
        }
        long t2 = System.nanoTime();
        //System.out.println("solving took " + ((t2-t1) / 1000000) + " ms");            
        
        t1 = System.nanoTime();

        int max = 0;
        HashSet<Map.Entry<Road, Integer>> best = new HashSet<>();

        for (Map.Entry<Road, Integer> entry : cities.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                best.clear();
                best.add(entry);
            }
            if (entry.getValue() == max)
                best.add(entry);
        }

        for (Map.Entry<Road, Integer> r : best) {
            bw.write(r.getKey().con()[0] + "," + r.getKey().con()[1] + "\n");
        }
        t2 = System.nanoTime();
        //System.out.println("solving took " + ((t2-t1) / 1000000) + " ms");            
        
        br.close();
        bw.close();
    }    

    static HashMap<Integer, ArrayList<Road>> stop2road = new HashMap<>();

    // Is called for every fact
    public static HashSet<Road> path(TreeMap<Road, Integer> cities, int start, int dest, HashMap<Road, ArrayList<Road>> memo) {
        Queue<Road> queue1 = new LinkedList<Road>();
        Queue<Road> queue2 = new LinkedList<Road>();

        // Find all lines containing start
        for (Road line : stop2road.get(start)) {
            if (line.contains(start)) {
                line.path().add(line);
                queue1.add(line);
            } 
        }
        
        // BFS - dequeue and enqueue
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            
            // One queue contains all paths of equal length, when we find first shortest we check city names
            Queue<Road> queue = (queue1.isEmpty() ? queue2 : queue1);
            Queue<Road> other = (queue1.isEmpty() ? queue1 : queue2);
            
            // Avoid checking paths multiple times
            HashSet<Road> seen = new HashSet<>(queue);         
            
            while (!queue.isEmpty()) {

                Road line = queue.remove();
    
                // check all other paths in current queue
                if (line.contains(dest)) {
                    queue.add(line);
                    HashSet<Road> r = new HashSet<>(queue);
                    return r;
                }

                int stop1 = line.con()[0];
                int stop2 = line.con()[1];
    
                for (Road r : stop2road.get(stop1)) {
                    if (r.seen() || seen.contains(r))
                        continue;
                    r.pathStep(line.path());
                    other.add(r);
                    r.seen(true);
                }
                
                for (Road r : stop2road.get(stop2)) {
                    if (r.seen() || seen.contains(r))
                        continue;
                    r.pathStep(line.path());
                    other.add(r);
                    r.seen(true);
                }


            }
        }
        return null;
    }

    public static void clearPath(TreeMap<Road, Integer> cities) {
        for (Road r : cities.keySet()) {
            r.path().clear();
            r.seen(false);
        }
    }
}

class Road {
    private int[] connection;
    private ArrayList<Road> path;
    private boolean seen;

    public Road(int a, int b) {
        this.connection = new int[2];
        connection[0] = a;
        connection[1] = b;
        this.path = new ArrayList<>(5);
        seen = false;
    }
    
    public void pathStep(ArrayList<Road> path) {
        this.path.addAll(path);
        this.path.add(this);
    }

    public boolean seen() {
        return this.seen;
    }

    public void seen(boolean s) {
        this.seen = s;
    }

    public int[] con() {
        return this.connection;
    }

    public ArrayList<Road> path() {
        return this.path;
    }

    public boolean contains(int a) {
        if (this.connection[0] == a || this.connection[1] == a)
            return true;
        return false;
    }

    public String toString() {
        return String.format("[%d <-> %d]", this.connection[0], this.connection[1]);
    }
}

class sortRoad implements Comparator<Road> {
    public int compare(Road a, Road b) {
        if (a.con()[0] < b.con()[0])
            return -1;
        if (a.con()[0] > b.con()[0])
            return 1;
        else {
            if (a.con()[1] < b.con()[1])
                return -1;
            if (a.con()[1] > b.con()[1])
                return 1;
        }
        return 0;
    }
} 


class sortPath implements Comparator<Road> {
    public int compare(Road a, Road b) {
        ArrayList<Road> a_path = a.path();
        ArrayList<Road> b_path = b.path();
        int prevA = -1;
        int distA = -1;
        int prevB = -1;
        int distB = -1;
        
        for (int i = 0; i < Math.min(a_path.size(), b_path.size()); i++) {
            int[] a_con = a_path.get(i).con();
            int[] b_con = b_path.get(i).con();
            int checkA = 0;
            int checkB = 0;

            if (i == 0) {
                if (a_con[0] < b_con[0])
                    return -1;
                if (a_con[0] > b_con[0])
                    return 1;
                if (a_con[0] == b_con[0]) {
                    if (a_con[1] < b_con[1])
                        return -1;
                    if (a_con[1] > b_con[1])
                        return 1;
                    }
                prevA = a_con[0];
                distA = a_con[1];
                prevB = b_con[0];
                distB = b_con[1];
            } else {
                if (a_con[0] == prevA)
                    checkA = a_con[1];
                else if (distA != -1 && a_con[0] == distA)
                    checkA = a_con[1];
                else if (a_con[1] == prevA)
                    checkA = a_con[0];
                else if (distA != -1 && a_con[1] == distA)
                    checkA = a_con[0];
                prevA = checkA;
                distA = -1;

                if (b_con[0] == prevB)
                    checkB = b_con[1];
                else if (distB != -1 && b_con[0] == distB)
                    checkB = b_con[1];
                else if (b_con[1] == prevB)
                    checkB = b_con[0];
                else if (distB != -1 && b_con[1] == distB)
                    checkB = b_con[0];
                prevB = checkB;
                distB = -1;
            }

            if (checkA < checkB)
                return -1;
            if (checkA > checkB)
                return 1;
        }

        if (a.path().size() < b.path().size())
            return 1;
        if (b.path().size() < a.path().size())
            return -1;
        return 0;
    }
}
