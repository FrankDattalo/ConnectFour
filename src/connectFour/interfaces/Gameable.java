package connectFour.interfaces;

import connectFour.interfaces.AlphaBetable;

public interface Gameable {
    boolean getComputerStartingFirst();

    boolean promptPlayAgain();

    boolean isPlayerTurn();

    AlphaBetable playerMove();

    AlphaBetable computerMove();

    void startGame();

    void displayWin();

    void displayDraw();
}