package com.ursuradu.maze;

import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.model.MazePath;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A generated maze with solution path.
 */
@Data
@AllArgsConstructor
public class Maze {

  private MazeConfig mazeConfig;
  private Board board;
  private MazePath solutionPath;
}
