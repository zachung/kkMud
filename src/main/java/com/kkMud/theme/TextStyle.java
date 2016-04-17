package main.java.com.kkMud.theme;

public class TextStyle {

  private TerminalColor color = TerminalColor.GREEN;

  private Boolean isBold = false;
  
  private Boolean isItalic = false;
  
  private Boolean isUnderlined = false;
  
  private Boolean isFaint = false;

  public TerminalColor getColor() {
    return color;
  }

  public void setColor(TerminalColor color) {
    this.color = color;
  }

  public Boolean getIsBold() {
    return isBold;
  }

  public void setIsBold(Boolean isBold) {
    this.isBold = isBold;
  }

  public Boolean getIsItalic() {
    return isItalic;
  }

  public void setIsItalic(Boolean isItalic) {
    this.isItalic = isItalic;
  }

  public Boolean getIsUnderlined() {
    return isUnderlined;
  }

  public void setIsUnderlined(Boolean isUnderlined) {
    this.isUnderlined = isUnderlined;
  }
  
  public Boolean getIsFaint() {
    return isFaint;
  }

  public void setIsFaint(Boolean isFaint) {
    this.isFaint = isFaint;
  }
}
