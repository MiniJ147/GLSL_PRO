package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    private int key_current_pressed = -100;
    private boolean key_ready_to_add = false;
    private boolean key_cap = false;
    private boolean shift_held = false;

    private final int[] key_blacklist = {16,17,18,20};

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        key_ready_to_add = true;
        key_current_pressed = e.getKeyCode();

        if(key_current_pressed==KeyEvent.VK_CAPS_LOCK)
            key_cap = !key_cap;
        else if(key_current_pressed==KeyEvent.VK_SHIFT)
            shift_held = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SHIFT)
            shift_held = false;
    }

    public boolean isShift_held() {
        return shift_held;
    }

    public int getKey_current_pressed() {
        return key_current_pressed;
    }

    public boolean isKey_cap() {
        return key_cap;
    }

    public int[] getKey_blacklist() {
        return key_blacklist;
    }

    public boolean isKey_ready_to_add() {
        return key_ready_to_add;
    }

    public void setKey_ready_to_add(boolean readyToAdd) {
        this.key_ready_to_add = readyToAdd;
    }
}
