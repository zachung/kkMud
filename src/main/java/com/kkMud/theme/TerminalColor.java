package main.java.com.kkMud.theme;

import java.awt.Color;

public enum TerminalColor {
  BLACK(0,0,0),
  RED(128,0,0),
  GREEN(0,128,0),
  BROWN(128,128,0),
  BLUE(0,0,128),
  MAGENTA(128,0,128),
  CYAN(0,128,128),
  GRAY(192,192,192),

  DARK_GRAY(128, 128, 128),
  LIGHT_RED(255, 0, 0),
  LIGHT_GREEN(0, 255, 0),
  LIGHT_YELLOW(255, 255, 0),
  LIGHT_BLUE(0, 0, 255),
  LIGHT_MAGENTA(255, 0, 255),
  LIGHT_CYAN(0, 255, 255),
  WHITE(255, 255, 255),
  ;

  private final Color color;

  private TerminalColor(Color color) {
    this.color = color;
  }

  private TerminalColor(Integer r, Integer g, Integer b) {
    this.color = new Color(r, g, b);
  }

  public Color getColor() {
    return color;
  }
}
