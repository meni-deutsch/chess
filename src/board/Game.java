package board;


import static board.Side.*;
class Game {
    private static Side side = WHITE;
    private static final Board board = Board.getInstance();


    static void changeTurn(){
        side = side.equals(WHITE) ? BLACK : WHITE;
        board.updateWhereCanThePiecesGo(side);
        checkGameStatus();

    }
    static void checkGameStatus() {
        board.updateRecording();
        board.updateMoveCount();
        board.isDeadPosition();
        board.checkIfPiecesCanMove(side);
    }

    public static Side getSide() {
        return side;
    }












}
