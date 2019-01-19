package com.qmx.blewifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FunctionActivity extends AppCompatActivity {

    @BindView(R.id.btn_client)
    Button btnClient;
    @BindView(R.id.btn_server)
    Button btnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_client)
    public void onBtnClientClicked() {
        startActivity(new Intent(this,MainActivity.class));
    }

    @OnClick(R.id.btn_server)
    public void onBtnServerClicked() {
        startActivity(new Intent(this,GattServerActivity.class));
    }
}
