package gfx;

import javax.swing.*;
import java.awt.*;

public class Display {
    private int width, height;
    private String name;
    private JFrame frame;
    private Canvas canvas;

    public Display(int width, int height, String name){
        this.width=width;
        this.height=height;
        this.name=name;

        createDisplay(width,height,name);
    }

    private void createDisplay(int width, int height, String name){
        frame = new JFrame(name);
        frame.setSize(new Dimension(width,height));
        frame.setResizable(true);
        frame.setFocusable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();
        canvas.setSize(new Dimension(width,height));
        canvas.setFocusable(false);

        frame.add(canvas);
        frame.setVisible(false);
    }

    public JFrame getFrame() {
        return frame;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
