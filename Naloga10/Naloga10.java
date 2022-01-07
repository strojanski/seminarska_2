import java.io.*;
import java.util.*;

public class Naloga10 {
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));

        int point_count = Integer.parseInt(br.readLine());

        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<Point> points = new ArrayList<>();
        HashMap<Pair, Double> distances = new HashMap<>();   

        for (int i = 0; i < point_count; i++) {
            String[] line = br.readLine().split(",");
            ArrayList<Point> p = new ArrayList<>();
            Point point = new Point(i+1, line[0], line[1]); 
            p.add(point);
            points.add(point);
            Group g = new Group(p); 
            point.setGroup(g);
            // Each point is a group at first
            groups.add(g);
        }

            
        int wanted_group_count = Integer.parseInt(br.readLine());

        // Connect points
        getDist(points, distances);
        connect(distances, groups, wanted_group_count);
       
        Collections.sort(groups, new sortGroups());

        for (Group g : groups) {
            bw.write(g.toString() + "\n");
        }

        br.close();
        bw.close();
    }

    public static void getDist(ArrayList<Point> points, HashMap<Pair, Double> distances) {
        for (Point p1 : points) {
            for (Point p2 : points) {
                if (!p1.equals(p2) && p1.id() != p2.id()) {
                    double dist = euclideanDistance(p1, p2);
                    Pair p = new Pair(p1, p2);
                    if (!distances.containsKey(p))
                        distances.put(p, dist);                   
                }
            }
        }
    }

    public static void connect(HashMap<Pair, Double> distances, ArrayList<Group> groups, int wanted_group_count) {
        
        ArrayList<Map.Entry<Pair, Double>> entries = new ArrayList<>();
        entries.addAll(distances.entrySet());
        Collections.sort(entries, new sortEntry());
        
        for (int i = 0; i < entries.size(); i++) {
            if (groups.size() == wanted_group_count)
                break;

            Map.Entry<Pair, Double> entry = entries.get(i);
            //System.out.println(entry);
            Pair p = entry.getKey();
            Group ga = p.a().group();
            Group gb = p.b().group();

            if (ga.equals(gb))
                continue;
            if (ga.pointsInside.size() > gb.pointsInside.size()) {
                ga.addPoints(gb.pointsInside);
                groups.remove(gb);
                p.b().setGroup(ga);
            } else {
                gb.addPoints(ga.pointsInside);
                groups.remove(ga);
                p.a().setGroup(gb);
            }
        }
    }


    public static double euclideanDistance(Point a, Point b) {
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
    }
}

class Pair {
    private Point a, b;

    public Pair(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Point a() {
        return this.a;
    }

    public Point b() {
        return this.b;
    }

    @Override
    public String toString() {
        return a + " " + b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair == false)
            return false;
        Pair b = (Pair) obj;
        if (this.a.equals(b.a()) && this.b.equals(b.b()) || this.b().equals(b.a()) && this.a().equals(b.b()))
            return true;
        return false;
    }
}

class Group {
    public ArrayList<Point> pointsInside;
    public Group closestGroup;
    public double closestGroupDist;
    public int groupId;

    public Group() {
        this.closestGroupDist = 0;
        this.groupId = this.pointsInside.hashCode();
    }

    public Group(ArrayList<Point> pi) {
        this.pointsInside = pi;
        this.groupId = this.pointsInside.hashCode();
    }

    public Group(Group g) {
        this.pointsInside = g.pointsInside;
        this.groupId = this.pointsInside.hashCode();
    }

    public void addPoint(Point point) {
        if (!this.pointsInside.contains(point))
            this.pointsInside.add(point);
        point.setGroup(this);
        Collections.sort(this.pointsInside, new sortPoints());
    }

    public void addPoints(ArrayList<Point> points) {
        for (Point new_point : points) {
            if (this.pointsInside.contains(new_point))
                continue;
            this.pointsInside.add(new_point);
            new_point.setGroup(this);
        }
        Collections.sort(this.pointsInside, new sortPoints());
    }

    // Finds the point closest to a given point
    public double findGroupDist(Group other) {
        double min_dist = Double.MAX_VALUE;

        for (Point p1 : this.pointsInside) {
            for (Point p2 : other.pointsInside) {
                double dist = Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
                if (dist < min_dist) {
                    min_dist = dist;
                }
            }
        }
        return min_dist;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.pointsInside.size(); i++) {
            Point p = this.pointsInside.get(i);
            sb.append(p.toString());
            if (i < this.pointsInside.size()-1)
                sb.append(",");
        }
        return sb.toString();
        //return String.format("%s", this.pointsInside.toString());
    }
}

class sortPoints implements Comparator<Point> {
    public int compare(Point a, Point b) {
        if (a.id() < b.id())
            return -1;
        if (a.id() > b.id())
            return 1;
        return 0;
    }
}

class sortGroups implements Comparator<Group> {
    public int compare (Group a, Group b) {
        Collections.sort(a.pointsInside, new sortPoints());
        Collections.sort(b.pointsInside, new sortPoints());
        for (int i = 0; i < a.pointsInside.size(); i++) {
            if (i > b.pointsInside.size())
                return -1;
            if (a.pointsInside.get(i).id() < b.pointsInside.get(i).id())
                return -1;
            if (a.pointsInside.get(i).id() > b.pointsInside.get(i).id())
                return 1;
            else   
                continue;
        }
        return 0;
    }
}

class Point {
    private int id;
    public double x, y;
    private Group group;
    private TreeMap<Point, Double> point2dist;
    public boolean taken;

    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.point2dist = new TreeMap<>(new sortPoints());
        this.group = null;
        this.taken = false;
    }
    
    public Point(int id, String x, String y) {
        this.id = id;
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
        this.group = null;
        this.point2dist = new TreeMap<>(new sortPoints());
        this.taken = false;
    }

    public TreeMap<Point, Double> point2dist() {
        return this.point2dist;
    }

    // Tells which group point is in
    public Group group() {
        return this.group;
    }

    public int id(){
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }

    public void setGroup(Group g) {
        this.group = g;
    }
}

class sortEntry implements Comparator<Map.Entry<Pair, Double>> {
    public int compare(Map.Entry<Pair, Double> e1, Map.Entry<Pair, Double> e2) {
        if (e1.getValue() < e2.getValue())
            return -1;
        if (e1.getValue() > e2.getValue())
            return 1;
        return 0;
    }
}