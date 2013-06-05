package com.ipsmarx.dialer;

public class CallInfoCareTaker
{
  private static CallInfoCareTaker instance = null;
  static Object objMemento;

  public static CallInfoCareTaker getInstance()
  {
    if (instance == null)
      instance = new CallInfoCareTaker();
    return instance;
  }

  public void restoreState(UAControl.CallInfo paramCallInfo)
  {
    paramCallInfo.restore(objMemento);
  }

  public void saveState(UAControl.CallInfo paramCallInfo)
  {
    objMemento = paramCallInfo.save();
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallInfoCareTaker
 * JD-Core Version:    0.6.2
 */