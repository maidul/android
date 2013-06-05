package com.ipsmarx.dialer;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginError extends Activity
{
  Button button;

  protected boolean isOnline()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903053);
    TextView localTextView = (TextView)findViewById(2131427384);
    if (isOnline());
    for (String str = getIntent().getStringExtra("LoginMessage"); ; str = "Please check your device's network connection.")
    {
      localTextView.setText(str);
      this.button = ((Button)findViewById(2131427385));
      this.button.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          LoginError.this.finish();
        }
      });
      return;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.LoginError
 * JD-Core Version:    0.6.2
 */