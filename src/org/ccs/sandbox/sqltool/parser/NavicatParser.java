package org.ccs.sandbox.sqltool.parser;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ccs.sandbox.sqltool.datamodel.mysql.Field;
import org.ccs.sandbox.sqltool.datamodel.mysql.FieldDesc;
import org.ccs.sandbox.sqltool.datamodel.mysql.NavicatSQL;
import org.ccs.sandbox.sqltool.datamodel.mysql.REField;
import org.ccs.sandbox.sqltool.input.Input;

import ysb.common.FileUtil;
import ysb.common.REUtil;

public class NavicatParser {
    private static final Logger log = Logger.getLogger(NavicatParser.class);
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static final String INSERT_INTO = "INSERT INTO";
    private String navicatSQLText;
    private String validText;
    private String tableName;
    private String primaryKeyName;
    private String engine;
    private String charset;
    private REField[] reFields;
    private Field[] fields;

    public NavicatParser(String navicatSQLText) {
        log.debug("Contructor NavicatParser(String navicatSQLText) is called.");
        this.navicatSQLText = navicatSQLText;
        makeValidText();
    }

    public NavicatSQL parse() throws Exception {
        log.debug("makeTableName");
        makeTableName();
        log.debug("makeREFields");
        makeREFields();
        log.debug("makeEngine");
        makeEngine();
        log.debug("makeCharset");
        makeCharset();
        log.debug("makeFields");
        makeFields();
        NavicatSQL navicatSQL = new NavicatSQL();
        navicatSQL.setTableName(this.tableName);
        navicatSQL.setFields(this.fields);
        navicatSQL.setCharset(this.charset);
        navicatSQL.setEngine(this.engine);
        return navicatSQL;
    }

    private void makeFields() {
        Field[] fields = new Field[this.reFields.length];
        for (int i = 0; i < this.reFields.length; ++i) {
            REField reField = this.reFields[i];
            FieldDesc fieldDesc = new FieldDesc();
            fieldDesc.setPrimaryKey(reField.getName().equalsIgnoreCase(this.primaryKeyName));
            fieldDesc.setLongDesc(reField.getLongDesc());
            fieldDesc.setMysqlTypeAndLength(reField.getMysqlTypeAndLength());
            fieldDesc.setMysqlType(reField.getMysqlType());
            fieldDesc.setLength(reField.getLength());
            fieldDesc.setDotLength(reField.getDotLength());
            fieldDesc.setNullable((!(reField.isContainLiteral_NOT_())) || (!(reField.isContainLiteral_NULL_())));
            fieldDesc.setComment(reField.getComment());
            fieldDesc.setPrimaryKeyDesc((reField.isContainLiteral_auto_increment_()) ? "自增主键" : "");
            Field localField = new Field(reField.getName(), fieldDesc);
            localField.setFieldName(reField.getName());
            localField.setFieldDesc(fieldDesc);
            fields[i] = localField;
        }
        this.fields = fields;
    }

    private void makeCharset() {
        String[] arrayOfString = REUtil.getREGroupSet(this.validText, "CHARSET=([\\w]+)");
        if ((arrayOfString != null) && (arrayOfString.length > 0))
            this.charset = arrayOfString[0];
    }

    private void makeEngine() {
        String[] arrayOfString = REUtil.getREGroupSet(this.validText, "ENGINE=([\\w]+)");
        if ((arrayOfString != null) && (arrayOfString.length > 0))
            this.engine = arrayOfString[0];
    }

    private void makePrimaryKey() {
        String[] arrayOfString = REUtil.getREGroupSet(this.validText, "PRIMARY KEY  \\(`([\\w]+)`\\)");
        if ((arrayOfString != null) && (arrayOfString.length > 0))
            this.primaryKeyName = arrayOfString[0];
    }

    private void makeTableName() {
        String[] arrayOfString = REUtil.getREGroupSet(this.validText, "CREATE TABLE `([\\w]+)` \\(");
        if ((arrayOfString != null) && (arrayOfString.length > 0))
            this.tableName = arrayOfString[0];
    }

    private void makeREFields() {
        makePrimaryKey();
        String[] arrayOfString = REUtil.getREGroupVector(this.validText,
                "(`([\\w]+)` (([\\w]+)(?:\\(([\\d]+),?(\\d)?\\))?)( unsigned)?( NOT)?( NULL)?( default (?:NULL|'[^']*'))?( COMMENT '([^']+)')?( auto_increment)?)");
        Vector localVector = new Vector();
        REField localREField = new REField();
        int i = 13;
        for (int j = 0; j < arrayOfString.length; ++j) {
            localREField.setField(j % i, arrayOfString[j]);
            if (j % i == i - 1) {
                localVector.add(localREField);
                localREField = new REField();
            }
        }
        REField[] arrayOfREField = new REField[localVector.size()];
        localVector.toArray(arrayOfREField);
        this.reFields = arrayOfREField;
    }

    private void makeValidText() {
        this.validText = this.navicatSQLText;
        if (this.validText.indexOf("CREATE TABLE") != -1)
            this.validText = this.validText.substring(this.validText.indexOf("CREATE TABLE"));
        else
            log.warn("text (CREATE TABLE) cannot be found!");
        if (this.validText.indexOf("INSERT INTO") != -1)
            this.validText = this.validText.substring(0, this.validText.indexOf("INSERT INTO"));
    }

    public static void main(String[] args) throws Exception {
        String str = FileUtil.getFileContent(new File(Input.NAVICAT_TEST_FILE), null);
        NavicatParser parser = new NavicatParser(str);
        NavicatSQL navicatSQL = parser.parse();
        log.info(navicatSQL.getCharset());
        log.info(navicatSQL.getEngine());
        log.info(navicatSQL.getTableName());
        log.info(navicatSQL.getFields()[0].getFieldName());
        log.info(navicatSQL.getFields()[0].getFieldDesc().getMysqlType());
        log.info(navicatSQL.getFields()[0].getFieldDesc().getLength());
        log.info(navicatSQL.getFields()[0].getFieldDesc().isPrimaryKey());
        log.info(navicatSQL.getFields()[0].getFieldDesc().isNullable());
    }
}