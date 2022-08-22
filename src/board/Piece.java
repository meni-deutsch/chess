package board;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static board.Board.*;

public abstract class Piece {
    /**
     * can be "white" or "black", represents with witch player the piece is
     */
    public final String SIDE;
    public final King MY_KING;
    protected Place place;
    protected List<Place> whereCanIMove;
    protected List<Place> whereCanIMoveWithoutCaringForTheKing;

    public Piece(@NotNull String SIDE, @NotNull Place place) {
        if (place.isOutOfBounds())
            throw new IndexOutOfBoundsException("place has to be in the board");
        this.SIDE = SIDE;
        this.place = place;
        {
            Board.change(place, this);
            if ((SIDE.equals(WHITE) ? Board.WHITE_KING : Board.BLACK_KING) == null)
                this.MY_KING = (King) (this);

            else
                this.MY_KING = SIDE.equals(WHITE) ? Board.WHITE_KING : Board.BLACK_KING;
        }
    }


    public void whereCanIMove() {
        whereCanIMove = makeWhereCanIMoveWithoutCaringForTheKing().stream().filter(x->!isNotSafeToMove(x)).collect(Collectors.toList());
    }


    public boolean canIEatAt(@NotNull Place place) {
        return this.canIEatAt(place.getRank(), place.getFile());
    }

    public boolean canIEatAt(int x, int y) {
        if (Board.isOutOfBounds(x, y))
            return false;
        if (Board.whoIn(x, y) != null&&!(Board.whoIn(x, y)instanceof GhostPawn)) {
            return !(Board.whoIn(x, y).SIDE).equals(this.SIDE);
        }
        return true;
    }


    public  List<Place> makeWhereCanIMoveWithoutCaringForTheKing(){
        whereCanIMoveWithoutCaringForTheKing = makeWhereIAttack();
        return whereCanIMoveWithoutCaringForTheKing;

    }

    public abstract List<Place> makeWhereIAttack() ;




    public Place getPlace() {
        return place;
    }

    public Piece setPlace(Place place) {
        this.place = place;
        return this;
    }

    public int getRank() {
        return place.getRank();
    }

    public int getFile() {
        return place.getFile();
    }

    public void moveTo(Place whereToMove) {
        deMarkCheck(myKing(SIDE).place);
        Board.Recording.updateMoveRecording( place,whereToMove, this);
        Piece eatenPiece = Board.whoIn(whereToMove);
        Board.change(this.place, null);
        if (eatenPiece != null) {
            eatenPiece.captured(this);
        } else {
            Board.change(whereToMove, this);
        }

    }

    public void addLine(List<Place> places, int x, int y) {
        addLine(places, new Place(this.place.toString()), x, y);
    }



    public void addLine(List<Place> places, Place place, int x, int y) {
        place = new Place(place.getRank() + y, place.getFile() + x);
        if (place.isOutOfBounds())
            return;
        if (Board.whoIn(place) != null && !(Board.whoIn(place) instanceof GhostPawn)) {
            if (!Board.whoIn(place).SIDE.equals(SIDE))
                places.add(place);
            return;
        }
        places.add(place);
        addLine(places, place, x, y);
    }

    public boolean isNotSafeToMove(@NotNull Place target) {
        Piece capturedPiece = Board.whoIn(target);
        Place priorPosition = this.place;
        if (capturedPiece != null) {
            capturedPiece.remove();
            Board.change(target, this);
            Board.change(priorPosition,null);
            boolean ans = MY_KING.isEndangered();
            Board.change(priorPosition, this);
            Board.add(capturedPiece);
            return ans;
        }

        Board.change(target, this);
        Board.change(priorPosition,null);
        boolean ans = MY_KING.isEndangered();
        Board.change(priorPosition, this);
        Board.change(target, null);
        return ans;
    }

    public void captured(Piece eater) {
        Board.Recording.clear();
        Board.Recording.resetCount();
        this.remove();
        Board.change(this.getPlace(), eater);
    }

    public void deleteIfGhostPawn() {}

    public void remove(){
        Board.remove(this);
    }


}
