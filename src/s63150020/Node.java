package s63150020;

import java.util.ArrayList;
import java.util.Random;


/**
 * This class represents a node in a tree-like structure of different fields.
 * It contains information about the children and parent nodes, various
 * statistics about this node and provides methods for finding best children
 * nodes.
 */
public class Node {
    /**
     * Exploration constant for the MC-UCT heuristic formula.
     */
    public static final double UCT_CONSTANT = 0.5;

    /**
     * Constant for determining the sample size of children for finding the 
     * best one. Size of the subset eqauls to numberOfChildren / SUBSET_SIZE.
     */
    public static final int SUBSET_SIZE = 5;

    /**
     * Parent of this node.
     */
    private Node parent;

    /**
     * List of children for this node.
     */
    private Node[] children;

    /**
     * Move that this node represents.
     */
    private Field move;

    /**
     * Number of visits to this node.
     */
    private int nVisits = 0;

    /**
     * Number of games won from this node.
     */
    private int nWins = 0;

    /**
     * True iff game was won by playing this move.
     */
    private boolean isTerminal;

    /**
     * Creates new instance of this class.
     *
     * @param parent parent of this node
     * @param move move that this node represents
     */
    public Node(Node parent, Field move) {
        this.parent = parent;
        this.move = move;
    }

    /**
     * Creates all the children of this node.
     */
    public void createChildren(ArrayList<Field> moves) {
        children = new Node[moves.size()];
        for(int i = 0; i < children.length; i++) {
            children[i] = new Node(this, moves.get(i));
        }
    }

    /**
     * Updates the score for this node and all its parent.
     *
     * @param status score of the game
     */
    public void updateScore(int status) {
        nWins += status;
        nVisits++;

        if(parent != null) {
            parent.updateScore(status);
        }
    }

    /**
     * Calculates the score of this node.
     *
     * @return calculated score
     */
    public double getScore() {
        if(isTerminal) {
            return Double.NEGATIVE_INFINITY;
        } else if(nVisits == 0) {
            return Double.POSITIVE_INFINITY;  // Checking for NaN takes too long
        }

        return getWinRatio() + UCT_CONSTANT * Math.sqrt(Math.log(parent.getNVisits()) / nVisits);
    }

    /**
     * Returns the win ration for this node.
     *
     * @return win ratio
     */
    public double getWinRatio() {
        return ((double) nWins) / nVisits;
    }

    /**
     * Returns the number of visits to this node.
     */
    public int getNVisits() {
        return nVisits;
    }

    /**
     * Sets this node as terminal.
     *
     * @param isWinning true iff game was won by playing this move
     */
    public void setIsTerminal(boolean isWinning) {
        nWins = isWinning ? Integer.MIN_VALUE : Integer.MIN_VALUE;
        isTerminal = true;
        parent.updateScore(isWinning ? 1 : -1);
    }

    /**
     * Finds the child of this node with the highest win ratio.
     *
     * @return child with highest win ratio
     */
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

    /**
     * Returns the move that this node represents.
     *
     * @return the move
     */
    public Field getMove() {
        return move;
    }

    /**
     * Returns true iff this field has any children.
     *
     * @return true iff this field has children
     */
    public boolean hasChildren() {
        return children != null;
    }

    /**
     * Chooses the most promising child by comparing their scores.
     * Works only on a smaller subset of children (1/n, where n is defined by
     * SUBSET_SIZE).
     *
     * @param generator RNG for picking children
     * @return the most promising child
     */
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
