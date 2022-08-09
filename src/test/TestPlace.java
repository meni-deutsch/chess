package test;

import main.Place;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPlace {
    Place place;

    @BeforeEach
    void setUp() {
        place = new Place(0, 0);
    }

    @Test
    void testIfToStringEqualsToTheInsertedString() {
        place = new Place("e4");
        Assertions.assertEquals("e4", place.toString());
    }

    @Test
    void testIfToStringEqualsToTheInsertedInput() {
        place = new Place(3, 4);
        Assertions.assertEquals("e4", place.toString());
    }
    @Test
    void isEndangered(){

    }


}
