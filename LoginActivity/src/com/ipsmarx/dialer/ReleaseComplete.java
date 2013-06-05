package com.ipsmarx.dialer;

class ReleaseComplete extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    SipService.getService().setMissedCallNumber("");
    UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.CONNECT_TO_CALL", null);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ReleaseComplete
 * JD-Core Version:    0.6.2
 */