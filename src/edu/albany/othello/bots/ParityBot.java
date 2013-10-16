package edu.albany.othello.bots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

//favors moves that move into a region with an odd number of open spots in the current BoardState
//the bot becomes more confident as the game goes on
//gameDuration = #turn/60
//emptyPieces = number of empty pieces in a region
//favor = -1 if even, 1 if odd
//confidence = gameDuration * favor / emptyPieces
public class ParityBot extends Bot {

	public ParityBot(Piece p) {
		super(p);
	}

	@Override
	public Map<Move, Double> getMoveConfidences(BoardState bs,
			Map<Piece, Map<Move, Set<BoardState>>> deepestBoardStates) {

		Map<Move, Double> moveConfidences = new HashMap<Move, Double>();
		double gameDuration = (double) (bs.getNumPieces(this.piece) + bs
				.getNumPieces(this.piece.getAlternate())) / 60;

		// for each move
		for (Move m : deepestBoardStates.get(this.piece).keySet()) {

			// number of empty pieces
			Set<Move> region = numPiecesInRegion(bs, m, new HashSet<Move>());
			int numEmptyPieces = region.size();

			// favor is -1 if even, 1 if odd
			int favor = (numEmptyPieces % 2 == 0) ? -1 : 1;

			double confidence = gameDuration * favor / numEmptyPieces;

			moveConfidences.put(m, confidence);
		}

		return moveConfidences;
	}

	private Set<Move> numPiecesInRegion(BoardState bs, Move move,
			Set<Move> region) {
		Set<Move> surroundingPositions = new HashSet<Move>();

		for (int r = move.getR() - 1; r <= move.getR() + 1; r++) {
			for (int c = move.getC() - 1; c <= move.getC() + 1; c++) {
				//if (r != c) {
					surroundingPositions.add(new Move(null, r, c));
				//}
			}
		}

		// for each surrounding position
		for (Move m : surroundingPositions) {
			// if it is a valid position && there is no piece in this position
			// && the piece has not been added to the set yet
			if (BoardState.isInBounds(m.getR(), m.getC())
					&& bs.getPieceAt(m.getR(), m.getC()) == null
					&& region.add(m))
				// recursively find all positions from this position
				region.addAll(numPiecesInRegion(bs, m, region));
		}

		return region;
	}
}
