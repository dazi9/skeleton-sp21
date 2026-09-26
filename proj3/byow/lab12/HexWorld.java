package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void addHexagon(TETile[][] world, int x, int y, int s, TETile t) {
        for (int row = 0; row < 2 * s; row++) {
            int offset = Math.min(row, 2 * s - 1 - row);
            int width = s + 2 * offset;
            addRow(world, x - offset, y + row, width, t);
        }
    }

    private static void addRow(TETile[][] world, int x, int y, int width, TETile t) {
        for (int i = 0; i < width; i++) {
            world[x + i][y] = t;
        }
    }

    public static void main(String args[]) {

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        addHexagon(world, 34, 13, 3, Tileset.FLOWER);

        ter.renderFrame(world);
    }
}
