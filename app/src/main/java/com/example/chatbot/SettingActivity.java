package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    RadioGroup rgLang;
    RadioButton rbKorean, rbEnglish;
    Switch switchTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //2022-01-19 오상민
        //튜토리얼 다시보기 버튼
        Button moveButton;
        moveButton = findViewById(R.id.moveButton);
        moveButton.setOnClickListener(onClickListener);


        rgLang = (RadioGroup)findViewById(R.id.rgLang);
        rbKorean = (RadioButton)findViewById(R.id.rbKorean);
        rbEnglish = (RadioButton)findViewById(R.id.rbEnglish);
        switchTTS = (Switch)findViewById(R.id.switchTTS);


        Boolean IsEnglish = PreferenceManager.getBoolean(SettingActivity.this, "IsEnglish");
        if(IsEnglish != null){
            rbEnglish.setChecked(IsEnglish);
        }

        Boolean IsTTS = PreferenceManager.getBoolean(SettingActivity.this, "IsTTS");
        if(IsTTS != null){
            switchTTS.setChecked(IsTTS);
        }

        rgLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rbKorean:{
                        PreferenceManager.setBoolean(SettingActivity.this, "IsKorean", rbKorean.isChecked());
                        PreferenceManager.setBoolean(SettingActivity.this, "IsEnglish", rbEnglish.isChecked());
                        break;
                    }
                    case R.id.rbEnglish:{
                        PreferenceManager.setBoolean(SettingActivity.this, "IsEnglish", rbEnglish.isChecked());
                        PreferenceManager.setBoolean(SettingActivity.this, "IsKorean", rbKorean.isChecked());
                        break;
                    }
                }
            }
        });

        switchTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.setBoolean(SettingActivity.this, "IsTTS", switchTTS.isChecked());
            }
        });
    }

    //2022-01-19 오상민
    //튜토리얼 다시보기 버튼 기능구현
    Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    };
}