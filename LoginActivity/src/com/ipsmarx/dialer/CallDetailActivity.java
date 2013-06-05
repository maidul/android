package com.ipsmarx.dialer;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class CallDetailActivity extends ListActivity
  implements AdapterView.OnItemClickListener
{
  static final String[] CALL_LOG_PROJECTION = { "date", "duration", "number", "type" };
  static final int CALL_TYPE_COLUMN_INDEX = 4;
  static final int COLUMN_INDEX_ID = 0;
  static final int COLUMN_INDEX_LABEL = 3;
  static final int COLUMN_INDEX_NAME = 1;
  static final int COLUMN_INDEX_NUMBER = 4;
  static final int COLUMN_INDEX_TYPE = 2;
  static final int DATE_COLUMN_INDEX = 1;
  static final int DURATION_COLUMN_INDEX = 7;
  static final int NUMBER_COLUMN_INDEX = 3;
  static final String[] PHONES_PROJECTION = { "_id", "display_name", "type", "label", "number" };
  private TextView mCallDuration;
  private TextView mCallTime;
  private TextView mCallType;
  private ImageView mCallTypeIcon;
  LayoutInflater mInflater;
  private String mNumber = null;
  Resources mResources;

  private String formatDuration(long paramLong)
  {
    long l1 = 0L;
    if (paramLong >= 60L)
    {
      l1 = paramLong / 60L;
      paramLong -= l1 * 60L;
    }
    long l2 = paramLong;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Long.valueOf(l1);
    arrayOfObject[1] = Long.valueOf(l2);
    return getString(2131230760, arrayOfObject);
  }

  private void updateData(int paramInt)
  {
    ContentResolver localContentResolver = getContentResolver();
    DBAdapter localDBAdapter = new DBAdapter(this);
    localDBAdapter.open();
    Cursor localCursor1 = localDBAdapter.getCall(paramInt);
    if (localCursor1 != null);
    while (true)
    {
      int i;
      try
      {
        if (!localCursor1.moveToFirst())
          break label507;
        this.mNumber = localCursor1.getString(3);
        long l1 = localCursor1.getLong(1);
        long l2 = localCursor1.getLong(7);
        i = localCursor1.getInt(4);
        String str = DateUtils.formatDateRange(this, l1, l1, 23);
        this.mCallTime.setText(str);
        if (i == 3)
        {
          this.mCallDuration.setVisibility(8);
          break label525;
          if ((!this.mNumber.equals("-1")) && (!this.mNumber.equals("-2")))
            break label284;
          TextView localTextView = (TextView)findViewById(2131427332);
          if (localTextView != null)
          {
            if (!this.mNumber.equals("-2"))
              break label552;
            //change maidul
            int j = 2131230758;
            localTextView.setText(j);
          }
          localDBAdapter.close();
          return;
        }
        this.mCallDuration.setVisibility(0);
        this.mCallDuration.setText(formatDuration(l2));
        break label525;
      }
      finally
      {
      }
      this.mCallTypeIcon.setImageResource(2130837641);
      this.mCallType.setText(2131230747);
      continue;
      this.mCallTypeIcon.setImageResource(2130837643);
      this.mCallType.setText(2131230752);
      continue;
      this.mCallTypeIcon.setImageResource(2130837642);
      this.mCallType.setText(2131230753);
      continue;
      //change maidul
      Cursor localCursor2 = localContentResolver.query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(this.mNumber)), PHONES_PROJECTION, null, null, null);
      Uri localUri;ArrayList localArrayList;
      if ((localCursor2 != null) && (localCursor2.moveToFirst()))
      {
        long l3 = localCursor2.getLong(0);
        localUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, l3);
        this.mNumber = PhoneNumberUtils.formatNumber(localCursor2.getString(4));
        localArrayList = new ArrayList();
        if (localUri == null)
          break label445;
        Intent localIntent1 = new Intent("android.intent.action.VIEW", localUri);
        localArrayList.add(new ViewEntry(2130837698, getString(2131230754), localIntent1));
      }
      while (true)
      {
        setListAdapter(new ViewAdapter(this, localArrayList));
        break;
        this.mNumber = PhoneNumberUtils.formatNumber(this.mNumber);
        localUri = null;
        break label359;
        Intent localIntent2 = new Intent("android.intent.action.INSERT_OR_EDIT");
        localIntent2.setType("vnd.android.cursor.item/contact");
        localIntent2.putExtra("phone", this.mNumber);
        localArrayList.add(new ViewEntry(2130837697, getString(2131230755), localIntent2));
      }
      label507: Toast.makeText(this, 2131230756, 0).show();
      finish();
      continue;
      label525: switch (i)
      {
      default:
      case 1:
      case 2:
      case 3:
      }
      int j = 2131230746;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903040);
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    this.mResources = getResources();
    this.mCallType = ((TextView)findViewById(2131427329));
    this.mCallTypeIcon = ((ImageView)findViewById(2131427328));
    this.mCallTime = ((TextView)findViewById(2131427330));
    this.mCallDuration = ((TextView)findViewById(2131427331));
    getListView().setOnItemClickListener(this);
  }

  public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if ((paramView.getTag() instanceof ViewEntry))
      startActivity(((ViewEntry)paramView.getTag()).intent);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    switch (paramInt)
    {
    default:
    case 5:
    }
    do
      return super.onKeyDown(paramInt, paramKeyEvent);
    while (((TelephonyManager)getSystemService("phone")).getCallState() != 0);
    //chnage maidul
    //return true;
  }

  public void onResume()
  {
    super.onResume();
    updateData(getIntent().getExtras().getInt("_id"));
  }

  public void startSearch(String paramString, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    if (paramBoolean2)
      super.startSearch(paramString, paramBoolean1, paramBundle, paramBoolean2);
  }

  static final class ViewAdapter extends BaseAdapter
  {
    private final List<CallDetailActivity.ViewEntry> mActions;
    private final LayoutInflater mInflater;

    public ViewAdapter(Context paramContext, List<CallDetailActivity.ViewEntry> paramList)
    {
      this.mActions = paramList;
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }

    public int getCount()
    {
      return this.mActions.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mActions.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = this.mInflater.inflate(2130903041, paramViewGroup, false);
      CallDetailActivity.ViewEntry localViewEntry = (CallDetailActivity.ViewEntry)this.mActions.get(paramInt);
      paramView.setTag(localViewEntry);
      ImageView localImageView = (ImageView)paramView.findViewById(2131427328);
      TextView localTextView1 = (TextView)paramView.findViewById(16908308);
      localImageView.setImageResource(localViewEntry.icon);
      localTextView1.setText(localViewEntry.text);
      View localView = paramView.findViewById(2131427333);
      boolean bool = TextUtils.isEmpty(localViewEntry.number);
      if ((!TextUtils.isEmpty(localViewEntry.label)) && (!bool));
      for (int i = 0; (i != 0) && (bool); i = 1)
      {
        localView.setVisibility(8);
        return paramView;
      }
      localView.setVisibility(0);
      TextView localTextView2 = (TextView)paramView.findViewById(2131427334);
      if (i != 0)
        localTextView2.setVisibility(8);
      while (true)
      {
        ((TextView)paramView.findViewById(2131427335)).setText(localViewEntry.number);
        return paramView;
        localTextView2.setText(localViewEntry.label);
        localTextView2.setVisibility(0);
      }
    }
  }

  static final class ViewEntry
  {
    public int icon = -1;
    public Intent intent = null;
    public String label;
    public String number;
    public String text = null;

    public ViewEntry(int paramInt, String paramString, Intent paramIntent)
    {
      this.icon = paramInt;
      this.text = paramString;
      this.intent = paramIntent;
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.CallDetailActivity
 * JD-Core Version:    0.6.2
 */