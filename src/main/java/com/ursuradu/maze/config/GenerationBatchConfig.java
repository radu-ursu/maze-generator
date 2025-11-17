package com.ursuradu.maze.config;

import static java.util.Collections.emptyList;

import java.util.List;

import com.ursuradu.maze.MazeConfigPreset;
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
  @Builder.Default
  private List<MazeConfig> mazeConfigs = emptyList();
  @Builder.Default
  private List<MazeConfigPreset> mazeConfigPresets = emptyList();
}
