package com.ursuradu.maze;

import static com.ursuradu.maze.Direction.DOWN;
import static com.ursuradu.maze.Direction.LEFT;
import static com.ursuradu.maze.Direction.RIGHT;
import static com.ursuradu.maze.Direction.UP;

import java.util.Set;

public class ThickShapes implements ShapeProvider {

  public String getShapeSvg(final Set<Direction> directions) {
    if (directions.equals(Set.of(Direction.UP, Direction.DOWN))) {
      return """
           <path d="M 100 0 L 100 200" class="path-outer"/>
           <path d="M 100 0 L 100 200" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.LEFT, RIGHT))) {
      return """
          <path d="M 0 100 L 200 100 " class="path-outer"/>
          <path d="M 0 100 L 200 100 " class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.DOWN, RIGHT))) {
      return """
          <path d="M 100 200 L 100 100 L 200 100" class="path-outer"/>
          <path d="M 100 200 L 100 100 L 200 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.DOWN, Direction.LEFT))) {
      return """
          <path d="M 100 200 L 100 100 L 0 100" class="path-outer"/>
          <path d="M 100 200 L 100 100 L 0 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP, Direction.LEFT))) {
      return """
          <path d="M 100 0 L 100 100 L 0 100" class="path-outer"/>
          <path d="M 100 0 L 100 100 L 0 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP, RIGHT))) {
      return """
          <path d="M 100 0 L 100 100 L 200 100" class="path-outer"/>
          <path d="M 100 0 L 100 100 L 200 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP, Direction.DOWN, Direction.LEFT))) {
      return """
          <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-outer"/>
          <path d="M 0 100 L 100 100 M 100 0 L 100 200" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP, Direction.DOWN, RIGHT))) {
      return """
          <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-outer"/>
          <path d="M 100 100 L 200 100 M 100 0 L 100 200" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP, Direction.LEFT, RIGHT))) {
      return """
          <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-outer"/>
          <path d="M 0 100 L 200 100 M 100 0 L 100 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.DOWN, Direction.LEFT, RIGHT))) {
      return """
          <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-outer"/>
          <path d="M 0 100 L 200 100 M 100 100 L 100 200" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.DOWN))) {
      return """
          <path d="M 100 200 L 100 0" class="path-outer"/>
          <path d="M 100 200 L 100 10" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.UP))) {
      return """
          <path d="M 100 0 L 100 200" class="path-outer"/>
          <path d="M 100 0 L 100 190" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(Direction.LEFT))) {
      return """
          <path d="M 0 100 L 200 100" class="path-outer"/>
          <path d="M 0 100 L 190 100" class="path-inner"/>
          """;
    }
    if (directions.equals(Set.of(RIGHT))) {
      return """
          <path d="M 200 100 L 0 100" class="path-outer"/>
          <path d="M 200 100 L 10 100" class="path-inner"/>
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

  @Override
  public String getBridgeShapeSvg() {
    return """
        <path d="M 100 0 L 100 200" class="path-outer"/>
        <path d="M 100 0 L 100 200" class="path-inner"/>
        <path d="M 0 100 L 200 100" class="path-outer"/>
        <path d="M 0 100 L 200 100" class="path-inner"/>
        """;
  }
}
