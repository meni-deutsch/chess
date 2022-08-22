package board;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;


/**
 * @author Meni Deutsch<br>
 * The class that manages the board, moves the pieces, and stores them
 */


public class Board {
    public static final King WHITE_KING;
    public static final King BLACK_KING;
    /**
     * The colors white and black
     */
    public static final String BLACK = "black";
    public static final String WHITE = "white";
    private static List<Piece> whitePieces = new LinkedList<>();
    private static List<Piece> blackPieces = new LinkedList<>();
    /**
     * A variable that says if the game is on going, or over, and if it's over from which cause
     */
    private static String gameStatus = "on going";
    //region board
    private static Piece[][] board = new Piece[8][8];
    private static UI boardUI;
    //endregion

    static {
        WHITE_KING = new King(WHITE);
        BLACK_KING = new King(BLACK);
    }


    /**
     * Returns true if the available pieces can checkmate the other side. The method <b>isn't perfect</b>. There a lot of
     * cases that a checkmate isn't possible, no matter which moves would be done, but the method wouldn't return true.
     * The method checks only if the pieces are enough to checkmate, not if the position of the pieces is checkmate-able.
     *
     * @param side the side the method checks if the pieces can checkmate.
     * @return can the side checkmate
     */
    public static boolean canCheckMate(String side) {
        var mySide = mySide(side);
        if (mySide.stream().anyMatch(x -> x instanceof Rook || x instanceof Queen || x instanceof Pawn))
            return true;
        if (mySide.stream().filter(x -> x instanceof Bishop || x instanceof Knight).count() >= 2 && mySide.stream().anyMatch(x -> x instanceof Knight))
            return true;
        if (mySide.stream().filter(x -> x instanceof Bishop).count() >= 2) {
            //noinspection OptionalGetWithoutIsPresent
            return !isWhole((float) (mySide.stream().filter(x -> x instanceof Bishop)
                    .map(x -> (x.getFile() + x.getRank()) % 2).reduce(Integer::sum).get()) /
                    mySide.stream().filter(x -> x instanceof Bishop).count());
        }
        return false;
    }

    /**
     * Checks if there are a piece that can move. If there are non, ends the game by changing the {@code gameStatus} to
     * "stalemate - draw".
     *
     * @param side which side to check
     */
    public static void checkIfPiecesCanMove(String side) {
        for (Piece piece : mySide(side)
        ) {
            if (!piece.whereCanIMove.isEmpty()) {
                return;
            }
        }
        if (myKing(side).isEndangered())
            gameStatus = side.equals(WHITE) ? BLACK : WHITE + " win";
        else
            gameStatus = "stalemate - draw";
    }

    /**
     * Like {@link Board#canCheckMate} but for both of the sides. If both of the sides can't checkmate, ends the game by
     * changing {@code gameStatus} to "dead position - draw".
     */
    public static void isDeadPosition() {
        gameStatus = canCheckMate(WHITE) || canCheckMate(BLACK) ? gameStatus : "dead position - draw";
    }

