package com.ursuradu.maze;

import java.util.HashSet;
import java.util.Set;

public class SvgGenerator {

    int scale = 200; // space between nodes

    private static void extractDirections(MazeNode node, MazeNode child, Set<DIRECTION> directions) {
        if (child.getBoardNode().getX() == node.getBoardNode().getX() - 1) {
            directions.add(DIRECTION.LEFT);
        }
        if (child.getBoardNode().getX() == node.getBoardNode().getX() + 1) {
            directions.add(DIRECTION.RIGHT);
        }
        if (child.getBoardNode().getY() == node.getBoardNode().getY() + 1) {
            directions.add(DIRECTION.DOWN);
        }
        if (child.getBoardNode().getY() == node.getBoardNode().getY() - 1) {
            directions.add(DIRECTION.UP);
        }
    }

    public String generateSVG(MazeNode root) {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1000 1000'>\n");
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
        traverseAndDraw(root, svg, null);

        svg.append("</svg>");
        return svg.toString();
    }

    void traverseAndDraw(MazeNode node, StringBuilder svg, Set<MazeNode> visited) {
//        if (!visited.add(node)) return; // prevent loops

        // Draw node
        String svgForNode = determineTypeOfShape(node);
        // Draw links to children
        for (MazeNode child : node.getChildren()) {
            svg.append(svgForNode);
            traverseAndDraw(child, svg, visited);
        }
    }

    private String determineTypeOfShape(MazeNode node) {

        int x = node.getBoardNode().getX() * scale;
        int y = node.getBoardNode().getY() * scale;
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
        if (directions.equals(Set.of(DIRECTION.LEFT, DIRECTION.RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 200 100 " class="path-outer"/>
                    <path d="M 0 100 L 200 100 " class="path-inner"/>
                                       </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.RIGHT))) {
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
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.RIGHT))) {
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
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN, DIRECTION.RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.LEFT, DIRECTION.RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-outer"/>
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-inner"/>
                    </g>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.LEFT, DIRECTION.RIGHT))) {
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
        if (directions.equals(Set.of(DIRECTION.RIGHT))) {
            return transform + """
                    <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                    <path d="M 200 100 L 100 100" class="path-outer"/>
                    <path d="M 200 100 L 110 100" class="path-inner"/>
                    </g>
                    """;
        }

        return transform + """
                 <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                   </g>
                """;

    }
}
