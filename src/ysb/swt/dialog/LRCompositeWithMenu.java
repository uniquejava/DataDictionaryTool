package ysb.swt.dialog;

import org.ccs.sandbox.sqltool.HelpDocument;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class LRCompositeWithMenu extends LRComposite {
    protected Menu menuBar;

    protected String getCopyright() {
        return "CopyRight 2007-2017\nEmail: iamcyper@qq.com\n主页：https://github.com/uniquejava, 欢迎star";
    }

    protected String getCopyrightPiggySnow() {
        return "期间(2013)由 PiggySnow 修改，修复word 2007 下不能输出的问题。 主页：http://www.piggysnow.com/";
    }

    public static void main(String[] paramArrayOfString) {
        LRCompositeWithMenu app;
        try {
            app = new LRCompositeWithMenu();
            ShellUtil.makeShellCentered(display, shell);
            app.createSysMenuBar(shell);
            app.makeComponents(shell);
            app.readAndDispatch(display, shell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Function[] getSysFunctions1() {
        return null;
    }

    protected Function[] getSysFunctions2() {
        return null;
    }

    protected void createSysMenuBar(Shell parent) {
        this.menuBar = new Menu(parent, SWT.BAR);

        // file menu
        Menu fileMenu = new Menu(parent, SWT.DROP_DOWN);
        MenuItem file = new MenuItem(this.menuBar, SWT.CASCADE);
        file.setText("&File");
        file.setMenu(fileMenu);

        MenuItem save = new MenuItem(fileMenu, SWT.PUSH);
        save.setText("&Save");

        new MenuItem(fileMenu, SWT.SEPARATOR);

        MenuItem exit = new MenuItem(fileMenu, SWT.PUSH);
        exit.setText("&Exit\t ESC");
        exit.setAccelerator(27);
        exit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });

        // mysql menu
        Menu mysqlMenu = new Menu(parent, SWT.DROP_DOWN);
        MenuItem mysql = new MenuItem(this.menuBar, SWT.CASCADE);
        mysql.setText(getSysFunctions1Text());
        mysql.setMenu(mysqlMenu);

        Function[] functions = getSysFunctions1();
        if (functions != null) {
            for (int i = 0; i < functions.length; ++i) {
                final Function fn = functions[i];
                MenuItem menuItem = new MenuItem(mysqlMenu, SWT.PUSH);
                menuItem.setText(fn.getText());
                menuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        fn.execute();
                    }
                });
            }
        }

        // oracle menu
        Menu oracleMenu = new Menu(parent, SWT.DROP_DOWN);
        MenuItem oracle = new MenuItem(this.menuBar, SWT.CASCADE);
        oracle.setText(getSysFunctions2Text());
        oracle.setMenu(oracleMenu);

        Function[] functions2 = getSysFunctions2();
        if (functions2 != null) {
            for (int i = 0; i < functions2.length; ++i) {
                final Function fn = functions2[i];
                MenuItem menuItem = new MenuItem(oracleMenu, SWT.PUSH);
                menuItem.setText(fn.getText());
                menuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        fn.execute();
                    }
                });
            }
        }

        // help menu
        Menu helpMenu = new Menu(parent, SWT.DROP_DOWN);
        MenuItem help = new MenuItem(this.menuBar, SWT.CASCADE);
        help.setText("&Help");
        help.setMenu(helpMenu);

        MenuItem doc = new MenuItem(helpMenu, SWT.PUSH);
        doc.setText("查看帮助文档");
        doc.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                HelpDocumentDialog dialog = new HelpDocumentDialog(getShell());
                dialog.setHelpText(HelpDocument.getInstance().getText());
                dialog.open();
            }
        });

        MenuItem aboutMe = new MenuItem(helpMenu, SWT.PUSH);
        aboutMe.setText("关于 科比代码 工作室");
        aboutMe.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog.openInformation(null, "CobeCode Studio", getCopyright());
            }
        });

        MenuItem aboutPiggyShow = new MenuItem(helpMenu, SWT.PUSH);
        aboutPiggyShow.setText("关于 PiggySnow 工作室");
        aboutPiggyShow.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog.openInformation(null, "PiggySnow 工作室", getCopyrightPiggySnow());
            }
        });

        parent.setMenuBar(this.menuBar);

    }

    protected String getSysFunctions1Text() {
        return "F&unction1";
    }

    protected String getSysFunctions2Text() {
        return "F&unction2";
    }
}