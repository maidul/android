package com.ipsmarx.dialer;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class ContactsActivity extends ListActivity
  implements View.OnClickListener
{
  private static final String PEOPLE_SORT = "display_name COLLATE LOCALIZED ASC";
  private static final String[] PHONE_PROJECTION = { "_id", "display_name", "data3", "data2", "data1", "is_super_primary" };
  EditText _filterText;
  ContactListAdapter adapter;
  private Cursor constantsCursor;
  Context context;

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
      return;
    case 2131427351:
    }
    String str = (String)paramView.getTag();
    ((SipService)SipService.ServiceInstance.getService()).ConditionCheckDialOut(str, this);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903046);
    this.context = this;
    Uri localUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    this.constantsCursor = this.context.getContentResolver().query(localUri, PHONE_PROJECTION, null, null, "display_name COLLATE LOCALIZED ASC");
    this.adapter = new ContactListAdapter(this, this.constantsCursor);
    setListAdapter(this.adapter);
    this._filterText = ((EditText)findViewById(2131427346));
    this._filterText.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
      }

      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
      }

      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        ContactsActivity.this.adapter.getFilter().filter(paramAnonymousCharSequence.toString());
      }
    });
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    new MenuInflater(this).inflate(2131361794, paramMenu);
    return super.onCreateOptionsMenu(paramMenu);
  }

  protected void onDestroy()
  {
    super.onDestroy();
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    super.onListItemClick(paramListView, paramView, paramInt, paramLong);
    paramView.getId();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 2131427423)
      startActivity(new Intent("android.intent.action.INSERT", Contacts.People.CONTENT_URI));
    return super.onOptionsItemSelected(paramMenuItem);
  }

  public void onStart()
  {
    super.onStart();
  }

  private class ContactListAdapter extends ResourceCursorAdapter
    implements Filterable
  {
    private Context mContext;

    public ContactListAdapter(Context paramCursor, Cursor arg3)
    {
      super(2130903047, localCursor, false);
    }

    public void bindView(View paramView, Context paramContext, Cursor paramCursor)
    {
      ContactsActivity.ContactListItemCache localContactListItemCache = (ContactsActivity.ContactListItemCache)paramView.getTag();
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
      ContactsActivity.ContactListItemCache localContactListItemCache = new ContactsActivity.ContactListItemCache();
      localContactListItemCache.divider = localView.findViewById(2131427354);
      localContactListItemCache.nameView = ((TextView)localView.findViewById(2131427353));
      localContactListItemCache.callView = localView.findViewById(2131427349);
      localContactListItemCache.callButton = ((ImageView)localView.findViewById(2131427351));
      if (localContactListItemCache.callButton != null)
        localContactListItemCache.callButton.setOnClickListener(ContactsActivity.this);
      localContactListItemCache.labelView = ((TextView)localView.findViewById(2131427334));
      localContactListItemCache.dataView = ((TextView)localView.findViewById(2131427352));
      localView.setTag(localContactListItemCache);
      return localView;
    }

    public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
    {
      Cursor localCursor;
      if (getFilterQueryProvider() != null)
        localCursor = getFilterQueryProvider().runQuery(paramCharSequence);
      do
      {
        return localCursor;
        localCursor = null;
        if (paramCharSequence != null)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("UPPER(");
          localStringBuilder.append("display_name");
          localStringBuilder.append(") GLOB ?");
          String[] arrayOfString = new String[1];
          arrayOfString[0] = (paramCharSequence.toString().toUpperCase() + "*");
          localCursor = ContactsActivity.this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactsActivity.PHONE_PROJECTION, localStringBuilder.toString(), arrayOfString, null);
        }
      }
      while (localCursor == null);
      localCursor.getCount();
      return localCursor;
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
 * Qualified Name:     com.ipsmarx.dialer.ContactsActivity
 * JD-Core Version:    0.6.2
 */