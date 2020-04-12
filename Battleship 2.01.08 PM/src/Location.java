public class Location {
    private int row;
    private int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Location) {
            Location check = (Location) obj;
            return check.getCol() == this.getCol() && check.getRow() == this.getRow();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Row: " + row + " Col: " + col;
    }
}
