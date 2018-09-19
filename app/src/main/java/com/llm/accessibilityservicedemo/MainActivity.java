package com.llm.accessibilityservicedemo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Button closeButton;
    private TextView logTv;
    StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注册EventBus
        EventBus.getDefault().register(this);
        initView();

    }

    private void initView() {
        stringBuilder = new StringBuilder();
        button = findViewById(R.id.my_button);
        button.setOnClickListener(this);
        closeButton = findViewById(R.id.close_bt);
        closeButton.setOnClickListener(this);
        logTv = findViewById(R.id.log_tv);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.my_button) {
            goAccess();
        } else if (view.getId() == R.id.close_bt) {
            goAccess();
        }
    }

    /**
     * 接收服务log数据
     *
     * @param str
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(String str) {
        if (!TextUtils.isEmpty(str)) {
            stringBuilder.append(str + "\n");
            logTv.setText(str);
        }
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().unregister(this);
    }
}
