
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PositionTest {
    
    @Test
    void testPositionCreation() {
        Position pos = new Position(2, 3);
        assertEquals(2, pos.getRow());
        assertEquals(3, pos.getCol());
    }
}
