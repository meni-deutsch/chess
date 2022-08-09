package main;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static main.Board.WHITE;
import static main.Board.add;

public class Pawn extends Piece {
    private boolean wasMoved = false;

    public Pawn(String SIDE, Place place) {
        super(SIDE, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pawn pawn = (Pawn) o;
        return super.place.equals(pawn.place) && super.SIDE.equals(pawn.SIDE) && super.MY_KING == pawn.MY_KING;
    }

    public boolean isWasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    @Override
    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        int direction = this.SIDE.equals(WHITE) ? 1 : -1;
        whereCanIMoveWithoutCaringForTheKing = new LinkedList<>();
        if ((Board.whoIn(place.getRank() + direction, place.getFile())) == null) {
            whereCanIMoveWithoutCaringForTheKing.add(new Place(place.getRank() + direction, place.getFile()));
            if (!this.wasMoved)
                if (!(Board.isOutOfBounds(place.getRank() + 2 * direction, place.getFile())))
                    if ((Board.whoIn(place.getRank() + 2 * direction, place.getFile())) == null)
                        whereCanIMoveWithoutCaringForTheKing.add(new Place(place.getRank() + 2 * (direction), place.getFile()));
        }

        whereCanIMoveWithoutCaringForTheKing.addAll(makeWhereIAttack());
        return whereCanIMoveWithoutCaringForTheKing;
    }

    /**
     * creates and updates whereIAttack. adds all the places where the pawn attacks.
     *
     * @return whereIAttack
     */
    public List<Place> makeWhereIAttack() {
        List<Place> whereIAttack = new LinkedList<>();
        int direction = SIDE.equals(WHITE) ? 1 : -1;
        if (Board.whoIn(place.getRank() + direction, place.getFile() + 1) != null)
            if (!Board.whoIn(place.getRank() + direction, place.getFile() + 1).SIDE.equals(this.SIDE))
                whereIAttack.add(new Place(place.getRank() + direction, place.getFile() + 1));
        if (Board.whoIn(place.getRank() + direction, place.getFile() - 1) != null)
            if (!Board.whoIn(place.getRank() + direction, place.getFile() - 1).SIDE.equals(this.SIDE))
                whereIAttack.add(new Place(place.getRank() + direction, place.getFile() - 1));
        return whereIAttack;
    }


    @Override
    public String toString() {
        return "Pawn{" +
                "wasMoved=" + wasMoved +
                ", SIDE='" + SIDE + '\'' +
                ", MY_KING=" + MY_KING.SIDE + " King" +
                ", place=" + place +
                '}';
    }

    @Override
    public void moveTo(@NotNull Place whereToMove) {
        wasMoved = true;
        Board.Recording.resetCount();
        Board.Recording.clear();
        if (Math.abs(whereToMove.getRank() - getRank()) == 2) {
            new GhostPawn(this);
        }

        super.moveTo(whereToMove);

        if (whereToMove.getRank() == 7) {
            EventQueue.invokeLater(Board::updateUI);
            promotion();
        }
    }


    public void promotion() {
        Object[] menu = {
                (new ImageIcon("src/main/pictures/" + this.SIDE + " queen.png", "queen")),
                (new ImageIcon("src/main/pictures/" + this.SIDE + " rook.png", "rook")),
                (new ImageIcon("src/main/pictures/" + this.SIDE + " bishop.png", "bishop")),
                (new ImageIcon("src/main/pictures/" + this.SIDE + " knight.png", "knight"))
        };
        this.remove();
        switch (JOptionPane.showOptionDialog(null, "", "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, menu, menu[0])) {
            case 1 -> add(new Rook(SIDE, place));
            case 2 -> add(new Bishop(SIDE, place));
            case 3 -> add(new Knight(SIDE, place));
            default -> add(new Queen(SIDE, place));

        }


    }
}
