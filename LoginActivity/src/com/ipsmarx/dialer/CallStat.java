package com.ipsmarx.dialer;

import android.content.Context;

class CallStat extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    SignalStrengthMonitor localSignalStrengthMonitor = SignalStrengthMonitor.getInstance();
    SipService.getService().getContext().registerReceiver(localSignalStrengthMonitor.setRegistration(true), localSignalStrengthMonitor.getFilter());
    SipService.getService().setMissedCallNumber("");
    SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallStat
 * JD-Core Version:    0.6.2
 */