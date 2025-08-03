package ipo.ipo_lab_1;

import java.util.ArrayList;

public class Board {
    public ArrayList<DominoTile> board;

    Board() {
        this.board = new ArrayList<>();
    }

    public ArrayList<DominoTile> getBoard() {
        return board;
    }

    public int getBoardSize() {
        return board.size();
    }

    public boolean isEmpty() {
        return board.isEmpty();
    }

    public void clearBoard() {
        board.clear();
    }

    /**
     * Проверяет легальность хода
     * @return boolean значение легальности хода
     */
    public boolean isLegalMove(DominoTile deckTile) {
        System.out.println(board);

        if (board.isEmpty()) {
            return true;
        } else {
                return getLeftmostTile().getLeftValue() == deckTile.getRightValue()
                        || getLeftmostTile().getLeftValue() == deckTile.getLeftValue()
                        || getRightmostTile().getRightValue() == deckTile.getRightValue()
                        || getRightmostTile().getRightValue() == deckTile.getLeftValue();
        }
    }

    public DominoTile getRightmostTile() {
        return board.getLast();
    }
    public DominoTile getLeftmostTile() {
        return board.getFirst();
    }
}
