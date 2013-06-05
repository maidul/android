package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TimeZone;

public class ConnectedCall extends Activity
  implements View.OnClickListener, SensorEventListener, Observer
{
  static final float PROXIMITY_THRESHOLD;
  private static final HashMap<Integer, Character> mDisplayMap = new HashMap();
  private static final HashMap<Character, Integer> mToneMap = new HashMap();
  private transient AudioManager audioManager;
  private transient long callId;
  private CallService callServiceLayout;
  private long callWaitingCallTime;
  private Boolean callWaitingDeclined = Boolean.valueOf(true);
  private Boolean callWaitingShown = Boolean.valueOf(false);
  Dialog callWaitingdialog;
  public Bundle callwaitingBundle;
  private long currentCallTime;
  public View editstatus;
  public Boolean holdon;
  private incomingRingerThread incomingRingerThread;
  public Boolean keypadon;
  private WatchCallState mCallStateWatcher;
  public Consts.CallType mCallType;
  private final Handler mHandler = new Handler();
  private LogNumber mLogNumber;
  private SharedPreferences mPreferences;
  private ToneGenerator mToneGenerator;
  private final Runnable mUpdateTimeTask = new Runnable()
  {
    public void run()
    {
      long l1 = System.currentTimeMillis() - ConnectedCall.this.currentCallTime;
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
      localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      if (!ConnectedCall.this.voicemail.booleanValue())
        ((TextView)ConnectedCall.this.editstatus).setText(localSimpleDateFormat.format(new Date(l1)));
      if (UAControl.callwaitingCallinfo.CALLWAITING)
      {
        long l2 = System.currentTimeMillis() - ConnectedCall.this.callWaitingCallTime;
        ConnectedCall.this.getWindow().setFeatureInt(7, 2130903061);
        ((Button)ConnectedCall.this.getWindow().findViewById(2131427405)).setText(" Touch to return to call " + localSimpleDateFormat.format(new Date(l2)) + " ");
      }
      ConnectedCall.this.mHandler.postDelayed(ConnectedCall.this.mUpdateTimeTask, 100L);
    }
  };
  public Boolean muteon;
  public Bundle myBundle;
  public Intent myLocalIntent;
  public Boolean onPausecalled = Boolean.valueOf(false);
  public Sensor proximitySensor;
  private BroadcastReceiver receiver;
  public Ringer ringer;
  public SensorManager sensorManager;
  public Boolean sensoron;
  public SipService service = (SipService)SipService.ServiceInstance.getService();
  public Boolean speakeron;
  public Boolean successfulTransfer = Boolean.valueOf(false);
  public transient TableLayout tLayout;
  public Boolean voicemail;
  private PowerManager.WakeLock wl;

  private void addToCallLogInternal(String paramString, Consts.CallType paramCallType)
  {
    int i = 1;
    if (paramCallType == Consts.CallType.MISSEDCALL)
      i = 3;
    if (paramCallType == Consts.CallType.OUTGOINGCALL)
      i = 2;
    String str1 = getContactNameFromNumber(paramString);
    if (str1 == null);
    for (str1 = "Unknown"; ; str1 = "Unknown")
      do
      {
        String str2 = PreferenceManager.getDefaultSharedPreferences(this).getString("useremail", "NULL");
        DBAdapter localDBAdapter = new DBAdapter(this);
        localDBAdapter.open();
        localDBAdapter.insertCall(System.currentTimeMillis(), str1, paramString, Integer.valueOf(i).intValue(), Integer.valueOf(1), "", (System.currentTimeMillis() - this.currentCallTime) / 1000L, str2);
        localDBAdapter.close();
        return;
      }
      while ((str1.equalsIgnoreCase("Unknown")) || (!str1.equals("")));
  }

  private boolean dismissKeyguard()
  {
    if (Boolean.valueOf(((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode()).booleanValue())
    {
      getWindow().addFlags(4718592);
      return true;
    }
    return false;
  }

  private void setLogNumberAfterCallWaitingEnd(long paramLong)
  {
    UAControl.CallInfo localCallInfo = (UAControl.CallInfo)UAControl.callwaitingCallinfo.clone();
    CallInfoCareTaker.getInstance().restoreState(UAControl.callwaitingCallinfo);
    if (localCallInfo.msg.cid != paramLong)
    {
      this.mLogNumber.setLognumber(localCallInfo.LogNumber).setCallType(localCallInfo.calltype);
      if (!this.callWaitingDeclined.booleanValue())
        beginUpdatingCallLog(UAControl.callwaitingCallinfo.LogNumber, UAControl.callwaitingCallinfo.calltype);
    }
    while (true)
    {
      UAControl.callwaitingCallinfo = (UAControl.CallInfo)localCallInfo.clone();
      return;
      this.mLogNumber.setLognumber(UAControl.callwaitingCallinfo.LogNumber).setCallType(UAControl.callwaitingCallinfo.calltype);
      if (!this.callWaitingDeclined.booleanValue())
        beginUpdatingCallLog(localCallInfo.LogNumber, localCallInfo.calltype);
    }
  }

  private void showDialPad()
  {
    ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837631));
    this.editstatus.setVisibility(8);
    if ((0xF & getResources().getConfiguration().screenLayout) == 1)
    {
      LinearLayout localLinearLayout3 = (LinearLayout)findViewById(2131427340);
      localLinearLayout3.setPadding(localLinearLayout3.getPaddingLeft(), 0, localLinearLayout3.getPaddingRight(), 0);
    }
    while (true)
    {
      LinearLayout localLinearLayout2 = (LinearLayout)findViewById(2131427343);
      localLinearLayout2.removeAllViews();
      ButtonGridLayout localButtonGridLayout = (ButtonGridLayout)((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903051, null);
      ((Button)findViewById(2131427395)).setBackgroundResource(2130837632);
      this.keypadon = Boolean.valueOf(true);
      localLinearLayout2.addView(localButtonGridLayout);
      initDialScreen();
      return;
      LinearLayout localLinearLayout1 = (LinearLayout)findViewById(2131427340);
      localLinearLayout1.setPadding(localLinearLayout1.getPaddingLeft(), 0, localLinearLayout1.getPaddingRight(), localLinearLayout1.getPaddingBottom());
      localLinearLayout1.setGravity(80);
    }
  }

  public void beginUpdatingCallLog(String paramString, Consts.CallType paramCallType)
  {
    new Thread(new Runnable()
    {
      Consts.CallType calltype;
      String phoneNumber;

      public void run()
      {
        try
        {
          ConnectedCall.this.addToCallLogInternal(this.phoneNumber, this.calltype);
          return;
        }
        catch (Exception localException)
        {
        }
      }
    }).start();
  }

  public long getCallWaitingCallTime()
  {
    return this.callWaitingCallTime;
  }

  public Bundle getCallwaitingBundle()
  {
    return this.callwaitingBundle;
  }

  public String getContactNameFromNumber(String paramString)
  {
    String[] arrayOfString = { "display_name", "data1" };
    Uri localUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(paramString));
    Cursor localCursor = getContentResolver().query(localUri, arrayOfString, null, null, null);
    startManagingCursor(localCursor);
    if (localCursor.moveToFirst())
      return localCursor.getString(localCursor.getColumnIndex("display_name"));
    return paramString;
  }

  public long getCurrentCallTime()
  {
    return this.currentCallTime;
  }

  public void initDialScreen()
  {
    mDisplayMap.put(Integer.valueOf(2131427356), Character.valueOf('1'));
    mDisplayMap.put(Integer.valueOf(2131427357), Character.valueOf('2'));
    mDisplayMap.put(Integer.valueOf(2131427358), Character.valueOf('3'));
    mDisplayMap.put(Integer.valueOf(2131427359), Character.valueOf('4'));
    mDisplayMap.put(Integer.valueOf(2131427360), Character.valueOf('5'));
    mDisplayMap.put(Integer.valueOf(2131427361), Character.valueOf('6'));
    mDisplayMap.put(Integer.valueOf(2131427362), Character.valueOf('7'));
    mDisplayMap.put(Integer.valueOf(2131427363), Character.valueOf('8'));
    mDisplayMap.put(Integer.valueOf(2131427364), Character.valueOf('9'));
    mDisplayMap.put(Integer.valueOf(2131427366), Character.valueOf('0'));
    mDisplayMap.put(Integer.valueOf(2131427367), Character.valueOf('#'));
    mDisplayMap.put(Integer.valueOf(2131427365), Character.valueOf('*'));
    mToneMap.put(Character.valueOf('1'), Integer.valueOf(1));
    mToneMap.put(Character.valueOf('2'), Integer.valueOf(2));
    mToneMap.put(Character.valueOf('3'), Integer.valueOf(3));
    mToneMap.put(Character.valueOf('4'), Integer.valueOf(4));
    mToneMap.put(Character.valueOf('5'), Integer.valueOf(5));
    mToneMap.put(Character.valueOf('6'), Integer.valueOf(6));
    mToneMap.put(Character.valueOf('7'), Integer.valueOf(7));
    mToneMap.put(Character.valueOf('8'), Integer.valueOf(8));
    mToneMap.put(Character.valueOf('9'), Integer.valueOf(9));
    mToneMap.put(Character.valueOf('0'), Integer.valueOf(0));
    mToneMap.put(Character.valueOf('#'), Integer.valueOf(11));
    mToneMap.put(Character.valueOf('*'), Integer.valueOf(10));
    Iterator localIterator = mDisplayMap.keySet().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return;
      findViewById(((Integer)localIterator.next()).intValue()).setOnClickListener(this);
    }
  }

  public void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    case 2131427399:
    default:
    case 2131427400:
    case 2131427375:
    case 2131427376:
    case 2131427405:
    case 2131427336:
    case 2131427337:
    case 2131427338:
    case 2131427394:
    case 2131427396:
    case 2131427398:
    case 2131427397:
    case 2131427395:
    case 2131427370:
      while (true)
      {
        if (mDisplayMap.containsKey(Integer.valueOf(paramView.getId())))
        {
          if (this.callId != 0L)
          {
            char[] arrayOfChar = new char[2];
            arrayOfChar[0] = ((Character)mDisplayMap.get(Integer.valueOf(paramView.getId()))).charValue();
            UAControl localUAControl1 = SipService.uaCtrl;
            localUAControl1.getClass();
            UAControl.Message localMessage1 = new UAControl.Message(localUAControl1, "SENDKEY", this.callId);
            String str1 = new String(arrayOfChar);
            localMessage1.addAttribute("Key", str1);
            SipService.uaCtrl.sendMessage(localMessage1);
          }
          this.mToneGenerator.startTone(((Integer)mToneMap.get(mDisplayMap.get(Integer.valueOf(paramView.getId())))).intValue(), 150);
        }
        return;
        if (this.incomingRingerThread != null)
          this.incomingRingerThread.setRunning(false);
        SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_IDLE);
        UAControl localUAControl18 = SipService.uaCtrl;
        localUAControl18.getClass();
        UAControl.Message localMessage18 = new UAControl.Message(localUAControl18, "DISCONNECT", this.callId);
        SipService.uaCtrl.sendMessage(localMessage18);
        if (this.ringer != null)
          this.ringer.stopRing();
        if (this.callwaitingBundle == null)
        {
          finish();
          continue;
          SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_IDLE);
          UAControl localUAControl17 = SipService.uaCtrl;
          localUAControl17.getClass();
          UAControl.Message localMessage17 = new UAControl.Message(localUAControl17, "DISCONNECT", this.callId);
          SipService.uaCtrl.sendMessage(localMessage17);
          if (this.ringer != null)
            this.ringer.stopRing();
          Log.w("Connected callDecline %d", Long.toString(this.callId));
          finish();
          continue;
          UAControl localUAControl16 = SipService.uaCtrl;
          localUAControl16.getClass();
          UAControl.Message localMessage16 = new UAControl.Message(localUAControl16, "CONNECT", this.callId);
          SipService.uaCtrl.sendMessage(localMessage16);
          this.ringer.stopRing();
          ((LinearLayout)findViewById(2131427344)).removeAllViews();
          View localView4 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null);
          ((LinearLayout)findViewById(2131427344)).addView(localView4);
          findViewById(2131427400).setOnClickListener(this);
          findViewById(2131427341);
          ((TextView)findViewById(2131427342)).setText("Connected");
          SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
          if (this.mPreferences.getString("ServiceType", "").equalsIgnoreCase("PBX"))
            findViewById(2131427397);
          findViewById(2131427395).setOnClickListener(this);
          ToggleButton localToggleButton7 = (ToggleButton)findViewById(2131427398);
          localToggleButton7.setOnClickListener(this);
          ToggleButton localToggleButton8 = (ToggleButton)findViewById(2131427394);
          localToggleButton8.setOnClickListener(this);
          ToggleButton localToggleButton9 = (ToggleButton)findViewById(2131427396);
          localToggleButton9.setOnClickListener(this);
          if (this.sensoron.booleanValue())
          {
            localToggleButton8.setEnabled(true);
            findViewById(2131427395).setEnabled(true);
            localToggleButton9.setEnabled(true);
            localToggleButton7.setEnabled(true);
          }
          this.currentCallTime = System.currentTimeMillis();
          this.mHandler.postDelayed(this.mUpdateTimeTask, 100L);
          ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837566));
          continue;
          CallInfoCareTaker localCallInfoCareTaker = CallInfoCareTaker.getInstance();
          UAControl.CallInfo localCallInfo = (UAControl.CallInfo)UAControl.callwaitingCallinfo.clone();
          localCallInfoCareTaker.restoreState(UAControl.callwaitingCallinfo);
          Long localLong4 = Long.valueOf(UAControl.callwaitingCallinfo.msg.cid);
          Long localLong5 = Long.valueOf(localCallInfo.msg.cid);
          label866: boolean bool;
          if (this.callWaitingShown.booleanValue())
          {
            this.callServiceLayout.setState(this.callServiceLayout.getCallWaitingHeldState());
            this.callServiceLayout.getState().swapTimers().Layout();
            UAControl localUAControl14 = SipService.uaCtrl;
            localUAControl14.getClass();
            UAControl.Message localMessage14 = new UAControl.Message(localUAControl14, "HOLD", localLong5.longValue());
            SipService.uaCtrl.sendMessage(localMessage14);
            SipService.getService().setCallOnHold(localLong5.longValue());
            if (!this.callWaitingShown.booleanValue())
              break label1032;
            bool = false;
            this.callWaitingShown = Boolean.valueOf(bool);
            localCallInfoCareTaker.saveState(localCallInfo);
          }
          try
          {
            Thread.sleep(30L);
            UAControl localUAControl15 = SipService.uaCtrl;
            localUAControl15.getClass();
            UAControl.Message localMessage15 = new UAControl.Message(localUAControl15, "RESUME", localLong4.longValue());
            SipService.uaCtrl.sendMessage(localMessage15);
            this.callId = localLong4.longValue();
            continue;
            this.callServiceLayout.setState(this.callServiceLayout.getCallWaitingResumedState());
            this.callServiceLayout.getState().swapTimers().Layout();
            break label866;
            label1032: bool = true;
          }
          catch (InterruptedException localInterruptedException2)
          {
            while (true)
              localInterruptedException2.printStackTrace();
          }
          getWindow().setFeatureInt(7, 2130903061);
          Button localButton = (Button)getWindow().findViewById(2131427405);
          localButton.setVisibility(0);
          localButton.setOnClickListener(this);
          this.callWaitingdialog.dismiss();
          Long localLong2 = Long.valueOf(UAControl.callwaitingCallinfo.msg.cid);
          Long localLong3 = Long.valueOf(this.callwaitingBundle.getLong("previousCallId"));
          UAControl localUAControl12 = SipService.uaCtrl;
          localUAControl12.getClass();
          UAControl.Message localMessage12 = new UAControl.Message(localUAControl12, "HOLD", localLong3.longValue());
          SipService.uaCtrl.sendMessage(localMessage12);
          SipService.getService().setCallOnHold(localLong3.longValue());
          this.callWaitingCallTime = this.currentCallTime;
          this.currentCallTime = System.currentTimeMillis();
          if (this.holdon.booleanValue())
            ((ToggleButton)findViewById(2131427398)).setChecked(false);
          try
          {
            Thread.sleep(30L);
            this.callServiceLayout.setState(this.callServiceLayout.getCallWaitingResumedState());
            this.callServiceLayout.getState().Layout();
            this.callId = localLong2.longValue();
            UAControl localUAControl13 = SipService.uaCtrl;
            localUAControl13.getClass();
            UAControl.Message localMessage13 = new UAControl.Message(localUAControl13, "CONNECT", localLong2.longValue());
            SipService.uaCtrl.sendMessage(localMessage13);
            SipService.getService();
            SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
            this.callWaitingShown = Boolean.valueOf(true);
            this.callWaitingDeclined = Boolean.valueOf(false);
          }
          catch (InterruptedException localInterruptedException1)
          {
            while (true)
              localInterruptedException1.printStackTrace();
          }
          this.callWaitingdialog.dismiss();
          Log.v(ConnectedCall.class.toString(), "This CallId will be disconnected at user request-->" + UAControl.callwaitingCallinfo.msg.cid);
          UAControl localUAControl11 = SipService.uaCtrl;
          localUAControl11.getClass();
          UAControl.Message localMessage11 = new UAControl.Message(localUAControl11, "DISCONNECT", UAControl.callwaitingCallinfo.msg.cid);
          SipService.uaCtrl.sendMessage(localMessage11);
          CallInfoCareTaker.getInstance().restoreState(UAControl.callwaitingCallinfo);
          this.callWaitingDeclined = Boolean.valueOf(true);
          continue;
          this.callWaitingdialog.dismiss();
          this.callWaitingCallTime = System.currentTimeMillis();
          Log.v(ConnectedCall.class.toString(), "CallId about to be ended -->" + this.callId);
          UAControl.callwaitingCallinfo.CALLWAITING = true;
          UAControl.callwaitingCallinfo.endcurrentAnswerSecond = true;
          Long localLong1 = Long.valueOf(this.callwaitingBundle.getLong("previousCallId"));
          SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
          UAControl localUAControl10 = SipService.uaCtrl;
          localUAControl10.getClass();
          UAControl.Message localMessage10 = new UAControl.Message(localUAControl10, "DISCONNECT", localLong1.longValue());
          SipService.uaCtrl.sendMessage(localMessage10);
          this.callId = UAControl.callwaitingCallinfo.msg.cid;
          this.callServiceLayout.setState(this.callServiceLayout.getCallWaitingResumedState());
          this.callServiceLayout.getState().Layout();
          this.currentCallTime = System.currentTimeMillis();
          if (this.holdon.booleanValue())
            ((ToggleButton)findViewById(2131427398)).setChecked(false);
          BroadcastReceiver local3 = new BroadcastReceiver()
          {
            final long connectTocallId = ConnectedCall.this.callId;

            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
            {
              if (paramAnonymousContext == null);
              while (!paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.CONNECT_TO_CALL"))
                return;
              UAControl localUAControl = SipService.uaCtrl;
              localUAControl.getClass();
              UAControl.Message localMessage = new UAControl.Message(localUAControl, "CONNECT", this.connectTocallId);
              SipService.uaCtrl.sendMessage(localMessage);
              ConnectedCall.this.unregisterReceiver(this);
            }
          };
          IntentFilter localIntentFilter = new IntentFilter("com.ipsmarx.dialer.custom.intent.action.CONNECT_TO_CALL");
          registerReceiver(local3, localIntentFilter);
          this.callWaitingDeclined = Boolean.valueOf(false);
          continue;
          if (((ToggleButton)findViewById(2131427394)).isChecked())
          {
            UAControl localUAControl9 = SipService.uaCtrl;
            localUAControl9.getClass();
            UAControl.Message localMessage9 = new UAControl.Message(localUAControl9, "MUTE", this.callId);
            SipService.uaCtrl.sendMessage(localMessage9);
            this.muteon = Boolean.valueOf(true);
          }
          else
          {
            UAControl localUAControl8 = SipService.uaCtrl;
            localUAControl8.getClass();
            UAControl.Message localMessage8 = new UAControl.Message(localUAControl8, "UNMUTE", this.callId);
            SipService.uaCtrl.sendMessage(localMessage8);
            this.muteon = Boolean.valueOf(false);
            continue;
            if (!this.holdon.booleanValue())
              SipService.getService().restartAudioProcessing(false);
            if (!((ToggleButton)findViewById(2131427396)).isChecked())
            {
              this.audioManager.setSpeakerphoneOn(false);
              this.speakeron = Boolean.valueOf(false);
              String str3 = this.mPreferences.getString("Handset_EchoState", null);
              UAControl localUAControl7 = SipService.uaCtrl;
              localUAControl7.getClass();
              UAControl.Message localMessage7 = new UAControl.Message(localUAControl7, "AUDIODEVSTAT", this.callId);
              localMessage7.addAttribute("Name", "Handset");
              if ((str3 != null) && (!str3.equalsIgnoreCase("")))
                localMessage7.addAttribute("EchoState", str3);
              SipService.uaCtrl.sendMessage(localMessage7);
            }
            while (!this.holdon.booleanValue())
            {
              SipService.getService().restartAudioProcessing(true);
              break;
              this.audioManager.setSpeakerphoneOn(true);
              this.speakeron = Boolean.valueOf(true);
              String str2 = this.mPreferences.getString("Speaker_EchoState", null);
              UAControl localUAControl6 = SipService.uaCtrl;
              localUAControl6.getClass();
              UAControl.Message localMessage6 = new UAControl.Message(localUAControl6, "AUDIODEVSTAT", this.callId);
              localMessage6.addAttribute("Name", "Speaker");
              if ((str2 != null) && (!str2.equalsIgnoreCase("")))
                localMessage6.addAttribute("EchoState", str2);
              SipService.uaCtrl.sendMessage(localMessage6);
            }
            if (!((ToggleButton)findViewById(2131427398)).isChecked())
            {
              UAControl localUAControl5 = SipService.uaCtrl;
              localUAControl5.getClass();
              UAControl.Message localMessage5 = new UAControl.Message(localUAControl5, "RESUME", this.callId);
              SipService.uaCtrl.sendMessage(localMessage5);
              this.holdon = Boolean.valueOf(false);
            }
            else
            {
              UAControl localUAControl4 = SipService.uaCtrl;
              localUAControl4.getClass();
              UAControl.Message localMessage4 = new UAControl.Message(localUAControl4, "HOLD", this.callId);
              SipService.uaCtrl.sendMessage(localMessage4);
              this.holdon = Boolean.valueOf(true);
              continue;
              if (SipService.getService() != null)
                SipService.getService().setCallTransferMode(true);
              UAControl localUAControl3 = SipService.uaCtrl;
              localUAControl3.getClass();
              UAControl.Message localMessage3 = new UAControl.Message(localUAControl3, "HOLD", this.callId);
              SipService.uaCtrl.sendMessage(localMessage3);
              Bundle localBundle = getIntent().getExtras();
              localBundle.putLong("currentCallTime", this.currentCallTime);
              localBundle.putLong("currentCallId", this.callId);
              Intent localIntent = new Intent();
              localIntent.setClass(this, DialerApp.class);
              localIntent.putExtras(localBundle);
              localIntent.setAction("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER");
              localIntent.setFlags(805306368);
              startActivity(localIntent);
              continue;
              if (this.keypadon.booleanValue())
              {
                ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837566));
                if (!this.voicemail.booleanValue())
                  this.editstatus.setVisibility(0);
                label2403: View localView3;
                LinearLayout localLinearLayout3;
                if ((0xF & getResources().getConfiguration().screenLayout) == 1)
                {
                  LinearLayout localLinearLayout4 = (LinearLayout)findViewById(2131427340);
                  localLinearLayout4.setPadding(localLinearLayout4.getPaddingLeft(), 135, localLinearLayout4.getPaddingRight(), localLinearLayout4.getPaddingBottom());
                  ((LinearLayout)findViewById(2131427343)).removeAllViews();
                  ((LinearLayout)findViewById(2131427344)).removeAllViews();
                  localView3 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null);
                  if ((0xF & getResources().getConfiguration().screenLayout) != 1)
                    break label2661;
                  localLinearLayout3 = (LinearLayout)findViewById(2131427344);
                  int m = localLinearLayout3.getPaddingLeft();
                  int n = localLinearLayout3.getPaddingRight();
                  int i1 = localLinearLayout3.getPaddingBottom();
                  localLinearLayout3.setPadding(m, 0, n, i1);
                }
                while (true)
                {
                  localLinearLayout3.addView(localView3);
                  findViewById(2131427400).setOnClickListener(this);
                  ToggleButton localToggleButton4 = (ToggleButton)findViewById(2131427394);
                  localToggleButton4.setOnClickListener(this);
                  findViewById(2131427395).setOnClickListener(this);
                  ToggleButton localToggleButton5 = (ToggleButton)findViewById(2131427396);
                  localToggleButton5.setOnClickListener(this);
                  ToggleButton localToggleButton6 = (ToggleButton)findViewById(2131427398);
                  localToggleButton6.setOnClickListener(this);
                  this.keypadon = Boolean.valueOf(false);
                  if (!this.sensoron.booleanValue())
                    break;
                  localToggleButton4.setEnabled(true);
                  findViewById(2131427395).setEnabled(true);
                  localToggleButton5.setEnabled(true);
                  localToggleButton6.setEnabled(false);
                  break;
                  ((LinearLayout)findViewById(2131427340)).setGravity(80);
                  break label2403;
                  label2661: localLinearLayout3 = (LinearLayout)findViewById(2131427344);
                  LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                  localLayoutParams2.topMargin = ((int)TypedValue.applyDimension(1, 40.0F, getResources().getDisplayMetrics()));
                  localLinearLayout3.setLayoutParams(localLayoutParams2);
                }
              }
              showDialPad();
              continue;
              ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837565));
              SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_IDLE);
              UAControl localUAControl2 = SipService.uaCtrl;
              localUAControl2.getClass();
              UAControl.Message localMessage2 = new UAControl.Message(localUAControl2, "DISCONNECT", this.callId);
              SipService.uaCtrl.sendMessage(localMessage2);
              finish();
            }
          }
        }
      }
    case 2131427372:
    }
    ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837566));
    if (!this.voicemail.booleanValue())
      this.editstatus.setVisibility(0);
    label2893: View localView1;
    LinearLayout localLinearLayout1;
    if ((0xF & getResources().getConfiguration().screenLayout) == 1)
    {
      LinearLayout localLinearLayout2 = (LinearLayout)findViewById(2131427340);
      localLinearLayout2.setPadding(localLinearLayout2.getPaddingLeft(), 135, localLinearLayout2.getPaddingRight(), localLinearLayout2.getPaddingBottom());
      ((LinearLayout)findViewById(2131427343)).removeAllViews();
      ((LinearLayout)findViewById(2131427344)).removeAllViews();
      localView1 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null);
      if ((0xF & getResources().getConfiguration().screenLayout) != 1)
        break label3157;
      localLinearLayout1 = (LinearLayout)findViewById(2131427344);
      int i = localLinearLayout1.getPaddingLeft();
      int j = localLinearLayout1.getPaddingRight();
      int k = localLinearLayout1.getPaddingBottom();
      localLinearLayout1.setPadding(i, 50, j, k);
    }
    while (true)
    {
      localLinearLayout1.addView(localView1);
      findViewById(2131427400).setOnClickListener(this);
      ToggleButton localToggleButton1 = (ToggleButton)findViewById(2131427394);
      localToggleButton1.setOnClickListener(this);
      findViewById(2131427395).setOnClickListener(this);
      ToggleButton localToggleButton2 = (ToggleButton)findViewById(2131427396);
      localToggleButton2.setOnClickListener(this);
      View localView2 = findViewById(2131427397);
      localView2.setOnClickListener(this);
      ToggleButton localToggleButton3 = (ToggleButton)findViewById(2131427398);
      localToggleButton3.setOnClickListener(this);
      if (!this.sensoron.booleanValue())
        break;
      localToggleButton1.setEnabled(true);
      localView2.setEnabled(true);
      findViewById(2131427395).setEnabled(true);
      localToggleButton2.setEnabled(true);
      localToggleButton3.setEnabled(true);
      break;
      ((LinearLayout)findViewById(2131427340)).setGravity(80);
      break label2893;
      label3157: localLinearLayout1 = (LinearLayout)findViewById(2131427344);
      LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(-2, -2);
      localLayoutParams1.topMargin = ((int)TypedValue.applyDimension(1, 40.0F, getResources().getDisplayMetrics()));
      localLinearLayout1.setLayoutParams(localLayoutParams1);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    requestWindowFeature(7);
    this.sensorManager = ((SensorManager)getSystemService("sensor"));
    this.proximitySensor = this.sensorManager.getDefaultSensor(8);
    this.audioManager = ((AudioManager)getSystemService("audio"));
    this.voicemail = Boolean.valueOf(false);
    this.muteon = Boolean.valueOf(false);
    this.speakeron = Boolean.valueOf(false);
    this.holdon = Boolean.valueOf(false);
    this.sensoron = Boolean.valueOf(false);
    this.keypadon = Boolean.valueOf(false);
    super.onCreate(paramBundle);
    setContentView(2130903045);
    dismissKeyguard();
    Log.v("InComingCall", "CC oncreate called");
    this.myLocalIntent = getIntent();
    this.myBundle = this.myLocalIntent.getExtras();
    if (((0xF & getResources().getConfiguration().screenLayout) == 3) || ((0xF & getResources().getConfiguration().screenLayout) == 4))
      setRequestedOrientation(0);
    this.tLayout = ((TableLayout)findViewById(2131427392));
    this.mCallStateWatcher = new WatchCallState();
    this.mCallStateWatcher.addObserver(this);
    this.voicemail = Boolean.valueOf(this.myBundle.getBoolean("Voicemail"));
    this.editstatus = findViewById(2131427342);
    this.wl = ((PowerManager)getSystemService("power")).newWakeLock(268435482, "DoNotDimScreen");
    this.wl.acquire();
    this.mLogNumber = new LogNumber();
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    this.mToneGenerator = new ToneGenerator(3, 80);
    setVolumeControlStream(3);
    setBluetoothOn();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.callwaiting");
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.MISSED_CALL");
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.END_CALLWAITING");
    this.receiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousContext == null)
          return;
        if (paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.callwaiting"))
        {
          ConnectedCall.this.callwaitingBundle = paramAnonymousIntent.getExtras();
          ConnectedCall.this.showDialog(Consts.CallState.UA_STATE_CALL_WAITING.getCallStateCode());
        }
        if (paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.END_CALLWAITING"))
        {
          long l1 = paramAnonymousIntent.getExtras().getLong("disconnectedCallId");
          long l2 = SipService.getService().getCallOnHold();
          ConnectedCall.this.setLogNumberAfterCallWaitingEnd(l1);
          if ((l1 != l2) && (l2 != 0L) && (!ConnectedCall.this.callWaitingDeclined.booleanValue()))
          {
            if (!ConnectedCall.this.callServiceLayout.getState().equals(ConnectedCall.this.callServiceLayout.getCallWaitingResumedState()))
              break label392;
            ConnectedCall.this.callServiceLayout.setState(ConnectedCall.this.callServiceLayout.getCallWaitingHeldState());
          }
        }
        while (true)
        {
          ConnectedCall.this.callServiceLayout.getState().swapTimers().Layout();
          CallInfoCareTaker.getInstance().restoreState(UAControl.callwaitingCallinfo);
          long l3 = UAControl.callwaitingCallinfo.msg.cid;
          UAControl localUAControl = SipService.uaCtrl;
          localUAControl.getClass();
          UAControl.Message localMessage = new UAControl.Message(localUAControl, "RESUME", l3);
          SipService.uaCtrl.sendMessage(localMessage);
          ConnectedCall.this.callServiceLayout.setState(ConnectedCall.this.callServiceLayout.getNormalState());
          ConnectedCall.this.callServiceLayout.getState().Layout();
          ConnectedCall.this.callwaitingBundle = null;
          UAControl.callwaitingCallinfo.CALLWAITING = false;
          ConnectedCall.this.callId = UAControl.callwaitingCallinfo.msg.cid;
          ConnectedCall.this.callWaitingDeclined = Boolean.valueOf(true);
          if (!paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.MISSED_CALL"))
            break;
          if (ConnectedCall.this.callWaitingdialog != null)
            ConnectedCall.this.callWaitingdialog.dismiss();
          if (ConnectedCall.this.callwaitingBundle == null)
            break label415;
          ConnectedCall.this.callId = ConnectedCall.this.callwaitingBundle.getLong("previousCallId");
          String str = SipService.getService().getMissedCallNumber();
          ConnectedCall.this.beginUpdatingCallLog(str, Consts.CallType.MISSEDCALL);
          CallInfoCareTaker.getInstance().restoreState(UAControl.callwaitingCallinfo);
          return;
          label392: ConnectedCall.this.callServiceLayout.setState(ConnectedCall.this.callServiceLayout.getCallWaitingResumedState());
        }
        label415: ConnectedCall.this.mLogNumber.setLognumber(SipService.getService().getMissedCallNumber());
        ConnectedCall.this.mLogNumber.setCallType(Consts.CallType.MISSEDCALL);
      }
    };
    registerReceiver(this.receiver, localIntentFilter);
    setToForegroundService(this.myLocalIntent, this.myBundle);
    this.callServiceLayout = new CallService(this);
  }

  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    super.onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
    if (paramView.getId() == 2131427411)
    {
      getMenuInflater().inflate(2131361795, paramContextMenu);
      paramContextMenu.setHeaderTitle("Call Transfer Options");
      Bitmap localBitmap = ((BitmapDrawable)getResources().getDrawable(2130837670)).getBitmap();
      paramContextMenu.setHeaderIcon(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(localBitmap, 50, 50, true)));
    }
  }

  protected Dialog onCreateDialog(int paramInt)
  {
    switch ($SWITCH_TABLE$com$ipsmarx$dialer$Consts$CallState()[Consts.CallState.values()[paramInt].ordinal()])
    {
    default:
    case 7:
    }
    while (true)
    {
      return super.onCreateDialog(paramInt);
      if (this.myBundle.getString("number") == null)
      {
        String str = SipService.getService().getOutgoingNumber();
        this.myBundle.putString("number", str);
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
      LayoutInflater localLayoutInflater = getLayoutInflater();
      View localView1 = localLayoutInflater.inflate(2130903044, null);
      ((TextView)localView1.findViewById(2131427339)).setText("Incoming Call from:\n" + UAControl.callwaitingCallinfo.msg.getAttribute("CallingNumber"));
      localBuilder.setCustomTitle(localView1);
      View localView2 = localLayoutInflater.inflate(2130903043, null);
      localBuilder.setView(localView2);
      this.callWaitingdialog = localBuilder.create();
      ((ImageButton)localView2.findViewById(2131427337)).setOnClickListener(this);
      ((ImageButton)localView2.findViewById(2131427336)).setOnClickListener(this);
      ((ImageButton)localView2.findViewById(2131427338)).setOnClickListener(this);
      this.callWaitingdialog.setCancelable(false);
      ((Vibrator)getSystemService("vibrator")).vibrate(500L);
      this.callWaitingdialog.show();
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();
    this.audioManager.setSpeakerphoneOn(false);
    this.audioManager.setMicrophoneMute(false);
    this.audioManager.stopBluetoothSco();
    unregisterReceiver(this.receiver);
    beginUpdatingCallLog(this.mLogNumber.getLognumber(), this.mLogNumber.getCalltype());
    this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    SipService.getService().stopForeground(true);
    this.wl.release();
    setBluetoothOff();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
      moveTaskToBack(true);
    label104: 
    do
    {
      do
      {
        return true;
        if (SipService.uaCtrl.getCallstate() == Consts.CallState.UA_STATE_INCOMING_CALL);
        switch (paramInt)
        {
        default:
          return super.onKeyDown(paramInt, paramKeyEvent);
        case 24:
          this.audioManager.adjustStreamVolume(2, 1, 1);
          return true;
        case 25:
          if (!paramKeyEvent.isLongPress())
            break label104;
        case 26:
        }
      }
      while ((this.ringer == null) || (!this.ringer.isRinging()));
      this.ringer.stopRing();
      return true;
      this.audioManager.adjustStreamVolume(2, -1, 1);
      return true;
      if (this.ringer == null)
        return super.onKeyDown(paramInt, paramKeyEvent);
    }
    while (!this.ringer.isRinging());
    this.ringer.stopRing();
    return true;
  }

  protected void onNewIntent(Intent paramIntent)
  {
    this.successfulTransfer = Boolean.valueOf(paramIntent.getBooleanExtra("successfulTransfer", false));
    setIntent(paramIntent);
    super.onNewIntent(paramIntent);
  }

  protected void onPause()
  {
    this.myLocalIntent.getExtras().putLong("currentCallId", this.callId);
    super.onPause();
  }

  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.currentCallTime = paramBundle.getInt("currentCallTime");
    this.keypadon = Boolean.valueOf(paramBundle.getBoolean("keypadon"));
    this.callWaitingCallTime = paramBundle.getLong("callWaitingCallTime");
  }

  public void onResume()
  {
    super.onResume();
    Log.w("Connected callCreated %d", Long.toString(this.callId));
    Log.v(getClass().toString(), " onresume is called:" + SipService.uaCtrl.getCallstate());
    this.mCallStateWatcher.callStateChanged(SipService.uaCtrl.getCallstate());
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putLong("currentCallTime", this.currentCallTime);
    paramBundle.putLong("callWaitingCallTime", this.callWaitingCallTime);
    paramBundle.putBoolean("keypadon", this.keypadon.booleanValue());
    paramBundle.putString("lognumber", this.mLogNumber.getLognumber());
  }

  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    float f = paramSensorEvent.values[0];
    if (f > 0.0F)
    {
      this.sensoron = Boolean.valueOf(true);
      if (SipService.uaCtrl.getCallstate() == Consts.CallState.UA_STATE_INCALL)
      {
        if (f <= 0.0F)
          break label166;
        ToggleButton localToggleButton5 = (ToggleButton)findViewById(2131427394);
        if (localToggleButton5 != null)
          localToggleButton5.setEnabled(true);
        View localView3 = findViewById(2131427395);
        if (localView3 != null)
          localView3.setEnabled(true);
        View localView4 = findViewById(2131427397);
        if (localView4 != null)
          localView4.setEnabled(true);
        ToggleButton localToggleButton6 = (ToggleButton)findViewById(2131427398);
        if (localToggleButton6 != null)
          localToggleButton6.setEnabled(true);
      }
      label125: if (f <= 0.0F)
        break label255;
      ToggleButton localToggleButton2 = (ToggleButton)findViewById(2131427396);
      if (localToggleButton2 != null)
        localToggleButton2.setEnabled(true);
    }
    label166: label255: ToggleButton localToggleButton1;
    do
    {
      return;
      this.sensoron = Boolean.valueOf(false);
      break;
      ToggleButton localToggleButton3 = (ToggleButton)findViewById(2131427394);
      if (localToggleButton3 != null)
        localToggleButton3.setEnabled(false);
      View localView1 = findViewById(2131427395);
      if (localView1 != null)
        localView1.setEnabled(false);
      View localView2 = findViewById(2131427397);
      if (localView2 != null)
        localView2.setEnabled(false);
      ToggleButton localToggleButton4 = (ToggleButton)findViewById(2131427398);
      if (localToggleButton4 == null)
        break label125;
      localToggleButton4.setEnabled(false);
      break label125;
      localToggleButton1 = (ToggleButton)findViewById(2131427396);
    }
    while (localToggleButton1 == null);
    localToggleButton1.setEnabled(false);
  }

  public void onStart()
  {
    super.onStart();
    this.sensorManager.registerListener(this, this.proximitySensor, 3);
  }

  public void onStop()
  {
    this.sensorManager.unregisterListener(this);
    this.audioManager.setMicrophoneMute(false);
    super.onStop();
  }

  public void setBluetoothOff()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (localBluetoothAdapter.isEnabled()))
    {
      AudioManager localAudioManager = (AudioManager)getSystemService("audio");
      localAudioManager.stopBluetoothSco();
      localAudioManager.setBluetoothScoOn(false);
    }
  }

  public void setBluetoothOn()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (localBluetoothAdapter.isEnabled()))
    {
      AudioManager localAudioManager = (AudioManager)getSystemService("audio");
      localAudioManager.startBluetoothSco();
      localAudioManager.setBluetoothScoOn(true);
    }
  }

  public void setCallWaitingCallTime(long paramLong)
  {
    this.callWaitingCallTime = paramLong;
  }

  public void setCallwaitingBundle(Bundle paramBundle)
  {
    this.callwaitingBundle = paramBundle;
  }

  public void setCurrentCallTime(long paramLong)
  {
    this.currentCallTime = paramLong;
  }

  public void setToForegroundService(Intent paramIntent, Bundle paramBundle)
  {
    if (SipService.uaCtrl.getCallstate() != Consts.CallState.UA_STATE_INCALL)
    {
      Notification localNotification = new Notification(2130837670, "In Call", System.currentTimeMillis());
      paramIntent.setFlags(813694976);
      PendingIntent localPendingIntent = PendingIntent.getActivity(SipService.getService().getContext(), 0, paramIntent, 0);
      localNotification.setLatestEventInfo(SipService.getService().getContext(), "In Call", paramBundle.getString("number"), localPendingIntent);
      localNotification.flags = (0x20 | localNotification.flags);
      SipService.getService().startForeground(100, localNotification);
    }
  }

  public void update(Observable paramObservable, Object paramObject)
  {
    this.myLocalIntent = getIntent();
    this.myBundle = this.myLocalIntent.getExtras();
    if (this.myBundle.containsKey("number"))
      this.mLogNumber.setLognumber(this.myBundle.getString("number"));
    if (this.callId == 0L)
      this.callId = this.myBundle.getLong("currentCallId");
    if (this.myBundle.containsKey("CallType"))
      this.mLogNumber.setCallType(Consts.CallType.UNKNOWN.getEnumName(this.myBundle.getInt("CallType")));
    switch ($SWITCH_TABLE$com$ipsmarx$dialer$Consts$CallState()[((Consts.CallState)paramObject).ordinal()])
    {
    case 5:
    default:
    case 3:
    case 2:
    case 4:
    case 1:
    case 6:
    }
    ToggleButton localToggleButton2;
    do
    {
      do
      {
        return;
        String str3 = this.myBundle.getString("number");
        View localView1 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null);
        ((LinearLayout)findViewById(2131427344)).addView(localView1);
        findViewById(2131427400).setOnClickListener(this);
        View localView2 = findViewById(2131427341);
        if (!this.voicemail.booleanValue())
        {
          ((TextView)this.editstatus).setText("Dialing");
          ((TextView)localView2).setText(str3);
        }
        while (true)
        {
          ((ToggleButton)findViewById(2131427398)).setEnabled(false);
          ((ToggleButton)findViewById(2131427394)).setEnabled(false);
          ((ImageButton)findViewById(2131427397)).setEnabled(false);
          ToggleButton localToggleButton7 = (ToggleButton)findViewById(2131427396);
          localToggleButton7.setEnabled(true);
          localToggleButton7.setOnClickListener(this);
          findViewById(2131427397).setEnabled(false);
          findViewById(2131427395).setEnabled(false);
          String str4 = this.mPreferences.getString("Handset_EchoState", null);
          UAControl localUAControl2 = SipService.uaCtrl;
          localUAControl2.getClass();
          UAControl.Message localMessage2 = new UAControl.Message(localUAControl2, "AUDIODEVSTAT", this.callId);
          localMessage2.addAttribute("Name", "Handset");
          if ((str4 != null) && (!str4.equalsIgnoreCase("")))
            localMessage2.addAttribute("EchoState", str4);
          SipService.uaCtrl.sendMessage(localMessage2);
          return;
          ((TextView)localView2).setText("Voicemail");
          this.editstatus.setVisibility(4);
        }
        String str1 = this.myBundle.getString("number");
        LinearLayout localLinearLayout3 = (LinearLayout)((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903050, null);
        LinearLayout localLinearLayout4 = (LinearLayout)findViewById(2131427344);
        if (!localLinearLayout4.isShown())
          localLinearLayout4.addView(localLinearLayout3);
        findViewById(2131427375).setOnClickListener(this);
        findViewById(2131427376).setOnClickListener(this);
        ((TextView)findViewById(2131427341)).setText(str1);
        this.ringer = Ringer.getInstance(getApplicationContext());
        if (!this.ringer.isRinging())
          this.ringer.ring();
        String str2 = this.mPreferences.getString("Speaker_EchoState", null);
        UAControl localUAControl1 = SipService.uaCtrl;
        localUAControl1.getClass();
        UAControl.Message localMessage1 = new UAControl.Message(localUAControl1, "AUDIODEVSTAT", this.callId);
        localMessage1.addAttribute("Name", "Speaker");
        if ((str2 != null) && (!str2.equalsIgnoreCase("")))
          localMessage1.addAttribute("EchoState", str2);
        SipService.uaCtrl.sendMessage(localMessage1);
        return;
        if (this.incomingRingerThread != null)
          this.incomingRingerThread.setRunning(false);
        ((LinearLayout)findViewById(2131427340)).setBackgroundDrawable(getResources().getDrawable(2130837566));
        LinearLayout localLinearLayout2 = (LinearLayout)findViewById(2131427344);
        localLinearLayout2.removeAllViewsInLayout();
        localLinearLayout2.addView(((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null));
        findViewById(2131427400).setOnClickListener(this);
        TextView localTextView = (TextView)findViewById(2131427342);
        if (((String)localTextView.getText()).equalsIgnoreCase("Ringing"))
          localTextView.setText("Connected");
        ToggleButton localToggleButton4 = (ToggleButton)findViewById(2131427394);
        localToggleButton4.setOnClickListener(this);
        findViewById(2131427395).setOnClickListener(this);
        ToggleButton localToggleButton5 = (ToggleButton)findViewById(2131427396);
        localToggleButton5.setOnClickListener(this);
        ToggleButton localToggleButton6 = (ToggleButton)findViewById(2131427398);
        localToggleButton6.setOnClickListener(this);
        if (this.sensoron.booleanValue())
        {
          localToggleButton4.setEnabled(true);
          findViewById(2131427395).setEnabled(true);
          localToggleButton5.setEnabled(true);
          localToggleButton6.setEnabled(true);
        }
        if (this.holdon.booleanValue())
          localToggleButton6.setChecked(true);
        if (this.muteon.booleanValue())
          localToggleButton4.setChecked(true);
        if (this.speakeron.booleanValue())
          localToggleButton5.setChecked(true);
        if (this.currentCallTime == 0L)
          this.currentCallTime = System.currentTimeMillis();
        this.mHandler.postDelayed(this.mUpdateTimeTask, 100L);
        if ((this.voicemail.booleanValue()) || (this.keypadon.booleanValue()))
          showDialPad();
      }
      while (!this.successfulTransfer.booleanValue());
      findViewById(2131427400).performClick();
      Intent localIntent = new Intent(SipService.getService().getContext(), BackGroundDialogs.class);
      localIntent.setType("text/plain");
      localIntent.putExtra("android.intent.extra.TEXT", getString(2131230764));
      localIntent.setFlags(805306368);
      startActivity(localIntent);
      this.successfulTransfer = Boolean.valueOf(false);
      return;
      if (this.incomingRingerThread != null)
        this.incomingRingerThread.setRunning(false);
      this.mHandler.removeCallbacks(this.mUpdateTimeTask);
      if (this.ringer != null)
        this.ringer.stopRing();
      finish();
      return;
      if ((Boolean.valueOf(this.myBundle.getBoolean("StartMedia")).booleanValue()) && (this.incomingRingerThread == null))
      {
        incomingRingerThread localincomingRingerThread = new incomingRingerThread(null);
        this.incomingRingerThread = localincomingRingerThread;
        this.incomingRingerThread.setRunning(true).start();
        Log.v(getClass().toString(), "playing Ringer after NOT Receiving StartMedia");
      }
      LinearLayout localLinearLayout1 = (LinearLayout)findViewById(2131427344);
      localLinearLayout1.removeAllViewsInLayout();
      localLinearLayout1.addView(((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903057, null));
      findViewById(2131427400).setOnClickListener(this);
      ((TextView)findViewById(2131427342)).setText("Ringing");
      ToggleButton localToggleButton1 = (ToggleButton)findViewById(2131427394);
      localToggleButton2 = (ToggleButton)findViewById(2131427396);
      localToggleButton2.setOnClickListener(this);
      ToggleButton localToggleButton3 = (ToggleButton)findViewById(2131427398);
      if (this.holdon.booleanValue())
        localToggleButton3.setChecked(true);
      if (this.muteon.booleanValue())
        localToggleButton1.setChecked(true);
    }
    while (!this.speakeron.booleanValue());
    localToggleButton2.setChecked(true);
  }

  public static class Call
  {
    public Long callId;
    public long calltime;
    public String number;

    public Call(Long paramLong1, String paramString, Long paramLong2)
    {
      this.callId = paramLong1;
      this.number = paramString;
      this.calltime = paramLong2.longValue();
    }
  }

  public class LogNumber
  {
    Consts.CallType calltype;
    String lognumber;

    public LogNumber()
    {
    }

    public Consts.CallType getCalltype()
    {
      return this.calltype;
    }

    public String getLognumber()
    {
      return this.lognumber;
    }

    public void setCallType(Consts.CallType paramCallType)
    {
      this.calltype = paramCallType;
    }

    public LogNumber setLognumber(String paramString)
    {
      this.lognumber = paramString;
      return this;
    }
  }

  class WatchCallState extends Observable
  {
    WatchCallState()
    {
    }

    void callStateChanged(Consts.CallState paramCallState)
    {
      setChanged();
      notifyObservers(paramCallState);
    }
  }

  private class incomingRingerThread extends Thread
  {
    final int MAX_VOLUME = 100;
    private int currAudioMode;
    final float desiredVolume = 20.0F;
    MediaPlayer incomingRinger;
    private AudioManager m_amAudioManager;
    boolean running = false;
    final float volume = (float)(1.0D - Math.log(80.0D) / Math.log(100.0D));

    private incomingRingerThread()
    {
    }

    public void run()
    {
      try
      {
        this.currAudioMode = ConnectedCall.this.audioManager.getMode();
        this.m_amAudioManager = ((AudioManager)ConnectedCall.this.getSystemService("audio"));
        this.m_amAudioManager.setMode(2);
        this.m_amAudioManager.setSpeakerphoneOn(false);
        this.incomingRinger = MediaPlayer.create(ConnectedCall.this.getApplicationContext(), 2131034112);
        this.incomingRinger.setVolume(this.volume, this.volume);
        while (true)
        {
          boolean bool = this.running;
          if (!bool)
            return;
          this.incomingRinger.start();
          Thread.sleep(4000L);
        }
      }
      catch (Exception localException)
      {
        Log.e(getClass().getName(), "Error starting Ringer when No StartMedia.", localException);
        return;
      }
      finally
      {
        this.m_amAudioManager.setMode(this.currAudioMode);
      }
    }

    public incomingRingerThread setRunning(boolean paramBoolean)
    {
      this.running = paramBoolean;
      return this;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ConnectedCall
 * JD-Core Version:    0.6.2
 */