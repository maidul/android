package com.ipsmarx.dialer;

class CallService
{
  private final CallServiceState CallServicestate = this.callNormalState;
  CallServiceState callNormalState;
  CallServiceState callWaitingHeldState;
  CallServiceState callWaitingResumedState;
  private String name;
  private CallServiceState state = this.callNormalState;

  public CallService(ConnectedCall paramConnectedCall)
  {
    this.callWaitingHeldState = new callWaitingHeldState(paramConnectedCall);
    this.callWaitingResumedState = new callWaitingResumedState(paramConnectedCall);
    this.callNormalState = new callNormalState(paramConnectedCall);
  }

  public CallServiceState getCallWaitingHeldState()
  {
    return this.callWaitingHeldState;
  }

  public CallServiceState getCallWaitingResumedState()
  {
    return this.callWaitingResumedState;
  }

  public CallServiceState getNormalState()
  {
    return this.callNormalState;
  }

  public CallServiceState getState()
  {
    return this.state;
  }

  public void setState(CallServiceState paramCallServiceState)
  {
    this.state = paramCallServiceState;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallService
 * JD-Core Version:    0.6.2
 */