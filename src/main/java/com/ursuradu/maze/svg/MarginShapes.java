package com.ursuradu.maze.svg;

import java.util.Set;

import com.ursuradu.maze.enums.Direction;

public final class MarginShapes implements ShapeProvider {

  /**
   * Draw ONLY the internal margins (walls) for a cell as individual <line> elements. Rule: draw a wall on each side that is NOT in dirs (dirs = open sides).
   *
   * @param dirs open directions (UP/DOWN/LEFT/RIGHT)
   * @param TILE tile size in px (e.g., 200)
   * @param wallThickness wall thickness in px
   */
  public String getShapeSvg(final Set<Direction> dirs, final int TILE, final int wallThickness) {
    final int m = Math.max(0, Math.min(wallThickness, TILE)); // clamp
    final int halfM = m / 2;
    final boolean openUp = dirs.contains(Direction.UP);
    final boolean openDown = dirs.contains(Direction.DOWN);
    final boolean openLeft = dirs.contains(Direction.LEFT);
    final boolean openRight = dirs.contains(Direction.RIGHT);

    final StringBuilder d = new StringBuilder();

    // Per closed side, add a wall line
    if (!openUp) {
      // Top wall: from (-halfM,0) to (TILE+halfM,0)
      d.append("<line x1=\"").append(-halfM).append("\" y1=\"0\" x2=\"").append(TILE + halfM).append("\" y2=\"0\" stroke=\"black\" stroke-width=\"").append(m)
          .append("\" class=\"wall\" />\n");
    }
    if (!openDown) {
      // Bottom wall: from (-halfM,TILE) to (TILE+halfM,TILE)
      d.append("<line x1=\"").append(-halfM).append("\" y1=\"").append(TILE).append("\" x2=\"").append(TILE + halfM).append("\" y2=\"").append(TILE)
          .append("\" stroke=\"black\" stroke-width=\"").append(m).append("\" class=\"wall\" />\n");
    }
    if (!openLeft) {
      // Left wall: from (0,-halfM) to (0,TILE+halfM)
      d.append("<line x1=\"0\" y1=\"").append(-halfM).append("\" x2=\"0\" y2=\"").append(TILE + halfM).append("\" stroke=\"black\" stroke-width=\"").append(m)
          .append("\" class=\"wall\" />\n");
    }
    if (!openRight) {
      // Right wall: from (TILE,-halfM) to (TILE,TILE+halfM)
      d.append("<line x1=\"").append(TILE).append("\" y1=\"").append(-halfM).append("\" x2=\"").append(TILE).append("\" y2=\"").append(TILE + halfM)
          .append("\" stroke=\"black\" stroke-width=\"").append(m).append("\" class=\"wall\" />\n");
    }

    if (d.isEmpty()) {
      // Cross (+): fully open => no walls
      return "";
    }

    return d.toString();
  }

  /**
   * Default TILE=200, wall thickness M=40.
   */
  public String getShapeSvg(final Set<Direction> dirs) {
    return getShapeSvg(dirs, 200, 14);
  }

  @Override
  public String getBridgeShapeSvg() {
    throw new IllegalArgumentException();
  }
}