package test;

import board.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static board.Board.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {


    @BeforeAll
    static void setup() {
        makeBoard();
    }

    @BeforeEach
    void clear() {
        empty();
    }

    @MethodSource("test.FunctionsForTesting#allThePiecesWithoutNull")
    @ParameterizedTest
    void testRemove(@NotNull Piece piece) {
        makeBoard();
        Assumptions.assumeFalse(mySide(piece.SIDE).stream().anyMatch(piece::equals));
        add(piece);
        remove(piece);
        Assertions.assertFalse(mySide(piece.SIDE).stream().anyMatch(x -> x == piece));
        Assertions.assertNull(whoIn(piece.getPlace()));
    }

    @MethodSource("test.FunctionsForTesting#allThePlaces")
    @ParameterizedTest
    void testWhiteNumber(Place place) {
        Piece[] whitePieces = new Piece[10];
        for (int i = 0; i < 10; i++) {
            whitePieces[i] = new Pawn(WHITE, place);
            add(whitePieces[i]);
            assertEquals(whitePieces[i], whiteNumber(i));
        }
    }

    @MethodSource("test.FunctionsForTesting#allThePlaces")
    @ParameterizedTest
    void testBlackNumber(Place place) {
        Piece[] blackPieces = new Piece[10];
        for (int i = 0; i < 10; i++) {
            blackPieces[i] = new Pawn(BLACK, place);
            add(blackPieces[i]);
            assertEquals(blackPieces[i], blackNumber(i));
        }
    }

    @MethodSource("test.FunctionsForTesting#allThePlaces")
    @ParameterizedTest
    void testWhoIn(Place place) {
        Piece p = new Pawn(WHITE, place);
        assertEquals(p, whoIn(place));
        p = new Pawn(BLACK, place);
        assertEquals(p, whoIn(place));
        empty();
        p = new Rook(WHITE, place);
        assertEquals(p, whoIn(place));
        empty();
        p = new Rook(BLACK, place);
        assertEquals(p, whoIn(place));
        empty();
        p = new Bishop(WHITE, place);
        assertEquals(p, whoIn(place));
        empty();
        p = new Bishop(BLACK, place);
        assertEquals(p, whoIn(place));
        empty();
        Queen q = new Queen(WHITE);
        change(place, q);
        assertEquals(q, whoIn(place));
        empty();
        q = new Queen(BLACK);
        change(place, q);
        assertEquals(q, whoIn(place));
        empty();
        King k = new King(WHITE);
        change(place, k);
        assertEquals(k, whoIn(place));
        empty();
        k = new King(BLACK);
        change(place, k);
        assertEquals(k, whoIn(place));


    }

    @MethodSource("test.FunctionsForTesting#allThePlacesTimes2")
    @ParameterizedTest
    void testChange(Place p1, Place p2) {
        Piece p = new Pawn(WHITE, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        p = new Pawn(BLACK, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        empty();
        p = new Rook(WHITE, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        empty();
        p = new Rook(BLACK, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        empty();
        p = new Bishop(WHITE, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        empty();
        p = new Bishop(BLACK, p1);
        change(p2, p);
        assertEquals(p, whoIn(p2));
        assertEquals(p, whoIn(p.getPlace()));
        empty();
        Queen q = new Queen(WHITE, p1);
        change(p2, q);
        assertEquals(q, whoIn(p2));
        assertEquals(q, whoIn(q.getPlace()));
        empty();
        q = new Queen(BLACK, p1);
        change(p2, q);
        assertEquals(q, whoIn(p2));
        assertEquals(q, whoIn(q.getPlace()));
        empty();
        King k = new King(WHITE);
        change(p1, k);
        change(p2, k);
        assertEquals(k, whoIn(p2));
        assertEquals(k, whoIn(k.getPlace()));
        empty();
        k = new King(BLACK);
        change(p1, k);
        change(p2, k);
        assertEquals(k, whoIn(p2));
        assertEquals(k, whoIn(k.getPlace()));

    }

    @Test
    void testNumberOfWhite() {
        makeBoard();
        assertEquals(16, numberOfWhite());

    }

    @Test
    void testNumberOfBlacks() {
        makeBoard();
        assertEquals(16, numberOfBlack());

    }

    @Test
    void testEmpty() {
        assertEquals(0, numberOfWhite());
        assertEquals(0, numberOfBlack());
        assertAll(Arrays.stream(Board.getBoard()).map(x -> () -> assertArrayEquals(new Piece[8], x)));
    }

    @Test
    void testIsOutOfBounds() {
        assertTrue(isOutOfBounds(8, 0));
        assertTrue(isOutOfBounds(-8, 0));
        assertTrue(isOutOfBounds(0, 8));
        assertTrue(isOutOfBounds(0, -8));
        assertFalse(isOutOfBounds(7, 7));
        assertFalse(isOutOfBounds(7, 0));
        assertFalse(isOutOfBounds(0, 7));


    }

    @SuppressWarnings("DuplicatedCode")
    @MethodSource("test.FunctionsForTesting#allThePlaces")
    @ParameterizedTest
    void testOppositeSide(Place place) {
        Piece[] whitePieces = new Piece[10];
        Piece[] blackPieces = new Piece[10];
        for (int i = 0; i < 10; i++) {
            whitePieces[i] = new Bishop(WHITE, place);
            Assertions.assertEquals(whitePieces[i], whoIn(place));
            blackPieces[i] = new Bishop(BLACK, place);
            Assertions.assertEquals(blackPieces[i], whoIn(place));

        }
        Arrays.stream(whitePieces).forEach(Board::add);
        Arrays.stream(blackPieces).forEach(Board::add);
        Assertions.assertArrayEquals(whitePieces, oppositeSide(BLACK).toArray());
        Assertions.assertArrayEquals(blackPieces, oppositeSide(WHITE).toArray());

    }


    @MethodSource("test.FunctionsForTesting#allThePlaces")
    @ParameterizedTest
    @SuppressWarnings("DuplicatedCode")
    void testAdd(Place place) {
        Piece[] whitePieces = new Piece[10];
        Piece[] blackPieces = new Piece[10];
        for (int i = 0; i < 10; i++) {
            whitePieces[i] = new Rook(WHITE, place);
            Assertions.assertEquals(whitePieces[i], whoIn(place));
            blackPieces[i] = new Rook(BLACK, place);
            Assertions.assertEquals(blackPieces[i], whoIn(place));

        }
        Arrays.stream(whitePieces).forEach(Board::add);
        Arrays.stream(blackPieces).forEach(Board::add);
        Assertions.assertArrayEquals(whitePieces, mySide(WHITE).toArray());
        Assertions.assertArrayEquals(blackPieces, mySide(BLACK).toArray());

    }


    @MethodSource("test.FunctionsForTesting#allTheTypesOfPieces")
    @ParameterizedTest
    void TestPrint(Piece piece) {
        if (piece == null)
            System.out.println("null");
        else
            System.out.println(piece.getClass() + " " + piece.SIDE);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                change(new Place(i, j), piece);
            }

        }
        //print(WHITE);
    }

    @Test
    void TestGetBoardIsACopy() {
        makeBoard();
        var firstBoard = getBoard();

        Board.change(new Place("a5"), new Pawn(WHITE, new Place("a5")));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        for (int i = 7; i >= 0; i--) {
            System.out.print(i + 1);
            for (int j = 0; j < 8; j++) {
                System.out.print("|" + toSymbol(whoIn(i, j)));
            }
            System.out.println("|");
        }
        System.out.println();

        for (int i = 7; i >= 0; i--) {
            System.out.print(i + 1);
            for (int j = 0; j < 8; j++) {
                System.out.print("|" + toSymbol(firstBoard[i][j]));
            }
            System.out.println("|");
        }


    }

}
