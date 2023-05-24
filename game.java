import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
//import java.util.Random;
import java.util.Random;

//creating out world, represents the game NBullets
class NBullets extends World {
  int numBullets; // number of bullets allowed to spawn
  ILoShips ships; // ships in game
  int shipsSpawn; // prevents too many ships (max amount of ships)
  ILoBullets bullets; // bullets in game
  int shipsDestroyed; // for scoreboard amount of ships destroyed

  // NBullets constructor
  // Creates the game with these given parameters
  NBullets(int numBullets, ILoShips ships, int shipsSpawn, ILoBullets bullets, int shipsDestroyed) {
    if (numBullets >= 0) {
      this.numBullets = numBullets;
      this.ships = ships;
      this.shipsSpawn = shipsSpawn;
      this.bullets = bullets;
      this.shipsDestroyed = shipsDestroyed;
    }
  }

  // creates initial scene with current ships and bullets
  @Override
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(500, 500);
    scene = scene.placeImageXY(
        new TextImage("Bullets: " + Integer.toString(this.numBullets), 13, Color.BLACK), 450, 450);
    scene = this.ships.printShips(scene);
    scene = this.bullets.printBullets(scene);
    scene = scene.placeImageXY(
        new TextImage("Score: " + Integer.toString(this.shipsDestroyed), 13, Color.BLACK), 50, 450);
    return scene;
  }

  // moves, deletes, and spawns new bullets and ships every tick
  @Override
  public World onTick() {
    if (this.shipsSpawn > 0) {
      return new NBullets(this.numBullets, this.ships.destroyed(this.bullets).move(),
          this.shipsSpawn - 1, this.bullets.eliminate().removeBullet(this.ships).moveBullets(),
          this.ships.willHit(this.shipsDestroyed, this.bullets));
    }
    else {
      return new NBullets(this.numBullets,
          this.ships.destroyed(this.bullets).spawn((new Random()).nextInt(4) + 1).move(), 8,
          this.bullets.eliminate().removeBullet(this.ships).moveBullets(),
          this.ships.willHit(this.shipsDestroyed, this.bullets));
    }
  }

  // shoots a bullet when there are bullets left
  @Override
  public World onKeyEvent(String key) {
    if (key.equals(" ") && this.numBullets > 0) {
      return new NBullets(this.numBullets - 1, this.ships, this.shipsSpawn,
          this.bullets.spawnBullets(), this.shipsDestroyed);
    }
    else {
      return this;
    }
  }

  // Scene that shows the game has ended
  WorldScene makeEndScene() {
    WorldScene scene = new WorldScene(500, 500);
    scene = scene.placeImageXY(new TextImage("Game Over Loser.", 25, Color.RED), 250, 250);
    return scene;
  }

  // finds when the game should end
  @Override
  public WorldEnd worldEnds() {
    if (this.numBullets == 0 && this.bullets.nonScreen()) {
      return new WorldEnd(true, makeEndScene());
    }
    else {
      return new WorldEnd(false, makeEndScene());
    }
  }
}

class ExamplesNBullets {

  ILoShips listShips = new ConsLoShips(new NewShip().newShip(), new ConsLoShips(
      new NewShip().newShip(), new ConsLoShips(new NewShip().newShip(), new MtLoShips())));

  ILoBullets listBullets = new MtLoBullets();

  boolean testBigBang(Tester t) {
    NBullets world = new NBullets(15, this.listShips, 8, this.listBullets, 0);
    // width, height, tick rate = 0.5 means every 0.5 seconds the onTick method will
    // get called.
    return world.bigBang(500, 500, .15);
  }
}