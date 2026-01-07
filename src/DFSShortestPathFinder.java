public class DFSShortestPathFinder {
    
    private final Graph graph;

    public DFSShortestPathFinder(Graph g){
        this.graph = g;
    }

    // a simple data structure storing current city, visited cities along the path, and total dist along the current path
    private static class State{
        String city;
        ArrayList<String> path;
        int dist;

        State(String c, ArrayList<String> p, int d){
            city = c;
            path = p;
            dist = d;
        }

    }

    public void dfsShortest(String start, String target){

        ArrayStack<State> stack = new ArrayStack<>(graph.getCities().size());
        ArrayList<String> initialPath = new ArrayList<>();
        initialPath.add(start);

        stack.pushCity(new State(start, initialPath, 0));

        int bestDist = Integer.MAX_VALUE;
        ArrayList<String> bestPath = null;

        while(!stack.isEmpty()){

            State s = stack.popCity();

            if(s.city.equals(target)){
                if (s.dist < bestDist) {
                    bestDist = s.dist;
                    bestPath = s.path;
                }
                continue;
            }
            
            //Expand neighbors
            ArrayList<Graph.Edge> neighbors = graph.getNeighbors(s.city);

            for (int i = 0; i < neighbors.size(); i++){
                Graph.Edge e = neighbors.get(i);
                
                //Avoid redundant cycles
                if(!s.path.contains(e.to)){

                    //Copying path
                    ArrayList<String> newPath = clonePath(s.path);
                    newPath.add(e.to);

                    int newDist = s.dist + e.dist;

                    if(newDist < bestDist){//Adds only if the new distance is smaller
                        stack.pushCity(new State(e.to, newPath, newDist));
                    }
                    
                }
            }
        }

        if(bestPath == null){
            System.out.println("No Path Found...");
            return;
        }

        System.out.println("Shortest DFS Path: " + bestPath.toString());
        System.out.println("Minimum Distance: " + bestDist + " km");

    }
    //Copies all the elements, jnot just the reference, so every city can be modified seperately
    private ArrayList<String> clonePath(ArrayList<String> src){
        ArrayList<String> copy = new ArrayList<>();
        for(int i = 0; i < src.size(); i++){
            copy.add(src.get(i));
        }
        return copy;
    }
}
