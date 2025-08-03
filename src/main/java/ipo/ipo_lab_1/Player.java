package ipo.ipo_lab_1;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Класс, представляющий игрока
 */
public class Player {

    public ArrayList<DominoTile> hand;
    public void setMyHand(ArrayList<DominoTile> myHand) {
        hand = myHand;
    }

    private int turn;

    /**
     * Метод для взятия костяшки из базара в колоду игрока
     * @param boneyard базар, содержащий оставшиеся 14 костяшек
     */
    public void drawFromBoneyard(Boneyard boneyard) {
        if (!boneyard.isBoneyardEmpty()) {
            DominoTile drawDominoTile = boneyard.getBoneyardTiles().getFirst();
            hand.add(drawDominoTile);
            boneyard.getBoneyardTiles().remove(drawDominoTile);
        } else {
            System.out.println("Boneyard is empty");
        }
    }

    public ArrayList<DominoTile> getHand() {
        return hand;
    }
    public boolean isEmptyHand() {
        return hand.isEmpty();
    }
    public int getSize() {
        return hand.size();
    }


    /**
     * Метод для поиска дубля в колоде игрока
     * @return самый большой дубль
     */
    public DominoTile findHighestDouble() {
        DominoTile highest = null;
        for (DominoTile tile : hand) {
            if (tile.isDouble() && tile.getSum() != 0) {
                if (highest == null || tile.getSum() > highest.getSum()) {
                    highest = tile;
                }
            }
        }
        return highest;
    }

    /**
     * Если @findHighestDouble метод вернет null,
     * то вызывается этот метод для поиска стартовой костяшки в колоде игрока
     * @return кость с самой высокой суммой сторон
     */
    public DominoTile findHighestTile() {
        return hand
                .stream()
                .max(Comparator.comparingInt(DominoTile::getSum))
                .orElse(null);
    }

    public boolean playerHasLegitMove(Board board) {
        if (board.isEmpty()) {
            return true;
        }

        if (hand.isEmpty()) {
            return false;
        }

        int leftValueOnBoard = board.getLeftmostTile().getLeftValue();
        int rightValueOnBoard = board.getRightmostTile().getRightValue();

        for (DominoTile tile : hand) {
            int leftValueInHand = tile.getLeftValue();
            int rightValueInHand = tile.getRightValue();

            if (leftValueInHand == leftValueOnBoard
                    || leftValueInHand == rightValueOnBoard
                    || rightValueInHand == leftValueOnBoard
                    || rightValueInHand == rightValueOnBoard) {
                return true;
            }
        }

        return false;
    }

    public int countTurns() {
        return turn;
    }

    public int countScores() {
        int score = 0;
        for (DominoTile tile : hand) {
            score += tile.getLeftValue() + tile.getRightValue();
        }
        return score;
    }
}
