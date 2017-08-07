package org.ccs.sandbox.sqltool.ui;

import java.io.File;

import org.apache.log4j.Logger;
import org.ccs.sandbox.sqltool.ConfigSet;
import org.ccs.sandbox.sqltool.controller.BatchProcessor;
import org.ccs.sandbox.sqltool.controller.Controller;
import org.ccs.sandbox.sqltool.controller.Controller.DB;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import ysb.common.C;
import ysb.common.FileUtil;
import ysb.swt.dialog.Function;
import ysb.swt.dialog.LRCompositeWithMenu;
import ysb.swt.dialog.ShellUtil;

public class DataDictionaryToolUI extends LRCompositeWithMenu {

    public static final String PRODUCT = "MySQL/Oracle数据字典生成工具";
    public static final String VERSION = "V2.0.0(build20170807)";
    private static String[] fileNames;
    private static final Logger log = Logger.getLogger(DataDictionaryToolUI.class);
    private static final String TARGET_DOC = ConfigSet.getInstance().get("target.doc");

    public static void main(String[] paramArrayOfString) {
        DataDictionaryToolUI app = new DataDictionaryToolUI();
        shell.setText(PRODUCT + VERSION);
        ShellUtil.makeShellMaximized(display, shell);
        ShellUtil.makeShellCentered(display, shell);
        app.makeComponents(shell);
        app.createSysMenuBar(shell);
        app.readAndDispatch(display, shell);
    }

    public int getLeft() {
        return 1;
    }

    public int getRight() {
        return 100;
    }

    protected Function[] getFunctions() {
        return null;
    }

    protected String getSysFunctions1Text() {
        return "&MySQL";
    }

    protected Function[] getSysFunctions1() {
        MySQLMultiFileLoader multiFileLoader = new MySQLMultiFileLoader();
        MySQLMultiFileGenerator multiFileGenerator = new MySQLMultiFileGenerator();
        MySQLBigFileLoader bigFileLoader = new MySQLBigFileLoader();
        MySQLBigFileGenerator bigFileGenerator = new MySQLBigFileGenerator();
        return new Function[] { multiFileLoader, multiFileGenerator, bigFileLoader, bigFileGenerator };
    }

    private class MySQLMultiFileLoader implements Function {
        public String getText() {
            return "A1：载入SQL文件(一个sql文件对应一张表结构,可选多个文件)";
        }

        public void execute() {
            FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
            String firstFilePath = fileDialog.open();

            fileNames = fileDialog.getFileNames();

            if (firstFilePath != null)
                try {
                    srcText.setText("");
                    for (int i = 0; i < fileNames.length; ++i) {
                        fileNames[i] = fileDialog.getFilterPath() + C.FILE_S + fileNames[i];
                        srcText.append(fileNames[i]);
                        srcText.append(C.LINE_S);
                    }
                } catch (Exception e) {
                    error(e);
                }
        }
    }

    private class MySQLMultiFileGenerator implements Function {
        public String getText() {
            return "A2: 生成数据字典(一个sql文件对应一张表结构,可选多个文件)";
        }

        public void execute() {
            BatchProcessor batchProcessor = new BatchProcessor(DB.MYSQL);
            try {
                batchProcessor.readSQLScript(fileNames, loadEncodingCombo.getText());
                batchProcessor.parse();
                batchProcessor.write2MSWord(new File(TARGET_DOC));
            } catch (Throwable e) {
                error(e);
            }
        }

    }

    private class MySQLBigFileLoader implements Function {
        public String getText() {
            return "B1：载入SQL文件(一个sql文件包括多个表结构)";
        }

        public void execute() {
            FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
            String filePath = fileDialog.open();
            if (filePath != null)
                try {
                    srcText.setText(filePath);
                } catch (Exception e) {
                    error(e);
                }
        }
    }

    /** IMPORTANT */
    private class MySQLBigFileGenerator implements Function {
        public String getText() {
            return "B2: 生成数据字典(一个sql文件包括多个表结构)";
        }

        public void execute() {
            BatchProcessor batchProcessor = new BatchProcessor(DB.MYSQL);
            try {
                batchProcessor.readBigSQLScript(srcText.getText(), loadEncodingCombo.getText());
                batchProcessor.parse();
                batchProcessor.write2MSWord(new File(TARGET_DOC));
            } catch (Throwable e) {
                e.printStackTrace();
                error(e);
            }
        }
    }

    protected String getSysFunctions2Text() {
        return "&Oracle";
    }

    protected Function[] getSysFunctions2() {
        SimpleFileLoader simpleFileLoader = new SimpleFileLoader();
        SimpleFileGenerator simpleFileGenerator = new SimpleFileGenerator();
        return new Function[] { simpleFileLoader, simpleFileGenerator };
    }

    private class SimpleFileLoader implements Function {
        public String getText() {
            return "A1：载入SQL文件(一次只能处理一张表)";
        }

        public void execute() {
            FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
            String filePath = fileDialog.open();
            if (filePath != null)
                try {
                    String content = FileUtil.getFileContent(new File(filePath), loadEncodingCombo.getText());
                    srcText.setText(content);
                } catch (Exception e) {
                    error(e);
                }
        }
    }

    private class SimpleFileGenerator implements Function {
        public String getText() {
            return "A2: 生成数据字典()";
        }

        public void execute() {
            DB db = DB.ORACLE;
            try {
                log.debug("NEW Controller...");
                Controller controller = new Controller();
                log.debug("Read SQL SCRIPT From Console");
                controller.setSqlText(srcText.getText());
                if (srcText.getText().indexOf("`") != -1) {
                    db = DB.MYSQL;
                }

                log.debug("Begin Parse .........");
                controller.parse(db);
                log.debug("Begin Write to MSWord");
                controller.write2MSWord(null, null, db);
            } catch (Exception e) {
                error(e);
            }
        }
    }

}