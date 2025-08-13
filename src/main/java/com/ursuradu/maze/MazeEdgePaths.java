package com.ursuradu.maze;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MazeEdgePaths {

    /**
     * Public API: longest path whose endpoints are both on the maze edge.
     */
    public static PathResult longestEdgeToEdgePath(MazeNode anyNode, Maze maze) {
        if (anyNode == null) return new PathResult(0, List.of());

        // climb to root, so we only traverse via children (no need to juggle parent links)
        MazeNode root = anyNode;
        while (root.getParent() != null) root = root.getParent();

        Best best = new Best();
        dfsConstrained(root, best, maze);

        // If we didn’t find a path with two edge endpoints, best.path may have < 2 nodes.
        // Return as-is; caller can check lengthNodes >= 2 to know a valid edge-to-edge path exists.
        return new PathResult(best.lengthEdges, best.path);
    }

    private static Downward dfsConstrained(MazeNode node, Best best, Maze maze) {
        // Collect all candidate downward paths that END at an edge node in this subtree.
        // Include zero-length path if THIS node is an edge.
        List<Downward> candidates = new ArrayList<>();

        if (isEdge(node, maze)) {
            candidates.add(new Downward(0, new ArrayList<>(List.of(node))));
        }

        for (MazeNode child : node.getChildren()) {
            Downward childBest = dfsConstrained(child, best, maze);
            if (childBest.valid) {
                // prepend current node
                List<MazeNode> fromHere = new ArrayList<>();
                fromHere.add(node);
                fromHere.addAll(childBest.path);
                candidates.add(new Downward(childBest.depth + 1, fromHere));
            }
        }

        // Find the top two longest valid downward-to-edge paths
        candidates.sort((a, b) -> Integer.compare(b.depth, a.depth));
        Downward best1 = candidates.size() > 0 ? candidates.get(0) : new Downward();
        Downward best2 = candidates.size() > 1 ? candidates.get(1) : new Downward();

        // If we have at least two valid candidates, we can form an edge-to-edge path through 'node'
        if (best1.valid && best2.valid) {
            List<MazeNode> full = mergeThroughCenter(best1.path, best2.path);
            int edges = best1.depth + best2.depth; // edges between the two edge endpoints
            if (edges > best.lengthEdges) {
                best.lengthEdges = edges;
                best.path = full;                  // Now ordered strictly from edge to edge
            }
        }


        // Return to parent: the single best downward-to-edge path from here (or invalid if none)
        return best1.valid ? best1 : new Downward();
    }

    // ---------- Implementation details ----------

    /**
     * Decide if a node lies on the maze's outer edge.
     * Implement using node.getPosition() and your maze bounds.
     * <p>
     * Example (if Position has zero-based x,y and you know width/height):
     * return p.x == 0 || p.y == 0 || p.x == width - 1 || p.y == height - 1;
     */
    private static boolean isEdge(MazeNode node, Maze maze) {
        Position p = node.getPosition();
        return p.x() == 0 || p.y() == 0 || p.x() == (maze.getWidth() - 1) || p.y() == (maze.getHeight() - 1);
    }

    // Utility: merge two downward paths that both start at the same center node
    private static List<MazeNode> mergeThroughCenter(List<MazeNode> a, List<MazeNode> b) {
        // a: [center, ..., edgeA]
        // b: [center, ..., edgeB]
        List<MazeNode> left = new ArrayList<>(a);
        Collections.reverse(left);                 // [edgeA, ..., center]
        left.addAll(b.subList(1, b.size()));       // + [ ..., edgeB] (skip duplicate center)
        return left;                               // [edgeA, ..., center, ..., edgeB]
    }

    @Getter
    public static class PathResult {
        private final int lengthEdges;
        private final int lengthNodes;
        private final List<MazeNode> nodes;
        private final List<Position> positions;

        PathResult(int edges, List<MazeNode> nodes) {
            this.lengthEdges = Math.max(0, edges);
            this.lengthNodes = this.lengthEdges + 1;
            this.nodes = nodes;
            this.positions = nodes.stream().map(MazeNode::getPosition).toList();
        }
    }

    // ---------- You implement this ----------

    /**
     * Global best constrained diameter (endpoints must be edge nodes).
     */
    private static class Best {
        int lengthEdges = -1;            // -1 means "not found yet"
        List<MazeNode> path = List.of(); // empty if no valid pair found
    }

    /**
     * A downward path from 'node' to some edge node in its subtree (inclusive).
     */
    private static class Downward {
        int depth;                 // edges count from current node to the edge endpoint
        List<MazeNode> path;       // nodes from current node (index 0) down to the edge endpoint
        boolean valid;             // whether such a path exists

        Downward() {
            this.depth = -1;
            this.path = List.of();
            this.valid = false;
        }

        Downward(int depth, List<MazeNode> path) {
            this.depth = depth;
            this.path = path;
            this.valid = true;
        }
    }

}
