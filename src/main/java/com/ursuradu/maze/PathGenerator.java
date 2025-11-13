package com.ursuradu.maze;

import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.PathRequirements;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.model.Portal;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

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

    public Optional<MazePath> getPath(final MazeNode root, final MazeConfig mazeConfig) {

        processNodeAndChildren(root);
        paths.forEach(
                path -> path.getNodes().forEach(
                        node -> {
                            final Optional<Portal> portal = board.getPortal(node.getPosition());
                            portal.ifPresent(value -> path.getPortals().put(value.getId(), value));
                        })
        );

        return paths.stream()
                .filter(path -> board.getPortal(path.getNodes().getLast().getPosition()).isEmpty()) // path exit should not be portal
                .filter(getFilter(mazeConfig.getPathRequirements(), mazeConfig))
                .max(Comparator.comparingInt(o -> o.getNodes().size()));
    }

    private Predicate<MazePath> getFilter(final List<PathRequirements> pathRequirements, final MazeConfig mazeConfig) {
        if (mazeConfig.getPortalsCount() > 0) {
            if (pathRequirements.contains(CONTAIN_ALL_PORTALS)) {
                return path -> path.getPortals().size() == mazeConfig.getPortalsCount();
            }
            if (pathRequirements.contains(DONT_CONTAIN_ALL_PORTALS)) {
                return path -> path.getPortals().size() < mazeConfig.getPortalsCount();
            }
        }
        return path -> true;
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
