public class AircraftCarrier extends Ship {
    /**
     * Construct an AircraftCarrier with a length
     * of 5 and the specified Locations.
     *
     * @param locations
     */
    public AircraftCarrier(Location... locations) {
        super(5);
        addLocation(locations);
    }

    @Override
    public String toString() {
        return "Aircraft Carrier";
    }
}
