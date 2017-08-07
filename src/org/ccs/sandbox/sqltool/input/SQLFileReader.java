package org.ccs.sandbox.sqltool.input;

import java.io.File;

public interface SQLFileReader {

    String read(File file, String encoding) throws Exception;

    String read(File file) throws Exception;

}