package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {
    private int currentKeyPressed = -100;
    private boolean readyToAdd = false;
    private boolean cap = false;
    private boolean shift_held = false;

    private final int[] blackListedKeys = {16,17,18,20};

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        readyToAdd = true;
        currentKeyPressed = e.getKeyCode();

        if(currentKeyPressed==KeyEvent.VK_CAPS_LOCK)
            cap = !cap;
        else if(currentKeyPressed==KeyEvent.VK_SHIFT)
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

    public int getCurrentKeyPressed() {
        return currentKeyPressed;
    }

    public boolean isCap() {
        return cap;
    }

    public int[] getBlackListedKeys() {
        return blackListedKeys;
    }

    public boolean isReadyToAdd() {
        return readyToAdd;
    }

    public void setReadyToAdd(boolean readyToAdd) {
        this.readyToAdd = readyToAdd;
    }
}
