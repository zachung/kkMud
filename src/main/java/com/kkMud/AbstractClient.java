// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package main.java.com.kkMud;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import main.java.com.kkMud.theme.AsciiEscapeReader;
import main.java.com.kkMud.theme.NumberTextStyle;
import main.java.com.kkMud.theme.TerminalColor;
import main.java.com.kkMud.theme.TextStyle;

/**
 * The <code> AbstractClient </code> contains all the methods necessary to set up the client side of
 * a client-server architecture. When a client is thus connected to the server, the two programs can
 * then exchange <code> Object </code> instances.
 * <p>
 *
 * Method <code> handleMessageFromServer </code> must be defined by a concrete subclass. Several
 * other hook methods may also be overriden.
 * <p>
 *
 * Several public service methods are provided to application that use this framework.
 * <p>
 *
 * The modifications made to this class in version 2.2 are:
 * <ul>
 * <li>Method <code>sendToServer()</code> is not declared final anymore. This allows user of the
 * framework to override it, perhaps to perform some filtering before sending the message to the
 * server. However, any overriden version of this method should include a call to the original one.
 * <li>A test is made before calling the <code>handleMessageFromServer</code> method such that when
 * <code>closeConnection</code> returns, it is garanteed that no new messages will be handled.
 * </ul>
 * The modifications made to this class in version 2.31 are:
 * <ul>
 * <li>The <code>run()</code> method now calls the <code>connectionException</code> callback when an
 * object of unknown class is received from the input stream or when the message handler throw a
 * <code>RuntimeException</code>.
 * <li>The <code>connectionClosed</code> callback might be called after
 * <code>connectionException</code> if the exception causes the end of te thread.
 * <li>The <code>clientReader</code> reference is set to <code>null</code> earlier in
 * <code>run()</code> method.
 * <li>The call to <code>connectionClosed</code> has been moved from <code>closeConnection</code> to
 * <code>run</code> method to garantee that connection is really closed when this callback is
 * called.
 * </ul>
 * <p>
 *
 * Project Name: OCSF (Object Client-Server Framework)
 * <p>
 *
 * @author Dr. Robert Lagani&egrave;re
 * @author Dr. Timothy C. Lethbridge
 * @author Fran&ccedil;ois B&eacutel;langer
 * @author Paul Holden
 * @version December 2003 (2.31)
 */
public abstract class AbstractClient implements Runnable {

  // INSTANCE VARIABLES ***********************************************

  /**
   * Sockets are used in the operating system as channels of communication between two processes.
   * 
   * @see java.net.Socket
   */
  private Socket clientSocket;

  /**
   * The stream to handle data going to the server.
   */
  private DataOutputStream output;

  /**
   * The stream to handle data from the server.
   */
  private DataInputStream input;

  /**
   * The thread created to read data from the server.
   */
  private Thread clientReader;

  /**
   * Indicates if the thread is ready to stop. Needed so that the loop in the run method knows when
   * to stop waiting for incoming messages.
   */
  private boolean readyToStop = false;

  /**
   * The server's host name.
   */
  private String host;

  /**
   * The port number.
   */
  private int port;

  // CONSTRUCTORS *****************************************************

  /**
   * Constructs the client.
   *
   * @param host the server's host name.
   * @param port the port number.
   */
  public AbstractClient(String host, int port) {
    // Initialize variables
    this.host = host;
    this.port = port;
  }

  public AbstractClient() {}

  // INSTANCE METHODS *************************************************

  /**
   * Opens the connection with the server. If the connection is already opened, this call has no
   * effect.
   *
   * @exception IOException if an I/O error occurs when opening.
   */
  final public void openConnection() throws IOException {
    // Do not do anything if the connection is already open
    if (isConnected())
      return;

    // Create the sockets and the data streams
    try {
      clientSocket = new Socket(host, port);
      output = new DataOutputStream(clientSocket.getOutputStream());
      // input =
      // new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "Big5_HKSCS"));
      input = new DataInputStream(clientSocket.getInputStream());
    } catch (IOException ex)
    // All three of the above must be closed when there is a failure
    // to create any of them
    {
      try {
        closeAll();
      } catch (Exception exc) {
      }

      throw ex; // Rethrow the exception.
    }

    clientReader = new Thread(this); // Create the data reader thread
    readyToStop = false;
    clientReader.start(); // Start the thread
  }

