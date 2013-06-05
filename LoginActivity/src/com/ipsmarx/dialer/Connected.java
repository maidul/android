package com.ipsmarx.dialer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

class Connected extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("callstate", 3);
    localBundle.putLong("currentCallId", paramCallInfo.msg.cid);
    Intent localIntent = new Intent();
    localIntent.putExtras(localBundle);
    localIntent.setFlags(805306368);
    if (SipService.getService() != null)
    {
      localIntent.setClass(SipService.getService().getContext(), ConnectedCall.class);
      SipService.getService().getContext().startActivity(localIntent);
      SipService.getService().getContext().registerReceiver(SignalStrengthMonitor.getInstance().setRegistration(true), SignalStrengthMonitor.getInstance().getFilter());
    }
    SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Connected
 * JD-Core Version:    0.6.2
 */