import java.util.ArrayList;

public class Tester {

    public static void main(String[] args) {

        int numberOfGames = 100;

        for (int i = 0; i < numberOfGames; i++) {

            Battleship battleship = new Battleship();
            battleship.addPlayer(new ExpertComputerPlayer("Monte Carlo"));        // construct player1's AI here
            battleship.addPlayer(new ComputerPlayer("Random"));       // construct player2's AI here

            Player p1 = battleship.getPlayer(0);
            Player p2 = battleship.getPlayer(1);

            boolean p1Turn = true;
            while (!battleship.gameOver()) {
                if (p1Turn)
                    p1.attack(p2, new Location(0, 0));
                else
                    p2.attack(p1, new Location(0, 0));

                p1Turn = !p1Turn;

                battleship.upkeep();

//                try {
//                    Thread.sleep(250);                      // milliseconds; adjust to change speed
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
            }
            System.out.println(battleship.getWinner());
        }
    }
}
