import java.io.*;
import java.util.*;

public class Naloga7 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        int line_count = Integer.parseInt(br.readLine());
        ArrayList<ArrayList<Integer>> lines = new ArrayList<ArrayList<Integer>>();

        // Store each line as a linked list
        for (int i = 0; i < line_count; i++) {
            String[] l = br.readLine().split(",");
            ArrayList<Integer> line = new ArrayList<>(); 

            for (int j = 0; j < l.length; j++) {
                line.add(Integer.parseInt(l[j]));
            }

            // Store each line in an array of lines 
            lines.add(line);
        }

        String[] path = br.readLine().split(",");
        int start = Integer.parseInt(path[0]);  // The stop at which we start the travel
        int dest = Integer.parseInt(path[1]);   // The destination


        boolean existsS = false;
        boolean existsD = false;
        
        // Check if stops exist
        for (ArrayList<Integer> line : lines) {
            if (line.contains(start))
                existsS = true;
            
            if (line.contains(dest))
                existsD = true;
        }

        if (!existsD || !existsS) {
            br.close();
            bw.close();
            return;
        }

        // Min transfers
        int min_transfers = transfers(lines, start, dest);
        
        // Min stops
        int min_stops = stops(lines, start, dest);
 
        // path1 == path2 ??
       
        bw.write(min_transfers + "\n" + min_stops + "\n" + 
            (transfers_min_stop == -1 ? (stops_min_transfer == 0 ? (cnt != 0 ? (cnt == min_transfers ? 1 : 0) : 1) : (stops_min_transfer == min_stops ? 1 : 0)) : (transfers_min_stop == min_transfers ? 1 : 0))  + "\n");

        br.close();
        bw.close();
    }

    static int stops_min_transfer = 0;
    static int transfers_min_stop = -1;

    // BFS on transfers
    public static int transfers(ArrayList<ArrayList<Integer>> lines, int start, int dest) {
        Queue<ArrayList<Integer>> queue1 = new LinkedList<ArrayList<Integer>>();
        Queue<ArrayList<Integer>> queue2 = new LinkedList<ArrayList<Integer>>();
        HashSet<ArrayList<Integer>> seen = new HashSet<>();

        int transfer_count = 0;

        // Find all lines containing start
        for (ArrayList<Integer> line : lines) {
            if (line.contains(start)) {
                if (line.contains(dest)) {
                    stops_min_transfer = Math.abs(line.indexOf(start) - line.indexOf(dest));
                    return 0;
                }
                queue1.add(line);
            } 
        }

        // BFS - dequeue and enqueue
        while (!queue1.isEmpty() || !queue2.isEmpty()) {

            // 2 queues so we know when to increase transfer count
            Queue<ArrayList<Integer>> queue = (queue1.isEmpty() ? queue2 : queue1);
            Queue<ArrayList<Integer>> other = (queue1.isEmpty() ? queue1 : queue2);
            seen.addAll(queue);
            
            while (!queue.isEmpty()) {

                ArrayList<Integer> line = queue.remove();
    
                if (line.contains(dest)) {
                    return transfer_count;
                }
    
                // enqueue all lines we could transfer to
                for (ArrayList<Integer> l : lines) {
                    if (seen.contains(l))
                        continue;
                    for (int stop : line) {
                        if (l.contains(stop)) {
                            other.add(l);
                            seen.add(l);
                        }
                    }
                }
            }

            transfer_count++;
        }

        return -1;
    }

    static int cnt = 0;
    static Queue<Integer> transfers = new LinkedList<>();
    
    public static int stops(ArrayList<ArrayList<Integer>> lines, int start, int dest) {
        Queue<Integer> queue1 = new LinkedList<Integer>();
        Queue<Integer> queue2 = new LinkedList<Integer>();
        HashSet<ArrayList<Integer>> seen = new HashSet<>();
        HashSet<Integer> seenStops = new HashSet<>();
            
        int stop_count = 1;

        // Find all adjacent stops
        for (ArrayList<Integer> line : lines) {
            // Add adjacent stops on every line containing start
            if (line.contains(start)) {
                int i = line.indexOf(start);
                if (i-1 >= 0) {
                    if (line.get(i-1) == dest) {
                        transfers_min_stop = 0;
                        return 1;
                    }
                    queue1.add(line.get(i-1));
                    transfers.add(0);
                    seen.add(line);
                }
                if (i+1 < line.size()) {
                    if (line.get(i+1) == dest) {
                        transfers_min_stop = 0;
                        return 1;
                    }
                    queue1.add(line.get(i+1));
                    transfers.add(0);
                    seen.add(line);
                }
            }
        }
        
        // BFS - dequeue and enqueue
        while (!queue1.isEmpty() || !queue2.isEmpty()) {

            Queue<Integer> queue = (queue1.isEmpty() ? queue2 : queue1);
            Queue<Integer> other = (queue1.isEmpty() ? queue1 : queue2);
            seenStops.addAll(queue);

            // +1 level depth
            while (!queue.isEmpty()) {

                int stop = queue.remove();
                cnt = transfers.remove();

                if (stop == dest) {
                    // Always only one shortest path so we dont need to check others
                    return stop_count;
                } 
                
                // Find every line containing current stop
                for (ArrayList<Integer> line : lines) {
                    // Add adjacent stops on every such line
                    if (line.contains(stop)) {
                        
                        int i = line.indexOf(stop);

                        // Add left station
                        if (i-1 >= 0 && !seenStops.contains(line.get(i-1))) {
                            transfers.add((seen.contains(line) ? cnt : cnt+1));
                            other.add(line.get(i-1));
                            seenStops.add(line.get(i-1));
                            seen.add(line);
                        }
                        // Add right station
                        if (i+1 < line.size() && !seenStops.contains(line.get(i+1))) {
                            transfers.add((seen.contains(line) ? cnt : cnt+1));
                            other.add(line.get(i+1));
                            seenStops.add(line.get(i+1));
                            seen.add(line);
                        }
                    }
                }
            }

            stop_count++;
        }

        return -1;
    }

} 