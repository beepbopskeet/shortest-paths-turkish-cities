public class DFSPathFinder {

    private final Graph graph;

    public DFSPathFinder(Graph g){
        this.graph = g;
    }

    //Traversal
    public void dfs(String start, String target){

        ArrayStack<String> stack = new ArrayStack<>(graph.getCities().size());//The next city to explore is always the one on top of the stack
        ArrayList<String> visited = new ArrayList<>();//all cities that have already been visited
        ArrayList<String> parentCity = new ArrayList<>();//parent city for each visited city

        //Push start
        stack.pushCity(start);
        visited.add(start);
        parentCity.add(null);

        while(!stack.isEmpty()){

            String current = stack.popCity();

            if(current.equals(target)){//checks if the city at the top of the stack is our target city
                printPath(visited, parentCity, start, target);
                return;
                
            }

            //Expand neighbors
            ArrayList<Graph.Edge> neighbors = graph.getNeighbors(current);

            for(int i = 0; i < neighbors.size(); i++){
                Graph.Edge e = neighbors.get(i);

                if(!visited.contains(e.to)){
                    visited.add(e.to);
                    parentCity.add(current);
                    stack.pushCity(e.to);
                }
            }

        }

        System.out.println( "No path found.");

    }

    //Path reconstruction
    private void printPath(ArrayList<String> visited, ArrayList<String> parent, String start, String target){

        ArrayList<String> path = new ArrayList<>();
        int totalDistance = 0;

        String current = target;

        while (current != null) { 
            path.add(current);
            String previous = getParentOf(current, visited, parent);

            if(previous != null){
                totalDistance += graph.getDistance(previous, current);
            }
            current = previous;
        }

        path.reverse();
        System.out.println("DFS Path: " + path.toString());
        System.out.println("Total Distance: " + totalDistance + "km");
    }

    private String getParentOf(String city, ArrayList<String> visited, ArrayList<String> parent){
        for(int i = 0; i < visited.size(); i++){
            if(visited.get(i).equals(city)){
                return parent.get(i);
            }
        }
        return null;
    }
}