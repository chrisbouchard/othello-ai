package edu.albany.othello;

public class Human extends Player {
    
    public Human(Piece p) {
        super(p);
    }
    
    @Override
    public void thinkOfMove() {
        System.out.println("AWAITING FIRST STRIKE COMMAND");
        OthelloApplication.view.setCurrentHuman(this);
    }
    
    public void makeMove(int r, int c) {
        OthelloApplication.controller.makeMove(new Move(getPiece(), r, c));
    }
}
