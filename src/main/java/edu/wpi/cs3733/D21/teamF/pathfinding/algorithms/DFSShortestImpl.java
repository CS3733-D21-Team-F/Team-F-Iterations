package edu.wpi.cs3733.D21.teamF.pathfinding.algorithms;

import edu.wpi.cs3733.D21.teamF.pathfinding.*;

public class DFSShortestImpl implements IPathfindingAlgorithm {


    /**
     * Performs Depth-First Search on adjacency matrix
     * @param a vertex to start from
     * @return ArrayList of resultant path
     */
    @Override
    public Path getPath(Graph graph, Vertex a, Vertex b) {
        if (graph.contains(a) && graph.contains(b)) { //FIXME: NULL CHECK
            return DFS_Recur(a, b, new Path());
        }
        return null;
    }


    //FIXME: COMMENT
    private Path DFS_Recur(Vertex curr, Vertex end, Path path)
    {
        if(curr == null) //FIXME: DO MORE NULL CHECKS, END SHOULDN'T BE ABLE TO BE NULL ANYWAYS
            return null;

        path.addVertexToPath(curr, 0.0);
        //path.add(curr);

        if(curr == end)
            return path;

        Path acceptedPath = null;

        for(Edge e : curr.getEdges())
        {
            final Vertex n = curr.getNeighbor(e);

            if(!path.contains(n))
            {
                //FIXME: Use more as a stack?
                //final Path duplicated = path.clone();
                //duplicated.addVertexToPath(n, e.getWeight());
                path.addVertexToPath(n, e.getWeight());

                final Path currPath = DFS_Recur(n, end, path); //duplicated);

                //FIXME: DO BETTER HEURISTIC CHECK ON PATH SIZES TO ENSURE WE DON'T TEST ALL. MAKE PATH CLASS
                if(currPath != null) {
                    if (acceptedPath == null)
                        acceptedPath = currPath.clone();
                    else if (currPath.getPathCost() < acceptedPath.getPathCost())//currPath.size() < acceptedPath.size())
                    {
                        acceptedPath = currPath.clone();
                    }
                }
                path.removeVertexFromPath(n, e.getWeight());
            }
        }

        return acceptedPath;
    }
}
