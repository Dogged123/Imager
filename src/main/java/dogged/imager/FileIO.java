package dogged.imager;

import java.io.*;

public class FileIO {
    public static void makeDirectory(String directoryName) {
        try {
            if (new File(directoryName).mkdirs()) System.out.println("Successfully created directory: " + directoryName);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static String[] getDirectoryContents(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.list();
    }
}
