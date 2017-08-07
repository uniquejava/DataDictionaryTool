package org.ccs.sandbox.sqltool.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.ccs.sandbox.sqltool.ConfigSet;
import org.ccs.sandbox.sqltool.datamodel.mysql.NavicatSQL;
import org.ccs.sandbox.sqltool.datamodel.oracle.ToadSQL;
import org.ccs.sandbox.sqltool.input.NavicatFileReader;
import org.ccs.sandbox.sqltool.input.SQLFileReader;
import org.ccs.sandbox.sqltool.input.ToadFileReader;
import org.ccs.sandbox.sqltool.output.MSWordWriter;
import org.ccs.sandbox.sqltool.parser.NavicatParser;
import org.ccs.sandbox.sqltool.parser.ToadParser;

import ysb.common.FileUtil;

public class Controller {
    public static enum DB {
        MYSQL, ORACLE
    }

    private String sqlText;
    private NavicatSQL navicatSQL;
    private ToadSQL toadSQL;
    private DB db;

    public Controller() {
    }

    public Controller(DB db) {
        this.db = db;
    }

    public String readSQLScript(File sqlFile, String encoding, DB db) throws Exception {
        SQLFileReader sqlFileReader;
        switch (db) {
        case MYSQL:
            sqlFileReader = new NavicatFileReader();
            this.sqlText = sqlFileReader.read(sqlFile, encoding);
            break;
        case ORACLE:
            sqlFileReader = new ToadFileReader();
            this.sqlText = sqlFileReader.read(sqlFile, encoding);
        }
        return this.sqlText;
    }

    public void setSqlText(String paramString) {
        this.sqlText = paramString;
    }

    public Object parse(DB db) throws Exception {
        if ((this.sqlText != null) && (this.sqlText.trim().length() > 0)) {
            Object localObject;
            switch (db) {
            case MYSQL:
                localObject = new NavicatParser(this.sqlText);
                this.navicatSQL = ((NavicatParser) localObject).parse();
                return this.navicatSQL;
            case ORACLE:
                localObject = new ToadParser(this.sqlText);
                this.toadSQL = ((ToadParser) localObject).parse();
                return this.toadSQL;
            }
        }
        return null;
    }

    public void write2MSWord(File template, File dest, DB db) throws Exception {
        if ((this.navicatSQL != null) || (this.toadSQL != null)) {
            MSWordWriter wordWriter = new MSWordWriter();

            // 如果模板不存在， 则使用内置的模板
            if ((template == null) || (!(template.exists()))) {
                String templatePath = ConfigSet.getInstance().get("template.location");
                // 复制模板到指定的位置
                FileUtil.transfer(super.getClass().getResourceAsStream("dict2.doc"),
                        new FileOutputStream(templatePath));
                template = new File(templatePath);
            }
            switch (db) {
            case MYSQL:
                wordWriter.write(this.navicatSQL, template, dest);
                break;
            case ORACLE:
                wordWriter.write(this.toadSQL, template, dest);
            }
        }
    }

    public static void main(String[] paramArrayOfString) throws Exception {
        Controller controller = new Controller(DB.MYSQL);
        controller.readSQLScript(new File("c:/test.sql"), "UTF-8", DB.MYSQL);
        controller.parse(DB.MYSQL);
        controller.write2MSWord(null, new File("c:/file_output.doc"), DB.MYSQL);
    }

    public NavicatSQL getNavicatSQL() {
        return this.navicatSQL;
    }

    public ToadSQL getToadSQL() {
        return this.toadSQL;
    }
}