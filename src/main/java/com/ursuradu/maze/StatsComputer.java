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
  final Board board;
  AtomicInteger junctionCounter = new AtomicInteger(0);

  public StatsComputer(final Board board) {
    this.board = board;
  }

  public void computePaths(final MazeNode root) {
    processNodeAndChildren(root);
  }

  // TODO continue from here. Attendable paths are paths that stop at a junction where there are < x possible ways to go next
  public List<MazePath> getAttendablePaths() {
    final List<MazePath> attendableNodes = new ArrayList<>();
    for (final MazePath path : paths) {
      attendableNodes.addAll(path.getNodes());
    }
    return attendableNodes.stream().distinct().toList();
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
