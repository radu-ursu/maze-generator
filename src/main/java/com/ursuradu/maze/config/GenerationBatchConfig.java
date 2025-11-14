package com.ursuradu.maze.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenerationBatchConfig {

  private int numberOfMazes;
  private boolean combinedImages;
  private boolean exportPngs;
  private boolean openSolutionInBrowser;
  private boolean openMazesInBrowser;
  private List<MazeConfig> mazeConfigs;
}
