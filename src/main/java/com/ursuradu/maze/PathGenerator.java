package com.ursuradu.maze;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class PathGenerator {

  final List<MazePath> paths = new ArrayList<>();
  final Stack<MazeNode> stack = new Stack<>();
  AtomicInteger counter = new AtomicInteger(0);

  public MazePath getPath(final MazeNode root) {

    processNodeAndChildren(root);
    paths.forEach(System.out::println);
    return paths.stream().max(Comparator.comparingInt(o -> o.getNodes().size())).orElseThrow();
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
