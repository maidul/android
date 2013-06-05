package com.ipsmarx.dialer;

import android.util.Base64;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Encryption
{
  public String HMAC(String paramString)
  {
    try
    {
      SecretKeySpec localSecretKeySpec = new SecretKeySpec("ipsmarx99".getBytes(), "HmacSHA1");
      Mac localMac = Mac.getInstance("HmacSHA1");
      localMac.init(localSecretKeySpec);
      String str = Base64.encodeToString(localMac.doFinal(paramString.getBytes()), 0);
      return str;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
      Log.v("david", "Error:" + localNoSuchAlgorithmException.getMessage());
      return "";
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localInvalidKeyException.printStackTrace();
      Log.v("david", "error:" + localInvalidKeyException.getMessage());
    }
    return "";
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Encryption
 * JD-Core Version:    0.6.2
 */