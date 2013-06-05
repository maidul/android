package com.ipsmarx.dialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;

public final class SignalStrengthMonitor extends BroadcastReceiver
{
  private static SignalStrengthMonitor instance = null;
  private boolean mUserWarned = false;
  private IntentFilter mfilter = new IntentFilter();
  private boolean mregistered = false;

  private SignalStrengthMonitor()
  {
    this.mfilter.addAction("android.net.wifi.RSSI_CHANGED");
  }

  public static SignalStrengthMonitor getInstance()
  {
    if (instance == null)
      instance = new SignalStrengthMonitor();
    return instance;
  }

  public void broadcastMsg(String paramString, Bundle paramBundle)
  {
    Intent localIntent = new Intent(paramString);
    if (paramBundle != null)
      localIntent.putExtras(paramBundle);
    if ((SipService.getService() != null) && (SipService.getService().getContext() != null))
      SipService.getService().getContext().sendBroadcast(localIntent);
  }

  public int calculateStrength(Context paramContext, int paramInt)
  {
    String str = getNetworkType(paramContext);
    int i;
    if (str.equalsIgnoreCase("Mobile"))
      i = -1;
    do
    {
      boolean bool;
      do
      {
        return i;
        bool = str.equalsIgnoreCase("WIFI");
        i = 0;
      }
      while (!bool);
      if (Build.VERSION.SDK_INT >= 14)
        return WifiManager.calculateSignalLevel(paramInt, 101);
      i = 0;
    }
    while (paramInt <= -100);
    if (paramInt >= -55)
      return 100;
    float f = 45;
    return (int)(100 * (paramInt + 100) / f);
  }

  public IntentFilter getFilter()
  {
    return this.mfilter;
  }

  public String getNetworkType(Context paramContext)
  {
    String str = "UNKNOWN";
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting()))
    {
      if (localNetworkInfo.getType() != 1)
        break label40;
      str = "WIFI";
    }
    label40: 
    while (localNetworkInfo.getType() != 0)
      return str;
    return "Mobile";
  }

  public boolean getRegistration()
  {
    return this.mregistered;
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramContext == null);
    int i;
    do
    {
      do
        return;
      while (!paramIntent.getAction().equals("android.net.wifi.RSSI_CHANGED"));
      i = calculateStrength(paramContext, paramIntent.getIntExtra("newRssi", 0));
      if ((i <= 30) && (i != -1) && (!this.mUserWarned))
      {
        ((Vibrator)paramContext.getSystemService("vibrator")).vibrate(300L);
        Intent localIntent = new Intent(paramContext, BackGroundDialogs.class);
        localIntent.setType("text/plain");
        localIntent.putExtra("android.intent.extra.TEXT", paramContext.getString(2131230763));
        localIntent.setFlags(805306368);
        paramContext.startActivity(localIntent);
        this.mUserWarned = true;
      }
    }
    while (i <= 30);
    broadcastMsg("com.ipsmarx.dialer.custom.intent.action.DIMISS_DIALOG", null);
    this.mUserWarned = false;
  }

  public SignalStrengthMonitor setRegistration(boolean paramBoolean)
  {
    this.mregistered = paramBoolean;
    return this;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.SignalStrengthMonitor
 * JD-Core Version:    0.6.2
 */