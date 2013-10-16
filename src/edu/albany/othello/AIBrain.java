package edu.albany.othello;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.albany.othello.bots.AbsoluteWinLoseBot;
import edu.albany.othello.bots.AntiMobilityBot;
import edu.albany.othello.bots.AntiStableBot;
import edu.albany.othello.bots.Bot;
import edu.albany.othello.bots.MaxPieceBot;
import edu.albany.othello.bots.MobilityBot;
import edu.albany.othello.bots.ParityBot;
import edu.albany.othello.bots.RandomBot;
import edu.albany.othello.bots.StableBot;
import edu.albany.othello.bots.WinLossBot;

// import java.util.Map.Entry;

public class AIBrain extends Player {
    // An element in the set for the current level
    private class Element {
        private BoardState bs;
        private Piece p;
        private Element parent;
        private Move rootMove;

        public Element(BoardState bs, Piece p, Element parent, Move rootMove) {
            this.bs = bs;
            this.p = p;
            this.parent = parent;
            this.rootMove = rootMove;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Element)) {
                return false;
            }

            Element e = (Element) o;
            return e.bs.equals(bs) && e.p == p && e.parent == parent
                    && e.rootMove.equals(rootMove);
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = hash * 31 + bs.hashCode();
            hash = hash * 31 + p.hashCode();
            hash = hash * 31 + ((rootMove == null) ? 0 : rootMove.hashCode());

