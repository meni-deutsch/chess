package board;

import org.jetbrains.annotations.NotNull;
import board.Place;
import board.Board;
import board.Piece;

import java.util.Objects;

public class Place implements Comparable<Place> {
    public final int rank;
    public final int file;

    public Place(int rank, int file) {
        this.rank = rank;
        this.file = file;
    }

    public Place(@NotNull String place) {
        this.file = place.charAt(0) - 97;
        this.rank = place.charAt(1) - 49;
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return rank == place.rank && file == place.file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, file);
    }

    @Override
    public String toString() {
        return "" + (char) (file + 97) + (rank + 1);
    }


    boolean isEndangered(Side side, Board board) {
        for (Piece piece : board.oppositeSide(side)) {
            if (piece.makeWhereIAttack().stream().anyMatch(this::equals))
                return true;
        }
        return false;
    }

    public boolean isOutOfBounds() {
        return Board.isOutOfBounds(rank, file);
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NotNull Place o) {
        return this.toString().compareTo(o.toString());
    }



}
