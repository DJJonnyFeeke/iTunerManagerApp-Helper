package me.personal.jfeeke.multimediamobileviewer;

import java.util.ArrayList;
import java.util.List;

class MyStringPair {
    private final String columnOne;
    private final String columnTwo;
    private final String columnThree;
    private final String columnFour;
    private final String columnFive;
    private final String columnSix;
    private final String columnSeven;

    private MyStringPair(String columnOne, String columnTwo, String columnThree, String columnFour, String columnFive, String columnSix, String columnSeven) {
        super();
        this.columnOne = columnOne;
        this.columnTwo = columnTwo;
        this.columnThree = columnThree;
        this.columnFour = columnFour;
        this.columnFive = columnFive;
        this.columnSix = columnSix;
        this.columnSeven = columnSeven;
    }

    String getColumnOne() {
        return columnOne;
    }
/*    public void setColumnOne(String columnOne) {
        this.columnOne = columnOne;
    }*/
    String getColumnTwo() {
        return columnTwo;
    }
/*    public void setColumnTwo(String columnTwo) {
        this.columnTwo = columnTwo;
    }*/
    String getColumnThree() {
        return columnThree;
    }
/*    public void setColumnThree(String columnThree) {
        this.columnThree = columnThree;
    }*/
    String getColumnFour() {
        return columnFour;
    }
/*    public void setColumnFour(String columnFour) {
        this.columnFour = columnFour;
    }*/
    String getColumnFive() {
        return columnFive;
    }
/*    public void setColumnFive(String columnFive) {
        this.columnFive = columnFive;
    }*/
    String getColumnSix() {
        return columnSix;
    }
/*    public void setColumnSix(String columnSix) {
        this.columnSix = columnSix;
    }*/
    String getColumnSeven() {
        return columnSeven;
    }
/*    public void setColumnSeven(String columnSeven) {
        this.columnSeven = columnSeven;
    }*/

    static ArrayList<MyStringPair> makeData(ArrayList<List<String>> n) {
        ArrayList<MyStringPair> pair = new ArrayList<>();
        for(int x=0;x<n.size();x++){
            List<String> currRow = n.get(x);
            pair.add(new MyStringPair(currRow.get(0), currRow.get(1), currRow.get(3), currRow.get(8), currRow.get(12), currRow.get(13), currRow.get(16)));
        }
        return pair;
    }
}
