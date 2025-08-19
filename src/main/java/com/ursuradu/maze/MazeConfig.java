package com.ursuradu.maze;

public record MazeConfig(
    int width,
    int height,
    boolean showPath,
    MazeDrawType drawType,
    boolean hasBridges,
    int portals
) {

  public void validate() {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Incorrect width or height");
    }
    if (drawType != MazeDrawType.THICK && hasBridges) {
      throw new IllegalArgumentException("Can't draw maze with these props");
    }
  }
}
