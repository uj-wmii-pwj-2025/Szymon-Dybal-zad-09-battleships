package uj.wmii.pwj.collections.engine;

import java.util.Arrays;
import java.util.Random;

public class RandomBattleshipGenerator implements BattleshipGenerator {
    private char[][] board = new char[10][10];
    private int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private final Random random = new Random();
    private void emptyBoard(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                board[i][j] = '.';
            }
        }
    }

    private String convertToString(char[][] board){
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                output.append(board[i][j]);
            }
        }
        return output.toString();
    }

    private boolean checkArea(int row, int column){
        if (row < 0 || row >= 10 || column < 0 || column >= 10 || board[row][column] == '#') {
            return false;
        }
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                int checkR = row + r;
                int checkC = column + c;

                if (checkR >= 0 && checkR < 10 && checkC >= 0 && checkC < 10) {
                    if (board[checkR][checkC] == '#') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int addMainShipPoint(){
        int row;
        int column;
        int a = 0;
        while(a < 100){
            a++;
            row = random.nextInt(10);
            column = random.nextInt(10);
            if(checkArea(row, column)){
                board[row][column] = 'x';
                return row*10+column;
            }
        }
        return -1;
    }
    private boolean[] neighbours(int row, int column){
        boolean[] n = new boolean[4];
        Arrays.fill(n, false);

        if(row - 1 < 0 || board[row - 1][column] == '#' || board[row - 1][column] == 'x') {
            n[0] = true;
        }
        if(column + 1 > 9 || board[row][column + 1] == '#'  || board[row][column + 1] == 'x'){
            n[1] = true;
        }
        if(row + 1 > 9 || board[row + 1][column] == '#' || board[row + 1][column] == 'x'){
            n[2] = true;
        }
        if(column - 1 < 0 || board[row][column - 1] == '#' || board[row][column - 1] == 'x'){
            n[3] = true;
        }
        return n;
    }
    private void convert(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(board[i][j] == 'x'){
                    board[i][j] = '#';
                }
            }
        }
    }
    private void createShip(){
        int main = addMainShipPoint();
        int row = main/10;
        int column = main - row * 10;
        int next;
        boolean[] neighbours;
        int shipNumber = -1;
        boolean shipExpanded;
        int a;

        for(int i = 0; i < shipSizes.length; i++){
            if(shipSizes[i] != 0){
                shipNumber = i;
                break;
            }
        }
        for(int i = 1; i < shipSizes[shipNumber]; i++){
            shipExpanded = false;
            if(checkArea(row - 1, column) ||
                    checkArea(row, column + 1) ||
                    (checkArea(row + 1, column)) ||
                    (checkArea(row, column - 1))
            ){
                neighbours = neighbours(row, column);
                a = 0;
                while(!shipExpanded){
                    a++;
                    if( a == 40){
                        for(int j = 0; j < shipSizes.length; j++){
                            if(shipSizes[j] == i ){
                                shipSizes[j] = 0;
                                break;
                            }
                        }
                        convert();
                        return;
                    }
                    next = random.nextInt(4);
                    if(!neighbours[next]){
                        switch (next){
                            case(0):
                                if(checkArea(row - 1, column)){
                                    row = row - 1;
                                    board[row][column] = 'x';
                                    shipExpanded = true;
                                }
                                break;
                            case(1):
                                if(checkArea(row, column + 1)){
                                    column = column + 1;
                                    board[row][column] = 'x';
                                    shipExpanded = true;
                                }
                                break;
                            case(2):
                                if(checkArea(row + 1, column)){
                                    row = row + 1;
                                    board[row][column] = 'x';
                                    shipExpanded = true;
                                }
                                break;
                            case(3):
                                if(checkArea(row, column - 1)){
                                    column = column - 1;
                                    board[row][column] = 'x';
                                    shipExpanded = true;
                                }
                                break;
                        }
                    }
                }
            }else{
                for(int j = 0; j < shipSizes.length; j++){
                    if(shipSizes[j] == i ){
                        shipSizes[j] = 0;
                        break;
                    }
                }
                return;
            }
        }
        shipSizes[shipNumber] = 0;
        convert();
    }
    @Override
    public String generateMap() {
        emptyBoard();
        for(int i = 0; i < shipSizes.length; i++){
            createShip();
        }
        return convertToString(board);
    }
}
