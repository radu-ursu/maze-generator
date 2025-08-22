package com.ursuradu.maze;

import static com.ursuradu.maze.PathRequirements.CONTAIN_ALL_PORTALS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class PathGenerator {

  final List<MazePath> paths = new ArrayList<>();
  final Stack<MazeNode> stack = new Stack<>();
  final Board board;
  AtomicInteger counter = new AtomicInteger(0);

  public PathGenerator(final Board board) {
    this.board = board;
  }

  public Optional<MazePath> getPath(final MazeNode root, final List<PathRequirements> pathRequirements, final MazeConfig mazeConfig) {

    processNodeAndChildren(root);
    paths.forEach(
        path -> path.getNodes().forEach(
            node -> {
              final Optional<Portal> portal = board.getPortal(node.getPosition());
              portal.ifPresent(value -> path.getPortals().put(value.getId(), value));
            }
        )
    );

    return paths.stream()
        .filter(getFilter(pathRequirements, mazeConfig))
        .max(Comparator.comparingInt(o -> o.getNodes().size()));
  }

  private Predicate<MazePath> getFilter(final List<PathRequirements> pathRequirements, final MazeConfig mazeConfig) {
    if (pathRequirements.contains(CONTAIN_ALL_PORTALS)) {
      return path -> path.getPortals().size() == mazeConfig.getPortals();
    }
    return path -> true;
  }

  private void processNodeAndChildren(final MazeNode node) {

    stack.push(node);

    if (node.isEdge) {
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
