package com.ursuradu.maze;

import java.util.Set;

public class ClassicShapes {

    /**
     * Default TILE=200, MARGIN(thickness)=40.
     */
    public static String getShapeSvg(Set<DIRECTION> dirs) {
        return getShapeSvg(dirs, 200, 10);
    }

    /**
     * Draw ONLY internal margins (walls) for a cell.
     * Rule: draw a wall on each side that is NOT present in dirs.
     *
     * @param dirs set of open directions for this tile (UP/DOWN/LEFT/RIGHT)
     * @param TILE tile size in px (e.g., 200 for your coordinate system)
     * @param M    wall thickness in px (margin)
     * @return SVG <rect> elements (no outer <g>)
     */
    public static String getShapeSvg(Set<DIRECTION> dirs, int TILE, int M) {
        int m = Math.max(0, Math.min(M, TILE)); // clamp
        StringBuilder sb = new StringBuilder();

        boolean openUp = dirs.contains(DIRECTION.UP);
        boolean openDown = dirs.contains(DIRECTION.DOWN);
        boolean openLeft = dirs.contains(DIRECTION.LEFT);
        boolean openRight = dirs.contains(DIRECTION.RIGHT);

        // For each CLOSED side, draw a full-length wall rectangle.
        if (!openUp) {
            // top wall
            sb.append("<rect x=\"0\" y=\"0\" width=\"").append(TILE)
                    .append("\" height=\"").append(m).append("\" class=\"wall\"/>\n");
        }
        if (!openDown) {
            // bottom wall
            sb.append("<rect x=\"0\" y=\"").append(TILE - m)
                    .append("\" width=\"").append(TILE)
                    .append("\" height=\"").append(m).append("\" class=\"wall\"/>\n");
        }
        if (!openLeft) {
            // left wall
            sb.append("<rect x=\"0\" y=\"0\" width=\"").append(m)
                    .append("\" height=\"").append(TILE).append("\" class=\"wall\"/>\n");
        }
        if (!openRight) {
            // right wall
            sb.append("<rect x=\"").append(TILE - m).append("\" y=\"0\" width=\"")
                    .append(m).append("\" height=\"").append(TILE).append("\" class=\"wall\"/>\n");
        }

        return sb.toString();
    }
}
