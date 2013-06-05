package com.ipsmarx.dialer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

class Disconnected extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    if (SipService.rejectedCall.booleanValue())
    {
      SipService.rejectedCall = Boolean.valueOf(false);
      return;
    }
    SipService localSipService = SipService.getService();
    Context localContext = null;
    if (localSipService != null)
      localContext = SipService.getService().getContext();
    UAControl localUAControl1 = SipService.uaCtrl;
    localUAControl1.getClass();
    UAControl.Message localMessage1 = new UAControl.Message(localUAControl1, "RELEASE", paramCallInfo.msg.cid);
    SipService.uaCtrl.sendMessage(localMessage1);
    if ((SignalStrengthMonitor.getInstance().getRegistration()) && (!UAControl.callwaitingCallinfo.CALLWAITING) && (localContext != null))
      localContext.unregisterReceiver(SignalStrengthMonitor.getInstance().setRegistration(false));
    if (paramCallInfo.call_state == Consts.CallState.UA_STATE_INCOMING_CALL)
    {
      Bundle localBundle1 = new Bundle();
      localBundle1.putString("CallingNumber", SipService.getService().getMissedCallNumber());
      SipService.getService().setMissedCallNumber(paramCallInfo.msg.getAttribute("CallingNumber"));
      UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.MISSED_CALL", localBundle1);
    }
    if ((SipService.uaCtrl.getCallstate() != Consts.CallState.UA_STATE_IDLE) && (!UAControl.callwaitingCallinfo.CALLWAITING) && (localContext != null))
    {
      SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_IDLE);
      Bundle localBundle3 = new Bundle();
      localBundle3.putInt("callstate", 0);
      localBundle3.putLong("currentCallId", paramCallInfo.msg.cid);
      Intent localIntent = new Intent();
      localIntent.setClass(localContext, ConnectedCall.class);
      localIntent.putExtras(localBundle3);
      localIntent.setFlags(805306368);
      localContext.startActivity(localIntent);
    }
    if (paramCallInfo.call_state == Consts.CallState.UA_STATE_OUTGOING_CALL)
      UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.CALL_REJECTED", null);
    if (!UAControl.callwaitingCallinfo.CALLWAITING)
      SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_IDLE);
    while (true)
    {
      if (SipService.getService() != null)
        SipService.getService().setCallTransferMode(false);
      if (UAControl.callwaitingCallinfo.CALLWAITING)
      {
        Bundle localBundle2 = new Bundle();
        localBundle2.putLong("disconnectedCallId", paramCallInfo.msg.cid);
        UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.END_CALLWAITING", localBundle2);
      }
      UAControl.callwaitingCallinfo.CALLWAITING = false;
      if (!UAControl.callwaitingCallinfo.endcurrentAnswerSecond)
        break;
      UAControl.callwaitingCallinfo.endcurrentAnswerSecond = false;
      UAControl localUAControl2 = SipService.uaCtrl;
      localUAControl2.getClass();
      UAControl.Message localMessage2 = new UAControl.Message(localUAControl2, "CONNECT", UAControl.callwaitingCallinfo.msg.cid);
      SipService.uaCtrl.sendMessage(localMessage2);
      return;
      SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCALL);
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Disconnected
 * JD-Core Version:    0.6.2
 */