package com.example;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MoveTest {
    
    @Test
    void testMoveCreation() {
        Move move = new Move('A', Direction.UP);
        assertEquals('A', move.getSymbol());
        assertEquals(Direction.UP, move.getDirection());
    }
}
