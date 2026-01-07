import java.io.BufferedReader;
import java.io.FileReader;

public class CityCSVLoader{
    public static Graph loadFromDistanceMatrix(String filepath) throws Exception{
        Graph g = new Graph();

        BufferedReader buffer = new BufferedReader(new FileReader(filepath));
        String header = buffer.readLine();

        if (header == null) {
            return g;
        }
        //When seperating each word/number in each line, we use a comma as the delimiter
        String[] headerTokens = header.split(",");
        int n = headerTokens.length -1;//First cell is empty
        String[] cols = new String[n]; 

        for (int i = 1; i < headerTokens.length; i++) {
            cols[i-1] = headerTokens[i].trim();
            g.addEdge(cols[i-1], cols[i-1], 0);
        }

        String line;

        while((line = buffer.readLine()) != null){
            String[] parts = line.split(",");

            if(parts.length < 2){
                continue;
            }

            String rowCity = parts[0].trim();
            g.addEdge(rowCity, rowCity, 0);

            for(int j = 1; j < parts.length && j-1 < cols.length; j++){
                String colCity = cols[j-1];
                String val = parts[j].trim();
                int dist;

                try {
                    dist = Integer.parseInt(val);//casting the string val into an integer and assigns it into dist
                } 
                catch (NumberFormatException e) {
                    dist = 99999;
                }

                if(dist != 0 && dist < 99999){
                    //add undirected edge (no self duplicate)
                    g.addUndirectedEdge(rowCity, colCity, dist);
                }
            }

        }
        return g;

    }
}