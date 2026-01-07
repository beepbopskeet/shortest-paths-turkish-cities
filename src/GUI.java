import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.*;


public class GUI {
    //data fields
    private static Graph graph;
    private static Dijkstra dijkstra;
    private static DFSPathFinder dfs;
    private static DFSShortestPathFinder dfsShortest;

    public static void main(String[] args) {

        //load and initialize algorithms for finding path
        try {
            graph = CityCSVLoader.loadFromDistanceMatrix("Turkish cities.csv");
            dijkstra = new Dijkstra("Turkish cities.csv");
            dfs = new DFSPathFinder(graph);
            dfsShortest = new DFSShortestPathFinder(graph);
        }

        //exception if dataset loading fails
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while loading the dataset: "+e.getMessage());
            return;
        }

        //main frame of the gui
        JFrame ui_frame = new JFrame("Shortest Path Problem");
        //panel that holds gui components
        JPanel ui_panel = new JPanel();
        ui_panel.setBackground(Color.GRAY);

        //labels for inpult fields
        JLabel startLabel = new JLabel("Start City:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel destinationLabel = new JLabel("Destination City:");
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 15));

        //text area to display the results of algorithms
        JTextArea resultArea = new JTextArea(40, 70);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        JScrollPane scrollResult = new JScrollPane(resultArea);

        //input fields for user input
        JTextField startField = new JTextField(20);
        JTextField destinationField = new JTextField(20);

        //button for running algorithms
        JButton runButton = new JButton("Run Algorithms");

        //panels for arranging GUI components with layout and color settings
        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        startRow.setBackground(Color.GRAY);

        JPanel destinationRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        destinationRow.setBackground(Color.GRAY);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setBackground(Color.GRAY);

        JPanel resultRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultRow.setBackground(Color.WHITE);

        //window size and close behavior
        ui_frame.setSize(600, 500);
        ui_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ui_panel.setLayout(new BoxLayout(ui_panel, BoxLayout.Y_AXIS));
        ui_panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        //handle button click: validate input and prepare city names
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startCityInput = startField.getText().trim();
                String destinationCityInput = destinationField.getText().trim();

                //check if input fields are empty
                if (startCityInput.isEmpty() || destinationCityInput.isEmpty()) {
                    resultArea.setText("Please enter both cities.");
                    return;
                }

                //case insensitive handling of user input
                String startCity = makeCityNameCaseInsensitive(startCityInput);
                String destCity = makeCityNameCaseInsensitive(destinationCityInput);

                if (startCity == null || destCity == null) {
                    resultArea.setText("One or both cities not found in the database.");
                    return;
                }

                // Redirect System.out to capture algorithm output
                PrintStream originalOut = System.out;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.setOut(new PrintStream(baos));

                try {
                    //run path finding algorithms and display results
                    System.out.println("DFS Algorithm: ");
                    dfs.dfs(startCity, destCity);

                    System.out.println("\nDFS (Shortest) Algorithm: ");
                    dfsShortest.dfsShortest(startCity, destCity);

                    System.out.println("\nDijkstra's Algorithm: ");
                    Dijkstra.DijkstraResult dResult = dijkstra.findShortestPaths(startCity);
                    int destIdx = dijkstra.getCityIndex(destCity);

                    System.out.println("Path: " + dResult.getPathString(destIdx));
                    System.out.println("Distance: " + (int)dResult.getDistance(destIdx) + " km");
                    
                    resultArea.setText(baos.toString());
                    resultArea.setCaretPosition(0);

                } catch (Exception ex) {
                    resultArea.setText("Error during calculation: " + ex.getMessage());
                } finally {
                    System.setOut(originalOut); // Reset console output
                }
            }
        });

        startRow.add(startLabel);
        startRow.add(startField);

        destinationRow.add(destinationLabel);
        destinationRow.add(destinationField);

        buttonRow.add(runButton);

        resultRow.add(scrollResult);

        ui_panel.add(startRow);
        ui_panel.add(Box.createVerticalStrut(8));
        ui_panel.add(destinationRow);
        ui_panel.add(Box.createVerticalStrut(15));
        ui_panel.add(buttonRow);
        ui_panel.add(Box.createVerticalStrut(15));
        ui_panel.add(resultRow);

        ui_frame.add(ui_panel, BorderLayout.CENTER);
        ui_frame.setVisible(true);
    }

    public static String makeCityNameCaseInsensitive(String input){
        ArrayList<String> cityList = graph.getCities();
        for(int i = 0; i < cityList.size(); i++){
            String city = cityList.get(i);
            if (city.equalsIgnoreCase(input)) {
                return city;
                
            }
        }
        return null;
    }
}
