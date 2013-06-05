package com.ipsmarx.dialer.ProductFactory;

import android.content.Context;

public class SandalsPhone extends SoftPhone
{
  String IP = "72.252.252.202";
  Context mContext;

  public boolean dataAllowed()
  {
    return false;
  }

  public String getLoginType()
  {
    return "MAC";
  }

  public boolean hasLoginScreen()
  {
    return false;
  }

  public boolean hasSignOutBtn()
  {
    return false;
  }

  public String productIP()
  {
    return this.IP;
  }

  public String productName()
  {
    return "Sandals";
  }

  public void setProductIP(String paramString)
  {
    this.IP = paramString;
  }

  public void setProductInitialized(Boolean paramBoolean)
  {
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ProductFactory.SandalsPhone
 * JD-Core Version:    0.6.2
 */