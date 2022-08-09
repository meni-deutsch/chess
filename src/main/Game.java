package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static main.Board.*;

public class Game {
    private static String side = WHITE;
    private static Piece chosenPiece = null;

    public static void main(String[] args) {
        makeBoard();
    }


    public static boolean gameContinues() {
        return getGameStatus().equals("on going");
    }

    public static void checkGameStatus() {
        Board.Recording.update();
        Board.Recording.updateCount();
        isDeadPosition();
        checkIfPiecesCanMove(side);
    }


    public static String getSide() {
        return side;
    }


    public static void inputWhoMoves(Place place) {
        if (!isInputTheRightSide(whoIn(place))) {
            chosenPiece = null;
            return;
        }
        chosenPiece = whoIn(place);
        annotatePossibleLocations(chosenPiece.whereCanIMove);
    }

    public static boolean isInPossibleLocations(@NotNull Place place, @NotNull List<Place> possibleLocations) {
        restoreLocations(chosenPiece.whereCanIMove);
        return possibleLocations.stream().anyMatch(place::equals);
    }


    public static boolean isInputTheRightSide(Piece piece) {
        if (piece == null) {
            System.out.println("in the place that you inputted there wasn't a piece.");
            return false;
        } else return (piece.SIDE).equals(side);
    }


    public static void pressed(Place place) {
        if (chosenPiece != null && isInPossibleLocations(place, chosenPiece.whereCanIMove)) {
            chosenPiece.moveTo(place);
            chosenPiece = null;
            side = side.equals(WHITE) ? BLACK : WHITE;
            EventQueue.invokeLater(Board::updateUI);
            updateWhereCanThePiecesGo(side);
            checkGameStatus();
            if (!gameContinues())
                closeBoard();
        } else
            inputWhoMoves(place);
    }

}
