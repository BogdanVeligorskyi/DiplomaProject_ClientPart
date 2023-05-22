package ua.cn.cpnu.microclimate_system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class OptionsActivity extends AppCompatActivity {

    RadioGroup languageRadioGroup;
    RadioGroup themeRadioGroup;
    private int[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new int[3];
        if (savedInstanceState != null) {
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_UKRAINIAN_ON);
            options[2] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            options[0] = getIntent().
                    getIntExtra(MainActivity.IS_NOTIFICATIONS_ON, 0);
            options[1] = getIntent().
                    getIntExtra(MainActivity.IS_UKRAINIAN_ON, 0);
            options[2] = getIntent().
                    getIntExtra(MainActivity.IS_DARK_MODE_ON, 0);
        }

        setContentView(R.layout.activity_options);

        findViewById(R.id.save_button)
                .setOnClickListener(button -> {
                    try {
                        saveOptions();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
                    intent.putExtra(MainActivity.IS_UKRAINIAN_ON, options[1]);
                    intent.putExtra(MainActivity.IS_DARK_MODE_ON, options[2]);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                });


        Switch notificationsSwitch = findViewById(R.id.notifications_switch);
        languageRadioGroup = findViewById(R.id.language_radiogroup);
        themeRadioGroup = findViewById(R.id.theme_radiogroup);
        RadioButton ukrLanguageRadioButton = findViewById(R.id.ukr_lang_radiobutton);
        RadioButton engLanguageRadioButton = findViewById(R.id.eng_lang_radiobutton);
        RadioButton darkThemeRadioButton = findViewById(R.id.dark_theme_radiobutton);
        RadioButton whiteThemeRadioButton = findViewById(R.id.white_theme_radiobutton);

        // notifications
        notificationsSwitch.setChecked(options[0] != 0);

        // language
        if (options[1] == 0) {
            engLanguageRadioButton.setChecked(true);
        } else {
            ukrLanguageRadioButton.setChecked(true);
        }
        // theme
        if (options[2] == 0) {
            whiteThemeRadioButton.setChecked(true);
        } else {
            darkThemeRadioButton.setChecked(true);
        }

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                options[0] = 1;
            } else {
                options[0] = 0;
            }
        });

        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.eng_lang_radiobutton:
                    Toast.makeText(getApplicationContext(), "English",
                            Toast.LENGTH_SHORT).show();
                    options[1] = 0;
                    break;
                case R.id.ukr_lang_radiobutton:
                    Toast.makeText(getApplicationContext(), "Українська",
                            Toast.LENGTH_SHORT).show();
                    options[1] = 1;
                    break;
            }
        });

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // on radio button check change
            switch (checkedId) {
                case R.id.dark_theme_radiobutton:
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    options[2] = 1;
                    break;
                case R.id.white_theme_radiobutton:
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    options[2] = 0;
                    break;
            }
        });

    }

    // save options to settings.txt file
    private void saveOptions() throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (getApplicationContext().openFileOutput(MainActivity.SETTINGS_FILENAME,
                                    Context.MODE_PRIVATE));
            outputStreamWriter.write("notifications=" + options[0] + "\n"
                    + "language=" + options[1] + "\n"
                    + "theme=" + options[2]);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // saving current state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
        outState.putInt(MainActivity.IS_UKRAINIAN_ON, options[1]);
        outState.putInt(MainActivity.IS_DARK_MODE_ON, options[2]);
    }

}
