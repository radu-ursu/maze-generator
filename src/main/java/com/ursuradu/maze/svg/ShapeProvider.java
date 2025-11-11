package com.ursuradu.maze.svg;

import java.util.Set;

import com.ursuradu.maze.enums.Direction;

public interface ShapeProvider {

  String getShapeSvg(Set<Direction> dirs);

  String getBridgeShapeSvg();

}
