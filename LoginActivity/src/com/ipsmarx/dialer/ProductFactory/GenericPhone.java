package com.ipsmarx.dialer.ProductFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class GenericPhone extends SoftPhone
{
  String IP;
  Context mContext;

  public GenericPhone(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public String getLoginType()
  {
    return "NONE";
  }

  public boolean hasConfigurationMenu()
  {
    return true;
  }

  public boolean hasLoginScreen()
  {
    return false;
  }

  public boolean isProductInitialized()
  {
    return PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("ProfileComplete", false);
  }

  public String productIP()
  {
    return null;
  }

  public String productName()
  {
    return "IPsmarx";
  }

  public void setProductIP(String paramString)
  {
    this.IP = paramString;
  }

  public void setProductInitialized(Boolean paramBoolean)
  {
    PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().putBoolean("ProfileComplete", true).commit();
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ProductFactory.GenericPhone
 * JD-Core Version:    0.6.2
 */