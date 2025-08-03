package ipo.ipo_lab_1;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Класс, который инициализирует все костяшки домино перед их раздачей игрокам.
 * Хранит оставшиеся костяшки, пока они не понадобятся для взятия из базара
 */
public class Boneyard {
    public ArrayList<DominoTile> dominoTiles;
    public ArrayList<DominoTile> playerHands;

    public Boneyard() {
        dominoTiles = new ArrayList<>();
    }

    public void generateAllTiles() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                dominoTiles.add(new DominoTile(i, j));
            }
        }
        shuffleTiles();
    }

    private void shuffleTiles() {
        Collections.shuffle(dominoTiles);
    }
    public ArrayList<DominoTile> getBoneyardTiles() {
        return dominoTiles;
    }
    public boolean isBoneyardEmpty() {
        return dominoTiles.isEmpty();
    }
    public int getBoneyardListSize() {
        return dominoTiles.size();
    }


    public ArrayList<DominoTile> generatePlayerHands() {
        playerHands = new ArrayList<>();

        for (int i = 0; i <= 6; i++) {
            DominoTile tile = dominoTiles.get(i);
            playerHands.add(tile);
            dominoTiles.remove(tile);
        }

        return playerHands;
    }
}
