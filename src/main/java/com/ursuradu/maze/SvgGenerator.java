package com.ursuradu.maze;

import java.util.HashSet;
import java.util.Set;

import static com.ursuradu.maze.Direction.*;

public class SvgGenerator {

    private int scale = 200; // space between nodes

    private Set<Position> pathPositions = new HashSet<>();
    private MazeEdgePaths.PathResult pathResult;
    private Maze maze;
    private ShapeProvider shapeProvider;


    public String generateSVG(Maze maze, MazeEdgePaths.PathResult pathResult) {
        pathPositions = new HashSet<>();
        this.pathResult = pathResult;
//        this.pathResult.getNodes().forEach(node -> {
//            pathPositions.add(node.getPosition());
//        });
        this.maze = maze;
        this.shapeProvider = getShapeProvider(maze.getMazeConfig());
        StringBuilder svg = new StringBuilder();
        int viewBoxWidth = maze.getWidth() * scale;
        int viewBoxHeight = maze.getHeight() * scale;
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 ");
        svg.append(viewBoxWidth).append(" ").append(viewBoxHeight).append("'>\n");
        svg.append("""
                                <style>
                                            .tile-bg {
                                              fill: #FFFFFF;
                                              stroke-width: 0;
                                               stroke: #0f0;
                
                                            }
                                            .path-outer {
                                              stroke: #555;
                                              stroke-width: 60;
                                              fill: none;
                                              shape-rendering: crispEdges;
                
                                            }
                                            .path-inner {
                                              stroke: #fff;
                                              stroke-width: 40;
                                              fill: none;
                                              shape-rendering: crispEdges;
                                            }
                                            .correct-path {
                                              stroke: #e00;
                                              stroke-width: 1;
                                              fill: #e00;
                                            }
                                            .wall {
                                              fill: #555;
                //                            shape-rendering: crispEdges;
                                            }
                                </style>
                
                """);
        svg.append("""
                  <rect x="0" y="0" width="%d" height="%d" fill="#fff"/>
                """.formatted(viewBoxWidth, viewBoxHeight));

        traverseAndDraw(maze.getRoot(), svg);

        svg.append("</svg>");
        return svg.toString();
    }

    private ShapeProvider getShapeProvider(MazeConfig mazeConfig) {
        if (mazeConfig.drawType().equals(MazeDrawType.THICK)) {
            return new ThickShapes();
        }
        if (mazeConfig.drawType().equals(MazeDrawType.CLASSIC)) {
            return new MarginShapes();
        }
        throw new IllegalArgumentException("Don't know what shapeProvider to provide");
    }

    void traverseAndDraw(MazeNode node, StringBuilder svg) {
        // Draw node
        String svgForNode = getShape(node);
        svg.append(svgForNode);
        // Draw links to children
        for (MazeNode child : node.getChildren()) {
            traverseAndDraw(child, svg);
        }
    }

    private String getShape(MazeNode node) {

        int x = node.getPosition().x() * scale;
        int y = node.getPosition().y() * scale;
        StringBuilder shapeSvg = new StringBuilder("<g transform=\"translate(" + x + "," + y + ")\">\n");

        if (node instanceof BridgeMazeNode) {
            shapeSvg.append(shapeProvider.getBridgeShapeSvg());
        } else {
            Set<Direction> directions = maze.getDirectionsToLinks(node);
            addDirectionForStartAndEndOfPath(node, directions);
            shapeSvg.append(shapeProvider.getShapeSvg(directions));
        }
        if (pathPositions.contains(node.getPosition())) {
            shapeSvg.append("""
                    <rect x="95" y="95" width="10" height="10" class="correct-path"/>
                    """);
        }
        shapeSvg.append("</g>\n");
        return shapeSvg.toString();
    }

    private void addDirectionForStartAndEndOfPath(MazeNode node, Set<Direction> directions) {
        if (isFirstNodeOfThePath(node) || isLastNodeOfThePath(node)) {
            if (node.getPosition().x() == 0) {
                directions.add(LEFT);
                return;
            }
            if (node.getPosition().x() == maze.getWidth() - 1) {
                directions.add(RIGHT);
                return;
            }
            if (node.getPosition().y() == 0) {
                directions.add(UP);
                return;
            }
            if (node.getPosition().y() == maze.getHeight() - 1) {
                directions.add(DOWN);
            }
        }
    }

    private boolean isFirstNodeOfThePath(MazeNode node) {
        return pathResult.getNodes().getFirst().getPosition().equals(node.getPosition());
    }

    private boolean isLastNodeOfThePath(MazeNode node) {
        return pathResult.getNodes().getLast().getPosition().equals(node.getPosition());
    }
}
