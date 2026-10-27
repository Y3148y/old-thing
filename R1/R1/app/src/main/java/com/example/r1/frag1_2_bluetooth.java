package com.example.r1;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Set;

public class frag1_2_bluetooth extends Fragment {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ArrayAdapter<String> listAdapter;
    private BluetoothAdapter mAdapter;




//    Button btn_test;

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
                    Toast.makeText(getActivity().getApplicationContext(), "没有设备", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        //广播注册
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.device_name);
        ListView lv_device = getActivity().findViewById(R.id.listView1);
        lv_device.setAdapter(listAdapter);

        //选择连接设备
        lv_device.setOnItemClickListener((arg0, v, arg2, arg3) -> {
            String info = ((TextView) v).getText().toString();
            if (info.equals("没有已配对设备")) {
                Toast.makeText(getActivity().getApplicationContext(), "没有已配对设备", Toast.LENGTH_LONG).show();
            } else {
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                BluetoothDevice device;
                device = mAdapter.getRemoteDevice(address);
                ConnectThread(device);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
                //注意该fragment中需要开启接收数据线程，因此这里只是隐藏该fragment，而不是remove移除
//                getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        });
        PrintDevice();

//        btn_test = getActivity().findViewById(R.id.btn_test);

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag1_2_bluetooth, container, false);
        view.bringToFront();
        view.setAlpha(1.f);
        view.setTransitionAlpha(1.0f);
        return view;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null){
            mAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(mReceiver);

    }

    @SuppressLint("MissingPermission")
    public void ConnectThread(BluetoothDevice device) {
        try {
            MainActivity.mmSocket = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            mAdapter.cancelDiscovery();
            MainActivity.mmSocket.connect();
            Toast.makeText(getActivity().getApplicationContext(), "连接成功", Toast.LENGTH_LONG).show();
//            Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show();
            MainActivity.CONNECT_STATUS = true;
            // 接收数据进程
            ReceiverData2 receiverDataThread = new ReceiverData2();
            receiverDataThread.start();
//            MainActivity.ReceiveData receivethread = new MainActivity().ReceiveData();
//            ReceiveData receivethread = new ReceiveData();// 连接成功后开启接收数据服务
//            receivethread.start();
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), "连接失败", Toast.LENGTH_SHORT)
                    .show();
            MainActivity.CONNECT_STATUS = false;
            try {
                MainActivity.mmSocket.close();
            } catch (IOException e2) {
                e.printStackTrace();
            }
        }
    }

    public class ReceiverData2 extends Thread{
        public ReceiverData2(){
            try {
                MainActivity.mmInStream = MainActivity.mmSocket.getInputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            super.run();
            int bytes = 0;
            byte[] buffer = new byte[1024];
            while (true) {
                try {
//                    final String readStr = new String(buffer, 0, bytes);
                    if (MainActivity.mmInStream.available() > 0 == false) {
                        continue;
                    } else {
                        Thread.sleep(200);
                    }
                    bytes = MainActivity.mmInStream.read(buffer);
                    final String readStr = new String(buffer, 0, bytes);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("xyz".equals(readStr)){
//                                btn_test.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                            if ("2378G".equals(readStr)){
//                                btn_test.setBackgroundColor(getResources().getColor(R.color.teal_700));
                            }
                        }
                    });
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (readStr.equals("xyz")) {
////                                tower_red.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                            }
//                            if ("248g".equals(readStr)){
////                                tower_red.setBackgroundColor(getResources().getColor(R.color.grey));
//                            }
//                        }
//                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
