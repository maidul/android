package com.ipsmarx.dialer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

class callWaitingResumedState extends CallServiceState
{
  public callWaitingResumedState(ConnectedCall paramConnectedCall)
  {
    this.cc = paramConnectedCall;
  }

  public void Layout()
  {
    String str = this.cc.getContactNameFromNumber(this.cc.getCallwaitingBundle().getString("number"));
    ((TextView)this.cc.findViewById(2131427341)).setText(str);
    Log.v("cw layoutstate:", "cw ResumedState");
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.callWaitingResumedState
 * JD-Core Version:    0.6.2
 */