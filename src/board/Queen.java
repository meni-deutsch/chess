package board;

import java.util.LinkedList;
import java.util.List;

import static board.Board.WHITE;

public class Queen extends Piece {

    public Queen(String SIDE) {
        super(SIDE, SIDE.equals(WHITE) ? new Place("d1") : new Place("d8"));
    }

    public Queen(String SIDE,Place place) {
        super(SIDE, place);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queen queen = (Queen) o;
        return super.place.equals(queen.place) && super.SIDE.equals(queen.SIDE) && super.MY_KING == queen.MY_KING;
    }

    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new LinkedList<>();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                addLine(whereIAttack,x,y);
            }
        return whereIAttack;
    }
    @Override
    public String toString() {
        return "Queen{" +
                ", SIDE='" + SIDE + '\'' +
                ", MY_KING=" + MY_KING.SIDE + " King" +
                ", place=" + place +
                '}';
    }
}
