package uj.wmii.pwj.collections.gameHelpers;

public record point(int x, int y) {
    public static point fromString(String coords) {
        int x = coords.charAt(0) - 'A';
        int y = Integer.parseInt(coords.substring(1)) - 1;
        return new point(x, y);
    }
}
