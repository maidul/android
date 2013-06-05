package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import org.apache.http.conn.util.InetAddressUtils;

public class SipService extends Service
{
  private static final String SERVER_IP = "ServerIP";
  private static final String TAG = "MyService";
  private static Activity mActivity;
  public static Boolean rejectedCall = Boolean.valueOf(false);
  public static UAControl uaCtrl;
  private String MissedCallNumber;
  public String RegStatus;
  private final IBinder _bind = new LocalBinder();
  private transient AudioServer auSrv;
  private long callOnHold;
  public boolean callTransferMode = false;
  public boolean cellularCallState = false;
  private transient long currentCallId;
  public boolean isRegistered;
  private transient Context mContext;
  public Dialog mDialog;
  private transient String myDisplayName;
  private transient String myNumber;
  private String outgoingnumber;
  private transient Random random;
  public transient SharedPreferences sp;
  private transient WifiManager.WifiLock wifilock;

  static
  {
    System.loadLibrary("sip_ua_jni");
  }

  private native int SipUAUpdateCred(String paramString1, String paramString2, String paramString3, String paramString4);

  public static Activity getActivityReference()
  {
    if ((mActivity instanceof Activity))
      return mActivity;
    return null;
  }

  public static SipService getService()
  {
    return (SipService)ServiceInstance.getService();
  }

  public static void setActivityReference(Activity paramActivity)
  {
    mActivity = paramActivity;
  }

  private void setUpDefaultCodecs(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = this.sp.edit();
    if (!paramBoolean)
      localEditor.remove("defaultcodec").commit();
    int i = this.sp.getInt("defaultcodec", -1);
    String str = this.sp.getString("AllowG729", "0");
    int j;
    if (this.sp.getString("G729_Licenses", "0").equals("1"))
      j = 1;
    while ((str != null) && (str.equals("1")) && (j != 0))
      if (i != -1)
      {
        getService().switchCodecs(i);
        return;
        j = 0;
      }
      else
      {
        localEditor.putString("localcodec1", getString(2131230766));
        localEditor.putString("localcodec2", getString(2131230767)).commit();
        getService().switchCodecs(Consts.CodecSelection.G729FIRST.getPriorityCode());
        return;
      }
    getService().switchCodecs(Consts.CodecSelection.NOG729.getPriorityCode());
  }

  public static void setWakeLockMsg(boolean paramBoolean)
  {
    Intent localIntent = new Intent("com.ipsmarx.dialer.custom.intent.action.cpuwakelock");
    localIntent.putExtra("on", paramBoolean);
    if (getActivityReference() != null)
      getActivityReference().sendBroadcast(localIntent);
  }

  private native int startAudio();

  private native int startSipUA(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt1, String paramString7, int paramInt2, String paramString8, String paramString9);

  private native int stopAudio();

  private native int userAgentStatus();

