package com.ursuradu.maze.config;

import java.util.List;

import com.ursuradu.maze.enums.MazeDrawStyle;
import com.ursuradu.maze.enums.PathRequirements;
import com.ursuradu.maze.model.OnTheFlyPortals;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MazeConfig {

  private String displayName;
  private int width;
  private int height;
  private MazeDrawStyle style;
  private int portalsCount;
  private OnTheFlyPortals onTheFlyPortals;
  private List<PathRequirements> pathRequirements;
}
