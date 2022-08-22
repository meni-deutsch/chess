package test;

import board.Board;
import board.Place;
import board.Queen;
import board.Rook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static board.Board.BLACK; import static board.Board.WHITE;

public class TestMakeBoard {

    @BeforeAll
    static void setup() {
        Board.makeBoard();
    }


    @Test
    void TestIfListOfPiecesMadeCorrectlyTestThatForKings() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.whiteNumber(0), Board.WHITE_KING),
                () -> Assertions.assertTrue(Board.WHITE_KING.getPlace().equals(new Place("e1"))),
                () -> Assertions.assertEquals(Board.blackNumber(0), Board.BLACK_KING),
                () -> Assertions.assertTrue(Board.BLACK_KING.getPlace().equals(new Place("e8")))
        );
    }

    @Test
    void TestIfListOfPiecesMadeCorrectlyTestThatForQueens() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.whiteNumber(1), new Queen(WHITE)),
                () -> Assertions.assertTrue(Board.whiteNumber(1).getPlace().equals(new Place("d1"))),
                () -> Assertions.assertEquals(Board.blackNumber(1), new Queen(BLACK)),
                () -> Assertions.assertTrue(Board.blackNumber(1).getPlace().equals(new Place("d8")))
        );
    }

    @Test
    void TestIfListOfPiecesMadeCorrectlyTestThatForRooks() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.whiteNumber(2), new Rook(WHITE, new Place("a1"))),
                () -> Assertions.assertEquals(Board.whiteNumber(3), new Rook(WHITE, new Place("h1"))),
                () -> Assertions.assertEquals(Board.blackNumber(2), new Rook(BLACK, new Place("a8"))),
                () -> Assertions.assertEquals(Board.blackNumber(3), new Rook(BLACK, new Place("h8")))
        );
    }

    @Test
    void TestIfMakeBoardMakesBoardCorrectlyKings() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.WHITE_KING, Board.whoIn(new Place("e1"))),
                () -> Assertions.assertEquals(Board.BLACK_KING, Board.whoIn(new Place("e8")))
        );
    }

    @Test
    void TestIfMakeBoardMakesBoardCorrectlyQueens() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.whoIn(new Place("d1")), Board.whiteNumber(1)),
                () -> Assertions.assertEquals(Board.whoIn(new Place("d8")), Board.blackNumber(1))
        );
    }

    @Test
    void TestIfMakeBoardMakesBoardCorrectlyRooks() {
        Assertions.assertAll(
                () -> Assertions.assertEquals(Board.whoIn(new Place("a1")), Board.whiteNumber(2)),
                () -> Assertions.assertEquals(Board.whoIn(new Place("h1")), Board.whiteNumber(3)),
                () -> Assertions.assertEquals(Board.whoIn(new Place("a8")), Board.blackNumber(2)),
                () -> Assertions.assertEquals(Board.whoIn(new Place("h8")), Board.blackNumber(3))
        );
    }
}
