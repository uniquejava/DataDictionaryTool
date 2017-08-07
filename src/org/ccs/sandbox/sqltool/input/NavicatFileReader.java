package org.ccs.sandbox.sqltool.input;

import java.io.File;

import ysb.common.FileUtil;

public class NavicatFileReader implements SQLFileReader {
    @Override
    public String read(File file, String encoding) throws Exception {
        return FileUtil.getFileContent(file, encoding);
    }

    @Override
    public String read(File file) throws Exception {
        return FileUtil.getFileContent(file, null);
    }
}