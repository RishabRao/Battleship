import java.util.Random;

public class  HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
        populateShips(); // populates ships
    }

    /**
     * Attack the specified Location loc.  Marks
     * the attacked Location on the guess board
     * with a positive number if the enemy Player
     * controls a ship at the Location attacked;
     * otherwise, if the enemy Player does not
     * control a ship at the attacked Location,
     * guess board is marked with a negative number.
     * <p>
     * If the enemy Player controls a ship at the attacked
     * Location, the ship must add the Location to its
     * hits taken.  Then, if the ship has been sunk, it
     * is removed from the enemy Player's list of ships.
     * <p>
     * Return true if the attack resulted in a ship sinking;
     * false otherwise.
     *
     * @param enemy
     * @param loc
     * @return
     */
    @Override
    public boolean attack(Player enemy, Location loc) {

        if(enemy.hasShipAtLocation(loc)) { // checks to avoid null pointer

            enemy.getShip(loc).takeHit(loc);
            getGuessBoard()[loc.getRow()][loc.getCol()] = 1;

            if(enemy.getShip(loc).isSunk()) {
                enemy.removeShip(enemy.getShip(loc));
                return true;
            }

        }

        else {
            getGuessBoard()[loc.getRow()][loc.getCol()] = -1;
        }
        return false;
    }

    /**
     * Construct an instance of
     * <p>
     * AircraftCarrier,
     * Destroyer,
     * Submarine,
     * Cruiser, and
     * PatrolBoat
     * <p>
     * and add them to this Player's list of ships.
     */
    @Override
    public void populateShips() {
        addShip(new AircraftCarrier(new Location(4, 2),
                new Location(5, 2), new Location(6, 2),
                new Location(7, 2), new Location(8, 2)));

        addShip(new Cruiser(new Location(2, 6),
                new Location(2, 7),
                new Location(2, 8)));

        addShip(new Destroyer(new Location(0, 0),
                new Location(1, 0),
                new Location(2, 0),
                new Location(3, 0)));

        addShip(new PatrolBoat(new Location(9, 7),
                new Location(9, 8)));

        addShip(new Submarine(new Location(6, 6),
                new Location(6, 7),
                new Location(6, 8)));
    }
}
