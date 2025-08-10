package com.ursuradu.maze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void createBoard(){
        Board board = new Board(2,2);

        assertEquals(4, board.getNodes().size());
        List<NodeRelation> relations1 = board.getNodes().get(new Position(0, 0)).getRelations();
        assertEquals(2, relations1.size());
        assertEquals(DIRECTION.RIGHT,relations1.getFirst().type());
        assertEquals(DIRECTION.DOWN,relations1.getLast().type());

        List<NodeRelation> relations4 = board.getNodes().get(new Position(1, 1)).getRelations();
        assertEquals(2, relations4.size());
        assertEquals(DIRECTION.LEFT,relations4.getFirst().type());
        assertEquals(DIRECTION.UP,relations4.getLast().type());


    }
}