package edu.albany.othello;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class PieceButton extends JButton {

    /**
     * 
     */
    private static final long serialVersionUID = -3573867997835593775L;

    private static final int STROKE_WIDTH = 2;

    private Piece p;
    private boolean selected;

    public PieceButton(Piece p) {
        this.p = p;
        selected = false;
        setSize(50, 50);
    }

    public Piece getPiece() {
        return p;
    }

    public void setPiece(Piece p) {
        this.p = p;
    }

    @Override
    public void paint(Graphics g) {
        int diameter = Math.min(getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(0f, 0.7f, 0.3f));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.setColor(new Color(0f, 0f, 0f, 0.5f));
        g2.drawRect(0, 0, getWidth(), getHeight());
        
        g2.setStroke(new BasicStroke(STROKE_WIDTH));
        
        if (selected) {
            g2.setColor(new Color(0f, 1f, 0.5f, 0.8f));
            g2.fillOval(STROKE_WIDTH, STROKE_WIDTH,
                    diameter - 2 * STROKE_WIDTH, diameter - 2 * STROKE_WIDTH);
        }

        if (p != null) {
            switch (p) {
            case BLACK:
                g2.setColor(Color.BLACK);
                break;

            case WHITE:
                g2.setColor(Color.WHITE);
                break;
            }

            
            g2.fillOval(STROKE_WIDTH, STROKE_WIDTH,
                    diameter - 2 * STROKE_WIDTH, diameter - 2 * STROKE_WIDTH);

            g2.setColor(Color.BLACK);
            g2.drawOval(STROKE_WIDTH, STROKE_WIDTH,
                    diameter - 2 * STROKE_WIDTH, diameter - 2 * STROKE_WIDTH);
        }

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
