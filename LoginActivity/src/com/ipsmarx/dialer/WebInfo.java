package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.util.EncodingUtils;

public class WebInfo extends Activity
  implements View.OnClickListener
{
  WebView engine;
  private volatile WebChromeClient mWebChromeClient;
  private volatile WebViewClient mWebViewClient;
  SharedPreferences sp;

  public static final String md5(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(paramString.getBytes());
      byte[] arrayOfByte = localMessageDigest.digest();
      StringBuffer localStringBuffer = new StringBuffer();
      int i = arrayOfByte.length;
      int j = 0;
      if (j >= i)
        return localStringBuffer.toString();
      String str;
      for (Object localObject = Integer.toHexString(0xFF & arrayOfByte[j]); ; localObject = str)
      {
        if (((String)localObject).length() >= 2)
        {
          localStringBuffer.append((String)localObject);
          j++;
          break;
        }
        str = "0" + (String)localObject;
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
    }
    return "";
  }

  public int convertDpToPixel(float paramFloat, Context paramContext)
  {
    return Math.round(paramFloat * (paramContext.getResources().getDisplayMetrics().densityDpi / 160.0F));
  }

  public void onBackPressed()
  {
    if ((this.engine.isFocused()) && (this.engine.canGoBack()))
    {
      this.engine.goBack();
      return;
    }
    finish();
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
      return;
    case 2131427414:
    }
    SharedPreferences.Editor localEditor = this.sp.edit();
    localEditor.remove("signinPref");
    localEditor.putBoolean("signin", false);
    localEditor.commit();
    ((NotificationManager)getApplicationContext().getSystemService("notification")).cancelAll();
    SipService.setWakeLockMsg(false);
    if (SipService.getService().isNetworkAllowed())
      SipService.getService().stopRegistration(null);
    while (true)
    {
      SoftPhone localSoftPhone = SoftPhoneFactory.getInstance().getCurrentProduct();
      SipService.getService().stopSelf();
      if (!localSoftPhone.productName().equalsIgnoreCase("IPsmarx"))
        break;
      ((DialerApp)getParent()).getTabHost().setCurrentTab(0);
      return;
      SipService.getService().stopRegistration("Stopnetwork");
    }
    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903065);
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    String str1 = this.sp.getString("useremail", "");
    String str2 = this.sp.getString("password", "");
    String str3 = new StringBuilder(String.valueOf("http://")).append(this.sp.getString("ServerIP", "http://www.ipsmarx.com")).toString() + "/softswitch/breeze/myaccount.aspx";
    String str4 = "Email=" + str1 + "&Password=" + md5(str2);
    this.engine = ((WebView)findViewById(2131427413));
    this.engine.setWebViewClient(new WebViewClient()
    {
      public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        paramAnonymousWebView.loadUrl(paramAnonymousString);
        return true;
      }
    });
    this.engine.getSettings().setJavaScriptEnabled(true);
    this.engine.postUrl(str3, EncodingUtils.getBytes(str4, "BASE64"));
    if (SoftPhoneFactory.getInstance().getCurrentProduct().hasSignOutBtn())
    {
      View localView = findViewById(2131427414);
      localView.setVisibility(0);
      localView.setOnClickListener(this);
      RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.engine.getLayoutParams();
      localLayoutParams.setMargins(0, 0, 0, convertDpToPixel(40.0F, this));
      this.engine.setLayoutParams(localLayoutParams);
    }
  }

  public void setWebChromeClient(WebChromeClient paramWebChromeClient)
  {
    this.mWebChromeClient = paramWebChromeClient;
  }

  public void setWebViewClient(WebViewClient paramWebViewClient)
  {
    this.mWebViewClient = paramWebViewClient;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.WebInfo
 * JD-Core Version:    0.6.2
 */