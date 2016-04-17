package test.java.com.kkMud;

import java.io.UnsupportedEncodingException;

public class StirngDecoder {

  public void main(String[] args) throws UnsupportedEncodingException {
    // 因為 java 的 byte 都是 signed int
    System.out.println(0xA1);
    System.out.println((int)0xA1);
    System.out.println((byte)(int)0xA1);
    System.out.println((byte)(int)0xA1 == 0xA1);
    print();
  }

  private void isBig5() {
    int intValue = 127;
    Byte curByte = (byte) intValue;
    boolean isBig5 = curByte <= 0x81 && curByte >= 0x7E;
    System.out.println(isBig5);
  }

  private static void print() throws UnsupportedEncodingException {
    int[] ints = {
        116,
        104,
        27,
        91,
        50,
        59,
        51,
        55,
        59,
        48,
        109,
        32,
        169,
        77,
        32,
        27,
        91,
        49,
        109,
        119,
        101,
        115,
        116,
        27,
        91,
        50,
        59,
        51,
        55,
        59,
        48
};
    byte[] bytes = new byte[ints.length];
    for (int i = 0, len = bytes.length; i < len; i++) {
      bytes[i] = (byte)ints[i];
    }
    String msg = new String(bytes, "BIG5");
    System.out.println(msg);
  }
}
