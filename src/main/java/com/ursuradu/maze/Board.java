package com.ursuradu.maze;

import static com.ursuradu.maze.Direction.DOWN;
import static com.ursuradu.maze.Direction.LEFT;
import static com.ursuradu.maze.Direction.RIGHT;
import static com.ursuradu.maze.Direction.UP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

@Getter
public class Board {

  private final Map<Position, List<MazeNode>> mazeMap = new HashMap<>();
  private final Set<Position> nonFinalPositions = new HashSet<>();
  private final int width;
  private final int height;
  private final List<Portal> portals = new ArrayList<>();

  public Board(final MazeConfig mazeConfig) {
    this.width = mazeConfig.width();
    this.height = mazeConfig.height();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        mazeMap.put(new Position(x, y), new ArrayList<>());
      }
    }
  }

  public List<Position> getFreeNearbyPositions(final MazeNode mazeNode) {
    final List<Position> result = new ArrayList<>();
    final List<Position> nearbyPositions = getNearbyPositions(mazeNode);
    for (final Position neighbourPosition : nearbyPositions) {
      if (isPositionFree(neighbourPosition)) {
        result.add(neighbourPosition);
      }
    }
    return result;
  }

  public List<Position> getNearbyPositions(final MazeNode node) {
    final List<Position> result = new ArrayList<>();
    getPositionFrom(node.getPosition(), LEFT).ifPresent(result::add);
    getPositionFrom(node.getPosition(), RIGHT).ifPresent(result::add);
    getPositionFrom(node.getPosition(), UP).ifPresent(result::add);
    getPositionFrom(node.getPosition(), DOWN).ifPresent(result::add);
    return result;
  }

  public List<MazeNode> getMazeNodesAtPosition(final Position position) {
    return mazeMap.get(position);
  }

  public boolean isPositionFree(final Position position) {
    return mazeMap.get(position).isEmpty();
  }

  public boolean isOutOfBounds(final Position position) {
    return !mazeMap.containsKey(position);
  }

  public void markAsFinal(final Position position) {
    System.out.println("Marking as final " + position);
    nonFinalPositions.remove(position);
  }

  public Optional<Position> getPositionFrom(final Position position, final Direction direction) {
    final Position newPosition;
    if (direction == DOWN) {
      newPosition = new Position(position.x(), position.y() + 1);
    } else if (direction == UP) {
      newPosition = new Position(position.x(), position.y() - 1);
    } else if (direction == LEFT) {
      newPosition = new Position(position.x() - 1, position.y());
    } else if (direction == RIGHT) {
      newPosition = new Position(position.x() + 1, position.y());
    } else {
      throw new IllegalArgumentException("Invalid direction " + direction);
    }

    return isOutOfBounds(newPosition) ? Optional.empty() : Optional.of(newPosition);
  }

  public boolean isEdge(final Position position) {
    return getPositionFrom(position, DOWN).isEmpty()
        || getPositionFrom(position, UP).isEmpty()
        || getPositionFrom(position, LEFT).isEmpty()
        || getPositionFrom(position, RIGHT).isEmpty();
  }

  public void addNode(final MazeNode mazeNode) {
    getMazeNodesAtPosition(mazeNode.getPosition()).add(mazeNode);
  }

  public Optional<Portal> getPortal(final Position position) {
    for (final Portal portal : portals) {
      if (portal.contains(position)) {
        return Optional.of(portal);
      }
    }
    return Optional.empty();
  }

  public boolean isPortal(final Position position) {
    return getPortal(position).isPresent();
  }

  public boolean isNear(final Position position1, final Position position2) {
    return getPositionFrom(position1, DOWN).orElse(position1).equals(position2)
        || getPositionFrom(position1, UP).orElse(position1).equals(position2)
        || getPositionFrom(position1, LEFT).orElse(position1).equals(position2)
        || getPositionFrom(position1, RIGHT).orElse(position1).equals(position2);
  }
}
