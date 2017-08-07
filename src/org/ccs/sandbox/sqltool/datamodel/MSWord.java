package org.ccs.sandbox.sqltool.datamodel;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import ysb.common.JacobManager;

public class MSWord {
    private static final Logger log = Logger.getLogger(MSWord.class);

    // Specifies whether Microsoft Word automatically resizes cells in a table to
    // fit the contents (AutoFit).
    // see
    // https://msdn.microsoft.com/VBA/Word-VBA/articles/wddefaulttablebehavior-enumeration-word
    interface DefaultTableBehavior {
        // Disables AutoFit. Default.
        int word8TableBehavior = 0;
        // Enables AutoFit.
        int word9TableBehavior = 1;
    }

    // Specifies how Microsoft Word resizes a table when the AutoFit feature is
    // used.
    // see
    // https://msdn.microsoft.com/en-us/library/office/microsoft.office.interop.word.wdautofitbehavior.aspx?cs-save-lang=1&cs-lang=csharp#code-snippet-1
    interface AutoFitBehavior {
        // The table is set to a fixed size, regardless of the content, and is not
        // automatically sized.
        int autoFitFixed = 0;
        // The table is automatically sized to fit the content contained in the table.
        int autoFitContent = 1;
        // The table is automatically sized to the width of the active window.
        int autoFitWindow = 2;
    }

    // word运行程序对象
    ActiveXComponent word;

    // 所有word文档集合
    Dispatch documents;

    // word文档
    Dispatch document;

    // 选定的范围或插入点
    Dispatch selection;

    // 所有表格
    private Dispatch dTables;

    // 当前表格
    private Dispatch dTable;
    private Dispatch dCell;
    private Dispatch dRows;
    private Dispatch dRow;
    private File template;

    public MSWord(File paramFile) throws Exception {
        this.template = paramFile;

        JacobManager.loadDll();
    }

    public void open() throws IOException {
        this.word = new ActiveXComponent("Word.Application");
        // 为true表示word应用程序可见
        this.word.setProperty("Visible", new Variant(true));
        this.documents = this.word.getProperty("Documents").toDispatch();
        if ((this.template != null) && (this.template.exists())) {
            // 打开一个已存在的文档
            this.document = Dispatch.call(this.documents, "Open", this.template.getPath()).toDispatch();
        } else {
            // 创建一个新的word文档
            this.document = Dispatch.call(this.documents, "Add").toDispatch();
        }
        this.selection = this.word.getProperty("Selection").toDispatch();
    }

    /**
     * @see https://msdn.microsoft.com/en-us/vba/word-vba/articles/tables-add-method-word
     * @param numCols
     * @param numRows
     * 
     */
    public void addNewTable(int numCols, int numRows) {
        this.dTables = Dispatch.get(this.document, "Tables").toDispatch();
        Dispatch range = Dispatch.get(this.selection, "Range").toDispatch();
        this.dTable = Dispatch.call(this.dTables, "Add", range, numCols, numRows,
                DefaultTableBehavior.word9TableBehavior, AutoFitBehavior.autoFitContent).toDispatch();
        select(this.dTable);
        moveDown(1);
    }

    public void duplicateLastRow(int paramInt1, int paramInt2) {
        searchRow(getRowCount(paramInt1));
        Dispatch.call(this.dRow, "select");
        Dispatch.call(this.selection, "Copy");
        for (int i = 0; i < paramInt2; ++i)
            Dispatch.call(this.selection, "Paste");
    }

    public void copyLastRow(int paramInt) {
        searchRow(getRowCount(paramInt));
        Dispatch.call(this.dRow, "select");
        Dispatch.call(this.selection, "Copy");
    }

    public int getRowCount(int paramInt) {
        searchRows(paramInt);
        int i = Dispatch.get(this.dRows, "count").changeType((short) 3).getInt();
        return i;
    }

    public void copyRow(int paramInt1, int paramInt2) {
        searchTable(paramInt1);
        searchRows();
        this.dRow = searchRow(paramInt2);
        Dispatch.call(this.dRow, "select");
        Dispatch.call(this.selection, "Copy");
    }

    public Dispatch searchRow(int paramInt) {
        this.dRow = Dispatch.invoke(this.dRows, "item", 1, new Object[] { new Integer(paramInt) }, new int[1])
                .toDispatch();
        return this.dRow;
    }

    public Dispatch searchRows(int paramInt) {
        searchTable(paramInt);
        this.dRows = Dispatch.get(this.dTable, "rows").toDispatch();
        return this.dRows;
    }

    public Dispatch searchRows() {
        this.dRows = Dispatch.get(this.dTable, "rows").toDispatch();
        return this.dRows;
    }

    public void paste() {
        Dispatch.call(this.selection, "Paste");
    }

