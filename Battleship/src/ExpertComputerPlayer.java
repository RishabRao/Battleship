import java.util.*;
import java.util.concurrent.*;

public class ExpertComputerPlayer extends Player {

    private HashMap<Ship, Integer> remainingShips;
    private int[][] frequencyTable;
    private int[][] placementBoard;

    private int sampleSize;

    private int hitsGivenToTarget;

    private int hitsRemaining;
    private int consecutiveMisses;

    private boolean hasPlacedSub;
    private boolean landedSecondHit;

    private boolean hasTimedOut;
    private boolean shouldEnterErrorState;
    private boolean hitMultipleTargets;

    private ArrayList<Location> hits;
    private ArrayList<Location> allHits;
    private ArrayList<LocationWithFrequency> possibleTargets;

    private int moves;

    public ExpertComputerPlayer(String name) {
        super(name);
        remainingShips = new HashMap<>();

        remainingShips.put(new AircraftCarrier(), 5);
        remainingShips.put(new Cruiser(), 3);
        remainingShips.put(new Destroyer(), 4);
        remainingShips.put(new PatrolBoat(), 2);
        remainingShips.put(new Submarine(), 3);

        hasPlacedSub = false;

        sampleSize = 150000;

        frequencyTable = new int[10][10];
        placementBoard = new int[10][10];

        hitsGivenToTarget = 0;
        hitsRemaining = 17;

        hits = new ArrayList<>();
        allHits = new ArrayList<>();
        possibleTargets = new ArrayList<>();

        hitMultipleTargets = false;
        hasTimedOut = false;
        shouldEnterErrorState = false;

        moves = 0;

        populateShips();

    }

    @Override
    public boolean attack(Player enemy, Location loc) {

        final boolean[] attackResult = new boolean[1];

        final Runnable attackAlgorithm = new Thread() {
            @Override
            public void run() {
                attackResult[0] = attackAlgorithem(enemy);
            }
        };

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(attackAlgorithm);
        executor.shutdown();
        try {
            future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException ignored) {

        } catch (TimeoutException | NullPointerException e) {
            hasTimedOut = true;
        }

        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }

