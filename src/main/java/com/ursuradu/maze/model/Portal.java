package com.ursuradu.maze.model;

import lombok.Data;

@Data
public class Portal {

  private Position p1;
  private Position p2;
  private Position enter;
  private int id;

  public Portal(final Position p1, final Position p2) {
    this.p1 = p1;
    this.p2 = p2;
  }

  public boolean contains(final Position p) {
    return p.equals(p1) || p.equals(p2);
  }

  public Position getOtherPosition(final Position p) {
    if (!contains(p)) {
      throw new IllegalArgumentException("Invalid position");
    }
    return p.equals(p1) ? p2 : p.equals(p2) ? p1 : null;
  }

  @Override
  public String toString() {
    return "Portal{" +
        "id=" + id +
        ", p1=" + p1 +
        ", p2=" + p2 +
        ", enter=" + enter +
        '}';
  }
}
