package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * main
 * <p>
 * Created by @author bendeutz on @created 8/30/16.
 *
 * @version 0.1
 *          Description here!
 */
public class MyActionListener implements ActionListener {
    private boolean state = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        state = true;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
