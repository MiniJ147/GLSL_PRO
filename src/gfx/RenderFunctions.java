package gfx;

import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;

import static utils.Utils.println;

class Line{
    private String line;
    public int x, y;

    public Line(String line, int x, int y){
        println("Created Line");
        this.line=line;
        this.x=x;
        this.y=y;
    }

    public void render(Graphics g){
        g.drawString(line,x,y);
    }

    String getLine(){
        return line;
    }

    void setLine(String str){
        println("Setting Line");
        line = str;
    }
};

public abstract class RenderFunctions {

    private static LinkedList<Line> render_lines = new LinkedList<>();

    public static void drawString(Graphics g, String text, int x, int y){
        int lineHeight = g.getFontMetrics().getHeight();
        int i = 0;
        for (String src_line : text.split("\n")) {
            y+=lineHeight;

            if(render_lines.size()<=i || render_lines.get(i)==null){
                render_lines.add(new Line(src_line,x,y));
            }
            Line curr_line = render_lines.get(i);

            if(curr_line.getLine().length()!=src_line.length() || curr_line.getLine().compareTo(src_line)>0)
                curr_line.setLine(src_line);

            curr_line.render(g);

            //rendering line number
            g.drawString("["+(i+1)+"]",4,y);
            i++;
        }
    }
}
