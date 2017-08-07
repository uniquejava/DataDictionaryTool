package org.ccs.sandbox.sqltool.input;

import java.io.File;

import ysb.common.FileUtil;

public class ToadFileReader implements SQLFileReader {
    public String read(File file, String encoding) throws Exception {
        return FileUtil.getFileContent(file, encoding);
    }

    public String read(File file) throws Exception {
        return FileUtil.getFileContent(file, null);
    }
}