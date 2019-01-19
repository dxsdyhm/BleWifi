package com.qmx.blewifi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.qmx.entity.BleConfig;
import com.qmx.entity.BleLESettingCompment;
import com.qmx.entity.BleWifiInfo;
import com.qmx.entity.ConnectResult;
import com.qmx.utils.BluetoothChatService;
import com.qmx.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OldBlueConfigActivity extends AppCompatActivity {

    @BindView(R.id.et_wifiname)
    EditText etWifiname;
    @BindView(R.id.et_wifipwd)
    EditText etWifipwd;
    @BindView(R.id.btn_sendinfo)
    Button btnSendinfo;
    @BindView(R.id.tx_state)
    TextView txState;


    private BleDevice device;

    private String wifiname;
    private String wifiPwd;

    private BleLESettingCompment compment;
    private BluetoothChatService mChatService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificonfig);
        ButterKnife.bind(this);
        device = getIntent().getParcelableExtra(BleDevice.class.getSimpleName());
        initChat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    //初始化服务端
    private void initChat() {
        mChatService = new BluetoothChatService(this, mHandler);
        mChatService.connect(device.getDevice(),false);
    }

    //开始监听客户端连接
    private void startService(){
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
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
        Log.e("dxsTest","realdata:"+ Arrays.toString(info.getRealData()));
        mChatService.write(info.getRealData());
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //告诉客户端已经连上（后期可能需要返回关键信息）
                            mChatService.write(new byte[]{0,0,0,0});
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readBuf[0]==2){
                        ConnectResult result=new ConnectResult(readBuf);
                        if(result.getConnectState()==0){
                            updateUI("正在连接-->");
                        }else if(result.getConnectState()==1) {
                            updateUI("连接成功-->");
                        }else {
                            updateUI("连接失败-->"+result.getErrorCode());
                        }
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:

                    break;
                case Constants.MESSAGE_TOAST:

                    break;
            }
        }
    };
}
