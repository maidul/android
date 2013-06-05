package com.ipsmarx.dialer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

class InComingCall extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    if (PreferenceManager.getDefaultSharedPreferences(SipService.getService().getContext()).getBoolean("signin", false))
    {
      if ((SipService.uaCtrl.getCallstate() != Consts.CallState.UA_STATE_INCOMING_CALL) && (SipService.uaCtrl.getCallstate() != Consts.CallState.UA_STATE_RINGING))
        break label89;
      UAControl localUAControl1 = SipService.uaCtrl;
      localUAControl1.getClass();
      UAControl.Message localMessage1 = new UAControl.Message(localUAControl1, "DISCONNECT", paramCallInfo.msg.cid);
      SipService.rejectedCall = Boolean.valueOf(true);
      SipService.uaCtrl.sendMessage(localMessage1);
    }
    label89: 
    do
    {
      return;
      if (SipService.uaCtrl.getCallstate() != Consts.CallState.UA_STATE_INCALL)
      {
        Log.v("InComingCall 1st call", paramCallInfo.msg.cid);
        UAControl localUAControl4 = SipService.uaCtrl;
        localUAControl4.getClass();
        UAControl.Message localMessage4 = new UAControl.Message(localUAControl4, "RINGING", paramCallInfo.msg.cid);
        SipService.uaCtrl.sendMessage(localMessage4);
        SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCOMING_CALL);
        Bundle localBundle2 = new Bundle();
        localBundle2.putInt("callstate", 1);
        String str2 = paramCallInfo.msg.getAttribute("CallingNumber");
        SipService.getService().setMissedCallNumber(str2);
        localBundle2.putString("number", str2);
        localBundle2.putLong("currentCallId", paramCallInfo.msg.cid);
        localBundle2.putInt("CallType", Consts.CallType.INCOMINGCALL.getPriorityCode());
        Context localContext = SipService.getService().getContext();
        if (localContext != null)
        {
          Intent localIntent = new Intent();
          localIntent.setClass(localContext, ConnectedCall.class);
          localIntent.putExtras(localBundle2);
          localIntent.setFlags(805306372);
          localContext.startActivity(localIntent);
          paramCallInfo.LogNumber = str2;
          paramCallInfo.calltype = Consts.CallType.INCOMINGCALL;
          UAControl.callwaitingCallinfo = (UAControl.CallInfo)paramCallInfo.clone();
          CallInfoCareTaker.getInstance().saveState(UAControl.callwaitingCallinfo);
        }
      }
    }
    while (paramCallInfo.call_state != Consts.CallState.UA_STATE_INCALL);
    if (UAControl.callwaitingCallinfo.CALLWAITING)
    {
      SipService.rejectedCall = Boolean.valueOf(true);
      UAControl localUAControl3 = SipService.uaCtrl;
      localUAControl3.getClass();
      UAControl.Message localMessage3 = new UAControl.Message(localUAControl3, "DISCONNECT", paramCallInfo.msg.cid);
      SipService.uaCtrl.sendMessage(localMessage3);
      return;
    }
    Log.v("InComingCall 2nd call", paramCallInfo.msg.cid);
    UAControl localUAControl2 = SipService.uaCtrl;
    localUAControl2.getClass();
    UAControl.Message localMessage2 = new UAControl.Message(localUAControl2, "RINGING", paramCallInfo.msg.cid);
    SipService.uaCtrl.sendMessage(localMessage2);
    paramCallInfo.CALLWAITING = true;
    paramCallInfo.call_state = Consts.CallState.UA_STATE_INCOMING_CALL;
    SipService.uaCtrl.setCallstate(Consts.CallState.UA_STATE_INCOMING_CALL);
    SipService.getService().setMissedCallNumber(paramCallInfo.msg.getAttribute("CallingNumber"));
    Bundle localBundle1 = new Bundle();
    String str1 = paramCallInfo.msg.getAttribute("CallingNumber");
    localBundle1.putString("number", str1);
    localBundle1.putLong("currentCallId", paramCallInfo.msg.cid);
    localBundle1.putInt("callstate", Consts.CallState.UA_STATE_CALL_WAITING.getCallStateCode());
    localBundle1.putLong("previousCallId", UAControl.callwaitingCallinfo.msg.cid);
    UAControl.callwaitingCallinfo.CALLWAITING = true;
    paramCallInfo.LogNumber = str1;
    paramCallInfo.calltype = Consts.CallType.INCOMINGCALL;
    CallInfoCareTaker.getInstance().saveState(UAControl.callwaitingCallinfo);
    UAControl.callwaitingCallinfo = (UAControl.CallInfo)paramCallInfo.clone();
    UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.callwaiting", localBundle1);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.InComingCall
 * JD-Core Version:    0.6.2
 */