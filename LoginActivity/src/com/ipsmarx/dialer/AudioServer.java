package com.ipsmarx.dialer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class AudioServer
  implements Runnable, Thread.UncaughtExceptionHandler
{
  private transient AudioManager audioManager;
  private transient Context context;
  private int previousMode;
  private transient Thread serverThread;
  private transient ServerSocket sock;

  public AudioServer(AudioManager paramAudioManager, Context paramContext)
  {
    this.audioManager = paramAudioManager;
    this.context = paramContext;
    try
    {
      this.sock = new ServerSocket(0);
      return;
    }
    catch (IOException localIOException)
    {
      Log.d("AudioServer2", "socket IO: " + localIOException.getMessage());
      localIOException.printStackTrace();
    }
  }

  public int getServerPort()
  {
    return this.sock.getLocalPort();
  }

  public void run()
  {
    Object localObject = null;
    AudioSession localAudioSession = null;
    Log.d("AudioServer", "run()");
    try
    {
      while (true)
      {
        Socket localSocket = this.sock.accept();
        localObject = localSocket;
        if (localObject != null)
        {
          Log.d("AudioServer", "new connection");
          if (localAudioSession != null)
          {
            this.audioManager.setMode(this.previousMode);
            localAudioSession.stop();
          }
          localAudioSession = new AudioSession(localObject);
          this.previousMode = this.audioManager.getMode();
          if (((0xF & this.context.getResources().getConfiguration().screenLayout) == 1) && (Consts.MODEINCALL_SMALLMODELS_ALLOWED_LIST.contains(Build.BRAND.toLowerCase())))
            this.audioManager.setMode(2);
          if (Build.VERSION.SDK_INT >= 17)
            this.audioManager.setMode(3);
          localAudioSession.start();
        }
      }
    }
    catch (IOException localIOException)
    {
      while (true)
        Log.d("AudioServer3", "socket IO: " + localIOException.getMessage());
    }
  }

  public void start()
  {
    if (this.serverThread == null)
    {
      this.serverThread = new Thread(this);
      this.serverThread.start();
    }
  }

  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    Log.v("AudioServer1: ", paramThrowable.getMessage());
  }

  private static class AudioSession
  {
    private Thread playbackThread;
    private final Player player;
    private Thread recordThread;
    private final Recorder recorder;
    private final Socket sock;

    public AudioSession(Socket paramSocket)
    {
      Log.d("AudioSession", "new session");
      try
      {
        DataInputStream localDataInputStream1 = new DataInputStream(paramSocket.getInputStream());
        localDataInputStream2 = localDataInputStream1;
        this.player = new Player(localDataInputStream2);
      }
      catch (IOException localIOException2)
      {
        try
        {
          DataOutputStream localDataOutputStream1 = new DataOutputStream(paramSocket.getOutputStream());
          localDataOutputStream2 = localDataOutputStream1;
          this.recorder = new Recorder(localDataOutputStream2);
          this.sock = paramSocket;
          return;
          localIOException2 = localIOException2;
          Log.e("AudioServer5", "AudioSession IO: " + localIOException2.toString());
          DataInputStream localDataInputStream2 = null;
        }
        catch (IOException localIOException1)
        {
          while (true)
          {
            Log.e("AudioServer4", "AudioSession IO: " + localIOException1.toString());
            DataOutputStream localDataOutputStream2 = null;
          }
        }
      }
    }

    public int start()
    {
      Log.d("AudioSession", "start()");
      if ((this.recordThread == null) && (this.playbackThread == null))
      {
        this.recordThread = new Thread(this.recorder);
        this.playbackThread = new Thread(this.player);
        this.recordThread.start();
        this.playbackThread.start();
        return 0;
      }
      return -1;
    }

    public void stop()
    {
      try
      {
        this.playbackThread.join();
        this.recordThread.join();
        this.sock.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.v("AudioServer", "AudioSession stopped IO: " + localIOException.toString());
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.v("AudioServer6", "AudioSession stopped Interrupted: " + localInterruptedException.toString());
      }
    }

    static class Player
      implements Runnable
    {
      private AudioTrack auOut;
      private final DataInputStream input;
      private int minBufferSize;

      protected Player(DataInputStream paramDataInputStream)
      {
        this.input = paramDataInputStream;
        this.minBufferSize = AudioTrack.getMinBufferSize(8000, 4, 2);
      }

      public void run()
      {
        int i = 0;
        int j = 0;
        Log.d("Player", "run() " + this.minBufferSize);
        Process.setThreadPriority(-19);
        this.auOut = new AudioTrack(0, 8000, 2, 2, this.minBufferSize, 1);
        this.auOut.play();
        this.minBufferSize = 320;
        byte[] arrayOfByte = new byte[this.minBufferSize];
        try
        {
          do
          {
            int k = this.input.read(arrayOfByte, j, arrayOfByte.length - j);
            i = k;
            j += i;
            if (j == arrayOfByte.length)
            {
              i = this.auOut.write(arrayOfByte, 0, j);
              j = 0;
            }
          }
          while (i > 0);
          this.auOut.release();
          return;
        }
        catch (IOException localIOException)
        {
          while (true)
            Log.v("AudioServer9", "Player: " + localIOException.toString());
        }
      }
    }

    static class Recorder
      implements Runnable
    {
      private AudioRecord auIn;
      private final int minBufferSize;
      private final DataOutputStream output;

      protected Recorder(DataOutputStream paramDataOutputStream)
      {
        this.output = paramDataOutputStream;
        this.minBufferSize = AudioRecord.getMinBufferSize(8000, 16, 2);
      }

      @SuppressLint("NewApi")
	public void run()
      {
        byte[] arrayOfByte = new byte[this.minBufferSize];
        Log.d("Recorder", "run() " + this.minBufferSize);
        Process.setThreadPriority(-19);
        this.auIn = new AudioRecord(6, 8000, 16, 2, this.minBufferSize);
        if (this.auIn.getState() != 1)
        {
          Log.d("Recorder", "cannot initialize with VOICE_RECOGNITION source, fallback to MIC");
          this.auIn.release();
          this.auIn = new AudioRecord(1, 8000, 16, 2, this.minBufferSize);
        }
        int i;
        while (true)
        {
          this.auIn.startRecording();
          i = this.auIn.read(arrayOfByte, 0, this.minBufferSize);
          if (i <= 0)
            try
            {
              this.output.write(arrayOfByte, 0, this.minBufferSize);
              if (i < 0)
              {
                return;
               // Log.d("Recorder", "using VOICE_RECOGNITION source");
              }
            }
            catch (IOException localIOException2)
            {
              Log.v("AudioServer8", "Recorder: " + localIOException2.toString());
            }
        }
       
          while (true)
          {
            this.auIn.release();
            return;
            try
            {
              this.output.write(arrayOfByte, 0, i);
              if (i > 0)
                break;
            }
            catch (IOException localIOException1)
            {
              Log.v("AudioServer7", "Recorder: " + localIOException1.toString());
            }
          }
      }
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.AudioServer
 * JD-Core Version:    0.6.2
 */