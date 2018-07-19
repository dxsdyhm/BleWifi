package com.qmx.blewifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.qmx.entity.BleConfig;
import com.qmx.entity.BleLESettingCompment;
import com.qmx.entity.BleWifiInfo;
import com.qmx.entity.BluePackage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiConfigActivity extends AppCompatActivity {
    @BindView(R.id.et_wifiname)
    EditText etWifiname;
    @BindView(R.id.et_wifipwd)
    EditText etWifipwd;
    @BindView(R.id.tx_state)
    TextView txState;
    @BindView(R.id.btn_sendinfo)
    Button btnSendinfo;

    private BleDevice device;

    private String wifiname;
    private String wifiPwd;

    private BleLESettingCompment compment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonfig);
        ButterKnife.bind(this);
        device = getIntent().getParcelableExtra(BleDevice.class.getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showConfigState(BluePackage setting){
        Log.e("dxsTest","succ:"+String.valueOf(setting.getData()));
        txState.append(Arrays.toString(setting.getData()));
        txState.append("\n");
    }

    @OnClick(R.id.btn_sendinfo)
    public void onViewClicked() {
        wifiname = etWifiname.getText().toString().trim();
        wifiPwd = etWifipwd.getText().toString().trim();
        BleWifiInfo info=new BleWifiInfo(wifiname,wifiPwd);
        compment=new BleLESettingCompment(info.getRealData());
        Log.e("dxsTest","realdata:"+Arrays.toString(info.getRealData()));
        sendData(0);
    }

    private void sendData(final int position){
        if(!compment.getList().containsKey(position)){
            return;
        }
        final BluePackage setting=compment.getList().get(position);
        BleManager.getInstance().write(
                device,
                BleConfig.UUID_SERVER,
                BleConfig.UUID_CHARWRITE,
                setting.getSettingData(),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        // 发送数据到设备成功
                        Log.e("dxsTest","onWriteSuccess:"+ Arrays.toString(justWrite));
                        EventBus.getDefault().post(setting);
                        sendData(position+1);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        // 发送数据到设备失败
                        Log.e("dxsTest","exception"+exception.getDescription());
                    }
                });
    }
}
