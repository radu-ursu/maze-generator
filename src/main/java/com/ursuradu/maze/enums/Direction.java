package com.ursuradu.maze.enums;

public enum Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT;

  public Direction getOpposite() {
    if (this == UP) {
      return DOWN;
    } else if (this == DOWN) {
      return UP;
    } else if (this == LEFT) {
      return RIGHT;
    } else if (this == RIGHT) {
      return LEFT;
    }
    return null;
  }
}
