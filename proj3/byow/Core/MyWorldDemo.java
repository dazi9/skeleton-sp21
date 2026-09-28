package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class MyWorldDemo {



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();

        ter.initialize(Engine.WIDTH, Engine.HEIGHT);

        TETile[][] world;
        Engine engine = new Engine();

        world = engine.interactWithInputString("n35154S");


        ter.renderFrame(world);
    }
}
