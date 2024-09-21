package graphs;

import java.util.*;

public class ToposortDAGSolver<V> implements ShortestPathSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;
    private final V start;

    public ToposortDAGSolver(Graph<V> graph, V start) {
        this.edgeTo = new HashMap<>();
        this.distTo = new HashMap<>();
        this.start = start;

        distTo.put(start, 0.0);
        edgeTo.put(start, null);

        List<V> order = dfsPostOrderReversed(graph, start);

        for (V from : order) {
            for (Edge<V> edge : graph.neighbors(from)) {
                V to = edge.to();
                double oldDist = distTo.getOrDefault(to, Double.POSITIVE_INFINITY);
                double newDist = distTo.get(from) + edge.weight();
                if (newDist < oldDist) {
                    distTo.put(to, newDist);
                    edgeTo.put(to, edge);
                }
            }
        }  
    }

    //recursive DFS
    public List<V> dfsPostOrderReversed(Graph<V> graph, V start){
        Set<V> visited = new HashSet<>(); //what type of list??
        List<V> result = new ArrayList<>();
        dfs(graph, start, visited, result);
        Collections.reverse(result);
        return result;  
    }
    
    private void dfs(Graph<V> graph, V from, Set<V> visited, List<V> result){
        visited.add(from);
        for (Edge<V> edge : graph.neighbors(from)) {
            V to = edge.to();
            if (!visited.contains(to)) {
                dfs(graph, to, visited, result);
            }
        }
        result.add(from);
    }

    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from();
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
