package com.ursuradu.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OnTheFlyPortals {

  public static final OnTheFlyPortals DISABLED = new OnTheFlyPortals(false, 0);
  public static final OnTheFlyPortals SMALL_RATE = new OnTheFlyPortals(true, 15);
  public static final OnTheFlyPortals MEDIUM_RATE = new OnTheFlyPortals(true, 25);
  public static final OnTheFlyPortals LARGE_RATE = new OnTheFlyPortals(true, 40);

  private boolean active;
  private int maxRate;
}
