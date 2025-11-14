package com.ursuradu.maze.svg;

import static com.ursuradu.maze.MazeGenerator.getMovementDirection;
import static com.ursuradu.maze.enums.Direction.*;
import static com.ursuradu.maze.enums.MazeDrawStyle.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ursuradu.maze.Board;
import com.ursuradu.maze.MazeGenerator;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.Direction;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.model.Portal;
import com.ursuradu.maze.model.Position;

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

  public String generateSVG(final MazePath path, final boolean showSolution) {
    pathPositions = new HashSet<>();
    this.path = path;
    this.path.getNodes().forEach(node -> pathPositions.add(node.getPosition()));
    this.shapeProvider = getShapeProvider(mazeConfig);
    final StringBuilder svg = new StringBuilder();
    final int viewBoxWidth = board.getWidth() * SCALE + 2 * SCALE;
    final int viewBoxHeight = board.getHeight() * SCALE + 2 * SCALE;
    svg.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='-200 -200 ");
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
                                      stroke-width: 100;
                                      fill: none;
                                      shape-rendering: crispEdges;
        
                                    }
                                    .path-inner {
                                      stroke: #fff;
                                      stroke-width: 70;
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
                                    }
                        </style>
        
        """);
    svg.append("""
          <rect x="-200" y="-200" width="%d" height="%d" fill="#fff"/>
        """.formatted(viewBoxWidth, viewBoxHeight));
    svg.append("""
        <defs>
        <marker id="arrowhead" markerWidth="10" markerHeight="7"
        refX="10" refY="3.5" orient="auto">
        <polygon points="0 0, 10 3.5, 0 7" fill="black" />
        </marker>
        </defs>
        """);
    for (final Position position : board.getMazeMap().keySet()) {
      drawPosition(position, svg, showSolution);
    }

    svg.append("</svg>");
    return svg.toString();
  }

  private ShapeProvider getShapeProvider(final MazeConfig mazeConfig) {
    if (mazeConfig.getStyle().equals(PIPES) || mazeConfig.getStyle().equals(PIPES_BRIDGES)) {
      return new ThickShapes();
    }
    if (mazeConfig.getStyle().equals(CLASSIC)) {
      return new MarginShapes();
    }
    throw new IllegalArgumentException("Don't know what shapeProvider to provide");
  }

  private void drawPosition(final Position position, final StringBuilder svg, final boolean showSolution) {

    final List<MazeNode> mazeNodesAtPosition = board.getMazeNodesAtPosition(position);
    final String svgForNode = getShape(mazeNodesAtPosition, showSolution, position);
    // TODO temp
    if (svgForNode != null) {
      svg.append(svgForNode);
    } else {
      System.out.println("Didn't have svg for position: " + position);
    }
  }

  private String getShape(final List<MazeNode> nodes, final boolean showSolution, final Position position) {

    final int x = position.x() * SCALE;
    final int y = position.y() * SCALE;
    final StringBuilder shapeSvg = new StringBuilder("<g transform=\"translate(" + x + "," + y + ")\">\n");
    if (nodes.isEmpty()) {
      System.out.println("No shape for empty nodes at position: " + position);
      shapeSvg.append(shapeProvider.getShapeSvg(new HashSet<>()));
      shapeSvg.append("</g>\n");
      return shapeSvg.toString();
    }
    if (nodes.size() == 1) {
      final MazeNode node = nodes.getFirst();

      final Set<Direction> directions = MazeGenerator.getDirectionsToLinks(node);
      final Optional<Direction> directionForStartAndEndOfPath = getExtraDirectionForStartAndEndOfPath(node);
      directionForStartAndEndOfPath.ifPresent(directions::add);

      // remove children/parent directions for portal
      final Optional<Portal> portal = board.getPortal(position);
      if (portal.isPresent()) {
        directions.clear();
        if (portal.get().getEnter().equals(position)) {
          directions.add(getMovementDirection(position, node.getParent().getPosition()));
        } else {
          if (!node.getChildren().isEmpty()) {
            directions.add(getMovementDirection(position, node.getChildren().getFirst().getPosition()));
          } else {
            System.out.println("Portal node has no children: " + node);
          }
        }
      }
      shapeSvg.append(shapeProvider.getShapeSvg(directions));
      // draw arrow for start and end of path
      if (directionForStartAndEndOfPath.isPresent()) {
        final Direction direction = directionForStartAndEndOfPath.get();
        final boolean isExit = isLastNodeOfThePath(nodes.getFirst().getPosition());
        switch (direction) {
          case LEFT:
            if (isExit) {
              shapeSvg.append(" <line x1=\"15\" y1=\"100\" x2=\"-185\" y2=\"100\"");
            } else {
              shapeSvg.append(" <line x1=\"-185\" y1=\"100\" x2=\"15\" y2=\"100\"");
            }
            break;
          case RIGHT:
            if (isExit) {
              shapeSvg.append(" <line x1=\"185\" y1=\"100\" x2=\"385\" y2=\"100\"");
            } else {
              shapeSvg.append(" <line x1=\"385\" y1=\"100\" x2=\"185\" y2=\"100\"");
            }
            break;
          case UP:
            if (isExit) {
              shapeSvg.append(" <line x1=\"100\" y1=\"15\" x2=\"100\" y2=\"-185\"");
            } else {
              shapeSvg.append(" <line x1=\"100\" y1=\"-185\" x2=\"100\" y2=\"15\"");
            }
            break;
          case DOWN:
            if (isExit) {
              shapeSvg.append(" <line x1=\"100\" y1=\"185\" x2=\"100\" y2=\"385\"");
            } else {
              shapeSvg.append(" <line x1=\"100\" y1=\"385\" x2=\"100\" y2=\"185\"");
            }
            break;
        }
        shapeSvg.append("""
            
            stroke="black" stroke-width="5"
            marker-end="url(#arrowhead)" />
            """);
      }
    } else {
      shapeSvg.append(shapeProvider.getBridgeShapeSvg());
    }
    if (showSolution) {
      if (pathPositions.contains(nodes.getFirst().getPosition())) {
        shapeSvg.append("""
            <rect x="95" y="95" width="10" height="10" class="correct-path"/>
            """);
      }
    }
    shapeSvg.append("</g>\n");
    if (board.isPortal(nodes.getFirst().getPosition())) {
      final int portalPositionX = x + SCALE / 2;
      final int portalPositionY = y + SCALE / 2;
      shapeSvg.append("<g transform=\"translate(" + portalPositionX + "," + portalPositionY + ") scale(3.5,4)\">");
      shapeSvg.append("""
           <path d="M 0.000 0.000 L 0.040 0.002 L 0.080 0.008 L 0.119 0.018 L 0.157 0.032 L 0.194 0.049 L 0.229 0.071 L 0.263 0.096 L 0.295 0.125 L 0.324 0.157 L 0.351 0.192 L 0.375 0.230 L 0.396 0.271 L 0.414 0.315 L 0.428 0.361 L 0.439 0.409 L 0.446 0.459 L 0.449 0.511 L 0.448 0.564 L 0.442 0.618 L 0.432 0.673 L 0.418 0.729 L 0.399 0.784 L 0.376 0.840 L 0.348 0.895 L 0.315 0.949 L 0.278 1.002 L 0.237 1.054 L 0.190 1.104 L 0.140 1.152 L 0.085 1.197 L 0.026 1.240 L -0.037 1.279 L -0.104 1.316 L -0.175 1.349 L -0.250 1.378 L -0.327 1.402 L -0.408 1.423 L -0.491 1.438 L -0.577 1.449 L -0.666 1.455 L -0.756 1.455 L -0.848 1.450 L -0.941 1.439 L -1.036 1.423 L -1.131 1.401 L -1.226 1.372 L -1.321 1.338 L -1.416 1.297 L -1.510 1.250 L -1.602 1.197 L -1.693 1.138 L -1.782 1.072 L -1.869 1.001 L -1.953 0.923 L -2.033 0.840 L -2.111 0.750 L -2.184 0.655 L -2.253 0.555 L -2.317 0.449 L -2.376 0.339 L -2.430 0.223 L -2.478 0.103 L -2.520 -0.021 L -2.556 -0.149 L -2.585 -0.281 L -2.607 -0.416 L -2.622 -0.554 L -2.630 -0.695 L -2.630 -0.838 L -2.622 -0.982 L -2.606 -1.128 L -2.583 -1.274 L -2.551 -1.421 L -2.510 -1.568 L -2.462 -1.715 L -2.405 -1.860 L -2.339 -2.004 L -2.265 -2.146 L -2.182 -2.285 L -2.092 -2.422 L -1.993 -2.555 L -1.885 -2.684 L -1.770 -2.809 L -1.647 -2.928 L -1.517 -3.043 L -1.379 -3.152 L -1.234 -3.254 L -1.082 -3.350 L -0.923 -3.438 L -0.759 -3.519 L -0.589 -3.592 L -0.413 -3.657 L -0.232 -3.713 L -0.047 -3.760 L 0.143 -3.797 L 0.336 -3.825 L 0.532 -3.843 L 0.731 -3.851 L 0.932 -3.849 L 1.135 -3.836 L 1.338 -3.812 L 1.542 -3.777 L 1.746 -3.732 L 1.949 -3.675 L 2.151 -3.608 L 2.351 -3.529 L 2.548 -3.439 L 2.742 -3.338 L 2.932 -3.227 L 3.118 -3.104 L 3.299 -2.971 L 3.475 -2.828 L 3.644 -2.675 L 3.806 -2.511 L 3.961 -2.338 L 4.109 -2.156 L 4.248 -1.964 L 4.378 -1.765 L 4.498 -1.557 L 4.609 -1.341 L 4.709 -1.118 L 4.798 -0.889 L 4.876 -0.653 L 4.943 -0.412 L 4.997 -0.166 L 5.039 0.085 L 5.069 0.339 L 5.085 0.597 L 5.088 0.857 L 5.078 1.119 L 5.055 1.382 L 5.017 1.645 L 4.966 1.908 L 4.901 2.170 L 4.822 2.430 L 4.730 2.688 L 4.623 2.942 L 4.503 3.193 L 4.369 3.439 L 4.222 3.679 L 4.062 3.913 L 3.888 4.141 L 3.702 4.360 L 3.504 4.572 L 3.294 4.774 L 3.072 4.967 L 2.840 5.149 L 2.596 5.320 L 2.343 5.480 L 2.080 5.628 L 1.808 5.763 L 1.528 5.885 L 1.240 5.993 L 0.945 6.087 L 0.644 6.167 L 0.337 6.231 L 0.025 6.280 L -0.291 6.313 L -0.610 6.331 L -0.931 6.332 L -1.254 6.317 L -1.578 6.285 L -1.902 6.236 L -2.225 6.171 L -2.546 6.089 L -2.864 5.990 L -3.179 5.875 L -3.490 5.743 L -3.795 5.594 L -4.094 5.430 L -4.386 5.249 L -4.670 5.053 L -4.945 4.841 L -5.211 4.614 L -5.466 4.373 L -5.710 4.118 L -5.942 3.849 L -6.162 3.567 L -6.368 3.273 L -6.560 2.967 L -6.737 2.650 L -6.899 2.323 L -7.045 1.986 L -7.175 1.640 L -7.287 1.287 L -7.382 0.926 L -7.459 0.559 L -7.518 0.186 L -7.558 -0.191 L -7.579 -0.571 L -7.580 -0.954 L -7.562 -1.339 L -7.525 -1.724 L -7.468 -2.109 L -7.391 -2.492 L -7.295 -2.873 L -7.178 -3.251 L -7.042 -3.624 L -6.887 -3.991 L -6.713 -4.352 L -6.519 -4.706 L -6.307 -5.051 L -6.077 -5.386 L -5.828 -5.711 L -5.563 -6.025 L -5.280 -6.326 L -4.982 -6.614 L -4.667 -6.888 L -4.338 -7.146 L -3.995 -7.389 L -3.637 -7.616 L -3.268 -7.825 L -2.886 -8.016 L -2.493 -8.189 L -2.091 -8.342 L -1.679 -8.475 L -1.259 -8.588 L -0.832 -8.680 L -0.399 -8.751 L 0.039 -8.800 L 0.481 -8.827 L 0.926 -8.832 L 1.372 -8.814 L 1.819 -8.773 L 2.265 -8.710 L 2.710 -8.624 L 3.151 -8.516 L 3.589 -8.384 L 4.021 -8.230 L 4.446 -8.054 L 4.864 -7.856 L 5.274 -7.636 L 5.673 -7.394 L 6.062 -7.132 L 6.438 -6.849 L 6.801 -6.547 L 7.150 -6.225 L 7.483 -5.885 L 7.801 -5.526 L 8.101 -5.151 L 8.383 -4.760 L 8.646 -4.353 L 8.890 -3.931 L 9.112 -3.496 L 9.314 -3.049 L 9.493 -2.590 L 9.650 -2.121 L 9.783 -1.643 L 9.893 -1.156 L 9.978 -0.663 L 10.039 -0.164 L 10.074 0.339 L 10.085 0.845 L 10.069 1.354 L 10.029 1.863 L 9.962 2.371 L 9.869 2.877 L 9.751 3.380 L 9.607 3.878 L 9.437 4.370 L 9.243 4.855 L 9.023 5.331 L 8.779 5.797 L 8.510 6.252 L 8.218 6.695 L 7.903 7.124 L 7.565 7.539 L 7.206 7.937 L 6.826 8.318 L 6.425 8.681 L 6.005 9.024 L 5.567 9.348 L 5.112 9.650 L 4.640 9.929 L 4.153 10.186 L 3.652 10.418 L 3.138 10.626 L 2.613 10.809 L 2.077 10.965 L 1.531 11.095 L 0.979 11.197 L 0.419 11.272 L -0.145 11.319 L -0.713 11.338 L -1.284 11.328 L -1.855 11.289 L -2.425 11.221 L -2.993 11.124 L -3.558 10.999 L -4.117 10.845 L -4.670 10.662 L -5.215 10.451 L -5.750 10.212 L -6.275 9.946 L -6.787 9.653 L -7.286 9.333 L -7.769 8.987 L -8.236 8.617 L -8.686 8.222 L -9.116 7.803 L -9.527 7.363 L -9.915 6.900 L -10.282 6.417 L -10.625 5.915 L -10.943 5.394 L -11.235 4.856 L -11.502 4.303 L -11.740 3.734 L -11.951 3.153 L -12.133 2.560 L -12.285 1.957 L -12.407 1.345 L -12.499 0.725 L -12.560 0.100 L -12.589 -0.530 L -12.587 -1.162 L -12.552 -1.795 L -12.486 -2.428 L -12.388 -3.058 L -12.258 -3.685 L -12.096 -4.307 L -11.903 -4.921 L -11.678 -5.527 L -11.423 -6.123 L -11.137 -6.706 L -10.821 -7.277 L -10.475 -7.833 L -10.102 -8.372 L -9.700 -8.893 L -9.272 -9.396 L -8.817 -9.877 L -8.338 -10.337 L -7.834 -10.773 L -7.308 -11.184 L -6.760 -11.570 L -6.192 -11.929 L -5.604 -12.260 L -4.999 -12.562 L -4.378 -12.834 L -3.742 -13.075 L -3.093 -13.285 L -2.432 -13.462 L -1.762 -13.606 L -1.083 -13.717 L -0.397 -13.794 L 0.294 -13.837 L 0.988 -13.845 L 1.684 -13.818 L 2.379 -13.756 L 3.072 -13.659 L 3.762 -13.527 L 4.446 -13.360 L 5.122 -13.158 L 5.790 -12.922 L 6.447 -12.652 L 7.091 -12.349 L 7.721 -12.013 L 8.335 -11.644 L 8.931 -11.245 L 9.509 -10.814 L 10.065 -10.354 L 10.599 -9.865 L 11.110 -9.349 L 11.595 -8.806 L 12.054 -8.238 L 12.484 -7.647 L 12.886 -7.032 L 13.257 -6.397 L 13.597 -5.742 L 13.905 -5.069 L 14.179 -4.379 L 14.419 -3.675 L 14.624 -2.958 L 14.793 -2.229 L 14.926 -1.491 L 15.022 -0.745 L 15.080 0.007 L 15.101 0.762 L 15.084 1.520 L 15.028 2.278 L 14.935 3.034 L 14.803 3.787 L 14.634 4.534 L 14.426 5.273 L 14.182 6.003 L 13.900 6.722 L 13.582 7.428 L 13.228 8.118 L 12.838 8.792 L 12.415 9.446 L 11.958 10.081 L 11.468 10.693 L 10.947 11.282 L 10.396 11.845 L 9.816 12.381 L 9.208 12.889 L 8.574 13.366 L 7.915 13.813 L 7.233 14.227 L 6.529 14.607 L 5.806 14.952 L 5.064 15.262 L 4.305 15.534 L 3.532 15.769 L 2.746 15.966 L 1.950 16.123 L 1.144 16.240 L 0.332 16.317" stroke="#999" stroke-width="2.2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
          """);
      shapeSvg.append("</g>");

      final int portalNumberX = portalPositionX;
      final int portalNumberY = portalPositionY;
      shapeSvg.append("<g font-family=\"Arial Black\" font-size=\"100\" text-anchor=\"middle\" dominant-baseline=\"middle\">\n" +
          "                            <g transform=\"translate(" + portalNumberX + "," + portalNumberY + ")\">\n" +
          "                            <text>" + board.getPortal(nodes.getFirst().getPosition()).get().getId() + "</text>\n" +
          "                            </g></g>");

    }
    if (isFirstNodeOfThePath(nodes.getFirst().getPosition()) || isLastNodeOfThePath(nodes.getLast().getPosition())) {
      // TODO wtf is this
    }

    return shapeSvg.toString();
  }

  private Optional<Direction> getExtraDirectionForStartAndEndOfPath(final MazeNode node) {
    if (isFirstNodeOfThePath(node.getPosition()) || isLastNodeOfThePath(node.getPosition())) {
      if (board.getPositionFrom(node.getPosition(), LEFT).isEmpty()) {
        return Optional.of(LEFT);
      }
      if (board.getPositionFrom(node.getPosition(), RIGHT).isEmpty()) {
        return Optional.of(RIGHT);
      }
      if (board.getPositionFrom(node.getPosition(), UP).isEmpty()) {
        return Optional.of(UP);
      }
      if (board.getPositionFrom(node.getPosition(), DOWN).isEmpty()) {
        return Optional.of(DOWN);
      }
    }
    return Optional.empty();
  }

  private boolean isFirstNodeOfThePath(final Position position) {
    return path.getNodes().getFirst().getPosition().equals(position);
  }

  private boolean isLastNodeOfThePath(final Position position) {
    return path.getNodes().getLast().getPosition().equals(position);
  }
}
