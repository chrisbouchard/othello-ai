package edu.albany.othello.bots;

import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

public abstract class Bot {
	protected Piece piece;

	public Bot(Piece p) {
		this.piece = p;
	}

	// returns set of all move and confidence pairs
	public abstract Map<Move, Double> getMoveConfidences(BoardState bs,
			Map<Piece, Map<Move, Set<BoardState>>> deepestBoardStates);
}
