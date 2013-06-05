package com.ipsmarx.dialer;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DialerApp extends TabActivity
  implements TabHost.OnTabChangeListener
{
  private SipService _boundService = null;
  private final ServiceConnection _serviceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      DialerApp.this._boundService = SipService.getService();
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      DialerApp.this._boundService = null;
      Display Name
    }
  };
  private Button cancelTransferbtn;
  private long currentCallTime;
  private final Handler mHandler = new Handler();
  private final Runnable mUpdateTimeTask = new Runnable()
  {
    public void run()
    {
      long l = System.currentTimeMillis() - DialerApp.this.currentCallTime;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
      localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      DialerApp.this.cancelTransferbtn.setText(" Touch to return to call " + localSimpleDateFormat.format(new Date(l)) + " ");
      DialerApp.this.mHandler.postDelayed(DialerApp.this.mUpdateTimeTask, 100L);
    }
  };
  private BroadcastReceiver receiver;

  public void doBindService()
  {
    startService(new Intent(this, SipService.class));
    if (Boolean.valueOf(bindService(new Intent(this, SipService.class), this._serviceConnection, 1)).booleanValue())
    {
      SipService.setActivityReference(getCurrentActivity());
      SipService.setWakeLockMsg(true);
    }
  }

  public SipService getBindService()
  {
    if (this._boundService != null)
      return this._boundService;
    return (SipService)SipService.ServiceInstance.getService();
  }

  public void onCreate(Bundle paramBundle)
  {
    Intent localIntent1 = getIntent();
    if ((localIntent1 != null) && (localIntent1.getAction() != null) && (localIntent1.getAction().equalsIgnoreCase("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER")))
      requestWindowFeature(7);
    super.onCreate(paramBundle);
    setContentView(2130903062);
    String str = PreferenceManager.getDefaultSharedPreferences(this).getString("Culture", "en-US");
    Resources localResources = getResources();
    TabHost localTabHost = (TabHost)findViewById(16908306);
    TabHost.TabSpec localTabSpec1 = localTabHost.newTabSpec("dialer");
    TabHost.TabSpec localTabSpec2 = localTabHost.newTabSpec("contacts");
    TabHost.TabSpec localTabSpec3 = localTabHost.newTabSpec("web");
    TabHost.TabSpec localTabSpec4 = localTabHost.newTabSpec("calllog");
    localTabHost.setOnTabChangedListener(this);
    Intent localIntent2 = new Intent(this, DialerTab.class);
    if ((localIntent1 != null) && (localIntent1.getAction() != null) && (localIntent1.getAction().equalsIgnoreCase("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER")))
    {
      getWindow().setFeatureInt(7, 2130903066);
      localIntent2.setAction("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER");
      this.currentCallTime = localIntent1.getLongExtra("currentCallTime", 0L);
      this.mHandler.postDelayed(this.mUpdateTimeTask, 100L);
      this.cancelTransferbtn = ((Button)getWindow().findViewById(2131427415));
      localIntent2.putExtras(localIntent1.getExtras());
    }
    localTabSpec1.setIndicator("", localResources.getDrawable(2130837622)).setContent(localIntent2);
    localTabSpec2.setIndicator("", localResources.getDrawable(2130837568)).setContent(new Intent(this, ContactsActivity.class));
    if (str.equalsIgnoreCase("en-US"))
      localTabSpec3.setIndicator("Home", localResources.getDrawable(2130837639)).setContent(new Intent(this, WebInfo.class));
    localTabSpec4.setIndicator("", localResources.getDrawable(2130837560)).setContent(new Intent(this, CallLogListInternal.class));
    localTabHost.addTab(localTabSpec1);
    localTabHost.addTab(localTabSpec2);
    localTabHost.addTab(localTabSpec3);
    localTabHost.addTab(localTabSpec4);
    if (str.equalsIgnoreCase("ko-KR"))
    {
      TabHost.TabSpec localTabSpec5 = localTabHost.newTabSpec("myaccount");
      localTabSpec5.setIndicator("", localResources.getDrawable(2130837681)).setContent(new Intent(this, WebInfo.class));
      localTabHost.addTab(localTabSpec5);
    }
    doBindService();
    this.receiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.DESTROY"))
        {
          DialerApp.this.unbindService(DialerApp.this._serviceConnection);
          SipService.getService().stopSelf();
        }
      }
    };
    IntentFilter localIntentFilter = new IntentFilter("com.ipsmarx.dialer.custom.intent.action.DESTROY");
    registerReceiver(this.receiver, localIntentFilter);
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.receiver != null)
      unregisterReceiver(this.receiver);
    this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    unbindService(this._serviceConnection);
  }

  public void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    if ((paramIntent.getAction() != null) && (paramIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG")))
    {
      showCallLog(paramIntent);
      paramIntent.setAction(null);
    }
    setIntent(paramIntent);
  }

  protected void onResume()
  {
    super.onResume();
    showCallLog(getIntent());
  }

  public void onTabChanged(String paramString)
  {
    if (paramString.equalsIgnoreCase("calllog"))
      UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG", null);
  }

  public void showCallLog(Intent paramIntent)
  {
    if ((paramIntent != null) && (paramIntent.getAction() != null) && (paramIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG")))
    {
      paramIntent.setAction(null);
      if ((SipService.uaCtrl.getCallstate() == Consts.CallState.UA_STATE_INCALL) || (SipService.uaCtrl.getCallstate() == Consts.CallState.UA_STATE_INCOMING_CALL))
      {
        Intent localIntent = new Intent();
        localIntent.setClass(this, ConnectedCall.class);
        localIntent.setFlags(131072);
        startActivity(localIntent);
      }
      TabHost localTabHost = getTabHost();
      localTabHost.setCurrentTab(0);
      localTabHost.setCurrentTab(3);
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.DialerApp
 * JD-Core Version:    0.6.2
 */