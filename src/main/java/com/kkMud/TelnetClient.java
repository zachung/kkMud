package main.java.com.kkMud;

import java.io.IOException;

import main.java.com.kkMud.theme.TextStyle;

public class TelnetClient extends AbstractClient {

  private TerminalPrinter printer;

  public TelnetClient(TerminalPrinter printer) {
    this.printer = printer;
  }
  
  protected void handleBig5String(String msg, TextStyle textStyle) {
    printer.onReceive(msg, textStyle);
  }

  @Override
  protected void connectionException(Exception exception) {
    try {
      closeConnection();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    printer.connectionException(exception);
  }

}
