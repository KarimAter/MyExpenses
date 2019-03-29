package com.karim.ater.myexpenses.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.storage.StorageReference;
import com.karim.ater.myexpenses.Helpers.CloudOperations;
import com.karim.ater.myexpenses.Helpers.MySharedPrefs;
import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame, new MyPreferenceFragment())
                .commit();
        // <!-- TODO: Customize toolbar-->
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {

        // <!-- TODO: Notifications switch-->
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            Preference backup = getPreferenceManager().findPreference("backup");
            Preference sync = getPreferenceManager().findPreference("sync");
            backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (MySharedPrefs.getAuthenticationStatus(getContext())) {
                        CloudOperations cloudOperations = new CloudOperations(getActivity());
                        cloudOperations.backUpFromFireBase();
                    }
                    else Toast.makeText(getContext(), "Sign in", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (MySharedPrefs.getAuthenticationStatus(getContext())) {
                        CloudOperations cloudOperations = new CloudOperations(getActivity());
                        cloudOperations.syncOnFireBase();
                    }
                    else Toast.makeText(getContext(), "Sign in", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}
