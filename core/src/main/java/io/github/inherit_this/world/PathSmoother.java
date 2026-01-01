package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Smooths pathfinding waypoints into curves using Catmull-Rom splines.
 * Converts jagged A* paths into organic, flowing corridors.
 */
public class PathSmoother {

    /**
     * Smooth a path using Catmull-Rom spline interpolation.
     *
     * @param rawPath Original waypoints from A*
     * @param smoothing Smoothing factor (0.0-1.0), recommended 0.3-0.5
     * @return Smoothed path with more waypoints for curve following
     */
    public List<Vector2> smoothPath(List<Vector2> rawPath, float smoothing) {
        if (rawPath == null || rawPath.size() < 2) {
            return rawPath;
        }

        // Remove redundant collinear points first
        List<Vector2> simplified = removeCollinearPoints(rawPath);

        if (simplified.size() < 4) {
            // Need at least 4 points for Catmull-Rom, return simplified path
            return simplified;
        }

        List<Vector2> smoothed = new ArrayList<>();

        // Catmull-Rom spline requires 4 points: P0, P1, P2, P3
        // The curve goes through P1 to P2, using P0 and P3 for tangent calculation
        for (int i = 0; i < simplified.size() - 1; i++) {
            Vector2 p0 = (i == 0) ? simplified.get(0) : simplified.get(i - 1);
            Vector2 p1 = simplified.get(i);
            Vector2 p2 = simplified.get(i + 1);
            Vector2 p3 = (i + 2 >= simplified.size()) ? simplified.get(simplified.size() - 1) : simplified.get(i + 2);

            // Sample curve between P1 and P2
            int samples = (int) Math.max(3, p1.dst(p2) * smoothing * 2);  // More samples for longer segments
            for (int j = 0; j < samples; j++) {
                float t = j / (float) samples;
                Vector2 point = catmullRom(p0, p1, p2, p3, t);
                smoothed.add(point);
            }
        }

        // Add final point
        smoothed.add(simplified.get(simplified.size() - 1));

        return smoothed;
    }

    /**
     * Catmull-Rom spline interpolation.
     * Returns point on curve at parameter t (0.0-1.0).
     */
    private Vector2 catmullRom(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        // Catmull-Rom basis matrix coefficients
        float x = 0.5f * (
            (2 * p1.x) +
            (-p0.x + p2.x) * t +
            (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
            (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3
        );

        float y = 0.5f * (
            (2 * p1.y) +
            (-p0.y + p2.y) * t +
            (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
            (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3
        );

        return new Vector2(x, y);
    }

    /**
     * Remove collinear (redundant) points from path.
     * Points that lie on the same line as their neighbors are removed.
     */
    private List<Vector2> removeCollinearPoints(List<Vector2> path) {
        if (path.size() <= 2) {
            return new ArrayList<>(path);
        }

        List<Vector2> simplified = new ArrayList<>();
        simplified.add(path.get(0));  // Always keep first point

        for (int i = 1; i < path.size() - 1; i++) {
            Vector2 prev = path.get(i - 1);
            Vector2 curr = path.get(i);
            Vector2 next = path.get(i + 1);

            if (!areCollinear(prev, curr, next, 0.1f)) {
                simplified.add(curr);
            }
        }

        simplified.add(path.get(path.size() - 1));  // Always keep last point

        return simplified;
    }

    /**
     * Check if three points are approximately collinear.
     */
    private boolean areCollinear(Vector2 p1, Vector2 p2, Vector2 p3, float tolerance) {
        // Calculate cross product magnitude (perpendicular distance from p2 to line p1-p3)
        float dx1 = p2.x - p1.x;
        float dy1 = p2.y - p1.y;
        float dx2 = p3.x - p1.x;
        float dy2 = p3.y - p1.y;

        float crossProduct = Math.abs(dx1 * dy2 - dy1 * dx2);

        return crossProduct < tolerance;
    }
}
