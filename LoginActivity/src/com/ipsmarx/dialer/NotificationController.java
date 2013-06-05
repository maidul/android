package com.ipsmarx.dialer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;

public class NotificationController extends BroadcastReceiver
{
  private static int count;

  private static void resetCount()
  {
    count = 0;
  }

  public void createNotification(Context paramContext, String paramString, boolean paramBoolean)
  {
    try
    {
      NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
      localNotificationManager.cancel(101);
      Notification localNotification = new Notification(2130837670, "missed call " + paramString, System.currentTimeMillis());
      localNotification.flags = (0x10 | localNotification.flags);
      Intent localIntent = new Intent(paramContext, DialerApp.class);
      localIntent.setAction("com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG");
      localIntent.setFlags(805306368);
      PendingIntent localPendingIntent = PendingIntent.getActivity(paramContext, 0, localIntent, 268435456);
      localNotification.setLatestEventInfo(paramContext, SoftPhoneFactory.getInstance().getCurrentProduct().productName(), "Missed Call " + paramString, localPendingIntent);
      if (paramBoolean)
      {
        int i = 1 + count;
        count = i;
        localNotification.number = i;
      }
      localNotificationManager.notify(101, localNotification);
      return;
    }
    catch (Exception localException)
    {
      Log.v("NotificationController", "Missed call Notification failed:" + localException.toString());
    }
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ((paramContext == null) || (paramIntent == null));
    do
    {
      return;
      if (paramIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG"))
      {
        ((NotificationManager)paramContext.getSystemService("notification")).cancel(101);
        resetCount();
      }
    }
    while (!paramIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.MISSED_CALL"));
    String str = paramIntent.getExtras().getString("CallingNumber");
    createNotification(paramContext, str, true);
    Log.v("missedcall", "callingNumber:" + str);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.NotificationController
 * JD-Core Version:    0.6.2
 */