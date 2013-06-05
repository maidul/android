package com.ipsmarx.dialer;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings.System;
import android.util.Log;

public class Ringer
{
  private static final int PAUSE_LENGTH = 1000;
  private static final String THIS_FILE = "Ringer";
  private static final int VIBRATE_LENGTH = 1000;
  private static Ringer instance = null;
  Context context;
  RingerThread ringerThread;
  Ringtone ringtone = null;
  Vibrator vibrator;
  VibratorThread vibratorThread;

  private Ringer(Context paramContext)
  {
    this.context = paramContext;
    this.vibrator = ((Vibrator)this.context.getSystemService("vibrator"));
  }

  public static Ringer getInstance(Context paramContext)
  {
    if (instance == null)
      instance = new Ringer(paramContext);
    return instance;
  }

  public boolean isRinging()
  {
    return (this.ringerThread != null) || (this.vibratorThread != null);
  }

  public void ring()
  {
    Log.d("Ringer", "==> ring() called...");
    AudioManager localAudioManager;
    try
    {
      localAudioManager = (AudioManager)this.context.getSystemService("audio");
      int i = localAudioManager.getRingerMode();
      if (i == 0)
      {
        Log.d("Ringer", "skipping ring and vibrate because profile is Silent");
        return;
      }
      int j = localAudioManager.getVibrateSetting(0);
      Log.d("Ringer", "v=" + j + " rm=" + i);
      if ((this.vibratorThread == null) && ((j == 1) || (i == 1)))
      {
        this.vibratorThread = new VibratorThread(null);
        Log.d("Ringer", "Starting vibrator...");
        this.vibratorThread.start();
      }
      if (i == 1)
      {
        Log.d("Ringer", "skipping ring because profile is Vibrate");
        return;
      }
    }
    finally
    {
    }
    if (localAudioManager.getStreamVolume(2) == 0)
    {
      Log.d("Ringer", "skipping ring because volume is zero");
      return;
    }
    Uri localUri = Settings.System.DEFAULT_RINGTONE_URI;
    this.ringtone = RingtoneManager.getRingtone(this.context, localUri);
    if (this.ringtone == null)
    {
      Log.d("Ringer", "No ringtone available - do not ring");
      return;
    }
    Log.d("Ringer", "Starting ring with " + this.ringtone.getTitle(this.context));
    if (this.ringerThread == null)
    {
      this.ringerThread = new RingerThread(null);
      Log.d("Ringer", "Starting ringer...");
      localAudioManager.setMode(1);
      this.ringerThread.start();
    }
  }

  // ERROR //
  public void stopRing()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc 11
    //   4: ldc 156
    //   6: invokestatic 66	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   9: pop
    //   10: aload_0
    //   11: getfield 57	com/ipsmarx/dialer/Ringer:vibratorThread	Lcom/ipsmarx/dialer/Ringer$VibratorThread;
    //   14: ifnull +25 -> 39
    //   17: aload_0
    //   18: getfield 57	com/ipsmarx/dialer/Ringer:vibratorThread	Lcom/ipsmarx/dialer/Ringer$VibratorThread;
    //   21: invokevirtual 159	com/ipsmarx/dialer/Ringer$VibratorThread:interrupt	()V
    //   24: aload_0
    //   25: getfield 57	com/ipsmarx/dialer/Ringer:vibratorThread	Lcom/ipsmarx/dialer/Ringer$VibratorThread;
    //   28: ldc2_w 160
    //   31: invokevirtual 165	com/ipsmarx/dialer/Ringer$VibratorThread:join	(J)V
    //   34: aload_0
    //   35: aconst_null
    //   36: putfield 57	com/ipsmarx/dialer/Ringer:vibratorThread	Lcom/ipsmarx/dialer/Ringer$VibratorThread;
    //   39: aload_0
    //   40: getfield 55	com/ipsmarx/dialer/Ringer:ringerThread	Lcom/ipsmarx/dialer/Ringer$RingerThread;
    //   43: ifnull +25 -> 68
    //   46: aload_0
    //   47: getfield 55	com/ipsmarx/dialer/Ringer:ringerThread	Lcom/ipsmarx/dialer/Ringer$RingerThread;
    //   50: invokevirtual 166	com/ipsmarx/dialer/Ringer$RingerThread:interrupt	()V
    //   53: aload_0
    //   54: getfield 55	com/ipsmarx/dialer/Ringer:ringerThread	Lcom/ipsmarx/dialer/Ringer$RingerThread;
    //   57: ldc2_w 160
    //   60: invokevirtual 167	com/ipsmarx/dialer/Ringer$RingerThread:join	(J)V
    //   63: aload_0
    //   64: aconst_null
    //   65: putfield 55	com/ipsmarx/dialer/Ringer:ringerThread	Lcom/ipsmarx/dialer/Ringer$RingerThread;
    //   68: aload_0
    //   69: monitorexit
    //   70: return
    //   71: astore_1
    //   72: aload_0
    //   73: monitorexit
    //   74: aload_1
    //   75: athrow
    //   76: astore_3
    //   77: goto -14 -> 63
    //   80: astore 4
    //   82: goto -48 -> 34
    //
    // Exception table:
    //   from	to	target	type
    //   2	24	71	finally
    //   24	34	71	finally
    //   34	39	71	finally
    //   39	53	71	finally
    //   53	63	71	finally
    //   63	68	71	finally
    //   68	70	71	finally
    //   72	74	71	finally
    //   53	63	76	java/lang/InterruptedException
    //   24	34	80	java/lang/InterruptedException
  }

  private class RingerThread extends Thread
  {
    private RingerThread()
    {
    }

    public void run()
    {
      try
      {
        Ringer.this.ringtone.play();
        while (Ringer.this.ringtone.isPlaying())
          Thread.sleep(100L);
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.d("Ringer", "Ringer thread interrupt");
        Ringer.this.ringtone.stop();
        Log.d("Ringer", "Ringer thread exiting");
        return;
      }
      finally
      {
        Ringer.this.ringtone.stop();
      }
    }
  }

  private class VibratorThread extends Thread
  {
    private VibratorThread()
    {
    }

    public void run()
    {
      try
      {
        while (true)
        {
          Ringer.this.vibrator.vibrate(1000L);
          Thread.sleep(2000L);
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.d("Ringer", "Vibrator thread interrupt");
        Ringer.this.vibrator.cancel();
        Log.d("Ringer", "Vibrator thread exiting");
        return;
      }
      finally
      {
        Ringer.this.vibrator.cancel();
      }
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Ringer
 * JD-Core Version:    0.6.2
 */