  public void ConditionCheckDialOut(String paramString, Activity paramActivity)
  {
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    String str1 = this.sp.getString("CallLicenseTypeLocal", "VoIP");
    final String str2;
    final String str3;
    if (paramString.length() > 0)
    {
      str2 = PhoneNumberUtils.stripSeparators(paramString);
      SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      str3 = localSharedPreferences.getString("AccessNumber", "NULL");
      String str4 = localSharedPreferences.getString("AllowAccessNumber", "NULL");
      if (str1.equalsIgnoreCase("Both"))
      {
        if ((isOnline()) || (str2.length() <= 0) || (str3.length() <= 2) || (!str4.equalsIgnoreCase("1")))
          break label286;
        AlertDialog localAlertDialog = new AlertDialog.Builder(paramActivity).create();
        localAlertDialog.setTitle(2131230738);
        localAlertDialog.setMessage(getString(2131230737));
        localAlertDialog.setButton(getString(2131230739), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            try
            {
              Intent localIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + str3 + "," + str2));
              localIntent.addFlags(268435456);
              SipService.this.startActivity(localIntent);
              return;
            }
            catch (Exception localException)
            {
              localException.printStackTrace();
            }
          }
        });
        localAlertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
          }
        });
        localAlertDialog.show();
      }
    }
    while (true)
    {
      if (str1.equalsIgnoreCase("VoIP"))
      {
        setOutgoingNumber(str2);
        dialOut(str2);
      }
      if (str1.equalsIgnoreCase("Pinless"));
      try
      {
        if (!str3.equalsIgnoreCase("NULL"))
        {
          Intent localIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + str3 + "," + str2));
          localIntent.addFlags(268435456);
          startActivity(localIntent);
          return;
          label286: setOutgoingNumber(str2);
          dialOut(str2);
        }
        else
        {
          Toast.makeText(this, "Access number not provided.", 1).show();
          return;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
  }

  public void beginCallTransfer(String paramString)
  {
    UAControl localUAControl = uaCtrl;
    localUAControl.getClass();
    UAControl.Message localMessage = new UAControl.Message(localUAControl, "TRANSFER", this.currentCallId);
    localMessage.addAttribute("Number", paramString);
    uaCtrl.sendMessage(localMessage);
  }

  public void clearAllCalls()
  {
    UAControl localUAControl1 = uaCtrl;
    localUAControl1.getClass();
    UAControl.Message localMessage1 = new UAControl.Message(localUAControl1, "DISCONNECT", this.currentCallId);
    UAControl localUAControl2 = uaCtrl;
    localUAControl2.getClass();
    UAControl.Message localMessage2 = new UAControl.Message(localUAControl2, "DISCONNECT", UAControl.callwaitingCallinfo.msg.cid);
    uaCtrl.sendMessage(localMessage1);
    try
    {
      uaCtrl.sendMessage(localMessage1);
      Thread.sleep(300L);
      uaCtrl.sendMessage(localMessage2);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }

  public void dialOut(String paramString)
  {
    if (!isNetworkAllowed())
    {
      showAlert(getNetworktype() + " " + "connection has been disabled. Try connecting Wifi.", false);
      return;
    }
    if ((!isRegistered()) && (isNetworkAllowed()))
    {
      showAlert("Not Registered.  Check network settings or account info.", false);
      return;
    }
    if (!this.cellularCallState)
    {
      if (isOnline())
      {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(this.sp.getString("rule1", "NULL"));
        localArrayList.add(this.sp.getString("rule2", "NULL"));
        localArrayList.add(this.sp.getString("rule3", "NULL"));
        localArrayList.add(this.sp.getString("rule4", "NULL"));
        localArrayList.add(this.sp.getString("rule5", "NULL"));
        localArrayList.add(this.sp.getString("rule6", "NULL"));
        localArrayList.add(this.sp.getString("rule7", "NULL"));
        localArrayList.add(this.sp.getString("rule8", "NULL"));
        localArrayList.add(this.sp.getString("rule9", "NULL"));
        localArrayList.add(this.sp.getString("rule10", "NULL"));
        int i = localArrayList.size();
        for (int j = 0; ; j++)
        {
          if (j >= i);
          while (true)
          {
            if (!isInCallTransferMode())
              break label379;
            beginCallTransfer(paramString);
            return;
            String[] arrayOfString = ((String)localArrayList.get(j)).split("=");
            if (!paramString.startsWith(arrayOfString[0]))
              break;
            String str = paramString.substring(arrayOfString[0].length());
            paramString = arrayOfString[1] + str;
          }
        }
        label379: this.currentCallId = (0x7FFFFFFF & this.random.nextLong());
        UAControl localUAControl = uaCtrl;
        localUAControl.getClass();
        UAControl.Message localMessage = new UAControl.Message(localUAControl, "CALLINIT", this.currentCallId);
        localMessage.addAttribute("CalledNumber", paramString);
        localMessage.addAttribute("CallingNumber", this.myNumber);
        localMessage.addAttribute("DisplayName", this.myDisplayName);
        uaCtrl.sendMessage(localMessage);
        uaCtrl.setCallstate(Consts.CallState.UA_STATE_OUTGOING_CALL);
        Bundle localBundle = new Bundle();
        localBundle.putInt("callstate", 2);
        localBundle.putString("number", paramString);
        if (paramString.equalsIgnoreCase("*97"))
          localBundle.putBoolean("Voicemail", true);
        localBundle.putLong("currentCallId", this.currentCallId);
        Intent localIntent = new Intent();
        localIntent.setClass(getApplicationContext(), ConnectedCall.class);
        localIntent.putExtras(localBundle);
        localIntent.setFlags(268435456);
        getApplicationContext().startActivity(localIntent);
        return;
      }
      Toast.makeText(this, "Please check your network connection.", 1).show();
      return;
    }
    Toast.makeText(this, "Cellular call in progress, please try later.", 1).show();
  }

  public void dialVMNumber()
  {
    if (!isNetworkAllowed())
    {
      showAlert(getNetworktype() + " " + "connection has been disabled. Try connecting Wifi.", false);
      return;
    }
    if ((!isRegistered()) && (isNetworkAllowed()))
    {
      showAlert("Not Registered.  Check network settings or account info.", false);
      return;
    }
    setOutgoingNumber("VoiceMail");
    if (isOnline())
    {
      this.currentCallId = (0x7FFFFFFF & this.random.nextLong());
      Bundle localBundle = new Bundle();
      localBundle.putInt("callstate", 2);
      localBundle.putString("number", "*97");
      localBundle.putLong("currentCallId", this.currentCallId);
      localBundle.putBoolean("Voicemail", true);
      Intent localIntent = new Intent();
      localIntent.setClass(getApplicationContext(), ConnectedCall.class);
      localIntent.putExtras(localBundle);
      localIntent.setFlags(268435456);
      uaCtrl.setCallstate(Consts.CallState.UA_STATE_OUTGOING_CALL);
      getApplicationContext().startActivity(localIntent);
      UAControl localUAControl = uaCtrl;
      localUAControl.getClass();
      UAControl.Message localMessage = new UAControl.Message(localUAControl, "CALLINIT", this.currentCallId);
      localMessage.addAttribute("CalledNumber", "*97");
      localMessage.addAttribute("CallingNumber", this.myNumber);
      localMessage.addAttribute("DisplayName", this.myDisplayName);
      uaCtrl.sendMessage(localMessage);
      return;
    }
    Toast.makeText(this, "Please check your data connection.", 1).show();
  }

  public long getCallOnHold()
  {
    return this.callOnHold;
  }

  public Context getContext()
  {
    return this.mContext;
  }

  public String getLocalIpAddress()
  {
    try
    {
      InetAddress localInetAddress;
      do
      {
        Enumeration localEnumeration1 = NetworkInterface.getNetworkInterfaces();
        Enumeration localEnumeration2;
        while (!localEnumeration2.hasMoreElements())
        {
          if (!localEnumeration1.hasMoreElements())
            break;
          localEnumeration2 = ((NetworkInterface)localEnumeration1.nextElement()).getInetAddresses();
        }
        localInetAddress = (InetAddress)localEnumeration2.nextElement();
      }
      while ((localInetAddress.isLoopbackAddress()) || (!InetAddressUtils.isIPv4Address(localInetAddress.getHostAddress())));
      String str = localInetAddress.getHostAddress().toString();
      return str;
    }
    catch (SocketException localSocketException)
    {
      Log.e(" get IP Address", localSocketException.toString());
    }
    return null;
  }

  public String getMissedCallNumber()
  {
    try
    {
      String str = this.MissedCallNumber;
      return str;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  public String getNetworktype()
  {
    String str = "";
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    if (localConnectivityManager.getActiveNetworkInfo() != null)
      str = localConnectivityManager.getActiveNetworkInfo().getTypeName();
    return str;
  }

  public String getOutgoingNumber()
  {
    return this.outgoingnumber;
  }

  public String getRegStatus()
  {
    return this.RegStatus;
  }

  public void init()
  {
    int i = 0;
    Log.v("MyService", "calling SipService init()");
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    Boolean localBoolean1 = Boolean.valueOf(this.sp.getBoolean("signin", false));
    this.myDisplayName = this.sp.getString("DispName", "NULL");
    this.myNumber = this.sp.getString("SipId", "NULL");
    String str1 = this.sp.getString("ServerIP", "NULL");
    String str2 = this.sp.getString("Password", "NULL");
    String str3 = this.sp.getString("AllowG729", "0");
    String str4 = getLocalIpAddress();
    int j = AudioRecord.getMinBufferSize(8000, 16, 2);
    Log.d("DialerApp", "Min Buffer Size: " + j);
    try
    {
      StringBuilder localStringBuilder = new StringBuilder("is the mobile StipStack Awake:");
      String str5;
      Boolean localBoolean2;
      String str7;
      if (isMobileSipStackAwake().booleanValue())
      {
        str5 = "YES";
        Log.v("MyService", str5);
        SoftPhone localSoftPhone = SoftPhoneFactory.getInstance().getCurrentProduct();
        boolean bool1 = localSoftPhone.dataAllowed();
        i = 0;
        if (bool1)
          break label725;
        boolean bool2 = localSoftPhone.dataAllowed();
        i = 0;
        if (bool2)
          break label719;
        if (isWiFiUp())
          break label725;
        break label719;
        localBoolean2 = Boolean.valueOf(bool3);
        boolean bool4 = localBoolean1.booleanValue();
        i = 0;
        if (bool4)
        {
          boolean bool5 = isMobileSipStackAwake().booleanValue();
          i = 0;
          if (bool5)
            break label631;
          boolean bool6 = localBoolean2.booleanValue();
          i = 0;
          if (!bool6)
            break label631;
          boolean bool7 = Build.VERSION.SDK_INT < 1.5D;
          i = 0;
          if (!bool7)
            break label731;
          str6 = Build.VERSION.SDK_INT;
          str7 = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
          Log.v("DialerApp", "Starting Sip UA\nServerIP:" + str1 + "App Version:" + str7 + "\nIP Address:" + str4 + "\nDisplayName:" + this.myDisplayName + "\nNumber:" + this.myNumber + "\nAudioServer Port:" + this.auSrv.getServerPort() + "\nAllowG729:" + str3 + "\nminbufsize:" + j);
        }
      }
      for (i = startSipUA(str4, str1, this.myDisplayName, this.myNumber, this.myNumber, str2, this.auSrv.getServerPort(), str3, j, "Android SDK:" + str6, str7); ; i = 0)
      {
        if (!localBoolean2.booleanValue())
          showAlert(getNetworktype() + " " + "connection has been disabled. Try connecting Wifi.", true);
        Log.d("DialerApp", "startSipUA() return: " + i);
        if ((i > 0) && (uaCtrl == null))
          uaCtrl = new UAControl(i);
        this.random = new Random(System.currentTimeMillis());
        stopForeground(true);
        return;
        str5 = "NO";
        break;
        label631: updateCredentials(this.myNumber, this.myNumber, str2, str1);
      }
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      while (true)
        Log.d("DialerApp", "startSipUA() return: " + localUnsatisfiedLinkError.getMessage());
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      while (true)
      {
        Log.d("DialerApp", "Version name could not be found: " + localNameNotFoundException.getMessage());
        continue;
        label719: boolean bool3 = false;
        continue;
        label725: bool3 = true;
        continue;
        label731: String str6 = "API <= 1.5";
      }
    }
  }

  public void init2()
  {
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
  }

  public boolean isInCallTransferMode()
  {
    return this.callTransferMode;
  }

  public Boolean isMobileSipStackAwake()
  {
    int i = 1;
    if (userAgentStatus() == i);
    while (true)
    {
      return Boolean.valueOf(i);
      int j = 0;
    }
  }

  public boolean isNetworkAllowed()
  {
    return (SoftPhoneFactory.getInstance().getCurrentProduct().dataAllowed()) || (isWiFiUp());
  }

  protected boolean isOnline()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
  }

  public boolean isRegistered()
  {
    return this.isRegistered;
  }

  public boolean isWiFiUp()
  {
    return ((ConnectivityManager)getSystemService("connectivity")).getNetworkInfo(1).isConnectedOrConnecting();
  }

  public IBinder onBind(Intent paramIntent)
  {
    return this._bind;
  }

  public void onCreate()
  {
    Log.d("MyService", "onCreate");
    ServiceInstance.setService(this);
    setContext(getApplicationContext());
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    SoftPhoneFactory localSoftPhoneFactory = SoftPhoneFactory.getInstance();
    String str = this.sp.getString("productType", "IPsmarx");
    Log.v("MyService", str + ":softphone product restored to factory");
    localSoftPhoneFactory.setCurrentProduct(str, this);
    localSoftPhoneFactory.getCurrentProduct().setProductIP(this.sp.getString("ServerIP", ""));
    this.auSrv = new AudioServer((AudioManager)getSystemService("audio"), getApplicationContext());
    this.auSrv.start();
    if (!isOnline())
    {
      Toast.makeText(this, "Please check your device's network connection.", 1).show();
      return;
    }
    init();
    ListenToPhoneState localListenToPhoneState = new ListenToPhoneState(null);
    ((TelephonyManager)getSystemService("phone")).listen(localListenToPhoneState, 32);
  }

  public void onDestroy()
  {
    Log.d("MyService", "onDestroy");
    setWakeLockMsg(false);
    this.wifilock.release();
    super.onDestroy();
  }

  public void onStart(Intent paramIntent, int paramInt)
  {
    Log.d("MyService", "onStart");
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    this.wifilock = ((WifiManager)getSystemService("wifi")).createWifiLock(3, "wifilock");
    this.wifilock.acquire();
    if (updateCredentials(this.sp.getString("SipId", ""), this.sp.getString("AuthId", ""), this.sp.getString("Password", ""), this.sp.getString("ServerIP", "")).booleanValue())
    {
      setUpDefaultCodecs(false);
      return;
    }
    setUpDefaultCodecs(true);
  }

  public void restartAudioProcessing(boolean paramBoolean)
  {
    if (uaCtrl.getCallstate() == Consts.CallState.UA_STATE_INCALL)
    {
      if (paramBoolean)
        startAudio();
    }
    else
      return;
    stopAudio();
  }

  public void saveEchoState(String paramString1, String paramString2)
  {
    this.sp.edit().putString(paramString1 + "_EchoState", paramString2);
  }

  public void setCallOnHold(long paramLong)
  {
    this.callOnHold = paramLong;
  }

  public void setCallTransferMode(boolean paramBoolean)
  {
    this.callTransferMode = paramBoolean;
  }

  public void setContext(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public void setCurrentCallId(Long paramLong)
  {
    this.currentCallId = paramLong.longValue();
  }

  public void setMissedCallNumber(String paramString)
  {
    try
    {
      this.MissedCallNumber = paramString;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  public void setOutgoingNumber(String paramString)
  {
    this.outgoingnumber = paramString;
  }

  public void setRegStatus(String paramString)
  {
    this.RegStatus = paramString;
  }

  public void setRegistered(boolean paramBoolean)
  {
    this.isRegistered = paramBoolean;
  }

  public void showAlert(String paramString, boolean paramBoolean)
  {
    Intent localIntent = new Intent(this, BackGroundDialogs.class);
    localIntent.setType("text/plain");
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("keepDuringOnPause", paramBoolean);
    localIntent.putExtras(localBundle);
    localIntent.putExtra("android.intent.extra.TEXT", paramString);
    localIntent.setFlags(805306368);
    startActivity(localIntent);
  }

  public void stopRegistration(String paramString)
  {
    if (isMobileSipStackAwake().booleanValue())
    {
      Log.v("MyService", "Sending ACCOUNTSTATUS WTIH Loggedout");
      UAControl localUAControl = uaCtrl;
      localUAControl.getClass();
      UAControl.Message localMessage = new UAControl.Message(localUAControl, "ACCOUNTSTATUS", 0L);
      localMessage.addAttribute("Loggedout", "0");
      if ((paramString != null) && (paramString.equalsIgnoreCase("Stopnetwork")))
        localMessage.addAttribute("Stopnetwork", "1");
      uaCtrl.sendMessage(localMessage);
    }
  }

  public void switchCodecs(int paramInt)
  {
    if ((getService() != null) && (uaCtrl != null))
    {
      UAControl localUAControl = uaCtrl;
      localUAControl.getClass();
      UAControl.Message localMessage = new UAControl.Message(localUAControl, "SWITCH_CODEC", 0L);
      localMessage.addAttribute("Priority", paramInt);
      uaCtrl.sendMessage(localMessage);
    }
  }

  public Boolean updateCredentials(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    int i = 1;
    SoftPhone localSoftPhone = SoftPhoneFactory.getInstance().getCurrentProduct();
    if ((!localSoftPhone.dataAllowed()) && ((localSoftPhone.dataAllowed()) || (!isWiFiUp())));
    for (boolean bool = false; !Boolean.valueOf(bool).booleanValue(); bool = i)
      return Boolean.valueOf(false);
    if ((paramString4 != null) || (paramString2 != null) || (paramString1 != null))
    {
      int j = SipUAUpdateCred(paramString1, paramString2, paramString3, paramString4);
      Log.v("SipService", "called update credentials " + j);
      if (j == i);
      while (true)
      {
        return Boolean.valueOf(i);
        i = 0;
      }
    }
    stopRegistration(null);
    return Boolean.valueOf(false);
  }

  public void updateSipStackWithNetworkConfig(Boolean paramBoolean, int paramInt)
  {
    if (!isMobileSipStackAwake().booleanValue())
      return;
    SoftPhoneFactory localSoftPhoneFactory = SoftPhoneFactory.getInstance();
    SoftPhone localSoftPhone = localSoftPhoneFactory.getCurrentProduct();
    String str1 = getClass().toString();
    StringBuilder localStringBuilder1 = new StringBuilder("is uaCtrl null:");
    String str2;
    label53: String str4;
    label101: UAControl.Message localMessage;
    if (uaCtrl == null)
    {
      str2 = "yes";
      Log.v(str1, str2);
      String str3 = getClass().toString();
      StringBuilder localStringBuilder2 = new StringBuilder("is sipservice.getservice null:");
      if (getService() != null)
        break label249;
      str4 = "yes";
      Log.v(str3, str4);
      UAControl localUAControl = uaCtrl;
      localUAControl.getClass();
      localMessage = new UAControl.Message(localUAControl, "NETSTAT", 0L);
      if (paramBoolean.booleanValue())
        break label305;
      if (paramInt != 0)
        break label257;
      if (localSoftPhone.dataAllowed())
      {
        String str6 = getLocalIpAddress();
        Log.v("MyService", "Network Reachable via Cellular Data. Sending NETSTAT- Reachable:" + str6);
        localMessage.addAttribute("Reachable", str6);
      }
    }
    while (true)
    {
      uaCtrl.sendMessage(localMessage);
      if ((localSoftPhoneFactory.getCurrentProduct().dataAllowed()) || ((!paramBoolean.booleanValue()) && (paramInt == 1)))
        break;
      stopRegistration("Stopnetwork");
      return;
      str2 = "no";
      break label53;
      label249: str4 = "no";
      break label101;
      label257: if (paramInt == 1)
      {
        String str5 = getLocalIpAddress();
        Log.v("MyService", "Network Reachable via WiFi. Sending NETSTAT- Reachable:" + str5);
        localMessage.addAttribute("Reachable", str5);
        continue;
        label305: Log.v("MyService", "Network UnReachable. Sending NETSTAT- Reachable:no");
        localMessage.addAttribute("Reachable", "no");
      }
    }
  }

  private class ListenToPhoneState extends PhoneStateListener
  {
    private ListenToPhoneState()
    {
    }

    public void onCallStateChanged(int paramInt, String paramString)
    {
      stateName(paramInt);
    }

    public String stateName(int paramInt)
    {
      switch (paramInt)
      {
      default:
        return Integer.toString(paramInt);
      case 0:
        SipService.this.cellularCallState = false;
        return "Idle";
      case 2:
        SipService.this.cellularCallState = true;
        return "Off hook";
      case 1:
      }
      SipService.this.cellularCallState = true;
      return "Ringing";
    }
  }

  public class LocalBinder extends Binder
  {
    public LocalBinder()
    {
    }

    public SipService getService()
    {
      return SipService.this;
    }
  }

  public static class ServiceInstance
  {
    private static Context _service;

    public static Context getService()
    {
      return _service;
    }

    public static void setService(Context paramContext)
    {
      _service = paramContext;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.SipService
 * JD-Core Version:    0.6.2
 */