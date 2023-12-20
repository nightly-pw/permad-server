package club.premiering.permad.util;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static byte[] loadFileData(String filePath) throws Exception {
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        char[] chars = new char[(int) file.length()];
        fileReader.read(chars);
        byte[] mapData = new String(chars).getBytes(StandardCharsets.UTF_8);
        return mapData;
    }
}
