package main.java.com.kkMud.frame;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.java.com.kkMud.shortcut.ShortcutKey;

public class ShortcutSettingFrame extends JDialog {
  private static final long serialVersionUID = 1L;

  private static final String STEP1 = "choise shortcut";
  private static final String STEP2 = "input your command";
  private String curStep = STEP1;

  private JLabel label;
  private JDialog dialog;

  public ShortcutSettingFrame(String keyName) {
    super();
    initView();
    startSetting();
    dialog = this;
  }

  private void initView() {
    setSize(600, 200);
    setModal(true);
    setAlwaysOnTop(true);
    setModalityType(ModalityType.APPLICATION_MODAL);
    // 是否隱藏關閉按鈕
    // setUndecorated(true);
    // getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    label = new JLabel("按下你想要設定的熱鍵。", SwingConstants.CENTER);
    setResizable(false);
    add(label);
  }

  private void startSetting() {
    final ShortcutSettingFrame dialog = this;
    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {}

      @Override
      public void keyPressed(KeyEvent e) {
        final int keyCode = e.getKeyCode();
        final int modifiers = e.getModifiers();
        String modifierName = KeyEvent.getKeyModifiersText(modifiers);

        // 停止設定
        if (keyCode == KeyEvent.VK_ESCAPE) {
          dialog.dispose();
        }

        // 至少要有一個 modifier
        if ("".equals(modifierName))
          return;
        // 不能是 action
        if (e.isActionKey())
          return;

        // 必須要是一般 key
        if (!((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90)))
          return;
        if (!curStep.equals(STEP1))
          return;
        curStep = STEP2;

        /**
         * action keys: F1, HOME modifier keys: Ctrl, Alt, Shift
         */
        StringBuilder sb = new StringBuilder();
        sb.append("你想要在按下 ");
        sb.append(modifierName);
        sb.append("+");
        sb.append(KeyEvent.getKeyText(keyCode));
        sb.append(" 時做什麼？\n");
        sb.append("輸入完按 Enter");
        label.setText(sb.toString());

        JPanel bottomPanel = new JPanel();
        ShortcutSettingTextField tf = new ShortcutSettingTextField(modifiers, keyCode);

        bottomPanel.add(tf);
        add(bottomPanel, BorderLayout.SOUTH);
        tf.requestFocus();
      }

      @Override
      public void keyReleased(KeyEvent e) {}
    });
  }

  public class ShortcutSettingTextField extends JTextField {
    private static final long serialVersionUID = 1L;

    private final Integer modifiers;
    private final Integer keyCode;

    protected ShortcutKey sk = new ShortcutKey();

    public ShortcutSettingTextField(Integer modifiers, Integer keyCode) {
      super("執行的指令", 15);
      this.modifiers = modifiers;
      this.keyCode = keyCode;
      initView();
      // load preset shortcut
      String command = sk.getCommand(modifiers, keyCode);
      if (!command.equals(""))
        setText(command);
    }

    public void initView() {
      addKeyListener(new KeyListener() {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
          // 停止設定
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dialog.dispose();
          }
          // submit
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sk.setCommand(modifiers, keyCode, ((JTextField) e.getSource()).getText());
            dialog.dispose();
          }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
      });
      selectAll();
    }
  }

}
