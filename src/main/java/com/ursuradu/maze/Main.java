package com.ursuradu.maze;

import static com.ursuradu.maze.PathRequirements.CONTAIN_ALL_PORTALS;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(final String[] args) {

    final MazeConfig mazeConfig = new MazeConfig(35, 35, MazeDrawType.CLASSIC, false, 5, true, 200);
    mazeConfig.validate();
    MazePath path = null;
    MazeNode root;
    Board board;
    do {
      board = new Board(mazeConfig);
      final MazeGenerator mazeGenerator = new MazeGenerator(board, mazeConfig);
      root = mazeGenerator.generateMaze();

      final PathGenerator pathGenerator = new PathGenerator(board);
      final java.util.Optional<MazePath> generated = pathGenerator.getPath(root, List.of(CONTAIN_ALL_PORTALS), mazeConfig);
      if (generated.isPresent()) {
        System.out.println("Generated Maze Path: " + generated.get());
        path = generated.get();
      } else {
        System.out.println("No Maze Path found! Retrying...");
      }
    } while (path == null);
    final SvgGenerator svgGenerator = new SvgGenerator(mazeConfig, board);
    final String contentSolution = svgGenerator.generateSVG(path, true);
    final String content = svgGenerator.generateSVG(path, false);

    final String solutionFileName = "maze-solution.svg";
    saveFile(solutionFileName, contentSolution);
    final String mazeFileName = "maze.svg";
    saveFile(mazeFileName, content);

    // Open in default browser
    openInBrowser(mazeFileName);
    openInBrowser(solutionFileName);

  }

  private static void openInBrowser(final String mazeFileName) {
    try {
      final File svgFile = new File(mazeFileName);
      if (svgFile.exists()) {
        Desktop.getDesktop().browse(svgFile.toURI());
        System.out.println("Opened SVG in browser.");
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void saveFile(final String fileName, final String contentSolution) {
    // Save SVG to file
    try (final FileWriter writer = new FileWriter(fileName)) {
      writer.write(contentSolution);
      System.out.println("SVG file saved as " + fileName);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
