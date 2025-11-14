package com.ursuradu.maze.utils;

import java.util.List;
import java.util.Random;

import com.ursuradu.maze.Board;
import com.ursuradu.maze.model.Position;

public class RandomGenerator {

  static Random random = new Random();

  public static String generateId() {
    final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final StringBuilder sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

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
