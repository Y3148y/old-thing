package com.example.r1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class BluetoothController {
    private BluetoothAdapter mAdapter;

    //问题1
    public void BlueToothController() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //是否支持蓝牙
    public boolean isSupportBlueTooth() {
        if (mAdapter != null) {
            return true;
        } else {
            return false;
        }
    }

    //判断当前蓝牙状态
    public boolean getBlueToothStatus() {
        assert (mAdapter != null);
        return mAdapter.isEnabled();

    }

    //问题2 permission_connect permission_check
    //打开蓝牙
    @SuppressLint("MissingPermission")
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
        //    mAdapter.enable();
    }

    //问题3 permission_advertise permission_check
    //打开蓝牙可见性
    @SuppressLint("MissingPermission")
    public void enableVisibly(Context context) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);

    }


    //查找设备
    @SuppressLint("MissingPermission")
    public void findDevice() {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }


    //获取绑定设备
    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> getbondedDevicelist() {
        return new ArrayList<>(mAdapter.getBondedDevices());

    }

    //关闭蓝牙
    @SuppressLint("MissingPermission")
    public void turnoffBlueTooth() {
        mAdapter.disable();

    }
}