  /**
   * Sends an object to the server. This is the only way that methods should communicate with the
   * server. This method can be overriden, but if so it should still perform the general function of
   * sending to server, by calling the <code>super.sendToServer()</code> method perhaps after some
   * kind of filtering is done.
   *
   * @param msg The message to be sent.
   * @exception IOException if an I/O error occurs when sending
   */
  public void sendToServer(String msg) throws IOException {
    if (clientSocket == null || output == null) {
      throw new SocketException("socket does not exist");
    }

    // messageQueue.offer(msg);
    // sendFromMessageQueue();
    
    // 發送中文
    byte[] bytes = msg.getBytes("big5");
    int[] ints = new int[bytes.length];
    for (int i = 0, l = bytes.length; i < l; i++) {
      ints[i] = bytes[i] & 0xff;
      output.write(ints[i]);
    }
    output.writeBytes("\n");
  }

  public void sendFromMessageQueue() throws IOException {
    // FIXME: I don't know why it's 1200ms, just try and error.
    Date canSendDate = new Date(lastReceiveMsgDate.getTime() + 2000);
    if (canSendDate.before(new Date())) {
      String msg = messageQueue.poll();
      if (msg == null)
        return;

      output.writeBytes(msg + "\n");
    }
  }

  /**
   * Closes the connection to the server.
   *
   * @exception IOException if an I/O error occurs when closing.
   */
  final public void closeConnection() throws IOException {

    readyToStop = true;
    closeAll();
  }

  // ACCESSING METHODS ------------------------------------------------

  /**
   * @return true if the client is connnected.
   */
  final public boolean isConnected() {
    return clientReader != null && clientReader.isAlive();
  }

  /**
   * @return the port number.
   */
  final public int getPort() {
    return port;
  }

  /**
   * Sets the server port number for the next connection. The change in port only takes effect at
   * the time of the next call to openConnection().
   *
   * @param port the port number.
   */
  final public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return the host name.
   */
  final public String getHost() {
    return host;
  }

  /**
   * Sets the server host for the next connection. The change in host only takes effect at the time
   * of the next call to openConnection().
   *
   * @param host the host name.
   */
  final public void setHost(String host) {
    this.host = host;
  }

  /**
   * returns the client's description.
   *
   * @return the client's Inet address.
   */
  final public InetAddress getInetAddress() {
    return clientSocket.getInetAddress();
  }

  // RUN METHOD -------------------------------------------------------

  /**
   * Waits for messages from the server. When each arrives, a call is made to
   * <code>handleMessageFromServer()</code>. Not to be explicitly called.
   */
  final public void run() {
    connectionEstablished();

    // The message from the server
    int msg;

    // Loop waiting for data

    try {
      // messageTimer.schedule(new TimerTask() {
      // @Override
      // public void run() {
      // try {
      // sendFromMessageQueue();
      // } catch (IOException e) {}
      // }
      // }, 0, 100);
      while (!readyToStop) {
        // Get data from Server and send it to the handler
        // The thread waits indefinitely at the following
        // statement until something is received from the server

        try { // added in version 2.31

          // String cur = input.readLine();
          // handleBig5String(cur + "\n");
          readByte();

        } catch (RuntimeException ex) { // thrown by handleMessageFromServer

          connectionException(ex);
        }
      }
    } catch (Exception exception) {
      if (!readyToStop) {
        try {
          closeAll();
        } catch (Exception ex) {
        }

        clientReader = null;
        connectionException(exception);
      }
    } finally {

      clientReader = null;
      connectionClosed(); // moved here in version 2.31
    }
  }

  private Boolean isInEscape = false;
  private Boolean isInUnknow = false;
  private Boolean isInUnknow2 = false;

