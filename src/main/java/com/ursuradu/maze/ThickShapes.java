package com.ursuradu.maze;

import java.util.Set;

import static com.ursuradu.maze.DIRECTION.*;

public class ThickShapes {

    public static String getShapeSvg(Set<DIRECTION> directions) {
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN))) {
            return """
                     <path d="M 100 0 L 100 200" class="path-outer"/>
                     <path d="M 100 0 L 100 200" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.LEFT, RIGHT))) {
            return """
                    <path d="M 0 100 L 200 100 " class="path-outer"/>
                    <path d="M 0 100 L 200 100 " class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, RIGHT))) {
            return """
                    <path d="M 100 200 L 100 100 L 200 100" class="path-outer"/>
                    <path d="M 100 200 L 100 100 L 200 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.LEFT))) {
            return """
                    <path d="M 100 200 L 100 100 L 0 100" class="path-outer"/>
                    <path d="M 100 200 L 100 100 L 0 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.LEFT))) {
            return """
                    <path d="M 100 0 L 100 100 L 0 100" class="path-outer"/>
                    <path d="M 100 0 L 100 100 L 0 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, RIGHT))) {
            return """
                    <path d="M 100 0 L 100 100 L 200 100" class="path-outer"/>
                    <path d="M 100 0 L 100 100 L 200 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN, DIRECTION.LEFT))) {
            return """
                    <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.DOWN, RIGHT))) {
            return """
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP, DIRECTION.LEFT, RIGHT))) {
            return """
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-outer"/>
                    <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN, DIRECTION.LEFT, RIGHT))) {
            return """
                    <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.DOWN))) {
            return """
                    <path d="M 100 200 L 100 100" class="path-outer"/>
                    <path d="M 100 200 L 100 110" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.UP))) {
            return """
                    <path d="M 100 0 L 100 100" class="path-outer"/>
                    <path d="M 100 0 L 100 90" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(DIRECTION.LEFT))) {
            return """
                    <path d="M 0 100 L 100 100" class="path-outer"/>
                    <path d="M 0 100 L 90 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(RIGHT))) {
            return """
                    <path d="M 200 100 L 100 100" class="path-outer"/>
                    <path d="M 200 100 L 110 100" class="path-inner"/>
                    """;
        }
        if (directions.equals(Set.of(RIGHT, LEFT, UP, DOWN))) {
            return """
                    <path d="M 100 0 L 100 200" class="path-outer"/>
                    <path d="M 0 100 L 200 100" class="path-outer"/>
                    <path d="M 100 0 L 100 200" class="path-inner"/>
                    <path d="M 0 100 L 200 100" class="path-inner"/>
                    """;
        }

        return """
                 <rect x="0" y="0" width="200" height="200" class="tile-bg"/>
                """;
    }
}
