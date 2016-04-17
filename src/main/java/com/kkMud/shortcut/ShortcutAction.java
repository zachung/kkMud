package main.java.com.kkMud.shortcut;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ShortcutAction extends AbstractAction {
  private static final long serialVersionUID = 1L;

  private String cmd;

  public ShortcutAction(String cmd) {
    this.cmd = cmd;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    System.out.println(cmd);
  }

}
