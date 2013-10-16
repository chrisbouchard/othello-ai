package edu.albany.othello;

public class MoveConfidence {
	private double confidence;
	private Move move;

	public MoveConfidence(double confidence, Move move){
		this.confidence = confidence;
		this.move = move;
	}

	public double getConfidence(){
		return confidence;
	}

	public Move getMove(){
		return move;
	}
}
