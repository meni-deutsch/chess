package board;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

import static board.Side.WHITE;


class Pawn extends Piece {
    private boolean wasMoved = false;
    static Place pawnToPromote = null;

    public Pawn(Side side, Place place) {
        super(side, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pawn pawn = (Pawn) o;
        return super.place.equals(pawn.place) && super.SIDE.equals(pawn.SIDE) && super.MY_KING == pawn.MY_KING;
    }



    @Override
    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        int direction = this.SIDE.equals(WHITE) ? 1 : -1;
        whereCanIMoveWithoutCaringForTheKing = new LinkedList<>();
        if ((board.whoIn(place.getRank() + direction, place.getFile())) == null) {
            whereCanIMoveWithoutCaringForTheKing.add(new Place(place.getRank() + direction, place.getFile()));
            if (!this.wasMoved)
                if (!(Board.isOutOfBounds(place.getRank() + 2 * direction, place.getFile())))
                    if ((board.whoIn(place.getRank() + 2 * direction, place.getFile())) == null)
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
        if (board.whoIn(place.getRank() + direction, place.getFile() + 1) != null)
            if (!board.whoIn(place.getRank() + direction, place.getFile() + 1).SIDE.equals(this.SIDE))
                whereIAttack.add(new Place(place.getRank() + direction, place.getFile() + 1));
        if (board.whoIn(place.getRank() + direction, place.getFile() - 1) != null)
            if (!board.whoIn(place.getRank() + direction, place.getFile() - 1).SIDE.equals(this.SIDE))
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
        board.resetMoveCount();
        board.clearRecording();
        if (Math.abs(whereToMove.getRank() - getRank()) == 2) {
            new GhostPawn(this);
        }

        super.moveTo(whereToMove);

        if (whereToMove.getRank() == 7||whereToMove.getRank() == 0) {
            //EventQueue.invokeLater(board::updateUI);
            promotion();
        }
    }


    public void promotion() {
        pawnToPromote = getPlace();
//        Object[] menu = {
//                (new ImageIcon("src/main/ui.pictures/" + this.SIDE + " queen.png", "queen")),
//                (new ImageIcon("src/main/ui.pictures/" + this.SIDE + " rook.png", "rook")),
//                (new ImageIcon("src/main/ui.pictures/" + this.SIDE + " bishop.png", "bishop")),
//                (new ImageIcon("src/main/ui.pictures/" + this.SIDE + " knight.png", "knight"))
//        };
//        this.remove();
//        switch (JOptionPane.showOptionDialog(null, "", "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, menu, menu[0])) {
//            case 1 -> board.add(new Rook(SIDE, place));
//            case 2 -> board.add(new Bishop(SIDE, place));
//            case 3 -> board.add(new Knight(SIDE, place));
//            default -> board.add(new Queen(SIDE, place));
//
//        }


    }
}
