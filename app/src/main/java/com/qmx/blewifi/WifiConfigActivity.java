package com.qmx.blewifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.qmx.entity.BleConfig;
import com.qmx.entity.BleLESettingCompment;
import com.qmx.entity.BleWifiInfo;
import com.qmx.entity.BluePackage;
import com.qmx.entity.ConnectResult;
import com.qmx.utils.ByteUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
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
        BleManager.getInstance().notify(
                device,
                BleConfig.UUID_SERVER,
                BleConfig.UUID_CHARREAD,
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        Log.e("dxsTest","onIndicateSuccess");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.e("dxsTest","onNotifyFailure"+exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        Log.e("dxsTest","onCharacteristicChanged"+Arrays.toString(data));
                        if(data[0]==2){
                            if(data[1]==0){
                                updateUI("正在连接-->");
                            }else if(data[1]==1) {
                                updateUI("连接成功-->");
                            }else {
                                updateUI("连接失败-->");
                            }
                        }
                    }
                });
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
        updateUI(Arrays.toString(setting.getData()));
    }

    private void updateUI(String text){
        txState.append(text);
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
                        sendData(position);
                        Log.e("dxsTest","exception"+exception.getDescription());
                    }
                });

    }
}
