package com.example.r1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Set;

public class ListActivity extends Activity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ArrayAdapter<String> listAdapter;
    private BluetoothAdapter mAdapter;


    //广播接收者
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获取蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    listAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (listAdapter == null) {
                    Toast.makeText(ListActivity.this, "没有设备", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAdapter = BluetoothAdapter.getDefaultAdapter();



        //注册广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        ListView lv_device = findViewById(R.id.listView1);
        Button bt_find = findViewById(R.id.scanbutton);
        lv_device.setAdapter(listAdapter);

        bt_find.setOnClickListener(arg0 -> mAdapter.startDiscovery());

        //选择连接设备
        lv_device.setOnItemClickListener((arg0, v, arg2, arg3) -> {
            String info = ((TextView) v).getText().toString();
            if (info.equals("没有已配对设备")) {
                Toast.makeText(getApplicationContext(), "没有已配对设备", Toast.LENGTH_LONG).show();
            } else {
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        PrintDevice();
    }

    //打印已配对设备
    @SuppressLint("MissingPermission")
    public void PrintDevice() {
        //打印出已配对的设备
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                listAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            listAdapter.add("没有已配对设备");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

}
