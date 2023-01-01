package gfx;

import java.awt.*;

public abstract class RenderFunctions {

    public static void drawString(Graphics g, String text, int x, int y){
        int lineHeight = g.getFontMetrics().getHeight();
        for (String line : text.split("\n")) {
            g.drawString(line, x, y += lineHeight);
        }
    }
}
