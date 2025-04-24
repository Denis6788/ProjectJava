
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleTest {
    private Vehicle horizontalVehicle;
    private Vehicle verticalVehicle;

    @BeforeEach
    void setUp() {
        horizontalVehicle = new Vehicle('H', new Position(2, 0), new Position(2, 1));
        verticalVehicle = new Vehicle('V', new Position(0, 3), new Position(1, 3));
    }

    @Test
    void testVehicleCreation() {
        assertEquals('H', horizontalVehicle.getSymbol());
        assertEquals(2, horizontalVehicle.getBack().getRow());
        assertEquals(0, horizontalVehicle.getBack().getCol());
        assertEquals(2, horizontalVehicle.getFront().getRow());
        assertEquals(1, horizontalVehicle.getFront().getCol());
    }

    @Test
    void testHorizontalMoveRight() throws RushHourException {
        horizontalVehicle.move(Direction.RIGHT);
        assertEquals(1, horizontalVehicle.getBack().getCol());
        assertEquals(2, horizontalVehicle.getFront().getCol());
    }

    @Test
    void testHorizontalMoveLeft() throws RushHourException {
        horizontalVehicle.move(Direction.LEFT);
        assertEquals(-1, horizontalVehicle.getBack().getCol());
        assertEquals(0, horizontalVehicle.getFront().getCol());
    }

    @Test
    void testInvalidHorizontalMove() {
        assertThrows(RushHourException.class, () -> horizontalVehicle.move(Direction.UP));
        assertThrows(RushHourException.class, () -> horizontalVehicle.move(Direction.DOWN));
    }

    @Test
    void testVerticalMoveUp() throws RushHourException {
        verticalVehicle.move(Direction.UP);
        assertEquals(-1, verticalVehicle.getBack().getRow());
        assertEquals(0, verticalVehicle.getFront().getRow());
    }

    @Test
    void testVerticalMoveDown() throws RushHourException {
        verticalVehicle.move(Direction.DOWN);
        assertEquals(1, verticalVehicle.getBack().getRow());
        assertEquals(2, verticalVehicle.getFront().getRow());
    }

    @Test
    void testInvalidVerticalMove() {
        assertThrows(RushHourException.class, () -> verticalVehicle.move(Direction.LEFT));
        assertThrows(RushHourException.class, () -> verticalVehicle.move(Direction.RIGHT));
    }
    @Test
    void testMoveBeyondBoundary() {
        Vehicle edgeVehicle = new Vehicle('E', new Position(0, 0), new Position(0, 0));
        assertThrows(RushHourException.class, () -> edgeVehicle.move(Direction.LEFT));
    }

    @Test
    void testRedCarAtExit() {
        Vehicle redCar = new Vehicle('R', new Position(2, 4), new Position(2, 5));
        assertTrue(redCar.getFront().equals(new Position(2, 5)));
    }
}
