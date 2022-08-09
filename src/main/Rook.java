package main;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    private boolean wasMoved = false;


    public Rook(String SIDE, Place place) {
        super(SIDE, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rook rook = (Rook) o;
        return rightToCastling() == rook.rightToCastling() && super.place.equals(rook.place) && super.SIDE.equals(rook.SIDE) && super.MY_KING == rook.MY_KING;
    }

    @Override
    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new ArrayList<>();
        addLine(whereIAttack,1,0);
        addLine(whereIAttack,-1,0);
        addLine(whereIAttack,0,1);
        addLine(whereIAttack,0,-1);
        return whereIAttack;
    }
    @Override
    public void moveTo(@NotNull Place whereToMove){
        if(rightToCastling())
            Board.Recording.clear();
        wasMoved = true;
        super.moveTo(whereToMove);
    }
    @Override
    public String toString() {
        return "Rook{" +
                "wasMoved=" + wasMoved +
                ", SIDE='" + SIDE + '\'' +
                ", MY_KING=" + MY_KING.SIDE + " King" +
                ", place=" + place +
                '}';
    }


    public boolean rightToCastling() {
        return !this.wasMoved && !MY_KING.isWasMoved() &&getRank()==MY_KING.getRank();
    }
}
