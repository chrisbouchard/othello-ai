package edu.albany.othello.bots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

public class WinLossBot extends Bot {

	public WinLossBot(Piece p) {
		super(p);
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

			int winCount = 0;

			for (BoardState deepBS : deepestBoardStatesSet) {
				if (deepBS.getWinningPiece() == this.piece)
					winCount++;
				if (deepBS.getWinningPiece() == this.piece.getAlternate())
					winCount--;
			}
			if (deepestBoardStates.size() == 0) {
				moveConfidences.put(m, 0.0);
			} else {
				moveConfidences.put(m, (double)(winCount / deepestBoardStates.size()));
			}
		}
		return moveConfidences;
	}
}
