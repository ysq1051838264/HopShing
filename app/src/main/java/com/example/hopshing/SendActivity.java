package com.example.hopshing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

/**
 * Created by yangshuquan on 2018/8/12.
 */

public class SendActivity extends AppCompatActivity {
    String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    String UUID_CHARACTERISTIC_WRITE = "0000ffe1-0000-1000-8000-00805f9b34fb";
    String UUID_CHARACTERISTIC_NOTIFY = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static final String KEY_DATA = "key_data";

    private BleDevice bleDevice;

    EditText data;
    TextView back;
    Button send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        data = (EditText) findViewById(R.id.data);
        back = (TextView) findViewById(R.id.back);
        send = (Button) findViewById(R.id.send);

        initData();
    }

    private void initData() {

        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null)
            finish();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleManager.getInstance().write(
                        bleDevice,
                        UUID_SERVICE,
                        UUID_CHARACTERISTIC_WRITE,
                        HexUtil.hexStringToBytes(data.getText().toString()),
                        new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        back.setText( "write success, current: " + current
                                                + " total: " + total
                                                + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                    }
                                });
                            }

                            @Override
                            public void onWriteFailure(final BleException exception) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        back.setText(  exception.toString());
                                    }
                                });
                            }
                        });
            }
        });
    }
}
