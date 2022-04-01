package path_finding;

import java.io.Serializable;

/**
 * 
 */
public class Map implements Serializable {
    private static final long serialVersionUID = 6181512265542053592L;

    private int columns = 10;
    private int rows = 10;
    private double dense = .5;
    private int capacity = columns * rows;
    private double density = capacity * .5;
    private int startx = -1;
    private int starty = -1;
    private int finishx = -1;
    private int finishy = -1;
    private Node[][] map;

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
        capacity = columns * rows;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        capacity = columns * rows;
    }
    
    public void setMapSize(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        this.capacity = columns * rows;
    }

    public double getDense() {
        return dense;
    }

    public void setDense(double dense) {
        this.dense = dense;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
    
    public void updateDensity() {
        this.density = capacity * dense;
    }

    public int getStartX() {
        return startx;
    }

    public void setStartX(int startx) {
        this.startx = startx;
    }

    public int getStartY() {
        return starty;
    }

    public void setStartY(int starty) {
        this.starty = starty;
    }

    public int getFinishX() {
        return finishx;
    }

    public void setFinishX(int finishx) {
        this.finishx = finishx;
    }

    public int getFinishY() {
        return finishy;
    }

    public void setFinishY(int finishy) {
        this.finishy = finishy;
    }

    public Node[][] getMap() {
        return map;
    }

    public void setMap(Node[][] map) {
        this.map = map;
    }
    
    public void setMap(Map m) {
        this.columns = m.getColumns();
        this.rows = m.getRows();
        this.dense = m.getDense();
        this.capacity = m.getCapacity();
        this.density = m.getDensity();
        this.startx = m.getStartX();
        this.starty = m.getStartY();
        this.finishx = m.getFinishX();
        this.finishy = m.getFinishY();
        this.map = m.getMap();
    }

    public void createNewMap() {
        this.map = new Node[columns][rows];
    }

    public Node getStartNode() {
        return map[startx][starty];
    }
    
    public Node getFinishNode() {
        return map[finishx][finishy];
    }

    public boolean hasStartNode() {
        return startx > -1 && starty > -1;
    }

    public boolean hasFinishNode() {
        return finishx > -1 && finishy > -1;
    }
    
    public int[][] getArrayIntMap() {
        int[][] result = new int[map.length][map.length];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                result[i][j] = map[i][j].getNo();
            }
        }
        return result;
    }
}