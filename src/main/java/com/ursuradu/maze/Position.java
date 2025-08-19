package com.ursuradu.maze;

public record Position(int x, int y) {

  @Override
  public String toString() {
    return "{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}
