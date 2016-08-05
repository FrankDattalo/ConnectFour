package connectFour.interfaces;

public interface AlphaBeta {

    long getMsUntilTimeout();

    void setMsUntilTimeout(long timeout);

    AlphaBetable getBestMove(AlphaBetable startingNode);

    int alphaBeta(AlphaBetable node, int alpha, int beta, int depth);
}