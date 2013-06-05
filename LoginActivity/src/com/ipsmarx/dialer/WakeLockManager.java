package com.ipsmarx.dialer;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.Settings.System;
import android.util.Log;

public class WakeLockManager extends BroadcastReceiver
{
  private static PowerManager.WakeLock mWakeLock;
  private String LCLT;

  private void setNeverSleepPolicy(boolean paramBoolean, Context paramContext)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if (paramBoolean);
    for (int i = 2; ; i = 0)
    {
    	return Settings.System.putInt(localContentResolver, "wifi_sleep_policy", i);
     
      
    }
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramContext == null);
    do
    {
      do
        return;
      while (!paramIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.cpuwakelock"));
      Log.v("wakelock", "GOT THE wakelock INTENT");
      this.LCLT = PreferenceManager.getDefaultSharedPreferences(paramContext).getString("CallLicenseTypeLocal", "");
      boolean bool = paramIntent.getExtras().getBoolean("on");
      try
      {
        if (mWakeLock == null)
          mWakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "Breeze WakeLock");
        if (mWakeLock == null)
        {
          Log.d("wakelock", "Wakelock is null");
          return;
        }
      }
      catch (Exception localException)
      {
        Log.e("wakelock", "failed to use wakelock");
        return;
      }
      if (!bool)
        break;
    }
    while ((mWakeLock.isHeld()) || (this.LCLT.equalsIgnoreCase("Pinless")));
    setNeverSleepPolicy(true, paramContext);
    mWakeLock.acquire();
    Log.v("wakelock", "acquiring wakelock");
    return;
    if (mWakeLock.isHeld())
    {
      Log.v("wakelock", "releasing wakelock");
      setNeverSleepPolicy(false, paramContext);
      mWakeLock.release();
    }
    mWakeLock = null;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.WakeLockManager
 * JD-Core Version:    0.6.2
 */