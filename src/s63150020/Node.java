package s63150020;

import java.util.ArrayList;


public class Node {
    private static final double UCT_CONSTANT = 1.4;

    private Node parent;
    private Node[] children;

    private Field move;

    private int nVisits = 0;
    private int nWins = 0;

    private boolean isTerminal;

    public Node(Node parent, Field move) {
        this.parent = parent;
        this.move = move;
    }

    public void createChildren(ArrayList<Field> moves) {
        children = new Node[moves.size()];
        for(int i = 0; i < children.length; i++) {
            children[i] = new Node(this, moves.get(i));
        }
    }

    public void updateScore(int status) {
        nWins += status;
        nVisits++;

        if(parent != null) {
            parent.updateScore(status);
        }
    }

    public double getScore() {
        if(isTerminal) {
            return Double.NEGATIVE_INFINITY;
        }

        return getWinRatio() + UCT_CONSTANT * Math.sqrt(Math.log(parent.getNVisits()) / nVisits);
    }

    public double getWinRatio() {
        return ((double) nWins) / nVisits;
    }

    public int getNVisits() {
        return nVisits;
    }

    public void setIsTerminal(boolean isWinning) {
        nWins = isWinning ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        isTerminal = true;
        parent.updateScore(isWinning ? 1 : -1);
    }

    public Field getBestMove() {
        double bestWR = Double.NEGATIVE_INFINITY;
        Field move = null;

        for(Node n : children) {
            double wr = n.getWinRatio();
            if(wr > bestWR) {
                bestWR = wr;
                move = n.getMove();
            }
        }
        
        return move;
    }

    public Field getMove() {
        return move;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public Node chooseChild() {
        double bestScore = Double.NEGATIVE_INFINITY;
        Node child = null;

        for(Node n : children) {
            double score = n.getScore();
            if(Double.isNaN(score)) {
                return n;
            }

            if(score > bestScore) {
                bestScore = score;
                child = n;
            }
        }
        
        return child;
    }
}
