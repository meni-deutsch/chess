package board.test;

import board.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static board.Side.WHITE;
import static board.Side.BLACK;

public class FunctionsForTesting {

    public static Stream<Arguments> allThePlaces() {
        ArrayList<Place> places = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                places.add(new Place(i, j));
            }
        }
        return places.stream().map(Arguments::of);
    }

    public static Stream<Arguments> allThePlacesTimes2() {
        ArrayList<Place[]> places = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        Place[] twoPlaces = {new Place(i, j), new Place(k, l)};
                        places.add(twoPlaces);
                    }

                }

            }
        }
        return places.stream().map(p2 -> Arguments.of(p2[0], p2[1]));
    }

    public static @Nullable Stream<Arguments> allTheTypesOfPieces() {
        ArrayList<Piece> pieces = new ArrayList<>(Arrays.asList(
                new King(WHITE),
                new King(BLACK),
                new Queen(WHITE),
                new Queen(BLACK),
                new Rook(WHITE, new Place("a1")),
                new Rook(BLACK, new Place("a1")),
                new Bishop(WHITE, new Place("a1")),
                new Bishop(BLACK, new Place("a1")),
                new Knight(WHITE, new Place("a1")),
                new Knight(BLACK, new Place("a1")),
                new Pawn(WHITE, new Place("a1")),
                new Pawn(BLACK, new Place("a1")),
                new GhostPawn(new Pawn(WHITE, new Place("a2"))),
                new GhostPawn(new Pawn(BLACK, new Place("a2"))),
                null));

        return pieces.stream().map(Arguments::of);
    }

    public static @NotNull Stream<Arguments> allTheTypesOfPiecesWithoutNull() {
        return allTheTypesOfPieces().filter(x -> x.get()[0] != null);
    }

    public static @NotNull List<Arguments> allThePiecesWithoutNull() {
        var ans = new ArrayList<Arguments>();
        allThePlaces().forEach( place -> allTheTypesOfPiecesWithoutNull().forEach(pieceArgument ->
                ans.add(Arguments.of(((Piece) (pieceArgument.get()[0])).setPlace((Place) place.get()[0])))
        ));
        return ans;
    }

}
