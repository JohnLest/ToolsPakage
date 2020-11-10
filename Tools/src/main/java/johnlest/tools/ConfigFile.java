package johnlest.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFile {
    public static Object config(String key, String filename = "app.conf") {
        Properties prop = new Properties();
        String filePath = String.format("resources/%s", filename);
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return prop.getProperty(key); 
    }
}
