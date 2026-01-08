package uj.wmii.pwj.collections.engine;

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new RandomBattleshipGenerator();
    }

}
