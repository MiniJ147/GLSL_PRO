package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class MouseManager implements MouseListener, MouseWheelListener {
    private int mouse_scroll_offset = 0;
    private int mouse_scroll_speed = 10;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(mouse_scroll_offset+e.getWheelRotation()>0)
            mouse_scroll_offset += e.getWheelRotation();
    }

    public int getMouse_scroll_offset() {
        return mouse_scroll_offset;
    }

    public int getMouse_scroll_speed() {
        return mouse_scroll_speed;
    }
}
