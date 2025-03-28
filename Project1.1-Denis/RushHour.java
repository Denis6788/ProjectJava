
import java.util.HashMap;
import java.util.Map;

public class RushHour {
    private Map<Character, Vehicle> vehicles;

    public RushHour() {
        vehicles = new HashMap<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getSymbol(), vehicle);
    }

    public void moveVehicle(Move move) throws RushHourException {
        Vehicle vehicle = vehicles.get(move.getSymbol());
        if (vehicle == null) {
            throw new RushHourException("Vehicle not found!");
        }
        vehicle.move(move.getDirection());
    }
}
