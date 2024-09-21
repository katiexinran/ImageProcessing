import graphs.*;

import java.util.*;

public class GenerativeSeamFinder implements SeamFinder {
    private final ShortestPathSolver.Constructor<Node> sps;

    public GenerativeSeamFinder(ShortestPathSolver.Constructor<Node> sps) {
        this.sps = sps;
    }

    public List<Integer> findSeam(Picture picture, EnergyFunction f) {
        PixelGraph graph = new PixelGraph(picture, f);
        List<Node> seamList = sps.run(graph, graph.source).solution(graph.sink);
        seamList = seamList.subList(1, seamList.size() - 1);
        ArrayList<Integer> allSeams = new ArrayList<>(seamList.size());
        for (Node seam : seamList) {
            allSeams.add(((PixelGraph.Pixel) seam).y);
        }
        return allSeams;
    }

    private class PixelGraph implements Graph<Node> {
        public final Picture picture;
        public final EnergyFunction f;

        public PixelGraph(Picture picture, EnergyFunction f) {
            this.picture = picture;
            this.f = f;
        }

        public List<Edge<Node>> neighbors(Node node) {
            return node.neighbors(picture, f);
        }

        public final Node source = new Node() {
            public List<Edge<Node>> neighbors(Picture picture, EnergyFunction f) {
                List<Edge<Node>> result = new ArrayList<>(picture.height());
                for (int j = 0; j < picture.height(); j += 1) {
                    Pixel to = new Pixel(0, j);
                    result.add(new Edge<>(this, to, f.apply(picture, 0, j)));
                }
                return result;
            }
        };

        public final Node sink = new Node() {
            public List<Edge<Node>> neighbors(Picture picture, EnergyFunction f) {
                return List.of(); // Sink has no neighbors
            }
        };

        public class Pixel implements Node {
            public final int x;
            public final int y;

            public Pixel(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public List<Edge<Node>> neighbors(Picture picture, EnergyFunction f) { 
                List<Edge<Node>> neighbors = new ArrayList<>(3);                
                Pixel from = new Pixel(x, y);
                if (picture.width() == (x + 1)) {
                    neighbors.add(new Edge<>(from, sink, 0));
                } 
                if (picture.width() > (x + 1)) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (j < picture.height() && j >= 0) {
                            Pixel to = new Pixel(x + 1, j);
                            neighbors.add(new Edge<>(from, to, f.apply(picture, x + 1, j)));
                        }
                    }
                }
                return neighbors;
            }

            public String toString() {
                return "(" + x + ", " + y + ")";
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                } else if (!(o instanceof Pixel)) {
                    return false;
                }
                Pixel other = (Pixel) o;
                return this.x == other.x && this.y == other.y;
            }

            public int hashCode() {
                return Objects.hash(x, y);
            }
        }
    }
}
