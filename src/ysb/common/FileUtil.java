package ysb.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;

public class FileUtil {

    public static void transfer(String paramString, Writer paramWriter) {
        try {
            paramWriter.write(paramString);
            paramWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(paramWriter);
        }
    }

    public static void transfer(byte[] paramArrayOfByte, OutputStream os) {
        try {
            os.write(paramArrayOfByte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(os);
        }
    }

    public static void transfer(InputStream is, OutputStream os) {
        tranfer(is, os, 102400);
    }

    public static void tranfer(InputStream is, OutputStream os, int bufferSize) {
        try {
            byte[] buffer = new byte[bufferSize];
            int i;
            while ((i = is.read(buffer)) != -1) {
                os.write(buffer, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(os, is);
        }
    }

    public static String getFileContent(File file, String lineSeparator, String encoding) throws Exception {
        FileInputStream fis = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder((int) file.length());
        if (file.length() > 0) {
            try {
                fis = new FileInputStream(file);
                if ((encoding != null) && (!encoding.equals(""))) {
                    br = new BufferedReader(new InputStreamReader(fis, encoding));
                } else {
                    br = new BufferedReader(new InputStreamReader(fis));
                }
                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                    sb.append(lineSeparator);
                }
                sb.setLength(sb.length() - lineSeparator.length());
            } finally {
                close(fis);
                close(br);
            }
        }
        return sb.toString();
    }

    public static String[] getFileContent(File file, String lineSeparator, String encoding, String paramString3)
            throws Exception {
        FileInputStream fis = null;
        BufferedReader br = null;
        Vector localVector = new Vector();
        if (file.length() > 0) {
            try {
                fis = new FileInputStream(file);
                if ((encoding != null) && (!(encoding.equals(""))))
                    br = new BufferedReader(new InputStreamReader(fis, encoding));
                else
                    br = new BufferedReader(new InputStreamReader(fis));
                String str = "";
                boolean beforeSQL = true;
                StringBuilder sb = new StringBuilder();
                while (((str = br.readLine()) != null)) {
                    if (str.indexOf(paramString3) == -1) {
                        sb.append(str);
                        sb.append(lineSeparator);
                    } else {
                        if (beforeSQL) {
                            beforeSQL = false;
                        } else {
                            localVector.add(sb.toString());
                        }
                        sb = new StringBuilder(str + lineSeparator);
                    }
                }
            } finally {
                close(fis);
                close(br);
            }
        }
        String[] arrayOfString = new String[localVector.size()];
        localVector.toArray(arrayOfString);
        return arrayOfString;
    }

    public static String getFileContent(File file, String encoding) throws Exception {
        return getFileContent(file, C.LINE_S, encoding);
    }

    public static void setFileContent(File paramFile, String content, String encoding) throws Exception {
        FileOutputStream fis = null;
        try {
            byte[] arrayOfByte;
            fis = new FileOutputStream(paramFile);
            if ((encoding != null) && (!(encoding.equals(""))))
                arrayOfByte = content.getBytes(encoding);
            else
                arrayOfByte = content.getBytes();
            fis.write(arrayOfByte, 0, arrayOfByte.length);
        } finally {
            close(fis);
        }
    }

    public static void appendAtTheBeginning(File paramFile, String paramString1, String paramString2) throws Exception {
        String str = getFileContent(paramFile, paramString2);
        str = paramString1 + str;
        setFileContent(paramFile, str, paramString2);
    }

    public static void appendAtTheEnd(File paramFile, String paramString1, String paramString2) throws Exception {
        String str = getFileContent(paramFile, paramString2);
        str = str + paramString1;
        setFileContent(paramFile, str, paramString2);
    }

    public static void close(InputStream is) {
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Reader paramReader) {
        try {
            paramReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(OutputStream os) {
        try {
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(InputStream is, OutputStream os) {
        close(os, is);
    }

    public static void close(Writer paramWriter) {
        if (paramWriter != null)
            try {
                paramWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void close(OutputStream os, InputStream is) {
        try {
            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}