package com.ipsmarx.dialer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Consts
{
  public static final String ALLOW_G729_FLAG = "AllowG729";
  public static final String AUTH_ID = "AuthId";
  public static final String BRANDED_IP = "173.244.121.200";
  public static final String BrandedPhone = "Breeze";
  public static final int CALLLOG_TAB = 3;
  public static final String CALL_REJECTED = "com.ipsmarx.dialer.custom.intent.action.CALL_REJECTED";
  public static final String CALL_TRANSFER_MODE = "com.ipsmarx.dialer.custom.intent.action.CALL_TRANSFER_MODE";
  public static final String CALL_WAITING = "com.ipsmarx.dialer.custom.intent.action.callwaiting";
  public static final String CONNECT_TO_CALL = "com.ipsmarx.dialer.custom.intent.action.CONNECT_TO_CALL";
  public static final String DESTROY_ACTIVITY = "com.ipsmarx.dialer.custom.intent.action.DESTROY";
  public static final String DISMISS_DIALOG = "com.ipsmarx.dialer.custom.intent.action.DIMISS_DIALOG";
  public static final String DISP_NAME = "DispName";
  public static final String END_CALLWAITING = "com.ipsmarx.dialer.custom.intent.action.END_CALLWAITING";
  public static final String ERROR_TRANSFER = "com.ipsmarx.dialer.custom.intent.action.ERROR_TRANSFER";
  public static final String GenericPhone = "IPsmarx";
  public static final int INCALL_NOTIFICATION_TAG = 100;
  public static final int KEYPAD_TAB = 0;
  public static final String MAC_LOGIN = "MAC";
  public static final int MISSEDCALL_NOTIFICATION_TAG = 101;
  public static final String MISSED_CALL = "com.ipsmarx.dialer.custom.intent.action.MISSED_CALL";
  public static final int MOBILE = -1;
  public static final Set<String> MODEINCALL_SMALLMODELS_ALLOWED_LIST = new HashSet(Arrays.asList(new String[] { "samsung" }));
  public static final String NO_LOGIN = "NONE";
  public static final String PASSWORD = "Password";
  public static final String PRODUCT_INITIALIZED = "ProfileComplete";
  public static final String Phone = "Breeze";
  public static final int RSSI_THRESHOLD = 30;
  public static final String SHOW_CALLLOG = "com.ipsmarx.dialer.custom.intent.action.SHOW_CALLLOG";
  public static final String SHOW_TRANSFER = "com.ipsmarx.dialer.custom.intent.action.SHOW_TRANSFER";
  private static final String SIP_PORT = "SIPPort";
  public static final String STANDARD_LOGIN = "STANDARD";
  public static final String STOP_NETWORK = "Stopnetwork";
  public static final String SUCCESSFUL_BLIND_TRANSFER = "com.ipsmarx.dialer.custom.intent.action.SUCCESSFUL_BLIND_TRANSFER";
  public static final boolean SWITCH_WAKE_LOCK_OFF = false;
  public static final boolean SWITCH_WAKE_LOCK_ON = true;
  public static final String SandalsPhone = "Sandals";
  public static final String Sandles_IP = "72.252.252.202";
  public static final String ServerIP = "ServerIP";
  public static final String USER_NAME = "SipId";
  public static final String WAKELOCK_INTENT = "com.ipsmarx.dialer.custom.intent.action.cpuwakelock";
  public static final String defaultCodec = "defaultcodec";
  public static final String localCodec1 = "localcodec1";
  public static final String localCodec2 = "localcodec2";

  public static enum CallState
  {
    private int code;

    static
    {
      UA_STATE_INCALL = new CallState("UA_STATE_INCALL", 3, 3);
      UA_STATE_HOLD = new CallState("UA_STATE_HOLD", 4, 4);
      UA_STATE_RINGING = new CallState("UA_STATE_RINGING", 5, 5);
      UA_STATE_CALL_WAITING = new CallState("UA_STATE_CALL_WAITING", 6, 6);
      CallState[] arrayOfCallState = new CallState[7];
      arrayOfCallState[0] = UA_STATE_IDLE;
      arrayOfCallState[1] = UA_STATE_INCOMING_CALL;
      arrayOfCallState[2] = UA_STATE_OUTGOING_CALL;
      arrayOfCallState[3] = UA_STATE_INCALL;
      arrayOfCallState[4] = UA_STATE_HOLD;
      arrayOfCallState[5] = UA_STATE_RINGING;
      arrayOfCallState[6] = UA_STATE_CALL_WAITING;
    }

    private CallState(int arg3)
    {
      int j;
      this.code = j;
    }

    public int getCallStateCode()
    {
      return this.code;
    }
  }

  public static enum CallType
  {
    private int code;

    static
    {
      INCOMINGCALL = new CallType("INCOMINGCALL", 1, 1);
      OUTGOINGCALL = new CallType("OUTGOINGCALL", 2, 2);
      UNKNOWN = new CallType("UNKNOWN", 3, 3);
      CallType[] arrayOfCallType = new CallType[4];
      arrayOfCallType[0] = MISSEDCALL;
      arrayOfCallType[1] = INCOMINGCALL;
      arrayOfCallType[2] = OUTGOINGCALL;
      arrayOfCallType[3] = UNKNOWN;
    }

    private CallType(int arg3)
    {
      int j;
      this.code = j;
    }

    public CallType getEnumName(int paramInt)
    {
      CallType[] arrayOfCallType = values();
      int i = arrayOfCallType.length;
      for (int j = 0; ; j++)
      {
        Object localObject = null;
        if (j >= i);
        while (true)
        {
          if (localObject == null)
            localObject = UNKNOWN;
          return localObject;
          CallType localCallType = arrayOfCallType[j];
          if (paramInt != localCallType.getPriorityCode())
            break;
          localObject = localCallType;
        }
      }
    }

    public int getPriorityCode()
    {
      return this.code;
    }
  }

  public static enum CodecSelection
  {
    private int code;

    static
    {
      G729FIRST = new CodecSelection("G729FIRST", 1, 1);
      G729SECOND = new CodecSelection("G729SECOND", 2, 2);
      ALLG729 = new CodecSelection("ALLG729", 3, 3);
      CodecSelection[] arrayOfCodecSelection = new CodecSelection[4];
      arrayOfCodecSelection[0] = NOG729;
      arrayOfCodecSelection[1] = G729FIRST;
      arrayOfCodecSelection[2] = G729SECOND;
      arrayOfCodecSelection[3] = ALLG729;
    }

    private CodecSelection(int arg3)
    {
      int j;
      this.code = j;
    }

    public int getPriorityCode()
    {
      return this.code;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Consts
 * JD-Core Version:    0.6.2
 */