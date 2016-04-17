package main.java.com.kkMud.shortcut;

import main.java.com.kkMud.setting.ShortcutSetting;

public class ShortcutKey {

  private ShortcutSetting setting = new ShortcutSetting();
  
  public void setCommand(Integer modifiers, Integer keyCode, String command) {
    setting.setFunction(toShortcut(modifiers, keyCode), command);
  }
  
  // modifiers + keyCode -> shortcut
  private String toShortcut(Integer modifiers, Integer keyCode) {
    StringBuilder sb = new StringBuilder();
    sb.append(modifiers);
    sb.append("+");
    sb.append(keyCode);
    return sb.toString();
  }
  
  public String getCommand(Integer modifiers, Integer keyCode) {
    return setting.getFunction(toShortcut(modifiers, keyCode));
  }
  
  public String getCommand(String shortcut) {
    return setting.getFunction(shortcut);
  }
}
