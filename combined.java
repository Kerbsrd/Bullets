//import tester.*;
//import javalib.worldimages.*;
//import javalib.funworld.*;
//import javalib.worldcanvas.WorldCanvas;
//import java.awt.Color;
//import java.util.Random;
//
//interface IPart {
//
//  IPart move();
//
//  WorldScene print(WorldScene scene);
//
//  boolean offScreen();
//
//  boolean collide(int otherX, int otherY);
//
//}
//
//abstract class APart implements IPart {
//  int posX;
//  int posY;
//  int size;
//  WorldImage part;
//
//  APart(int posX, int posY, int size) {
//    this.posX = posX;
//    this.posY = posY;
//    this.size = size;
//  }
//
//  public abstract IPart move();
//
//  public abstract WorldScene print(WorldScene scene);
//
//  public boolean offScreen() {
//    if (posX > 501 || posX < 0 || posY < 0 || posY > 501) {
//      return true;
//    }
//    else {
//      return false;
//    }
//  }
//
//  public boolean collide(int otherX, int otherY) {
//    return (17 + this.size) >= Math.hypot((this.posX - otherX), (posY - otherY));
//  }
//
//}
//
//class Bullet extends APart {
//  int gen;
//  int size;
//  WorldImage bullet;
//
//  Bullet(int posX, int posY, int size, int gen) {
//    super(posX, posY, size);
//    this.gen = gen;
//    this.size = gen * 2;
//    this.bullet = new CircleImage(this.size, OutlineMode.SOLID, Color.BLUE);
//  }
//
//  @Override
//  public IPart move() {
//    return new Bullet(this.posX, this.posY - 25, this.size, this.gen);
//  }
//
//  @Override
//  public WorldScene print(WorldScene scene) {
//    scene = scene.placeImageXY(this.bullet, this.posX, this.posY);
//    return scene;
//  }
//}
//
/////////////////////////////////////////////////////////////////
//
//class RShip extends APart {
//  WorldImage ship;
//
//  RShip(int posX, int posY, int size) {
//    super(posX, posY, size);
//    this.size = 17;
//    ship = new CircleImage(17, OutlineMode.SOLID, Color.RED);
//  }
//
//  @Override
//  public IPart move() {
//    return new RShip(this.posX - 10, this.posY, 17);
//  }
//
//  @Override
//  public WorldScene print(WorldScene scene) {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//}
//
//class LShip extends APart {
//  WorldImage ship;
//
//  LShip(int posX, int posY) {
//    super(posX, posY);
//    ship = new CircleImage(17, OutlineMode.SOLID, Color.RED);
//  }
//
//  @Override
//  public IPart move() {
//    return new LShip(this.posX + 10, this.posY);
//  }
//}
//
//interface ILoPart {
//
//  WorldScene print(WorldScene scene);
//
//  ILoPart moveList();
//
//  ILoPart spawnList();
//
//  ILoPart remove();
//
//  boolean nonScreen();
//
//  boolean anyCollide();
//
//  ILoPart destroyed(ILoPart other);
//
//}
//
//abstract class AMtLoPart implements ILoPart {
//
//}
//
//abstract class ALoPart implements ILoPart {
//
//}
//
//  @Override
//  public IPart move() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//@Override
//public WorldScene print(WorldScene scene) {
//  // TODO Auto-generated method stub
//  return null;
//}
