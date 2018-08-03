package com.example.hopshing;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hdr.blelib.BleManagerCallbacks;
import com.hdr.blelib.BleProfileService;
import com.hdr.wristband.BlePresenter;
import com.hdr.wristband.model.BleDevice;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements BlePresenter.BleView {

    Button btn;

    BlePresenter blePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        blePresenter = new BlePresenter(this);
        blePresenter.init();

        if (!mBluetoothAdapter.isEnabled()) {
            //启动修改蓝牙可见性的Intent
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置蓝牙可见性的时间，方法本身规定最多可见300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivityForResult(intent, 1);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @NotNull
    @Override
    public Context getCtx() {
        return this;
    }


    @Override
    public void connectSuccess() {
        Log.i("ysq", "连接成功,关闭蓝牙扫描");

        if (blePresenter.getBleService() != null && blePresenter.getBleService().getWristDecoder() != null)
            blePresenter.getBleService().getWristDecoder().getSaveValue();
    }

    @Override
    public void newScanDevice(@NotNull BleDevice device) {

    }

    @Override
    public void updateScanDevice(int index, @NotNull BleDevice device) {

    }
}
