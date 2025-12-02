package com.ursuradu.maze;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.enums.MazeSize;
import com.ursuradu.maze.enums.PathRequirements;

import java.util.Collections;
import java.util.stream.Stream;

import static com.ursuradu.maze.enums.MazeDrawStyle.BRIDGES;
import static com.ursuradu.maze.enums.MazeDrawStyle.CLASSIC;

public class Main {

    public static void main(final String[] args) throws Exception {

        final GenerationBatchConfig batchConfig = GenerationBatchConfig.builder()
                .numberOfMazes(19)
                .combinedImages(false)
                .exportPngs(true)
                .openSolutionInBrowser(false)
                .openMazesInBrowser(false)
                .mazeConfigs(Stream.of(
//                        getMazeConfigBuilder()
//                                .displayName("bridges")
//                                .style(BRIDGES)
//                                .size(MazeSize.MAZE_SIZE_11_16)
//                                .portalsCount(5)
//                                .build(),
                        getMazeConfigBuilder()
                                .displayName("classic")
                                .style(CLASSIC)
                                .size(MazeSize.MAZE_SIZE_13_18)
                                .pathRequirements(Collections.emptyList())
                                .portalsCount(0)
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
