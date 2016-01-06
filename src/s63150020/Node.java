package s63150020;

import java.util.ArrayList;
import java.util.Random;


public class Node {
    public static final double UCT_CONSTANT = 0.5;
    public static final int SUBSET_SIZE = 5;

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
        } else if(nVisits == 0) {
            return Double.POSITIVE_INFINITY;  // Checking for NaN takes too long
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
        nWins = isWinning ? Integer.MIN_VALUE : Integer.MIN_VALUE;
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

    public Node chooseChild(Random generator) {
        double bestScore = Double.NEGATIVE_INFINITY;
        Node child = null;

        // It seems that the random selection of nodes gives better results
        // int start = generator.nextInt(children.length);
        // int end = start + children.length / SUBSET_SIZE;
        // for(int i = start; i < end; i++) {
        //     Node n = children[i % children.length];
        for(int i = 0; i < children.length / SUBSET_SIZE; i++) {
            Node n = children[generator.nextInt(children.length)];
            double score = n.getScore();
            if(score == Double.POSITIVE_INFINITY) {
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
