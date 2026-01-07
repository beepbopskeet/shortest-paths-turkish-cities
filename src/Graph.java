public class Graph {
    
    private ArrayList<String> cities;
    private ArrayList<ArrayList<Edge>> adj;
    private int cityCount;

    public Graph(){
       cities =  new ArrayList<>();
       adj = new ArrayList<>();
       cityCount = 0;
    }

    //Checks if the city exists, if it does, it returns its index
    private int ensureCity(String name){

        int idx = getCityIndex(name);
        if(idx != -1){
            return idx;
        }

        //add city and empty neighbor list
        cities.add(name);
        adj.add(new ArrayList<Edge>());
        cityCount++;
        return cityCount -1;
    }

    //Gets the index of the specified city, returns -1 if it cant find it found.
    public int getCityIndex(String name){

        for (int i = 0; i < cityCount; i++) {
            if (cities.get(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    // Add directed edge; will create cities if they don't exist
    public void addEdge(String from, String to, int distance){
        int i = ensureCity(from);
        int j = ensureCity(to);

        // add neighbor (directed)
        adj.get(i).add(new Edge(to, distance));
    }

    // Add undirected edge (both directions)
    public void addUndirectedEdge(String a, String b, int distance){
        addEdge(a, b, distance);
        addEdge(a, b, distance);
    }

    // Return neighbors (might be empty)
    public ArrayList<Edge> getNeighbors(String city){
        int i = getCityIndex(city);
        if (i == -1) {
            return new ArrayList<Edge>();
        }
        return adj.get(i);
    }

    //Gets the distance between two adjacent cities
    public int getDistance(String from, String to){

        int i = getCityIndex(from);
        if(i == -1){
            return 99999;
        }

        ArrayList<Edge> list = adj.get(i);
        for (int k = 0; k < list.size(); k++){
            Edge e = list.get(k);
            if (e.to.equals(to)){
                return e.dist;
            }
        }
        return 99999;
    }


    //Return all city names
    public ArrayList<String> getCities(){
        return cities;
    }

    public static class Edge{
        public String to;
        public int dist;
        public Edge(String to, int dist){
            this.to = to;
            this.dist = dist;
        }
    }
}
