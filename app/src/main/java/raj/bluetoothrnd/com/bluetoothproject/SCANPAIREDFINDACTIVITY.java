package raj.bluetoothrnd.com.bluetoothproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;
import static android.content.ContentValues.TAG;


public class SCANPAIREDFINDACTIVITY extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;

    ListView listDevicesFound;
    Button btnScanDevice,paired;
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;

    ArrayAdapter<String> btArrayAdapter;
    List<BluetoothDevice> mBluetoothDeviceList;

    boolean isScanButtonPress=false,isPairedButtonPress=false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnScanDevice = (Button)findViewById(R.id.StartScanButton);
        paired = (Button)findViewById(R.id.paired);

        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listDevicesFound = (ListView)findViewById(R.id.list);
        btArrayAdapter = new ArrayAdapter<String>(SCANPAIREDFINDACTIVITY.this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        mBluetoothDeviceList=new ArrayList<BluetoothDevice>() ;

        CheckBlueToothState();

        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
        paired.setOnClickListener(btnPairedDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));


        listDevicesFound.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Toast.makeText(SCANPAIREDFINDACTIVITY.this, btArrayAdapter.getItem(position) , Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
//                intent.putExtra(EXTRA_DEVICE, mBluetoothDeviceList.get(position));
//                int PAIRING_VARIANT_PIN = 272;
//                intent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
//                sendBroadcast(intent);
                if(isScanButtonPress) {
                    try {
                        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
                        Method createBondMethod = class1.getMethod("createBond");
                        Boolean returnValue = (Boolean) createBondMethod.invoke(mBluetoothDeviceList.get(position));

                    } catch (Exception e) {

                    }
                }

                if(isPairedButtonPress) {
                    try {
                        Method m = mBluetoothDeviceList.get(position).getClass()
                                .getMethod("removeBond", (Class[]) null);
                        m.invoke(mBluetoothDeviceList.get(position), (Object[]) null);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            }
        });


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            stateBluetooth.setText("Bluetooth NOT support");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                }else{
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            }else{
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private Button.OnClickListener btnScanDeviceOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
//            Intent discoverableIntent =
//                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60*60);
//            startActivity(discoverableIntent);
            isScanButtonPress=true;isPairedButtonPress=false;
            btArrayAdapter.clear();
            mBluetoothDeviceList.clear();
            bluetoothAdapter.startDiscovery();
        }};

    private Button.OnClickListener btnPairedDeviceOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
//            Intent discoverableIntent =
//                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60*60);
//            startActivity(discoverableIntent);

            Set<BluetoothDevice> pairedDevices =bluetoothAdapter.getBondedDevices();

            isScanButtonPress=false;isPairedButtonPress=true;
            btArrayAdapter.clear();
            mBluetoothDeviceList.clear();
            for(BluetoothDevice paired:pairedDevices){
                btArrayAdapter.add(paired.getName() + "\n" + paired.getAddress());
            }

            mBluetoothDeviceList.addAll(pairedDevices);
            btArrayAdapter.notifyDataSetChanged();

         //   bluetoothAdapter.startDiscovery();
        }};




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBluetoothDeviceList.add(device);
                btArrayAdapter.notifyDataSetChanged();
            }
        }};

}