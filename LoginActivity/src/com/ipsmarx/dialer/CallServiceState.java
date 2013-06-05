package com.ipsmarx.dialer;

public abstract class CallServiceState
{
  ConnectedCall cc;

  void Layout()
  {
  }

  public CallServiceState swapTimers()
  {
    Long localLong1 = Long.valueOf(this.cc.getCallWaitingCallTime());
    Long localLong2 = Long.valueOf(this.cc.getCurrentCallTime());
    this.cc.setCallWaitingCallTime(localLong2.longValue());
    this.cc.setCurrentCallTime(localLong1.longValue());
    return this;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallServiceState
 * JD-Core Version:    0.6.2
 */