  /** esc command */
  private String escString;
  /** cur color */
  private TextStyle defaultStyle = new TextStyle();
  private TextStyle curStyle = new TextStyle();
  private TextStyle numberStyle = new NumberTextStyle();

  private Date lastReceiveMsgDate = new Date();
  private Queue<String> messageQueue = new LinkedList<String>();
  private Timer messageTimer = new Timer();

  private void readByte() throws IOException {

    ArrayList<Byte> inputLine = new ArrayList<Byte>();
    int curByte;
    int byteInx = 0;
    while (true) {
      curByte = input.readUnsignedByte();
      inputLine.add((byte) curByte);
      if (byteInx > 0)
        break;
      // escape 27 -> 109
      if (isESC(curByte)) {
        isInEscape = true;
        escString = "";
        return;
      }
      if (isInEscape) {
        escString += (char) curByte;
        if (curByte == 109) {
          isInEscape = false;
          curStyle = AsciiEscapeReader.parse(escString);
        }
        return;
      }
      // unknow 255 -> 31
      if (curByte == 255)
        isInUnknow = true;
      if (isInUnknow) {
        if (curByte == 31)
          isInUnknow = false;
        return;
      }
      // unknow2 249 -> 216
//      if (curByte == 249)
//        isInUnknow2 = true;
//      if (isInUnknow2) {
//        if (curByte == 216)
//          isInUnknow2 = false;
//        return;
//      }
//    if (!isBig5HighByte(curByte))
//    return;
      if (isControl(curByte))
        break;
      if (isPrintable(curByte))
        break;
      byteInx++;
    }


    byte[] bytes = new byte[inputLine.size()];
    for (int i = 0, len = bytes.length; i < len; i++) {
      bytes[i] = (byte) inputLine.get(i);
    }

    // Concrete subclasses do what they want with the
    // msg by implementing the following method
    if (!readyToStop) { // Added in version 2.2
      String msg = new String(bytes, "x-MS950-HKSCS");
      // picked from all java encoding set : Charset.availableCharsets().keySet()
      // x-Big5-HKSCS-2001
      // x-MS950-HKSCS
      // x-MS950-HKSCS-XP
      // x-windows-950
      if (curByte >= 48 && curByte <= 57) {
        handleBig5String(msg, numberStyle);
      } else {
        handleBig5String(msg, curStyle);
      }
    }
    lastReceiveMsgDate = new Date();
  }

  private void changeCurColor(String escString) {
    if ("[2;37;0m".equals(escString)) {
      setStyleToDefault();
    } else if ("[0;30m".equals(escString) || "[30m".equals(escString)) {
      curStyle.setColor(TerminalColor.BLACK);
    } else if ("[0;31m".equals(escString) || "[31m".equals(escString)) {
      curStyle.setColor(TerminalColor.RED);
    } else if ("[0;32m".equals(escString) || "[32m".equals(escString)) {
      curStyle.setColor(TerminalColor.GREEN);
    } else if ("[0;33m".equals(escString) || "[33m".equals(escString)) {
      curStyle.setColor(TerminalColor.BROWN);
    } else if ("[0;34m".equals(escString) || "[34m".equals(escString)) {
      curStyle.setColor(TerminalColor.BLUE);
    } else if ("[0;35m".equals(escString) || "[35m".equals(escString)) {
      curStyle.setColor(TerminalColor.MAGENTA);
    } else if ("[0;36m".equals(escString) || "[36m".equals(escString)) {
      curStyle.setColor(TerminalColor.CYAN);
    } else if ("[0;37m".equals(escString) || "[37m".equals(escString)) {
      curStyle.setColor(TerminalColor.GRAY);
    } else if ("[1;30m".equals(escString)) {
      curStyle.setColor(TerminalColor.DARK_GRAY);
    } else if ("[1;31m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_RED);
    } else if ("[1;32m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_GREEN);
    } else if ("[1;33m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_YELLOW);
    } else if ("[1;34m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_BLUE);
    } else if ("[1;35m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_MAGENTA);
    } else if ("[1;36m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_CYAN);
    } else if ("[1;37m".equals(escString)) {
      curStyle.setColor(TerminalColor.WHITE);
    } else if ("[1m".equals(escString)) {
      curStyle.setColor(TerminalColor.LIGHT_GREEN);
      curStyle.setIsBold(true);
    } else if ("[2m".equals(escString)) {
      curStyle.setIsFaint(true);
    } else if ("[3m".equals(escString)) {
      curStyle.setIsItalic(true);
    } else if ("[4m".equals(escString)) {
      curStyle.setIsUnderlined(true);
    } else if ("[30m".equals(escString)) {
      curStyle.setColor(TerminalColor.BLACK);
      curStyle.setIsBold(false);
    } else if ("[0m".equals(escString)) {
      System.out.println(escString);
      setStyleToDefault();
    } else {
      /**
       * TODO: [0m [0;39m [1;33;5m [0;38m [1;32;5m
       */
      System.out.println(escString);
      setStyleToDefault();
    }
  }

