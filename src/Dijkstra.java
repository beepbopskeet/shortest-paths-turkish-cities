import java.io.*; // used for reading the CSV file
import java.util.Arrays; // * means get everything from this package
import java.util.PriorityQueue;

public class Dijkstra {    
    private String[] cityNames; // all city names go here
    private double[][] adjacencyMatrix; // stores distances between cities
    private int numCities; // how many cities total
    
    // this helps us keep track of a city and how far it is
    private static class CityDistance implements Comparable<CityDistance> {
        int cityIndex;
        double distance;        
        public CityDistance(int cityIndex, double distance) {
        this.cityIndex = cityIndex;
        this.distance = distance;
        }        
        // compare cities by distance (smaller is better)
        @Override
        public int compareTo(CityDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
    
    // when we create Dijkstra we load the CSV file
    public Dijkstra(String csvFilePath) {
        readCSV(csvFilePath);
    }
    
    //TODO: Delete this and use the CityCSVLoader instead
    // reads the CSV file and builds our graph
    private void readCSV(String csvFilePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            String headerLine = br.readLine(); // first line has city names
            String[] headers = headerLine.split(",");
            numCities = headers.length - 1; // first cell is empty so skip it
            cityNames = new String[numCities];
            for (int i = 0; i < numCities; i++) {
                cityNames[i] = headers[i + 1].trim(); // save each city name
            }
            adjacencyMatrix = new double[numCities][numCities];
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < numCities) {
                String[] values = line.split(",");
                for (int col = 0; col < numCities; col++) {
                    String value = values[col + 1].trim();
                    adjacencyMatrix[row][col] = Double.parseDouble(value); // save distance
                }
                row++;
            }
            br.close();
            System.out.println("Loaded " + numCities + " cities!");
        } catch (Exception e) { // catch = if something goes wrong do this
            System.out.println("Problem reading file!");
        }
    }
    // find shortest paths starting from a city 
    public DijkstraResult findShortestPaths(String sourceCityName) {
        int sourceIndex = getCityIndex(sourceCityName);
        if (sourceIndex == -1) {
            System.out.println("City not found: " + sourceCityName);
            return null;
        }
        return findShortestPaths(sourceIndex);
    }
    
    // DIJKSTRA'S ALGORITHM: finds shortest paths from one city to all others
    public DijkstraResult findShortestPaths(int sourceIndex) {
        long start = System.nanoTime(); // start timer (remove)
        double[] distance = new double[numCities]; // how far to each city
        Arrays.fill(distance, Double.POSITIVE_INFINITY); // start with infinity
        distance[sourceIndex] = 0.0; // starting city is 0 km away
        int[] predecessor = new int[numCities]; // tracks the path
        Arrays.fill(predecessor, -1); // -1 means no path yet
        boolean[] visited = new boolean[numCities]; // marks cities we checked
        
        PriorityQueue<CityDistance> pq = new PriorityQueue<>(); // priority queue (min heap)
        pq.offer(new CityDistance(sourceIndex, 0.0)); // add starting city
        
        while (!pq.isEmpty()) { // keep going until done
            CityDistance current = pq.poll(); // get closest city
            int u = current.cityIndex;
            if (visited[u]) continue; // skip if already checked
            visited[u] = true; // mark as checked
            
            for (int v = 0; v < numCities; v++) { // check all neighbors
                if (u == v || adjacencyMatrix[u][v] >= 99999 || visited[v]) continue; // skip if no road
                double newDist = distance[u] + adjacencyMatrix[u][v]; // calculate new distance
                if (newDist < distance[v]) { // found shorter path?
                    distance[v] = newDist; // update distance
                    predecessor[v] = u; // remember path
                    pq.offer(new CityDistance(v, newDist)); // add to queue
                }
            }
        }
        long end = System.nanoTime();//remove
        double time = (end - start) / 1_000_000.0; // time in milliseconds
        return new DijkstraResult(distance, predecessor, sourceIndex, time);
    }
    
    // find city's number from its name
    public int getCityIndex(String cityName) {
        for (int i = 0; i < numCities; i++) {
            if (cityNames[i].equalsIgnoreCase(cityName)) {
                return i;
            }
        }
        return -1;
    }    
    // get city name from number
    public String getCityName(int index) {
        if (index >= 0 && index < numCities) {
            return cityNames[index];
        }
        return "Unknown";
    }    
    public String[] getCityNames() {return cityNames;}    
    public int getNumCities() {return numCities;}
    
    // stores results after running Dijkstra
    public class DijkstraResult {
        private double[] distances;
        private int[] predecessors;
        private int sourceIndex;
        private double time;
        
        public DijkstraResult(double[] distances, int[] predecessors, int sourceIndex, double time) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.sourceIndex = sourceIndex;
            this.time = time;
        }
        
        public double getTime() {
            return time;
        }        
        // get distance to a city
        public double getDistance(int cityIndex) {
            return distances[cityIndex];
        }        
        public double getDistance(String cityName) {
            int index = getCityIndex(cityName);
            if (index == -1) return -1;
            return distances[index];
        }        
        // builds the path from source to destination
        public ArrayList<Integer> getPath(int destIndex) {
            ArrayList<Integer> path = new ArrayList<>();
            if (distances[destIndex] == Double.POSITIVE_INFINITY) {
                return path; // can't reach
            }
            int current = destIndex;
            while (current != -1) { // go backwards
                path.add(current);
                current = predecessors[current];
            }
            path.reverse(); // flip to go forward
            return path;
        }
        
        public ArrayList<Integer> getPath(String cityName) {
            int index = getCityIndex(cityName);
            if (index == -1) return new ArrayList<>();
            return getPath(index);
        }
        
        // makes path string 
        public String getPathString(int destIndex) {
            ArrayList<Integer> path = getPath(destIndex);
            if (path.isEmpty()) {
                return "No path";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                sb.append(cityNames[path.get(i)]);
                if (i < path.size() - 1) {
                    sb.append(" -> ");
                }
            }
            return sb.toString();
        }
        
        public String getPathString(String cityName) {
            int index = getCityIndex(cityName);
            if (index == -1) return "City not found";
            return getPathString(index);
        }
    }
    
    // compare the path between two cities
    public void comparePath(String source, String destination) {
        DijkstraResult result = findShortestPaths(source);
        if (result != null) {
            int destIndex = getCityIndex(destination);
            if (destIndex != -1) {
                System.out.printf("Distance: %.0f km\n", result.getDistance(destIndex));
                System.out.println("Path: " + result.getPathString(destIndex));
                System.out.printf("Time: %.3f ms\n", result.getTime());
                System.out.println();
            }
        }
    }    
}