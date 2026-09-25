package com.ursuradu.maze;

import static com.ursuradu.maze.enums.PathRequirements.CONTAIN_ALL_PORTALS;
import static com.ursuradu.maze.enums.PathRequirements.DONT_CONTAIN_ALL_PORTALS;
import static com.ursuradu.maze.enums.PathRequirements.PATH_LENGTH_MAX;
import static com.ursuradu.maze.enums.PathRequirements.PATH_LENGTH_MEDIAN;
import static com.ursuradu.maze.enums.PathRequirements.PATH_LENGTH_MIN;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.model.Portal;

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
    final Stream<MazePath> mazePathStream = paths.stream()
        .filter(path -> board.getPortal(path.getNodes().getLast().getPosition()).isEmpty()) // path exit should not be portal
        .filter(getFilter(mazeConfig));

    if (mazeConfig.getPathRequirements().contains(PATH_LENGTH_MAX)) {
      return mazePathStream
          .max(Comparator.comparingInt(o -> o.getNodes().size()));
    } else if (mazeConfig.getPathRequirements().contains(PATH_LENGTH_MIN)) {
      return mazePathStream
          .min(Comparator.comparingInt(o -> o.getNodes().size()));
    } else if (mazeConfig.getPathRequirements().contains(PATH_LENGTH_MEDIAN)) {
      final List<MazePath> list = mazePathStream
          .sorted(Comparator.comparingInt(o -> o.getNodes().size()))
          .toList();
      int maxSize = list.getLast().getNodes().size();
      for (MazePath path : list) {
        if (path.getNodes().size() > maxSize / 2) {
          return Optional.of(path);
        }
      }
    }
    System.out.println("No path size requirement has been selected - going for max");
    return mazePathStream
        .max(Comparator.comparingInt(o -> o.getNodes().size()));
  }

  private Predicate<MazePath> getFilter(final MazeConfig mazeConfig) {
    if (mazeConfig.getPortalsCount() > 0) {
      if (mazeConfig.getPathRequirements().contains(CONTAIN_ALL_PORTALS)) {
        return path -> {
          Set<Portal> containedPortals = getPortalsInPath(path);
          return containedPortals.size() == mazeConfig.getPortalsCount();
        };
      }
      if (mazeConfig.getPathRequirements().contains(DONT_CONTAIN_ALL_PORTALS)) {
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
