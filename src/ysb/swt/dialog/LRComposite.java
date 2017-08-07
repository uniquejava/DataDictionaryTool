package ysb.swt.dialog;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ysb.common.C;
import ysb.common.ExceptionUtil;
import ysb.common.FileUtil;

public class LRComposite {
    protected Composite leftComp;
    protected Text srcText;
    protected Combo loadEncodingCombo;
    protected Combo saveEncodingCombo;
    protected Composite buttonComp;
    protected Button okButton;
    protected Button cancelButton;
    protected int left = 5;
    protected int right = 5;
    protected ToolBar customizeToolbar;
    protected ToolItem customizeButton;
    protected Menu customizeMenu;
    protected static Display display = Display.getDefault();
    protected static Shell shell = new Shell();

    public static void main(String[] paramArrayOfString) {
        LRComposite app;
        try {
            app = new LRComposite();
            ShellUtil.makeShellCentered(display, shell);
            app.makeComponents(shell);
            app.readAndDispatch(display, shell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void readAndDispatch(Display display, Shell shell) {
        shell.open();
        shell.layout();

        this.onViewDidLoad();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();

        }

        this.onExit();

        display.dispose();
    }

    protected void onViewDidLoad() {

    }

    protected void onExit() {

    }

    protected void setTitle(Shell shell, String title) {
        shell.setText(title);
    }

    protected Shell getShell() {
        return shell;
    }

    public void makeComponents(Shell parent) {
        init();
        addBe4SashForm();
        SashForm sashForm = new SashForm(parent, SWT.NONE);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.leftComp = new Composite(sashForm, SWT.NONE);
        renderLeftComp();

        Composite rightComp = new Composite(sashForm, SWT.NONE);
        rightComp.setLayout(new GridLayout());
        Composite topComp = new Composite(rightComp, SWT.BORDER);
        topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        topComp.setLayout(new RowLayout());
        Button btnLoad = new Button(topComp, SWT.PUSH);
        btnLoad.setText("Load");
        btnLoad.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
                String str1 = fileDialog.open();
                if (str1 != null)
                    try {
                        String str2 = FileUtil.getFileContent(new File(str1), loadEncodingCombo.getText());
                        srcText.setText(str2);
                    } catch (Exception e) {
                        error(e);
                    }
            }
        });
        this.loadEncodingCombo = new Combo(topComp, SWT.NONE);
        this.loadEncodingCombo.add(C.FILE_ENCODING);
        this.loadEncodingCombo.add("UTF-8");
        this.loadEncodingCombo.add("GBK");
        this.loadEncodingCombo.add("");
        this.loadEncodingCombo.select(0);
        this.loadEncodingCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveEncodingCombo.select(loadEncodingCombo.getSelectionIndex());
            }
        });
        Button btnSave = new Button(topComp, SWT.PUSH);
        btnSave.setText("Save");
        btnSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                fileDialog.setFileName("tmp.txt");
                String str = fileDialog.open();
                if (str != null)
                    try {
                        FileUtil.setFileContent(new File(str), srcText.getText(), saveEncodingCombo.getText());
                    } catch (Exception e2) {
                        error(e2);
                    }
            }
        });
        this.saveEncodingCombo = new Combo(topComp, SWT.NONE);
        this.saveEncodingCombo.add(C.FILE_ENCODING);
        this.saveEncodingCombo.add("UTF-8");
        this.saveEncodingCombo.add("GBK");
        this.saveEncodingCombo.add("");
        this.saveEncodingCombo.select(0);

        createCustomizeToolBar(topComp);

        this.srcText = new Text(rightComp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        this.srcText.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.srcText.addListener(1, new Listener() {
            public void handleEvent(Event event) {
                if ((event.stateMask == SWT.CTRL) && (event.keyCode == 'a'))
                    srcText.selectAll();
            }
        });

        this.buttonComp = new Composite(parent, SWT.BORDER);
        this.buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 15;
        this.buttonComp.setLayout(rowLayout);
        this.okButton = new Button(this.buttonComp, SWT.PUSH);
        this.okButton.setText("   OK   ");
        this.okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                okPressed();
            }
        });
        this.cancelButton = new Button(this.buttonComp, SWT.PUSH);
        this.cancelButton.setText("Cancel");
        this.cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });

        sashForm.setWeights(new int[] { getLeft(), getRight() });
    }

    private void createCustomizeToolBar(Composite parent) {
        this.customizeToolbar = new ToolBar(parent, SWT.DROP_DOWN);
        this.customizeButton = new ToolItem(this.customizeToolbar, SWT.CASCADE);
        this.customizeButton.setText("Customize");
        this.customizeMenu = new Menu(parent.getShell(), SWT.PUSH);
        addFunctionMenuItem();
        this.customizeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Rectangle rectangle = customizeButton.getBounds();
                Point point = new Point(rectangle.x, rectangle.y + rectangle.height);
                point = customizeToolbar.toDisplay(point);
                customizeMenu.setLocation(point.x, point.y);
                customizeMenu.setVisible(true);
            }
        });
    }

    protected void addFunctionMenuItem() {
        Function[] functions = getFunctions();
        if (functions != null)
            for (int i = 0; i < functions.length; ++i) {
                final Function fn = functions[i];
                MenuItem menuItem = new MenuItem(this.customizeMenu, SWT.PUSH);
                menuItem.setText(fn.getText());
                menuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        fn.execute();
                    }
                });
            }
    }

    protected Function[] getFunctions() {
        return new Function[] { new Function() {

            @Override
            public String getText() {
                return "Sample Menu";
            }

            @Override
            public void execute() {
                System.out.println("Sample Menu clicked.");
            }
        } };
    }

    protected void okPressed() {
        getShell().dispose();
    }

    protected void cancelPressed() {
        getShell().dispose();
    }

    protected void renderLeftComp() {
    }

    protected void addBe4SashForm() {
    }

    protected void init() {
    }

    protected void error(Throwable paramThrowable) {
        MessageDialog.openInformation(getShell(), "Error", ExceptionUtil.printStackTrace(paramThrowable));
    }

    protected void info(String message) {
        MessageDialog.openInformation(getShell(), "Message", message);
    }

    protected void info(String title, String message) {
        MessageDialog.openInformation(getShell(), title, message);
    }

    public int getLeft() {
        return this.left;
    }

    public void setLeft(int paramInt) {
        this.left = paramInt;
    }

    public int getRight() {
        return this.right;
    }

    public void setRight(int paramInt) {
        this.right = paramInt;
    }

    static {
        shell.setLayout(new GridLayout());
        shell.setSize(700, 500);
        shell.setText("SWT Application");
    }
}