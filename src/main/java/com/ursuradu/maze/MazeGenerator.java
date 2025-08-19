package com.ursuradu.maze;

import static com.ursuradu.maze.Direction.DOWN;
import static com.ursuradu.maze.Direction.LEFT;
import static com.ursuradu.maze.Direction.RIGHT;
import static com.ursuradu.maze.Direction.UP;
import static com.ursuradu.maze.MazeNodeOrientation.HORIZONTAL;
import static com.ursuradu.maze.MazeNodeOrientation.VERTICAL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MazeGenerator {

  private final Board board;
  private final MazeConfig mazeConfig;
  private MazeNode lastAddedNode;
  private MazeNode currentBridgeNode;
  private Direction currentBridgeDirection;
  private MazeNode currentPortalNode;

  public MazeGenerator(final Board board, final MazeConfig mazeConfig) {
    this.board = board;
    this.mazeConfig = mazeConfig;
  }

  public static Set<Direction> getDirectionsToLinks(final MazeNode node) {
    final Set<Direction> directions = new HashSet<>();
    node.getChildren().forEach(child -> directions.add(getMovementDirection(node.getPosition(), child.getPosition())));
    if (node.getParent() != null) {
      directions.add(getMovementDirection(node.getPosition(), node.getParent().getPosition()));
    }
    return directions;
  }

  public static Direction getMovementDirection(final Position position, final Position neighbourPosition) {
    if (position.x() < neighbourPosition.x()) {
      return RIGHT;
    }
    if (position.x() > neighbourPosition.x()) {
      return LEFT;
    }
    if (position.y() < neighbourPosition.y()) {
      return DOWN;
    }
    if (position.y() > neighbourPosition.y()) {
      return UP;
    }
    throw new IllegalArgumentException("Invalid movement direction");
  }

  private Portal getNewPortal() {
    Position randomPosition1;
    Position randomPosition2;
    do {
      randomPosition1 = RandomGenerator.getRandomPosition(board);
      randomPosition2 = RandomGenerator.getRandomPosition(board);
      if (!board.isPortal(randomPosition1)
          && !board.isPortal(randomPosition2)
          && !randomPosition1.equals(randomPosition2)
      ) {
        return new Portal(randomPosition1, randomPosition2);
      }
    } while (true);
  }

  public MazeNode generateMaze() {

    for (int x = 0; x < mazeConfig.portals(); x++) {
      board.getPortals().add(getNewPortal());
      //debug
      System.out.println(board.getPortals().getFirst());
    }

    final Position startPosition = RandomGenerator.getRandomEdgePosition(board);
    System.out.println("Root: " + startPosition);
    final MazeNode root = new MazeNode(startPosition, true);
    addNodeToMaze(root, null);

    while (!board.getNonFinalPositions().isEmpty()) {
      addNextNode();
    }

    return root;
  }

  private MazeNode getNodeToStartFrom() {
    if (currentBridgeNode != null) {
      return currentBridgeNode;
    }
    if (currentPortalNode != null) {
      return currentPortalNode;
    }
    if (board.isPortal(lastAddedNode.getPosition())) {
      //debug
      System.out.println("Lastaddednode is portal");
    }
    if (board.getNonFinalPositions().contains(lastAddedNode.getPosition())) {
      System.out.println("Looking at lastAddedNode " + lastAddedNode.getPosition());
      return lastAddedNode;
    } else {
      final Position randomNonFinalPosition = RandomGenerator.getRandomPositionFrom(board.getNonFinalPositions().stream().toList());
      System.out.println("Looking at nonFinalPosition " + randomNonFinalPosition);
      // by convention bridges are final so we will not get them here
      return board.getMazeMap().get(randomNonFinalPosition).getFirst();
    }
  }

  private Optional<Position> getNextFreePosition(final MazeNode fromNode) {

    // if currently in bridge, we must go in the same direction
    if (currentBridgeDirection != null) {
      final Optional<Position> nextPosition = board.getPositionFrom(currentBridgeNode.getPosition(), currentBridgeDirection);
      if (nextPosition.isEmpty()) {
        throw new RuntimeException("Something bad happened, this should have been checked");
      } else {
        return nextPosition;
      }
    } else {
      final List<Position> freePositions = getFreePositions(fromNode);
      if (freePositions.isEmpty()) {
        return Optional.empty();
      }
      return Optional.ofNullable(RandomGenerator.getRandomPositionFrom(freePositions));
    }
  }

  private List<Position> getFreePositions(final MazeNode fromNode) {
    // portal present on this node
    System.out.println("Getting free positions");
    final Position fromPosition = fromNode.getPosition();
    if (board.getPortal(fromPosition).isPresent()) {
      final Portal portal = board.getPortal(fromPosition).get();
      System.out.println("Found portal " + fromPosition);
      // exiting a portal
      if (portal.getEnter() != null) {
        System.out.println("On portal exit: " + fromPosition);
        return board.getFreeNearbyPositions(fromNode);
      } else {
        portal.setEnter(fromPosition);
        return Stream.of(
                portal.getOtherPosition(fromPosition))
            .collect(Collectors.toCollection(ArrayList::new));
      }
    }
    if (mazeConfig.hasBridges()) {
      final List<Position> result = new ArrayList<>();
      final List<Position> nearbyPositions = board.getNearbyPositions(fromNode);
      for (final Position nearbyPosition : nearbyPositions) {
        if (board.isPositionFree(nearbyPosition) || canMakeBridge(fromNode, nearbyPosition)) {
          result.add(nearbyPosition);
        }
      }
      return result;
    } else {
      return board.getFreeNearbyPositions(fromNode);
    }
  }

  private boolean canMakeBridge(final MazeNode fromNode, final Position neighbourPosition) {
    final Direction movementDirection = getMovementDirection(fromNode.getPosition(), neighbourPosition);
    Position currentPosition = fromNode.getPosition();
    while (true) {
      final Optional<Position> nextPosition = board.getPositionFrom(currentPosition, movementDirection);
      if (nextPosition.isPresent()) {
        // next position is on board
        final List<MazeNode> mazeNodesAtPosition = board.getMazeNodesAtPosition(nextPosition.get());
        if (mazeNodesAtPosition.isEmpty()) {
          System.out.println("Can make Bridge at " + currentPosition);
          return true;
        } else {
          if (mazeNodesAtPosition.size() != 1) {
            return false;
          }
          if (board.isPortal(nextPosition.get())) {
            // is portal AND there is path there
            return false;
          }
          final MazeNodeOrientation orientation = getOrientation(mazeNodesAtPosition.getFirst());
          if (orientation.equals(HORIZONTAL) && (movementDirection.equals(UP) || movementDirection.equals(DOWN))) {
            currentPosition = nextPosition.get();
          } else if (orientation.equals(VERTICAL) && (movementDirection.equals(LEFT) || movementDirection.equals(RIGHT))) {
            currentPosition = nextPosition.get();
          } else {
            return false;
          }
        }
      } else {
        return false;
      }
    }
  }

  private MazeNodeOrientation getOrientation(final MazeNode mazeNode) {
    final Set<Direction> directionsToLinks = getDirectionsToLinks(mazeNode);
    if (directionsToLinks.equals(Set.of(Direction.UP, Direction.DOWN))) {
      return MazeNodeOrientation.VERTICAL;
    } else if (directionsToLinks.equals(Set.of(LEFT, Direction.RIGHT))) {
      return HORIZONTAL;
    } else {
      return MazeNodeOrientation.MULTIPLE;
    }
  }

  private void addNodeToMaze(final MazeNode mazeNode, final MazeNode parent) {
    board.addNode(mazeNode);
    if (parent != null) {
      parent.getChildren().add(mazeNode);
      mazeNode.setParent(parent);
    }
    lastAddedNode = mazeNode;
    board.getNonFinalPositions().add(mazeNode.getPosition());
  }

  private void addNextNode() {
    final MazeNode nodeToStartFrom = getNodeToStartFrom();
    if (board.isPortal(nodeToStartFrom.getPosition())) {
      //debug
      System.out.println("Nodetostartfrom is portal");
    }
    System.out.println("Starting from node: " + nodeToStartFrom.getPosition());
    final Optional<Position> nextFreePosition = getNextFreePosition(nodeToStartFrom);

    if (nextFreePosition.isPresent()) {
      final Position nextPosition = nextFreePosition.get();
      final boolean isBridge = !board.isPositionFree(nextPosition);
      if (isBridge) {
        System.out.println("Starting on continuing bridge from " + nodeToStartFrom.position + " on " + nextPosition);
        final MazeNode newMazeNode = new MazeNode(nodeToStartFrom, nextPosition, board.isEdge(nextPosition));
        addNodeToMaze(newMazeNode, nodeToStartFrom);
        // bridges are final, we'll start from them by convention
        board.markAsFinal(nextPosition);
        currentBridgeNode = newMazeNode;
        currentBridgeDirection = getMovementDirection(nodeToStartFrom.getPosition(), nextPosition);
      } else {
        // ending bridge
        if (currentBridgeNode != null) {
          currentBridgeDirection = null;
          currentBridgeNode = null;
        }
        final MazeNode newMazeNode = new MazeNode(nodeToStartFrom, nextPosition, board.isEdge(nextPosition));
        addNodeToMaze(newMazeNode, nodeToStartFrom);
        if (board.isPortal(nextPosition)) {
          if (currentPortalNode != null) {
            // entering portal
            board.markAsFinal(nextPosition);
            currentPortalNode = newMazeNode;
          } else {
            // exiting portal
            board.markAsFinal(nextPosition);
            currentPortalNode = null;
          }
        }
      }
    } else {
      board.markAsFinal(nodeToStartFrom.getPosition());
    }
  }
}
