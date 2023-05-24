
//import tester.*;
import javalib.funworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;
import java.util.Random;

//import javalib.funworld.*;
import java.awt.Color;

//Represents a list of ships
interface ILoShips {

  // puts all ships in a list into a scene
  WorldScene printShips(WorldScene scene);

  // moves all ships
  ILoShips move();

  // spawns ships randomly
  ILoShips spawn(int count);

  // limits the number of ships allowed to be spawned
  ILoShips remove(int elements);

  // removes a ship when a collision occurs
  ILoShips destroyed(ILoBullets bullets);

  // returns true is a ship is in the same place as a bullet
  boolean impacted(int bulletX, int bulletY, int size);

  // determines how many ships are being hit
  int willHit(int count, ILoBullets bullets);
}

//represents an empty list of ships
class MtLoShips implements ILoShips {
  MtLoShips() {
  }

  public WorldScene printShips(WorldScene scene) {
    return scene;
  }

  public ILoShips move() {
    return this;
  }

  public ILoShips spawn(int count) {
    if (count > 0) {
      return new ConsLoShips(new NewShip().newShip(), this.spawn(count - 1));
    }
    else {
      return this.remove(15);
    }
  }

  public ILoShips remove(int elements) {
    return this;
  }

  public ILoShips destroyed(ILoBullets bullets) {
    return this;
  }

  public boolean impacted(int bulletX, int bulletY, int size) {
    return false;
  }

  public int willHit(int count, ILoBullets bullets) {
    return count;
  }
}

//represents a list of ships (the ships in game)
class ConsLoShips implements ILoShips {
  IShip first;
  ILoShips rest;

  ConsLoShips(IShip first, ILoShips rest) {
    this.first = first;
    this.rest = rest;
  }

  // prints all the ships in a list onto a scene
  public WorldScene printShips(WorldScene scene) {
    scene = scene.placeImageXY(this.first.ship(), this.first.posX(), this.first.posY());
    scene = this.rest.printShips(scene);
    return scene;
  }

  // moves all the ships in the list
  public ILoShips move() {
    return new ConsLoShips(this.first.moveShip(), this.rest.move());
  }

  // spawns ships
  public ILoShips spawn(int count) {
    if (count > 0) {
      return new ConsLoShips(new NewShip().newShip(), this.spawn(count - 1));
    }
    else {
      return this.remove(15);
    }
  }

  // removes spawning more ships if there are already a
  // certain amount on screen
  public ILoShips remove(int elements) {
    if (elements > 0) {
      return new ConsLoShips(this.first, this.rest.remove(elements - 1));
    }
    else {
      return new ConsLoShips(this.first, new MtLoShips());
    }
  }

  // removes a ship from the list if it has collided with a bullet
  public ILoShips destroyed(ILoBullets bullets) {
    if (bullets.anyColide(this.first.posX(), this.first.posY())) {
      return this.rest.destroyed(bullets.deleteBullet(this.first.posX(), this.first.posY()));
    }
    else {
      return new ConsLoShips(this.first, this.rest.destroyed(bullets));
    }
  }

  // checks if a ship is in the same place as a bullet
  public boolean impacted(int bulletX, int bulletY, int size) {
    return this.first.impactBullet(bulletX, bulletY, size)
        || this.rest.impacted(bulletX, bulletY, size);
  }

  // returns how many bullets are exploding
  public int willHit(int count, ILoBullets bullets) {
    if (bullets.anyColide(this.first.posX(), this.first.posY())) {
      return this.rest.willHit(count + 1, bullets);
    }
    else {
      return this.rest.willHit(count, bullets);
    }
  }
}

// creates a new left or right ship
class NewShip {
  NewShip() {
  }

  // randomized if they start from the right or left side
  IShip newShip() {
    if (new Random().nextInt(2) == 1) {
      return new RightShip(500, 250 - new Random().nextInt(180));
    }
    else {
      return new LeftShip(0, 250 - new Random().nextInt(180));
    }
  }

}

interface IShip {

  // moves a ship
  IShip moveShip();

  // constructs the ship image
  WorldImage ship();

  // finds x position
  int posX();

  // finds y position
  int posY();

  // tells if a ship is being hit by a bullet
  boolean impactBullet(int bulletX, int bulletY, int size);
}

abstract class AShip implements IShip {
  int posX;
  int posY;
  WorldImage ship;

  AShip(int posX, int posY) {
    this.posX = posX;
    this.posY = posY;
    ship = new CircleImage(17, OutlineMode.SOLID, Color.RED);
  }

