package com.ursuradu.maze;

import java.util.Set;

public interface ShapeProvider {

    String getShapeSvg(Set<Direction> dirs);

    String getBridgeShapeSvg();

}
