import java.awt.Color;
//import java.util.Random;

import javalib.funworld.WorldScene;
import javalib.worldimages.*;
import javalib.worldimages.OutlineMode;
import tester.Tester;

//Represents a list of bullets
interface ILoBullets {

  // Prints a list of bullets onto a world scene
  WorldScene printBullets(WorldScene scene);

  // Moves each bullet in a list
  ILoBullets moveBullets();

  // creates each bullet in a list
  ILoBullets spawnBullets();

  // removes bullets that are off-screen
  ILoBullets eliminate();

  // returns true if a bullet is outside of the game window
  boolean nonScreen();

  // returns true if any bullet within a list is within
  // the distance of a ships radius
  boolean anyColide(int shipX, int shipY);

  // removes the bullet that collides with a ship
  ILoBullets deleteBullet(int shipX, int shipY);

  // returns a list with two new gen2Bullets to the list
  ILoBullets removeBullet(ILoShips ships);
}

//an empty list of bullets
class MtLoBullets implements ILoBullets {
  IBullet image;

  // represents no bullet
  MtLoBullets() {
    this.image = new EmptyBullet(250, 500, 1, 90);
  }

  // returns scene with no new bullet
  public WorldScene printBullets(WorldScene scene) {
    scene = this.image.printBullet(scene);
    return scene;
  }

  // There is nothing to move
  public ILoBullets moveBullets() {
    return this;
  }

  // there are no new bullets to return
  public ILoBullets spawnBullets() {
    return new ConsLoBullets((new NewBullet().createBullet()), this);
  }

  // There are no bullets to eliminate
  public ILoBullets eliminate() {
    return this;
  }

  // There is no bullet off screen
  public boolean nonScreen() {
    return true;
  }

  // There is no bullet to colide with anything
  public boolean anyColide(int shipX, int shipY) {
    return false;
  }

  // There are no more bullets to "delete"
  public ILoBullets deleteBullet(int shipX, int shipY) {
    return this;
  }

  // There are no more bullets to remove
  public ILoBullets removeBullet(ILoShips ships) {
    return this;
  }
}

//represents a list of bullet
class ConsLoBullets implements ILoBullets {
  IBullet first;
  ILoBullets rest;

  ConsLoBullets(IBullet first, ILoBullets rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns an image of the bullets into the overall scene
  public WorldScene printBullets(WorldScene scene) {
    scene = this.first.printBullet(scene);
    scene = this.rest.printBullets(scene);
    return scene;
  }

  // moves every bullet
  public ILoBullets moveBullets() {
    return new ConsLoBullets(this.first.moveBullet(), this.rest.moveBullets());
  }

  // creates a new 1st gen bullet
  public ILoBullets spawnBullets() {
    return new ConsLoBullets((new NewBullet().createBullet()), this);
  }

  // eliminates a bullet from a list if it is off screen
  public ILoBullets eliminate() {
    if (this.first.offScreen()) {
      return this.rest.eliminate();
    }
    else {
      return new ConsLoBullets(this.first, this.rest.eliminate());
    }
  }

  // checks that all bullets are off screen
  public boolean nonScreen() {
    return this.first.offScreen() && this.rest.nonScreen();
  }

  // checks in any bullet collides with a ship
  public boolean anyColide(int shipX, int shipY) {
    return this.first.colide(shipX, shipY) || this.rest.anyColide(shipX, shipY);
  }

  // deletes a bullet that collides with a ship
  public ILoBullets deleteBullet(int shipX, int shipY) {
    if (this.first.colide(shipX, shipY)) {
      return this.rest.deleteBullet(shipX, shipY);
    }
    else {
      return new ConsLoBullets(this.first, this.rest.deleteBullet(shipX, shipY));
    }
  }

  // removes a bullet that impacted a ship
  public ILoBullets removeBullet(ILoShips ships) {
    if (ships.impacted(this.first.posX(), this.first.posY(), this.first.size())) {
      return this.first.nextGenBullet(this.rest.removeBullet(ships), this.first.posX(),
          this.first.posY());
    }
    else {
      return new ConsLoBullets(this.first, this.rest.removeBullet(ships));
    }
  }

}

// represents a way to create a first gen bullet
class NewBullet {
  NewBullet() {
  }

  // creates a first gen bullet
  IBullet createBullet() {
    return new Bullet(250, 500, 1, 90);
  }

}

// represents a bullet
interface IBullet {

