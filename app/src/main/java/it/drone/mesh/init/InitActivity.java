package it.drone.mesh.init;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import it.drone.mesh.R;
import it.drone.mesh.roles.common.exceptions.NotEnabledException;
import it.drone.mesh.roles.common.exceptions.NotSupportedException;
import it.drone.mesh.roles.server.BLEServer;

public class InitActivity extends Activity {

    ImageView isBtEnabled;
    ImageView isScanning;
    ImageView whoAreYou;
    ImageView startServices;
    BLEServer server;

    RecyclerView recyclerDeviceList;
    DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        isBtEnabled = findViewById(R.id.bt_enabled);
        isScanning = findViewById(R.id.scanning);
        whoAreYou = findViewById(R.id.whatami);
        startServices = findViewById(R.id.startServices);
        isBtEnabled.setImageResource(((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled() ? R.drawable.ic_bluetooth_24dp : R.drawable.ic_bluetooth_disabled_black_24dp);

        startServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) view).setImageResource(R.drawable.ic_pause);
                initializeService();
            }
        });

        recyclerDeviceList = findViewById(R.id.recy_scan_results);
        deviceAdapter = new DeviceAdapter(server.getRoutingTable().getDevices());
        recyclerDeviceList.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetChanged();
    }

    private void initializeService() {
        try {
            server = BLEServer.getInstance(this);
        } catch (NotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (NotEnabledException e) {
            isBtEnabled.setImageResource(R.drawable.ic_bluetooth_disabled_black_24dp);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
