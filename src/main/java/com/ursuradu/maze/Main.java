package com.ursuradu.maze;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

  public static void main(final String[] args) {

    final MazeConfig mazeConfig = new MazeConfig(15, 15, true, MazeDrawType.CLASSIC, false, 1, true);
    mazeConfig.validate();
    MazePath path;
    MazeNode root;
    Board board;
    do {
      board = new Board(mazeConfig);
      final MazeGenerator mazeGenerator = new MazeGenerator(board, mazeConfig);
      root = mazeGenerator.generateMaze();
      System.out.println("Generation completed");

      final PathGenerator pathGenerator = new PathGenerator();
      path = pathGenerator.getPath(root);
      System.out.println(path);
    } while (mazeConfig.portals() > 0 && mazeConfig.correctPathMustContainPortals() && !path.isContainsPortals());
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
