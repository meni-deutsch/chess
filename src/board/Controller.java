package board;

import java.awt.*;
import java.util.List;

/**
 * a class that controls the board and gives the needed commands to the ui
 */
public class Controller {
    private static Place chosenPlace;
    private static Place lastPlace;
    private final static Board board = Board.newInstance();

    private static Piece whoIn(Place place) {
        return board.whoIn(place);
    }

    private static Piece whoIn(int rank, int file) {
        return board.whoIn(rank, file);
    }

    public static Boolean isPieceChosen(){
        return chosenPlace!=null;
    }
    public static char getPieceTypeAt(int rank, int file) {
        return whoIn(rank, file) == null ? ' ' : switch (whoIn(rank, file).getClass().getSimpleName()) {
            case "King" -> 'k';
            case "Queen" -> 'q';
            case "Rook" -> 'r';
            case "Bishop" -> 'b';
            case "Knight" -> 'n';
            case "Pawn" -> 'p';
            case "GhostPawn" -> ' ';
            default ->
                    throw new IllegalStateException("Unexpected value: " + whoIn(rank, file).getClass().getSimpleName());
        };
    }

    public static void printMoveRecording() {
        board.printMoveRecording();
    }

    public static void changeTurn() {
        Game.changeTurn();
    }

    public static Side getSide() {
        return Game.getSide();
    }

    public static Side getPieceSideAt(int rank, int file) {
        return whoIn(rank, file) == null ? null : whoIn(rank, file).SIDE;
    }

    public static List<Place> getAvailablePlaces() {

        return chosenPlace != null?whoIn(chosenPlace).whereCanIMove:whoIn(lastPlace).whereCanIMove;
    }


    public static boolean setMovingPiece(int rank, int file) {
        if (chosenPlace != null || Pawn.pawnToPromote != null) return false;
        if (getSide() == Controller.getPieceSideAt(rank, file)) {
            chosenPlace = new Place(rank, file);
            return true;
        }
        return false;
    }



    public static boolean movePieceTo(int rank, int file) {
        if (Pawn.pawnToPromote != null) return false;
        if (chosenPlace != null && getAvailablePlaces().contains(new Place(rank, file))) {
            board.move(chosenPlace.rank, chosenPlace.file, rank, file);
            lastPlace = chosenPlace;
            chosenPlace = null;
            return true;
        }
        lastPlace = chosenPlace;
        chosenPlace = null;
        return false;
    }

    public static String getGameStatus() {
        return board.getGameStatus();
    }

    public static Place getEndangeredKing() {
        return board.endangeredKing;
    }

    public static Boolean isReadyToPromote() {
        return Pawn.pawnToPromote != null;
    }

    public static void promote(char pieceType) {
        if (Pawn.pawnToPromote == null) {
            return;
        }
        whoIn(Pawn.pawnToPromote).remove();
        switch (pieceType) {
            case 'b' -> board.add(new Bishop(getSide(), Pawn.pawnToPromote));
            case 'n' -> board.add(new Knight(getSide(), Pawn.pawnToPromote));
            case 'q' -> board.add(new Queen(getSide(), Pawn.pawnToPromote));
            case 'r' -> board.add(new Rook(getSide(), Pawn.pawnToPromote));
            default -> throw new IllegalArgumentException("pieceType must be b,n,q,r or space");
        }
        Pawn.pawnToPromote=null;
        if(board.myKing(getSide().oppositeSide()).isEndangered()){
            board.checkKing(board.myKing(getSide().oppositeSide()));
        }
    }


}
