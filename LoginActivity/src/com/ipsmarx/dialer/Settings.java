package com.ipsmarx.dialer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.ipsmarx.dialer.ProductFactory.SoftPhone;
import com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Settings extends PreferenceActivity
  implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener
{
  private Map<String, ?> backupentries;
  private SharedPreferences mPreferences;

  private void beginListening(Boolean paramBoolean)
  {
    if (getPreferenceScreen().getSharedPreferences() == null)
      return;
    if (paramBoolean.booleanValue())
    {
      getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
      return;
    }
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }

  private void buildDefaultCodec(ListPreference paramListPreference, String paramString)
  {
    Consts.CodecSelection localCodecSelection = Consts.CodecSelection.NOG729;
    ListPreference localListPreference;
    boolean bool1;
    boolean bool2;
    int i;
    label97: SharedPreferences.Editor localEditor;
    if (paramString.equalsIgnoreCase("codec1"))
    {
      localListPreference = (ListPreference)findPreference("codec2");
      bool1 = paramListPreference.getValue().equalsIgnoreCase(getString(2131230767));
      bool2 = localListPreference.getValue().equalsIgnoreCase(getString(2131230767));
      i = paramListPreference.compareTo(localListPreference);
      if (((bool1) || (!bool2) || (i >= 0)) && ((!bool1) || (bool2) || (i <= 0)))
        break label226;
      localCodecSelection = Consts.CodecSelection.G729FIRST;
      localEditor = this.mPreferences.edit();
      localEditor.putInt("defaultcodec", localCodecSelection.getPriorityCode()).commit();
      switch ($SWITCH_TABLE$com$ipsmarx$dialer$Consts$CodecSelection()[localCodecSelection.ordinal()])
      {
      default:
        localEditor.putString("localcodec1", getString(2131230767)).putString("localcodec2", getString(2131230767));
      case 4:
      case 1:
      case 2:
      case 3:
      }
    }
    while (true)
    {
      localEditor.commit();
      SipService.getService().switchCodecs(localCodecSelection.getPriorityCode());
      return;
      localListPreference = (ListPreference)findPreference("codec1");
      break;
      label226: if (((!bool1) && (bool2) && (i > 0)) || ((bool1) && (!bool2) && (i < 0)))
      {
        localCodecSelection = Consts.CodecSelection.G729SECOND;
        break label97;
      }
      if ((bool1) && (bool2))
      {
        localCodecSelection = Consts.CodecSelection.NOG729;
        break label97;
      }
      if ((bool1) || (bool2))
        break label97;
      localCodecSelection = Consts.CodecSelection.ALLG729;
      break label97;
      localEditor.putString("localcodec1", getString(2131230766)).putString("localcodec2", getString(2131230766));
      continue;
      localEditor.putString("localcodec1", getString(2131230767)).putString("localcodec2", getString(2131230767));
      continue;
      localEditor.putString("localcodec1", getString(2131230766)).putString("localcodec2", getString(2131230767));
      continue;
      localEditor.putString("localcodec1", getString(2131230767)).putString("localcodec2", getString(2131230766));
    }
  }

