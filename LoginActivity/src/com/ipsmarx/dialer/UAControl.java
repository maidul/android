package com.ipsmarx.dialer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class UAControl extends AsyncTask<DataInputStream, Message, Long>
{
  static CallInfo callwaitingCallinfo;
  private final Map<String, Command> callScenarios;
  private Consts.CallState callstate;
  private SocketChannel channel;
  private DataInputStream input;
  private Message msg;
  private DataOutputStream output;

  // ERROR //
  public UAControl(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 29	android/os/AsyncTask:<init>	()V
    //   4: aload_0
    //   5: getstatic 34	com/ipsmarx/dialer/Consts$CallState:UA_STATE_IDLE	Lcom/ipsmarx/dialer/Consts$CallState;
    //   8: putfield 36	com/ipsmarx/dialer/UAControl:callstate	Lcom/ipsmarx/dialer/Consts$CallState;
    //   11: new 38	java/net/InetSocketAddress
    //   14: dup
    //   15: ldc 40
    //   17: iload_1
    //   18: invokespecial 43	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
    //   21: astore_2
    //   22: aload_0
    //   23: invokestatic 49	java/nio/channels/SocketChannel:open	()Ljava/nio/channels/SocketChannel;
    //   26: putfield 51	com/ipsmarx/dialer/UAControl:channel	Ljava/nio/channels/SocketChannel;
    //   29: aload_0
    //   30: getfield 51	com/ipsmarx/dialer/UAControl:channel	Ljava/nio/channels/SocketChannel;
    //   33: iconst_1
    //   34: invokevirtual 55	java/nio/channels/SocketChannel:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   37: pop
    //   38: aload_0
    //   39: getfield 51	com/ipsmarx/dialer/UAControl:channel	Ljava/nio/channels/SocketChannel;
    //   42: aload_2
    //   43: invokevirtual 59	java/nio/channels/SocketChannel:connect	(Ljava/net/SocketAddress;)Z
    //   46: pop
    //   47: aload_0
    //   48: new 61	java/io/DataInputStream
    //   51: dup
    //   52: aload_0
    //   53: getfield 51	com/ipsmarx/dialer/UAControl:channel	Ljava/nio/channels/SocketChannel;
    //   56: invokevirtual 65	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
    //   59: invokevirtual 71	java/net/Socket:getInputStream	()Ljava/io/InputStream;
    //   62: invokespecial 74	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   65: putfield 76	com/ipsmarx/dialer/UAControl:input	Ljava/io/DataInputStream;
    //   68: aload_0
    //   69: new 78	java/io/DataOutputStream
    //   72: dup
    //   73: aload_0
    //   74: getfield 51	com/ipsmarx/dialer/UAControl:channel	Ljava/nio/channels/SocketChannel;
    //   77: invokevirtual 65	java/nio/channels/SocketChannel:socket	()Ljava/net/Socket;
    //   80: invokevirtual 82	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
    //   83: invokespecial 85	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   86: putfield 87	com/ipsmarx/dialer/UAControl:output	Ljava/io/DataOutputStream;
    //   89: aload_0
    //   90: new 89	com/ipsmarx/dialer/UAControl$Message
    //   93: dup
    //   94: aload_0
    //   95: invokespecial 92	com/ipsmarx/dialer/UAControl$Message:<init>	(Lcom/ipsmarx/dialer/UAControl;)V
    //   98: putfield 94	com/ipsmarx/dialer/UAControl:msg	Lcom/ipsmarx/dialer/UAControl$Message;
    //   101: new 96	com/ipsmarx/dialer/UAControl$CallInfo
    //   104: dup
    //   105: aload_0
    //   106: invokespecial 97	com/ipsmarx/dialer/UAControl$CallInfo:<init>	(Lcom/ipsmarx/dialer/UAControl;)V
    //   109: putstatic 99	com/ipsmarx/dialer/UAControl:callwaitingCallinfo	Lcom/ipsmarx/dialer/UAControl$CallInfo;
    //   112: aload_0
    //   113: new 101	java/util/HashMap
    //   116: dup
    //   117: invokespecial 102	java/util/HashMap:<init>	()V
    //   120: putfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   123: aload_0
    //   124: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   127: ldc 106
    //   129: new 108	com/ipsmarx/dialer/InComingCall
    //   132: dup
    //   133: invokespecial 109	com/ipsmarx/dialer/InComingCall:<init>	()V
    //   136: invokeinterface 115 3 0
    //   141: pop
    //   142: aload_0
    //   143: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   146: ldc 117
    //   148: new 119	com/ipsmarx/dialer/OutGoingCall
    //   151: dup
    //   152: invokespecial 120	com/ipsmarx/dialer/OutGoingCall:<init>	()V
    //   155: invokeinterface 115 3 0
    //   160: pop
    //   161: aload_0
    //   162: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   165: ldc 122
    //   167: new 124	com/ipsmarx/dialer/RegisterCall
    //   170: dup
    //   171: invokespecial 125	com/ipsmarx/dialer/RegisterCall:<init>	()V
    //   174: invokeinterface 115 3 0
    //   179: pop
    //   180: aload_0
    //   181: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   184: ldc 127
    //   186: new 129	com/ipsmarx/dialer/AUDIODEVSTAT
    //   189: dup
    //   190: invokespecial 130	com/ipsmarx/dialer/AUDIODEVSTAT:<init>	()V
    //   193: invokeinterface 115 3 0
    //   198: pop
    //   199: aload_0
    //   200: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   203: ldc 132
    //   205: new 134	com/ipsmarx/dialer/Connected
    //   208: dup
    //   209: invokespecial 135	com/ipsmarx/dialer/Connected:<init>	()V
    //   212: invokeinterface 115 3 0
    //   217: pop
    //   218: aload_0
    //   219: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   222: ldc 137
    //   224: new 139	com/ipsmarx/dialer/CallStat
    //   227: dup
    //   228: invokespecial 140	com/ipsmarx/dialer/CallStat:<init>	()V
    //   231: invokeinterface 115 3 0
    //   236: pop
    //   237: aload_0
    //   238: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   241: ldc 142
    //   243: new 144	com/ipsmarx/dialer/Transferred
    //   246: dup
    //   247: invokespecial 145	com/ipsmarx/dialer/Transferred:<init>	()V
    //   250: invokeinterface 115 3 0
    //   255: pop
    //   256: aload_0
    //   257: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   260: ldc 147
    //   262: new 149	com/ipsmarx/dialer/TransferError
    //   265: dup
    //   266: invokespecial 150	com/ipsmarx/dialer/TransferError:<init>	()V
    //   269: invokeinterface 115 3 0
    //   274: pop
    //   275: aload_0
    //   276: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   279: ldc 152
    //   281: new 154	com/ipsmarx/dialer/Disconnected
    //   284: dup
    //   285: invokespecial 155	com/ipsmarx/dialer/Disconnected:<init>	()V
    //   288: invokeinterface 115 3 0
    //   293: pop
    //   294: aload_0
    //   295: getfield 104	com/ipsmarx/dialer/UAControl:callScenarios	Ljava/util/Map;
    //   298: ldc 157
    //   300: new 159	com/ipsmarx/dialer/ReleaseComplete
    //   303: dup
    //   304: invokespecial 160	com/ipsmarx/dialer/ReleaseComplete:<init>	()V
    //   307: invokeinterface 115 3 0
    //   312: pop
    //   313: iconst_1
    //   314: anewarray 61	java/io/DataInputStream
    //   317: astore 18
    //   319: aload 18
    //   321: iconst_0
    //   322: aload_0
    //   323: getfield 76	com/ipsmarx/dialer/UAControl:input	Ljava/io/DataInputStream;
    //   326: aastore
    //   327: aload_0
    //   328: aload 18
    //   330: invokevirtual 164	com/ipsmarx/dialer/UAControl:execute	([Ljava/lang/Object;)Landroid/os/AsyncTask;
    //   333: pop
    //   334: return
    //   335: astore 20
    //   337: aload 20
    //   339: invokevirtual 167	java/net/UnknownHostException:printStackTrace	()V
    //   342: goto -295 -> 47
    //   345: astore_3
    //   346: aload_3
    //   347: invokevirtual 168	java/io/IOException:printStackTrace	()V
    //   350: goto -303 -> 47
    //   353: astore 4
    //   355: ldc 170
    //   357: aload 4
    //   359: invokevirtual 174	java/io/IOException:toString	()Ljava/lang/String;
    //   362: invokestatic 180	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   365: pop
    //   366: goto -298 -> 68
    //   369: astore 6
    //   371: ldc 170
    //   373: aload 6
    //   375: invokevirtual 174	java/io/IOException:toString	()Ljava/lang/String;
    //   378: invokestatic 180	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   381: pop
    //   382: goto -293 -> 89
    //
    // Exception table:
    //   from	to	target	type
    //   11	47	335	java/net/UnknownHostException
    //   11	47	345	java/io/IOException
    //   47	68	353	java/io/IOException
    //   68	89	369	java/io/IOException
  }

  public static void broadcastMsg(String paramString, Bundle paramBundle)
  {
    Intent localIntent = new Intent(paramString);
    if (paramBundle != null)
      localIntent.putExtras(paramBundle);
    if (SipService.getActivityReference() != null)
      SipService.getActivityReference().sendBroadcast(localIntent);
  }

  protected Long doInBackground(DataInputStream[] paramArrayOfDataInputStream)
  {
    while (true)
      try
      {
        str = paramArrayOfDataInputStream[0].readLine();
        if (str == null)
          return Long.valueOf(0L);
        if (this.msg.type == null)
        {
          if (str.equals(""))
            continue;
          this.msg.parseMessageLine(str);
        }
      }
      catch (IOException localIOException)
      {
        String str;
        localIOException.printStackTrace();
        continue;
        if (str.equals(""))
        {
          Message[] arrayOfMessage = new Message[1];
          arrayOfMessage[0] = this.msg;
          publishProgress(arrayOfMessage);
          this.msg = new Message();
        }
        else
        {
          this.msg.parseAttributeLine(str);
        }
      }
  }

  public Consts.CallState getCallstate()
  {
    return this.callstate;
  }

  protected void onPostExecute(Long paramLong)
  {
  }

  protected void onProgressUpdate(Message[] paramArrayOfMessage)
  {
    int i = paramArrayOfMessage.length;
    for (int j = 0; ; j++)
    {
      if (j >= i)
        return;
      processMessage(paramArrayOfMessage[j]);
    }
  }

  public int processMessage(Message paramMessage)
  {
    Log.w("UAControl", "processMessage(): " + paramMessage.type);
    String str = paramMessage.type;
    CallInfo localCallInfo = new CallInfo();
    localCallInfo.msg = paramMessage;
    localCallInfo.call_state = getCallstate();
    if (this.callScenarios.containsKey(str))
      ((Command)this.callScenarios.get(str)).execute(localCallInfo);
    while (true)
    {
      return 0;
      Log.v("UAControl", "processMessage():" + paramMessage.type + "--->Unknown scenario/command sent to CommandQueue");
    }
  }

  public int sendMessage(Message paramMessage)
  {
    return paramMessage.write(this.output);
  }

  public void setCallstate(Consts.CallState paramCallState)
  {
    this.callstate = paramCallState;
  }

  public class CallInfo
    implements Cloneable
  {
    public boolean CALLWAITING;
    public String LogNumber;
    public Consts.CallState call_state = Consts.CallState.UA_STATE_IDLE;
    public Consts.CallType calltype;
    public String displayName;
    public boolean endcurrentAnswerSecond;
    public UAControl.Message msg;

    public CallInfo()
    {
    }

    public Object clone()
    {
      try
      {
        CallInfo localCallInfo = (CallInfo)super.clone();
        return localCallInfo;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        Log.d("CallInfo class", localCloneNotSupportedException.toString());
      }
      return null;
    }

    public void restore(Object paramObject)
    {
      Memento localMemento = (Memento)paramObject;
      this.msg = localMemento.memento_msg;
      this.call_state = localMemento.memento_call_state;
      this.LogNumber = localMemento.memento_LogNumber;
      this.CALLWAITING = localMemento.memento_CALLWAITING;
      this.endcurrentAnswerSecond = localMemento.memento_endcurrentAnswerSecond;
      this.displayName = localMemento.memento_displayName;
      this.calltype = localMemento.memento_calltype;
    }

    public Memento save()
    {
      return new Memento(this.msg, this.call_state, this.LogNumber, this.calltype, this.displayName, this.CALLWAITING);
    }

    private class Memento
    {
      public boolean memento_CALLWAITING;
      public String memento_LogNumber;
      public Consts.CallState memento_call_state;
      public Consts.CallType memento_calltype;
      public String memento_displayName;
      public boolean memento_endcurrentAnswerSecond;
      public UAControl.Message memento_msg;

      public Memento(UAControl.Message paramCallState, Consts.CallState paramString1, String paramCallType, Consts.CallType paramString2, String paramBoolean, boolean arg7)
      {
        this.memento_msg = paramCallState;
        this.memento_call_state = paramString1;
        this.memento_LogNumber = paramCallType;
        boolean bool;
        this.memento_CALLWAITING = bool;
        this.memento_endcurrentAnswerSecond = UAControl.CallInfo.this.endcurrentAnswerSecond;
        this.memento_displayName = paramBoolean;
        this.memento_calltype = paramString2;
      }
    }
  }

  public class Message
  {
    public Map<String, String> attributes;
    public long cid;
    public String type;

    protected Message()
    {
      this.attributes = new HashMap();
    }

    public Message(String paramLong, long arg3)
    {
      this.type = paramLong.toUpperCase();
      Object localObject;
      this.cid = localObject;
      this.attributes = new HashMap();
    }

    public void addAttribute(String paramString1, String paramString2)
    {
      this.attributes.put(paramString1, paramString2);
    }

    public long callId()
    {
      return this.cid;
    }

    public String getAttribute(String paramString)
    {
      return (String)this.attributes.get(paramString);
    }

    public String messageType()
    {
      return this.type;
    }

    protected boolean parseAttributeLine(String paramString)
    {
      Log.d("UAControl.Message", "parseAttributeLine(): " + paramString);
      int i = paramString.indexOf(':');
      if ((i == -1) || (i < 1))
        return false;
      String str1 = paramString.substring(0, i).trim();
      String str2 = paramString.substring(i + 1).trim();
      Log.d("UAControl.Message", "parseAttributeLine(): " + str1 + "=" + str2);
      this.attributes.put(str1, str2);
      return true;
    }

    protected boolean parseMessageLine(String paramString)
    {
      String[] arrayOfString = paramString.split("\\s");
      Log.d("UAControl.Message", "parseMessageLine(): " + paramString);
      if (arrayOfString.length != 3)
        return false;
      UAControl.this.msg.type = arrayOfString[0].toUpperCase();
      UAControl.this.msg.cid = Long.parseLong(arrayOfString[1], 16);
      Log.d("UAControl.Message", "parseMessageLine(): type=" + UAControl.this.msg.type + " id=" + Long.toString(UAControl.this.msg.cid, 16));
      return true;
    }

    protected int write(DataOutputStream paramDataOutputStream)
    {
      if (this.type == null)
        return -1;
      try
      {
        paramDataOutputStream.writeBytes(this.type + " " + Long.toString(this.cid, 16) + " CCP/1.0\r\n");
        localIterator = this.attributes.entrySet().iterator();
        if (localIterator.hasNext());
      }
      catch (IOException localIOException1)
      {
        try
        {
          while (true)
          {
            Iterator localIterator;
            paramDataOutputStream.writeBytes("\r\n");
            return 0;
            localIOException1 = localIOException1;
            localIOException1.printStackTrace();
            continue;
            Map.Entry localEntry = (Map.Entry)localIterator.next();
            try
            {
              paramDataOutputStream.writeBytes((String)localEntry.getKey() + ": " + (String)localEntry.getValue() + "\r\n");
            }
            catch (IOException localIOException2)
            {
              localIOException2.printStackTrace();
            }
          }
        }
        catch (IOException localIOException3)
        {
          while (true)
            localIOException3.printStackTrace();
        }
      }
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.UAControl
 * JD-Core Version:    0.6.2
 */