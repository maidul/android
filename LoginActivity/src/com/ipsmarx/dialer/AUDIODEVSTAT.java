package com.ipsmarx.dialer;

class AUDIODEVSTAT extends Command
{
  public void execute(UAControl.CallInfo paramCallInfo)
  {
    String str1 = paramCallInfo.msg.getAttribute("Name");
    String str2 = paramCallInfo.msg.getAttribute("EchoState");
    if ((str1 != null) && (str2 != null))
      SipService.getService().saveEchoState(str1, str2);
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.AUDIODEVSTAT
 * JD-Core Version:    0.6.2
 */