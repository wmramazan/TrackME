package com.adnagu.trackme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.create(this, null).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    public static class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            findPreference("about_app").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.information)
                            .setTitle(R.string.about_app)
                            .setMessage(getString(R.string.app_name) +
                                    " v" +
                                    BuildConfig.VERSION_NAME +
                                    "\n\n" +
                                    getString(R.string.used_libraries) +
                                    getString(R.string.libraries))
                            .setPositiveButton(android.R.string.ok, null)
                            .create();
                    dialog.show();
                    return false;
                }
            });

            findPreference("reset_database").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.warning)
                            .setTitle(R.string.reset_database)
                            .setMessage(R.string.reset_database_dialog_text)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TrackME.db.resetTables();
                                }
                            })
                            .create();
                    dialog.show();
                    return false;
                }
            });
        }
    }


}
