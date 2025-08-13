package com.ursuradu.maze;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Maze maze = MazeGenerator.generateMaze(20, 20);
        System.out.println("Generation completed");

        MazeEdgePaths.PathResult pathResult = MazeEdgePaths.longestEdgeToEdgePath(maze.getRoot(), maze);
        System.out.println("Path found: " + pathResult.getLengthNodes());
        pathResult.getNodes().forEach(node -> System.out.println(node.getPosition()));

        SvgGenerator svgGenerator = new SvgGenerator();
        String content = svgGenerator.generateSVG(maze, pathResult);
        String fileName = "maze.svg";

        // Save SVG to file
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            System.out.println("SVG file saved as " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Open in default browser
        try {
            File svgFile = new File(fileName);
            if (svgFile.exists()) {
                Desktop.getDesktop().browse(svgFile.toURI());
                System.out.println("Opened SVG in browser.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
