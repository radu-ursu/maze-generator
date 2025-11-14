package com.ursuradu.maze;

import java.util.stream.Stream;

import com.ursuradu.maze.config.GenerationBatchConfig;

public class Main {

  public static void main(final String[] args) throws Exception {

    final MazeConfigPreset smallPipesBridges3Portal = MazeConfigPreset.SMALL_PIPES_BRIDGES_3_PORTAL;
    smallPipesBridges3Portal.getMazeConfig().setPortalsCount(5);
    final GenerationBatchConfig batchConfig = GenerationBatchConfig.builder()
        .numberOfMazes(100)
        .combinedImages(false)
        .exportPngs(false)
        .openSolutionInBrowser(false)
        .openMazesInBrowser(false)
        .mazeConfigs(Stream.of(
                smallPipesBridges3Portal
            )
            .map(MazeConfigPreset::getMazeConfig).toList())
        .build();

    new MazeApp().start(batchConfig);
  }
}
