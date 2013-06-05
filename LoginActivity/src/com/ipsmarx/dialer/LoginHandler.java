package com.ipsmarx.dialer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LoginHandler extends DefaultHandler
{
  private boolean AccessNumber = false;
  private boolean AllowAccessNumber;
  private boolean AllowG729 = false;
  private boolean AllowVoiceMail;
  private boolean BalanceShow;
  private boolean CallLicenseType;
  private boolean Culture;
  private boolean RateperminShow;
  private boolean ServiceType = false;
  private boolean TransferOptionsFlag;
  private boolean balance = false;
  private boolean companyurl = false;
  private boolean firstname = false;
  private boolean lastname = false;
  private ParsedLoginDataSet myParsedLoginDataSet = new ParsedLoginDataSet();
  private boolean proxyipserver = false;
  private boolean result = false;
  private boolean sippassword = false;
  private boolean sipusername = false;
  private boolean softswitchipserver = false;

  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.balance)
      this.myParsedLoginDataSet.setExtractedBalance(new String(paramArrayOfChar, paramInt1, paramInt2));
    do
    {
      return;
      if (this.firstname)
      {
        this.myParsedLoginDataSet.setExtractedFirstName(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.lastname)
      {
        this.myParsedLoginDataSet.setLastName(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.proxyipserver)
      {
        this.myParsedLoginDataSet.setProxyIpServer(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.result)
      {
        this.myParsedLoginDataSet.setResult(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.sipusername)
      {
        this.myParsedLoginDataSet.setSipUserName(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.softswitchipserver)
      {
        this.myParsedLoginDataSet.setSoftSwitchIpServer(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.sippassword)
      {
        this.myParsedLoginDataSet.setSipPassword(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.companyurl)
      {
        this.myParsedLoginDataSet.setCompanyUrl(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.ServiceType)
      {
        this.myParsedLoginDataSet.setServiceType(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.AccessNumber)
      {
        this.myParsedLoginDataSet.setAccessNumber(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.AllowAccessNumber)
      {
        this.myParsedLoginDataSet.setAllowAccessNumber(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.Culture)
      {
        this.myParsedLoginDataSet.setCulture(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.BalanceShow)
      {
        this.myParsedLoginDataSet.setBalanceShow(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.RateperminShow)
      {
        this.myParsedLoginDataSet.setRateperminShow(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.AllowVoiceMail)
      {
        this.myParsedLoginDataSet.setAllowVoiceMail(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.TransferOptionsFlag)
      {
        this.myParsedLoginDataSet.setTransferOptionsFlag(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
      if (this.CallLicenseType)
      {
        this.myParsedLoginDataSet.setCallLicenseType(new String(paramArrayOfChar, paramInt1, paramInt2));
        return;
      }
    }
    while (!this.AllowG729);
    this.myParsedLoginDataSet.setAllowG729(new String(paramArrayOfChar, paramInt1, paramInt2));
  }

  public void endDocument()
    throws SAXException
  {
  }

  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (paramString2.equals("Balance"))
      this.balance = false;
    do
    {
      return;
      if (paramString2.equals("AllowG729"))
      {
        this.AllowG729 = false;
        return;
      }
      if (paramString2.equals("FirstName"))
      {
        this.firstname = false;
        return;
      }
      if (paramString2.equals("LastName"))
      {
        this.lastname = false;
        return;
      }
      if (paramString2.equals("ProxyIPServer"))
      {
        this.proxyipserver = false;
        return;
      }
      if (paramString2.equals("Result"))
      {
        this.result = false;
        return;
      }
      if (paramString2.equals("SIPPassword"))
      {
        this.sippassword = false;
        return;
      }
      if (paramString2.equals("SIPUsername"))
      {
        this.sipusername = false;
        return;
      }
      if (paramString2.equals("SoftswitchIPServer"))
      {
        this.softswitchipserver = false;
        return;
      }
      if (paramString2.equals("CompanyUrl"))
      {
        this.companyurl = false;
        return;
      }
      if (paramString2.equals("ServiceType"))
      {
        this.ServiceType = false;
        return;
      }
      if (paramString2.equals("AccessNumber"))
      {
        this.AccessNumber = false;
        return;
      }
      if (paramString2.equals("AllowAccessNumber"))
      {
        this.AllowAccessNumber = false;
        return;
      }
      if (paramString2.equals("Culture"))
      {
        this.Culture = false;
        return;
      }
      if (paramString2.equals("BalanceShow"))
      {
        this.BalanceShow = false;
        return;
      }
      if (paramString2.equals("RateperminShow"))
      {
        this.RateperminShow = false;
        return;
      }
      if (paramString2.equals("AllowVoiceMail"))
      {
        this.AllowVoiceMail = false;
        return;
      }
      if (paramString2.equals("TransferOptionsFlag"))
      {
        this.TransferOptionsFlag = false;
        return;
      }
    }
    while (!paramString2.equals("CallLicenseType"));
    this.CallLicenseType = false;
  }

  public ParsedLoginDataSet getParsedLoginData()
  {
    return this.myParsedLoginDataSet;
  }

  public void startDocument()
    throws SAXException
  {
    this.myParsedLoginDataSet = new ParsedLoginDataSet();
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (paramString2.equals("Balance"))
      this.balance = true;
    do
    {
      return;
      if (paramString2.equals("FirstName"))
      {
        this.firstname = true;
        return;
      }
      if (paramString2.equals("LastName"))
      {
        this.lastname = true;
        return;
      }
      if (paramString2.equals("ProxyIPServer"))
      {
        this.proxyipserver = true;
        return;
      }
      if (paramString2.equals("Result"))
      {
        this.result = true;
        return;
      }
      if (paramString2.equals("SIPPassword"))
      {
        this.sippassword = true;
        return;
      }
      if (paramString2.equals("SIPUsername"))
      {
        this.sipusername = true;
        return;
      }
      if (paramString2.equals("SoftswitchIPServer"))
      {
        this.softswitchipserver = true;
        return;
      }
      if (paramString2.equals("CompanyUrl"))
      {
        this.companyurl = true;
        return;
      }
      if (paramString2.equals("ServiceType"))
      {
        this.ServiceType = true;
        return;
      }
      if (paramString2.equals("AccessNumber"))
      {
        this.AccessNumber = true;
        return;
      }
      if (paramString2.equals("AllowAccessNumber"))
      {
        this.AllowAccessNumber = true;
        return;
      }
      if (paramString2.equals("Culture"))
      {
        this.Culture = true;
        return;
      }
      if (paramString2.equals("BalanceShow"))
      {
        this.BalanceShow = true;
        return;
      }
      if (paramString2.equals("RateperminShow"))
      {
        this.RateperminShow = true;
        return;
      }
      if (paramString2.equals("AllowVoiceMail"))
      {
        this.AllowVoiceMail = true;
        return;
      }
      if (paramString2.equals("TransferOptionsFlag"))
      {
        this.TransferOptionsFlag = true;
        return;
      }
      if (paramString2.equals("CallLicenseType"))
      {
        this.CallLicenseType = true;
        return;
      }
    }
    while (!paramString2.equals("AllowG729"));
    this.AllowG729 = true;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.LoginHandler
 * JD-Core Version:    0.6.2
 */