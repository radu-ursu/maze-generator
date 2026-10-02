package com.ursuradu.maze.enums;

public enum MazeSize {

  MAZE_SIZE_7_10(7, 10),
  MAZE_SIZE_8_12(8, 12),
  MAZE_SIZE_9_13(9, 13),
  MAZE_SIZE_10_14(10, 14),
  MAZE_SIZE_11_16(11, 16),

  // Sizes for A4 portrait
  MAZE_SIZE_12_17(12, 17),
  MAZE_SIZE_13_19(13, 19),
  MAZE_SIZE_14_21(14, 21),
  MAZE_SIZE_15_23(15, 23),
  MAZE_SIZE_16_24(16, 24),

  // Sizes for A4 split (top and bottom)
  MAZE_SIZE_11_7(11, 7),
  MAZE_SIZE_12_8(12, 8),
  MAZE_SIZE_13_9(13, 9),
  MAZE_SIZE_15_10(15, 10),
  MAZE_SIZE_17_11(17, 11);

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
