package com.ipsmarx.dialer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallLogListInternal extends ListActivity
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
    String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("useremail", "NULL");
    DBAdapter localDBAdapter = new DBAdapter(this);
    localDBAdapter.open();
    setListAdapter(new CallLogsCursorAdapter(this, localDBAdapter.getAllCalls(str)));
    localDBAdapter.close();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    new MenuInflater(this).inflate(2131361792, paramMenu);
    return super.onCreateOptionsMenu(paramMenu);
  }

  protected void onDestroy()
  {
    super.onDestroy();
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    Intent localIntent = new Intent(getApplicationContext(), CallDetailActivity.class);
    localIntent.putExtra("_id", (int)paramLong);
    startActivity(localIntent);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2131427421)
    {
      DBAdapter localDBAdapter = new DBAdapter(this);
      localDBAdapter.open();
      localDBAdapter.deleteAll();
      localDBAdapter.close();
      refresh();
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }

  public void onResume()
  {
    super.onResume();
    refresh();
  }

  public void refresh()
  {
    String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("useremail", "NULL");
    DBAdapter localDBAdapter = new DBAdapter(this);
    localDBAdapter.open();
    setListAdapter(new CallLogsCursorAdapter(this, localDBAdapter.getAllCalls(str)));
    try
    {
      localDBAdapter.close();
      return;
    }
    catch (Exception localException)
    {
    }
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
      CallLogListInternal.RecentCallsListItemViews localRecentCallsListItemViews = (CallLogListInternal.RecentCallsListItemViews)paramView.getTag();
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
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogListInternal.this.drawableIncoming);
      return;
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogListInternal.this.drawableOutgoing);
      return;
      localRecentCallsListItemViews.iconView.setImageDrawable(CallLogListInternal.this.drawableMissed);
    }

    public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
    {
      View localView = super.newView(paramContext, paramCursor, paramViewGroup);
      CallLogListInternal.RecentCallsListItemViews localRecentCallsListItemViews = new CallLogListInternal.RecentCallsListItemViews();
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
        return;
      case 2131427401:
      }
      String str = (String)paramView.getTag();
      ((SipService)SipService.ServiceInstance.getService()).ConditionCheckDialOut(str, (Activity)CallLogListInternal.this.context);
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
 * Qualified Name:     com.ipsmarx.dialer.CallLogListInternal
 * JD-Core Version:    0.6.2
 */