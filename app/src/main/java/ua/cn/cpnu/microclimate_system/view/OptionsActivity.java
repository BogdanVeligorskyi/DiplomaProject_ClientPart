package ua.cn.cpnu.microclimate_system.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import ua.cn.cpnu.microclimate_system.R;
import ua.cn.cpnu.microclimate_system.model.FileProcessing;

// Options Activity, where you can change app settings
public class OptionsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notificationsSwitch;
    private RadioButton darkThemeRadioButton;
    private RadioButton whiteThemeRadioButton;
    private final int[] options = new int[2];

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        if (savedInstanceState != null) {
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            options[0] = getIntent().
                    getIntExtra(MainActivity.IS_NOTIFICATIONS_ON, 0);
            options[1] = getIntent().
                    getIntExtra(MainActivity.IS_DARK_MODE_ON, 0);
        }

        // 'Save' button
        findViewById(R.id.save_button)
                .setOnClickListener(button -> {
                    try {
                        FileProcessing.saveOptions(getApplicationContext(), options);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
                    intent.putExtra(MainActivity.IS_DARK_MODE_ON, options[1]);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                });


        // 'Notifications' switch
        notificationsSwitch = findViewById(R.id.notifications_switch);
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                options[0] = 1;
            } else {
                options[0] = 0;
            }
        });

        // 'Theme' radio group
        RadioGroup themeRadioGroup = findViewById(R.id.theme_radiogroup);
        darkThemeRadioButton = findViewById(R.id.dark_theme_radiobutton);
        whiteThemeRadioButton = findViewById(R.id.white_theme_radiobutton);
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // on theme change
            switch (checkedId) {
                case R.id.dark_theme_radiobutton:
                    options[1] = 1;
                    break;
                case R.id.white_theme_radiobutton:
                    options[1] = 0;
                    break;
            }
        });

        initOptions();

    }

    // initialize options
    private void initOptions() {

        // notifications
        notificationsSwitch.setChecked(options[0] != 0);

        // theme
        if (options[1] == 0) {
            whiteThemeRadioButton.setChecked(true);
        } else {
            darkThemeRadioButton.setChecked(true);
        }
    }

    // saving current state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
        outState.putInt(MainActivity.IS_DARK_MODE_ON, options[1]);
    }

}