        return attackResult[0];
    }

    private boolean attackAlgorithem(Player enemy) {
        moves++;
//        System.out.println(moves);
        int smallestTargetHits = 99999;

        for (Ship ship : remainingShips.keySet()) {
            if (remainingShips.get(ship) < smallestTargetHits) {
                smallestTargetHits = remainingShips.get(ship);
            }
        }

        if (hitsRemaining < smallestTargetHits && consecutiveMisses > 3) {
            shouldEnterErrorState = true;
        }

        LocationWithFrequency target;

        if (hasTimedOut || shouldEnterErrorState) {
            target = getErrorStateGuess();
        } else {
            if (hits.size() > 0) {
                target = getTargetingGuess();
            } else {
                target = getHuntingGuess();
            }
        }
        if (enemy.hasShipAtLocation(target.getLocation())) {
            getGuessBoard()[target.getRow()][target.getCol()] = 1;
            enemy.getShip(target.getLocation()).takeHit(target.getLocation());

            hitMultipleTargets = false;

            hitsGivenToTarget++;
            hitsRemaining--;
            consecutiveMisses = 0;
            hits.add(target.getLocation());
            allHits.add(target.getLocation());

            if (hitsGivenToTarget > 1) {
                landedSecondHit = true;
            }

            if (enemy.getShip(target.getLocation()).isSunk()) {

                int length = enemy.getShip(target.getLocation()).getLocations().size();

                enemy.removeShip(enemy.getShip(target.getLocation()));

                for (Ship ship : remainingShips.keySet()) {
                    if (remainingShips.get(ship) == length) {
                        remainingShips.remove(ship);
                        break;
                    }
                }

                if (length < hitsGivenToTarget) {
                    hitMultipleTargets = true;
                } else {
                    hits = new ArrayList<>();
                }

                hitsGivenToTarget = 0;
                possibleTargets = new ArrayList<>();
                landedSecondHit = false;
            }

        } else {
            getGuessBoard()[target.getRow()][target.getCol()] = -1;
            consecutiveMisses++;
        }
        return false;
    }

    /**
     * Uses monte carlo
     *
     * @return
     */
    private LocationWithFrequency getHuntingGuess() {
        refreshFrequencyTable();
        return findMax(frequencyTable);
    }

    /**
     * Uses when already has hit
     *
     * @return
     */
    private LocationWithFrequency getTargetingGuess() {
        refreshFrequencyTable();
        refreshPossibleTargets();

        int maxVal = -999;
        LocationWithFrequency targetLocation = null;
        for (LocationWithFrequency target : possibleTargets) {
            if (target.getFrequency() > maxVal) {
                targetLocation = target;
            }
        }
        if (landedSecondHit || hitMultipleTargets) {
            int row = -1;
            int col = -1;
            if (hits.get(0).getRow() == hits.get(1).getRow()) {
                row = hits.get(0).getRow();
            }
            if (hits.get(0).getCol() == hits.get(1).getCol()) {
                col = hits.get(0).getCol();
            }
            maxVal = -999;
            for (LocationWithFrequency target : possibleTargets) {
                if (row != -1) {
                    if (target.getFrequency() > maxVal && target.getRow() == row) {
                        targetLocation = target;
                    }
                }
                if (col != -1) {
                    if (target.getFrequency() > maxVal && target.getCol() == col) {
                        targetLocation = target;
                    }
                }
            }

        }
        possibleTargets.remove(targetLocation);
        return targetLocation;
    }

    private LocationWithFrequency getErrorStateGuess() {
        refreshTotalPossibleTargets();

        LocationWithFrequency targetGuess = new LocationWithFrequency(possibleTargets.get(0).getRow(), possibleTargets.get(0).getCol(), -1, -1);
        possibleTargets.remove(0);

        return targetGuess;
    }

    private void refreshPossibleTargets() {
        int[] rotations = {0, 90, 180, 270};
        for (Location hit : hits) {
            for (int rotation : rotations) {
                int[] steps = calculateSteps(rotation);
                int row_step = steps[0];
                int col_step = steps[1];
                try {
                    if (getGuessBoard()[hit.getRow() + row_step][hit.getCol() + col_step] == 0) {
                        possibleTargets.add(new LocationWithFrequency(hit.getRow() + row_step, hit.getCol() + col_step, rotation,
                                frequencyTable[hit.getRow() + row_step][hit.getCol() + col_step]));
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }


    private void refreshTotalPossibleTargets() {
        int[] rotations = {0, 90, 180, 270};

        for (Location hit : allHits) {
            for (int rotation : rotations) {

                int[] steps = calculateSteps(rotation);

                int row_step = steps[0];
                int col_step = steps[1];

                try {
                    if (getGuessBoard()[hit.getRow() + row_step][hit.getCol() + col_step] == 0) {
                        possibleTargets.add(new LocationWithFrequency(hit.getRow() + row_step, hit.getCol() + col_step, rotation,
                                frequencyTable[hit.getRow() + row_step][hit.getCol() + col_step]));
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    private void refreshFrequencyTable() {
        frequencyTable = new int[10][10]; // clears every time new guess is required
        for (int i = 0; i < sampleSize; i++) {
            sample();
        }
//        print2DArray(frequencyTable);
    }

    private void sample() {

        int maxAttempts = 100;

        int[] rotationOptions = {0, 90, 180, 270};

        Random random = new Random();

        for (Ship ship : remainingShips.keySet()) {
            int length = remainingShips.get(ship);
            int attempts = 0;
            while (attempts < maxAttempts) {

                int row = random.nextInt(10);
                int col = random.nextInt(10);
                int rotation = rotationOptions[random.nextInt(rotationOptions.length)];

                if (place(row, col, length, rotation)) {
                    break;
                }
                attempts++;
            }
        }
    }

    private boolean place(int row, int col, int length, int rotation) {

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
                if (getGuessBoard()[location.getRow()][location.getCol()] != 0) {
                    free = false;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
            free = false;
        }

        if (free) {
            for (Location location : cells) {
                if (getGuessBoard()[location.getRow()][location.getCol()] == 0) {
                    frequencyTable[location.getRow()][location.getCol()]++;
                }
            }
        }

        return free;
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
                    if (!hasPlacedSub) {
                        addShip(new Submarine(cells.toArray(new Location[3])));
                        hasPlacedSub = true;
                    } else {
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

    @Override
    public void populateShips() {

        int[] rotations = {0, 90, 180, 270};

        int maxAttempts = 100;

        Random random = new Random();

        int[] lenghts = {5, 4, 3, 3, 2};

        for (int length : lenghts) {
            int attempts = 0;
            while (attempts < maxAttempts) {

                int row = random.nextInt(10);
                int col = random.nextInt(10);
                int rotation = rotations[random.nextInt(4)];

                if (placeToBoard(row, col, length, rotation)) {
                    break;
                }
                attempts++;
            }
        }

    }

    public void print2DArray(int[][] array) {
        for (int[] i : array) {
            System.out.println(Arrays.toString(i));
        }
    }

    private LocationWithFrequency findMax(int[][] a) {
        int maxVal = -99999;
        LocationWithFrequency maxLocation = new LocationWithFrequency(-1, -1, -1, -9999);

        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[row].length; col++) {

                if (a[row][col] > maxVal) {
                    maxVal = a[row][col];
                    maxLocation = new LocationWithFrequency(row, col, -1, maxVal);
                }
            }
        }
        return maxLocation;
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

class LocationWithFrequency extends Location {

    private int frequency;
    private int rotation;

    public LocationWithFrequency(int row, int col, int rotation, int frequency) {
        super(row, col);
        this.frequency = frequency;
        this.rotation = rotation;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getRotation() {
        return rotation;
    }

    public Location getLocation() {
        return new Location(getRow(), getCol());
    }


    @Override
    public String toString() {
        return super.toString() + " Rotation " + rotation + " Frequency: " + frequency;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof LocationWithFrequency) {
            LocationWithFrequency other = (LocationWithFrequency) obj;
            return other.getRotation() == this.getRotation() && other.getRow() ==
                    this.getRow() && other.getCol() == this.getCol();

        }
        return false;
    }
}