  // calculating bullet place after its been moved
  IBullet moveBullet();

  // outputs bullets
  WorldScene printBullet(WorldScene scene);

  // returns true if the bullet is outside the screen
  boolean offScreen();

  // returns true if a bullet is within a ships radius
  public boolean colide(int shipX, int shipY);

  // X position of a bullet
  int posX();

  // Y position of a bullet
  int posY();

  // radius of the bullet
  int size();

  // creates a new list of bullets including the ones formed from
  // an explosion of another bullet
  ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY);
}

abstract class ABullet implements IBullet {
  int posX;
  int posY;
  int gen; // generation of each bullet
  int size;
  int direction; // determines path of a bullet
  WorldImage bullet; // Image of bullet (circleImage)

  ABullet(int posX, int posY, int gen, int direction) {
    this.posX = posX;
    this.posY = posY;
    this.bullet = new CircleImage(4, OutlineMode.SOLID, Color.BLUE);
    this.gen = gen;
    this.size = 4;
    this.direction = direction;
  }

  abstract public IBullet moveBullet();

  // Places bullet onto the current scene
  public WorldScene printBullet(WorldScene scene) {
    scene = scene.placeImageXY(this.bullet, this.posX, this.posY);
    return scene;
  }

  // detects any collision with a ship
  public boolean colide(int shipX, int shipY) {
    return (17 + this.size) >= Math.hypot((this.posX - shipX), (posY - shipY));
  }

  // returns x position of a bullet
  public int posX() {
    return this.posX;
  }

  // returns y position of a bullet
  public int posY() {
    return this.posY;
  }

  // returns the size of a bullet
  public int size() {
    return this.size;
  }

  // determines if a bullet is off the screen
  public boolean offScreen() {
    return this.posX > 500 + this.size || this.posY > 500 + this.size || this.posX < 0 - this.size
        || this.posY < 0 - this.size;
  }
}

// non-existing bullet
class EmptyBullet extends ABullet {
  EmptyBullet(int posX, int posY, int gen, int direction) {
    super(posX, posY, gen, direction);
    this.bullet = new EmptyImage();
  }

  public IBullet moveBullet() {
    return this;
  }

  public boolean offScreen() {
    return false;
  }

  public boolean colide(int shipX, int shipY) {
    return false;
  }

  public ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY) {
    return rest;
  }
}

class Bullet extends ABullet {
  Bullet(int posX, int posY, int gen, int direction) {
    super(posX, posY, gen, direction);
  }

  // moves the bullet
  public IBullet moveBullet() {
    return new Bullet(this.posX, this.posY - 25, this.gen, this.direction);
  }

  // spawns new bullets when called
  public ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY) {
    return new ConsLoBullets(new Bullet2Gen(posX, posY, gen + 1, 180),
        new ConsLoBullets(new Bullet2Gen(posX, posY, gen + 1, 0), rest));
  }
}

//new generation of bullet (splitting of a bullet)
class Bullet2Gen extends ABullet {
  Bullet2Gen(int posX, int posY, int gen, int direction) {
    super(posX, posY, gen, direction);
    this.size = 6;
    this.bullet = new CircleImage(6, OutlineMode.SOLID, Color.BLUE);
  }

  // moves the bullet in the right direction
  public IBullet moveBullet() {
    if (this.direction == 180) {
      return new Bullet2Gen(this.posX - 25, this.posY, this.gen, this.direction);
    }
    else {
      return new Bullet2Gen(this.posX + 25, this.posY, this.gen, this.direction);
    }
  }

  // creates a 3rd gen bullet
  public ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY) {
    return new ConsLoBullets(new Bullet3Gen(posX, posY, gen + 1, 0),
        new ConsLoBullets(new Bullet3Gen(posX, posY, gen + 1, 120),
            new ConsLoBullets(new Bullet3Gen(posX, posY, gen + 1, 240), rest)));
  }

}

// A third generation bullet (3 hits)
class Bullet3Gen extends ABullet {
  Bullet3Gen(int posX, int posY, int gen, int direction) {
    super(posX, posY, gen, direction);
    this.size = 8;
    this.bullet = new CircleImage(8, OutlineMode.SOLID, Color.BLUE);

  }

