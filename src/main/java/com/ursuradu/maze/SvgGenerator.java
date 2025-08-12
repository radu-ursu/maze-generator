package com.ursuradu.maze;

import java.util.HashSet;
import java.util.Set;

import static com.ursuradu.maze.DIRECTION.*;

public class SvgGenerator {

    private int scale = 200; // space between nodes

    private static void extractDirections(MazeNode node, MazeNode child, Set<DIRECTION> directions) {
        if (child.getPosition().x() == node.getPosition().x() - 1) {
            directions.add(DIRECTION.LEFT);
        }
        if (child.getPosition().x() == node.getPosition().x() + 1) {
            directions.add(RIGHT);
        }
        if (child.getPosition().y() == node.getPosition().y() + 1) {
            directions.add(DIRECTION.DOWN);
        }
        if (child.getPosition().y() == node.getPosition().y() - 1) {
            directions.add(DIRECTION.UP);
        }
    }

    public String generateSVG(Maze maze) {
        StringBuilder svg = new StringBuilder();
        int viewBoxWidth = maze.getWidth() * scale;
        int viewBoxHeight = maze.getHeight() * scale;
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 ");
        svg.append(viewBoxWidth).append(" ").append(viewBoxHeight).append("'>\n");
        svg.append("""
                <style>
                            .tile-bg {
                              fill: #EEEEEE;
                        	 stroke: #000000;
                            }
                            .path-outer {
                              stroke: #555;
                              stroke-width: 60;
                              fill: none;
                            }
                            .path-inner {
                              stroke: #eee;
                              stroke-width: 40;
                              fill: none;
                            }
                </style>
                
                """);

//        Set<MazeNode> visited = new HashSet<>();
        traverseAndDraw(maze.getRoot(), svg, null);

        svg.append("</svg>");
        return svg.toString();
    }

    void traverseAndDraw(MazeNode node, StringBuilder svg, Set<MazeNode> visited) {
//        if (!visited.add(node)) return; // prevent loops

        // Draw node
        String svgForNode = determineTypeOfShape(node);
        svg.append(svgForNode);
        // Draw links to children
        for (MazeNode child : node.getChildren()) {
            traverseAndDraw(child, svg, visited);
        }
    }

    private String determineTypeOfShape(MazeNode node) {

        int x = node.getPosition().x() * scale;
        int y = node.getPosition().y() * scale;
        String transform = "<g transform=\"translate(" + x + "," + y + ")\">\n";

        Set<DIRECTION> directions = new HashSet<>();
        node.getChildren().forEach(child -> extractDirections(node, child, directions));
        if (node.getParent() != null) {
            extractDirections(node, node.getParent(), directions);
        }

        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN))) {
            return transform + """
                     <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                     <path d="M 100 0 L 100 200" class="path-outer"/>
                     <path d="M 100 0 L 100 200" class="path-inner"/>
                     </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.LEFT, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 200 100 " class="path-outer"/>
                    <path d="M 0 100 L 200 100 " class="path-inner"/>
                                       </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 200 L 100 100 L 200 100" class="path-outer"/>
                    <path d="M 100 200 L 100 100 L 200 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.LEFT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 200 L 100 100 L 0 100" class="path-outer"/>
                    <path d="M 100 200 L 100 100 L 0 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.LEFT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 0 L 100 100 L 0 100" class="path-outer"/>
                    <path d="M 100 0 L 100 100 L 0 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 0 L 100 100 L 200 100" class="path-outer"/>
                    <path d="M 100 0 L 100 100 L 200 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.LEFT, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-outer"/>
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.LEFT, RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-inner"/>
                     </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 200 L 100 100" class="path-outer"/>
                    <path d="M 100 200 L 100 110" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 0 L 100 100" class="path-outer"/>
                    <path d="M 100 0 L 100 90" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.LEFT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 100 100" class="path-outer"/>
                    <path d="M 0 100 L 90 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 200 100 L 100 100" class="path-outer"/>
                    <path d="M 200 100 L 110 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(RIGHT, LEFT, UP, DOWN))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 200 100" class="path-outer"/>
                    <path d="M 100 0 L 100 200" class="path-inner"/>
                    <path d="M 0 100 L 200 100" class="path-inner"/>
                    </g>
                    """;
        }

        return transform + """
                 <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                   </g>
                """;

    }
}
