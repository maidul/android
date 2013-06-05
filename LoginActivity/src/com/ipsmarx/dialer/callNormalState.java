package com.ipsmarx.dialer;

import android.view.Window;
import android.widget.Button;

class callNormalState extends CallServiceState
{
  public callNormalState(ConnectedCall paramConnectedCall)
  {
    this.cc = paramConnectedCall;
  }

  public void Layout()
  {
    this.cc.getWindow().setFeatureInt(7, 2130903061);
    Button localButton = (Button)this.cc.getWindow().findViewById(2131427405);
    localButton.setVisibility(8);
    localButton.setOnClickListener(null);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.callNormalState
 * JD-Core Version:    0.6.2
 */