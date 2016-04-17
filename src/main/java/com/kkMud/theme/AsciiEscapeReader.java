package main.java.com.kkMud.theme;

public class AsciiEscapeReader {

  private static TerminalColor parseColor(int i) {
    TerminalColor color = null;
    switch(i) {
      case 48:
        color = TerminalColor.BLACK;
        break;
      case 49:
        color = TerminalColor.RED;
        break;
      case 50:
        color = TerminalColor.GREEN;
        break;
      case 51:
        color = TerminalColor.BROWN;
        break;
      case 52:
        color = TerminalColor.BLUE;
        break;
      case 53:
        color = TerminalColor.MAGENTA;
        break;
      case 54:
        color = TerminalColor.CYAN;
        break;
      case 55:
        color = TerminalColor.GREEN; // orign: white
        break;
      default:
        color = TerminalColor.GREEN;
    }
    return color;
  }

  public static TextStyle parse(String escStr) {
    TextStyle ts = new TextStyle();
    
    byte[] bytes = escStr.substring(1, escStr.length()-1).getBytes();
    
    for (int i = 0, l = bytes.length; i < l; i++) {
      int b = toUnsignedInt(bytes[i]);
      if (b == 49)
        ts.setIsBold(false);
      if (b == 51) {
        // set color
        ts.setColor(parseColor(toUnsignedInt(bytes[++i])));
      }
      if (b == 52) {
        ts.setIsUnderlined(true);
      }
    }
    
    return ts;
  }
  
  private static int toUnsignedInt(byte b) {
    return b & 0xff;
  }
}
