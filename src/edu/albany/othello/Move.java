package edu.albany.othello;

public class Move {
    private Piece piece;
    private int r, c;

    public Move(Piece piece, int r, int c) {
        this.piece = piece;
        this.r = r;
        this.c = c;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %d)", piece, r, c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Move)) {
            return false;
        }

        Move m = (Move) o;
        return piece == m.piece && r == m.r && c == m.c;
    }

    // Found this online. Not sure how great a hash it is, but it should
    // suffice.
    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (piece == null ? 0 : piece.hashCode());
        hash = 31 * hash + r;
        hash = 31 * hash + c;

        return hash;
    }
}
