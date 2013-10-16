package edu.albany.othello;

public abstract class Player {
	private Piece p;
	
	protected Player(Piece p) {
	    this.p = p;
	}

	protected BoardState getBoardState() {
		return OthelloApplication.model.getCurrentBoardState();
	}

	public Piece getPiece() {
		return p;
	}

	public abstract void thinkOfMove();
	
	public String toString() {
	    return String.format("[%s: %s]", p, super.toString());
	}
}
