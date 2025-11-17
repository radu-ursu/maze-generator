package com.ursuradu.maze;

import static com.ursuradu.maze.enums.MazeDrawStyle.CLASSIC;

import java.util.stream.Stream;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.MazeSize;

public class Main {

  public static void main(final String[] args) throws Exception {

    final MazeConfig mazeConfig = MazeConfig.builder()
        .size(MazeSize.MAZE_SIZE_16_22)
        .portalsCount(5)
        .style(CLASSIC)
        .build();
    final GenerationBatchConfig batchConfig = GenerationBatchConfig.builder()
        .numberOfMazes(1)
        .combinedImages(false)
        .exportPngs(false)
        .openSolutionInBrowser(true)
        .openMazesInBrowser(false)
        .mazeConfigs(Stream.of(mazeConfig).toList())
        .build();

    new MazeApp().start(batchConfig);
  }
}
