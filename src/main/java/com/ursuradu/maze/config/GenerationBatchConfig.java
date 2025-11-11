package com.ursuradu.maze.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerationBatchConfig {

  private int numberOfMazes;
  private List<MazeConfig> mazeConfigs;

}
