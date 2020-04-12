import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VsDriver extends Canvas
{
    public static final int WIDTH = 1024, HEIGHT = WIDTH / 12 * 9;
    private Battleship battleship;
    private int x, y, squareSize, len, moves;
    private Player p1, p2;
    private BufferedImage logo, end, vs;
    private boolean nogui;


    public int getMoves() {
        return moves;
    }

    public VsDriver(boolean nogui)
    {
        this.nogui = nogui;
        battleship = new Battleship();
        battleship.addPlayer(new ExpertComputerPlayer("Rishab"));        // construct player1's AI here
        battleship.addPlayer(new MariusComputerPlayer("Other"));       // construct player2's AI here

        x = 90;
        y = 200;
        squareSize = 36;
        len = squareSize * 10 - 1;
        p1 = battleship.getPlayer(0);
        p2 = battleship.getPlayer(1);
        if (!nogui) {
            // Get Battleship Logo
            try {
                logo = ImageIO.read(new File("src/Logo.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get VS Screen
            try {
                vs = ImageIO.read(new File("src/VS.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get End Screen
            try {
                end = ImageIO.read(new File("src/End.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = p1.getName() + " -vs- " + p2.getName();
            new Window(WIDTH, HEIGHT, title, this);

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            render();
        }
        if (nogui) {
//            System.out.println(move());
            move();
        }
        else
            move();
    }

    private void move()
    {
        boolean p1Turn = true;
        moves = 0;
        while(!battleship.gameOver())
        {
            if(p1Turn) {
                p1.attack(p2, new Location(0, 0));
            }
            else {
                p2.attack(p1, new Location(0, 0));
                moves++;
            }

            p1Turn = !p1Turn;

            battleship.upkeep();
            if (!nogui)
                render();

            try {
                if (nogui);
                    // milliseconds; adjust to change speed
                else
                    Thread.sleep(200);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
//        System.out.print(moves + ": ");
        if (p1Turn)
            System.out.println(moves + " " + p2.getName());
        if (!p1Turn && moves == 17)
            System.out.println("Perfect game for Red! (17 for 17)");
    }

    private void render()
    {
        Graphics g = getGraphics();

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if(!battleship.gameOver())
        {
            // Boards
            renderGrid(g, x, y, squareSize);
            renderGuesses(g, p1, x, y, squareSize);
            renderGrid(g, 570, y, squareSize);
            renderGuesses(g, p2, 570, y, squareSize);

            // Names
            g.setColor(Color.WHITE);
            g.drawString(p1.getName(), x, y + 25 + len);
            g.drawString(p2.getName(), 570, y + 25 + len);
        }
        else
        {
            // End Screen
            g.drawImage(end, 0, 0, this);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, squareSize));
            String winner = battleship.getWinner().getName();
            g.drawString(winner, WIDTH / 2 - (winner.length() * squareSize / 4), HEIGHT / 4);
            g.drawString("Wins!", WIDTH / 2 - ("Wins!".length() * squareSize / 4), HEIGHT / 4 + squareSize);
        }

        // Battleship Logo
        g.drawImage(logo, WIDTH / 2 - 246, 10, this);

        // VS
        g.drawImage(vs, WIDTH / 2 + 180, 70, this);

        g.dispose();
    }

    private void renderGrid(Graphics g, int x, int y, int s)
    {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", 1, s / 2));

        // Row Lines
        for(int i = 0; i < 11; i++)
            g.drawLine(x, y+i*s, x+len, y+i*s);

        // Column Lines
        for(int i = 0; i < 11; i++)
            g.drawLine(x+i*s, y, x+i*s, y+len);

        // Row Markers
        for(int i = 0; i < 10; i++)	//marks row coordinates on side
            g.drawString(i + "", x-(int)(s*0.43), y+(int)(s*0.67)+s*i);

        // Column Markers
        for(int i = 0; i < 10; i++) 	//marks column coordinates on top
            g.drawString(i + "", x+(int)(s*0.4)+s*i, y-(int)(s*0.2));
    }

    private void renderGuesses(Graphics g, Player player, int x, int y, int s)
    {
        int[][] guessBoard = player.getGuessBoard();
        for(int r = 0; r < guessBoard.length; r++)
            for(int c = 0; c < guessBoard[r].length; c++)
                if(guessBoard[r][c] > 0)    // hit
                {
                    g.setColor(Color.RED);
                    g.fillOval(c*s+x+(int)(s*0.35), r*s+y+(int)(s*0.35), (int)(s*0.33), (int)(s*0.33));
                }
                else if(guessBoard[r][c] < 0)   // miss
                {
                    g.setColor(Color.WHITE);
                    g.fillOval(c*s+x+(int)(s*0.35), r*s+y+(int)(s*0.35), (int)(s*0.33), (int)(s*0.33));
                }
    }

    public static void main(String[] args)
    {
        new VsDriver(false);
    }
}