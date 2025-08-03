package ipo.ipo_lab_1;


/**
 * Класс, представляющий костяшку домино
 */
public class DominoTile {
    private int leftValue, rightValue;
    private boolean isSelected;
    private boolean leftOpen, rightOpen;

    public DominoTile(int leftValue, int rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.leftOpen = true;
        this.rightOpen = true;
    }

    public int toCode(int leftValue, int rightValue) {
        int codePoint = 0x1F063;
        return codePoint + 7 * leftValue + rightValue;
    }

    public int toCode(DominoTile tile, boolean wide) {
        int codePoint = wide ? 0x1F031 : 0x1F063;
        return codePoint + 7 * tile.leftValue + tile.rightValue;
    }

    public int toCode() {
        int codePoint = 0x1F063;
        return codePoint + 7 * this.leftValue + this.rightValue;
    }

    public boolean isDouble() {
        return this.leftValue == this.rightValue;
    }

    public int getLeftValue() {
        return leftValue;
    }

    public int getRightValue() {
        return rightValue;
    }

    public boolean isLeftOpen() {
        return leftOpen;
    }

    public boolean isRightOpen() {
        return rightOpen;
    }

    public void flipDomino() {
        int temp = leftValue;
        leftValue = rightValue;
        rightValue = temp;

        boolean tempOpen = leftOpen;
        leftOpen = rightOpen;
        rightOpen = tempOpen;
    }

    public void closeRight() {
        this.rightOpen = false;
    }

    public void closeLeft() {
        this.leftOpen = false;
    }

    public int getSum() {
        return this.leftValue + this.rightValue;
    }

    @Override
    public String toString() {
        return leftValue + " | " + rightValue;
    }
}
