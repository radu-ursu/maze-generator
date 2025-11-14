package com.ursuradu.maze;

import static com.ursuradu.maze.utils.MagickUtils.*;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.svg.SvgGenerator;

public class MazeApp {

  private static final String OUTPUT_FOLDER_NAME = "output";
  private static final String SVG_FOLDER_NAME = "svgs";
  private static final String PNG_FOLDER_NAME = "pngs";
  private static final String COMBINED_PNG_FOLDER_NAME = "combined";
  private static final String SOLUTION_FOLDER_NAME = "solutions";

  private static void generateSvgFiles(final GenerationBatchConfig batchConfig, final File solutionDir, final File contentDir) {
    for (final MazeConfig mazeConfig : batchConfig.getMazeConfigs()) {
      System.out.println("Generating mazes with config:\n" + mazeConfigToText(mazeConfig));
      for (int i = 0; i < batchConfig.getNumberOfMazes(); i++) {
        final Maze maze = generate(mazeConfig);
        final SvgGenerator svgGenerator = new SvgGenerator(mazeConfig, maze.getBoard());
        try {
          // TODO fix the error instead of skipping
          final String contentSolution = svgGenerator.generateSVG(maze.getSolutionPath(), true);
          final String content = svgGenerator.generateSVG(maze.getSolutionPath(), false);

          final String fileNameFormat = "%s" + File.separator + "%s-%s_" + i + "-%s.svg";
          final String solutionFileName = String.format(
              fileNameFormat,
              solutionDir.getPath(),
              "solution",
              mazeConfig.getDisplayName(),
              maze.getId()
          );
          System.out.println("Saving solution to " + solutionFileName);
          saveSvgFile(solutionFileName, contentSolution);
          final String mazeFileName = String.format(
              fileNameFormat,
              contentDir.getPath(),
              "maze",
              mazeConfig.getDisplayName(),
              maze.getId()
          );
          System.out.println("Saving maze to " + mazeFileName);
          saveSvgFile(mazeFileName, content);

          if (batchConfig.isOpenSolutionInBrowser()) {
            openInBrowser(solutionFileName);
          }
          if (batchConfig.isOpenMazesInBrowser()) {
            openInBrowser(mazeFileName);
          }
        } catch (final Exception e) {
          System.out.println("Error generating/saving SVG: " + e.getMessage());
          e.printStackTrace();
          throw e;
        }
      }
    }
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

  private static void saveSvgFile(final String fileName, final String content) {
    // Save SVG to file
    try (final FileWriter writer = new FileWriter(fileName)) {
      writer.write(content);
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
    final File svgsDir = new File(outputDir, SVG_FOLDER_NAME);
    mkdirIfNotExists(svgsDir);
    final File pngFolder = new File(outputDir, PNG_FOLDER_NAME);
    mkdirIfNotExists(pngFolder);
    final File combined = new File(pngFolder, COMBINED_PNG_FOLDER_NAME);
    mkdirIfNotExists(combined);
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

  public void start(final GenerationBatchConfig batchConfig) throws Exception {

    final String currentDateTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    final File outputDir = new File(OUTPUT_FOLDER_NAME + File.separator + currentDateTime); // Folder name is safe
    createMazeFolders(outputDir);
    final File svgFilesDirectory = new File(outputDir, SVG_FOLDER_NAME);
    final File pngFilesDirectory = new File(outputDir, PNG_FOLDER_NAME);
    final File combinedPngFilesDirectory = new File(pngFilesDirectory, COMBINED_PNG_FOLDER_NAME);
    final File solutionDir = new File(outputDir, SOLUTION_FOLDER_NAME);

//    // Save MazeConfig to config file in the output directory
//    final File configFile = new File(outputDir, "config.txt");
//    saveFile(configFile.getPath(), mazeConfigToText(mazeConfig));

    generateSvgFiles(batchConfig, solutionDir, svgFilesDirectory);

    if (!batchConfig.isExportPngs()) {
      System.out.println("PNG export is disabled. Exiting.");
      return;
    }

    final List<Path> pngs = convertToPngFiles(outputDir, svgFilesDirectory, pngFilesDirectory);
    if (batchConfig.isCombinedImages()) {
      combinePngs(pngs, combinedPngFilesDirectory);
    }
    System.out.println("Done.");
  }
}
