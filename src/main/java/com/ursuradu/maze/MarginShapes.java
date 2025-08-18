package com.ursuradu.maze;

import java.util.Set;

public final class MarginShapes implements ShapeProvider {

    /**
     * Draw ONLY the internal margins (walls) for a cell as a single <path>.
     * Rule: draw a wall on each side that is NOT in dirs (dirs = open sides).
     *
     * @param dirs open directions (UP/DOWN/LEFT/RIGHT)
     * @param TILE tile size in px (e.g., 200)
     * @param M    wall thickness in px
     */
    public String getShapeSvg(Set<Direction> dirs, int TILE, int M) {
        final int m = Math.max(0, Math.min(M, TILE)); // clamp
        final boolean openUp = dirs.contains(Direction.UP);
        final boolean openDown = dirs.contains(Direction.DOWN);
        final boolean openLeft = dirs.contains(Direction.LEFT);
        final boolean openRight = dirs.contains(Direction.RIGHT);

        StringBuilder d = new StringBuilder();

        // helper to append a rectangle as a subpath
        // (nonzero fill rule unions subpaths, so no seams between them)
        java.util.function.BiConsumer<int[], StringBuilder> rectPath = (r, out) -> {
            int x = r[0], y = r[1], w = r[2], h = r[3];
            out.append("M ").append(x).append(' ').append(y)
                    .append(" h ").append(w)
                    .append(" v ").append(h)
                    .append(" h ").append(-w)
                    .append(" Z ");
        };

        // Per closed side, add a wall rectangle as a subpath
        if (!openUp) rectPath.accept(new int[]{0, 0, TILE, m}, d);          // top
        if (!openDown) rectPath.accept(new int[]{0, TILE - m, TILE, m}, d);          // bottom
        if (!openLeft) rectPath.accept(new int[]{0, 0, m, TILE}, d);       // left
        if (!openRight) rectPath.accept(new int[]{TILE - m, 0, m, TILE}, d);       // right

        if (d.length() == 0) {
            // Cross (+): fully open => no walls
            return "";
        }

        return "<path d=\"" + d + "\" class=\"wall\" fill-rule=\"nonzero\"/>\n";
    }

    /**
     * Default TILE=200, wall thickness M=40.
     */
    public String getShapeSvg(Set<Direction> dirs) {
        return getShapeSvg(dirs, 200, 10);
    }

    @Override
    public String getBridgeShapeSvg() {
        throw new IllegalArgumentException();
    }
}