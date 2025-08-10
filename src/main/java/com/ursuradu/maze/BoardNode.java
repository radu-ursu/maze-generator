package com.ursuradu.maze;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BoardNode {

    private Position position;
    private List<NodeRelation> relations = new ArrayList<>();

    public BoardNode(Position position) {
        this.position = position;
    }

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }

    public void addNeighbour(NodeRelation relation) {
        relations.add(relation);
    }
}
