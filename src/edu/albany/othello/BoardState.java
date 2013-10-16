package edu.albany.othello;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoardState {
    // Size of the board
    public static final int ROWS = 8;
    public static final int COLS = 8;

    public static void main(String[] args) {
        BoardState bs = new BoardState();
        System.out.println(bs);
        System.out.println(bs.getValidMoves(Piece.BLACK));
        System.out.println(bs.getBoardFromMove(new Move(Piece.BLACK, 2, 3))
                .getValidMoves(Piece.WHITE));
        System.out.println(bs.getBoardFromMove(new Move(Piece.BLACK, 2, 3))
                .getBoardFromMove(new Move(Piece.WHITE, 4, 2)));
        System.out.println(1.0 / 0.0);
    }

    // Convenience method to check if a square is on the board
    public static Boolean isInBounds(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    private Piece[][] board;
    private Map<Piece, Set<Move>> validMoves;
    //private Map<Move, BoardState> childBoards;

    // Create a default board state
    public BoardState() {
        init();

        board[ROWS / 2 - 1][COLS / 2 - 1] = Piece.WHITE;
        board[ROWS / 2 - 1][COLS / 2] = Piece.BLACK;
        board[ROWS / 2][COLS / 2 - 1] = Piece.BLACK;
        board[ROWS / 2][COLS / 2] = Piece.WHITE;
    }

    // Mostly for testing. Create a new board state absolutely
    public BoardState(Piece[][] board) {
        if (board.length != ROWS) {
            throw new IllegalArgumentException();
        }

        for (int r = 0; r < ROWS; ++r) {
            if (board[r].length != COLS) {
                throw new IllegalArgumentException();
            }
        }

        init();

        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLS; ++c) {
                this.board[r][c] = board[r][c];
            }
        }
    }

    // Create a new board state based on a given state
    // Private because new boards should be created using getBoardFromMove()
    private BoardState(BoardState parent, Move m) {
        // Check that the new move is in bounds and is legal
        if (!parent.getValidMoves(m.getPiece()).contains(m)) {
            throw new IllegalArgumentException();
        }

        init();

        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLS; ++c) {
                board[r][c] = parent.board[r][c];
            }
        }

        doCapture(m.getPiece(), m.getR(), m.getC());
        board[m.getR()][m.getC()] = m.getPiece();
    }

    public BoardState getBoardFromMove(Move m) {
        /*
         * BoardState bs = childBoards.get(m);
         * if (bs == null) {
         * bs = new BoardState(this, m);
         * childBoards.put(m, bs);
         * }
         * return bs;
         */

        return new BoardState(this, m);
    }

    /**
     * 
     * @param p
     *            Piece to count
     * @return The number of pieces of type p
     */
    public int getNumPieces(Piece p) {
        int count = 0;
        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLS; ++c) {
                if (board[r][c] == p) {
                    ++count;
                }
            }
        }
        return count;
    }

    public Piece getPieceAt(int r, int c) {
        if (!isInBounds(r, c)) {
            throw new IndexOutOfBoundsException();
        }

        return board[r][c];
    }

    public Set<Move> getValidMoves(Piece p) {
        Set<Move> moves = validMoves.get(p);

        if (moves == null) {
            moves = new HashSet<Move>();

            for (int r = 0; r < ROWS; ++r) {
                for (int c = 0; c < COLS; ++c) {
                    if (board[r][c] == null && canCapture(p, r, c)) {
                        moves.add(new Move(p, r, c));
                    }
                }
            }

            validMoves.put(p, moves);
        }

        return moves;
    }

    public boolean hasValidMove(Piece p) {
        return getValidMoves(p).size() != 0;
    }

    public boolean isGameOver() {
        for (Piece p : Piece.values()) {
            if (hasValidMove(p)) {
                return false;
            }
        }

        return true;
    }

    public Piece getWinningPiece() {
        if (isGameOver()) {
            for (Piece p : Piece.values()) {
                if (getNumPieces(p) > getNumPieces(p.getAlternate())) {
                    return p;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        String str = " ";

        for (int c = 0; c < COLS; ++c) {
            str += " " + c;
        }
        for (int r = 0; r < ROWS; ++r) {
            str += "\n" + r;

            for (int c = 0; c < COLS; ++c) {
                if (board[r][c] == null) {
                    str += "  ";
                }
                else {
                    switch (board[r][c]) {
                    case WHITE:
                        str += " W";
                        break;

                    case BLACK:
                        str += " B";
                        break;

                    default:
                        str += "  ";
                        break;
                    }
                }
            }
        }

        return str;
    }

    // Check if a piece of the given color could capture a piece if placed here
    private Boolean canCapture(Piece p, int r, int c) {
        for (int dr = -1; dr <= 1; ++dr) {
            for (int dc = -1; dc <= 1; ++dc) {
                if (canCaptureDirected(p, r + dr, c + dc, dr, dc, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Boolean canCaptureDirected(Piece p, int r, int c, int dr, int dc,
            boolean seenOther) {
        // Check if this square is in bounds
        if (!isInBounds(r, c) || board[r][c] == null) {
            return false;
        }

        // If this is our color, we can capture if we've crossed another color
        if (board[r][c] == p) {
            return seenOther;
        }

        // Otherwise, keep going
        return canCaptureDirected(p, r + dr, c + dc, dr, dc, true);
    }

    // Check if a piece of the given color could capture a piece if placed here
    private Boolean doCapture(Piece p, int r, int c) {
        for (int dr = -1; dr <= 1; ++dr) {
            for (int dc = -1; dc <= 1; ++dc) {
                doCaptureDirected(p, r + dr, c + dc, dr, dc, false);
            }
        }

        return false;
    }

    private Boolean doCaptureDirected(Piece p, int r, int c, int dr, int dc,
            boolean seenOther) {
        // Check if this square is in bounds
        if (!isInBounds(r, c) || board[r][c] == null) {
            return false;
        }

        // If this is our color, we can capture if we've crossed another color
        if (board[r][c] == p) {
            return seenOther;
        }

        // Otherwise, keep going
        if (doCaptureDirected(p, r + dr, c + dc, dr, dc, true)) {
            // We can capture, so capture this one
            board[r][c] = p;
            return true;
        }
        else {
            return false;
        }
    }

    private void init() {
        board = new Piece[ROWS][COLS];
        validMoves = new HashMap<Piece, Set<Move>>();
        //childBoards = new HashMap<Move, BoardState>();
    }
}
