package board;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static board.Side.WHITE;
class GhostPawn extends Piece {

    public final Pawn ORIGINAL_PAWN;

    public GhostPawn(@NotNull Pawn pawn) {
        super(pawn.SIDE, new Place(pawn.getRank() + (pawn.SIDE.equals(WHITE) ? 1 : -1), pawn.getFile()));
        ORIGINAL_PAWN = pawn;
        board.add(this);
    }

    @Override
    public List<Place> makeWhereIAttack() {
        return Collections.emptyList();
    }

    public void disappear() {
        captured(null);
    }

    @Override
    public void captured(Piece eater) {
        if (eater instanceof Pawn)
            ORIGINAL_PAWN.captured(null);
        super.captured(eater);
    }

    @Override
    public void whereCanIMove() {
        whereCanIMove = Collections.emptyList();
    }

    @Override
    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        return whereCanIMoveWithoutCaringForTheKing = Collections.emptyList();
    }

    public void deleteIfGhostPawn(){
        disappear();
    }

}
