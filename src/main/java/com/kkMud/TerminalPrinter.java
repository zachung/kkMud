package main.java.com.kkMud;

import main.java.com.kkMud.theme.TextStyle;

public interface TerminalPrinter {
  
  public void connectionException(Exception exception);
  
  public void onReceive(String msg, TextStyle textStyle);
  
  public void sendMessage(String msg);
  
  public void connectOrDisconnect();

}
