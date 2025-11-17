package com.ursuradu.maze.enums;

public enum MazeSize {

  MAZE_SIZE_7_10(7, 10),
  MAZE_SIZE_8_12(8, 12),
  MAZE_SIZE_9_13(9, 13),
  MAZE_SIZE_10_14(10, 14),
  MAZE_SIZE_11_16(11, 16),
  MAZE_SIZE_12_17(12, 17),
  MAZE_SIZE_13_18(13, 18),
  MAZE_SIZE_14_20(14, 20),
  MAZE_SIZE_15_21(15, 21),
  MAZE_SIZE_16_22(16, 22);

  private final int width;
  private final int height;

  MazeSize(final int width, final int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
