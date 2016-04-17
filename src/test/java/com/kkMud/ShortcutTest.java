package test.java.com.kkMud;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Ignore;
import org.junit.Test;

import main.java.com.kkMud.shortcut.ShortcutKey;

public class ShortcutTest {

  private static final ShortcutKey setting = new ShortcutKey();
  
  @Test
  public void colorReader() throws UnsupportedEncodingException {
    // zach 122 97 99 104
    parse("紅");
    parse("zach");
  }
  
  private void parse(String msg) {
    byte[] bytes;
    try {
      // big5
      bytes = msg.getBytes("big5");
      int[] ints = new int[bytes.length];
      for (int i = 0, l = bytes.length; i < l; i++) {
        ints[i] = bytes[i] & 0xff;
        System.out.println(ints[i]);
      }
      // normal
      bytes = msg.getBytes();
      for (byte b : bytes) {
        System.out.println(b);
      }
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Ignore
  @Test
  public void saveAndLoad() {
    String command = setting.getCommand(2, KeyEvent.VK_L);
    String[] commands = command.split("\\;");
    for (String c : commands) {
      System.out.println("1");
      System.out.println(c);
    }
  }

  @Ignore
  @Test
  public void timer() {
    Timer t = new Timer();
    t.schedule(new TimerTask() {

      @Override
      public void run() {
        System.out.println(new Date());
      }
    }, 0, 1000);
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
