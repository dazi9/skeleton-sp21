package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    private String encouragement;
    private Random decorationRand;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
        decorationRand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        String string = "";
        for (int i = 0; i < n; i++) {
            int index = RandomUtils.uniform(rand, 0, CHARACTERS.length);
            String c = Character.toString(CHARACTERS[index]);
            string = string + c;
        }
        return string;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);                                   // 中央文字
        StdDraw.text((double) this.width / 2, (double) this.height / 2, s);
        if (!gameOver) {
            StdDraw.setPenColor(new Color(70, 70, 70));                           // 深色条
            StdDraw.filledRectangle((double) this.width / 2, (double) this.height - 1,
                    (double) this.width / 2, 1);                  // 覆盖 y ∈ [38, 40]
            StdDraw.setPenColor(StdDraw.WHITE);                                   // 条上文字
            StdDraw.text(5, (double) this.height - 1, "Round:" + round);
            if (!playerTurn) {
                StdDraw.text(20, (double) this.height - 1, "Watch!");
            } else {
                StdDraw.text(20, (double) this.height - 1, "Type!");
            }
            StdDraw.text(35, (double) this.height - 1, encouragement);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(Character.toString(letters.charAt(i)));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String string = "";
        while (string.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                string = string + StdDraw.nextKeyTyped();
                drawFrame(string);
            }
        }
        return string;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 0;
        gameOver = false;
        //TODO: Establish Engine loop
        while (true) {
            playerTurn = false;
            round++;
            encouragement = ENCOURAGEMENT[RandomUtils.uniform(decorationRand, 0, ENCOURAGEMENT.length)];
            drawFrame("Round:" + round);
            String string = generateRandomString(round);
            flashSequence(string);
            playerTurn = true;
            drawFrame("");
            String ans = solicitNCharsInput(round);
            if (ans.equals(string)) {
                continue;
            } else {
                gameOver = true;
                drawFrame("Game Over! You made it to round:" + round);
                break;
            }
        }
    }

}
