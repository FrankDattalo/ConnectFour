package connectFour.interfaces;

import java.io.PrintStream;
import java.util.List;

public interface AlphaBetable {
    boolean isWin();

    int getMoveValue();

    List<AlphaBetable> getChildren();

    boolean isMaximizingPlayer();

    void setIsMaximizingPlayer(boolean value);

    void draw(PrintStream out);

    AlphaBetable humanPlay(int columnNumber);

    boolean isColumnFull(int columnNumber);
}