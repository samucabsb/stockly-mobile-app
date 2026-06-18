package br.com.samuel.stockly;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String PREFS_NAME = "stockly_session";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private final SharedPreferences preferences;

    public Session(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public long id() {
        return preferences.getLong(KEY_ID, -1);
    }

    public String name() {
        return preferences.getString(KEY_NAME, "");
    }

    public boolean logged() {
        return id() > 0;
    }

    public void save(long id, String name) {
        preferences.edit()
                .putLong(KEY_ID, id)
                .putString(KEY_NAME, name)
                .apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
