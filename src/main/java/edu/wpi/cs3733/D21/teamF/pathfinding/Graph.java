package edu.wpi.cs3733.D21.teamF.pathfinding;

import edu.wpi.cs3733.D21.teamF.pathfinding.algorithms.*;

import java.util.*;

public class Graph {
    private final List<Edge> edges;
    private final HashMap<String, Vertex> vertices;

    private IPathfindingAlgorithm pathfindingAlgorithm;

    /**
     * Creates a new Graph
     * @author Tony Vuolo
     */
    public Graph() {
        this.edges = new LinkedList<>();
        this.vertices = new HashMap<>();

        //Default to A*
        this.pathfindingAlgorithm = new AStarImpl();
    }

    /**
     * Adds an Edge to this Graph
     * @param edge the additive Edge
     * @return true if both endpoint Vertices are already contained in the Graph, else false
     * @author Tony Vuolo
     */
    public boolean addEdge(Edge edge) {
        this.edges.add(edge);
        Vertex[] endpoints = edge.getVertices();
        return this.vertices.get(endpoints[0].getID()) != null && this.vertices.get(endpoints[1].getID()) != null;
    }

    /**
     * Adds a Vertex to this Graph
     * @param vertex the additive Vertex
     * @author Tony Vuolo
     */
    public void addVertex(Vertex vertex) {
        this.vertices.put(vertex.getID(), vertex);
    }

