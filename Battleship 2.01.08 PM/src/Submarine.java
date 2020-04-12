public class Submarine extends Ship {
    /**
     * Construct a Submarine with a length
     * of 3 and the specified Locations.
     *
     * @param locations
     */
    public Submarine(Location... locations) {
        super(3);
        addLocation(locations);
    }

    @Override
    public String toString() {
        return "Submarine";
    }
}