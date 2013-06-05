package com.ipsmarx.dialer.ProductFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class BrandedPhone extends SoftPhone
{
  String IP = "173.244.121.200";
  Context mContext;

  public BrandedPhone(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public boolean isProductInitialized()
  {
    return PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("ProfileComplete", false);
  }

  public String productIP()
  {
    return this.IP;
  }

  public String productName()
  {
    return "Breeze";
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
 * Qualified Name:     com.ipsmarx.dialer.ProductFactory.BrandedPhone
 * JD-Core Version:    0.6.2
 */