package com.ursuradu.maze;

import com.ursuradu.maze.config.GenerationBatchConfig;
import com.ursuradu.maze.config.MazeConfig;
import com.ursuradu.maze.model.MazeNode;
import com.ursuradu.maze.model.MazePath;
import com.ursuradu.maze.svg.SvgGenerator;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    private static final String OUTPUT_FOLDER_NAME = "output";
    private static final String SVG_FOLDER_NAME = "svgs";
    private static final String PNG_FOLDER_NAME = "pngs";
    private static final String COMBINED_PNG_FOLDER_NAME = "combined";
    private static final String SOLUTION_FOLDER_NAME = "solutions";

    private static final int DENSITY_DPI = 300;         // SVG → PNG render density (higher = sharper, larger)
    private static final String RESIZE = "";            // e.g. "1024x" or "1024x1024". Leave "" to skip

    // magick montage *.svg -tile 2x1 -geometry +5+5 output.png
    // manual configuration
//  private static final int WIDTH = 10;
//  private static final int HEIGHT = WIDTH;
//  private static final MazeDrawStyle STYLE = CLASSIC;
//  private static final int NUMBER_OF_PORTALS = 1;
//  private static final OnTheFlyPortals PORTALS_ON_THE_FLY = DISABLED;
//  private static final List<PathRequirements> PATH_REQUIREMENTS = List.of(PathRequirements.CONTAIN_ALL_PORTALS);
//  final MazeConfig mazeConfig = new MazeConfig("Manual", WIDTH, HEIGHT, STYLE, NUMBER_OF_PORTALS, PORTALS_ON_THE_FLY, PATH_REQUIREMENTS);

    public static void main(final String[] args) throws Exception {

        final GenerationBatchConfig batchConfig = new GenerationBatchConfig(1, 2, Stream.of(
                        MazeConfigPreset.SMALL_CLASSIC_1_PORTAL, MazeConfigPreset.LARGE_CLASSIC_5_PORTAL
                )
                .map(MazeConfigPreset::getMazeConfig).toList());

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
        List<Path> pngs = convertToPngFiles(outputDir, svgFilesDirectory, pngFilesDirectory);
        if (batchConfig.getCombinedImages() != 1) {
            combinePngs(pngs, batchConfig.getCombinedImages(), combinedPngFilesDirectory);
        }
        System.out.println("Done.");
    }

    private static void combinePngs(List<Path> pngs, int combinedImages, File combinedPngFilesDirectory) throws Exception {
        System.out.println("Combining images...");
        // 3) Pair PNGs two-by-two and make horizontal tiles
        if (combinedImages != 2) {
            throw new IllegalArgumentException("Unsupported combined images");
        }
        for (int i = 0; i + 1 < pngs.size(); i += 2) {
            Path left = pngs.get(i);
            Path right = pngs.get(i + 1);
            String outName = stripExt(left.getFileName().toString())
                    + "__" + stripExt(right.getFileName().toString())
                    + "_tile2x1.png";
            Path out = Paths.get(combinedPngFilesDirectory.getPath()).resolve(outName);

            if (Files.exists(out)) {
                System.out.println("[skip] " + out + " exists");
            } else {
                combineSideBySide(left, right, out);
            }
        }
    }

    // Side-by-side (2x1) using +append (horizontal). For vertical, use -append.
    private static void combineSideBySide(Path leftPng, Path rightPng, Path outPng) throws Exception {
        List<String> cmd = Arrays.asList(
                "magick",
                "-background", "white",
                leftPng.toAbsolutePath().toString(),
                rightPng.toAbsolutePath().toString(),
                "+append",                                // horizontal stitch
                outPng.toAbsolutePath().toString()
        );
        run(cmd, "Tile: " + leftPng.getFileName() + " | " + rightPng.getFileName());
    }

    private static List<Path> convertToPngFiles(File outputDir, File svgFilesDirectory, File pngFilesDirectory) throws Exception {

        System.out.println("Converting SVG files to PNG files...");
        List<Path> svgs = Files.list(Paths.get(svgFilesDirectory.toURI()))
                .filter(p -> p.toString().toLowerCase().endsWith(".svg"))
                .sorted(Comparator.comparing(Main::getCreationOrModifiedTime))
                .toList();

        if (svgs.isEmpty()) {
            System.out.println("No SVGs found in: " + svgFilesDirectory.getPath());
            return svgs;
        }
        // 2) Convert each SVG → PNG
        List<Path> pngs = new ArrayList<>();
        for (Path svg : svgs) {
            Path png = Paths.get(pngFilesDirectory.getPath()).resolve(replaceExt(svg.getFileName().toString(), ".png"));
            if (Files.exists(png)) {
                System.out.println("[skip] " + png + " exists");
            } else {
                convertSvgToPng(svg, png, DENSITY_DPI, RESIZE);
            }
            pngs.add(png);
        }
        return pngs;
    }


    // SVG → PNG using ImageMagick. Transparent background, set density, optional resize.
    private static void convertSvgToPng(Path svg, Path png, int densityDpi, String resizeArg) throws Exception {
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "magick", "convert",
                "-background", "none",
                "-density", String.valueOf(densityDpi),
                svg.toAbsolutePath().toString()
        ));
        if (resizeArg != null && !resizeArg.isEmpty()) {
            cmd.add("-resize");
            cmd.add(resizeArg);
        }
        // Add a label in the corner
        cmd.addAll(Arrays.asList(
                "-gravity", "northwest",        // position
                "-fill", "white",               // text color
                "-undercolor", "#00000080",     // semi-transparent background under text
                "-pointsize", "36",             // font size
                "-annotate", "+10+10", getAnnotation(svg) // offset from corner and text content
        ));

        cmd.add(png.toAbsolutePath().toString());
        run(cmd, "SVG→PNG: " + svg.getFileName());
    }

    private static String getAnnotation(Path filePath) {
        return "Radu-" + stripExt(filePath.getFileName().toString());
    }

    private static String replaceExt(String filename, String newExt) {
        return stripExt(filename) + newExt;
    }

    private static String stripExt(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > 0) ? filename.substring(0, dot) : filename;
    }

    private static void run(List<String> command, String label) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // merge stderr into stdout
        Process p = pb.start();

        String output;
        try (InputStream is = p.getInputStream()) {
            output = new String(is.readAllBytes(), UTF_8);
        }
        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("Command failed (" + label + "):\n" + String.join(" ", command) + "\n\n" + output);
        } else if (!output.isBlank()) {
            System.out.println("[" + label + "]\n" + output.trim());
        }
    }

    private static void generateSvgFiles(GenerationBatchConfig batchConfig, File solutionDir, File contentDir) {
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
//                    // Open in default browser
//                    openInBrowser(solutionFileName);
//                    openInBrowser(mazeFileName);
                } catch (final Exception e) {
                    System.out.println("Error generating/saving SVG: " + e.getMessage());
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

    private static FileTime getCreationOrModifiedTime(Path p) {
        try {
            // Try creation time first
            FileTime creation = (FileTime) Files.getAttribute(p, "basic:creationTime");
            if (creation != null && creation.toMillis() > 0) {
                return creation;
            }
        } catch (IOException ignored) {
            // fall back to modified time
        }
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException e) {
            return FileTime.fromMillis(0); // fallback if all else fails
        }
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
}
