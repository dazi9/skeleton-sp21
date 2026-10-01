package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private enum Phase {
        MENU, SEED, EXPLORE, COLON
    } private Phase phase = Phase.MENU;
    private enum Signal {
        CONTINUE, QUIT
    }
    private String seedBuffer;
    private TETile[][] world = null;
    private int avatarX;
    private int avatarY;
    private String history;
    private static final String SAVE_FILE = "savefile.txt";
    private static final int PAUSE_TIME = 20;

    private void reset() {
        phase = Phase.MENU;
        seedBuffer = "";
        world = new TETile[WIDTH][HEIGHT];
        avatarX = 0;
        avatarY = 0;
        history = "";
    }

    private void move(char c) {
        int targetX = avatarX;
        int targetY = avatarY;
        switch (c) {
            case 'W':
                history += c;
                targetY = avatarY + 1;
                if (world[targetX][targetY] == Tileset.FLOOR) {
                    world[avatarX][avatarY] = Tileset.FLOOR;
                    avatarY = targetY;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                }
                break;
            case 'A':
                history += c;
                targetX = avatarX - 1;
                if (world[targetX][targetY] == Tileset.FLOOR) {
                    world[avatarX][avatarY] = Tileset.FLOOR;
                    avatarX = targetX;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                }
                break;
            case 'S':
                history += c;
                targetY = avatarY - 1;
                if (world[targetX][targetY] == Tileset.FLOOR) {
                    world[avatarX][avatarY] = Tileset.FLOOR;
                    avatarY = targetY;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                }
                break;
            case 'D':
                history += c;
                targetX = avatarX + 1;
                if (world[targetX][targetY] == Tileset.FLOOR) {
                    world[avatarX][avatarY] = Tileset.FLOOR;
                    avatarX = targetX;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                }
                break;
            default:
                break;
        }
    }

    private void save() {
        try {
            Files.writeString(Path.of(SAVE_FILE), history);
        } catch (IOException e) {
            /* Ignore IOException */
        }
    }

    private boolean load() {
        String content;
        try {
            content = Files.readString(Path.of(SAVE_FILE));
        } catch (IOException ignored) {
            return false;
        }
        history = "";
        for (int i = 0; i < content.length(); i++) {
            processChar(content.charAt(i));
        }
        return true;
    }

    private void spawnAvatar() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    avatarX = x;
                    avatarY = y;
                    world[x][y] = Tileset.AVATAR;
                    return;
                }
            }
        }
    }

    private Signal menuOperation(char c) {
        Signal signal = Signal.CONTINUE;
        switch (c) {
            case 'N':
                seedBuffer = "";
                history += c;
                phase = Phase.SEED;
                break;
            case 'Q':
                signal =  Signal.QUIT;
                break;
            case 'L':
                if (!load()) {
                    signal = Signal.QUIT;
                }
                break;
            default:
                break;
        }
        return signal;
    }

    private Signal seedOperation(char c) {
        if (c == 'S' && !seedBuffer.isEmpty()) {
            history += c;
            WorldGenerator generator = new WorldGenerator(Long.parseLong(seedBuffer));
            generator.generate(world);
            spawnAvatar();
            phase = Phase.EXPLORE;
        } else if (Character.isDigit(c)) {
            seedBuffer += c;
            history += c;
        }
        return Signal.CONTINUE;
    }

    private Signal exploreOperation(char c) {
        if (c == ':') {
            phase = Phase.COLON;
        } else {
            move(c);
        }
        return Signal.CONTINUE;
    }

    private Signal colonOperation(char c) {
        if (c == 'Q') {
            save();
            return Signal.QUIT;
        }
        phase = Phase.EXPLORE;
        return Signal.CONTINUE;
    }

    private Signal processChar(char ch) {
        Signal signal = Signal.CONTINUE;
        char c = Character.toUpperCase(ch);
        signal = switch (phase) {
            case MENU -> menuOperation(c);
            case SEED -> seedOperation(c);
            case EXPLORE -> exploreOperation(c);
            case COLON -> colonOperation(c);
        };
        return signal;
    }

    private void drawMenu() {
        StdDraw.clear();
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + 2, "New World(N)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "Load(L)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 2, "Quit(Q)");
        StdDraw.show();
    }

    private void drawSeedEntry() {
        StdDraw.clear();
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "Enter seed:" + seedBuffer);
        StdDraw.show();
    }

    private void drawWorld() {
        ter.renderFrame(world);
    }

    private void drawFrame() {
        switch (phase) {
            case MENU -> drawMenu();
            case SEED -> drawSeedEntry();
            case EXPLORE, COLON -> drawWorld();
            default -> { }
        }
    }

    private void drawHUD() {
        int tx = (int) StdDraw.mouseX();
        int ty = (int) StdDraw.mouseY();
        StdDraw.setPenColor(Color.WHITE);
        if (tx >= 0 && tx < WIDTH && ty >= 0 && ty < HEIGHT) {
            StdDraw.textLeft(5, WIDTH + 1, world[tx][ty].description());
        } else {
            StdDraw.textLeft(5, WIDTH + 1, "out of world");
        }
        StdDraw.show();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT + 2);
        reset();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Signal signal = processChar(StdDraw.nextKeyTyped());
                if (signal == Signal.QUIT) {
                    break;
                }
            }
            drawFrame();
            if (phase == Phase.COLON || phase == Phase.EXPLORE) {
                drawHUD();
            }
            StdDraw.pause(PAUSE_TIME);
        }
        System.exit(0);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        reset();
        for (int i = 0; i < input.length(); i++) {
            Signal signal = processChar(input.charAt(i));
            if (signal == Signal.QUIT) {
                break;
            }
        }
        return world;
    }
}
