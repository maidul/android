package com.ipsmarx.dialer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KrMobilelineBalancedMinutesHandler extends DefaultHandler
{
  private boolean basekoreamobileLineUrl = false;
  private ParsedKrMobilelineMinutesDataSet myParsedLoginDataSet = new ParsedKrMobilelineMinutesDataSet();

  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this.myParsedLoginDataSet.setExtractedbasekoreamobileLineUrl(new String(paramArrayOfChar, paramInt1, paramInt2));
  }

  public void endDocument()
    throws SAXException
  {
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    this.basekoreamobileLineUrl = false;
  }

  public ParsedKrMobilelineMinutesDataSet getParsedLoginData()
  {
    return this.myParsedLoginDataSet;
  }

  public void startDocument()
    throws SAXException
  {
    this.myParsedLoginDataSet = new ParsedKrMobilelineMinutesDataSet();
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    this.basekoreamobileLineUrl = true;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.KrMobilelineBalancedMinutesHandler
 * JD-Core Version:    0.6.2
 */