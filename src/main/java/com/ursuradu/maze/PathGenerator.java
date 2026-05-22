package com.ursuradu.maze;

import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.PathRequirements;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.model.Portal;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ursuradu.maze.enums.PathRequirements.CONTAIN_ALL_PORTALS;
import static com.ursuradu.maze.enums.PathRequirements.DONT_CONTAIN_ALL_PORTALS;

public class PathGenerator {

    final List<MazePath> paths = new ArrayList<>();
    final Stack<MazeNode> stack = new Stack<>();
    final Board board;
    AtomicInteger counter = new AtomicInteger(0);

    public PathGenerator(final Board board) {
        this.board = board;
    }

    public void generatePaths(final MazeNode root) {
        processNodeAndChildren(root);
    }

    public Optional<MazePath> getSolutionPath(final MazeConfig mazeConfig) {
        return paths.stream()
                .filter(path -> board.getPortal(path.getNodes().getLast().getPosition()).isEmpty()) // path exit should not be portal
                .filter(getFilter(mazeConfig.getPathRequirements(), mazeConfig))
                .max(Comparator.comparingInt(o -> o.getNodes().size()));
    }

    private Predicate<MazePath> getFilter(final List<PathRequirements> pathRequirements, final MazeConfig mazeConfig) {
        if (mazeConfig.getPortalsCount() > 0) {
            if (pathRequirements.contains(CONTAIN_ALL_PORTALS)) {
                return path -> {
                    Set<Portal> containedPortals = getPortalsInPath(path);
                    return containedPortals.size() == mazeConfig.getPortalsCount();
                };
            }
            if (pathRequirements.contains(DONT_CONTAIN_ALL_PORTALS)) {
                return path -> {
                    Set<Portal> containedPortals = getPortalsInPath(path);
                    return containedPortals.size() < mazeConfig.getPortalsCount();
                };
            }
        }
        return path -> true;
        // TODO make this return multiple filters so I can also add a filter that has a minimum number of nodes
    }

    private Set<Portal> getPortalsInPath(MazePath path) {
        return path.getNodes().stream()
                .map(node -> board.getPortal(node.getPosition()))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    private void processNodeAndChildren(final MazeNode node) {

        stack.push(node);

        if (node.isEdge()) {
            paths.add(new MazePath(counter.get(), stack.stream().toList()));
        }
        if (!node.getChildren().isEmpty()) {
            if (node.getChildren().size() > 1) {
                counter.incrementAndGet();
            }
            for (final MazeNode child : node.getChildren()) {
                processNodeAndChildren(child);
            }
            if (node.getChildren().size() > 1) {
                counter.decrementAndGet();
            }
        }
        stack.pop();

    }

}
