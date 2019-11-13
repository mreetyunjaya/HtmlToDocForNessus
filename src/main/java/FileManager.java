import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class FileManager {

    public static byte[] readFileBinary (String name) throws java.io.FileNotFoundException, java.io.IOException {
        File file = new File(name);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }

    public static String readFile (String name) throws java.io.FileNotFoundException, java.io.IOException {
        File file = new File(name);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String str = new String(data, "UTF-8");
        return str;
    }

    public static void writeFile (String name, String html) throws java.io.IOException {
        BufferedWriter outWriter = new BufferedWriter(new FileWriter(name));
        outWriter.write(html);
        outWriter.flush();
        outWriter.close();
    }
}
