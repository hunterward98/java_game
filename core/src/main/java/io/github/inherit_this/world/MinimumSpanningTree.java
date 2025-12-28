package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * Computes the Minimum Spanning Tree (MST) of a graph using Kruskal's algorithm.
 * The MST ensures all nodes are connected with minimal total edge length.
 * Used in procedural dungeon generation to reduce over-connected room graphs.
 */
public class MinimumSpanningTree {

    /**
     * Compute the Minimum Spanning Tree from a list of edges.
     * Uses Kruskal's algorithm with Union-Find for efficient cycle detection.
     *
     * @param edges List of edges to process
     * @return List of edges forming the MST (subset of input edges)
     */
    public List<DelaunayTriangulator.Edge> compute(List<DelaunayTriangulator.Edge> edges) {
        if (edges == null || edges.isEmpty()) {
            return new ArrayList<>();
        }

        // Sort edges by length (ascending)
        List<DelaunayTriangulator.Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingDouble(DelaunayTriangulator.Edge::length));

        // Initialize Union-Find structure with all unique vertices
        UnionFind unionFind = new UnionFind(getAllVertices(edges));

        // Kruskal's algorithm: Add edges in order of length, skipping those that create cycles
        List<DelaunayTriangulator.Edge> mst = new ArrayList<>();
        for (DelaunayTriangulator.Edge edge : sortedEdges) {
            // If vertices are not already connected, add this edge
            if (!unionFind.connected(edge.a, edge.b)) {
                unionFind.union(edge.a, edge.b);
                mst.add(edge);

                // MST is complete when we have (vertices - 1) edges
                if (mst.size() == unionFind.getComponentCount() - 1) {
                    break;
                }
            }
        }

        return mst;
    }

    /**
     * Extract all unique vertices from the edge list.
     *
     * @param edges List of edges
     * @return Set of all unique vertices
     */
    private Set<Vector2> getAllVertices(List<DelaunayTriangulator.Edge> edges) {
        Set<Vector2> vertices = new HashSet<>();
        for (DelaunayTriangulator.Edge edge : edges) {
            vertices.add(edge.a);
            vertices.add(edge.b);
        }
        return vertices;
    }

    /**
     * Union-Find (Disjoint Set Union) data structure.
     * Efficiently tracks connected components and supports union and find operations.
     */
    private static class UnionFind {
        private final Map<Vector2, Vector2> parent;
        private final Map<Vector2, Integer> rank;
        private int componentCount;

        /**
         * Initialize Union-Find with given vertices.
         * Initially, each vertex is its own component.
         *
         * @param vertices Set of vertices
         */
        public UnionFind(Set<Vector2> vertices) {
            this.parent = new HashMap<>();
            this.rank = new HashMap<>();
            this.componentCount = vertices.size();

            // Each vertex starts as its own parent (separate component)
            for (Vector2 vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        /**
         * Find the root (representative) of the component containing vertex.
         * Uses path compression for efficiency.
         *
         * @param vertex Vertex to find
         * @return Root of the component
         */
        public Vector2 find(Vector2 vertex) {
            Vector2 p = parent.get(vertex);
            if (p == null) {
                return vertex;  // Vertex not in structure
            }

            // Path compression: Make all nodes on path point directly to root
            if (!p.equals(vertex)) {
                parent.put(vertex, find(p));
            }

            return parent.get(vertex);
        }

        /**
         * Check if two vertices are in the same connected component.
         *
         * @param a First vertex
         * @param b Second vertex
         * @return true if vertices are connected
         */
        public boolean connected(Vector2 a, Vector2 b) {
            return find(a).equals(find(b));
        }

        /**
         * Union two components containing the given vertices.
         * Uses union by rank to keep tree shallow.
         *
         * @param a First vertex
         * @param b Second vertex
         */
        public void union(Vector2 a, Vector2 b) {
            Vector2 rootA = find(a);
            Vector2 rootB = find(b);

            // Already in same component
            if (rootA.equals(rootB)) {
                return;
            }

            // Union by rank: attach smaller tree under larger tree
            int rankA = rank.getOrDefault(rootA, 0);
            int rankB = rank.getOrDefault(rootB, 0);

            if (rankA < rankB) {
                parent.put(rootA, rootB);
            } else if (rankA > rankB) {
                parent.put(rootB, rootA);
            } else {
                // Ranks equal: choose arbitrarily and increment rank
                parent.put(rootB, rootA);
                rank.put(rootA, rankA + 1);
            }

            componentCount--;
        }

        /**
         * Get the number of separate connected components.
         *
         * @return Number of components
         */
        public int getComponentCount() {
            return componentCount;
        }
    }
}
