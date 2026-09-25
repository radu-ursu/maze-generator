package com.ursuradu.maze;

import static com.ursuradu.maze.enums.MazeDrawStyle.BRIDGES;
import static com.ursuradu.maze.enums.MazeDrawStyle.CLASSIC;
import static com.ursuradu.maze.enums.PathRequirements.CONTAIN_ALL_PORTALS;
import static com.ursuradu.maze.enums.PathRequirements.PATH_LENGTH_MAX;
import static com.ursuradu.maze.enums.PathRequirements.START_FROM_LEFT;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.MazeSize;
import com.ursuradu.maze.enums.PathRequirements;

public class Main {

  public static void main(final String[] args) throws Exception {

    final GenerationBatchConfig batchConfig = GenerationBatchConfig.builder()
        .numberOfMazes(1)
        .combinedImages(false)
        .exportPngs(true)
        .openSolutionInBrowser(false)
        .openMazesInBrowser(false)
        .folderNameSuffix("printPreviewTest")
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
                .size(MazeSize.MAZE_SIZE_17_11)
//                                .onTheFlyPortals(OnTheFlyPortals.SMALL_RATE)
                .pathRequirements(List.of(CONTAIN_ALL_PORTALS, PATH_LENGTH_MAX, START_FROM_LEFT))
                .portalsCount(1)
                .build()
        ).toList())
        .build();

    new MazeApp().start(batchConfig);
  }

  private static MazeConfig.MazeConfigBuilder getMazeConfigBuilder() {
    return MazeConfig.builder()
        .size(MazeSize.MAZE_SIZE_12_17)
        .portalsCount(5)
        .pathRequirements(Collections.singletonList(PathRequirements.DONT_CONTAIN_ALL_PORTALS))
        .style(BRIDGES);
  }
}
