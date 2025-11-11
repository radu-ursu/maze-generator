package com.ursuradu.maze;

import static com.ursuradu.maze.enums.Direction.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.Direction;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.Portal;
import com.ursuradu.maze.model.Position;
import com.ursuradu.maze.utils.RandomGenerator;
import lombok.Getter;

@Getter
public class Board {

  private final Map<Position, List<MazeNode>> mazeMap = new HashMap<>();
  private final Set<Position> nonFinalPositions = new HashSet<>();
  private final int width;
  private final int height;
  private final List<Portal> portals = new ArrayList<>();
  private int onTheFlyPortalsLeft;
  private int nextPortalId = 1;

  public Board(final MazeConfig mazeConfig) {
    this.width = mazeConfig.getWidth();
    this.height = mazeConfig.getHeight();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        mazeMap.put(new Position(x, y), new ArrayList<>());
      }
    }
    this.onTheFlyPortalsLeft = mazeConfig.getOnTheFlyPortals().isActive() ? mazeConfig.getPortalsCount() : 0;
  }

  public Portal addOnTheFlyPortal(final Position fromPosition) {
    final Portal newPortal = getNewPortal(fromPosition);
    portals.add(newPortal);
    onTheFlyPortalsLeft--;
    return newPortal;
  }

  public List<Position> getFreeNearbyPositions(final Position position) {
    final List<Position> result = new ArrayList<>();
    final List<Position> nearbyPositions = getNearbyPositions(position);
    for (final Position neighbourPosition : nearbyPositions) {
      if (isPositionFree(neighbourPosition)) {
        result.add(neighbourPosition);
      }
    }
    return result;
  }

  public List<Position> getNearbyPositions(final Position position) {
    final List<Position> result = new ArrayList<>();
    getPositionFrom(position, LEFT).ifPresent(result::add);
    getPositionFrom(position, RIGHT).ifPresent(result::add);
    getPositionFrom(position, UP).ifPresent(result::add);
    getPositionFrom(position, DOWN).ifPresent(result::add);
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

  public Portal getNewPortal() {
    return getNewPortal(null);
  }

  public Portal getNewPortal(final Position position1) {
    Position randomPosition1;
    Position randomPosition2;
    do {
      randomPosition1 = position1 == null ? RandomGenerator.getRandomPosition(this) : position1;
      randomPosition2 = RandomGenerator.getRandomPosition(this);
      if (!isPortal(randomPosition1)
          && !isPortal(randomPosition2)
          && !randomPosition1.equals(randomPosition2)
          && !isNear(randomPosition1, randomPosition2)
          && mazeMap.get(randomPosition1).isEmpty()
          && mazeMap.get(randomPosition2).isEmpty()
      ) {
        final Portal portal = new Portal(randomPosition1, randomPosition2);
        final int id = nextPortalId++;
        portal.setId(id);
        return portal;
      }
    } while (true);
  }
}
