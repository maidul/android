package com.ipsmarx.dialer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallLogList extends ListActivity
{
  static final int CALLER_NAME_COLUMN_INDEX = 2;
  static final String[] CALL_LOG_PROJECTION = { "_id", "date", "name", "number", "type", "numbertype", "numberlabel" };
  static final int CALL_TYPE_COLUMN_INDEX = 4;
  static final int COLUMN_INDEX_ID = 3;
  static final int COLUMN_LABEL_INDEX = 6;
  static final int COLUMN_TYPE_INDEX = 5;
  static final int DATE_COLUMN_INDEX = 1;
  private static final String THIS_FILE = "Call log list";
  Context context;
  private Drawable drawableIncoming;
  private Drawable drawableMissed;
  private Drawable drawableOutgoing;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903058);
    this.context = this;
    this.drawableIncoming = getResources().getDrawable(2130837644);
    this.drawableOutgoing = getResources().getDrawable(2130837646);
    this.drawableMissed = getResources().getDrawable(2130837645);
    Uri localUri = Uri.parse("content://call_log/calls");
    setListAdapter(new CallLogsCursorAdapter(this, this.context.getContentResolver().query(localUri, CALL_LOG_PROJECTION, null, null, null)));
  }

  protected void onDestroy()
  {
    super.onDestroy();
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
  }

  class CallLogsCursorAdapter extends ResourceCursorAdapter
    implements View.OnClickListener
  {
    public CallLogsCursorAdapter(Context paramCursor, Cursor arg3)
    {
      super(2130903059, localCursor);
    }

    public void bindView(View paramView, Context paramContext, Cursor paramCursor)
    {
      CallLogList.RecentCallsListItemViews localRecentCallsListItemViews = (CallLogList.RecentCallsListItemViews)paramView.getTag();
      String str1 = paramCursor.getString(3);
      String str2 = paramCursor.getString(2);
      String str3 = str1;
      localRecentCallsListItemViews.callView.setTag(str1);
      Matcher localMatcher;
      if (TextUtils.isEmpty(str2))
      {
        localMatcher = Pattern.compile("^(?:\")?([^<\"]*)(?:\")?[ ]*(?:<)?sip(?:s)?:([^@]*)@[^>]*(?:>)?").matcher(str1);
        if (localMatcher.matches())
        {
          if (TextUtils.isEmpty(localMatcher.group(2)))
            break label245;
          str3 = localMatcher.group(2);
        }
      }
      while (true)
      {
        localRecentCallsListItemViews.nameView.setText(str3);
        localRecentCallsListItemViews.numberView.setText(str1);
        if ((!paramCursor.isNull(5)) && (paramCursor.getInt(5) > 0))
        {
          localRecentCallsListItemViews.labelView.setVisibility(0);
          int j = paramCursor.getInt(5);
          String str4 = paramCursor.getString(6);
          localRecentCallsListItemViews.labelView.setText(ContactsContract.CommonDataKinds.Phone.getTypeLabel(paramContext.getResources(), j, str4));
        }
        int i = paramCursor.getInt(4);
        long l = paramCursor.getLong(1);
        localRecentCallsListItemViews.dateView.setText(DateUtils.getRelativeTimeSpanString(l, System.currentTimeMillis(), 60000L, 262144));
        switch (i)
        {
        default:
          return;
          label245: if (!TextUtils.isEmpty(localMatcher.group(1)))
          {
            str3 = localMatcher.group(1);
            continue;
            str3 = str2;
          }
          break;
        case 1:
        case 2:
        case 3:
        }
      }
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogList.this.drawableIncoming);
      return;
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogList.this.drawableOutgoing);
      return;
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogList.this.drawableMissed);
    }

    public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
    {
      View localView = super.newView(paramContext, paramCursor, paramViewGroup);
      CallLogList.RecentCallsListItemViews localRecentCallsListItemViews = new CallLogList.RecentCallsListItemViews();
      localRecentCallsListItemViews.nameView = ((TextView)localView.findViewById(2131427353));
      localRecentCallsListItemViews.labelView = ((TextView)localView.findViewById(2131427334));
      localRecentCallsListItemViews.numberView = ((TextView)localView.findViewById(2131427335));
      localRecentCallsListItemViews.dateView = ((TextView)localView.findViewById(2131427403));
      localRecentCallsListItemViews.iconView = ((ImageView)localView.findViewById(2131427402));
      localRecentCallsListItemViews.callView = localView.findViewById(2131427401);
      localRecentCallsListItemViews.callView.setOnClickListener(this);
      localView.setTag(localRecentCallsListItemViews);
      return localView;
    }

    public void onClick(View paramView)
    {
      switch (paramView.getId())
      {
      default:
      case 2131427401:
      }
      String str1;
      do
      {
        return;
        str1 = (String)paramView.getTag();
      }
      while (str1.length() <= 0);
      SipService localSipService = (SipService)SipService.ServiceInstance.getService();
      final String str2 = PhoneNumberUtils.stripSeparators(str1);
      SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(CallLogList.this.context);
      final String str3 = localSharedPreferences.getString("AccessNumber", "NULL");
      String str4 = localSharedPreferences.getString("AllowAccessNumber", "NULL");
      if ((!localSipService.isOnline()) && (str2.length() > 0) && (str3.length() > 2) && (str4.equalsIgnoreCase("1")))
      {
        AlertDialog localAlertDialog = new AlertDialog.Builder(CallLogList.this.context).create();
        localAlertDialog.setTitle(2131230738);
        localAlertDialog.setMessage(CallLogList.this.context.getString(2131230737));
        localAlertDialog.setButton(CallLogList.this.context.getString(2131230739), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            try
            {
              CallLogList.this.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + str3 + "," + str2)));
              return;
            }
            catch (Exception localException)
            {
              localException.printStackTrace();
            }
          }
        });
        localAlertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
          }
        });
        localAlertDialog.show();
        return;
      }
      localSipService.dialOut(str2);
    }
  }

  public static final class RecentCallsListItemViews
  {
    View callView;
    TextView dateView;
    ImageView iconView;
    TextView labelView;
    TextView nameView;
    TextView numberView;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallLogList
 * JD-Core Version:    0.6.2
 */