package com.ursuradu.maze;

import java.util.List;
import java.util.Random;

public class RandomGenerator {

  static Random random = new Random();

  public static Position getRandomPosition(final Board board) {
    final int randomX = random.nextInt(board.getWidth());
    final int randomY = random.nextInt(board.getHeight());
    return new Position(randomX, randomY);
  }

  public static Position getRandomEdgePosition(final Board board) {
    Position position;
    do {
      position = getRandomPosition(board);
    }
    while (!board.isEdge(position) || board.isPortal(position));
    return position;
  }

  public static Position getRandomPositionFrom(final List<Position> positions) {

    return positions.get(random.nextInt(positions.size()));
  }

  public static int getRandomInt(final int max) {
    return random.nextInt(1, max);
  }
}
