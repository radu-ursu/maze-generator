package com.ursuradu.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;

public class StatsComputer {

  final List<MazePath> paths = new ArrayList<>();
  final Stack<MazeNode> stack = new Stack<>();
  final Maze maze;
  AtomicInteger junctionCounter = new AtomicInteger(0);

  public StatsComputer(final Maze maze) {
    this.maze = maze;
  }

  public void computeStats() {
    computePaths();
    markAttendableNodes();
  }

  public void computePaths() {
    processNodeAndChildren(maze.getSolutionPath().getNodes().getFirst());
  }

  public void markAttendableNodes() {
    for (final MazePath path : paths) {
      markAttendableNodesForPath(path);
    }
  }

  private void markAttendableNodesForPath(final MazePath path) {
    final List<MazeNode> attendableNodesTillJunction = new ArrayList<>();
    for (final MazeNode node : path.getNodes()) {
      if (node.isJunction()) {
        attendableNodesTillJunction.add(node);
        attendableNodesTillJunction.forEach(n -> n.setStatsWouldHumanReachThisNode(true));
        attendableNodesTillJunction.clear();
      }
      if (node.getPosition().equals(maze.getSolutionPath().getNodes().getLast().getPosition())) {
        attendableNodesTillJunction.add(node);
        attendableNodesTillJunction.forEach(n -> n.setStatsWouldHumanReachThisNode(true));
        attendableNodesTillJunction.clear();
        return;
      } else {
        attendableNodesTillJunction.add(node);
      }
    }
    if (attendableNodesTillJunction.size() > 5) {
      attendableNodesTillJunction.subList(0, attendableNodesTillJunction.size() - 5).forEach(n -> n.setStatsWouldHumanReachThisNode(true));
    }
  }

  private void processNodeAndChildren(final MazeNode node) {

    stack.push(node);

    if (node.isCulDeSac()) {
      paths.add(new MazePath(junctionCounter.get(), stack.stream().toList()));
    } else {
      if (node.getChildren().size() > 1) {
        junctionCounter.incrementAndGet();
      }
      for (final MazeNode child : node.getChildren()) {
        processNodeAndChildren(child);
      }
      if (node.getChildren().size() > 1) {
        junctionCounter.decrementAndGet();
      }
    }
    stack.pop();
  }
}
