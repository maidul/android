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
import android.os.Bundle;
import android.widget.TextView;

public class BackGroundDialogs extends Activity
{
  public AlertDialog mAlert;
  private boolean mkeepWindowOnPauseEvent;
  public BroadcastReceiver receiver;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.ipsmarx.dialer.custom.intent.action.DIMISS_DIALOG");
    this.mkeepWindowOnPauseEvent = getIntent().getExtras().getBoolean("keepDuringOnPause", false);
    this.receiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (paramAnonymousContext == null);
        while (!paramAnonymousIntent.getAction().equals("com.ipsmarx.dialer.custom.intent.action.DIMISS_DIALOG"))
          return;
        BackGroundDialogs.this.finish();
      }
    };
    registerReceiver(this.receiver, localIntentFilter);
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mAlert != null)
    {
      if (this.mAlert.isShowing())
        this.mAlert.dismiss();
      unregisterReceiver(this.receiver);
    }
  }

  protected void onPause()
  {
    super.onPause();
    if ((this.mAlert != null) && (!this.mkeepWindowOnPauseEvent))
    {
      if (this.mAlert.isShowing())
        this.mAlert.dismiss();
      finish();
    }
  }

  protected void onResume()
  {
    super.onResume();
    String str = getIntent().getExtras().getString("android.intent.extra.TEXT");
    if (str.equalsIgnoreCase("DISMISS"))
      finish();
    while ((this.mAlert != null) && (this.mAlert.isShowing()))
      return;
    showAlert(str);
  }

  public void showAlert(String paramString)
  {
    this.mAlert = new AlertDialog.Builder(this).create();
    this.mAlert.setCancelable(false);
    TextView localTextView = new TextView(this);
    localTextView.setTypeface(null, 1);
    localTextView.setTextSize(16.0F);
    localTextView.setText(paramString);
    localTextView.setGravity(1);
    this.mAlert.setView(localTextView);
    this.mAlert.setButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        BackGroundDialogs.this.mAlert.dismiss();
        BackGroundDialogs.this.mkeepWindowOnPauseEvent = false;
        BackGroundDialogs.this.finish();
      }
    });
    this.mAlert.show();
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.BackGroundDialogs
 * JD-Core Version:    0.6.2
 */