  private void setStyleToDefault() {
    curStyle.setColor(defaultStyle.getColor());
    curStyle.setIsBold(defaultStyle.getIsBold());
    curStyle.setIsFaint(defaultStyle.getIsFaint());
    curStyle.setIsItalic(defaultStyle.getIsItalic());
    curStyle.setIsUnderlined(defaultStyle.getIsUnderlined());
  }

  private boolean isESC(Integer curByte) {
    return curByte == 0x1B;
  }

  /**
   * 在 Big-5 碼中, 每個中文字佔兩個 bytes, 第一個 byte 可以是 A1-F9 當中的任何一個; 第二個 byte 可以是 40-7E 或 A1-FE 當中的任何一個
   */
  private boolean isBig5HighByte(Integer curByte) {
    return curByte >= 0xA1 && curByte <= 0xF9;
  }

  /** 可顯示字元 */
  private boolean isPrintable(Integer curByte) {
    return curByte >= 32 && curByte <= 126;
  }

  /** 控制字元 */
  private boolean isControl(Integer curByte) {
    return curByte >= 0 && curByte <= 31 || curByte == 127;
  }

  // METHODS DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

  /**
   * Hook method called after the connection has been closed. The default implementation does
   * nothing. The method may be overriden by subclasses to perform special processing such as
   * cleaning up and terminating, or attempting to reconnect.
   */
  protected void connectionClosed() {}

  /**
   * Hook method called each time an exception is thrown by the client's thread that is reading
   * messages from the server. The method may be overridden by subclasses. Most exceptions will
   * cause the end of the reading thread except for
   * <code>ClassNotFoundException<\code>s received when an object of
   * unknown class is received and for the <code>RuntimeException</code>s that can be thrown by the
   * message handling method implemented by the user.
   *
   * @param exception the exception raised.
   */
  protected void connectionException(Exception exception) {}

  /**
   * Hook method called after a connection has been established. The default implementation does
   * nothing. It may be overridden by subclasses to do anything they wish.
   */
  protected void connectionEstablished() {}


  // METHODS TO BE USED FROM WITHIN THE FRAMEWORK ONLY ----------------

  /**
   * Closes all aspects of the connection to the server.
   *
   * @exception IOException if an I/O error occurs when closing.
   */
  final private void closeAll() throws IOException {
    // This method is final since version 2.2

    try {
      // Close the socket
      if (clientSocket != null)
        clientSocket.close();

      // Close the output stream
      if (output != null)
        output.close();

      // Close the input stream
      if (input != null)
        input.close();
    } finally {
      // Set the streams and the sockets to NULL no matter what
      // Doing so allows, but does not require, any finalizers
      // of these objects to reclaim system resources if and
      // when they are garbage collected.
      output = null;
      input = null;
      clientSocket = null;
    }
  }

  protected abstract void handleBig5String(String msg, TextStyle curStyle);
}
// end of AbstractClient class
