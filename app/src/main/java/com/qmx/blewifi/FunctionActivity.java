package com.qmx.blewifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FunctionActivity extends AppCompatActivity {

    @BindView(R.id.btn_client)
    Button btnClient;
    @BindView(R.id.btn_server)
    Button btnServer;
    @BindView(R.id.btn_unzip)
    Button btnUnzip;

    String src= PathUtils.getExternalStoragePath()+File.separator+"qmorn"+ File.separator+"5a14db9568fa43a488e51eb97466a552.zip";
    String des= PathUtils.getExternalStoragePath()+File.separator+"qmorn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_client)
    public void onBtnClientClicked() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @OnClick(R.id.btn_server)
    public void onBtnServerClicked() {
        startActivity(new Intent(this, GattServerActivity.class));
    }

    @OnClick(R.id.btn_unzip)
    public void onViewClicked() {
        try {
            ZipUtils.unzipFile(src,des);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(e);
        }
    }
}
