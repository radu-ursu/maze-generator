package com.ursuradu.maze;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

  public static void main(final String[] args) {

    final MazeConfig mazeConfig = new MazeConfig(20, 20, true, MazeDrawType.THICK, true);
    mazeConfig.validate();
    final Board board = new Board(mazeConfig.width(), mazeConfig.height());
    final MazeGenerator mazeGenerator = new MazeGenerator(board, mazeConfig);
    final MazeNode root = mazeGenerator.generateMaze();
    System.out.println("Generation completed");

    final PathGenerator pathGenerator = new PathGenerator();
    final MazePath path = pathGenerator.getPath(root);
    System.out.println(path);

    final SvgGenerator svgGenerator = new SvgGenerator(mazeConfig, board);
    final String content = svgGenerator.generateSVG(path);
    final String fileName = "maze.svg";

    // Save SVG to file
    try (final FileWriter writer = new FileWriter(fileName)) {
      writer.write(content);
      System.out.println("SVG file saved as " + fileName);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // Open in default browser
    try {
      final File svgFile = new File(fileName);
      if (svgFile.exists()) {
        Desktop.getDesktop().browse(svgFile.toURI());
        System.out.println("Opened SVG in browser.");
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

  }
}