  // moves the bullet
  public IBullet moveBullet() {
    if (this.direction == 0) {
      return new Bullet3Gen(this.posX + 25, this.posY, this.gen, this.direction);
    }
    else if (this.direction == 120) {
      return new Bullet3Gen((int) (this.posX - (Math.cos(120) * 25)),
          (int) (this.posY - (Math.sin(120) * 25)), this.gen, this.direction);
    }
    else {
      return new Bullet3Gen((int) (this.posX - (Math.cos(240) * 25)),
          (int) (this.posY + (Math.sin(240) * 25)), this.gen, this.direction);
    }
  }

  // checks if the bullet is off screen
  public boolean offScreen() {
    return this.posX > 510 || this.posY > 510 || this.posX < -10 || this.posY < -10;
  }

  // Creates an x-gen bullet
  public ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY) {
    return new BulletXGen(posX, posY, this.gen, this.gen).bullenXGenCreate(rest, posX, posY,
        this.gen, this.gen);
  }
}

// An x-gen bullet (after x hits)
class BulletXGen extends ABullet {
  BulletXGen(int posX, int posY, int gen, int direction) {
    super(posX, posY, gen, direction);
    this.size = 10;
    this.bullet = new CircleImage(10, OutlineMode.SOLID, Color.BLUE);
  }

  // moves the bullet according to its angle and quadrant
  public IBullet moveBullet() {
    if (this.direction >= 0 && this.direction < 90) {
      return new BulletXGen((int) (this.posX + Math.abs((Math.cos(this.direction)) * 25)),
          (int) (this.posY - (Math.abs(Math.sin(this.direction) * 25))), this.gen, this.direction);
    }
    else if (this.direction > 90 && this.direction < 180) {
      return new BulletXGen((int) (this.posX - Math.abs((Math.cos(this.direction) * 25))),
          (int) (this.posY - (Math.abs(Math.sin(this.direction) * 25))), this.gen, this.direction);
    }
    else if (this.direction > 180 && this.direction < 270) {
      return new BulletXGen((int) (this.posX - Math.abs((Math.cos(this.direction) * 25))),
          (int) (this.posY + (Math.abs(Math.sin(this.direction) * 25))), this.gen, this.direction);
    }
    else if (this.direction == 90) {
      return new BulletXGen(this.posX, this.posY - 25, this.gen, this.direction);
    }
    else if (this.direction == 180) {
      return new BulletXGen(this.posX - 25, this.posY, this.gen, this.direction);
    }
    else if (this.direction == 270) {
      return new BulletXGen(this.posX, this.posY + 25, this.gen, this.direction);
    }
    else {
      return new BulletXGen((int) (this.posX + (Math.cos(this.direction) * 25)),
          (int) (this.posY + (Math.sin(this.direction) * 25)), this.gen, this.direction);
    }
  }

  // checks if the bullet is off screen
  public boolean offScreen() {
    return this.posX > 510 || this.posY > 510 || this.posX < -10 || this.posY < -10;
  }

  // creates an x+1 gen bullet
  public ILoBullets nextGenBullet(ILoBullets rest, int posX, int posY) {
    return this.bullenXGenCreate(rest, posX, posY, this.gen, this.gen);
  }

  // creates x-number of x gen bullets
  ILoBullets bullenXGenCreate(ILoBullets rest, int posX, int posY, int gen, int count) {
    if (count >= 0) {
      return new ConsLoBullets(
          new BulletXGen(posX, posY, gen + 1, (int) (count * (360 / (gen + 1)))),
          this.bullenXGenCreate(rest, posX, posY, gen, count - 1));
    }
    else {
      return rest;
    }
  }
}

//BulletXGen xbullet = new BulletXGen(250, 250, 6, 240);
//BulletXGen xbullet2 = new BulletXGen(300, 250, 6, 270);
//
//
//
//double sum = Math.hypot(30, 30);
//
//boolean testRandom(Tester t) {
//  return t.checkExpect(this.bullets3.eliminate(), this.bulletsNew)
//      && t.checkExpect(this.empty.eliminate(), this.empty);
//}
//
//boolean testXGenMove(Tester t) {
//  return t.checkExpect(this.xbullet.moveBullet(), new BulletXGen(241, 273, 6, 240))
//      && t.checkExpect(this.xbullet2.moveBullet(), new BulletXGen(300, 275, 6, 270));
//}

//examples of bullets
class ExampleBullets {
  ExampleBullets() {
  }

  IShip milFalc = new RightShip(495, 300);
  IShip nave = new LeftShip(0, 250);

