package ui;


import board.Controller;
import board.Side;

import javax.swing.*;
import java.awt.*;


/**
 * a class the represents a square in the board
 */
class Square extends JButton {
    public static final Color SELECTED_COLOR = Color.GREEN;
    public static final Color WARNING_COLOR = Color.RED;

    //

    /**
     * the colour of the black squares.
     */
    public static final Color BLACK = new Color(0x6F, 0x3A, 0x25);
    /**
     * the colour of the white square
     */
    @SuppressWarnings("unused")
    public static final Color OLD_WHITE = new Color(0xF3, 0x92, 0x64);
    public static final Color WHITE = new Color(255, 170, 109);

    // colours for blue board
    @SuppressWarnings("unused")
    public static final Color BLUE_BLACK = new Color(20, 40, 120);
    @SuppressWarnings("unused")
    public static final Color BLUE_WHITE = new Color(50, 255, 255, 255);


    public final int rank;
    public final int file;
    /**
     * the default color of this square.
     */
    public final Color color;
    private boolean endangered;


    public Square(int rank, int file, int sizeOfBoard, char pieceType, Side side) {
        super();
        this.rank = rank;
        this.file = file;
        String pieceTypeName = switch (pieceType) {
            case 'b' -> "bishop";
            case 'k' -> "king";
            case 'n' -> "knight";
            case 'p' -> "pawn";
            case 'q' -> "queen";
            case 'r' -> "rook";
            case ' ' -> "";
            default -> throw new IllegalArgumentException("pieceType must be b,k,n,p,q,r or space");
        };

        color = (rank + file) % 2 == 0 ? BLACK : WHITE;
        setBackground(color);

        setSize((sizeOfBoard / 8), (sizeOfBoard / 8));
        setLocation(file * getHeight(), getWidth() * 7 - rank * getWidth());
        if (!pieceTypeName.isEmpty()) {
            if (side == null) {
                throw new IllegalArgumentException("side must be white or black when pieceType is not empty");
            }
            ImageIcon imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/"+ side + " " + pieceTypeName + ".svg.png")));
            imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance((int) (getWidth() * 0.65), (int) (getHeight() * 0.65), Image.SCALE_SMOOTH));
            setIcon(imageIcon);
        }
        addActionListener(e -> GUI.instance.pressed(rank, file));
        setVisible(true);

    }

    /**
     * updates the square, including his size, his icon, but not his colour that is updated in other functions.
     */
    public void updateIcon(char newPieceType, Side newSide) {
        String pieceTypeName = switch (newPieceType) {
            case 'b' -> "bishop";
            case 'k' -> "king";
            case 'n' -> "knight";
            case 'p' -> "pawn";
            case 'q' -> "queen";
            case 'r' -> "rook";
            case ' ' -> "";
            default -> null;
        };
        if (pieceTypeName == null)
            throw new IllegalArgumentException("pieceType must be b,k,n,p,q,r or space");

        if (!pieceTypeName.isEmpty()) {

            ImageIcon imageIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pictures/"+ newSide + " " + pieceTypeName + ".svg.png")));
            if ((int) (getWidth() * 0.7) == 0 || (int) (getHeight() * 0.7) == 0)
                imageIcon = null;
            else
                imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance((int) (getWidth() * 0.65), (int) (getHeight() * 0.65), Image.SCALE_SMOOTH));

            setIcon(imageIcon);
        } else
            setIcon(null);
    }

    public void updateSize() {
        setSize((GUI.instance.getMinDimension() / 8), (GUI.instance.getMinDimension() / 8));
//            if (Game.getSide().equals(Board.WHITE))
        setLocation(file * getHeight(), getWidth() * 7 - rank * getWidth());
//            else
//                setLocation(getHeight() * 7 - PLACE.getFile() * getHeight(), PLACE.getRank() * getWidth());

    }

    public void update(char newPieceType, Side newSide) {
        updateIcon(newPieceType, newSide);
        updateSize();
        restoreColor();
    }

    public void update() {
        update(Controller.getPieceTypeAt(rank, file), Controller.getPieceSideAt(rank, file));
    }

    public void restoreColor() {
        setBackground(color);

    }

    public void selected() {
        setBackground(SELECTED_COLOR);
    }

    public void endangered() {
        setBackground(WARNING_COLOR);
    }


    public void clearActionListener() {
        this.removeActionListener(getActionListeners()[0]);
    }


}