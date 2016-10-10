package com.ouyben.slidelockview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private SlideLockView mSlideLockView;
    private RelativeLayout mContentMain;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("滑动解锁");
        setSupportActionBar(mToolbar);

        mSlideLockView = (SlideLockView) findViewById(R.id.slideLockView);
        mContentMain = (RelativeLayout) findViewById(R.id.content_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(this);
        mSlideLockView.setLockListener(new SlideLockView.OnLockListener() {
            @Override
            public void onOpenLockSuccess() {
                Toast.makeText(MainActivity.this, "解锁成功", Toast.LENGTH_SHORT).show();
                mSlideLockView.setTipText("解锁成功");
                mSlideLockView.setTipsTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                break;
        }
    }
}
