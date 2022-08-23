package board;

import java.util.List;
public interface IBoardCommands {


    String getGameStatus();
    List<Place> getAvailablePlaces(int rank,int rile);

    /**
     *
     * @param fromRank -
     * @param fileFile -
     * @param toRank -
     * @param toFile-
     * @return the location of the endangered king, if there is no endangered king, return null
     */
    Place move(int fromRank, int fileFile, int toRank, int toFile);

    /**
     * @return piece type at input
     * q for Queen
     * b for Bishop
     * k for King
     * p for Pawn
     * r for Rook
     * n for knight
     * space for empty slot
     */
    char getPieceTypeAt(int rank, int file);

    Side getPieceSideAt(int rank, int file);
}
