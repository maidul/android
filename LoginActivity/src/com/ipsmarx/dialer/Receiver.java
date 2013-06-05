package com.ipsmarx.dialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class Receiver extends BroadcastReceiver
{
  public static final String TAG = "SIPReceiver";

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    Boolean localBoolean=null;
    StringBuilder localStringBuilder2;
    if ("android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction()))
    {
      localBoolean = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(paramContext).getBoolean("signin", false));
      if (!localBoolean.booleanValue())
      
      paramContext.startService(new Intent(paramContext, SipService.class));
      localStringBuilder2 = new StringBuilder("Service loaded at start ");
//      if (!localBoolean.booleanValue()){
//    	  
//      }
      
    }
    NetworkInfo localNetworkInfo;
    boolean bool;
    label193: for (String str2 = "true"; ; str2 = "false")
    {
      Log.v("SIPService", str2);
      if (paramIntent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE"))
      {
        Log.v("SIPReceiver", "Network connectivity has changed");
        localNetworkInfo = (NetworkInfo)paramIntent.getParcelableExtra("networkInfo");
       // ((NetworkInfo)paramIntent.getParcelableExtra("otherNetwork"));
        bool = paramIntent.getBooleanExtra("noConnectivity", false);
        if (!bool)
        
        Log.w("SIPReceiver", "onReceive: NOT connected, Data Service not available");
        SipService localSipService2 = SipService.getService();
        if (localSipService2 == null)
          break;
        Log.w("SIPReceiver", "onReceive: got service, starting service initilization");
        localSipService2.updateSipStackWithNetworkConfig(Boolean.valueOf(bool), localNetworkInfo.getType());
        localSipService2.init2();
      }
      return;
    }
    StringBuilder localStringBuilder1 = new StringBuilder("Service not loaded at start as user signed out ");
    if (localBoolean.booleanValue());
    for (String str1 = "true"; ; str1 = "false")
    {
      Log.v("SIPService", str1);
      break;
    }
    Log.w("SIPReceiver", "onReceive: service not available");
    
    //Log.w("SIPReceiver", "onReceive: network up, connected, starting Sip registration");
    SipService localSipService1 = SipService.getService();
    if (localSipService1 != null)
    {
      Log.w("SIPReceiver", "onReceive: got service, starting Sip registration");
      localSipService1.init();
      localSipService1.updateSipStackWithNetworkConfig(Boolean.valueOf(bool), localNetworkInfo.getType());
      return;
    }
    Log.w("SIPReceiver", "onReceive: service not available");
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Receiver
 * JD-Core Version:    0.6.2
 */