package com.frogshealth.lan.transmission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**********************************************************************
 *
 *
 * @author ankie
 * @类名 ChooseFileActivity
 * @包名 com.frogshealth.lan.transmission
 * @创建日期 2018/8/2
/**********************************************************************/

public class ChooseFileActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 返回按钮
     */
    private TextView mBackView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        bindViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void bindViews() {
        mBackView = findViewById(R.id.tv_layout_choose_file_back);
        mBackView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_layout_choose_file_back:
                finish();
                break;
            default:
                break;
        }
    }
}
