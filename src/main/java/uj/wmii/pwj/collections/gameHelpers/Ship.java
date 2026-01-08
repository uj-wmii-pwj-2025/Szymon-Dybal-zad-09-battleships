package uj.wmii.pwj.collections.gameHelpers;

public class Ship {
    private int health;

    public Ship(int size) {
        this.health = size;
    }

    public void hit() {
        this.health--;
    }

    public boolean Destroyed() {
        return health <= 0;
    }
}
