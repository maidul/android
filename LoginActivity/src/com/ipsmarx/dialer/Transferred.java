package com.ipsmarx.dialer;

class Transferred extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    UAControl.broadcastMsg("com.ipsmarx.dialer.custom.intent.action.SUCCESSFUL_BLIND_TRANSFER", null);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Transferred
 * JD-Core Version:    0.6.2
 */