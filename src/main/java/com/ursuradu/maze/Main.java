package com.ursuradu.maze;

import static com.ursuradu.maze.enums.MazeDrawStyle.*;
import static com.ursuradu.maze.enums.MazeSize.*;
import static com.ursuradu.maze.enums.PathRequirements.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;

public class Main {

  public static void main(final String[] args) throws Exception {

    final GenerationBatchConfig batchConfig = GenerationBatchConfig.builder()
        .numberOfMazes(1)
        .combinedImages(false)
        .exportPngs(false)
        .openSolutionInBrowser(true)
        .openMazesInBrowser(false)
        .folderNameSuffix("makingBookMap")
        .mazeConfigs(Stream.of(
//                        getMazeConfigBuilder()
//                                .displayName("bridges")
//                                .style(BRIDGES)
//                                .size(MazeSize.MAZE_SIZE_11_16)
//                                .portalsCount(5)
//                                .build(),
            MazeConfig.builder()
                .displayName("test")
                .style(CLASSIC)
                .size(MAZE_SIZE_16_24)
//                                .onTheFlyPortals(OnTheFlyPortals.SMALL_RATE)
                .pathRequirements(List.of(DONT_CONTAIN_ALL_PORTALS, START_FROM_LEFT, END_TO_RIGHT))
                .portalsCount(2)
                .build()
        ).toList())
        .build();

    new MazeApp().start(batchConfig);
  }

  private static MazeConfig.MazeConfigBuilder getMazeConfigBuilder() {
    return MazeConfig.builder()
        .size(MAZE_SIZE_12_17)
        .portalsCount(5)
        .pathRequirements(Collections.singletonList(DONT_CONTAIN_ALL_PORTALS))
        .style(BRIDGES);
  }
}
