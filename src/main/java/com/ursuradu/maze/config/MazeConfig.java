package com.ursuradu.maze.config;

import static java.util.Collections.emptyList;

import java.util.List;

import com.ursuradu.maze.enums.MazeDrawStyle;
import com.ursuradu.maze.enums.MazeSize;
import com.ursuradu.maze.enums.PathRequirements;
import com.ursuradu.maze.model.OnTheFlyPortals;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class MazeConfig {

  private String displayName;
  private MazeSize size;
  private MazeDrawStyle style;
  private int portalsCount;
  @Builder.Default
  private OnTheFlyPortals onTheFlyPortals = OnTheFlyPortals.DISABLED;
  @Builder.Default
  private List<PathRequirements> pathRequirements = emptyList();
}
