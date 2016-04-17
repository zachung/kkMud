package main.java.com.kkMud.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 可載入預設 properties，並於有修改時存到新檔
 * 
 * @author hungchihan
 *
 */
abstract public class Setting {

  private static final Properties prop = new Properties();

  public Setting() {
    readConfigFile();
  }

  protected abstract String getFileName();

  private String getFileNameWithExtension() {
    return getFileName() + ".properties";
  }

  public void setProperty(String key, String value) {
    OutputStream output = null;
    try {
      output = new FileOutputStream(getFileNameWithExtension());
      prop.setProperty(key, value);
      prop.store(output, null);
    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void readConfigFile() {
    InputStream input = null;
    try {
      File f = new File(getFileNameWithExtension());
      input = new FileInputStream(f);
    } catch (IOException ex) {
      input = null;
    }
    try {
      if (null == input) {
        // load from default setting
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         input = loader.getResourceAsStream(getFileNameWithExtension());
//        input = getClass().getResourceAsStream(getFileNameWithExtension());
      }
      prop.load(input);
    } catch (IOException e) {
      // do nothing
      e.printStackTrace();
    } finally {
      if (null != input) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public String getProperty(String key) {
    return prop.getProperty(key);
  }
}