    public void putTextToTable(int tableIndex, int paramInt2, String[] paramArrayOfString, int paramInt3) {
        searchTable(tableIndex);
        for (int k = 0; k < paramArrayOfString.length; ++k) {
            int i = k / paramInt3 + paramInt2;
            int j = k % paramInt3 + 1;
            searchCell(i, j);
            select(this.dCell);
            String str = paramArrayOfString[k];
            if (str != null) {
                str = str.replaceAll("(\\u005C)+r", "");
                str = str.replaceAll("(\\u005C)+", "$1");
                String[] arrayOfString = str.split("\\u005Cn");
                for (int l = 0; l < arrayOfString.length; ++l) {
                    write(arrayOfString[l]);
                    if ((arrayOfString.length > 1) && (l != arrayOfString.length - 1))
                        println();
                }
            }
        }
    }

    public void putTextToCell(int paramInt1, int paramInt2, int paramInt3, String text) {
        searchCell(paramInt1, paramInt2, paramInt3);
        select(this.dCell);
        Dispatch.put(this.selection, "Text", text);
    }

    private void select(Dispatch paramDispatch) {
        Dispatch.call(paramDispatch, "Select");
    }

    public Dispatch searchCell(int paramInt1, int paramInt2, int paramInt3) {
        searchTable(paramInt1);
        this.dCell = Dispatch.call(this.dTable, "Cell", new Variant(paramInt2), new Variant(paramInt3)).toDispatch();
        return this.dCell;
    }

    public Dispatch searchCell(int paramInt1, int paramInt2) {
        this.dCell = Dispatch.call(this.dTable, "Cell", new Variant(paramInt1), new Variant(paramInt2)).toDispatch();
        return this.dCell;
    }

    public Dispatch searchTable(int tableIndex) {
        searchTables();
        this.dTable = Dispatch.call(this.dTables, "Item", new Variant(tableIndex)).toDispatch();
        return this.dTable;
    }

    public void searchTables() {
        this.dTables = Dispatch.get(this.document, "Tables").toDispatch();
    }

    public void println(String text) {
        write(text);
        home();
        end();
        cmd("TypeParagraph");
    }

    public void print(String text) {
        home();
        end();
        write(text);
    }

    public void write(String text) {
        Dispatch.put(this.selection, "Text", text);
    }

    public void println() {
        home();
        end();
        cmd("TypeParagraph");
    }

    public void saveAs(File file) throws Exception {
        if (file != null) {
            if (!(file.exists()))
                file.createNewFile();
            Dispatch dispatch = Dispatch.call(this.word, "WordBasic").getDispatch();
            Dispatch.invoke(dispatch, "FileSaveAs", 1,
                    new Object[] { file.getPath(), new Variant(true), new Variant(false) }, new int[1]);
        }
    }

    public void close(boolean saveOnExit) {
        Dispatch.call(this.document, "Close", new Variant(saveOnExit));
        this.word.invoke("Quit", new Variant[0]);
    }

    public void moveDown(int times) {
        for (int i = 0; i < times; ++i)
            Dispatch.call(this.selection, "MoveDown");
    }

    public void moveLeft(int times) {
        for (int i = 0; i < times; ++i)
            Dispatch.call(this.selection, "MoveLeft");
    }

    public void moveRight(int times) {
        for (int i = 0; i < times; ++i)
            Dispatch.call(this.selection, "MoveRight");
    }

    public void insertRowsBelow(int paramInt1, int paramInt2) {
        int i = getRowCount(paramInt1);
        Dispatch dispatch = searchCell(paramInt1, i, 1);
        select(dispatch);
        for (int j = 0; j < paramInt2; ++j)
            cmd("InsertRowsBelow");
    }

    public void cmd(String paramString) {
        Dispatch.call(this.selection, paramString);
    }

    public void cmd(String paramString, Object paramObject) {
        Dispatch.call(this.selection, paramString, paramObject);
    }

    public void home() {
        Dispatch.call(this.selection, "HomeKey", "5");
    }

    public void end() {
        Dispatch.call(this.selection, "EndKey", "5");
    }

    public void goToBegin() {
        Dispatch.call(this.selection, "HomeKey", "6");
    }

    public void goToEnd() {
        Dispatch.call(this.selection, "EndKey", "6");
    }

    public void autoFitTable(int tableIndex) {
        searchTable(tableIndex);
        Dispatch.call(this.dTable, "AutoFitBehavior", AutoFitBehavior.autoFitContent);
        Dispatch.call(this.dTable, "AutoFitBehavior", AutoFitBehavior.autoFitWindow);
    }

    public void makeTables(int paramInt) {
        cmd("WholeStory");
        cmd("Copy");
        for (int i = 0; i < paramInt; ++i)
            cmd("PasteAndFormat", Integer.valueOf(0));
    }

    public void makeTables(String[] paramArrayOfString) {
        cmd("WholeStory");
        cmd("Cut");
        for (int i = 0; i < paramArrayOfString.length; ++i) {
            log.info("TABLE-NAME ------>" + paramArrayOfString[i]);
            Dispatch.put(this.selection, "Text", paramArrayOfString[i].toUpperCase() + "表");
            moveRight(1);
            cmd("TypeParagraph");
            cmd("PasteAndFormat", Integer.valueOf(0));
        }
    }

    public static void main(String[] paramArrayOfString) throws Exception {
        MSWord msWord = new MSWord(new File("d:/dict2.doc"));
        msWord.open();
        msWord.makeTables(5);
        msWord.addNewTable(5, 3);
        msWord.autoFitTable(6);
    }
}