import java.util.*;

public class MariusComputerPlayer extends ComputerPlayer {

    private static final String SUNK = "";
    private final Board board;

    MariusComputerPlayer(final String name) {
        super(name);
        board = new Board();
    }

    /**
     * Randomly chooses a Location that has not been
     * attacked (Location loc is ignored).  Marks
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
    public boolean attack(final Player enemy, Location loc) {
        final Ship ship = enemy.getShip(loc = board.attack());

//        System.out.println(loc);

        if (ship != null) {
            getGuessBoard()[loc.getRow()][loc.getCol()] = 1;
            ship.takeHit(loc);

            board.getHits().add(loc);

            if (ship.isSunk()) {

                board.getIntegers().remove(Integer.valueOf(ship.getLocations().size()));
                board.getMisses().addAll(board.getHits());
                board.getHits().clear();

                return true;
            }

        } else {
            getGuessBoard()[loc.getRow()][loc.getCol()] = -1;
            board.getMisses().add(loc);
        }

        return false;
    }

    @Override
    public String toString() {
        return "ComputerPlayer{" +
                "board=" + board +
                '}';
    }

    static class Board {
        private final Set<Location> misses = new HashSet<>(83);
        private int[][] board;
        private Set<Location> hits = new HashSet<>(5);
        private List<Integer> integers = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

        Board() {
            board = new int[10][10];
        }

        public Collection<Location> getMisses() {
            return misses;
        }

        public Collection<Integer> getIntegers() {
            return integers;
        }

        public Collection<Location> getHits() {
            return hits;
        }

        public void resetBoard() {
            board = new int[10][10];

            for (final Location location : misses)
                board[location.getRow()][location.getCol()] = -1;
        }

        //attacks the board using probability
        public Location attack() {
            resetBoard();

            if (getHits().isEmpty()) {
//                System.out.println("Hunting");

                for (final int[] r : board) {
                    for (int c = 0; c < r.length; c++) {
                        if (getIntegers().contains(5) && (c < (r.length - 4)))
                            if ((-1 != r[c]) && (-1 != r[c + 1]) && (-1 != r[c + 2]) && (-1 != r[c + 3]) && (-1 != r[c + 4])) {
                                ++r[c];
                                ++r[c + 1];
                                ++r[c + 2];
                                ++r[c + 3];
                                ++r[c + 4];
                            }

                        if (getIntegers().contains(4) && (c < (r.length - 3)))
                            if ((-1 != r[c]) && (-1 != r[c + 1]) && (-1 != r[c + 2]) && (-1 != r[c + 3])) {
                                ++r[c];
                                ++r[c + 1];
                                ++r[c + 2];
                                ++r[c + 3];
                            }
                        if (getIntegers().contains(3) && (c < (r.length - 2)))
                            if ((-1 != r[c]) && (-1 != r[c + 1]) && (-1 != r[c + 2])) {
                                ++r[c];
                                ++r[c + 1];
                                ++r[c + 2];

                            }
                        if (getIntegers().contains(3) && (2 == Collections.frequency(getIntegers(), 3)) && (c < (r.length - 2)))
                            if ((-1 != r[c]) && (-1 != r[c + 1]) && (-1 != r[c + 2])) {
                                ++r[c];
                                ++r[c + 1];
                                ++r[c + 2];

                            }
                        if (getIntegers().contains(2) && (c < (r.length - 1)))
                            if ((-1 != r[c]) && (-1 != r[c + 1])) {
                                ++r[c];
                                ++r[c + 1];
                            }
                    }
                }

                for (int c = 0; c < board[0].length; c++) {
                    for (int r = 0; r < board.length; r++) {
                        if (getIntegers().contains(5) && (r < (board.length - 4)))
                            if ((-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c]) && (-1 != board[r + 3][c]) && (-1 != board[r + 4][c])) {
                                ++board[r][c];
                                ++board[r + 1][c];
                                ++board[r + 2][c];
                                ++board[r + 3][c];
                                ++board[r + 4][c];
                            }

                        if (getIntegers().contains(4) && (r < (board.length - 3)))
                            if ((-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c]) && (-1 != board[r + 3][c])) {
                                ++board[r][c];
                                ++board[r + 1][c];
                                ++board[r + 2][c];
                                ++board[r + 3][c];
                            }
                        if (getIntegers().contains(3) && (r < (board.length - 2)))
                            if ((-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c])) {

                                ++board[r][c];
                                ++board[r + 1][c];
                                ++board[r + 2][c];


                            }
                        if (getIntegers().contains(3) && (2 == Collections.frequency(getIntegers(), 3)) && (r < (board.length - 2)))
                            if ((-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c])) {

                                ++board[r][c];
                                ++board[r + 1][c];
                                ++board[r + 2][c];


                            }
                        if (getIntegers().contains(2) && (r < (board.length - 1)))
                            if ((-1 != board[r][c]) && (-1 != board[r + 1][c])) {
                                ++board[r][c];
                                ++board[r + 1][c];
                            }
                    }
                }
            } else {
//                System.out.println("Hitting");

                for (final Location hit : getHits()) {
                    final int r = hit.getRow();
                    final int c = hit.getCol();

                    if (getIntegers().contains(2)) {
                        if ((0 <= (c - 1)) && (-1 != board[r][c]) && (-1 != board[r][c - 1])) {
                            ++board[r][c];
                            ++board[r][c - 1];
                        }


                        if (((c + 1) < board[0].length) && (-1 != board[r][c]) && (-1 != board[r][c + 1])) {
                            ++board[r][c];
                            ++board[r][c + 1];
                        }

                        if ((0 <= (r - 1)) && (-1 != board[r][c]) && (-1 != board[r - 1][c])) {
                            ++board[r][c];
                            ++board[r - 1][c];
                        }

                        if (((r + 1) < board.length) && (-1 != board[r][c]) && (-1 != board[r + 1][c])) {
                            ++board[r][c];
                            ++board[r + 1][c];
                        }


                    }
                    if (getIntegers().contains(3)) {
                        final int inc = Collections.frequency(getIntegers(), 3);

                        if ((0 <= (c - 2)) && (-1 != board[r][c]) && (-1 != board[r][c - 1]) && (-1 != board[r][c - 2])) {
                            board[r][c] += inc;
                            board[r][c - 1] += inc;
                            board[r][c - 2] += inc;
                        }
                        if (((c + 2) < board[0].length) && (-1 != board[r][c]) && (-1 != board[r][c + 1]) && (-1 != board[r][c + 2])) {
                            board[r][c] += inc;
                            board[r][c + 1] += inc;
                            board[r][c + 2] += inc;
                        }
                        if ((0 <= (r - 2)) && (-1 != board[r][c]) && (-1 != board[r - 1][c]) && (-1 != board[r - 2][c])) {
                            board[r][c] += inc;
                            board[r - 1][c] += inc;
                            board[r - 2][c] += inc;
                        }
                        if (((r + 2) < board.length) && (-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c])) {
                            board[r][c] += inc;
                            board[r + 1][c] += inc;
                            board[r + 2][c] += inc;
                        }


                    }
                    if (getIntegers().contains(4)) {
                        if ((0 <= (c - 3)) && (-1 != board[r][c]) && (-1 != board[r][c - 1]) && (-1 != board[r][c - 2]) && (-1 != board[r][c - 3])) {
                            ++board[r][c];
                            ++board[r][c - 1];
                            ++board[r][c - 2];
                            ++board[r][c - 3];
                        }
                        if (((c + 3) < board[0].length) && (-1 != board[r][c]) && (-1 != board[r][c + 1]) && (-1 != board[r][c + 2]) && (-1 != board[r][c + 3])) {
                            ++board[r][c];
                            ++board[r][c + 1];
                            ++board[r][c + 2];
                            ++board[r][c + 3];
                        }
                        if ((0 <= (r - 3)) && (-1 != board[r][c]) && (-1 != board[r - 1][c]) && (-1 != board[r - 2][c]) && (-1 != board[r - 3][c])) {
                            ++board[r][c];
                            ++board[r - 1][c];
                            ++board[r - 2][c];
                            ++board[r - 3][c];
                        }
                        if (((r + 3) < board.length) && (-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c]) && (-1 != board[r + 3][c])) {
                            ++board[r][c];
                            ++board[r + 1][c];
                            ++board[r + 2][c];
                            ++board[r + 3][c];
                        }


                    }
                    if (getIntegers().contains(5)) {
                        if ((0 <= (c - 4)) && (-1 != board[r][c]) && (-1 != board[r][c - 1]) && (-1 != board[r][c - 2]) && (-1 != board[r][c - 3]) && (-1 != board[r][c - 4])) {
                            ++board[r][c];
                            ++board[r][c - 1];
                            ++board[r][c - 2];
                            ++board[r][c - 3];
                            ++board[r][c - 4];
                        }
                        if (((c + 4) < board[0].length) && (-1 != board[r][c]) && (-1 != board[r][c + 1]) && (-1 != board[r][c + 2]) && (-1 != board[r][c + 3]) && (-1 != board[r][c + 4])) {
                            ++board[r][c];
                            ++board[r][c + 1];
                            ++board[r][c + 2];
                            ++board[r][c + 3];
                            ++board[r][c + 4];
                        }
                        if ((0 <= (r - 4)) && (-1 != board[r][c]) && (-1 != board[r - 1][c]) && (-1 != board[r - 2][c]) && (-1 != board[r - 3][c]) && (-1 != board[r - 4][c])) {
                            ++board[r][c];
                            ++board[r - 1][c];
                            ++board[r - 2][c];
                            ++board[r - 3][c];
                            ++board[r - 4][c];
                        }
                        if (((r + 4) < board.length) && (-1 != board[r][c]) && (-1 != board[r + 1][c]) && (-1 != board[r + 2][c]) && (-1 != board[r + 3][c]) && (-1 != board[r + 4][c])) {
                            ++board[r][c];
                            ++board[r + 1][c];
                            ++board[r + 2][c];
                            ++board[r + 3][c];
                            ++board[r + 4][c];
                        }
                    }
                }

                for (final Location hit : getHits()) {
                    board[hit.getRow()][hit.getCol()] = 0;
                }
            }

//            for (int[] i : board)
//                System.out.println(Arrays.toString(i));
            return findLargest();
        }

        //chooses the larges probability
        public Location findLargest() {
            int max = -1;

            final LinkedList<Location> loc = new LinkedList<>();

            for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board[0].length; c++)
                    if (board[r][c] > max) {
                        loc.clear();
                        max = board[r][c];
                        loc.add(new Location(r, c));
                    } else if (board[r][c] == max)
                        loc.add(new Location(r, c));

            Collections.shuffle(loc);

            return loc.pop();
        }

        @Override
        public String toString() {
            return "Board{" +
                    "board=" + Arrays.toString(board) +
                    ", hits=" + getHits() +
                    ", misses=" + misses +
                    ", integers=" + getIntegers() +
                    '}';
        }

    }
}