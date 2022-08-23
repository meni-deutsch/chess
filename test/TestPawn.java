package board.test;

import board.Board;
import board.Pawn;
import board.Place;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static board.Board.BLACK; import static board.Board.WHITE;

public class TestPawn {
    private static Stream<Arguments> testsForWalking() {
        ArrayList<Place> places = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 8; j++) {
                places.add(new Place(i, j));
            }
        }
        var ans = places.stream().map(p -> Arguments.of(p, true)).collect(Collectors.toSet());
        ans.addAll(places.stream().map(p -> Arguments.of(p, false)).collect(Collectors.toSet()));
        return ans.stream();

    }

    private static Stream<Arguments> testsForEatingToTheRight() {
        return testsForWalking().filter(i -> ((Place) (i.get()[0])).getFile() < 7 && ((Place) (i.get()[0])).getRank() < 6);
    }

    private static Stream<Arguments> testsForEatingToTheLeft() {
        return testsForWalking().filter(i -> ((Place) (i.get()[0])).getFile() > 0 && ((Place) (i.get()[0])).getRank() < 6);
    }

    private static Stream<Arguments> testsForEatingToBoth() {
        return testsForWalking().filter(i -> ((Place) (i.get()[0])).getFile() > 0 && ((Place) (i.get()[0])).getFile() < 7 && ((Place) (i.get()[0])).getRank() < 6);
    }

    private static void canPawnWalkTwice(List<Place> places, @NotNull Pawn pawn) {
        if (!pawn.isWasMoved()) {
            if (Board.isOutOfBounds(pawn.getRank() + 2 * (pawn.SIDE.equals(WHITE) ? 1 : -1),pawn.getFile()))
                return;
            places.add(new Place(pawn.getRank() + 2 * (pawn.SIDE.equals(WHITE) ? 1 : -1), pawn.getFile()));
        }
    }

    @BeforeEach
    void setup() {
        Board.makeBoard();
        Board.empty();
    }

    @ParameterizedTest
    @MethodSource("testsForWalking")
    void testEasyWalkingStraitWithoutAnyOneInTheWayForWhites(Place place, boolean wasMoved) {
        Pawn p = new Pawn(WHITE, place);
        p.setWasMoved(wasMoved);
        List<Place> whereThePawnShouldGetTo = new ArrayList<>();
        whereThePawnShouldGetTo.add(new Place(p.getRank() + 1, p.getFile()));
        canPawnWalkTwice(whereThePawnShouldGetTo, p);
        Assertions.assertEquals(whereThePawnShouldGetTo.size(), (long) p.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertAll(
                p.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereThePawnShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );

    }

    @ParameterizedTest
    @MethodSource("testsForWalking")
    void testEasyWalkingStraitWithoutAnyOneInTheWayForBlacks(@NotNull Place place, boolean wasMoved) {
        Pawn p = new Pawn(BLACK, place);
        p.setWasMoved(wasMoved);
        List<Place> whereThePawnShouldGetTo = new ArrayList<>();
        whereThePawnShouldGetTo.add(new Place(p.getRank() - 1, p.getFile()));
        canPawnWalkTwice(whereThePawnShouldGetTo, p);
        Assertions.assertEquals(whereThePawnShouldGetTo.size(), p.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertAll(
                p.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereThePawnShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );
    }

    @ParameterizedTest
    @MethodSource("testsForEatingToTheRight")
    void testEatingToTheRight(@NotNull Place place, boolean wasMoved) {
        Pawn w = new Pawn(WHITE, place);
        Pawn b = new Pawn(BLACK, new Place(place.getRank() + 1, place.getFile() + 1));
        w.setWasMoved(wasMoved);
        b.setWasMoved(wasMoved);
        List<Place> whereTheWShouldGetTo = new ArrayList<>(List.of(new Place(w.getRank() + 1, w.getFile()), new Place(w.getRank() + 1, w.getFile() + 1)));
        List<Place> whereTheBShouldGetTo = new ArrayList<>(List.of(new Place(b.getRank() - 1, b.getFile()), new Place(b.getRank() - 1, b.getFile() - 1)));
        canPawnWalkTwice(whereTheBShouldGetTo, b);
        canPawnWalkTwice(whereTheWShouldGetTo, w);
        Assertions.assertEquals(whereTheBShouldGetTo.size(), b.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertEquals(whereTheWShouldGetTo.size(), w.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertAll(
                w.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereTheWShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );
        Assertions.assertAll(
                b.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereTheBShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );
    }

    @ParameterizedTest
    @MethodSource("testsForEatingToTheLeft")
    void testEatingToTheLeft(@NotNull Place place, boolean wasMoved) {
        Pawn w = new Pawn(WHITE, place);
        Pawn b = new Pawn(BLACK, new Place(place.getRank() + 1, place.getFile() - 1));
        w.setWasMoved(wasMoved);
        b.setWasMoved(wasMoved);
        List<Place> whereTheWShouldGetTo = new ArrayList<>(List.of(new Place(w.getRank() + 1, w.getFile()), new Place(w.getRank() + 1, w.getFile() - 1)));
        List<Place> whereTheBShouldGetTo = new ArrayList<>(List.of(new Place(b.getRank() - 1, b.getFile()), new Place(b.getRank() - 1, b.getFile() + 1)));
        canPawnWalkTwice(whereTheBShouldGetTo, b);
        canPawnWalkTwice(whereTheWShouldGetTo, w);
        Assertions.assertEquals(whereTheBShouldGetTo.size(), b.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertEquals(whereTheWShouldGetTo.size(), w.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertAll(
                w.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereTheWShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );
        Assertions.assertAll(
                b.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereTheBShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );
    }

    @ParameterizedTest
    @MethodSource("testsForEatingToBoth")
    void testEatingToBoth(@NotNull Place place, boolean wasMoved) {
        Pawn w = new Pawn(WHITE, place);
        Pawn b1 = new Pawn(BLACK, new Place(place.getRank() + 1, place.getFile() - 1));
        Pawn b2 = new Pawn(BLACK, new Place(place.getRank() + 1, place.getFile() + 1));
        w.setWasMoved(wasMoved);
        List<Place> whereTheWShouldGetTo = new ArrayList<>(List.of(new Place(w.getRank() + 1, w.getFile()), b1.getPlace(), b2.getPlace()));
        canPawnWalkTwice(whereTheWShouldGetTo, w);
        Assertions.assertEquals(whereTheWShouldGetTo.size(), w.makeWhereCanIMoveWithoutCaringForTheKing().size(),
                "are they the same length?");
        Assertions.assertAll(
                w.makeWhereCanIMoveWithoutCaringForTheKing().stream().map(i -> () -> Assertions.assertTrue(whereTheWShouldGetTo
                        .stream().anyMatch(i::equals), "is " + i + " in whereThePawnShouldGetTo"))
        );

    }


}