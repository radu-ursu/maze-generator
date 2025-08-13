package com.ursuradu.maze;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MazeUtils {

    // ---------- 1) Longest path ANYWHERE in the tree (diameter) ----------
    public static PathResult longestPathInTree(MazeNode anyNode) {
        if (anyNode == null) return new PathResult(0, List.of());

        // climb to root so we only traverse down via children (no need to juggle parent links)
        MazeNode root = anyNode;
        while (root.getParent() != null) root = root.getParent();

        Best best = new Best();
        dfsDiameter(root, best);
        return new PathResult(best.lengthEdges, best.path);
    }

    // DFS over children only (rooted tree)
    private static Downward dfsDiameter(MazeNode node, Best best) {
        if (node.getChildren().isEmpty()) {
            return new Downward(0, new ArrayList<>(List.of(node)));
        }

        Downward best1 = new Downward(0, new ArrayList<>(List.of(node))); // longest down
        Downward best2 = new Downward(0, new ArrayList<>(List.of(node))); // second longest

        for (MazeNode child : node.getChildren()) {
            Downward d = dfsDiameter(child, best);

            // prepend current node to child's path
            List<MazeNode> fromHere = new ArrayList<>();
            fromHere.add(node);
            fromHere.addAll(d.path);
            Downward cand = new Downward(d.depth + 1, fromHere);

            if (cand.depth > best1.depth) {
                best2 = best1;
                best1 = cand;
            } else if (cand.depth > best2.depth) {
                best2 = cand;
            }
        }

        // Candidate diameter going through 'node' is best1 + best2
        int through = best1.depth + best2.depth;
        if (through > best.lengthEdges) {
            List<MazeNode> full = new ArrayList<>(best1.path);
            // Append the tail of best2 reversed (excluding the shared 'node')
            List<MazeNode> tail = new ArrayList<>(best2.path);
            Collections.reverse(tail);
            if (!tail.isEmpty()) tail.remove(0); // drop the duplicate 'node'
            full.addAll(tail);
            best.lengthEdges = through;
            best.path = full;
        }

        return best1; // return longest downward path to parent
    }

    // ---------- 2) Longest path starting FROM a given node (downward only) ----------
    public static PathResult longestPathFromNodeDown(MazeNode start) {
        if (start == null) return new PathResult(0, List.of());
        Downward d = dfsDown(start);
        return new PathResult(d.depth, d.path);
    }

    private static Downward dfsDown(MazeNode node) {
        if (node.getChildren().isEmpty()) {
            return new Downward(0, new ArrayList<>(List.of(node)));
        }
        Downward best = new Downward(-1, List.of());
        for (MazeNode child : node.getChildren()) {
            Downward dc = dfsDown(child);
            if (dc.depth > best.depth) {
                List<MazeNode> path = new ArrayList<>();
                path.add(node);
                path.addAll(dc.path);
                best = new Downward(dc.depth + 1, path);
            }
        }
        return best;
    }

    @Getter
    public static class PathResult {
        private final int lengthEdges;            // number of edges on the path
        private final int lengthNodes;            // number of nodes on the path
        private final List<MazeNode> nodes;       // endpoints included, in order
        private final List<Position> positions;   // convenience: positions of the nodes

        PathResult(int edges, List<MazeNode> nodes) {
            this.lengthEdges = edges;
            this.lengthNodes = edges + 1;
            this.nodes = nodes;
            this.positions = nodes.stream().map(MazeNode::getPosition).toList();
        }
    }

    // Tracks global best diameter
    private static class Best {
        int lengthEdges = 0;
        List<MazeNode> path = List.of();
    }

    // Info returned upward: best single downward path from this node
    private static class Downward {
        int depth; // in edges
        List<MazeNode> path; // starts at 'node' and goes down to a leaf

        Downward(int depth, List<MazeNode> path) {
            this.depth = depth;
            this.path = path;
        }
    }
}
