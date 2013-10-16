package edu.albany.othello;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class OthelloSwingView implements OthelloView {
    private class ButtonActionListener implements ActionListener {
        private int r;
        private int c;

        public ButtonActionListener(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // System.out.println(String.format("(%d, %d)", r, c));

            if (currentHuman != null) {
                Human h = currentHuman;
                currentHuman = null;
                h.makeMove(r, c);
            }
        }
    }

    private JFrame frame;
    private PieceButton[][] buttons;
    private JLabel messageLabel;
    private Human currentHuman;

    public OthelloSwingView() {
        currentHuman = null;

        frame = new JFrame("Play Othello!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LayoutManager frameLayout = new BorderLayout(2, 2);
        frame.setLayout(frameLayout);

        JPanel buttonPanel = new JPanel();
        LayoutManager buttonLayout = new GridLayout(BoardState.ROWS,
                BoardState.COLS);
        buttonPanel.setLayout(buttonLayout);
        frame.add(buttonPanel, BorderLayout.CENTER);

        messageLabel = new JLabel(" ");
        frame.add(messageLabel, BorderLayout.SOUTH);

        buttons = new PieceButton[BoardState.ROWS][BoardState.COLS];

        for (int r = 0; r < BoardState.ROWS; ++r) {
            for (int c = 0; c < BoardState.COLS; ++c) {
                buttons[r][c] = new PieceButton(null);
                buttonPanel.add(buttons[r][c]);
                buttons[r][c].addActionListener(new ButtonActionListener(r, c));
            }
        }

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    @Override
    public void displayMessage(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

    @Override
    public void setCurrentHuman(Human h) {
        currentHuman = h;
    }

    @Override
    public void update() {
        BoardState bs = OthelloApplication.model.getCurrentBoardState();
        Piece cp = OthelloApplication.model.getCurrentPiece();
        Set<Move> validMoves = bs.getValidMoves(cp);

        for (int r = 0; r < BoardState.ROWS; ++r) {
            for (int c = 0; c < BoardState.COLS; ++c) {
                buttons[r][c].setPiece(bs.getPieceAt(r, c));

                if (validMoves.contains(new Move(cp, r, c))) {
                    buttons[r][c].setSelected(true);
                }
                else {
                    buttons[r][c].setSelected(false);
                }

                buttons[r][c].repaint();
            }
        }

        String message = "";

        for (Piece p : Piece.values()) {
            message += String.format("[%c] %s: %d    ",
                    ((p == cp) ? '*' : ' '), p, bs.getNumPieces(p));
        }

        message += bs.isGameOver() ? "GAME OVER!" : "";

        messageLabel.setText(message);

        if (bs.isGameOver()) {
            if (bs.getWinningPiece() == null) {
                displayMessage("A STRANGE GAME.\n"
                        + "THE ONLY WINNING MOVE IS NOT TO PLAY.\n"
                        + "HOW ABOUT A NICE GAME OF CHESS?");
            }
            else {
                displayMessage(bs.getWinningPiece() + " WINS!");
            }

        }
    }
}
