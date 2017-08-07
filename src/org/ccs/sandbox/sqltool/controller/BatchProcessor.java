package org.ccs.sandbox.sqltool.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.ccs.sandbox.sqltool.ConfigSet;
import org.ccs.sandbox.sqltool.controller.Controller.DB;
import org.ccs.sandbox.sqltool.datamodel.mysql.NavicatSQL;
import org.ccs.sandbox.sqltool.datamodel.oracle.ToadSQL;
import org.ccs.sandbox.sqltool.input.NavicatFileReader;
import org.ccs.sandbox.sqltool.input.SQLFileReader;
import org.ccs.sandbox.sqltool.output.BatchWordWriter;
import org.ccs.sandbox.sqltool.parser.NavicatParser;

import ysb.common.C;
import ysb.common.FileUtil;

public class BatchProcessor {
    private String[] sqlTexts;
    private NavicatSQL[] navicatSQL;
    private ToadSQL[] toadSQL;

    private DB db;

    public BatchProcessor(DB db) {
        this.db = db;
    }

    public String[] readSQLScript(String[] fileNames, String encoding) throws Exception {
        switch (db) {
        case MYSQL:
            SQLFileReader fileReader = new NavicatFileReader();
            this.sqlTexts = new String[fileNames.length];
            for (int i = 0; i < fileNames.length; ++i)
                this.sqlTexts[i] = fileReader.read(new File(fileNames[i]), encoding);
        case ORACLE:
        }
        return this.sqlTexts;
    }

    public String[] readBigSQLScript(String paramString1, String encoding) throws Exception {
        switch (db) {
        case MYSQL:
            SQLFileReader fileReader = new NavicatFileReader();
            String[] arrayOfString = FileUtil.getFileContent(new File(paramString1.replaceAll(C.LINE_S, "")), C.LINE_S,
                    encoding, "DROP TABLE");
            this.sqlTexts = new String[arrayOfString.length];
            for (int i = 0; i < arrayOfString.length; ++i)
                this.sqlTexts[i] = arrayOfString[i];
        case ORACLE:
        }
        return this.sqlTexts;
    }

    public void setSqlTexts(String[] sqlTexts) {
        this.sqlTexts = sqlTexts;
    }

    public Object[] parse() throws Exception {
        this.navicatSQL = new NavicatSQL[this.sqlTexts.length];
        if ((this.sqlTexts != null) && (this.sqlTexts.length > 0))
            switch (db) {
            case MYSQL:
                for (int i = 0; i < this.sqlTexts.length; ++i) {
                    String str = this.sqlTexts[i];
                    NavicatParser parser = new NavicatParser(str);
                    this.navicatSQL[i] = parser.parse();
                }
            case ORACLE:
            }
        return this.navicatSQL;
    }

    public void write2MSWord(File paramFile) throws Throwable {
        String str = ConfigSet.getInstance().get("template.location");
        FileUtil.transfer(super.getClass().getResourceAsStream("dict2.doc"), new FileOutputStream(str));
        File localFile = new File(str);
        write2MSWord(localFile, paramFile);
    }

    public void write2MSWord(File paramFile1, File paramFile2) throws Throwable {
        if ((this.navicatSQL != null) || (this.toadSQL != null)) {
            BatchWordWriter localBatchWordWriter = new BatchWordWriter();
            if ((paramFile1 == null) || (!(paramFile1.exists()))) {
                String str = ConfigSet.getInstance().get("template.location");
                FileUtil.transfer(super.getClass().getResourceAsStream("dict2.doc"), new FileOutputStream(str));
                paramFile1 = new File(str);
            }
            switch (db) {
            case MYSQL:
                localBatchWordWriter.write(this.navicatSQL, paramFile1, paramFile2);
            case ORACLE:
            }
        }
    }

    public static void main(String[] args) throws Exception {
    }

    public NavicatSQL[] getNavicatSQL() {
        return this.navicatSQL;
    }

    public ToadSQL[] getToadSQL() {
        return this.toadSQL;
    }
}