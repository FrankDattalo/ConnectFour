package connectFour.implementations;

import connectFour.implementations.AlphaBetaImpl;
import connectFour.implementations.ConnectFour;
import connectFour.interfaces.AlphaBeta;
import connectFour.interfaces.AlphaBetable;
import connectFour.interfaces.Gameable;

import java.io.PrintStream;
import java.util.Scanner;

public class GameableImpl implements Gameable {
    private Scanner in;
    private PrintStream out;
    private final AlphaBeta ab;
    private AlphaBetable game;
    private boolean isPlayerTurn;

    public GameableImpl(final Scanner in, final PrintStream out) {
        this.in = in;
        this.out = out;
        this.ab = new AlphaBetaImpl();
    }

    private static boolean promptUntilYesOrNo(final String prompt, final PrintStream out, final Scanner in) {
        out.print(prompt + " >> ");
        final String whitelist = "YyNn";
        final String error = "INVALID: Respond with a Y or N";
        String input = in.nextLine();
        while ((input.length() > 1) || !whitelist.contains(input)) {
            out.println(error);
            out.print(prompt + " >> ");
            input = in.nextLine();
        }
        return whitelist.indexOf(input) <= 1;
    }

    @Override
    public boolean getComputerStartingFirst() {
        return !promptUntilYesOrNo("Would you like to move first?", this.out, this.in);
    }

    @Override
    public boolean promptPlayAgain() {
        return promptUntilYesOrNo("Would you like to play again?", this.out, this.in);
    }

    @Override
    public boolean isPlayerTurn() {
        final boolean ret = this.isPlayerTurn;
        this.isPlayerTurn = !this.isPlayerTurn;
        return ret;
    }

    @Override
    public void startGame() {
        this.isPlayerTurn = !this.getComputerStartingFirst();
        this.game = ConnectFour.startingState();

        while (!this.game.getChildren().isEmpty()) {
            AlphaBetable nextState;
            if (this.isPlayerTurn()) {
                nextState = this.playerMove();
            } else {
                nextState = this.computerMove();
            }
            this.game = nextState;

            this.game.draw(this.out);

            if (this.game.isWin()) {
                break;
            }

        }
        if (this.game.isWin()) {
            this.displayWin();
        } else {
            this.displayDraw();
        }
    }

    @Override
    public AlphaBetable playerMove() {
        final String prompt = "Enter a column between 1 and 7 to place a piece >> ";
        final String error = "INVALID: enter a number between 1 and 7, or column may be full";
        final String whitelist = "1234567";
        this.out.print(prompt);
        String input = this.in.nextLine();
        int intInput = Integer.parseInt(input);
        while ((input.length() > 1) || !whitelist.contains(input) || this.game.isColumnFull(intInput)) {
            this.out.println(error);
            this.game.draw(this.out);
            this.out.print(prompt);
            input = this.in.nextLine();
            intInput = Integer.parseInt(input);
        }
        return this.game.humanPlay(Integer.parseInt(input));
    }

    @Override
    public AlphaBetable computerMove() {
        final String prompt = "The computer is making a move...";
        this.out.println(prompt);
        return this.ab.getBestMove(this.game);
    }

    @Override
    public void displayWin() {
        this.out.println("The " + (!this.isPlayerTurn() ? "Player" : "Computer") + " won!");
    }

    @Override
    public void displayDraw() {
        this.out.println("It was a draw");
    }
}