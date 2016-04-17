package main.java.com.kkMud;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.java.com.kkMud.theme.InputTextStyle;
import main.java.com.kkMud.theme.TextStyle;

public class KkFrame extends JFrame implements TerminalPrinter {

  private static final long serialVersionUID = 1L;

  private JTextPane textArea;
  private JScrollPane scrollPane;
  private CommandTextField message;
  private JButton connect;
  private JButton send;
  private JPanel bottomPanel;

  private TelnetClient telnet;
  private TextStyle inputTextStyle = new InputTextStyle();

  private int port;
  private String host;

  public KkFrame(String host, int port) {
    this.host = host;
    this.port = port;
    // initMenu();
    initView();

    telnet = new TelnetClient(this);
    // actions
    connect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        connectOrDisconnect();
      }
    });
    message.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (telnet.isConnected()) {
          sendToServer();
        }
      }
    });

    send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendToServer();
      }
    });
  }

  private void initMenu() {
    // Create an instance of the test application
    MenuBar menubar = new MenuBar();
    menubar.setVisible(true);
    setJMenuBar(menubar);
  }

  private void initView() {
    textArea = new JTextPane();// JTextArea(40, 80);
    scrollPane = new JScrollPane(textArea);
    message = new CommandTextField(this);
    connect = new JButton("Connect");
    send = new JButton("Send");
    bottomPanel = new JPanel();

    // frame
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setDisconnectedState();
    textArea.setEditable(false);
    textArea.setFont(new Font("Courier New", Font.PLAIN, 18));
    // auto scroll to bottom with issue, so commented
    // DefaultCaret caret = (DefaultCaret) textArea.getCaret();
    // caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    textArea.setBounds(0, 0, 200, 200);
    textArea.setPreferredSize(new Dimension(800, 600));
    textArea.setBackground(Color.BLACK);
    textArea.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
      }
    });

    // add components
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    bottomPanel.add(message);
    bottomPanel.add(connect);
    bottomPanel.add(send);
    add(bottomPanel, BorderLayout.SOUTH);
    pack();
    setVisible(true);
    message.requestFocusInWindow();
  }

  public void connectOrDisconnect() {
    try {
      if (!telnet.isConnected()) {
        telnet.setHost(host);
        telnet.setPort(port);
        telnet.openConnection();
        textArea.setText("");
        message.setText("");
        setConnectedState();
      } else {
        telnet.closeConnection();
        setDisconnectedState();
      }
    } catch (IOException e1) {
      addMsg("Error connecting to host" + "\n");
    }

  }

  public void sendMessage(String msg) {
    try {
      telnet.sendToServer(msg);
      addMsg(msg + "\n");
    } catch (IOException e1) {
      JOptionPane.showMessageDialog(this, "send to server failed");
    }
  }

  private void sendToServer() {
    sendMessage(message.getText());
    message.selectAll();
  }

  private void addMsg(String msg) {
    onReceive(msg, inputTextStyle);
  }

  private void setDisconnectedState() {
    connect.setText("Connect");
    send.setEnabled(false);
  }

  private void setConnectedState() {
    connect.setText("Disconnect");
    send.setEnabled(true);
  }

  public void connectionException(Exception exception) {
    JOptionPane.showMessageDialog(this, "Host terminated connection");
    setDisconnectedState();
    connect.setText("Connect");
  }

  @Override
  public void onReceive(String msg, TextStyle textStyle) {
    StyledDocument doc = textArea.getStyledDocument();

    Style style = textArea.addStyle("I'm Style", null);
    StyleConstants.setForeground(style, textStyle.getColor().getColor());
    StyleConstants.setBold(style, textStyle.getIsBold());
    StyleConstants.setItalic(style, textStyle.getIsItalic());
    StyleConstants.setUnderline(style, textStyle.getIsUnderlined());
    try {
      doc.insertString(doc.getLength(), msg, style);
    } catch (BadLocationException e) {
    }
    // auto scroll to bottom
    textArea.setCaretPosition(textArea.getDocument().getLength());
    trunkTextArea(textArea);
  }

  final int SCROLL_BUFFER_SIZE = 1000;

  public void trunkTextArea(JTextPane txtWin) {
    if (txtWin.getText().split("\n").length < SCROLL_BUFFER_SIZE)
      return;
    try {
      textArea.getDocument().remove(0, 500);
    } catch (BadLocationException e) {
      // donothing
    }
  }
}
