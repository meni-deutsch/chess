package board;

public interface IBoardCommands {
    class Update{
        public Place location;
        public boolean isEndangered;
        /**
         * q for Queen
         * b for Bishop
         * k for King
         * p for Pawn
         * r for Rook
         * space for empty slot
         */
        public char pieceType;

        public Update(Place location, boolean isEndangered, char pieceType) {
            this.location = location;
            this.isEndangered = isEndangered;
            this.pieceType = pieceType;
        }
    }
    int getGameStatus();
    Place[] getAvailablePlaces(int Rank,int File);
    Update[] move(int fromRank, int fileFile, int toRank, int toFile);

}
