package board;


public enum Side {
    WHITE, BLACK;

    public String toString() {
        return this == WHITE ? "white" : "black";
    }

    public Side oppositeSide() {
        return this == WHITE ? BLACK : WHITE;
    }
}


