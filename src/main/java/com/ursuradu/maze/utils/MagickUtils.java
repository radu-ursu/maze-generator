package com.ursuradu.maze.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
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

public class MagickUtils {

  private static final int DENSITY_DPI = 300;         // SVG → PNG render density (higher = sharper, larger)
  private static final String RESIZE = "";            // e.g. "1024x" or "1024x1024". Leave "" to skip

  public static void combinePngs(final List<Path> pngs, final File combinedPngFilesDirectory) throws Exception {
    System.out.println("Combining images...");
    // 3) Pair PNGs two-by-two and make horizontal tiles
    for (int i = 0; i + 1 < pngs.size(); i += 2) {
      final Path left = pngs.get(i);
      final Path right = pngs.get(i + 1);
      final String outName = stripExt(left.getFileName().toString())
          + "__" + stripExt(right.getFileName().toString())
          + "_tile2x1.png";
      final Path out = Paths.get(combinedPngFilesDirectory.getPath()).resolve(outName);
      System.out.println("Combining to " + outName);
      if (Files.exists(out)) {
        System.out.println("[skip] " + out + " exists");
      } else {
        combineSideBySide(left, right, out);
      }
    }
  }

  // Side-by-side (2x1) using +append (horizontal). For vertical, use -append.
  public static void combineSideBySide(final Path leftPng, final Path rightPng, final Path outPng) throws Exception {
    final List<String> cmd = Arrays.asList(
        "magick",
        "-background", "white",
        leftPng.toAbsolutePath().toString(),
        rightPng.toAbsolutePath().toString(),
        "+append",                                // horizontal stitch
        outPng.toAbsolutePath().toString()
    );
    run(cmd, "Tile: " + leftPng.getFileName() + " | " + rightPng.getFileName());
  }

  public static List<Path> convertToPngFiles(final File outputDir, final File svgFilesDirectory, final File pngFilesDirectory) throws Exception {

    System.out.println("Converting SVG files to PNG files...");
    final List<Path> svgs = Files.list(Paths.get(svgFilesDirectory.toURI()))
        .filter(p -> p.toString().toLowerCase().endsWith(".svg"))
        .sorted(Comparator.comparing(MagickUtils::getCreationOrModifiedTime))
        .toList();

    if (svgs.isEmpty()) {
      System.out.println("No SVGs found in: " + svgFilesDirectory.getPath());
      return svgs;
    }
    // 2) Convert each SVG → PNG
    final List<Path> pngs = new ArrayList<>();
    for (final Path svg : svgs) {
      final Path png = Paths.get(pngFilesDirectory.getPath()).resolve(replaceExt(svg.getFileName().toString(), ".png"));
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
  public static void convertSvgToPng(final Path svg, final Path png, final int densityDpi, final String resizeArg) throws Exception {
    final List<String> cmd = new ArrayList<>(Arrays.asList(
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

  public static String getAnnotation(final Path filePath) {
    return "Radu-" + stripExt(filePath.getFileName().toString());
  }

  public static String replaceExt(final String filename, final String newExt) {
    return stripExt(filename) + newExt;
  }

  public static String stripExt(final String filename) {
    final int dot = filename.lastIndexOf('.');
    return (dot > 0) ? filename.substring(0, dot) : filename;
  }

  public static void run(final List<String> command, final String label) throws Exception {
    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true); // merge stderr into stdout
    final Process p = pb.start();

    final String output;
    try (final InputStream is = p.getInputStream()) {
      output = new String(is.readAllBytes(), UTF_8);
    }
    final int code = p.waitFor();
    if (code != 0) {
      throw new RuntimeException("Command failed (" + label + "):\n" + String.join(" ", command) + "\n\n" + output);
    } else if (!output.isBlank()) {
      System.out.println("[" + label + "]\n" + output.trim());
    }
  }

  private static FileTime getCreationOrModifiedTime(final Path p) {
    try {
      // Try creation time first
      final FileTime creation = (FileTime) Files.getAttribute(p, "basic:creationTime");
      if (creation != null && creation.toMillis() > 0) {
        return creation;
      }
    } catch (final IOException ignored) {
      // fall back to modified time
    }
    try {
      return Files.getLastModifiedTime(p);
    } catch (final IOException e) {
      return FileTime.fromMillis(0); // fallback if all else fails
    }
  }
}
