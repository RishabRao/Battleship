import java.util.ArrayList;
import java.util.IntSummaryStatistics;

public class Driver {
    public static void main(String[] args) {
        IntSummaryStatistics stats = new IntSummaryStatistics();
        for (int i = 0; i < 5; i++) {
            stats.accept(new VsDriver(false).getMoves());
        }
        System.out.println("Average moves: " + stats.getAverage());

    }
}