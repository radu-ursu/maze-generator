package com.ursuradu.maze;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(4, 4);
        Maze maze = MazeGenerator.generateMaze(board);
        System.out.println("Generation completed");
        SvgGenerator svgGenerator = new SvgGenerator();
        System.out.println(svgGenerator.generateSVG(maze.getRoot()));
    }
}
