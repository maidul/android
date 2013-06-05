package com.ipsmarx.dialer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

class OutGoingCall extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    Log.v("OutgoingCall call", paramCallInfo.msg.cid);
    paramCallInfo.LogNumber = SipService.getService().getOutgoingNumber();
    paramCallInfo.calltype = Consts.CallType.OUTGOINGCALL;
    UAControl.callwaitingCallinfo = (UAControl.CallInfo)paramCallInfo.clone();
    CallInfoCareTaker.getInstance().saveState(UAControl.callwaitingCallinfo);
    Bundle localBundle = new Bundle();
    if (paramCallInfo.msg.getAttribute("StartMedia") == null);
    for (boolean bool = true; ; bool = false)
    {
      Boolean localBoolean = Boolean.valueOf(bool);
      if (localBoolean.booleanValue())
        localBundle.putBoolean("StartMedia", localBoolean.booleanValue());
      SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_RINGING);
      localBundle.putInt("CallType", Consts.CallType.OUTGOINGCALL.getPriorityCode());
      localBundle.putInt("callstate", Consts.CallState.UA_STATE_RINGING.getCallStateCode());
      localBundle.putLong("currentCallId", paramCallInfo.msg.cid);
      Intent localIntent = new Intent();
      localIntent.setClass(SipService.getService().getContext(), ConnectedCall.class);
      localIntent.putExtras(localBundle);
      localIntent.setFlags(805306368);
      SipService.getService().getContext().startActivity(localIntent);
      return;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.OutGoingCall
 * JD-Core Version:    0.6.2
 */