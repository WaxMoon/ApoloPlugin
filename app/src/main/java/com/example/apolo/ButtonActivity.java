package com.example.apolo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class ButtonActivity extends AppCompatActivity {
    protected String TAG = "Button." + getClass().getSimpleName();

    private LinearLayout mContainer;

    public Intent withIntent(Class<? extends ButtonActivity> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("serial", UUID.randomUUID().toString());
        return intent;
    }

    public Intent withIntent(Class<? extends ButtonActivity> cls, int flags) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("serial", UUID.randomUUID().toString());
        intent.addFlags(flags);
        return intent;
    }

    public Intent withIntent(Intent intent, int flags) {
        intent.putExtra("serial", UUID.randomUUID().toString());
        intent.addFlags(flags);
        return intent;
    }

    public String serial(Intent intent) {
        return intent.getStringExtra("serial");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + serial(intent) + "  " + getTaskId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + serial(getIntent()) + " " + getTaskId());
        mContainer = new LinearLayout(this);
        mContainer.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mContainer);
        setContentView(scrollView);
        addButton(getClass().getName() + " " + getTaskId(), null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    protected Button addButton(final String text, final View.OnClickListener clickListener) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setOnClickListener(clickListener);
        mContainer.addView(button, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return button;
    }


}
