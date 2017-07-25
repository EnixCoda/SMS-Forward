package one.enix.smsforward;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[] {
                Manifest.permission.SEND_SMS,
        }, 0);

        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_shared_preferences_key), MainActivity.MODE_PRIVATE);
        initPhoneNumberInput(sharedPreferences);
    }

    private void initPhoneNumberInput(final SharedPreferences sharedPreferences) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText editText = (EditText) findViewById(R.id.targetPhoneNumberInput);
        editText.setText(sharedPreferences.getString(getString(R.string.target_phone_number_key), getString(R.string.default_forward_number)));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(getString(R.string.target_phone_number_key), s.toString()).apply();
            }
        });
    }
}