    /**
     * Makes a new game board. resets the board and the pieces, adds all the pieces to the board.
     * Updates there possible moves, and open the user interface.
     */
    public static void makeBoard() {
        //reset the board and the pieces
        empty();
        //region making the pieces and inserting them to the players' pieces
        add(WHITE_KING);
        add(BLACK_KING);
        add(new Queen(WHITE));
        add(new Queen(BLACK));
        add(new Rook(WHITE, new Place("a1")));
        add(new Rook(WHITE, new Place("h1")));
        add(new Rook(BLACK, new Place("a8")));
        add(new Rook(BLACK, new Place("h8")));
        add(new Knight(WHITE, new Place("b1")));
        add(new Knight(WHITE, new Place("g1")));
        add(new Knight(BLACK, new Place("b8")));
        add(new Knight(BLACK, new Place("g8")));
        add(new Bishop(WHITE, new Place("c1")));
        add(new Bishop(WHITE, new Place("f1")));
        add(new Bishop(BLACK, new Place("c8")));
        add(new Bishop(BLACK, new Place("f8")));
        for (int i = 0; i < 8; i++) {
            add(new Pawn(BLACK, new Place(6, i)));
            add(new Pawn(WHITE, new Place(1, i)));
        }
        //endregion making the pieces and inserting them to the players' pieces
        updateWhereCanThePiecesGo(WHITE);
        boardUI = new Board.UI();
        boardUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int[] i = {1};
                Board.Recording.moveRecording.forEach(move -> System.out.println((i[0]++) + " " + move));
            }
        });
        boardUI.setVisible(true);
    }

    /**
     * For all the Pieces updates there possible moves.
     *
     * @param side which pieces to update the white or the black
     */
    public static void updateWhereCanThePiecesGo(@NotNull String side) {
        mySide(side).forEach(Piece::whereCanIMove);
        deleteGhostPawns(side);
    }

    /**
     * Checks all the pieces from one side, and deletes all the GhostPawns
     *
     * @param side the side to delete from
     */
    public static void deleteGhostPawns(String side) {

        for (Piece piece : mySide(side)
        ) {
            piece.deleteIfGhostPawn();
        }
    }

    /**
     * Returns true if and only if, who in place is a rook that is not null, and has castling rights with his king.
     *
     * @param place the location of the piece to check if it has castling rights.
     * @return does the piece has castling right with his king.
     */
    public static boolean kingHasCastlingRightsWith(Place place) {
        if (!(whoIn(place) instanceof Rook))
            return false;
        if (whoIn(place) == null)
            return false;
        return ((Rook) whoIn(place)).rightToCastling();
    }

    /**
     * A convenience method for the method {@link Board#kingHasCastlingRightsWith(Place)}.
     *
     * @param place a string that tells the location of the piece to check if it has castling rights.
     * @return does the piece has castling right with his king.
     */
    public static boolean kingHasCastlingRightsWith(String place) {
        return kingHasCastlingRightsWith(new Place(place));
    }

    /**
     * returns the king of the inserted side.
     *
     * @param side the side of the king wanted.
     * @return the king of the side, side.
     */
    public static King myKing(@NotNull String side) {
        return side.equals(WHITE) ? WHITE_KING : BLACK_KING;
    }

    /**
     * Returns the board
     *
     * @return {@code board}
     */
    public static Piece[][] getBoard() {
        return board;
    }

    /**
     * makes a schematic of the board and returns it. every piece is turned into his side plus his type.
     * for example a black pawn would be written as "black Pawn".
     *
     * @return a schematic of the board.
     */
    public static String[][] getBoardSchematics() {
        String[][] boardSchematics = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardSchematics[i][j] = whoIn(i, j) == null ? null : whoIn(i, j).SIDE + whoIn(i, j).getClass().getSimpleName();

            }

        }
        return boardSchematics;

    }

    /**
     * This is a convenience method for the method {@link Board#whoIn(int, int)}.<br>
     * tells, who is in place.
     *
     * @param place the location of the wanted piece.
     * @return the piece in place.
     */
    public static @Nullable Piece whoIn(@NotNull Place place) {
        return whoIn(place.getRank(), place.getFile());
    }

    /**
     * who in the location specified by the rank and file inserted
     *
     * @param rank the rank of the wanted piece. {@link Place#rank}
     * @param file the file of the wanted piece. {@link Place#file}
     * @return the piece in rank and file
     */
    public static @Nullable Piece whoIn(int rank, int file) {
        if (rank < 0 || file < 0 || rank > 7 || file > 7)
            return null;
        return board[rank][file];
    }

    /**
     * changes the piece in the inputted place to inputted piece. if piece != null then changes the {@code piece.place} to place
     *
     * @param place the place to change in.
     * @param piece the piece to change to, can be null.
     */
    public static void change(@NotNull Place place, Piece piece) {
        board[place.getRank()][place.getFile()] = piece;
        if (piece != null)
            piece.setPlace(place);
    }

    /**
     * Returns the piece in the num-th position in {@code whitePieces}. used for iterating over {@code whitePieces}
     *
     * @param num where in {@code whitePieces} to get
     * @return the piece in the num-th position in {@code whitePieces}.
     */
    public static Piece whiteNumber(int num) {
        return whitePieces.get(num);
    }

    /**
     * Returns the piece in the num-th position in {@code blackPieces}. used for iterating over {@code blackPieces}
     *
     * @param num where in {@code blackPieces} to get
     * @return the piece in the num-th position in {@code blackPieces}.
     */
    public static Piece blackNumber(int num) {
        return blackPieces.get(num);
    }

    /**
     * the number of white pieces. used for iterating over {@code whitePieces}.
     *
     * @return the number of white pieces.
     */
    public static int numberOfWhite() {
        return whitePieces.size();
    }

    /**
     * the number of black pieces. used for iterating over {@code blackPieces}.
     *
     * @return the number of black pieces.
     */
    public static int numberOfBlack() {
        return blackPieces.size();
    }

    /**
     * empty the board and lists of pieces.
     */
    public static void empty() {//for tests
        board = new Piece[8][8];
        whitePieces = new LinkedList<>();
        blackPieces = new LinkedList<>();
    }

    /**
     * is the place inputted by x and y isn't in the bounds of the board
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return is the place inputted by x and y isn't in the bounds of the board
     */
    public static boolean isOutOfBounds(int x, int y) {
        return 0 > x || x > 7 || 0 > y || y > 7;
    }

    /**
     * copies the list of the pieces of the opposite side
     *
     * @param side the side that is opposite to the wanted side
     * @return the pieces of the opposite side
     */
    public static @Unmodifiable List<Piece> oppositeSide(@NotNull String side) {
        return side.equals(WHITE) ? List.copyOf(blackPieces) : List.copyOf(whitePieces);
    }

    /**
     * copies the list of the pieces of the inputted side
     *
     * @param side the side of the wanted pieces
     * @return the pieces of the opposite side
     */
    public static @Unmodifiable List<Piece> mySide(@NotNull String side) {
        return side.equals(WHITE) ? List.copyOf(whitePieces) : List.copyOf(blackPieces);
    }

    /**
     * removes a piece from the board. removes the piece from the list of pieces.
     *
     * @param piece the piece to remove
     */
    public static void remove(Piece piece) {
        if (piece == null)
            return;
        List<Piece> myPieces = piece.SIDE.equals(WHITE) ? whitePieces : blackPieces;
        myPieces.remove(piece);
        Board.change(piece.getPlace(), null);
    }

    /**
     * adds a piece from the board. adds the piece from the list of pieces.
     *
     * @param piece the piece to add
     */
    public static void add(@NotNull Piece piece) {
        List<Piece> myPieces = piece.SIDE.equals(WHITE) ? whitePieces : blackPieces;
        myPieces.add(piece);
        Board.change(piece.getPlace(), piece);

    }

    /**
     * returns a symbol corresponding with the type of the pieces
     *
     * @param piece the pieces to make into a symbol
     * @return a symbol corresponding with the type of the pieces
     */
    public static @NotNull String toSymbol(Piece piece) {
        if (piece instanceof King)
            return piece.SIDE.equals(WHITE) ? "\u265A" : "\u2654";
        if (piece instanceof Queen)
            return piece.SIDE.equals(WHITE) ? "\u265B" : "\u2655";
        if (piece instanceof Rook)
            return piece.SIDE.equals(WHITE) ? "\u265C" : "\u2656";
        if (piece instanceof Bishop)
            return piece.SIDE.equals(WHITE) ? "\u265D" : "\u2657";
        if (piece instanceof Knight)
            return piece.SIDE.equals(WHITE) ? "\u265E" : "\u2658";
        if (piece instanceof Pawn)
            return piece.SIDE.equals(WHITE) ? "\u265F" : "\u2659";
        return "\u9647";
    }

    /**
     * @return the status of the game
     */
    public static String getGameStatus() {
        return gameStatus;
    }

    /**
     * updates the graphical user interface
     */
    public static void updateUI() {
        if (WHITE_KING.isEndangered())
            markCheck(WHITE_KING.place);
        if (BLACK_KING.isEndangered())
            markCheck(BLACK_KING.place);
        boardUI.update();
    }

    /**
     * marks a location with a red colour. used to mark a check
     *
     * @param place the place to mark
     */
    public static void markCheck(Place place) {
        boardUI.markCheck(place);
    }

    /**
     * returns the colour of the location to the default colour. used after a check.
     *
     * @param place the place to restore
     */
    public static void deMarkCheck(Place place) {
        boardUI.deMarkCheck(place);
    }

    /**
     * Annotates all the inputted place with a green colour. used to mark a possible move.
     *
     * @param places the places to be mark with a green colour.
     */
    public static void annotatePossibleLocations(List<Place> places) {
        boardUI.annotatePossibleLocations(places);
    }

    /**
     * restores all the inputted place to there default colour. used after a mark of possible moves.
     *
     * @param places the places to be restored to there default colour.
     */
    public static void restoreLocations(List<Place> places) {
        boardUI.restoreLocations(places);
    }


    private static boolean isWhole(float num) {
        return num == Math.ceil(num);
    }

    /**
     * closes the board. doesn't close the ui, but the player can't play after that the board is closed. prints to the console the moves.
     */
    public static void closeBoard() {
        boardUI.close();
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        int[] i = {1};
        Board.Recording.moveRecording.forEach(move -> System.out.println((i[0]++) + " " + move));
        JOptionPane.showMessageDialog(null, "The Game Ended - " + gameStatus);
    }

    /**
     * a class the represents a square in the board
     */
    private static class Square extends JButton {
        /**
         * the colour of the black squares.
         */
        public static final Color BLACK = new Color(0x6F, 0x3A, 0x25);
        /**
         * the colour of the white square
         */
        public static final Color WHITE = new Color(0xF3, 0x92, 0x64);
        public final Place PLACE;
        /**
         * the default color of this square.
         */
        public final Color color;

        /**
         * the piece that is in this square. can be null
         */
        private Piece piece;


        public Square(@NotNull Place place, int sizeOfBoard) {
            super();
            PLACE = place;
            piece = whoIn(PLACE);
            color = (place.getFile() + place.getRank()) % 2 == 0 ? BLACK : WHITE;
            setBackground(color);

            setSize((sizeOfBoard / 8), (sizeOfBoard / 8));
            setLocation(PLACE.getFile() * getHeight(), getWidth() * 7 - PLACE.getRank() * getWidth());
            if (piece != null){
                ImageIcon imageIcon = new ImageIcon("src/main/pictures/" + piece.SIDE + " " + piece.getClass().getSimpleName().toLowerCase() + ".png");
                //imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance((int)(getWidth()*0.7), (int)(getHeight()*0.7),Image.SCALE_SMOOTH));
                setIcon(imageIcon);
            }
            addActionListener(e -> Game.pressed(this.PLACE));
            setVisible(true);

        }

        /**
         * updates the square, including his size, his icon, but not his colour that is updated in other functions.
         */
        public void update() {
            piece = whoIn(PLACE);
            if (piece != null)
                setIcon(new ImageIcon("src/main/pictures/" + piece.SIDE + " " + piece.getClass().getSimpleName().toLowerCase() + ".png"));
            else
                setIcon(null);
            setSize((boardUI.getMinDimension() / 8), (boardUI.getMinDimension() / 8));
//            if (Game.getSide().equals(Board.WHITE))
            setLocation(PLACE.getFile() * getHeight(), getWidth() * 7 - PLACE.getRank() * getWidth());
//            else
//                setLocation(getHeight() * 7 - PLACE.getFile() * getHeight(), PLACE.getRank() * getWidth());

        }

        public void restoreColor() {
            setBackground(color);
        }

        public void clearActionListener() {
            this.removeActionListener(getActionListeners()[0]);
        }
    }

    private static class BoardPanel extends JRootPane {
        final Square[][] squares = new Square[8][8];

        public BoardPanel(int size) {
            super();
            setBackground(Color.black);


            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    squares[i][j] = new Square(new Place(i, j), size);
                    getContentPane().add(squares[i][j]);
                }

            }
            getContentPane().add(glassPane);
            setVisible(true);

        }


        public Square getSquareAt(Place place) {
            return squares[place.getRank()][place.getFile()];
        }
    }

    private static class UI extends JFrame {
        final BoardPanel boardPanel;
        private int minDimension;
        private final int barSize;
        UI() {
            setTitle("Meni's Chess");
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            minDimension = (int) (Math.min(screenSize.height * 0.9, screenSize.width * 0.9));
            minDimension -= minDimension % 8;
            setSize(minDimension, minDimension);
            ImageIcon icon = new ImageIcon("src/main/pictures/chess logo.png");
            this.setIconImage(icon.getImage());
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            boardPanel = new BoardPanel(minDimension);
            setRootPane(boardPanel);
            setVisible(true);
            barSize = (getBounds().height-getContentPane().getBounds().height);
            System.out.println(getRootPane().getBounds().height+" " + barSize);
            setSize(minDimension+15,minDimension+barSize);
            this.getRootPane().addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    // This is only called when the user releases the mouse button.
                    UI.this.update();
                }
            });


        }


        public Square getSquareAt(Place place) {
            return boardPanel.getSquareAt(place);
        }

        public void update() {
            minDimension = Math.min(getHeight()-barSize, getWidth()-15);
            minDimension -= minDimension % 8;
            Arrays.stream(getContentPane().getComponents()).filter(x -> x instanceof Square).forEach(x -> ((Square) x).update());
            //// TODO: 26/04/2022 make a more visually pleasing transition
        }

        public int getMinDimension() {
            return minDimension;
        }

        public void markCheck(Place place) {
            getSquareAt(place).setBackground(Color.red);
        }

        public void deMarkCheck(Place place) {
            getSquareAt(place).restoreColor();
        }

        public void annotatePossibleLocations(List<Place> possibleLocations) {
            possibleLocations.forEach(location -> getSquareAt(location).setBackground(Color.green));
        }

        public void restoreLocations(List<Place> possibleLocations) {
            possibleLocations.forEach(location -> getSquareAt(location).restoreColor());
        }

        public void close() {
            Arrays.stream(getContentPane().getComponents()).filter(x -> x instanceof Square).forEach(x -> ((Square) x).clearActionListener());

        }

    }

    public static class State {
        public final String[][] BOARD;
        public final String SIDE;
        public final boolean[] castlingRights;


        public State() {
            this.BOARD = getBoardSchematics();
            this.SIDE = Game.getSide();
            this.castlingRights = new boolean[]{kingHasCastlingRightsWith("a1"), kingHasCastlingRightsWith("a8"), kingHasCastlingRightsWith("h1"), kingHasCastlingRightsWith("h8")};

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.deepEquals(BOARD, state.BOARD) && SIDE.equals(state.SIDE) && Arrays.equals(castlingRights, state.castlingRights);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(SIDE);
            result = 31 * result + Arrays.deepHashCode(BOARD);
            result = 31 * result + Arrays.hashCode(castlingRights);
            return result;
        }
    }

    public static class Recording {
        private static final List<State> recordingOfBoards = new ArrayList<>(List.of(new State()));
        private static final List<String> moveRecording = new ArrayList<>();
        private static int numOfMovesFromPawnMovingOrPieceCaptured = 0;

        /**
         * updates the recording of the prior boards, clears recording if non-retractable change was made. <br>
         * if a pawn was moved - clear in the moveTo method {@link Pawn#moveTo}.<br>
         * if a piece was captured - clear in the captured method {@link Piece#captured}.<br>
         * if a castling right was lost - clear in the rook's moveTo method {@link Rook#moveTo}.<br>
         * if a pawn was promoted - not needed because pawn can be promoted, only if he was moved.<br>
         * add the current state and check if there are two identical to him, and heck if there were two identical the
         * current position - if there are change the game status to "threefold - draw"
         */
        public static void update() {
            recordingOfBoards.add(new State());
            if (recordingOfBoards.stream().filter(recordingOfBoards.get(recordingOfBoards.size() - 1)::equals).count() >= 3) {
                gameStatus = "threefold - draw";
            }
        }

        public static void updateMoveRecording(@NotNull Place fromWhere, @NotNull Place toWhere, Piece piece) {
            moveRecording.add(toSymbol(piece) + fromWhere + toWhere);
        }


        public static void resetCount() {
            numOfMovesFromPawnMovingOrPieceCaptured = -1;
        }


        public static void clear() {
            recordingOfBoards.clear();
        }


        public static void updateCount() {
            numOfMovesFromPawnMovingOrPieceCaptured++;
            if (numOfMovesFromPawnMovingOrPieceCaptured >= 100)//over 100 hundred because counting both sides
                gameStatus = "fifty moves from the last time the a pawn was moved, or a piece was captured - draw";

        }
    }


}
