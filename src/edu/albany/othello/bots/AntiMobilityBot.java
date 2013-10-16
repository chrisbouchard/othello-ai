package edu.albany.othello.bots;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

public class AntiMobilityBot extends Bot {
    public AntiMobilityBot(Piece p) {
        super(p);
    }

    @Override
    public Map<Move, Double> getMoveConfidences(BoardState bs,
            Map<Piece, Map<Move, Set<BoardState>>> deepestBoardStates) {
        Map<Move, Double> moveConfidences = new HashMap<Move, Double>();
        for (Move m : deepestBoardStates.get(this.piece.getAlternate())
                .keySet()) {

            // get the deep BoardStates for this move
            Set<BoardState> deepestBoardStatesSet = deepestBoardStates.get(
                    this.piece.getAlternate()).get(m);

            double avgConfidence = 0;

            if (deepestBoardStatesSet.size() != 0) {
                for (BoardState deepBS : deepestBoardStatesSet) {
                    if (deepBS.getNumPieces(null) != 0) {
                        avgConfidence += ((double) deepBS.getValidMoves(

                        this.piece.getAlternate()).size())
                                / deepBS.getNumPieces(null);
                    }
                }
                avgConfidence /= deepestBoardStatesSet.size();
            }
            else {
                avgConfidence = 0;
            }

            moveConfidences.put(m, 1 - avgConfidence);

        }
        return moveConfidences;
    }
}
