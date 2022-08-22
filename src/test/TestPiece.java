package test;

import static board.Board.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestPiece {




    @BeforeAll
    static void setup(){
        makeBoard();
    }
    @BeforeEach
    void clear(){
        empty();
    }
    @Test
    void testWhereCanIMoveWithoutHurtingTheKing() {}
    @Test
    void testCanIEatAt(){}

    @Test
    void testMakeWhereCanIEat(){}

    @Test
    void testMoveTo(){}//might need more than one test function

    //makeWhereCanIMoveTestInSubclasses

    /**
     *
     */
    @MethodSource()
    @ParameterizedTest
    static void testMakeWhereIAttack(){}
}
