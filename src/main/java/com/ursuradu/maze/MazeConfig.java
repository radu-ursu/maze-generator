package com.ursuradu.maze;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MazeConfig {

  private int width;
  private int height;
  private MazeDrawType drawType;
  private boolean hasBridges;
  private int portals;
  private boolean makePortalsOnTheFly;
  private int maxPortalRate;

  public void validate() {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Incorrect width or height");
    }
    if (drawType != MazeDrawType.THICK && hasBridges) {
      hasBridges = false;
    }
  }
}
