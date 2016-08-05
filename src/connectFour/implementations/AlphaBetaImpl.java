package connectFour.implementations;

import connectFour.interfaces.AlphaBeta;
import connectFour.interfaces.AlphaBetable;

import java.util.List;

public class AlphaBetaImpl implements AlphaBeta {
    private long msUntilTimeout;

    public AlphaBetaImpl() {
        this.msUntilTimeout = 10000;
    }

    @Override
    public int alphaBeta(final AlphaBetable node, int alpha, int beta, final int depth) {
        final List<AlphaBetable> children = node.getChildren();
        if ((depth == 0) || children.isEmpty()) {
            return node.getMoveValue();
        }
        int value;
        if (node.isMaximizingPlayer()) {
            value = Integer.MIN_VALUE;
            for (final AlphaBetable child : children) {
                value = Math.max(value, this.alphaBeta(child, alpha, beta, depth - 1));
                alpha = Math.max(value, alpha);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        } else {
            value = Integer.MAX_VALUE;
            for (final AlphaBetable child : children) {
                value = Math.min(value, this.alphaBeta(child, alpha, beta, depth - 1));
                beta = Math.min(value, beta);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        }
    }

    @Override
    public void setMsUntilTimeout(final long timeout) {
        this.msUntilTimeout = timeout;
    }

    @Override
    public AlphaBetable getBestMove(final AlphaBetable startingNode) {
        AlphaBetable ret = null;
        int depth = 1;
        int bestMoveValue = Integer.MIN_VALUE;
        startingNode.setIsMaximizingPlayer(false);
        final long start = System.currentTimeMillis();
        final List<AlphaBetable> children = startingNode.getChildren();
        for (final AlphaBetable child : children) {
            final int moveValue = this.alphaBeta(child, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
            if (moveValue > bestMoveValue) {
                ret = child;
                bestMoveValue = moveValue;
            }
        }
        long now = System.currentTimeMillis();
        while ((now - start) < this.getMsUntilTimeout()) {
            depth++;
            for (final AlphaBetable child : children) {

                if(now - start < this.getMsUntilTimeout()) {
                    final int moveValue = this.alphaBeta(child, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
                    if (moveValue > bestMoveValue) {
                        ret = child;
                        bestMoveValue = moveValue;
                    }
                }

                now = System.currentTimeMillis();
            }
        }
        return ret;
    }

    @Override
    public long getMsUntilTimeout() {
        return this.msUntilTimeout;
    }
}