package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * Computes Delaunay triangulation of a set of points using the Bowyer-Watson algorithm.
 * The triangulation is optimal for connecting points with minimal edge crossings.
 * Used in procedural dungeon generation to create natural-looking room connections.
 */
public class DelaunayTriangulator {

    /**
     * Compute Delaunay triangulation of the given points.
     *
     * @param points List of points to triangulate
     * @return List of edges forming the triangulation
     */
    public List<Edge> triangulate(List<Vector2> points) {
        if (points == null || points.size() < 3) {
            return new ArrayList<>();
        }

        // Create super-triangle that encompasses all points
        Triangle superTriangle = createSuperTriangle(points);

        // Initialize triangle set with super-triangle
        Set<Triangle> triangles = new HashSet<>();
        triangles.add(superTriangle);

        // Add each point one at a time
        for (Vector2 point : points) {
            // Find triangles whose circumcircle contains this point
            Set<Triangle> badTriangles = new HashSet<>();
            for (Triangle triangle : triangles) {
                if (triangle.circumcircleContains(point)) {
                    badTriangles.add(triangle);
                }
            }

            // Find the boundary of the polygonal hole
            List<Edge> polygon = new ArrayList<>();
            for (Triangle triangle : badTriangles) {
                for (Edge edge : triangle.getEdges()) {
                    // Check if edge is shared by another bad triangle
                    boolean isShared = false;
                    for (Triangle otherTriangle : badTriangles) {
                        if (triangle != otherTriangle && otherTriangle.hasEdge(edge)) {
                            isShared = true;
                            break;
                        }
                    }
                    // Only boundary edges (not shared) are added
                    if (!isShared) {
                        polygon.add(edge);
                    }
                }
            }

            // Remove bad triangles
            triangles.removeAll(badTriangles);

            // Re-triangulate the hole by connecting point to boundary edges
            for (Edge edge : polygon) {
                Triangle newTriangle = new Triangle(edge.a, edge.b, point);
                triangles.add(newTriangle);
            }
        }

        // Remove triangles that share vertices with the super-triangle
        triangles.removeIf(triangle ->
                triangle.sharesVertexWith(superTriangle.a) ||
                triangle.sharesVertexWith(superTriangle.b) ||
                triangle.sharesVertexWith(superTriangle.c)
        );

        // Extract unique edges from final triangulation
        Set<Edge> uniqueEdges = new HashSet<>();
        for (Triangle triangle : triangles) {
            uniqueEdges.addAll(triangle.getEdges());
        }

        return new ArrayList<>(uniqueEdges);
    }

    /**
     * Create a super-triangle that encompasses all points.
     * The super-triangle must be large enough to contain all points.
     *
     * @param points List of points
     * @return Super-triangle
     */
    private Triangle createSuperTriangle(List<Vector2> points) {
        // Find bounding box
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (Vector2 point : points) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }

        float dx = maxX - minX;
        float dy = maxY - minY;
        float deltaMax = Math.max(dx, dy);
        float midX = (minX + maxX) / 2f;
        float midY = (minY + maxY) / 2f;

        // Create super-triangle with vertices far outside bounding box
        Vector2 p1 = new Vector2(midX - 20 * deltaMax, midY - deltaMax);
        Vector2 p2 = new Vector2(midX, midY + 20 * deltaMax);
        Vector2 p3 = new Vector2(midX + 20 * deltaMax, midY - deltaMax);

        return new Triangle(p1, p2, p3);
    }

    /**
     * Represents a triangle with three vertices.
     */
    public static class Triangle {
        public final Vector2 a, b, c;
        private Vector2 circumcenter;
        private float circumradiusSquared;
        private boolean circumcircleCalculated = false;

        public Triangle(Vector2 a, Vector2 b, Vector2 c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        /**
         * Check if the circumcircle of this triangle contains the given point.
         * Uses lazy calculation and caching of circumcircle properties.
         *
         * @param point Point to test
         * @return true if point is inside circumcircle
         */
        public boolean circumcircleContains(Vector2 point) {
            if (!circumcircleCalculated) {
                calculateCircumcircle();
            }

            float distSquared = point.dst2(circumcenter);
            return distSquared <= circumradiusSquared;
        }

        /**
         * Calculate circumcircle center and radius.
         * The circumcircle is the unique circle passing through all three vertices.
         */
        private void calculateCircumcircle() {
            float ax = a.x, ay = a.y;
            float bx = b.x, by = b.y;
            float cx = c.x, cy = c.y;

            float d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));

            if (Math.abs(d) < 1e-6) {
                // Degenerate triangle - use center of bounding box
                circumcenter = new Vector2((ax + bx + cx) / 3f, (ay + by + cy) / 3f);
                circumradiusSquared = Float.MAX_VALUE;
            } else {
                float ux = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d;
                float uy = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d;

                circumcenter = new Vector2(ux, uy);
                circumradiusSquared = circumcenter.dst2(a);
            }

            circumcircleCalculated = true;
        }

        /**
         * Get all three edges of this triangle.
         *
         * @return List of 3 edges
         */
        public List<Edge> getEdges() {
            List<Edge> edges = new ArrayList<>(3);
            edges.add(new Edge(a, b));
            edges.add(new Edge(b, c));
            edges.add(new Edge(c, a));
            return edges;
        }

        /**
         * Check if this triangle has the given edge.
         *
         * @param edge Edge to check
         * @return true if triangle contains this edge
         */
        public boolean hasEdge(Edge edge) {
            return (edge.equals(new Edge(a, b)) ||
                    edge.equals(new Edge(b, c)) ||
                    edge.equals(new Edge(c, a)));
        }

        /**
         * Check if this triangle shares a vertex with the given vertex.
         *
         * @param vertex Vertex to check
         * @return true if triangle contains this vertex
         */
        public boolean sharesVertexWith(Vector2 vertex) {
            return a.equals(vertex) || b.equals(vertex) || c.equals(vertex);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Triangle triangle = (Triangle) o;
            // Triangles are equal if they have the same vertices (order doesn't matter)
            Set<Vector2> thisVertices = new HashSet<>(Arrays.asList(a, b, c));
            Set<Vector2> otherVertices = new HashSet<>(Arrays.asList(triangle.a, triangle.b, triangle.c));
            return thisVertices.equals(otherVertices);
        }

        @Override
        public int hashCode() {
            // Hash code should be independent of vertex order
            return Objects.hash(a) + Objects.hash(b) + Objects.hash(c);
        }
    }

    /**
     * Represents an edge between two vertices.
     * Edges are undirected (a→b is the same as b→a).
     */
    public static class Edge {
        public final Vector2 a, b;

        public Edge(Vector2 a, Vector2 b) {
            this.a = a;
            this.b = b;
        }

        /**
         * Get the length of this edge.
         *
         * @return Euclidean distance between vertices
         */
        public float length() {
            return a.dst(b);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            // Edges are undirected: (a,b) == (b,a)
            return (a.equals(edge.a) && b.equals(edge.b)) ||
                   (a.equals(edge.b) && b.equals(edge.a));
        }

        @Override
        public int hashCode() {
            // Hash code should be independent of direction
            return Objects.hash(a) + Objects.hash(b);
        }

        @Override
        public String toString() {
            return String.format("Edge[%.1f,%.1f - %.1f,%.1f]", a.x, a.y, b.x, b.y);
        }
    }
}
