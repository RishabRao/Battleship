public class PatrolBoat extends Ship {
    /**
     * Construct a PatrolBoat with a length
     * of 2 and the specified Locations.
     *
     * @param locations
     */
    public PatrolBoat(Location... locations) {
        super(2);
        addLocation(locations);
    }

    @Override
    public String toString() {
        return "PatrolBoat";
    }
}