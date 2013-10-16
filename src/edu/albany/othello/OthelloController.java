package edu.albany.othello;

import java.util.Map;

public class OthelloController {
    private Map<Piece, Player> players;
    private boolean waitingOnMove;

    public OthelloController(Map<Piece, Player> players) {
        waitingOnMove = false;
        this.players = players;
    }

    public void makeMove(Move m) {
        try {
            OthelloApplication.model.makeMove(m.getR(), m.getC());
        }
        catch (IndexOutOfBoundsException ex) {
            OthelloApplication.view.displayMessage("Out of bounds!");
        }
        catch (IllegalArgumentException ex) {
            OthelloApplication.view.displayMessage("Bad move!");
        }
        finally {
            waitingOnMove = false;
        }
    }

    public Piece playGame() {
        OthelloApplication.model.initialize();
        
        while (!OthelloApplication.model.getCurrentBoardState().isGameOver()) {
            if (!waitingOnMove) {
                waitingOnMove = true;
                players.get(OthelloApplication.model.getCurrentPiece())
                        .thinkOfMove();
            }
        }
        
        BoardState finalBoard = OthelloApplication.model.getCurrentBoardState();
        return finalBoard.getWinningPiece();
    }
}
