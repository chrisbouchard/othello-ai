package edu.albany.othello.bots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

public class AbsoluteWinLoseBot extends Bot {
    private boolean hasGloated;

    public AbsoluteWinLoseBot(Piece p) {
        super(p);
        hasGloated = false;
    }

    @Override
    public Map<Move, Double> getMoveConfidences(BoardState bs,
            Map<Piece, Map<Move, Set<BoardState>>> deepestBoardStates) {
        Map<Move, Double> moveConfidences = new HashMap<Move, Double>();

        Map<Move, Set<BoardState>> allDeepestBoardStates = new HashMap<Move, Set<BoardState>>();

        for (Move m : deepestBoardStates.get(Piece.WHITE).keySet()) {
            allDeepestBoardStates.put(m, new HashSet<BoardState>(
                    deepestBoardStates.get(Piece.WHITE).get(m)));
        }

        for (Move m : deepestBoardStates.get(Piece.BLACK).keySet()) {
            allDeepestBoardStates.get(m).addAll(
                    deepestBoardStates.get(Piece.BLACK).get(m));
        }

        for (Move m : allDeepestBoardStates.keySet()) {

            // get the deep BoardStates for this move
            Set<BoardState> deepestBoardStatesSet = allDeepestBoardStates
                    .get(m);

            boolean isWin = true;
            boolean isLose = true;

            if (deepestBoardStatesSet.size() != 0) {
                for (BoardState deepBS : deepestBoardStatesSet) {
                    isWin &= deepBS.getWinningPiece() == this.piece;
                    isLose &= deepBS.getWinningPiece() == this.piece
                            .getAlternate();
                }

                moveConfidences.put(m, (isWin ? 1.0 : 0.0)
                        + (isLose ? -1.0 : 0.0));

                if (isWin && !hasGloated) {
                    System.out.println("**** YOU HAVE NO CHANCE TO SURVIVE "
                            + "MAKE YOUR TIME. ****");
                    hasGloated = true;
                }
            }
            else {
                moveConfidences.put(m, 0.0);
            }
        }
        return moveConfidences;
    }
}