  // creation of a ship
  public WorldImage ship() {
    return this.ship;
  }

  // returns x position
  public int posX() {
    return this.posX;
  }

  // returns y position
  public int posY() {
    return this.posY;
  }

  // tells if a ship is being hit by a bullet
  @Override
  public boolean impactBullet(int bulletX, int bulletY, int size) {
    return (17 + size) >= Math.hypot((this.posX - bulletX), (posY - bulletY));
  }

}

//a ship starting from the right side
class RightShip extends AShip {
  RightShip(int posX, int posY) {
    super(posX, posY);
  }

  // moves a right ship to the left
  public IShip moveShip() {
    return new RightShip(this.posX - 10, this.posY);

  }
}

//a ship starting from the left side
class LeftShip extends AShip {
  LeftShip(int posX, int posY) {
    super(posX, posY);
  }

  // moves left ships the right way
  public IShip moveShip() {
    return new LeftShip(this.posX + 10, this.posY);
  }

}

class ExamplesShips {
  ExamplesShips() {
  }

  IShip milFalc = new RightShip(500, 250);
  IShip nave = new LeftShip(0, 250);

  ILoShips ships = new ConsLoShips(this.milFalc, new ConsLoShips(this.nave, new MtLoShips()));

  WorldScene scene = new WorldScene(500, 500);

  ILoBullets bullets1 = new ConsLoBullets(new Bullet(495, 300, 1, 90),
      new ConsLoBullets(new Bullet(400, 300, 1, 90), new MtLoBullets()));

  ILoShips ships1 = new ConsLoShips(new LeftShip(325, 325),
      new ConsLoShips(new RightShip(400, 300), new MtLoShips()));

  ILoShips ships2 = new ConsLoShips(new RightShip(500, 177), new ConsLoShips(
      new RightShip(495, 300), new ConsLoShips(new LeftShip(180, 300), new MtLoShips())));

  WorldImage eship = new EmptyImage();
  WorldImage dship = new CircleImage(17, OutlineMode.SOLID, Color.RED);

  IShip s1 = new RightShip(528, 300);
  IShip s2 = new RightShip(495, 300);
  IShip s3 = new LeftShip(122, 300);
  IShip s4 = new LeftShip(248, 300);

  ILoShips empty = new MtLoShips();

  Random seed = new Random(8);

  boolean testprintShips(Tester t) {
    return t.checkExpect(this.ships2.printShips(this.scene),
        this.scene.placeImageXY(this.dship, 500, 177).placeImageXY(this.dship, 495, 300)
            .placeImageXY(dship, 180, 300))
        && t.checkExpect(this.empty.printShips(this.scene),
            this.scene.placeImageXY(this.eship, 250, 500));
  }

  boolean testSpawn(Tester t) {
    return t.checkExpect(this.ships2.spawn(0), this.ships2)
        && t.checkExpect(this.empty.spawn(0), this.empty);
  }

  boolean testMoveShip(Tester t) {
    return t.checkExpect(this.ships2.move(),
        new ConsLoShips(new RightShip(490, 177),
            new ConsLoShips(new RightShip(485, 300),
                new ConsLoShips(new LeftShip(190, 300), new MtLoShips()))))
        && t.checkExpect(this.empty.move(), this.empty)
        && t.checkExpect(this.milFalc.moveShip(), new RightShip(490, 250))
        && t.checkExpect(this.nave.moveShip(), new LeftShip(10, 250));
  }

  boolean testRemove(Tester t) {
    return t.checkExpect(this.ships.remove(0), new ConsLoShips(this.milFalc, this.empty))
        && t.checkExpect(this.ships2.remove(5), this.ships2);
  }

  boolean testDestroyed(Tester t) {
    return t.checkExpect(this.ships.destroyed(this.bullets1), this.ships)
        && t.checkExpect(this.ships1.destroyed(this.bullets1),
            new ConsLoShips(new LeftShip(325, 325), new MtLoShips()));
  }

  boolean testImpacted(Tester t) {
    return t.checkExpect(this.ships.impacted(1, 499, 126), false)
        && t.checkExpect(this.ships1.impacted(1, 325, 325), true);
  }

  boolean testWillHit(Tester t) {
    return t.checkExpect(this.ships.willHit(0, this.bullets1), 0)
        && t.checkExpect(this.ships1.willHit(0, this.bullets1), 1);
  }
}