    /**
     * Determines whether this Graph contains a specific Edge
     * @param edge the comparator Edge
     * @return true if the comparator Edge is contained in this Graph, else false
     * @author Tony Vuolo
     */
    public boolean contains(Edge edge) {
        for(Edge query : this.edges) {
            if(query.equals(edge)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to get the list of vertices.
     * @return An array containing all of our vertices.
     * @author Alex Friedman (ahf), Tony Vuolo
     */
    public List<Vertex> getVertices() {
        return new ArrayList<>(vertices.values());
    }

    /**
     * USed to get a vertex from its ID.
     * @param vertexID The ID of the vertex
     * @return The vertex
     * @author Alex Friedman (ahf), Tony Vuolo
     */
    public Vertex getVertex(String vertexID)
    {
        return this.vertices.get(vertexID);
    }

    /**
     * Determines whether this Graph contains a specific Vertex
     * @param vertex the comparator Vertex
     * @return true if the comparator Vertex is contained in this Graph, else false
     * @author Tony Vuolo
     */
    public boolean contains(Vertex vertex) {
        for(String key : this.vertices.keySet()) {
            Vertex query = this.vertices.get(key);
            if(query.equals(vertex)) {
                return true;
            }
        }
        return false;
    }

    private void disableStairs(){
        for(Edge e : this.edges){
            if(e.getVertices()[0].getID().contains("STAI") && e.getVertices()[1].getID().contains("STAI")){
                e.setLargeWeight();
            }
        }
    }

    private void enableStairs(){
        for(Edge e : this.edges){
            if(e.getVertices()[0].getID().contains("STAI") && e.getVertices()[1].getID().contains("STAI")){
                e.setComputedWeight();
            }
        }
    }

    /**
     * Finds the path of least weight between two Vertices
     * @param a the start Vertex
     * @param b the endpoint Vertex
     * @return the List of Vertices spanning the path of least weight from Vertex a to Vertex b
     * @author Tony Vuolo, Alex Friedman (ahf)
     */
    public Path getPath(Vertex a, Vertex b) {
        return pathfindingAlgorithm.getPath(this, a, b);
    }

    public Path getPathNoStair(Vertex a, Vertex b) {
        disableStairs();
        Path path = getPath(a, b);
        enableStairs();
        return path;
    }

    /**
     * Gets the Path of least weight between two vertices
     * @param v the (ordered) List of Vertices to be reached
     * @return the Path of least weight that will travel to every Vertex in the List in order
     */
    public Path getPath(List<Vertex> v) {
        if(v == null || v.size() == 0) {
            return null;
        }
        Path path = new Path();
        ListIterator<Vertex> iterator = v.listIterator();
        Vertex prev = iterator.next(), current;
        path.addVertexToPath(prev, 0);
        while(iterator.hasNext()) {
            current = iterator.next();

            final Path currentSegment = getPath(prev, current);
            if(currentSegment == null)
                return null;

            path.concatenate(currentSegment);
            prev = current;
        }
        return path;
    }

    public Path getPathNoStair(List<Vertex> v) {
        disableStairs();
        Path path = getPath(v);
        enableStairs();
        return path;
    }

    /**
     * Edits a array of Vertices to provide the least possible path weight
     * @param v the array of Vertices
     * @return the Path of least weight that will travel to every Vertex in the List in optimal order
     * @author Tony Vuolo (bdane)
     */
    public List<Vertex> getEfficientOrder(Vertex... v) {
        if(v.length == 0) {
            return new LinkedList<>();
        } else if(v.length <= 8) {
            return TSP(v);
        }
        Path[] paths = new Path[v.length * (v.length - 1) / 2];
        int index = 0;
        for(int i = 0; i < v.length; i++) {
            for(int j = i + 1; j < v.length; j++) {
                paths[index] = (getPath(v[i], v[j]));
                if(paths[index] == null) {
                    return null;
                }
                index++;
            }
        }
        for(int i = 0; i < paths.length; i++) {
            for(int j = 0; j < paths.length - 1 - i; j++) {
                if(paths[j].getPathCost() > paths[j + 1].getPathCost()) {
                    Path proxy = paths[j];
                    paths[j] = paths[j + 1];
                    paths[j + 1] = proxy;
                }
            }
        }
        HashCluster<Vertex> hashCluster = new HashCluster<>();
        for(Vertex vertex : v) {
            hashCluster.add(vertex);
        }
        for(Path path : paths) {
            Vertex start = path.getStart(), end = path.getEnd();
            Vertex oppStart = hashCluster.getOtherEnd(start), oppEnd = hashCluster.getOtherEnd(end);
            if(oppStart != null && oppEnd != null) {
                Vertex[] candidates = {start, oppStart, end, oppEnd};
                int possibleStart = -1, possibleEnd = -1, currentIndex = 0;
                for(Vertex candidate : candidates) {
                    if(candidate.equals(v[0])) {
                        possibleStart = currentIndex;
                    } else if(candidate.equals(v[v.length - 1])) {
                        possibleEnd = currentIndex;
                    }
                    currentIndex++;
                }
                if(possibleStart >= 0 || possibleEnd >= 0) {
                    if(Math.abs((possibleStart % 2) * (possibleEnd % 2)) == 1) {
                        if((hashCluster.getNumberOfChains() == 2) == (possibleStart >= 0 && possibleEnd >= 0)) {
                            hashCluster.join(start, end);
                        }
                    }
                } else {
                    hashCluster.join(start, end);
                }
            }
        }
        List<Vertex> newOrderedPath = new LinkedList<>();
        hashCluster.focus = v[0];
        for (Vertex vertex : hashCluster) {
            newOrderedPath.add(vertex);
        }
        return newOrderedPath;
    }

    /**
     * Gets the Path of least weight between two Vertices
     * @param list the List of Vertices
     * @return the Path of least weight that will travel to every Vertex in the List in optimal order
     */
    public Path getUnorderedPath(List<Vertex> list) {
        return getPath(getEfficientOrder(list.toArray(new Vertex[0])));
    }

    public Path getUnorderedPathNoStair(List<Vertex> list) {
        disableStairs();
        Path path = getUnorderedPath(list);
        enableStairs();
        return path;
    }
    /**
     * Gets the Traveling Salesman solution to the
     * @param v the array of Vertices
     * @return the least cost path that intersects all vertices in the array, preserving the first and last in order
     */
    public List<Vertex> TSP(Vertex... v) {
        if(v == null) {
            return null;
        } else if(v.length <= 2) {
            return new LinkedList<>(Arrays.asList(v));
        }
        HashMap<UnorderedPair, Path> paths = new HashMap<>();
        for(int i = 0; i < v.length; i++) {
            for(int j = i + 1; j < v.length; j++) {
                Path path = getPath(v[i], v[j]);
                if(path == null) {
                    return null;
                }
                paths.put(new UnorderedPair(i, j), path);
            }
        }
        Permutation permutation = new Permutation(v.length - 2);
        int[] currentPermutation = new int[v.length - 2];
        double totalLength = -1;
        do {
            int[] newPermute = permutation.getPermutation();
            double length = paths.get(new UnorderedPair(0, newPermute[0] + 1)).getPathCost()
                    + paths.get(new UnorderedPair(newPermute[newPermute.length - 1] + 1, v.length - 1)).getPathCost();
            for(int i = 1; i < newPermute.length; i++) {
                length += paths.get(new UnorderedPair(newPermute[i - 1] + 1, newPermute[i] + 1)).getPathCost();
            }
            if(totalLength < 0 || totalLength > length) {
                System.arraycopy(newPermute, 0, currentPermutation, 0, currentPermutation.length);
                totalLength = length;
            }

            permutation.makeNextPermutation();
        } while(! permutation.hasCycled());


        List<Vertex> vertices = new LinkedList<>();
        vertices.add(v[0]);
        for(int j : currentPermutation) {
            vertices.add(v[j + 1]);
        }
        vertices.add(v[v.length - 1]);

        return vertices;
    }

    /**
     * Used to change the pathfinding algorithm type.
     * @param algorithmName The name of the algorithm to use (AStar/A*, BFS, DFS);
     * @return true if we successfully changed the pathfinding algorithm. False if the specified algorithm could not be found.
     * @author Alex Friedman (ahf)
     */
    public boolean setPathfindingAlgorithm(String algorithmName)
    {
        switch (algorithmName.toLowerCase())
        {
            case "astar":
            case "a*":
            case "a star":
                this.pathfindingAlgorithm = new AStarImpl();
                return true;
            case "dfs":
            case "depth-first-search":
                this.pathfindingAlgorithm = new DFSImpl();
                return true;
            case "bfs":
            case "breadth-first-search":
                this.pathfindingAlgorithm = new BFSImpl();
                return true;
            case "bestfirst":
            case "best-first-search":
                this.pathfindingAlgorithm = new BestFSImpl();
                return true;
            case "dijkstra":
            case "dijkstra's search":
                this.pathfindingAlgorithm = new DijkstraImpl();
                return true;
        }
        return false;
    }


    /**
     * Determines if a given List contains a specific Vertex
     * @param list the given List
     * @param vertex the comparator Vertex
     * @return true if some value in the List is congruent to the Vertex, else false
     * @author Tony Vuolo
     */
    public boolean doesNotContain(List<Vertex> list, Vertex vertex) {
        for(Vertex query : list) {
            if(query.equals(vertex)) {
                return false;
            }
        }
        return true;
    }
}