            return hash;
        }

        public BoardState getBS() {
            return bs;
        }

        public Piece getP() {
            return p;
        }

        public Element getParent() {
            return parent;
        }

        public void setParent(Element parent) {
            this.parent = parent;
        }

        public Move getRootMove() {
            return rootMove;
        }
    }

    // holds the bot and weight pair
    private Map<Bot, Double> botList;
    private int maxElements;
    private boolean beQuiet;

    // Creates an AIBrain that uses all bots at predetermined weights
    public AIBrain(Piece p, int maxElements, boolean beQuiet) {
        // TODO Auto-generated constructor stub
        // TODO fill in botList
        super(p);
        this.maxElements = maxElements;
        this.beQuiet = beQuiet;
        botList = new HashMap<Bot, Double>();
        botList.put(new RandomBot(p), 1.0);
        botList.put(new MobilityBot(p), 4.0);
        botList.put(new AntiMobilityBot(p), 6.0);
        botList.put(new MaxPieceBot(p), 6.0);
        botList.put(new ParityBot(p), 9.0);
        botList.put(new WinLossBot(p), 10.0);
        botList.put(new AbsoluteWinLoseBot(p), Double.MAX_VALUE);
        botList.put(new StableBot(p), 100.0);
        botList.put(new AntiStableBot(p), 100.0);
    }

    // Creates an AIBrain that uses the given bots with their given weights
    public AIBrain(Piece p, int maxElements, boolean beQuiet,
            Map<Bot, Double> botWeight) {
        super(p);
        this.maxElements = maxElements;
        this.beQuiet = beQuiet;
        botList = new HashMap<Bot, Double>(botWeight);
    }

    public Move getBestMove() {
        Set<Map<Move, Double>> moveConfidenceSet = new HashSet<Map<Move, Double>>();

        BoardState currentBS = OthelloApplication.model.getCurrentBoardState();
        Piece currentPiece = OthelloApplication.model.getCurrentPiece();

        if (!beQuiet) {
            System.out.println("Thinking...");
        }

        Map<Piece, Map<Move, Set<BoardState>>> deepest = getDeepestBoardStates(
                maxElements, currentBS, currentPiece);

        if (!beQuiet) {
            int numBlackBoards = 0;
            int numWhiteBoards = 0;

            for (Move m : deepest.get(Piece.BLACK).keySet()) {
                numBlackBoards += deepest.get(Piece.BLACK).get(m).size();
            }

            for (Move m : deepest.get(Piece.WHITE).keySet()) {
                numWhiteBoards += deepest.get(Piece.WHITE).get(m).size();
            }

            System.out.println(String.format("Considering %d black boards...",
                    numBlackBoards));
            System.out.println(String.format("Considering %d white boards...",
                    numWhiteBoards));
        }

        // for each bot
        for (Bot b : botList.keySet()) {
            // all (move, confidence) pairs for b
            /*
             * Map<Move, Double> moveConfidences = b.getMoveConfidences(
             * getBoardState(), getDeepestBoardStates());
             */

            Map<Move, Double> moveConfidences = b.getMoveConfidences(
                    getBoardState(), deepest);

            // DEBUGGING OUTPUT
            if (!beQuiet) {
                System.out.println(String.format("    %s: %s", b.getClass()
                        .getSimpleName(), moveConfidences));
            }

            // for each (move, confidence) pair for b
            for (Entry<Move, Double> value : moveConfidences.entrySet()) {
                // compute the weighted conf
                value.setValue(value.getValue() * botList.get(b));
            }
            // by this point, moveConfidences holds the weighted confs for b
            // moveConfidences: (m1, wc1) (m2, wc2)

            // add moveConfidences to the set, where each element in the set
            // represents one bot
            moveConfidenceSet.add(moveConfidences);
        }
        // by this point, all bots added their hashmaps to the set
        // moveConfidenceSet: ((m1, w1c1) (m2, w1c2)) ((m1, w2c1) (m2, w2c2))
        // ((m1, w3c1) (m2, w3c2))

        // we need to find the total weights

        // create the new HashMap
        // (move1, sum1) (move2, sum2) (move3, sum3)
        HashMap<Move, Double> moveWeightedConfidenceSums = new HashMap<Move, Double>();

        // for each key in the first HashMap
        for (Move m : (moveConfidenceSet.iterator().next()).keySet()) {
            // calculate the totalValue for the current key
            Double totalValue = 0.0;
            // for each HashMap in moveConfidenceSet
            for (Map<Move, Double> botHashMap : moveConfidenceSet) {
                // add the value to the running total
                totalValue += botHashMap.get(m);
            }
            // add the (move, totalValue) to the new sum set
            moveWeightedConfidenceSums.put(m, totalValue);

        }
        // by this point moveWeightedConfidenceSums should contain each move
        // once with the weighted sum

        if (!beQuiet) {
            System.out.println(moveWeightedConfidenceSums);
        }

        // computes the best move given all moves and their total
        // holds the highest (move, total) pair
        Map.Entry<Move, Double> currentHighestPair = null;
        for (Map.Entry<Move, Double> entry : moveWeightedConfidenceSums
                .entrySet()) {
            if (currentHighestPair == null
                    || entry.getValue() > currentHighestPair.getValue()) {
                currentHighestPair = entry;
            }
        }

        if (!beQuiet) {
            System.out.println("AI Choses: " + currentHighestPair);
        }

        return currentHighestPair.getKey();
    }

    public Map<Piece, Map<Move, Set<BoardState>>> getDeepestBoardStates(
            int maxElements, BoardState currentBS, Piece currentPiece) {
        Map<Piece, Map<Move, Set<BoardState>>> ans = new HashMap<Piece, Map<Move, Set<BoardState>>>();

        for (Piece p : Piece.values()) {
            Map<Move, Set<BoardState>> pieceAns = new HashMap<Move, Set<BoardState>>();
            Set<Element> currentLevel = new HashSet<Element>();
            Set<Element> newElements = new HashSet<Element>();

            // Add root's children to the current level
            // Root's children have no parent
            for (Move m : currentBS.getValidMoves(currentPiece)) {
                Element rootChildElt = new Element(
                        currentBS.getBoardFromMove(m), currentPiece, null, m);
                currentLevel.add(rootChildElt);
                newElements.add(rootChildElt);
                pieceAns.put(m, new HashSet<BoardState>());
            }

            int availableElements = maxElements - currentLevel.size();

            // If we added new elements last time, process them
            while (!newElements.isEmpty()) {
                Set<Element> prevNewElements = newElements;
                newElements = new HashSet<Element>();

                for (Element newElt : prevNewElements) {
                    Piece nextP = newElt.p.getAlternate();
                    Set<Move> validMoves = newElt.getBS().getValidMoves(nextP);

                    // Handle skips
                    if (validMoves.isEmpty()) {
                        nextP = newElt.getP().getAlternate();
                        validMoves = newElt.getBS().getValidMoves(nextP);
                    }

                    for (Move vm : validMoves) {
                        // Add the new elements if there's room
                        if (availableElements > 0) {
                            Element newE = new Element(
                                    newElt.getBS().getBoardFromMove(vm), nextP,
                                    newElt, newElt.getRootMove());

                            // Try to add the element
                            if (currentLevel.add(newE)) {
                                // Ok, it must be new
                                newElements.add(newE);
                                --availableElements;

                                // If this is the color we're looking for,
                                // remove
                                // its ancestors
                                if (newE.p == p) {
                                    while (newE.getParent() != null
                                            && currentLevel
                                                    .contains(newE.getParent())) {
                                        Element parent = newE.getParent();
                                        newE.setParent(newE.getParent().getParent());
                                        currentLevel.remove(parent);
                                        ++availableElements;
                                    }
                                }
                            }
                        }

                        // Find elements whose parent was removed and set
                        // their parent to null
                        for (Element elt : currentLevel) {
                            if (elt.getParent() != null
                                    && !currentLevel.contains(elt.getParent())) {
                                elt.setParent(null);
                            }
                        }
                    }
                }

            }

            // Filter out the board states that don't match the color we
            // want and add to the proper key
            for (Element e : currentLevel) {
                if (e.getP() == p && e.getRootMove() != null) {
                    pieceAns.get(e.getRootMove()).add(e.getBS());
                }
            }

            ans.put(p, pieceAns);
        }

        return ans;
    }

    @Override
    public void thinkOfMove() {
        Move m = getBestMove();
        OthelloApplication.controller.makeMove(m);
    }
}
