package ui;

import board.Board;
import board.Place;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

class GUI extends JFrame {

    public static final GUI instance = new GUI();
    final BoardPanel boardPanel;
    final Board board = Board.newInstance();
    private final int barSize;
    private int minDimension;

    private Place chosenPlace = null;

    private GUI() {
        setTitle("Meni's Chess");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        minDimension = (int) (Math.min(screenSize.height * 0.9, screenSize.width * 0.9));
        minDimension -= minDimension % 8;
        setSize(minDimension, minDimension);
        ImageIcon icon = new ImageIcon("src/ui/pictures/chess logo.png");
        this.setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        boardPanel = new BoardPanel(minDimension);
        setRootPane(boardPanel);
        setVisible(true);
        barSize = (getBounds().height - getContentPane().getBounds().height);
        System.out.println(getRootPane().getBounds().height + " " + barSize);
        setSize(minDimension + 15, minDimension + barSize);
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
                update();
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                board.printMoveRecording();
            }
        });
    }



    @SuppressWarnings("EmptyMethod")
    public static void main(String[] args) {}

    boolean gameContinues() {
        return board.getGameStatus().equals("on going");
    }

    Square getSquareAt(@NotNull Place place) {
        return boardPanel.getSquareAt(place);
    }

    void update() {
        minDimension = Math.min(getHeight() - barSize, getWidth() - 15);
        minDimension -= minDimension % 8;
        Arrays.stream(getContentPane().getComponents()).filter(x -> x instanceof Square).forEach(x -> ((Square) x).update(board));
        //// TODO: 26/04/2022 make a more visually pleasing transition
    }

    int getMinDimension() {
        return minDimension;
    }


    public void annotatePossibleLocations(java.util.List<Place> possibleLocations) {
        possibleLocations.forEach(location -> getSquareAt(location).selected());
    }

    public void restoreLocations(List<Place> possibleLocations) {
        possibleLocations.forEach(location -> getSquareAt(location).restoreColor());
    }

    /**
     * closes the board. doesn't close the ui, but the player can't play after that the board is closed. prints to the console the moves.
     */
    public void close() {
        Arrays.stream(getContentPane().getComponents()).filter(x -> x instanceof Square).forEach(x -> ((Square) x).clearActionListener());
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        board.printMoveRecording();
        JOptionPane.showMessageDialog(null, "The Game Ended - " + board.getGameStatus());
    }

    public void pressed(int rank, int file) {
        if (chosenPlace != null && board.getAvailablePlaces(chosenPlace.rank, chosenPlace.file).contains(new Place(rank, file))) {
            Place endangeredPlace = board.move(chosenPlace.rank, chosenPlace.file, rank, file);
            updateLocations();
            if (endangeredPlace != null) {
                updateEndangered(endangeredPlace);
            }
            board.changeTurn();
            chosenPlace = null;
            EventQueue.invokeLater(this::update);
            if (!gameContinues())
                close();
            return;

        }
        if (chosenPlace != null) {
            restoreLocations(board.getAvailablePlaces(chosenPlace.rank, chosenPlace.file));
            chosenPlace = null;
        }
        if(board.getSide() == board.getPieceSideAt(rank, file)) {
            chosenPlace = new Place(rank, file);
            annotatePossibleLocations(board.getAvailablePlaces(rank, file));
        }
    }



    private void updateLocations() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPanel.squares[i][j].update(board.getPieceTypeAt(i, j), board.getPieceSideAt(i, j));
            }
        }
    }

    private void updateEndangered(@NotNull Place endangeredPlace) {
        getSquareAt(endangeredPlace).endangered();
    }

    private class BoardPanel extends JRootPane {
        final Square[][] squares = new Square[8][8];

        public BoardPanel(int size) {
            super();
            setBackground(Color.black);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    squares[i][j] = new Square(i, j, size, board.getPieceTypeAt(i, j), board.getPieceSideAt(i, j));
                    getContentPane().add(squares[i][j]);
                }

            }
            getContentPane().add(glassPane);
            setVisible(true);

        }


        public Square getSquareAt(@NotNull Place place) {
            return squares[place.getRank()][place.getFile()];
        }


    }


}
