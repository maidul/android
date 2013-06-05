package com.ipsmarx.dialer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KrLandlineBalancedMinutesHandler extends DefaultHandler
{
  private boolean basekoreaLandLineUrl = false;
  private ParsedKrLandlineMinutesDataSet myParsedLoginDataSet = new ParsedKrLandlineMinutesDataSet();

  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this.myParsedLoginDataSet.setExtractedbasekoreaLandLineUrl(new String(paramArrayOfChar, paramInt1, paramInt2));
  }

  public void endDocument()
    throws SAXException
  {
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    this.basekoreaLandLineUrl = false;
  }

  public ParsedKrLandlineMinutesDataSet getParsedLoginData()
  {
    return this.myParsedLoginDataSet;
  }

  public void startDocument()
    throws SAXException
  {
    this.myParsedLoginDataSet = new ParsedKrLandlineMinutesDataSet();
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    this.basekoreaLandLineUrl = true;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.KrLandlineBalancedMinutesHandler
 * JD-Core Version:    0.6.2
 */