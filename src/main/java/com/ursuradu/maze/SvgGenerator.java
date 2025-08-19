package com.ursuradu.maze;

import static com.ursuradu.maze.Direction.DOWN;
import static com.ursuradu.maze.Direction.LEFT;
import static com.ursuradu.maze.Direction.RIGHT;
import static com.ursuradu.maze.Direction.UP;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SvgGenerator {

  private static final int SCALE = 200; // space between nodes
  private final MazeConfig mazeConfig;
  private final Board board;
  private Set<Position> pathPositions = new HashSet<>();
  private MazePath path;
  private ShapeProvider shapeProvider;

  public SvgGenerator(final MazeConfig mazeConfig, final Board board) {
    this.mazeConfig = mazeConfig;
    this.board = board;
  }

  public String generateSVG(final MazePath path) {
    pathPositions = new HashSet<>();
    this.path = path;
    this.path.getNodes().forEach(node -> pathPositions.add(node.getPosition()));
    this.shapeProvider = getShapeProvider(mazeConfig);
    final StringBuilder svg = new StringBuilder();
    final int viewBoxWidth = board.getWidth() * SCALE;
    final int viewBoxHeight = board.getHeight() * SCALE;
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

//    traverseAndDraw(maze.getRoot(), svg);
    for (final Position position : board.getMazeMap().keySet()) {
      drawPosition(position, svg);
    }

    svg.append("</svg>");
    return svg.toString();
  }

  private ShapeProvider getShapeProvider(final MazeConfig mazeConfig) {
    if (mazeConfig.drawType().equals(MazeDrawType.THICK)) {
      return new ThickShapes();
    }
    if (mazeConfig.drawType().equals(MazeDrawType.CLASSIC)) {
      return new MarginShapes();
    }
    throw new IllegalArgumentException("Don't know what shapeProvider to provide");
  }

  private void drawPosition(final Position position, final StringBuilder svg) {

    final List<MazeNode> mazeNodesAtPosition = board.getMazeNodesAtPosition(position);
    final String svgForNode = getShape(mazeNodesAtPosition);
    svg.append(svgForNode);
  }

//  void traverseAndDraw(final MazeNode node, final StringBuilder svg) {
//    // Draw node
//    final String svgForNode = getShape(node);
//    svg.append(svgForNode);
//    // Draw links to children
//    for (final MazeNode child : node.getChildren()) {
//      traverseAndDraw(child, svg);
//    }
//  }

  private String getShape(final List<MazeNode> nodes) {

    if (nodes.isEmpty()) {
      throw new IllegalArgumentException("Don't know what shapeProvider to provide");
    }
    final int x = nodes.getFirst().getPosition().x() * SCALE;
    final int y = nodes.getFirst().getPosition().y() * SCALE;
    final StringBuilder shapeSvg = new StringBuilder("<g transform=\"translate(" + x + "," + y + ")\">\n");
    if (nodes.size() == 1) {
      final MazeNode node = nodes.getFirst();

      final Set<Direction> directions = MazeGenerator.getDirectionsToLinks(node);
      addDirectionForStartAndEndOfPath(node, directions);
      shapeSvg.append(shapeProvider.getShapeSvg(directions));
    } else {
      shapeSvg.append(shapeProvider.getBridgeShapeSvg());
    }
    if (pathPositions.contains(nodes.getFirst().getPosition())) {
      shapeSvg.append("""
          <rect x="95" y="95" width="10" height="10" class="correct-path"/>
          """);
    }
    shapeSvg.append("</g>\n");
    return shapeSvg.toString();
  }

  private void addDirectionForStartAndEndOfPath(final MazeNode node, final Set<Direction> directions) {
    if (isFirstNodeOfThePath(node) || isLastNodeOfThePath(node)) {
      if (board.getPositionFrom(node.getPosition(), LEFT).isEmpty()) {
        directions.add(LEFT);
        return;
      }
      if (board.getPositionFrom(node.getPosition(), RIGHT).isEmpty()) {
        directions.add(RIGHT);
        return;
      }
      if (board.getPositionFrom(node.getPosition(), UP).isEmpty()) {
        directions.add(UP);
        return;
      }
      if (board.getPositionFrom(node.getPosition(), DOWN).isEmpty()) {
        directions.add(DOWN);
      }
    }
  }

  private boolean isFirstNodeOfThePath(final MazeNode node) {
    return path.getNodes().getFirst().getPosition().equals(node.getPosition());
  }

  private boolean isLastNodeOfThePath(final MazeNode node) {
    return path.getNodes().getLast().getPosition().equals(node.getPosition());
  }
}
