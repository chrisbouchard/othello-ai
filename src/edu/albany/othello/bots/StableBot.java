package edu.albany.othello.bots;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.albany.othello.BoardState;
import edu.albany.othello.Move;
import edu.albany.othello.Piece;

public class StableBot extends Bot {

	public StableBot(Piece p) {
		super(p);
	}

	@Override
	public Map<Move, Double> getMoveConfidences(BoardState bs,
			Map<Piece, Map<Move, Set<BoardState>>> deepestBoardStates) {

		Map<Move, Double> moveConfidences = new HashMap<Move, Double>();

		// stablePieceSet holds the stable pieces for bs
		Set<Move> stablePieceSet = new HashSet<Move>();

		// for each move
		for (Move m : deepestBoardStates.get(this.piece).keySet()) {

			// get the deep BoardStates for this move
			Set<BoardState> deepestBoardStatesSet = deepestBoardStates.get(
					this.piece).get(m);
			double avgConfidence = 0;
			double avgStablePieces = 0;

			if (deepestBoardStatesSet.size() != 0) {

				// for each deep BoardState
				for (BoardState deepBS : deepestBoardStatesSet) {
					// find the number of stable pieces
					Set<Move> stablePieces = getStablePieces(deepBS,
							new HashSet<Move>());
					int numStablePieces = stablePieces.size();

					avgStablePieces += numStablePieces;
				}
				avgStablePieces /= deepestBoardStatesSet.size();
				Set<Move> stablePiecesBS = getStablePieces(bs, stablePieceSet);
				avgConfidence = (avgStablePieces - stablePiecesBS.size()) / 64;
			} else {
				avgConfidence = 0;
			}
			moveConfidences.put(m, avgConfidence);
		}

		return moveConfidences;
	}

	private Set<Move> getAdjacentPieces(BoardState bs, Move move, Piece piece) {
		Set<Move> adjacentPieces = new HashSet<Move>();
		// add adjacent pieces that are of the same color to the candidate set
		for (int r = move.getR() - 1; r <= move.getR() + 1; r++) {
			for (int c = move.getC() - 1; c <= move.getC() + 1; c++) {
				if (BoardState.isInBounds(r, c) && bs.getPieceAt(r, c) == piece) {
					adjacentPieces.add(new Move(piece, r, c));
				}
			}
		}
		return adjacentPieces;
	}

	private Set<Move> getValidAdjacentPieces(BoardState bs, Move move,
			Piece piece) {
		Set<Move> validAdjacentPieces = new HashSet<Move>();

		// get the adjacent pieces
		Set<Move> adjacentPieces = getAdjacentPieces(bs, move, piece);
		// for each adjacent piece
		for (Move m : adjacentPieces) {
			// if the adjacent move is valid, add it to the candidate set
			if (BoardState.isInBounds(m.getR(), m.getC())) {
				validAdjacentPieces.add(m);
			}
		}
		return validAdjacentPieces;
	}

	private Set<Move> getStablePieces(BoardState bs, Set<Move> stablePieceSet) {
		// candidates are pieces of the same color that are adjacent to a stable
		// piece. they may be a stable piece
		Set<Move> candidateSet = new HashSet<Move>();

		// set of 4 spots initially for each of the 4 corners
		if (bs.getPieceAt(0, 0) == this.piece)
			stablePieceSet.add(new Move(this.piece, 0, 0));
		if (bs.getPieceAt(0, 7) == this.piece)
			stablePieceSet.add(new Move(this.piece, 0, 7));
		if (bs.getPieceAt(7, 0) == this.piece)
			stablePieceSet.add(new Move(this.piece, 7, 0));
		if (bs.getPieceAt(7, 7) == this.piece)
			stablePieceSet.add(new Move(this.piece, 7, 7));

		// for each stable piece
		for (Move m : stablePieceSet) {
			// get the valid adjacent pieces
			Set<Move> validAdjacentPieces = getValidAdjacentPieces(bs, m,
					this.piece);
			candidateSet.addAll(validAdjacentPieces);
		}
		// for each candidate, find the new candidates and add them to the
		// candidate list
		Set<Move> newCandidateSet = candidateSet;
		Set<Move> oldCandidateSet = null;
		do {
			oldCandidateSet = newCandidateSet;
			newCandidateSet = new HashSet<Move>();
			for (Move m : oldCandidateSet) {
				// if the piece is stable and it has not been added already
				if (isStable(m, stablePieceSet) && stablePieceSet.add(m)) {
					newCandidateSet.addAll(getValidAdjacentPieces(bs, m,
							this.piece));
				}
			}

		} while (!newCandidateSet.isEmpty());

		// by this point there were no new candidates, so all stable pieces must
		// be in stablePieceSet

		return stablePieceSet;
	}

	/*
	 * 123
	 * 4X4
	 * 321
	 */
	private boolean isStable(Move m, Set<Move> stablePieceSet) {
		boolean topBottom = m.getR() == 1 || m.getR() == 7;
		boolean leftRight = m.getC() == 1 || m.getC() == 7;
		boolean adj1 = topBottom
				|| leftRight
				|| stablePieceSet.contains(new Move(piece, m.getR() - 1, m
						.getC() - 1))
				|| stablePieceSet.contains(new Move(piece, m.getR() + 1, m
						.getC() + 1));
		boolean adj2 = topBottom
				|| stablePieceSet.contains(new Move(piece, m.getR() - 1, m
						.getC()))
				|| stablePieceSet.contains(new Move(piece, m.getR() + 1, m
						.getC()));
		boolean adj3 = topBottom
				|| leftRight
				|| stablePieceSet.contains(new Move(piece, m.getR() - 1, m
						.getC() + 1))
				|| stablePieceSet.contains(new Move(piece, m.getR() + 1, m
						.getC() - 1));
		boolean adj4 = leftRight
				|| stablePieceSet.contains(new Move(piece, m.getR(),
						m.getC() - 1))
				|| stablePieceSet.contains(new Move(piece, m.getR(),
						m.getC() + 1));

		return adj1 && adj2 && adj3 && adj4;
	}
}
