package ipo.ipo_lab_1;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
    @FXML
    private GridPane workSpace;
    @FXML
    private Pane gamePane;
    @FXML
    private GridPane gameDeck;
    @FXML
    private HBox boneyardGrid;
    @FXML
    private Button quit;
    @FXML
    private Button restart;
    private Boneyard boneyard;
    private DominoTileRenderer tileRenderer;
    private Player player, computer;
    private boolean isPlayerTurn;
    private Board board;
    private DominoTile startingTile;
    private Map<DominoTile, ImageView> tileImageViewMap;

    public GameController() {
        board = new Board();
        player = new Player();
        computer = new Player();
        boneyard = new Boneyard();
        tileRenderer = new DominoTileRenderer();
        isPlayerTurn = false;
        tileImageViewMap = new HashMap<>();
    }

    public void startGame() {
        startingTile = determineFirstPlayer(player, computer);

        if (!isPlayerTurn) {
            onComputerMove();
        }
    }


    /**
     * логика для хода компьютера. выберет первую, легальную для хода, костяшку
     */
    public void onComputerMove() {
        System.out.println("Computer's turn");

        if (isPlayerTurn) {
            return;
        }
        // если ход первый -- размещаем starting tile
        if (board.isEmpty()) {
            ImageView dominoImageView = createDominoImageView(startingTile);
            // поворачиваем кость
            if (!startingTile.isDouble()) {
                dominoImageView.setRotate(-90);
            }

            dominoImageView.setLayoutX(gamePane.getPrefWidth() / 2);
            dominoImageView.setLayoutY(gamePane.getPrefHeight() / 2);

            board.getBoard().add(startingTile);
            computer.getHand().remove(startingTile);
            gamePane.getChildren().add(dominoImageView);

            isPlayerTurn = true;
            return;
        }

        boolean search = true;

        while (search) {
            for (int i = 0; i < computer.getHand().size(); i++) {
                if (computer.getHand().get(i).getLeftValue() == board.getRightmostTile().getRightValue()) {
                    computerAddToEnd(board, i);
                    search = false;
                    break;
                } else if (computer.getHand().get(i).getLeftValue() == board.getLeftmostTile().getLeftValue()) {
                    computerAddToBeginning(board, i);
                    search = false;
                    break;
                } else if (computer.getHand().get(i).getRightValue() == board.getLeftmostTile().getLeftValue()) {
                    computerAddToBeginning(board, i);
                    search = false;
                    break;
                } else if (computer.getHand().get(i).getRightValue() == board.getRightmostTile().getRightValue()) {
                    computerAddToEnd(board, i);
                    search = false;
                    break;
                }
            }

            if (search) {
                computer.drawFromBoneyard(boneyard);
                System.out.println("Computer draws from the boneyard");
            }
            if (boneyard.isBoneyardEmpty()) break;
        }

        isPlayerTurn = true;
    }


    /**
     * вспомогательный метод для onComputerMove(). позволяет добавить костяшку домино в конец цепочки на игровом поле
     * @param board игровое поле
     * @param i индекс костяшки в колоде компьютера, с которой компьютер совершит ход
      */
    public void computerAddToEnd(Board board, int i) {
        DominoTile tile = computer.getHand().get(i);
        double spacing = tile.isDouble() ? -10 : 2;

        System.out.println("Computer plays " + tile + " at right");
        ImageView placedTileView = tileImageViewMap.get(board.getRightmostTile());
        DominoTile placedTile = board.getRightmostTile();
        ImageView dominoImageView = createDominoImageView(tile);
        if (placedTile.isRightOpen()) {
            switch (gamePaneIsFullFromRight()) {
                case 3:
                    // по правой стороне вниз
                    spacing = 52;
                    if (!tile.isDouble()) {
                        if (placedTile.getRightValue() == tile.getRightValue()) {
                            dominoImageView.setRotate(180);
                            tile.flipDomino();
                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX());
                    dominoImageView.setLayoutY(placedTileView.getLayoutY() + placedTileView.getFitHeight() + spacing);
                    break;
                case 4:
                    // по нижней стороне влево
                    if (!tile.isDouble()) {
                        if (placedTile.getRightValue() == tile.getLeftValue()) {
                            dominoImageView.setRotate(90);
                            //tile.flipDomino();
                        } else if (placedTile.getRightValue() == tile.getRightValue()) {
                            tile.flipDomino();
                            // dominoImageView = createDominoImageView(tile);
                            dominoImageView.setRotate(-90);


                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX() - placedTileView.getFitWidth() - spacing);
                    dominoImageView.setLayoutY(placedTileView.getLayoutY());
                    break;
                case 0:
                    // обычный ход
                    if (!tile.isDouble()) {
                        if (placedTile.getRightValue() == tile.getLeftValue()) {
                            dominoImageView.setRotate(-90);
                        } else if (placedTile.getRightValue() == tile.getRightValue()) {
                            dominoImageView.setRotate(90);
                            tile.flipDomino();
                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX() + placedTileView.getFitWidth() + spacing);
                    dominoImageView.setLayoutY(placedTileView.getLayoutY());
                    break;
            }
            tile.closeLeft();
            board.getBoard().addLast(tile);
            placedTile.closeRight();
            computer.getHand().remove(i);
            gamePane.getChildren().add(dominoImageView);
        }
    }



    /**
     * вспомогательный метод для onComputerMove(). позволяет добавить костяшку домино в начало цепочки на игровом поле
     * @param board игровое поле
     * @param i индекс костяшки в колоде компьютера, с которой компьютер совершит ход
     */
    public void computerAddToBeginning(Board board, int i) {
        DominoTile tile = computer.getHand().get(i);
        double spacing = tile.isDouble() ? -10 : 2;

        System.out.println("Computer plays " + tile + " at left");
        ImageView placedTileView = tileImageViewMap.get(board.getLeftmostTile());
        ImageView dominoImageView = createDominoImageView(tile);
        DominoTile placedTile = board.getLeftmostTile();

        if (placedTile.isLeftOpen()) {
            switch (gamePaneIsFullFromLeft()) {
                // по левой стороне вверх
                case 1:
                    spacing = 52;
                    if (!tile.isDouble()) {
                        if (placedTile.getLeftValue() == tile.getLeftValue()) {
                            // зеркалим костяшку
                            dominoImageView.setRotate(180);
                            tile.flipDomino();
                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX());
                    dominoImageView.setLayoutY(placedTileView.getLayoutY() - placedTileView.getFitHeight() - spacing);

                    break;
                // по верхней стороне вправо
                case 2:
                    if (!tile.isDouble()) {
                        if (placedTile.getLeftValue() == tile.getLeftValue()) {
                            tile.flipDomino();
                            dominoImageView.setRotate(-90);
                        } else if (placedTile.getLeftValue() == tile.getRightValue()) {
                            dominoImageView.setRotate(90);
                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX() + placedTileView.getFitWidth() + spacing);
                    dominoImageView.setLayoutY(placedTileView.getLayoutY() + placedTileView.getFitHeight());
                    break;

                case 0:
                    // обычная логика левой стороны
                    if (!tile.isDouble()) {
                        if (placedTile.getLeftValue() == tile.getLeftValue()) {
                            dominoImageView.setRotate(90);
                            tile.flipDomino();
                        } else if (placedTile.getLeftValue() == tile.getRightValue()) {
                            dominoImageView.setRotate(-90);
                        }
                    }
                    dominoImageView.setLayoutX(placedTileView.getLayoutX() - dominoImageView.getFitWidth() - spacing);
                    dominoImageView.setLayoutY(placedTileView.getLayoutY());
                    break;
            }

            board.getBoard().addFirst(tile);
            tile.closeRight();
            placedTile.closeLeft();
            computer.getHand().remove(i);
            gamePane.getChildren().add(dominoImageView);
        }
    }


    public DominoTile determineFirstPlayer(Player player, Player computer) {
        DominoTile playerHighestDouble = player.findHighestDouble();
        DominoTile computerHighestDouble = computer.findHighestDouble();

        if (playerHighestDouble != null
                && (computerHighestDouble == null
                || playerHighestDouble.getLeftValue() > computerHighestDouble.getLeftValue())) {
            isPlayerTurn = true;
            return playerHighestDouble;
        } else if (computerHighestDouble != null) {
            isPlayerTurn = false;
            return computerHighestDouble;
        }

        // если ни у кого нет дубля, сравниваем по самой высокой сумме сторон
        DominoTile playerHighestTile = player.findHighestTile();
        DominoTile computerHighestTile = computer.findHighestTile();

        if (playerHighestTile.getSum() > computerHighestTile.getSum()) {
            isPlayerTurn = true;
            return playerHighestTile;
        } else {
            isPlayerTurn = false;
            return computerHighestTile;
        }
    }



    public void renderPlayerHand() {
        gameDeck.setHgap(5);
        gameDeck.setPadding(new Insets(0,0,0,10));
        gameDeck.setAlignment(Pos.CENTER);

        gameDeck.getChildren().clear();

        for (int i = 0; i < player.getSize(); i++) {
            DominoTile tile = player.getHand().get(i);
            ImageView tileImageView = createDominoImageView(tile);
            addEventHandlersToDomino(tileImageView);
            gameDeck.add(tileImageView, i, 1);
        }
    }

    public void handleDominoTileClick(ImageView imageView) {
        DominoTile dominoTile = (DominoTile) imageView.getUserData();
        System.out.println(dominoTile);
    }

    private boolean isOverlapping(ImageView imageView) {
        for (Node node : gamePane.getChildren()) {
            if (node != imageView
                    && node.getBoundsInParent().intersects(imageView.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void closeProgram(ActionEvent event) {
        if(event.getSource() == quit) {
            System.exit(0);
        }
    }


    @FXML
    private void restart(ActionEvent event) {
        if(event.getSource() == restart) {
            gameDeck.getChildren().clear();
            gamePane.getChildren().clear();
            board.clearBoard();
            boneyard.getBoneyardTiles().clear();
            gamePane.getChildren().remove(boneyardGrid);
            workSpace.requestFocus();
            boneyard.generateAllTiles();
            player.setMyHand(boneyard.generatePlayerHands());
            computer.setMyHand(boneyard.generatePlayerHands());
            renderPlayerHand();
            startGame();
        }
    }

    private ImageView createDominoImageView(DominoTile tile) {
        int code = tile.toCode(tile.getLeftValue(), tile.getRightValue());
        WritableImage dominoTile = tileRenderer.drawTile(code);

        ImageView imageView = new ImageView(dominoTile);

        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);

        // связываем imageView с его объектом dominoTile
        imageView.setUserData(tile);
        // связываем dominoTile с его ImageView
        tileImageViewMap.put(tile, imageView);

        return imageView;
    }

    private void addEventHandlersToDomino(ImageView imageView) {
        imageView.setOnMouseClicked(_ -> handleDominoTileClick(imageView));

        imageView.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("on drag detected");

                Dragboard dragboard = imageView.startDragAndDrop(TransferMode.ANY);

                WritableImage snapshot = new WritableImage((int) imageView.getBoundsInParent().getWidth(),
                        (int) imageView.getBoundsInParent().getHeight());
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                imageView.snapshot(parameters, snapshot);


                ClipboardContent content = new ClipboardContent();
                content.putImage(snapshot);
                dragboard.setContent(content);

                mouseEvent.consume();
            }
        });

        imageView.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("on drag done");

                if (dragEvent.getTransferMode() == TransferMode.MOVE) {
                    //gameDeck.getChildren().remove(imageView);
                    // onPlayerMove();
                    // onComputerMove();
                }
                dragEvent.consume();

            }
        });
    }


    /**
     * Этот метод производит манипуляции с костяшкой, которую хотят бросить на игровое поле.
     * По необходимости делает поворот костяшки, также при необходимости меняет стороны местами.
     * Самое главное: располагает костяшку с заданным отступом на игровом поле и добавляет её в массив board
     * @param sourceImageView ImageView костяшки, которую хотят расположить на поле
     * @param sourceTile Связанный с ImageView объект DominoTile. Нужен для добавления в массив board,
     *                  перестановки сторон и одной проверки: к каким сторонам костяшки возможно добавление новой
     *                  (вместе с этим закрытием одной из сторон, при успешном размещении на игровом поле).
     * @param mouseX координата мыши по позиции X в момент вызова метода
     * @param mouseY координата мыши по позиции Y в момент вызова метода
     */
    public void snapTileToNearestSide(ImageView sourceImageView, DominoTile sourceTile, double mouseX, double mouseY) {
        double spacing;

        // если доска пустая
        if (board.isEmpty()) {
            if (!sourceTile.isDouble()) {
                sourceImageView.setRotate(-90);
            }
            board.getBoard().add(sourceTile);
            return;
        }

        // получаем крайние костяшки
        DominoTile leftmostTile = board.getBoard().getFirst();
        DominoTile rightmostTile = board.getBoard().getLast();
        ImageView leftmostTileView = tileImageViewMap.get(leftmostTile);
        ImageView rightmostTileView = tileImageViewMap.get(rightmostTile);

        double distanceToLeft = Math.sqrt(
                Math.pow(mouseX - leftmostTileView.getLayoutX(), 2) +
                Math.pow(mouseY - (leftmostTileView.getLayoutY() + leftmostTileView.getFitHeight() / 2), 2)
        );

        double distanceToRight = Math.sqrt(
                Math.pow(mouseX - (rightmostTileView.getLayoutX() + rightmostTileView.getFitWidth()), 2) +
                        Math.pow(mouseY - (rightmostTileView.getLayoutY() + rightmostTileView.getFitHeight() / 2), 2)
        );

        // логика для левой стороны
        if (distanceToLeft < distanceToRight && leftmostTile.isLeftOpen()) {
            spacing = sourceTile.isDouble() ? -10 : 2;
            switch (gamePaneIsFullFromLeft()) {
                // по левой стороне вверх
                case 1:
                    spacing = 52;
                    if (!sourceTile.isDouble()) {
                        if (leftmostTile.getLeftValue() == sourceTile.getLeftValue()) {
                            sourceImageView.setRotate(180);
                            sourceTile.flipDomino();
                        }
                    }
                    sourceImageView.setLayoutX(leftmostTileView.getLayoutX());
                    sourceImageView.setLayoutY(leftmostTileView.getLayoutY() - sourceImageView.getFitHeight() - spacing);
                    break;
                // по верхней стороне вправо
                case 2:
                    if (!sourceTile.isDouble()) {
                        if (leftmostTile.getLeftValue() == sourceTile.getLeftValue()) {
                            sourceTile.flipDomino();
                            sourceImageView.setRotate(-90);
                        } else if (leftmostTile.getLeftValue() == sourceTile.getRightValue()) {
                            sourceImageView.setRotate(90);
                        }
                    }

                    sourceImageView.setLayoutX(leftmostTileView.getLayoutX() + leftmostTileView.getFitWidth() + spacing);
                    sourceImageView.setLayoutY(leftmostTileView.getLayoutY());
                    break;

                case 0:
                    // обычная логика для левой стороны
                    spacing = sourceTile.isDouble() ? -10 : 2;
                    if (!sourceTile.isDouble()) {
                        if (leftmostTile.getLeftValue() == sourceTile.getLeftValue()) {
                            sourceImageView.setRotate(90);
                            sourceTile.flipDomino();
                        } else if (leftmostTile.getLeftValue() == sourceTile.getRightValue()) {
                            sourceImageView.setRotate(-90);
                        }


                        sourceImageView.setLayoutX(leftmostTileView.getLayoutX() - sourceImageView.getFitWidth() - spacing);
                        sourceImageView.setLayoutY(leftmostTileView.getLayoutY());
                    }
                    break;
            }

            sourceTile.closeRight();
            board.getBoard().addFirst(sourceTile);
            leftmostTile.closeLeft();
        }
        // логика для правой стороны
        else if (distanceToRight < distanceToLeft && rightmostTile.isRightOpen()) {
            spacing = sourceTile.isDouble() ? -10 : 2;
            switch (gamePaneIsFullFromRight()) {

                case 3:
                    spacing = 52;
                    // по правой стороне вниз
                    if (!sourceTile.isDouble()) {
                       if (rightmostTile.getRightValue() == sourceTile.getRightValue()) {
                            sourceImageView.setRotate(180);
                            sourceTile.flipDomino();
                       }
                    }

                    sourceImageView.setLayoutX(rightmostTileView.getLayoutX());
                    sourceImageView.setLayoutY(rightmostTileView.getLayoutY() + rightmostTileView.getFitHeight() + spacing);

                    break;

                case 4:
                    // по нижней стороне влево
                    if (!sourceTile.isDouble()) {
                        if (rightmostTile.getRightValue() == sourceTile.getLeftValue()) {
                            sourceImageView.setRotate(90);
                        } else if (rightmostTile.getRightValue() == sourceTile.getRightValue()) {
                            sourceTile.flipDomino();
                            sourceImageView.setRotate(-90);

                        }
                    }

                    sourceImageView.setLayoutX(rightmostTileView.getLayoutX() - rightmostTileView.getFitWidth() - spacing);
                    sourceImageView.setLayoutY(rightmostTileView.getLayoutY());
                    break;

                case 0:
                    // типичная логика правой стороны
                    spacing = sourceTile.isDouble() ? -10 : 2;
                    if (!sourceTile.isDouble()) {
                        if (rightmostTile.getRightValue() == sourceTile.getLeftValue()) {
                            sourceImageView.setRotate(-90);
                        } else if (rightmostTile.getRightValue() == sourceTile.getRightValue()) {
                            sourceImageView.setRotate(90);
                            sourceTile.flipDomino();
                        }
                    }

                    sourceImageView.setLayoutX(rightmostTileView.getLayoutX() + rightmostTileView.getFitWidth() + spacing);
                    sourceImageView.setLayoutY(rightmostTileView.getLayoutY());
                    break;
            }


            sourceTile.closeLeft();
            board.getBoard().addLast(sourceTile);
            rightmostTile.closeRight();
        }
    }


    int gamePaneIsFullFromLeft() {
        DominoTile leftmostTile = board.getBoard().getFirst();
        ImageView leftmostTileView = tileImageViewMap.get(leftmostTile);

        // проверяем, выходим ли за границы слева
        double leftEdge = leftmostTileView.getLayoutX() - (2 * leftmostTileView.getFitWidth());
        // проверяем, выходим ли за границы сверху
        double topEdge = leftmostTileView.getLayoutY() - (2 * leftmostTileView.getFitWidth());
        if (leftEdge < 0 && topEdge > 0) {
            return 1; // левая сторона
        }

        if (topEdge < 0) {
            return 2; // верхняя сторона
        }
        return 0; // не вышли за границы
    }

    int gamePaneIsFullFromRight() {
        DominoTile rightmostTile = board.getBoard().getLast();
        ImageView rightmostTileView = tileImageViewMap.get(rightmostTile);

        // проверяем, выходим ли за границу справа
        double rightEdge = rightmostTileView.getLayoutX() + (2 * rightmostTileView.getFitWidth());
        // проверяем, выходим ли за границу снизу
        double bottomEdge = rightmostTileView.getLayoutY() + (3 * rightmostTileView.getFitWidth());
        if (rightEdge > gamePane.getWidth() && bottomEdge < gamePane.getHeight()) {
            return 3; // правая сторона
        }
        if (bottomEdge > gamePane.getHeight()) {
            return 4; // нижняя сторона
        }

        return 0; // не вышли за границы
    }

    public void checkWinCondition() {
        // проверяем, пуст ли базар и кто из игроков остался без костей в колоде
        if (boneyard.isBoneyardEmpty() && (player.isEmptyHand() || computer.isEmptyHand())) {
            if (player.isEmptyHand()) {
                showGameOverBanner("Congratulations! You win!");
            } else if (computer.isEmptyHand() || !player.playerHasLegitMove(board)) {
                showGameOverBanner("Sorry, you lost. Computer wins.");
            }
        }
    }


    private void showGameOverBanner(String message) {
        Label banner = new Label(message);
        banner.setStyle("-fx-font-size: 30px; -fx-text-fill: white; -fx-background-color: black;");
        banner.setPadding(new Insets(20));
        banner.setAlignment(Pos.CENTER);

        StackPane bannerContainer = new StackPane(banner);

        bannerContainer.setPrefSize(gamePane.getWidth(), gamePane.getHeight());
        bannerContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7)");

        gamePane.getChildren().add(bannerContainer);
        bannerContainer.toFront();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            gamePane.getChildren().remove(bannerContainer);
            restart(event);
        });
        delay.play();
    }


    private void handleBoneyardDrawing() {
        boneyardGrid.setOnMouseClicked(event -> {
            hideBoneyardView();
        });

        boneyardGrid.setOnMouseClicked(event -> {
            if (!player.playerHasLegitMove(board) && boneyard.isBoneyardEmpty()) {
                hideBoneyardView();
                checkWinCondition();
            }
        });

        showBoneyardView();
    }

    private void hideBoneyardView() {
        boneyardGrid.setVisible(false);
        boneyardGrid.setDisable(true);
        boneyardGrid.setMouseTransparent(true);
        boneyardGrid.toBack();
        boneyardGrid.getChildren().clear();
        gamePane.getChildren().remove(boneyardGrid);
    }

    private void showBoneyardView() {

        // добавляем оверлей на игровую панель
        if (!gamePane.getChildren().contains(boneyardGrid)) {
            gamePane.getChildren().add(boneyardGrid);
        }

        boneyardGrid.getChildren().clear();

        boneyardGrid.setVisible(true);
        boneyardGrid.setMouseTransparent(false);
        boneyardGrid.setDisable(false);
        boneyardGrid.toFront();

        boneyardGrid.setPadding(new Insets(50));  // Отступы внутри контейнера

        System.out.println("boneyardGrid added, requesting focus...");
        boneyardGrid.requestFocus();
        System.out.println("Adding boneyard tiles...");
        Rectangle hiddenTile;
        for (int i = 0; i < boneyard.getBoneyardTiles().size(); i++) {
            hiddenTile = getHiddenTile();
            // Добавляем костяшку в сетку
            boneyardGrid.getChildren().add(hiddenTile);
        }

        if (boneyard.isBoneyardEmpty()) {
            hideBoneyardView();
        }
    }

    private Rectangle getHiddenTile() {
        Rectangle hiddenTile = new Rectangle(25, 50);
        hiddenTile.setFill(Color.RED);
        hiddenTile.setStroke(Color.BLACK);

        hiddenTile.setOnMouseClicked(event -> {
            if (player.playerHasLegitMove(board)) {
                hideBoneyardView();
                checkWinCondition();
            } else if (boneyard.isBoneyardEmpty()) {
                checkWinCondition();
                hideBoneyardView();
                isPlayerTurn = false;
                onComputerMove();
            } else {
                System.out.println("Tile clicked!");
                player.drawFromBoneyard(boneyard);  // игрок тянет костяшку
                renderPlayerHand();  // обновляем колоду игрока
                boneyardGrid.getChildren().remove(hiddenTile);
            }
        });
        return hiddenTile;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gamePane.getChildren().remove(boneyardGrid);
        workSpace.requestFocus();
        boneyard.generateAllTiles();
        player.setMyHand(boneyard.generatePlayerHands());
        computer.setMyHand(boneyard.generatePlayerHands());
        renderPlayerHand();

        gamePane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getGestureSource() != gamePane
                        && dragEvent.getDragboard().hasImage()) {
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                dragEvent.consume();
            }
        });

        gamePane.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("on drag entered");
                if (dragEvent.getGestureSource() != gamePane
                        && dragEvent.getDragboard().hasImage()) {
                    System.out.println("over the board");
                }

                if (!player.playerHasLegitMove(board) && !boneyard.isBoneyardEmpty()) {
                    System.out.println("You have no valid moves. You need to draw a domino from a boneyard.");
                    handleBoneyardDrawing();
                    isPlayerTurn = false;
                } else {
                    checkWinCondition();
                }
                dragEvent.consume();
            }
        });

        gamePane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("on drag exited");

                dragEvent.consume();
                if (player.isEmptyHand() && !boneyard.isBoneyardEmpty()) {
                    System.out.println("You have no valid moves. You need to draw a domino from a boneyard.");
                    handleBoneyardDrawing();
                    isPlayerTurn = false;
                    //onComputerMove();
                } else {
                    checkWinCondition();
                }
            }
        });

        gamePane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                System.out.println("on drag dropped");

                Dragboard dragboard = dragEvent.getDragboard();
                boolean success = false;

                if (dragboard.hasImage()) {
                    ImageView sourceImageView = (ImageView) dragEvent.getGestureSource();
                    DominoTile sourceTile = (DominoTile) sourceImageView.getUserData();

                    // устанавливаем новые координаты на игровом поле
                    sourceImageView.setTranslateX(0);
                    sourceImageView.setTranslateX(0);

                    // устанавливаем позицию в рамках игрового поля
                    sourceImageView.setLayoutX(dragEvent.getSceneX() - gamePane.getLayoutX()
                            - sourceImageView.getBoundsInParent().getWidth() / 2);
                    sourceImageView.setLayoutY(dragEvent.getSceneY() - gamePane.getLayoutY()
                            - sourceImageView.getBoundsInParent().getHeight() / 2);

                    if (isOverlapping(sourceImageView)) {
                        System.out.println("one tile overlaps another");
                        dragEvent.setDropCompleted(false);
                        return;
                    }

                    if (board.isLegalMove(sourceTile)) {
                        double mouseX = dragEvent.getSceneX(); // Координата X мыши
                        double mouseY = dragEvent.getSceneY(); // Координата Y мыши

                        snapTileToNearestSide(sourceImageView, sourceTile, mouseX, mouseY);
                        System.out.println(board.getBoard());
                        success = true;
                        checkWinCondition();

                    } else {
                        System.out.println("Move is not legal");
                        dragEvent.setDropCompleted(false);
                        return;
                    }

                    // перемещаем объект с gameDeck на gamePane
                    if (sourceImageView.getParent() == gameDeck) {
                        player.getHand().remove(sourceTile);
                        gameDeck.getChildren().remove(sourceImageView);
                        gamePane.getChildren().add(sourceImageView);
                    }
                }

                dragEvent.setDropCompleted(success);
                dragEvent.consume();

                if (success) {
                    isPlayerTurn = false;
                    onComputerMove();
                }
            }
        });

        startGame();
    }
}
