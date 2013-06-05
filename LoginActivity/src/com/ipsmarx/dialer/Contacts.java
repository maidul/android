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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Contacts extends ListActivity
  implements View.OnClickListener
{
  private static final String PEOPLE_SORT = "display_name COLLATE LOCALIZED ASC";
  private static final String[] PHONE_PROJECTION = { "_id", "display_name", "data3", "data2", "data1", "is_super_primary" };
  private Cursor constantsCursor;
  Context context;

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
    case 2131427351:
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
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    final String str3 = localSharedPreferences.getString("AccessNumber", "NULL");
    String str4 = localSharedPreferences.getString("AllowAccessNumber", "NULL");
    if ((!localSipService.isOnline()) && (str2.length() > 0) && (str3.length() > 2) && (str4.equalsIgnoreCase("1")))
    {
      AlertDialog localAlertDialog = new AlertDialog.Builder(this.context).create();
      localAlertDialog.setTitle(2131230738);
      localAlertDialog.setMessage(this.context.getString(2131230737));
      localAlertDialog.setButton(this.context.getString(2131230739), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          try
          {
            Contacts.this.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + str3 + "," + str2)));
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

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903046);
    this.context = this;
    Uri localUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    this.constantsCursor = this.context.getContentResolver().query(localUri, PHONE_PROJECTION, null, null, "display_name COLLATE LOCALIZED ASC");
    setListAdapter(new ContactListAdapter(this, this.constantsCursor));
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    super.onListItemClick(paramListView, paramView, paramInt, paramLong);
    paramView.getId();
  }

  public void onStart()
  {
    super.onStart();
  }

  private class ContactListAdapter extends ResourceCursorAdapter
  {
    private Context mContext;

    public ContactListAdapter(Context paramCursor, Cursor arg3)
    {
      super(2130903047, localCursor, false);
      this.mContext = paramCursor;
    }

    public void bindView(View paramView, Context paramContext, Cursor paramCursor)
    {
      Contacts.ContactListItemCache localContactListItemCache = (Contacts.ContactListItemCache)paramView.getTag();
      String str1 = paramCursor.getString(1);
      localContactListItemCache.nameView.setText(str1, TextView.BufferType.SPANNABLE);
      if (!paramCursor.isNull(3))
      {
        localContactListItemCache.labelView.setVisibility(0);
        int i = paramCursor.getInt(3);
        String str3 = paramCursor.getString(2);
        localContactListItemCache.labelView.setText(ContactsContract.CommonDataKinds.Phone.getTypeLabel(paramContext.getResources(), i, str3));
      }
      while (true)
      {
        String str2 = paramCursor.getString(4);
        localContactListItemCache.dataView.setText(str2, TextView.BufferType.SPANNABLE);
        localContactListItemCache.callButton.setTag(str2);
        localContactListItemCache.lookupUri = null;
        return;
        localContactListItemCache.labelView.setVisibility(8);
      }
    }

    public String convertToString(Cursor paramCursor)
    {
      return paramCursor.getString(2);
    }

    public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
    {
      View localView = super.newView(paramContext, paramCursor, paramViewGroup);
      Contacts.ContactListItemCache localContactListItemCache = new Contacts.ContactListItemCache();
      localContactListItemCache.divider = localView.findViewById(2131427354);
      localContactListItemCache.nameView = ((TextView)localView.findViewById(2131427353));
      localContactListItemCache.callView = localView.findViewById(2131427349);
      localContactListItemCache.callButton = ((ImageView)localView.findViewById(2131427351));
      if (localContactListItemCache.callButton != null)
        localContactListItemCache.callButton.setOnClickListener(Contacts.this);
      localContactListItemCache.labelView = ((TextView)localView.findViewById(2131427334));
      localContactListItemCache.dataView = ((TextView)localView.findViewById(2131427352));
      localView.setTag(localContactListItemCache);
      return localView;
    }
  }

  static final class ContactListItemCache
  {
    public ImageView callButton;
    public View callView;
    public TextView dataView;
    public View divider;
    public TextView labelView;
    public Uri lookupUri;
    public TextView nameView;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Contacts
 * JD-Core Version:    0.6.2
 */