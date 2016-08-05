package connectFour.implementations;

import connectFour.interfaces.AlphaBetable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

class ConnectFour implements AlphaBetable {
    private final Column[] representation;
    private Boolean isWin;
    private Integer moveScore;
    private boolean isMaximizingPlayer;

    private ConnectFour() {
        this.representation = new Column[7];
        for (int i = 0; i < 7; i++) {
            this.representation[i] = new Column();
        }
    }

    private ConnectFour(final ConnectFour other) {
        this.representation = new Column[7];
        for (int i = 0; i < 7; i++) {
            this.representation[i] = new Column(other.representation[i]);
        }
    }

    public static ConnectFour startingState() {
        return new ConnectFour();
    }

    @Override
    public boolean isWin() {
        if (this.isWin == null) {
            this.calculateMoveScore();
        }
        return this.isWin;
    }

    @Override
    public int getMoveValue() {
        if (this.moveScore == null) {
            this.calculateMoveScore();
        }
        return this.moveScore;
    }

    private void calculateMoveScore() {
        final MoveScore score = new MoveScore();
        score.score = 0;
        score.isWin = false;

        final Function<Integer, Integer> increment = (i) -> i + 1;
        final Function<Integer, Integer> decrement = (i) -> i - 1;
        final Function<Integer, Integer> noop = (i) -> i;

        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 0));
        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 1));
        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 2));
        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 3));
        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 4));
        updateScore(score, cacluateLineScore(increment, noop, () -> 0, () -> 5));

        updateScore(score, cacluateLineScore(noop, increment, () -> 0, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 1, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 2, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 3, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 4, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 5, () -> 0));
        updateScore(score, cacluateLineScore(noop, increment, () -> 6, () -> 0));

        updateScore(score, cacluateLineScore(increment, increment, () -> 3, () -> 0));
        updateScore(score, cacluateLineScore(increment, increment, () -> 2, () -> 0));
        updateScore(score, cacluateLineScore(increment, increment, () -> 1, () -> 0));
        updateScore(score, cacluateLineScore(increment, increment, () -> 0, () -> 0));
        updateScore(score, cacluateLineScore(increment, increment, () -> 0, () -> 1));
        updateScore(score, cacluateLineScore(increment, increment, () -> 0, () -> 2));

        updateScore(score, cacluateLineScore(increment, decrement, () -> 3, () -> 5));
        updateScore(score, cacluateLineScore(increment, decrement, () -> 2, () -> 5));
        updateScore(score, cacluateLineScore(increment, decrement, () -> 1, () -> 5));
        updateScore(score, cacluateLineScore(increment, decrement, () -> 0, () -> 5));
        updateScore(score, cacluateLineScore(increment, decrement, () -> 0, () -> 4));
        updateScore(score, cacluateLineScore(increment, decrement, () -> 0, () -> 3));

        this.moveScore = score.score;
        this.isWin = score.isWin;

        if(this.isWin) {
            if(this.isMaximizingPlayer) {
                this.moveScore = Integer.MAX_VALUE;
            } else {
                this.moveScore = Integer.MIN_VALUE;
            }
        }
    }

    private static void updateScore(MoveScore total, MoveScore additional) {
        total.score += additional.score;

        if(!total.isWin) {
            total.isWin = additional.isWin;
        }
    }

    // this can be done better
    // should calculate for when a move blocks the opposite player's consecutive moves
    private MoveScore cacluateLineScore(final Function<Integer, Integer> xChanger, final Function<Integer, Integer> yChanger,
                                        final Supplier<Integer> xStart, final Supplier<Integer> yStart) {
        final int goodPoint = 1; // things you've never said in an argument
        final int badPoint = -1;
        final MoveScore m = new MoveScore();
        m.score = 0;
        m.isWin = false;
        int previousPiecesSeen = 0;
        int multiplier = 1;
        int x = xStart.get();
        int y = yStart.get();
        PieceType previous = null;
        while ((x < 7) && (x >= 0) && (y >= 0) && (y < 6)) {
            final PieceType current = this.representation[x].representation[y];
            if (current != PieceType.None) {
                if (current == previous) {
                    previousPiecesSeen++;
                    if (!m.isWin) {
                        m.isWin = previousPiecesSeen > 3;
                    }
                    multiplier *= 10;
                    m.score = m.score + ((current == PieceType.Computer ? goodPoint : badPoint) * multiplier);
                } else {
                    if (current == PieceType.Computer) {
                        m.score += goodPoint;
                    } else {
                        m.score += badPoint;
                    }
                    previousPiecesSeen = 1;
                    multiplier = 1;
                }
            }
            previous = current;
            x = xChanger.apply(x);
            y = yChanger.apply(y);
        }
        return m;
    }

    private static class MoveScore {
        int score;
        boolean isWin;
    }

    @Override
    public List<AlphaBetable> getChildren() {
        final List<AlphaBetable> ret = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            if (this.representation[i].isNotFull()) {
                final ConnectFour child = new ConnectFour(this);
                child.isMaximizingPlayer = this.nextPlayer();
                child.representation[i].play((child.isMaximizingPlayer) ? PieceType.Computer : PieceType.Human);
                ret.add(child);
            }
        }
        return ret;
    }

    private boolean nextPlayer() {
        return !this.isMaximizingPlayer;
    }

    @Override
    public boolean isMaximizingPlayer() {
        return this.isMaximizingPlayer;
    }

    @Override
    public void setIsMaximizingPlayer(final boolean value) {
        this.isMaximizingPlayer = value;
    }

    @Override
    public void draw(final PrintStream out) {

        // Very elegantly
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
        +"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        out.println(this);
    }

    @Override
    public String toString() {

        String template =
                    "6>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "5>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "4>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "3>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "2>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "1>  |   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   ||   %s   |\n" +
                    "    ---------------------------------------------------------------\n" +
                    "        1        2        3        4        5        6        7";

        return String.format(template,
                this.representation[0].representation[5],
                this.representation[1].representation[5],
                this.representation[2].representation[5],
                this.representation[3].representation[5],
                this.representation[4].representation[5],
                this.representation[5].representation[5],
                this.representation[6].representation[5],

                this.representation[0].representation[4],
                this.representation[1].representation[4],
                this.representation[2].representation[4],
                this.representation[3].representation[4],
                this.representation[4].representation[4],
                this.representation[5].representation[4],
                this.representation[6].representation[4],

                this.representation[0].representation[3],
                this.representation[1].representation[3],
                this.representation[2].representation[3],
                this.representation[3].representation[3],
                this.representation[4].representation[3],
                this.representation[5].representation[3],
                this.representation[6].representation[3],

                this.representation[0].representation[2],
                this.representation[1].representation[2],
                this.representation[2].representation[2],
                this.representation[3].representation[2],
                this.representation[4].representation[2],
                this.representation[5].representation[2],
                this.representation[6].representation[2],

                this.representation[0].representation[1],
                this.representation[1].representation[1],
                this.representation[2].representation[1],
                this.representation[3].representation[1],
                this.representation[4].representation[1],
                this.representation[5].representation[1],
                this.representation[6].representation[1],

                this.representation[0].representation[0],
                this.representation[1].representation[0],
                this.representation[2].representation[0],
                this.representation[3].representation[0],
                this.representation[4].representation[0],
                this.representation[5].representation[0],
                this.representation[6].representation[0]
                );
    }

    @Override
    public AlphaBetable humanPlay(final int columnNumber) {
        assert (columnNumber >= 1) && (columnNumber <= 7);
        final ConnectFour ret = new ConnectFour(this);
        ret.representation[columnNumber - 1].play(PieceType.Human);
        return ret;
    }

    private enum PieceType {
        Computer, Human, None;

        public String toString() {
            switch (this) {
                case Computer:  return "C";
                case Human:     return "P";
                default:        return " ";
            }
        }
    }

    private class Column {
        private int amountInColumn;
        private final PieceType[] representation;

        private Column() {
            this.amountInColumn = 0;
            this.representation = new PieceType[6];
            for (int i = 0; i < 6; i++) {
                this.representation[i] = PieceType.None;
            }
        }

        private Column(final Column copy) {
            this.amountInColumn = copy.amountInColumn;
            this.representation = new PieceType[6];
            for (int i = 0; i < 6; i++) {
                this.representation[i] = copy.representation[i];
            }
        }

        private PieceType getPieceAt(final int height) {
            assert (height <= 6) && (height >= 1);
            return this.representation[height - 1];
        }

        private void play(final PieceType p) {
            assert this.isNotFull();
            this.representation[this.amountInColumn] = p;
            this.amountInColumn++;
        }

        private boolean isNotFull() {
            return this.amountInColumn < 6;
        }

        @Override
        public String toString() {
            return this.representation.toString();
        }
    }

    @Override
    public boolean isColumnFull(final int columnNumber) {
        assert (columnNumber >= 1) && (columnNumber <= 7);
        return !this.representation[columnNumber - 1].isNotFull();
    }
}