package com.ursuradu.maze;

public record NodeRelation(
        BoardNode neighbour,
        DIRECTION type) {
}
