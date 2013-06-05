package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DialerTab extends Activity
  implements View.OnClickListener, View.OnLongClickListener, SharedPreferences.OnSharedPreferenceChangeListener
{
  private static final HashMap<Integer, Character> mDisplayMap = new HashMap();
  private static final HashMap<Character, Integer> mToneMap = new HashMap();
  private final Runnable UpdateBalance = new Runnable()
  {
    public void run()
    {
      SharedPreferences.Editor localEditor = DialerTab.this.mPreferences.edit();
      localEditor.putString("balance", DialerTab.this.balanceValue);
      localEditor.commit();
      TextView localTextView = (TextView)DialerTab.this.findViewById(2131427388);
      Float localFloat = new Float(DialerTab.this.balanceValue);
      DecimalFormat localDecimalFormat = new DecimalFormat("#.00");
      if ((DialerTab.this.mPreferences.getString("BalanceShow", "NULL").equalsIgnoreCase("1")) && (!DialerTab.this.mPreferences.getString("ServiceType", "").equalsIgnoreCase("PBX")))
      {
        localTextView.setVisibility(0);
        localTextView.setText("Balance = " + localDecimalFormat.format(localFloat).toString());
        if (localFloat.floatValue() <= 0.0F)
          localTextView.setTextColor(-65536);
        return;
      }
      localTextView.setVisibility(4);
    }
  };
  private String _lastNumber;
  private String balanceValue = "";
  BroadcastReceiver broadcastReceiver;
  public Button cancelTransferbtn;
  private long currentCallId;
  private ImageButton deleteButton;
  private ImageButton dialButton;
  private final String kRLandlineBalancedMinutes = "";
  private final String kRMobilelineBalancedMinutes = "";
  public Context mContext;
  private Drawable mDigitsBackground;
  private Drawable mDigitsBlankBackground;
  private Handler mHandler;
  private Handler mHandlerKr;
  private SharedPreferences mPreferences;
  private ToneGenerator mToneGenerator;
  EditText mtextDigits;
  private boolean networkAlertShown = false;
  public AlertDialog network_alert;
  private Random random;
  private ImageButton voicemailButton;

  private InputSource retrieveInputStream(HttpEntity paramHttpEntity)
  {
    try
    {
      InputSource localInputSource = new InputSource(paramHttpEntity.getContent());
      return localInputSource;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  private void toggleDrawable()
  {
    if (this.mtextDigits.length() != 0);
    for (int i = 1; i != 0; i = 0)
    {
      this.mtextDigits.setBackgroundDrawable(this.mDigitsBackground);
      this.dialButton.setEnabled(true);
      this.deleteButton.setEnabled(true);
      return;
    }
    this.mtextDigits.setCursorVisible(false);
    this.mtextDigits.setBackgroundDrawable(this.mDigitsBlankBackground);
    this.dialButton.setEnabled(false);
    this.deleteButton.setEnabled(false);
  }

  void appendDigit(char paramChar)
  {
    this.mtextDigits.getText().append(paramChar);
  }

  public void blindTransferCall()
  {
    SipService.getService().setCurrentCallId(Long.valueOf(this.currentCallId));
    SipService.getService().ConditionCheckDialOut(this.mtextDigits.getText().toString(), this);
  }

  public void cancelTransfer(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      UAControl localUAControl = SipService.uaCtrl;
      localUAControl.getClass();
      UAControl.Message localMessage = new UAControl.Message(localUAControl, "RESUME", this.currentCallId);
      SipService.uaCtrl.sendMessage(localMessage);
    }
    if (SipService.getService() != null)
      SipService.getService().setCallTransferMode(false);
    String str = this.mtextDigits.getText().toString();
    Intent localIntent = new Intent();
    Bundle localBundle = new Bundle();
    localBundle.putInt("callstate", 2);
    localBundle.putString("number", str);
    localBundle.putBoolean("successfulTransfer", paramBoolean);
    localBundle.putLong("currentCallId", this.currentCallId);
    localIntent.putExtras(localBundle);
    localIntent.setFlags(805306368);
    localIntent.setClass(this, ConnectedCall.class);
    startActivity(localIntent);
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
      while (localInetAddress.isLoopbackAddress());
      String str = localInetAddress.getHostAddress().toString();
      return str;
    }
    catch (SocketException localSocketException)
    {
      Log.e(" get IP Address", localSocketException.toString());
    }
    return null;
  }

  public String getlastNumber()
  {
    return this._lastNumber;
  }

  public void initDialScreen()
  {
    this.mtextDigits = ((EditText)findViewById(2131427390));
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
      {
        Resources localResources = getResources();
        this.mDigitsBackground = localResources.getDrawable(2130837557);
        this.mDigitsBlankBackground = localResources.getDrawable(2130837556);
        this.deleteButton = ((ImageButton)findViewById(2131427412));
        this.deleteButton.setOnClickListener(this);
        this.deleteButton.setOnLongClickListener(this);
        this.dialButton = ((ImageButton)findViewById(2131427411));
        this.dialButton.setOnClickListener(this);
        Intent localIntent = getIntent();
        if ((localIntent != null) && (localIntent.getAction() != null) && (localIntent.getAction().equalsIgnoreCase("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER")))
        {
          registerForContextMenu(this.dialButton);
          this.dialButton.setLongClickable(false);
        }
        this.voicemailButton = ((ImageButton)findViewById(2131427410));
        this.voicemailButton.setOnClickListener(this);
        return;
      }
      int i = ((Integer)localIterator.next()).intValue();
      View localView = findViewById(i);
      if (i == 2131427366)
        localView.setOnLongClickListener(this);
      localView.setOnClickListener(this);
    }
  }

  protected boolean isOnline()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
  }

  public void onClick(View paramView)
  {
    int i = paramView.getId();
    switch (i)
    {
    case 2131427413:
    case 2131427414:
    default:
    case 2131427415:
    case 2131427412:
    case 2131427411:
    case 2131427410:
    }
    while (true)
    {
      if (mDisplayMap.containsKey(Integer.valueOf(i)))
      {
        appendDigit(((Character)mDisplayMap.get(Integer.valueOf(i))).charValue());
        this.mToneGenerator.startTone(((Integer)mToneMap.get(mDisplayMap.get(Integer.valueOf(i)))).intValue(), 150);
      }
      toggleDrawable();
      return;
      cancelTransfer(false);
      continue;
      KeyEvent localKeyEvent = new KeyEvent(0, 67);
      this.mtextDigits.onKeyDown(67, localKeyEvent);
      continue;
      String str = this.mtextDigits.getText().toString();
      SipService localSipService = (SipService)SipService.ServiceInstance.getService();
      Boolean localBoolean = Boolean.valueOf(true);
      if (str.length() > 0)
        setlastNumber(str);
      while (localBoolean.booleanValue())
      {
        Intent localIntent = getIntent();
        if ((localIntent == null) || (localIntent.getAction() == null) || (str.length() <= 0) || (!localIntent.getAction().equalsIgnoreCase("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER")))
          break label298;
        paramView.showContextMenu();
        break;
        if ((getlastNumber() != null) && (getlastNumber().length() > 0))
        {
          if (str.length() == 0)
            localBoolean = Boolean.valueOf(false);
          str = getlastNumber();
          this.mtextDigits.setText(str);
        }
      }
      label298: localSipService.ConditionCheckDialOut(str.replace("+", ""), this);
      continue;
      ((SipService)SipService.ServiceInstance.getService()).dialVMNumber();
    }
  }

  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    boolean bool = true;
    switch (paramMenuItem.getItemId())
    {
    default:
      bool = false;
    case 2131427425:
      return bool;
    case 2131427424:
    }
    blindTransferCall();
    return bool;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903054);
    this.mToneGenerator = new ToneGenerator(3, 80);
    setVolumeControlStream(3);
    initDialScreen();
    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    this.mContext = this;
    this.mHandler = new Handler();
    this.mHandlerKr = new Handler();
    this.broadcastReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if ("com.ipsmarx.dialer.custom.intent.action.ERROR_TRANSFER".equalsIgnoreCase(paramAnonymousIntent.getAction()))
          DialerTab.this.showAlert("Transfer Failed.");
        if ("com.ipsmarx.dialer.custom.intent.action.SUCCESSFUL_BLIND_TRANSFER".equalsIgnoreCase(paramAnonymousIntent.getAction()))
        {
          if (SipService.getService() != null)
            SipService.getService().setCallTransferMode(false);
          DialerTab.this.cancelTransfer(true);
        }
        if ("com.ipsmarx.dialer.custom.intent.action.CALL_REJECTED".equalsIgnoreCase(paramAnonymousIntent.getAction()))
          DialerTab.this.showAlert(paramAnonymousContext.getString(2131230761));
      }
    };
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.ERROR_TRANSFER");
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.SUCCESSFUL_BLIND_TRANSFER");
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.CALL_REJECTED");
    registerReceiver(this.broadcastReceiver, localIntentFilter);
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

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    getMenuInflater().inflate(2131361793, paramMenu);
    return true;
  }

  public void onDestroy()
  {
    if ((this.network_alert != null) && (this.network_alert.isShowing()))
      this.network_alert.dismiss();
    unregisterReceiver(this.broadcastReceiver);
    super.onDestroy();
  }

  public boolean onLongClick(View paramView)
  {
    int i = paramView.getId();
    switch (i)
    {
    default:
    case 2131427366:
    case 2131427412:
    }
    while (true)
    {
      return true;
      if (mDisplayMap.containsKey(Integer.valueOf(i)))
      {
        appendDigit('+');
        continue;
        this.mtextDigits.setText("");
      }
    }
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131427422:
    }
    startActivity(new Intent(this, Settings.class));
    return true;
  }

  public void onPause()
  {
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    TextView localTextView = (TextView)findViewById(2131427389);
    if ((SipService.getService() != null) && (SipService.getService().RegStatus != null))
      localTextView.setText(SipService.getService().RegStatus);
    if ((SipService.getService() != null) && (SipService.uaCtrl != null) && (SipService.uaCtrl.getCallstate() != null));
    switch ($SWITCH_TABLE$com$ipsmarx$dialer$Consts$CallState()[SipService.uaCtrl.getCallstate().ordinal()])
    {
    default:
      PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("BalanceShow", "NULL");
      if (SipService.getService() != null)
      {
        if (!SipService.getService().isNetworkAllowed())
          break label311;
        startUpdatingBalance();
      }
      break;
    case 4:
    }
    while (true)
    {
      this.mtextDigits.setText(null);
      return;
      Intent localIntent1 = getIntent();
      if ((localIntent1 != null) && (localIntent1.getAction() != null) && (localIntent1.getAction().equalsIgnoreCase("com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER")))
      {
        this.currentCallId = localIntent1.getExtras().getLong("currentCallId");
        this.cancelTransferbtn = ((Button)getParent().getWindow().findViewById(2131427415));
        this.cancelTransferbtn.setVisibility(0);
        this.cancelTransferbtn.setOnClickListener(this);
        break;
      }
      String str = this.mtextDigits.getText().toString();
      Intent localIntent2 = new Intent();
      Bundle localBundle = new Bundle();
      localBundle.putInt("callstate", 2);
      localBundle.putString("number", str);
      localIntent2.putExtras(localBundle);
      localBundle.putLong("currentCallId", this.currentCallId);
      localIntent2.setClass(this, ConnectedCall.class);
      startActivity(localIntent2);
      break;
      label311: this.network_alert = new AlertDialog.Builder(this).create();
      this.network_alert.setTitle(2131230738);
      this.network_alert.setMessage("Selected Network Type is not permitted or is not connected");
      this.network_alert.setCancelable(true);
      this.network_alert.setButton("OK", new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface.cancel();
          DialerTab.this.networkAlertShown = true;
        }
      });
      if (!this.networkAlertShown)
      {
        this.network_alert.show();
        this.networkAlertShown = true;
      }
    }
  }

  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    isOnline();
  }

  public void onStart()
  {
    super.onStart();
  }

  public void onStop()
  {
    this.networkAlertShown = false;
    super.onStop();
  }

  public void setlastNumber(String paramString)
  {
    this._lastNumber = paramString;
  }

  public void showAlert(String paramString)
  {
    AlertDialog localAlertDialog = new AlertDialog.Builder(this).create();
    localAlertDialog.setCanceledOnTouchOutside(true);
    localAlertDialog.setCancelable(true);
    TextView localTextView = new TextView(this);
    localTextView.setTypeface(null, 1);
    localTextView.setTextSize(16.0F);
    localTextView.setText(paramString);
    localTextView.setGravity(1);
    localAlertDialog.setView(localTextView);
    localAlertDialog.setButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localAlertDialog.show();
  }

  protected void startUpdatingBalance()
  {
    new Thread()
    {
      public void run()
      {
        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(DialerTab.this.mContext);
        String str1 = localSharedPreferences.getString("useremail", "");
        String str2 = localSharedPreferences.getString("password", "");
        String str3 = SoftPhoneFactory.getInstance().getCurrentProduct().productIP();
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        HttpGet localHttpGet = new HttpGet(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder("http://").append(str3).append("/softswitch/WCF/CustomerAccountService.svc/CustomerInfo").toString())).append("?Email=").append(str1).toString())).append("&Password=").append(WebInfo.md5(str2)).toString() + "&ServiceTypeID=1");
        try
        {
          HttpEntity localHttpEntity = localDefaultHttpClient.execute(localHttpGet).getEntity();
          XMLReader localXMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
          LoginHandler localLoginHandler = new LoginHandler();
          localXMLReader.setContentHandler(localLoginHandler);
          localXMLReader.parse(DialerTab.this.retrieveInputStream(localHttpEntity));
          ParsedLoginDataSet localParsedLoginDataSet = localLoginHandler.getParsedLoginData();
          if (localParsedLoginDataSet.getResult().equals("Success"))
          {
            DialerTab.this.balanceValue = localParsedLoginDataSet.getExtractedBalance();
            DialerTab.this.mPreferences.edit().putString("AllowG729", localParsedLoginDataSet.getAllowG729()).commit();
            DialerTab.this.mHandler.post(DialerTab.this.UpdateBalance);
            return;
          }
          if (localParsedLoginDataSet.getResult().equals("Error: Invalid credential"))
          {
            SharedPreferences.Editor localEditor = localSharedPreferences.edit();
            localEditor.remove("signinPref");
            localEditor.commit();
            Intent localIntent = new Intent(DialerTab.this.getApplicationContext(), LoginActivity.class);
            DialerTab.this.startActivity(localIntent);
            DialerTab.this.finish();
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          Log.w("DialerTab class", "parser failed");
        }
      }
    }
    .start();
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.DialerTab
 * JD-Core Version:    0.6.2
 */