package main.java.com.kkMud;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import main.java.com.kkMud.frame.ShortcutSettingFrame;
import main.java.com.kkMud.shortcut.ShortcutKey;

/**
 * up & down review cmd history
 * 
 * @author hungchihan
 *
 */
public class CommandTextField extends JTextField {
  private static final long serialVersionUID = 1L;

  private final static LinkedList<String> historyCmds = new LinkedList<String>();
  private static final ShortcutKey setting = new ShortcutKey();

  protected static final Integer MAX_HISTORY_COUNT = 100;
  protected static final Integer INIT_LOC = 0;
  private static Integer curInx = INIT_LOC;

  private CommandTextField ctf;

  private TerminalPrinter printer;

  public CommandTextField(TerminalPrinter printer) {
    super("", 15);
    ctf = this;
    registerUp();
    registerDown();
    registerEnter();
    registerCtrl();
    registerEsc();
    this.printer = printer;
  }

  private void registerCtrl() {
    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {}

      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int modifiers = e.getModifiers();
        String keyName = KeyEvent.getKeyText(keyCode);

        // 系統熱鍵，不可被更改
        Boolean isCtrlHolded = e.isControlDown();
        if (isCtrlHolded) {
          if ("F10".equals(keyName)) {
            ShortcutSettingFrame shortcutSetting = new ShortcutSettingFrame(keyName);
            shortcutSetting.setVisible(true);
            return;
          }
        }
        // Shift + Meta + L = 連線 or 斷線
        if (modifiers == 5 && keyCode == KeyEvent.VK_L) {
          printer.connectOrDisconnect();
        }
        // 自訂指令熱鍵
        String command = setting.getCommand(modifiers, keyCode);
        if (!command.equals("")) {
          String[] commands = command.split("\\;");
          for (String c : commands) {
            printer.sendMessage(c);
          }
          return;
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {}
    });
  }

  // Up
  private void registerUp() {
    this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "UP");
    this.getActionMap().put("UP", new AbstractAction("UP") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        ctf.setText(getOlder());
        ctf.selectAll();
      }

    });
  }

  // Down
  private void registerDown() {
    this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
    this.getActionMap().put("DOWN", new AbstractAction("DOWN") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        ctf.setText(getNewer());
        ctf.selectAll();
      }

    });
  }

  // esc
  private void registerEsc() {
    this.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
    this.getActionMap().put("ESCAPE", new AbstractAction("ESCAPE") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        ctf.setText("");
      }

    });
  }

  // Enter
  private void registerEnter() {
    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (historyCmds.size() > MAX_HISTORY_COUNT)
          historyCmds.removeLast();
        historyCmds.addFirst(ctf.getText());
        curInx = INIT_LOC;
      }
    });
  }

  private String getNewer() {
    try {
      return historyCmds.get(--curInx);
    } catch (IndexOutOfBoundsException e) {
      return historyCmds.get(curInx = 0);
    }
  }

  private String getOlder() {
    try {
      return historyCmds.get(++curInx);
    } catch (IndexOutOfBoundsException e) {
      return historyCmds.get(--curInx);
    }
  }

  public void addHistory(String cmd) {
    historyCmds.add(cmd);
  }
}
