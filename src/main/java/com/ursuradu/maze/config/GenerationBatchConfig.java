package com.ursuradu.maze.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GenerationBatchConfig {

    private int numberOfMazes;
    private int combinedImages;
    private List<MazeConfig> mazeConfigs;

}
