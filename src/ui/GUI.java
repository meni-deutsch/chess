package ui;

import board.Controller;
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
    private final int barSize;
    private int minDimension;


    private GUI() {
        setTitle("Meni's Chess");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        minDimension = (int) (Math.min(screenSize.height * 0.8, screenSize.width * 0.8));
        minDimension -= minDimension % 8;
        setSize(minDimension, minDimension);
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/chess logo.png")));
        this.setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setLocation(getX(), (int) (screenSize.height * 0.1));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        boardPanel = new BoardPanel(minDimension);
        setRootPane(boardPanel);
        setVisible(true);
        barSize = (getBounds().height - getContentPane().getBounds().height);
        System.out.println(getRootPane().getBounds().height + " " + barSize);
        setSize(minDimension, minDimension + barSize);
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
                update();
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Controller.printMoveRecording();
            }
        });
    }


    @SuppressWarnings("EmptyMethod")
    public static void main(String[] args) {
    }

    boolean gameContinues() {
        return Controller.getGameStatus().equals("on going");
    }

    Square getSquareAt(@NotNull Place place) {
        return boardPanel.getSquareAt(place);
    }

    void update() {
        minDimension = Math.min(getHeight() - barSize, getWidth());
        minDimension -= minDimension % 8;
        Arrays.stream(getContentPane().getComponents()).filter(x -> x instanceof Square).forEach(x -> ((Square) x).update());
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
        Controller.printMoveRecording();
        JOptionPane.showMessageDialog(null, "The Game Ended - " + Controller.getGameStatus());
    }

    public void pressed(int rank, int file) {
        if (Controller.isPieceChosen()) {
            if (Controller.movePieceTo(rank, file)) {
                if (Controller.isReadyToPromote()) {
                    Object[] menu = {
                            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/" + Controller.getSide() + " " + "queen" + ".svg.png"))),
                            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/" + Controller.getSide() + " " + "rook" + ".svg.png"))),
                            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/" + Controller.getSide() + " " + "bishop" + ".svg.png"))),
                            new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/" + Controller.getSide() + " " + "knight" + ".svg.png")))
                    };
                    switch (JOptionPane.showOptionDialog(null, "", "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, menu, menu[0])) {
                        case 1 -> Controller.promote('r');
                        case 2 -> Controller.promote('b');
                        case 3 -> Controller.promote('n');
                        default -> Controller.promote('q');
                    }
                }

                updateLocations();
                EventQueue.invokeLater(this::update);
                if (Controller.getEndangeredKing() != null) {
                    EventQueue.invokeLater(() -> updateEndangered(Controller.getEndangeredKing()));
                }


                Controller.changeTurn();
                if (!gameContinues())
                    close();
                return;
            }
            restoreLocations(Controller.getAvailablePlaces());
        }

        if (Controller.setMovingPiece(rank, file))

            annotatePossibleLocations(Controller.getAvailablePlaces());


    }


    private void updateLocations() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPanel.squares[i][j].update(Controller.getPieceTypeAt(i, j), Controller.getPieceSideAt(i, j));
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
                    squares[i][j] = new Square(i, j, size, Controller.getPieceTypeAt(i, j), Controller.getPieceSideAt(i, j));
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
