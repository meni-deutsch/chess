package board;

import org.jetbrains.annotations.NotNull;

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


    public boolean equals(@NotNull Place p) {
        return this.rank == p.getRank() && this.file == p.getFile();
    }

    @Override
    public String toString() {
        return "" + (char) (file + 97) + (rank + 1);
    }


    public boolean isEndangered(String side) {
        for (Piece piece : Board.oppositeSide(side)) {
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
