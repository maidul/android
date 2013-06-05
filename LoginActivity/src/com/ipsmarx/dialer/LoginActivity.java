package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;
import com.ipsmarx.dialer.VisitorPatterns.PreferenceManagementVisitor;
import com.ipsmarx.dialer.VisitorPatterns.Visitable;
import com.ipsmarx.dialer.VisitorPatterns.Visitor;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class LoginActivity extends Activity
  implements View.OnClickListener, Visitable
{
  private static final String TAG = "Login";
  ProgressDialog dialog;
  private final Handler handler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      String str = (String)paramAnonymousMessage.obj;
      if (str.equals("SUCCESS"))
      {
        LoginActivity.this.removeDialog(0);
        SharedPreferences.Editor localEditor = LoginActivity.this.mPreferences.edit();
        localEditor.putBoolean("signin", true);
        localEditor.commit();
        Intent localIntent2 = new Intent(LoginActivity.this.getApplicationContext(), DialerApp.class);
        LoginActivity.this.startActivity(localIntent2);
        LoginActivity.this.finish();
      }
      if ((!str.equals("FAIL")) || (str.equals(LoginActivity.this.getApplicationContext().getString(2131230762))))
      {
        Intent localIntent1 = new Intent(LoginActivity.this.getApplicationContext(), LoginError.class);
        localIntent1.putExtra("LoginMessage", "No call license type\nassociated with this account");
        LoginActivity.this.startActivity(localIntent1);
        LoginActivity.this.removeDialog(0);
      }
    }
  };
  String loginmessage = null;
  private SharedPreferences mPreferences;
  PreferenceManagementVisitor preferencemgrVisitor;
  Button signin;
  Thread t;

  private final boolean checkLoginInfo()
  {
    boolean bool1 = this.mPreferences.contains("SipId");
    boolean bool2 = this.mPreferences.contains("Password");
    boolean bool3 = this.mPreferences.contains("ServerIP");
    return ((bool1) || (bool2)) && (bool3);
  }

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

  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visit(this);
  }

  public String getMacAddress()
  {
    return ((WifiManager)getSystemService("wifi")).getConnectionInfo().getMacAddress();
  }

  public boolean isWiFiUp()
  {
    return ((ConnectivityManager)getSystemService("connectivity")).getNetworkInfo(1).isConnectedOrConnecting();
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
      return;
    case 2131427381:
    }
    showDialog(0);
    this.t = new Thread()
    {
      public void run()
      {
        LoginActivity.this.tryLogin();
      }
    };
    this.t.start();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    SoftPhoneFactory localSoftPhoneFactory = SoftPhoneFactory.getInstance();
    Log.v("david", Build.VERSION.RELEASE);
    SoftPhone localSoftPhone = localSoftPhoneFactory.getSoftPhone("Breeze", getApplicationContext());
    localSoftPhoneFactory.setCurrentProduct(localSoftPhone.productName(), this);
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    Boolean localBoolean = Boolean.valueOf(this.mPreferences.getBoolean("signin", false));
    this.mPreferences.edit().putString("productType", localSoftPhone.productName()).commit();
    if ((localSoftPhone != null) && (!localSoftPhone.hasLoginScreen()) && (localSoftPhone.getLoginType().equals("NONE")) && (localSoftPhone.hasConfigurationMenu()))
    {
      if ((localSoftPhone.isProductInitialized()) && (localBoolean.booleanValue()))
      {
        startActivity(new Intent(getApplicationContext(), DialerApp.class));
        finish();
      }
    }
    else
    {
      if (((0xF & getResources().getConfiguration().screenLayout) == 3) || ((0xF & getResources().getConfiguration().screenLayout) == 4))
        setRequestedOrientation(0);
      setContentView(2130903052);
      if ((!localSoftPhone.getLoginType().equals("MAC")) || (this.mPreferences.getBoolean("signinPref", false)))
        break label400;
      if (isWiFiUp())
        break label281;
      showAlert("Please Ensure A WiFi Connection is Present", false);
      finish();
    }
    label281: EditText localEditText1;
    label400: EditText localEditText2;
    CheckBox localCheckBox;
    do
    {
      return;
      Intent localIntent = new Intent(this, Settings.class);
      startActivity(localIntent);
      break;
      String str2 = getMacAddress();
      String str3 = new Encryption().HMAC(str2).replaceAll("\\W", "");
      String str4 = str3 + "@sandals.com";
      String str5 = str3.substring(0, 17);
      EditText localEditText3 = (EditText)findViewById(2131427379);
      EditText localEditText4 = (EditText)findViewById(2131427380);
      localEditText3.setText(str4);
      localEditText4.setText(str5);
      Thread local2 = new Thread()
      {
        public void run()
        {
          LoginActivity.this.tryLogin();
        }
      };
      local2.start();
      finish();
      TextView localTextView = (TextView)findViewById(2131427383);
      Pattern localPattern = Pattern.compile("");
      String str1 = SoftPhoneFactory.getInstance().getCurrentProduct().productIP();
      Linkify.addLinks(localTextView, localPattern, "http://" + str1 + "/softswitch/breeze/signup.aspx");
      localEditText1 = (EditText)findViewById(2131427379);
      localEditText2 = (EditText)findViewById(2131427380);
      this.signin = ((Button)findViewById(2131427381));
      this.signin.setOnClickListener(this);
      localCheckBox = (CheckBox)findViewById(2131427382);
    }
    while (!this.mPreferences.getBoolean("signinPref", false));
    localCheckBox.setChecked(true);
    localEditText1.setText(this.mPreferences.getString("useremail", ""));
    localEditText2.setText(this.mPreferences.getString("password", ""));
    startActivity(new Intent(getApplicationContext(), DialerApp.class));
    finish();
  }

  protected Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 0:
    }
    this.dialog = new ProgressDialog(this);
    this.dialog.setMessage("Please wait while connecting...");
    this.dialog.setIndeterminate(true);
    this.dialog.setCancelable(true);
    return this.dialog;
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

  public void tryLogin()
  {
    Log.v("Login", "Trying to Login");
    boolean bool = ((CheckBox)findViewById(2131427382)).isChecked();
    EditText localEditText1 = (EditText)findViewById(2131427379);
    EditText localEditText2 = (EditText)findViewById(2131427380);
    String str1 = localEditText1.getText().toString();
    String str2 = localEditText2.getText().toString();
    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
    String str3 = SoftPhoneFactory.getInstance().getCurrentProduct().productIP();
    String str4 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder("http://").append(str3).append("/softswitch/WCF/CustomerAccountService.svc/CustomerInfo").toString())).append("?Email=").append(str1).toString())).append("&Password=").append(WebInfo.md5(str2)).toString() + "&ServiceTypeID=1";
    ParsedLoginDataSet localParsedLoginDataSet;
    try
    {
      HttpResponse localHttpResponse = localDefaultHttpClient.execute(new HttpGet(str4));
      Log.v("Login", localHttpResponse.getStatusLine().toString());
      HttpEntity localHttpEntity = localHttpResponse.getEntity();
      Log.v("Login", "Set response to responseEntity");
      XMLReader localXMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
      LoginHandler localLoginHandler = new LoginHandler();
      localXMLReader.setContentHandler(localLoginHandler);
      localXMLReader.parse(retrieveInputStream(localHttpEntity));
      localParsedLoginDataSet = localLoginHandler.getParsedLoginData();
      if (localParsedLoginDataSet.getResult().equals("Success"))
      {
        PreferenceManagementVisitor localPreferenceManagementVisitor = new PreferenceManagementVisitor(localParsedLoginDataSet, str1, str2, Boolean.valueOf(bool));
        this.preferencemgrVisitor = localPreferenceManagementVisitor;
        accept(this.preferencemgrVisitor);
        Message localMessage1 = new Message();
        if ((this.mPreferences.getString("CallLicenseType", "").equalsIgnoreCase("None")) && (localParsedLoginDataSet.getServiceType().equalsIgnoreCase("Residential")));
        for (localMessage1.obj = getString(2131230762); ; localMessage1.obj = "SUCCESS")
        {
          this.handler.sendMessage(localMessage1);
          return;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      Log.v("CLT", localThrowable.toString() + "-->exception" + localThrowable.fillInStackTrace());
      Intent localIntent1 = new Intent(getApplicationContext(), LoginError.class);
      localIntent1.putExtra("LoginMessage", "Unable to login");
      startActivity(localIntent1);
      removeDialog(0);
      return;
    }
    if (localParsedLoginDataSet.getResult().equals(getString(2131230741)))
    {
      Message localMessage2 = new Message();
      localMessage2.obj = "FAIL";
      this.handler.sendMessage(localMessage2);
      Intent localIntent2 = new Intent(getApplicationContext(), LoginError.class);
      localIntent2.putExtra("LoginMessage", localParsedLoginDataSet.getResult());
      startActivity(localIntent2);
      removeDialog(0);
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.LoginActivity
 * JD-Core Version:    0.6.2
 */