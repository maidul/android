package com.ipsmarx.dialer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.util.EncodingUtils;

public class MyAccount extends Activity
  implements View.OnClickListener
{
  WebView engine;
  SharedPreferences sp;

  public static final String md5(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(paramString.getBytes());
      byte[] arrayOfByte = localMessageDigest.digest();
      StringBuffer localStringBuffer = new StringBuffer();
      int i = 0;
      if (i >= arrayOfByte.length)
        return localStringBuffer.toString();
      String str;
      for (Object localObject = Integer.toHexString(0xFF & arrayOfByte[i]); ; localObject = str)
      {
        if (((String)localObject).length() >= 2)
        {
          localStringBuffer.append((String)localObject);
          i++;
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
    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903055);
    CookieSyncManager.createInstance(this);
    CookieManager.getInstance().removeSessionCookie();
    CookieSyncManager.getInstance().sync();
    this.sp = PreferenceManager.getDefaultSharedPreferences(this);
    String str1 = this.sp.getString("useremail", "");
    String str2 = this.sp.getString("password", "");
    String str3 = new StringBuilder(String.valueOf("http://")).append(this.sp.getString("ServerIP", "http://www.ipsmarx.com")).toString() + "/softswitch/breeze/myaccount.aspx";
    String str4 = "Email=" + str1 + "&Password=" + md5(str2);
    this.engine = ((WebView)findViewById(2131427391));
    this.engine.setWebChromeClient(new WebChromeClient()
    {
      public boolean onJsAlert(WebView paramAnonymousWebView, String paramAnonymousString1, String paramAnonymousString2, JsResult paramAnonymousJsResult)
      {
        return super.onJsAlert(paramAnonymousWebView, paramAnonymousString1, paramAnonymousString2, paramAnonymousJsResult);
      }
    });
    WebSettings localWebSettings = this.engine.getSettings();
    localWebSettings.setJavaScriptEnabled(true);
    localWebSettings.setDomStorageEnabled(true);
    localWebSettings.setDatabasePath("/data/data/com.ipsmarx.dialer/dialer/databases");
    this.engine.clearCache(false);
    this.engine.postUrl(str3, EncodingUtils.getBytes(str4, "BASE64"));
  }

  public void setWebChromeClient(WebChromeClient paramWebChromeClient)
  {
  }

  public void setWebViewClient(WebViewClient paramWebViewClient)
  {
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.MyAccount
 * JD-Core Version:    0.6.2
 */