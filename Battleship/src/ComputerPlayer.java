import java.util.ArrayList;
import java.util.Random;

public class ComputerPlayer extends Player {

    private boolean hasPlacedSub;
    private int[][] placementBoard;

    private ArrayList<Location> hits;
    private ArrayList<Location> possibleTargets;

    public ComputerPlayer(String name) {
        super(name);
        hasPlacedSub = false;
        placementBoard = new int[10][10];
        possibleTargets = new ArrayList<>();
        hits = new ArrayList<>();
        populateShips(); // populates ships
    }

    /**
     * Randomly chooses a Location that has not been
     * attacked (Location loc is ignored). Marks
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
     * @param enemy The Player to attack.
     * @param loc   The Location to attack.
     * @return
     */
    @Override
    public boolean attack(Player enemy, Location loc) {

        refreshTargets();

        if(possibleTargets.size() > 0) {
            Location guess = getPossibleGuess();

            int row = guess.getRow();
            int col = guess.getCol();

            if (getGuessBoard()[row][col] == 0) {

                if (enemy.hasShipAtLocation(guess)) {

                    enemy.getShip(guess).takeHit(guess);
                    getGuessBoard()[guess.getRow()][guess.getCol()] = 1;
                    hits.add(guess);

                    if (enemy.getShip(guess).isSunk()) {
                        enemy.removeShip(enemy.getShip(guess));
                        return true;
                    }

                }

                else {
                    getGuessBoard()[guess.getRow()][guess.getCol()] = -1;
                }
            }
        }

        else {
            Random randomHit = new Random();

            boolean hasTakenShot = false;

            while (!hasTakenShot) { // checks if you have taken a shot in case you need to repeat

                int row = randomHit.nextInt(10);
                int col = randomHit.nextInt(10);

                Location location = new Location(row, col);

                if (getGuessBoard()[row][col] == 0) {
                    if (enemy.hasShipAtLocation(location)) {
                        enemy.getShip(location).takeHit(location);
                        getGuessBoard()[location.getRow()][location.getCol()] = 1;
                        hits.add(location);

                        if (enemy.getShip(location).isSunk()) {
                            enemy.removeShip(enemy.getShip(location));
                            return true;
                        }

                    } else {
                        getGuessBoard()[location.getRow()][location.getCol()] = -1;
                    }

                    hasTakenShot = true;
                }
            }
            return false;
        }
        return false;
    }

    public Location getPossibleGuess() {
        Location guess = new Location(possibleTargets.get(0).getRow(), possibleTargets.get(0).getCol());
        possibleTargets.remove(0);

        return guess;
    }

    public void refreshTargets() {
        int[] rotations = {0, 90, 180, 270};
        for (Location hit : hits) {
            for (int rotation : rotations) {
                int[] steps = calculateSteps(rotation);
                int row_step = steps[0];
                int col_step = steps[1];
                try {
                    if (getGuessBoard()[hit.getRow() + row_step][hit.getCol() + col_step] == 0) {
                        possibleTargets.add(new Location(hit.getRow() + row_step, hit.getCol() + col_step));
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
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

        addShip(new AircraftCarrier(new Location(0, 0),
                new Location(0, 1), new Location(0, 2),
                new Location(0, 3), new Location(0, 4)));

        addShip(new Cruiser(new Location(9, 0),
                new Location(9, 1),
                new Location(9, 2)));

        addShip(new Destroyer(new Location(9, 3),
                new Location(9, 4),
                new Location(9, 5),
                new Location(9, 6)));

        addShip(new PatrolBoat(new Location(9, 7),
                new Location(9, 8)));

        addShip(new Submarine(new Location(6, 6),
                new Location(6, 7),
                new Location(6, 8)));
//        int[] rotations = {0, 90, 180, 270};
//
//        int maxAttempts = 100;
//
//        Random random = new Random();
//
//        int[] lenghts = {5, 4, 3, 3, 2};
//
//        for(int length : lenghts) {
//            int attempts = 0;
//            while (attempts < maxAttempts) {
//
//                int row = random.nextInt(10);
//                int col = random.nextInt(10);
//                int rotation = rotations[random.nextInt(4)];
//
//                if(placeToBoard(row, col, length, rotation)) {
//                    break;
//                }
//
//                attempts++;
//            }
//
//        }
    }

    public boolean placeToBoard(int row, int col, int length, int rotation) {

        boolean free;
        int[] steps = calculateSteps(rotation);

        int row_step = steps[0];
        int col_step = steps[1];

        ArrayList<Location> cells = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            int _row = i * row_step + row;
            int _col = i * col_step + col;
            cells.add(new Location(_row, _col));
        }

        try {
            free = true;
            for (Location location : cells) {
                if (placementBoard[location.getRow()][location.getCol()] != 0) {
                    free = false;
                    break;
                }
            }
        } catch (Exception ignored) {
            free = false;
        }

        if (free) {
            for (Location location : cells) {
                if (placementBoard[location.getRow()][location.getCol()] == 0) {
                    placementBoard[location.getRow()][location.getCol()] = 1;
                }
            }

            switch (length) {
                case 5:
                    addShip(new AircraftCarrier(cells.toArray(new Location[5])));

                    break;
                case 4:
                    addShip(new Destroyer(cells.toArray(new Location[4])));
                    break;
                case 3:
                    if(!hasPlacedSub) {
                        addShip(new Submarine(cells.toArray(new Location[3])));
                        hasPlacedSub = true;
                    }
                    else {
                        addShip(new Cruiser(cells.toArray(new Location[3])));
                    }
                    break;
                case 2:
                    addShip(new PatrolBoat(cells.toArray(new Location[2])));
                    break;
            }

        }

        return free;
    }

    public int[] calculateSteps(int rotation) {

        int row_step;
        int col_step;

        switch (rotation) {
            case 90:
                row_step = -1;
                col_step = 0;
                break;
            case 180:
                row_step = 0;
                col_step = -1;
                break;
            case 270:
                row_step = 1;
                col_step = 0;
                break;
            default:
                row_step = 0;
                col_step = 1;
                break;
        }

        return new int[]{row_step, col_step};
    }
}
