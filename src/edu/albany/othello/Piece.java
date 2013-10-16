package edu.albany.othello;

public enum Piece {
    BLACK, WHITE;
    
    public Piece getAlternate() {
        if (this == Piece.BLACK) {
            return Piece.WHITE;
        }
        else {
            return Piece.BLACK;
        }
    }
}
