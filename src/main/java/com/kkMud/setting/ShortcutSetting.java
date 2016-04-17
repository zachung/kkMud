package main.java.com.kkMud.setting;

public class ShortcutSetting extends Setting {
  
  @Override
  protected String getFileName() {
    return "function_key";
  }
  
  public String getFunction(String key) {
    String cmd = super.getProperty(key);
    if (cmd == null) {
      cmd = "";
    }
    return cmd;
  }
  
  public void setFunction(String key, String value) {
    super.setProperty(key, value);
  }

}
