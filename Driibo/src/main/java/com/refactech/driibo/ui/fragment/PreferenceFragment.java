
package com.refactech.driibo.ui.fragment;

import com.refactech.driibo.R;
import com.refactech.driibo.util.PreferenceUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.text.TextUtils;

/**
 * Created by Issac on 7/19/13.
 */
public class PreferenceFragment extends android.preference.PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private String mKeyLogin;

    private Preference mLoginPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        // 设置版本号
        Preference versionPreference = findPreference(getString(R.string.pref_key_version));
        PackageInfo packageInfo;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionPreference.setTitle(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mKeyLogin = getString(R.string.pref_key_login);
        mLoginPreference = findPreference(mKeyLogin);
        mLoginPreference.setOnPreferenceChangeListener(this);
        String userName = PreferenceUtils.getPrefString(mKeyLogin, null);
        if (!TextUtils.isEmpty(userName)) {
            mLoginPreference.setSummary(userName);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(mKeyLogin)) {
            mLoginPreference.setSummary((String) newValue);
        }
        return true;
    }
}
