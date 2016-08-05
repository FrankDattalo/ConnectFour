package connectFour.app;

import connectFour.implementations.GameableImpl;
import connectFour.interfaces.Gameable;

import java.util.Scanner;

public class Main {
    public static void main(final String... args) {
        final Gameable game = new GameableImpl(new Scanner(System.in), System.out);
        game.startGame();
        while (game.promptPlayAgain()) {
            game.startGame();
        }
    }
}