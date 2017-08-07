package ysb.swt.dialog;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ShellUtil {
    public static void makeShellCentered(Display display, Shell shell) {
        Rectangle shellRect = shell.getBounds();
        Rectangle displayRect = display.getBounds();
        int x = (displayRect.width - shellRect.width) / 2;
        int y = (displayRect.height - shellRect.height) / 2;
        shell.setLocation(x, y);
    }

    public static void makeShellMaximized(Display display, Shell shell) {
        shell.setSize(display.getBounds().width, display.getBounds().height - 30);
        shell.setLocation(0, 0);
    }
}