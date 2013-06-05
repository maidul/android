package com.ipsmarx.dialer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Contacts.Phones;

public class CallerInfo
{
  public static final String PRIVATE_NUMBER = "-2";
  private static final String TAG = "CallerInfo";
  public static final String UNKNOWN_NUMBER = "-1";
  public Drawable cachedPhoto;
  public Uri contactRefUri;
  public Uri contactRingtoneUri;
  public boolean isCachedPhotoCurrent;
  public String name;
  public boolean needUpdate;
  public String numberLabel;
  public int numberType;
  public long person_id;
  public String phoneLabel;
  public String phoneNumber;
  public int photoResource;
  public boolean shouldSendToVoicemail;

  public static CallerInfo getCallerInfo(Context paramContext, Uri paramUri)
  {
    return getCallerInfo(paramContext, paramUri, paramContext.getContentResolver().query(paramUri, null, null, null, null));
  }

  public static CallerInfo getCallerInfo(Context paramContext, Uri paramUri, Cursor paramCursor)
  {
    int i = 1;
    CallerInfo localCallerInfo = new CallerInfo();
    localCallerInfo.photoResource = 0;
    localCallerInfo.phoneLabel = null;
    localCallerInfo.numberType = 0;
    localCallerInfo.numberLabel = null;
    localCallerInfo.cachedPhoto = null;
    localCallerInfo.isCachedPhotoCurrent = false;
    if ((paramCursor != null) && (paramCursor.moveToFirst()))
    {
      int j = paramCursor.getColumnIndex("name");
      if (j != -1)
        localCallerInfo.name = paramCursor.getString(j);
      int k = paramCursor.getColumnIndex("number");
      if (k != -1)
        localCallerInfo.phoneNumber = paramCursor.getString(k);
      int m = paramCursor.getColumnIndex("label");
      if (m != -1)
      {
        int i4 = paramCursor.getColumnIndex("type");
        if (i4 != -1)
        {
          localCallerInfo.numberType = paramCursor.getInt(i4);
          localCallerInfo.numberLabel = paramCursor.getString(m);
          localCallerInfo.phoneLabel = Contacts.Phones.getDisplayLabel(paramContext, localCallerInfo.numberType, localCallerInfo.numberLabel).toString();
        }
      }
      int n = paramCursor.getColumnIndex("person");
      if (n == -1)
        break label334;
      localCallerInfo.person_id = paramCursor.getLong(n);
      int i2 = paramCursor.getColumnIndex("custom_ringtone");
      if ((i2 == -1) || (paramCursor.getString(i2) == null))
        break label366;
      localCallerInfo.contactRingtoneUri = Uri.parse(paramCursor.getString(i2));
      label272: int i3 = paramCursor.getColumnIndex("send_to_voicemail");
      if ((i3 == -1) || (paramCursor.getInt(i3) != i))
        break label375;
    }
    while (true)
    {
      localCallerInfo.shouldSendToVoicemail = i;
      localCallerInfo.needUpdate = false;
      localCallerInfo.name = normalize(localCallerInfo.name);
      localCallerInfo.contactRefUri = paramUri;
      return localCallerInfo;
      label334: int i1 = paramCursor.getColumnIndex("_id");
      if (i1 == -1)
        break;
      localCallerInfo.person_id = paramCursor.getLong(i1);
      break;
      label366: localCallerInfo.contactRingtoneUri = null;
      break label272;
      label375: i = 0;
    }
  }

  private static String normalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() > 0))
      return paramString;
    return null;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallerInfo
 * JD-Core Version:    0.6.2
 */