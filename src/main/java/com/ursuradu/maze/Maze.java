package com.ursuradu.maze;

import static com.ursuradu.maze.utils.RandomGenerator.generateId;

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

  private String id;
  private MazeConfig mazeConfig;
  private Board board;
  private MazePath solutionPath;
  private MazeStats stats;

  public Maze(final MazeConfig mazeConfig, final Board board, final MazePath solutionPath) {
    this(generateId(), mazeConfig, board, solutionPath, new MazeStats());
  }
}
