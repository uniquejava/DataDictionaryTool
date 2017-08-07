package ysb.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.jacob.com.LibraryLoader;

public class JacobManager {
    private static final Logger log = Logger.getLogger(JacobManager.class);

    private static boolean jacobDllLoaded = false;

    public static void loadDll() throws Exception {
        if (!jacobDllLoaded) {
            String libFile = System.getProperty("os.arch").equals("amd64") ? "jacob-1.17-x64.dll"
                    : "jacob-1.17-x86.dll";
            log.info(libFile);

            InputStream inputStream = JacobManager.class.getResourceAsStream(libFile);
            File temporaryDll = File.createTempFile("jacob", ".dll");
            FileOutputStream outputStream = new FileOutputStream(temporaryDll);
            byte[] array = new byte[8192];
            for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
                outputStream.write(array, 0, i);
            }
            outputStream.close();

            log.info(temporaryDll.getAbsolutePath());

            System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
            LibraryLoader.loadJacobLibrary();

            temporaryDll.deleteOnExit();

            jacobDllLoaded = true;
        }
    }
}
