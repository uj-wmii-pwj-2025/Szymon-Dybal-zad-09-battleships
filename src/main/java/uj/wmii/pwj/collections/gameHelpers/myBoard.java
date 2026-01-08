package uj.wmii.pwj.collections.gameHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class myBoard {
    private final Map<point, Ship> shipMap = new HashMap<>();
    private int shipsAliveCount = 0;
    private final boolean[][] shotHistory = new boolean[10][10];
    public myBoard(String mapString){
        parseMap(mapString);
    }
    private void parseMap(String mapString){
        char[][] map = new char[10][10];
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                map[i][j] = mapString.charAt(i * 10 + j);
            }
        }

        boolean[][] visited = new boolean[10][10];

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(map[i][j] == '#' && !visited[i][j]){
                    List<point> segments = new ArrayList<>();
                    findWholeShip(j, i, map, visited, segments);
                    Ship ship = new Ship(segments.size());
                    shipsAliveCount++;

                    for(point p : segments){
                        shipMap.put(p, ship);
                    }
                }
            }
        }
    }
    private void findWholeShip(int x, int  y, char[][] map, boolean[][] visited, List<point> segments){
        if(x < 0 || y < 0 || x >= 10 || y >= 10)return;
        if(visited[y][x] || map[y][x] != '#') return;

        visited[y][x] = true;
        segments.add(new point(x, y));
        findWholeShip(x - 1, y, map, visited, segments);
        findWholeShip(x + 1, y, map, visited, segments);
        findWholeShip(x, y - 1, map, visited, segments);
        findWholeShip(x, y + 1, map, visited, segments);
    }
    public  ShotResult shoot(point p){
        if(shotHistory[p.y()][p.x()]){
            if(shipMap.containsKey(p)) {
                Ship s = shipMap.get(p);
                if(s.Destroyed()){
                    return ShotResult.TRAFIONY_ZATOPIONY;
                }
                return ShotResult.TRAFIONY;
            }
            return ShotResult.PUDLO;
        }
        shotHistory[p.y()][p.x()] = true;

        Ship ship = shipMap.get(p);

        if (ship == null) {
            return ShotResult.PUDLO;
        }

        ship.hit();

        if (ship.Destroyed()) {
            shipsAliveCount--;
            if (shipsAliveCount == 0) {
                return ShotResult.WSZYSTKIE_ZATOPIONE;
            }
            return ShotResult.TRAFIONY_ZATOPIONY;
        }

        return ShotResult.TRAFIONY;
    }


}
