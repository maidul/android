package com.ipsmarx.dialer.VisitorPatterns;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.ipsmarx.dialer.LoginActivity;
import com.ipsmarx.dialer.ParsedLoginDataSet;

public class PreferenceManagementVisitor
  implements Visitor
{
  ParsedLoginDataSet parsedLoginDataSet;
  String password;
  Boolean signinpref;
  String username;

  public PreferenceManagementVisitor(ParsedLoginDataSet paramParsedLoginDataSet, String paramString1, String paramString2, Boolean paramBoolean)
  {
    this.parsedLoginDataSet = paramParsedLoginDataSet;
    this.username = paramString1;
    this.password = paramString2;
    this.signinpref = paramBoolean;
  }

  public void visit(LoginActivity paramLoginActivity)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramLoginActivity);
    SharedPreferences.Editor localEditor = localSharedPreferences.edit();
    String str1 = this.parsedLoginDataSet.getLastName();
    if (str1 == null)
      str1 = "";
    localEditor.putString("DispName", this.parsedLoginDataSet.getExtractedFirstName() + " " + str1);
    localEditor.putString("SipId", this.parsedLoginDataSet.getSipUserName());
    localEditor.putString("AuthId", this.parsedLoginDataSet.getSipUserName());
    localEditor.putString("Password", this.parsedLoginDataSet.getSipPassword());
    localEditor.putString("ServerIP", this.parsedLoginDataSet.getSoftSwitchIpServer());
    localEditor.putString("balance", this.parsedLoginDataSet.getExtractedBalance());
    localEditor.putString("SIPPort", "5060");
    localEditor.putString("CompanyUrl", this.parsedLoginDataSet.getCompanyUrl());
    localEditor.putString("useremail", this.username);
    localEditor.putString("password", this.password);
    localEditor.putString("AccessNumber", this.parsedLoginDataSet.getAccessNumber());
    localEditor.putString("ServiceType", this.parsedLoginDataSet.getServiceType());
    localEditor.putBoolean("signinPref", this.signinpref.booleanValue());
    localEditor.putString("AllowAccessNumber", this.parsedLoginDataSet.getAllowAccessNumber());
    localEditor.putString("BalanceShow", this.parsedLoginDataSet.getBalanceShow());
    localEditor.putString("Culture", this.parsedLoginDataSet.getCulture());
    localEditor.putString("RateperminShow", this.parsedLoginDataSet.getRateperminShow());
    localEditor.putString("AllowVoiceMail", this.parsedLoginDataSet.getAllowVoiceMail());
    localEditor.putString("TransferOptionsFlag", this.parsedLoginDataSet.getTransferOptionsFlag());
    localEditor.putString("CallLicenseType", this.parsedLoginDataSet.getCallLicenseType());
    localEditor.putString("AllowG729", this.parsedLoginDataSet.getAllowG729());
    localEditor.commit();
    String str2 = localSharedPreferences.getString("CallLicenseType", "");
    String str3 = localSharedPreferences.getString("ServiceType", "");
    if ((str2.equalsIgnoreCase("")) || (str2.equalsIgnoreCase("None")))
    {
      if (!str3.equalsIgnoreCase("PBX"))
      localEditor.putString("CallLicenseTypeLocal", "VoIP").commit();
      localEditor.putString("CallLicenseType", "Voip").commit();
    }
    while (true)
    {
      String str4 = localSharedPreferences.getString("CallLicenseType", "Both");
      if (!str4.equalsIgnoreCase("Both"))
        localEditor.putString("CallLicenseTypeLocal", str4).commit();
      String str5 = localSharedPreferences.getString("CallLicenseTypeLocal", "Not");
      if ((str5.equalsIgnoreCase("Not")) || (str5.equalsIgnoreCase("None")))
      {
        localEditor.putString("CallLicenseTypeLocal", "VoIP");
        localEditor.commit();
      }
      localEditor.putString("CallLicenseType", "None").putBoolean("signinPref", false).commit();
      return;
      
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.VisitorPatterns.PreferenceManagementVisitor
 * JD-Core Version:    0.6.2
 */