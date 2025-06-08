import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // Main method, which throws FileNotFoundException if the input files cannot be found
    public static void main(String[] args) throws FileNotFoundException {
        // Define the file names for city connections and coordinates
        String cityConnections = "docs/city_connections.txt";
        String cityCoordinates = "docs/city_coordinates.txt";

        // Initialize File objects in order to read the data
        File cityConnectionsFile = new File(cityConnections);
        File cityCoordinatesFile = new File(cityCoordinates);

        // Check if files exists, exit if not
        if (!cityConnectionsFile.exists()) {
            System.out.printf("%s cannot be found", cityConnections);
            System.exit(1);
        }
        if (!cityCoordinatesFile.exists()) {
            System.out.printf("%s cannot be found", cityCoordinates);
            System.exit(1);
        }

        // Initialize an Arraylist for storing the cities
        ArrayList<City> cityArrayList = new ArrayList<>();

        // Initialize scanners for reading files
        Scanner inputCityCoordinatesFile = new Scanner(cityCoordinatesFile);
        Scanner inputCityConnectionsFile = new Scanner(cityConnectionsFile);

        // Counter for cities
        int counter = 0;

        // Reading city coordinates file
        while (inputCityCoordinatesFile.hasNextLine()) {
            // Getting data line by line, and splitting each line to its components
            String[] nextLineArray = inputCityCoordinatesFile
                .nextLine()
                .split(", ");

            // Extract city name and city coordinates
            String cityName = nextLineArray[0];
            int xCoordinate = Integer.parseInt(nextLineArray[1]);
            int yCoordinate = Integer.parseInt(nextLineArray[2]);

            // Initialize new City object and add it to the cityArrayList.
            City temporaryCity = new City(cityName, xCoordinate, yCoordinate);
            cityArrayList.add(temporaryCity);

            // Increment the city counter by one
            counter += 1;
        }
        // Close the scanner
        inputCityCoordinatesFile.close();

        // Initialize an adjacency matrix for processing the city connections data
        // Defaulting it to infinity, which represent no connections between to cities
        double[][] adjacencyMatrix = new double[counter][counter];
        for (int i = 0; i < counter; i++) {
            for (int j = 0; j < counter; j++) {
                adjacencyMatrix[i][j] = Double.MAX_VALUE;
            }
        }

        // Set-up canvas for drawing
        StdDraw.setCanvasSize(2377 / 2, 1055 / 2);
        StdDraw.setXscale(0, 2377);
        StdDraw.setYscale(0, 1055);

        // Draw the background map
        StdDraw.picture(2377 / 2.0, 1055 / 2.0, "docs/map.png", 2377, 1055);

        // Enable double buffering for smoother drawing
        StdDraw.enableDoubleBuffering();

        // Pick a fancy font and set it.
        Font font = new Font("Arial", Font.PLAIN, 12);
        StdDraw.setFont(font);

        // Reading city connections file
        while (inputCityConnectionsFile.hasNextLine()) {
            // Getting data line by line, and splitting each line to its components
            String[] nextConnectionArray = inputCityConnectionsFile
                .nextLine()
                .split(",");

            // Extract city names from the line
            String city1 = nextConnectionArray[0];
            String city2 = nextConnectionArray[1];

            // Find and draw connections between cities by looping through cityArrayList
            for (City tempCity1 : cityArrayList) {
                for (City tempCity2 : cityArrayList) {
                    // Check for matching cities and make sure of the fact that they are not the same city.
                    if (city1.equals(tempCity1.cityName)) {
                        double x1 = tempCity1.x;
                        double y1 = tempCity1.y;
                        int indexCity1 = cityArrayList.indexOf(tempCity1);

                        if (
                            (city2.equals(tempCity2.cityName)) &&
                            !(tempCity1.equals(tempCity2))
                        ) {
                            double x2 = tempCity2.x;
                            double y2 = tempCity2.y;
                            int indexCity2 = cityArrayList.indexOf(tempCity2);

                            // Calculate the distance by using Pythagorean's Theorem and update the adjacency matrix
                            adjacencyMatrix[indexCity1][indexCity2] = Math.sqrt(
                                (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))
                            );

                            // Since roads work for both ways, meaning that our map is not directed, update the other way around.
                            adjacencyMatrix[indexCity2][indexCity1] =
                                adjacencyMatrix[indexCity1][indexCity2];

                            // Draw the line representing the connection
                            StdDraw.setPenColor(Color.gray);
                            StdDraw.line(x1, y1, x2, y2);
                        }
                    }
                }
            }
        }
        // Close the scanner
        inputCityConnectionsFile.close();

        // Draw cities on the map
        for (int i = 0; i < counter; i++) {
            StdDraw.filledCircle(
                cityArrayList.get(i).x,
                cityArrayList.get(i).y,
                5
            );
            StdDraw.text(
                cityArrayList.get(i).x,
                cityArrayList.get(i).y + 15,
                cityArrayList.get(i).cityName
            );
        }

        StdDraw.show(); // Show the empty map

        // Initialize a new scanner for user input
        Scanner inputScanner = new Scanner(System.in);

        // Variables for starting city
        String startingCity;
        int startingCityIndex = 0;

        // Variables for destination city
        String destinationCity;
        int destinationCityIndex = 0;

        // Boolean variable in order to determine whether the input is valid or not
        boolean isValid = false;

        // Prompt and read the starting city
        System.out.print("Please enter starting city: ");

        while (!isValid) {
            if (inputScanner.hasNextLine()) {
                startingCity = inputScanner.nextLine();

                // Check whether the starting city is valid or not
                for (City city : cityArrayList) {
                    // Case-insensitive validation is needed
                    if (city.cityName.equalsIgnoreCase(startingCity)) {
                        startingCityIndex = cityArrayList.indexOf(city);
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    // Prompt if input is not valid
                    System.out.println(
                        "City named '" +
                        startingCity +
                        "' not found. Please enter a valid city name."
                    );
                    System.out.print("Please enter starting city: ");
                }
            }
        }

        // Reset validity for destination city input
        isValid = false;

        // Prompt and read the destination city
        System.out.print("Please enter destination city: ");

        while (!isValid) {
            if (inputScanner.hasNextLine()) {
                destinationCity = inputScanner.nextLine();

                // Check whether the destination city is valid or not
                for (City city : cityArrayList) {
                    // Case-insensitive validation is needed
                    if (city.cityName.equalsIgnoreCase(destinationCity)) {
                        isValid = true;
                        destinationCityIndex = cityArrayList.indexOf(city);
                        break;
                    }
                }
                if (!isValid) {
                    // Prompt if input is not valid
                    System.out.println(
                        "City named '" +
                        destinationCity +
                        "' not found. Please enter a valid city name."
                    );
                    System.out.print("Please enter destination city: ");
                }
            }
        }
        // Run Dijkstra's algorithm to find the shortest path efficiently
        dijkstra(
            adjacencyMatrix,
            startingCityIndex,
            destinationCityIndex,
            cityArrayList
        );

        StdDraw.show(); // Show the final results
    }

    // Method to find the vertex with minimum distance value,
    // from the set of vertices not yet included in the shortest path tree
    public static int minDistance(
        double[] distanceArray,
        Boolean[] sptSet,
        int vertexCount
    ) {
        // Initialize min value and index
        double minValue = Double.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < vertexCount; v++) if (
            !sptSet[v] && distanceArray[v] <= minValue
        ) {
            minValue = distanceArray[v];
            min_index = v;
        }

        return min_index;
    }

    // A method which implements Dijkstra's shortest path algorithm for a graph represented as an adjacency matrix.
    // This method calculates the shortest distance from a single source vertex to all other vertices in the graph.
    // It utilizes a distance array to store the shortest distance from the source to each vertex,
    // a set (sptSet) to track vertices included in the shortest path tree (or whose minimum distance from the source is finalized),
    // and a parent array to store the path information, enabling path reconstruction from the source to any destination.
    // The method checks if the destination is reachable and proceeds with path visualization if so.
    public static void dijkstra(
        double[][] adjacencyMatrix,
        int source,
        int destination,
        ArrayList<City> cities
    ) {
        // Count of vertexes, in our context, count of given cities
        int vertexCount = adjacencyMatrix.length;

        // The output array. distanceArray[i] will hold the shortest distance from source to i
        double[] distanceArray = new double[vertexCount];

        // sptSet[i] will true if vertex i is included in the shortest path tree or shortest distance from source to i is finalized
        Boolean[] sptSet = new Boolean[vertexCount];

        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < vertexCount; i++) {
            distanceArray[i] = Double.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        distanceArray[source] = 0;

        // Initialize the parent array to store the predecessor of each vertex in the path to the shortest distance from the source vertex
        int[] parent = new int[vertexCount];
        parent[source] = -1; // Since source is the beginning, it has no predecessor. Mark it with a negative number.

        // Find the shortest path for all vertices
        for (int count = 0; count < vertexCount; count++) {
            // Pick the minimum distance vertex from the set of vertices not yet processed
            int vertexU = minDistance(distanceArray, sptSet, vertexCount);

            // Mark the picked vertex as processed
            sptSet[vertexU] = true;

            // Update distanceArray value of the adjacent vertices of the picked vertex
            for (
                int vertexV = 0;
                vertexV < vertexCount;
                vertexV++
            ) // 1) vertexV is not in the sptSet, // Update distanceArray[vertexV] and parent[vertexV] if and only if:
            // 2) There is an edge from vertexU to vertexV
            // 3) Total length of path from to source to vertexV through vertexU is smaller than current value of distanceArray[vertexV]
            if (
                !sptSet[vertexV] &&
                adjacencyMatrix[vertexU][vertexV] != Double.MAX_VALUE &&
                adjacencyMatrix[vertexU][vertexV] != Double.MAX_VALUE &&
                distanceArray[vertexU] + adjacencyMatrix[vertexU][vertexV] <
                distanceArray[vertexV]
            ) {
                // Update the parent of vertexV
                parent[vertexV] = vertexU;

                // Update the distance of vertexV
                distanceArray[vertexV] =
                    distanceArray[vertexU] + adjacencyMatrix[vertexU][vertexV];
            }
        }

        // Check if the destination is indeed reachable
        if (distanceArray[destination] != Double.MAX_VALUE) {
            // If he destination is reachable; proceed with path visualization and construction
            printSolution(destination, distanceArray, parent, cities);
        } else {
            // The destination is not reachable
            System.out.println("No path could be found.");
        }
    }

    // A utility method to print the total distance and the complete path from source to destination
    public static void printSolution(
        int destination,
        double[] distanceArray,
        int[] parent,
        ArrayList<City> cities
    ) {
        // Printing out total distance and path
        System.out.print(
            "Total Distance: " +
            String.format("%.2f", distanceArray[destination]) +
            ". Path: "
        );
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // Picking a fancy color for path and text
        StdDraw.setPenRadius(0.01); // Thicker line for the path

        // Start printing the path from the source
        printPath(destination, parent, cities);
    }

    // A method to recursively print the shortest path from source to current vertex j
    public static void printPath(int j, int[] parent, ArrayList<City> cities) {
        // If j is the source, print the city name, color the source on the map and return
        if (parent[j] == -1) {
            System.out.print(cities.get(j).cityName + " ");
            StdDraw.filledCircle(cities.get(j).x, cities.get(j).y, 5);
            StdDraw.text(
                cities.get(j).x,
                cities.get(j).y + 15,
                cities.get(j).cityName
            );
            return;
        }

        // Recursively call printPath to print the predecessor of j first
        printPath(parent[j], parent, cities);
        // After the predecessor (and its predecessors recursively) are printed, print the current city
        System.out.print("-> " + cities.get(j).cityName + " ");

        // Visualize the written path
        if (parent[j] != -1) {
            City fromCity = cities.get(parent[j]);
            City toCity = cities.get(j);

            StdDraw.line(fromCity.x, fromCity.y, toCity.x, toCity.y); // Draw the fancy-colored line
            StdDraw.filledCircle(cities.get(j).x, cities.get(j).y, 5); // Color the city on the path
            StdDraw.text(
                cities.get(j).x,
                cities.get(j).y + 15,
                cities.get(j).cityName
            ); // Color the name of the city on the path
        }
    }
}
