package com.ipsmarx.dialer;

import android.app.Activity;
import android.widget.TextView;

class RegisterCall extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    String str = paramCallInfo.msg.getAttribute("State");
    if (str != null)
    {
      if (!str.equalsIgnoreCase("registered"))
        break label71;
      if (SipService.getService() != null)
      {
        SipService.getService().setRegStatus("Registered");
        SipService.getService().setRegistered(true);
      }
    }
    while (true)
    {
      if (SipService.getActivityReference() != null)
        ((TextView)SipService.getActivityReference().findViewById(2131427389)).setText(SipService.getService().getRegStatus());
      return;
      label71: if (SipService.getService() != null)
      {
        SipService.getService().setRegStatus("Not Registered!");
        SipService.getService().setRegistered(false);
      }
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.RegisterCall
 * JD-Core Version:    0.6.2
 */