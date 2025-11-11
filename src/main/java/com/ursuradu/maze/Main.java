package com.ursuradu.maze;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.svg.SvgGenerator;

public class Main {

  private static final String OUTPUT_FOLDER_NAME = "output";
  private static final String CONTENT_FOLDER_NAME = "mazes";
  private static final String SOLUTION_FOLDER_NAME = "solutions";

  private static final int NUMBER_OF_MAZES = 5;
  // manual configuration
//  private static final int WIDTH = 10;
//  private static final int HEIGHT = WIDTH;
//  private static final MazeDrawStyle STYLE = CLASSIC;
//  private static final int NUMBER_OF_PORTALS = 1;
//  private static final OnTheFlyPortals PORTALS_ON_THE_FLY = DISABLED;
//  private static final List<PathRequirements> PATH_REQUIREMENTS = List.of(PathRequirements.CONTAIN_ALL_PORTALS);
//  final MazeConfig mazeConfig = new MazeConfig("Manual", WIDTH, HEIGHT, STYLE, NUMBER_OF_PORTALS, PORTALS_ON_THE_FLY, PATH_REQUIREMENTS);

  public static void main(final String[] args) {

    final GenerationBatchConfig batchConfig = new GenerationBatchConfig(NUMBER_OF_MAZES, Stream.of(
            MazeConfigPreset.values()
        )
        .map(MazeConfigPreset::getMazeConfig).toList());

    // Use ISO_LOCAL_DATE_TIME to ensure no nanos, only up to seconds
    final String currentDateTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    final File outputDir = new File(OUTPUT_FOLDER_NAME + File.separator + currentDateTime); // Folder name is safe
    createMazeFolders(outputDir);
    final File contentDir = new File(outputDir, CONTENT_FOLDER_NAME);
    final File solutionDir = new File(outputDir, SOLUTION_FOLDER_NAME);

//    // Save MazeConfig to config file in the output directory
//    final File configFile = new File(outputDir, "config.txt");
//    saveFile(configFile.getPath(), mazeConfigToText(mazeConfig));

    for (final MazeConfig mazeConfig : batchConfig.getMazeConfigs()) {
      System.out.println("Generating mazes with config:\n" + mazeConfigToText(mazeConfig));
      for (int i = 0; i < batchConfig.getNumberOfMazes(); i++) {
        final Maze maze = generate(mazeConfig);
        final SvgGenerator svgGenerator = new SvgGenerator(mazeConfig, maze.getBoard());
        try {
          // TODO fix the error instead of skipping
          final String contentSolution = svgGenerator.generateSVG(maze.getSolutionPath(), true);
          final String content = svgGenerator.generateSVG(maze.getSolutionPath(), false);

          final String solutionFileName = solutionDir.getPath() + File.separator + "maze-solution-" + mazeConfig.getDisplayName() + "-" + i + ".svg";
          System.out.println("Saving solution to " + solutionFileName);
          saveFile(solutionFileName, contentSolution);
          final String mazeFileName = contentDir.getPath() + File.separator + "maze-" + mazeConfig.getDisplayName() + "-" + i + ".svg";
          System.out.println("Saving maze to " + mazeFileName);
          saveFile(mazeFileName, content);
        } catch (final Exception e) {
          System.out.println("Error generating/saving SVG: " + e.getMessage());
        }
      }
    }
//    // Open in default browser
//    openInBrowser(solutionFileName);
//    openInBrowser(mazeFileName);
  }

  private static Maze generate(final MazeConfig mazeConfig) {
    Board board;
    do {
      board = new Board(mazeConfig);
      final MazeGenerator mazeGenerator = new MazeGenerator(board, mazeConfig);
      final MazeNode root = mazeGenerator.generateMaze();

      final PathGenerator pathGenerator = new PathGenerator(board);
      final java.util.Optional<MazePath> generated =
          pathGenerator.getPath(root, mazeConfig);
      if (generated.isPresent()) {
        System.out.println("Generated Maze Path: " + generated.get());
        return new Maze(mazeConfig, board, generated.get());
      } else {
        System.out.println("No Maze Path found! Retrying...");
      }
    } while (true);
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

  private static void mkdirIfNotExists(final File dir) {
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  private static void createMazeFolders(final File outputDir) {
    final File outputRootDir = new File(OUTPUT_FOLDER_NAME);
    mkdirIfNotExists(outputRootDir);
    mkdirIfNotExists(outputDir);
    final File contentDir = new File(outputDir, CONTENT_FOLDER_NAME);
    mkdirIfNotExists(contentDir);
    final File solutionDir = new File(outputDir, SOLUTION_FOLDER_NAME);
    mkdirIfNotExists(solutionDir);
  }

  // Helper to format MazeConfig as text
  private static String mazeConfigToText(final MazeConfig config) {
    return "width: " + config.getWidth() + '\n'
        + "height: " + config.getHeight() + '\n'
        + "drawType: " + config.getStyle() + '\n'
        + "hasBridges: " + config.getStyle().hasBridges() + '\n'
        + "portals: " + config.getPortalsCount() + '\n'
        + "onTheFlyPortals: " + config.getOnTheFlyPortals() + '\n'
        + "pathRequirements: " + config.getPathRequirements() + '\n';
  }
}
