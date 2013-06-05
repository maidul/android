package com.ipsmarx.dialer.ProductFactory;

public abstract class SoftPhone
{
  Boolean mState;

  public boolean dataAllowed()
  {
    return true;
  }

  public String getLoginType()
  {
    return "STANDARD";
  }

  public boolean hasConfigurationMenu()
  {
    return false;
  }

  public boolean hasLoginScreen()
  {
    return true;
  }

  public boolean hasSignOutBtn()
  {
    return true;
  }

  public boolean isProductInitialized()
  {
    return false;
  }

  public abstract String productIP();

  public String productName()
  {
    return "unknown";
  }

  public abstract void setProductIP(String paramString);

  public abstract void setProductInitialized(Boolean paramBoolean);
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ProductFactory.SoftPhone
 * JD-Core Version:    0.6.2
 */