  private boolean restoreSharedPreferences()
  {
    SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    Iterator localIterator = this.backupentries.entrySet().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return localEditor.commit();
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject = localEntry.getValue();
      String str = (String)localEntry.getKey();
      if ((localObject instanceof String))
        localEditor.putString(str, (String)localObject);
    }
  }

  private void setSummaryAll(PreferenceScreen paramPreferenceScreen)
  {
    int i = 0;
    if (i >= paramPreferenceScreen.getPreferenceCount())
      return;
    Preference localPreference = paramPreferenceScreen.getPreference(i);
    if ((localPreference instanceof ListPreference))
      localPreference.setSummary(((ListPreference)localPreference).getEntry());
    while (true)
    {
      i++;
      break;
      if ((localPreference instanceof EditTextPreference))
      {
        if (((EditTextPreference)localPreference).getEditText().getInputType() == 129)
        {
          EditTextPreference localEditTextPreference = (EditTextPreference)localPreference;
          if (localEditTextPreference.getText() != null)
          {
            StringBuffer localStringBuffer = new StringBuffer();
            for (int j = 0; ; j++)
            {
              if (j >= localEditTextPreference.getText().length())
              {
                localPreference.setSummary(localStringBuffer.toString());
                break;
              }
              localStringBuffer.append("*");
             http
            }
          }
        }
        else
        {
          localPreference.setSummary(((EditTextPreference)localPreference).getText());
        }
      }
      else if ((localPreference instanceof PreferenceScreen))
        setSummaryAll((PreferenceScreen)localPreference);
    }
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
      return;
    case 2131427418:
      SoftPhoneFactory.getInstance().getCurrentProduct().setProductIP(this.mPreferences.getString("ServerIP", ""));
      this.mPreferences.edit().putBoolean("signin", true).commit();
      if ((SipService.getService() != null) && (SipService.getService().isMobileSipStackAwake().booleanValue()))
      {
        Intent localIntent = new Intent(getApplicationContext(), DialerApp.class);
        localIntent.setFlags(131072);
        startActivity(localIntent);
        updateCredentials();
      }
      while (true)
      {
        finish();
        return;
        SipService.setWakeLockMsg(true);
        SoftPhoneFactory.getInstance().getCurrentProduct().setProductInitialized(Boolean.valueOf(true));
        startActivity(new Intent(getApplicationContext(), DialerApp.class));
      }
    case 2131427417:
    }
    restoreSharedPreferences();
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str1;
    ListPreference localListPreference;
    label180: int i;
    if (SoftPhoneFactory.getInstance().getCurrentProduct().hasConfigurationMenu())
    {
      setContentView(2130903060);
      addPreferencesFromResource(2130968577);
      View localView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130968578, null, false);
      getListView().setFooterDividersEnabled(true);
      getListView().addFooterView(localView, null, false);
      ImageButton localImageButton = (ImageButton)findViewById(2131427418);
      ((ImageButton)findViewById(2131427417)).setOnClickListener(this);
      localImageButton.setOnClickListener(this);
      this.mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      str1 = this.mPreferences.getString("CallLicenseType", "Both");
      beginListening(Boolean.valueOf(true));
      if (!str1.equals("Both"))
      {
        localListPreference = (ListPreference)findPreference("CallLicenseTypeLocal");
        localListPreference.setEnabled(false);
        if (!str1.equalsIgnoreCase("Voip"))
          break label374;
        localListPreference.setValue("VoIP");
      }
      boolean bool1 = this.mPreferences.getString("G729_Licenses", "0").equals(Integer.valueOf(1));
      boolean bool2 = this.mPreferences.getString("AllowG729", "0").equals("1");
      if ((!bool1) || (!bool2))
        break label383;
      i = 1;
      label238: if (i == 0)
        break label389;
      String str2 = this.mPreferences.getString("localcodec1", getString(2131230766));
      String str3 = this.mPreferences.getString("localcodec2", getString(2131230767));
      findPreference("codec1").setSummary(str2.toString());
      findPreference("codec1").setDefaultValue(str2);
      findPreference("codec2").setSummary(str3.toString());
      findPreference("codec2").setDefaultValue(str3);
      findPreference("selectcodec").setEnabled(true);
    }
    while (true)
    {
      setSummaryAll(getPreferenceScreen());
      this.backupentries = this.mPreferences.getAll();
      return;
      addPreferencesFromResource(2130968579);
      break;
      label374: localListPreference.setValue(str1);
      break label180;
      label383: i = 0;
      break label238;
      label389: if (findPreference("selectcodec") != null)
        findPreference("selectcodec").setEnabled(false);
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();
    beginListening(Boolean.valueOf(false));
  }

  protected void onPause()
  {
    super.onPause();
    beginListening(Boolean.valueOf(false));
  }

  protected void onResume()
  {
    super.onResume();
    beginListening(Boolean.valueOf(true));
  }

  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    Preference localPreference = findPreference(paramString);
    if ((localPreference instanceof ListPreference))
    {
      localListPreference = (ListPreference)localPreference;
      localPreference.setSummary(localListPreference.getEntry());
      if (localListPreference.getTitle().toString().equalsIgnoreCase("Codec"))
        buildDefaultCodec(localListPreference, paramString);
      if (localListPreference.getEntry().toString().equalsIgnoreCase("Pinless"))
        SipService.setWakeLockMsg(false);
    }
    while (!(localPreference instanceof EditTextPreference))
    {
      ListPreference localListPreference;
      return;
      SipService.setWakeLockMsg(true);
      return;
    }
    localPreference.setSummary(((EditTextPreference)localPreference).getText());
    localPreference.shouldCommit();
  }

  public void updateCredentials()
  {
    String str1 = this.mPreferences.getString("SipId", "");
    String str2 = this.mPreferences.getString("AuthId", "");
    String str3 = this.mPreferences.getString("Password", "");
    String str4 = this.mPreferences.getString("ServerIP", "");
    if (SipService.getService().updateCredentials(str1, str2, str3, str4).booleanValue())
      SipService.getService().init();
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.Settings
 * JD-Core Version:    0.6.2
 */