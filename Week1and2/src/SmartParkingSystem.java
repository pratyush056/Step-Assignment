import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        licensePlate = null;
        entryTime = 0;
        occupied = false;
    }
}

class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int operations = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();
    }

    private int hash(String license) {
        return Math.abs(license.hashCode()) % capacity;
    }

    public void parkVehicle(String license) {

        int index = hash(license);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = license;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedCount++;
        totalProbes += probes;
        operations++;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String license) {

        int index = hash(license);

        while (table[index].occupied) {

            if (license.equals(table[index].licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long duration = exitTime - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                occupiedCount--;

                System.out.println("Spot #" + index + " freed");
                System.out.println("Duration: " + String.format("%.2f", hours) + " hours");
                System.out.println("Fee: $" + String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    public void getStatistics() {

        double occupancy = (occupiedCount * 100.0) / capacity;
        double avgProbes = operations == 0 ? 0 : (double) totalProbes / operations;

        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
    }
}

public class SmartParkingSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ParkingLot lot = new ParkingLot(500);

        System.out.println("Commands:");
        System.out.println("1 licensePlate → Park");
        System.out.println("2 licensePlate → Exit");
        System.out.println("3 → Statistics");

        while (true) {

            int command = sc.nextInt();

            if (command == 1) {

                String plate = sc.next();
                lot.parkVehicle(plate);

            } else if (command == 2) {

                String plate = sc.next();
                lot.exitVehicle(plate);

            } else if (command == 3) {

                lot.getStatistics();
            }
        }
    }
}