  ILoShips ships = new ConsLoShips(this.milFalc, new ConsLoShips(this.nave, new MtLoShips()));

  WorldScene scene = new WorldScene(500, 500);

  ILoBullets bullets = new ConsLoBullets(new NewBullet().createBullet(),
      new ConsLoBullets(new Bullet(528, 520, 1, 90), new MtLoBullets()));

  ILoBullets bullets1 = new ConsLoBullets(new NewBullet().createBullet(),
      new ConsLoBullets(new Bullet(495, 300, 1, 90),
          new ConsLoBullets(new Bullet(400, 300, 1, 90), new MtLoBullets())));
  WorldImage ebullet = new EmptyImage(); // new EmptyBullet(250, 500, 1, 90);
  WorldImage dbullet = new CircleImage(4, OutlineMode.SOLID, Color.BLUE);

  IBullet b1 = new Bullet(528, 300, 1, 90);
  IBullet b2 = new Bullet(495, 300, 1, 90);
  IBullet b3 = new Bullet(400, 300, 1, 90);

  ILoBullets bulletsNew = new ConsLoBullets(new NewBullet().createBullet(), new MtLoBullets());
  ILoBullets empty = new MtLoBullets();

  double sum = Math.hypot(30, 30);

  boolean testRandom(Tester t) {
    return t.checkExpect(this.bullets.eliminate(), this.bulletsNew)
        && t.checkExpect(this.empty.eliminate(), this.empty);
  }

  boolean test(Tester t) {
    return t.checkInexact(this.sum, 42.42640687119285, 0.01);
  }

  boolean testprintBullets(Tester t) {
    return t.checkExpect(this.bullets.printBullets(this.scene),
        this.scene.placeImageXY(this.dbullet, 250, 500).placeImageXY(this.dbullet, 528, 520)
            .placeImageXY(this.ebullet, 250, 500)) 
        && t.checkExpect(this.empty.printBullets(this.scene),
            this.scene.placeImageXY(this.ebullet, 250, 500));
  }

  boolean testmoveBullets(Tester t) {
    return t.checkExpect(this.bullets.moveBullets(),
        new ConsLoBullets(new Bullet(250, 475, 1, 90),
            new ConsLoBullets(new Bullet(528, 495, 1, 90), new MtLoBullets())))
        && t.checkExpect(this.empty.moveBullets(), this.empty);
  }

  boolean testspawnBullets(Tester t) {
    return t.checkExpect(this.bullets.spawnBullets(), this.bullets.spawnBullets())
        && t.checkExpect(this.empty.spawnBullets(), this.empty.spawnBullets());
  }

  boolean testEliminate(Tester t) {
    return t.checkExpect(this.bullets.eliminate(),
        new ConsLoBullets(new NewBullet().createBullet(), new MtLoBullets()))
        && t.checkExpect(this.bullets1.eliminate(), this.bullets1);
  }

  boolean testNonandOffScreen(Tester t) {
    return t.checkExpect(this.bullets.nonScreen(), false)
        && t.checkExpect(this.bullets1.nonScreen(), false)
        && t.checkExpect(this.b1.offScreen(), true) && t.checkExpect(this.b2.offScreen(), false);
  }

  boolean testAnyCollideAndCollide(Tester t) {
    return t.checkExpect(this.bullets.anyColide(400, 300), false)
        && t.checkExpect(this.bullets1.anyColide(400, 300), true)
        && t.checkExpect(this.b1.colide(400, 300), false)
        && t.checkExpect(this.b3.colide(400, 300), true);
  }

  boolean testDeleteBullet(Tester t) {
    return t.checkExpect(this.bullets.deleteBullet(400, 300), this.bullets) && t.checkExpect(
        this.bullets1.deleteBullet(400, 300), new ConsLoBullets(new NewBullet().createBullet(),
            new ConsLoBullets(new Bullet(495, 300, 1, 90), new MtLoBullets())));
  }

  boolean testRemoveBullet(Tester t) {
    return t.checkExpect(this.bullets.removeBullet(this.ships), this.bullets)
        && t.checkExpect(this.bullets1.removeBullet(this.ships),
            new ConsLoBullets(new NewBullet().createBullet(),
                new ConsLoBullets(new Bullet2Gen(495, 300, 2, 180),
                    new ConsLoBullets(new Bullet2Gen(495, 300, 2, 0),
                        new ConsLoBullets(new Bullet(400, 300, 1, 90), new MtLoBullets())))));
  }
}