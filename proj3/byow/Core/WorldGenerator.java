package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import static byow.Core.RandomUtils.*;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {
    private Random random;

    private static final int MARGIN = 2;
    private static final int ROOM_MIN_COUNT = 5;
    private static final int ROOM_MAX_COUNT = 17;
    private static final int MAX_ATTEMPTS = 1000;
    /* uniform(Random, a, b) draws from [a, b), so a size is drawn as uniform(min, max + 1). */
    private static final int MIN_SIZE = 3;
    private static final int MAX_WIDTH_EXCLUSIVE = 12;
    private static final int MAX_HEIGHT_EXCLUSIVE = 9;

    private class Room {
        int x;
        int y;
        int width;
        int height;
    }

    private class Point {
        int x;
        int y;

        private Point() {
            this.x = 0;
            this.y = 0;
        }

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private List<Room> rooms = new ArrayList<>();

    private boolean[][] isRoom = new boolean[Engine.WIDTH][Engine.HEIGHT];


    WorldGenerator(long seed) {
        random = new Random(seed);
    }

    private boolean checkRoomInWorld(Room room) {
        if (room.x + room.width >= Engine.WIDTH - MARGIN
                || room.y + room.height >= Engine.HEIGHT - MARGIN) {
            return false;
        }
        return true;
    }

    private boolean checkRoom(Room room) {
        for (int i = room.x; i <= room.x + room.width; i++) {
            for (int j = room.y; j <= room.y + room.height; j++) {
                if (isRoom[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void drawRoom(TETile[][] world, Room room) {
        for (int i = room.x; i <= room.x + room.width; i++) {
            for (int j = room.y; j <= room.y + room.height; j++) {
                world[i][j] = Tileset.FLOOR;
                isRoom[i][j] = true;
            }
        }
    }

    private void drawVertically(TETile[][] world, Point prev, Point curr, TETile t) {
        int min = min(prev.y, curr.y);
        int max = max(prev.y, curr.y);
        for (int i = min; i <= max; i++) {
            world[prev.x][i] = t;
        }
    }

    private void drawHorizontally(TETile[][] world, Point prev, Point curr, TETile t) {
        int min = min(prev.x, curr.x);
        int max = max(prev.x, curr.x);
        for (int i = min; i <= max; i++) {
            world[i][curr.y] = t;
        }
    }

    private static boolean isNextToFloor(TETile[][] world, int col, int row) {
        /* The neighbor loop simply gives up on cells in the outermost ring. Safe only because
         * MARGIN = 2 guarantees floors (and thus walls) stay away from the outermost ring;
         * revisit these guards if MARGIN ever changes. */
        for (int i = col - 1; i <= col + 1; i++) {
            if (i == -1 || col == Engine.WIDTH - 1) {
                break;
            }
            for (int j = row - 1; j <= row + 1; j++) {
                if (j == -1 || row == Engine.HEIGHT - 1) {
                    break;
                }
                if (world[col][row] == Tileset.NOTHING && world[i][j] == Tileset.FLOOR) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void drawWall(TETile[][] world) {
        for (int i = 0; i < Engine.WIDTH; i++) {
            for (int j = 0; j < Engine.HEIGHT; j++) {
                if (isNextToFloor(world, i, j)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public TETile[][] generate(TETile[][] world) {
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
                isRoom[x][y] = false;
            }
        }

        int roomsCount = uniform(random, ROOM_MIN_COUNT, ROOM_MAX_COUNT);

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            Room room = new Room();
            room.x = uniform(random, MARGIN, Engine.WIDTH - MARGIN);
            room.y = uniform(random, MARGIN, Engine.HEIGHT - MARGIN);
            room.width = uniform(random, MIN_SIZE, MAX_WIDTH_EXCLUSIVE);
            room.height = uniform(random, MIN_SIZE, MAX_HEIGHT_EXCLUSIVE);
            if (checkRoomInWorld(room) && checkRoom(room)) {
                drawRoom(world, room);
            } else {
                continue;
            }
            if (!rooms.isEmpty()) {
                Room prevRoom = rooms.get(rooms.size() - 1);
                Point prev = new Point();
                prev.x = uniform(random, prevRoom.x, prevRoom.x + prevRoom.width);
                prev.y = uniform(random, prevRoom.y, prevRoom.y + prevRoom.height);
                Point curr = new Point();
                curr.x = uniform(random, room.x, room.x + room.width);
                curr.y = uniform(random, room.y, room.y + room.height);
                drawHorizontally(world, prev, curr, Tileset.FLOOR);
                drawVertically(world, prev, curr, Tileset.FLOOR);
            }
            rooms.add(room);
            if (rooms.size() == roomsCount) {
                break;
            }
        }


        drawWall(world);

        return world;
    }
}
