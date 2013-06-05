package com.ipsmarx.dialer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

class callWaitingHeldState extends CallServiceState
{
  public callWaitingHeldState(ConnectedCall paramConnectedCall)
  {
    this.cc = paramConnectedCall;
  }

  public void Layout()
  {
    String str = this.cc.getContactNameFromNumber(this.cc.myBundle.getString("number"));
    ((TextView)this.cc.findViewById(2131427341)).setText(str);
    Log.v("cw layoutstate:", "cw HeldState");
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.callWaitingHeldState
 * JD-Core Version:    0.6.2
 */