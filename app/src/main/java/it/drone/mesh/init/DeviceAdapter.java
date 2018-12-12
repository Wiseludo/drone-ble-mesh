package it.drone.mesh.init;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import it.drone.mesh.R;
import it.drone.mesh.models.Device;
import it.drone.mesh.roles.common.RoutingTable;
import it.drone.mesh.roles.common.Utility;
import it.drone.mesh.tasks.ConnectBLETask;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private static final String TEST_MESSAGE = "I AM A TEST MESSAGE";
    private ArrayList<Device> devices;
    private Context _applicationContext;
    private final static String TAG = DeviceAdapter.class.getSimpleName();

    DeviceAdapter(Context _applicationContext) {
        RoutingTable routingTable = RoutingTable.getInstance();
        this.devices = routingTable.getDeviceList();
        routingTable.subscribeToUpdates(new RoutingTable.OnRoutingTableUpdateListener() {
            @Override
            public void OnDeviceAdded(Device device) {
                notifyDataSetChanged();
            }

            @Override
            public void OnDeviceRemoved(Device device) {
                notifyDataSetChanged();
            }
        });
        this._applicationContext = _applicationContext;
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_result, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceViewHolder deviceViewHolder, int i) {
        final Device device = devices.get(i);

        deviceViewHolder.id.setText(device.getId());
        deviceViewHolder.power.setText(device.getSignalPower());
        deviceViewHolder.lastTime.setText(device.getTimeSinceString());
        deviceViewHolder.input.setText(device.getInput());
        deviceViewHolder.output.setText(device.getOutput());
        deviceViewHolder.testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(device.getId(), TEST_MESSAGE, new Utility.OnMessageSentListener() {
                    @Override
                    public void OnMessageSent(String message) {
                        device.writeInput(message);
                        deviceViewHolder.input.setText(device.getInput());
                    }

                    @Override
                    public void OnCommunicationError(String error) {
                        deviceViewHolder.input.setText(String.format("%s%s", deviceViewHolder.input.getText(), error));
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }

    /**
     * Invia il messaggio messagge al device identificato come destinationID
     *
     * @param destinationId Id della device da raggiungere
     * @param message       messaggio da inviare
     * @param listener      Listener di risposta
     */
    private void sendMessage(String destinationId, String message, Utility.OnMessageSentListener listener) {
        ConnectBLETask connectBLETask = new ConnectBLETask();
        connectBLETask.startClient();
        connectBLETask.sendMessage(message, destinationId, listener);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView id, lastTime, power, input, output;
        Button testButton;

        DeviceViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.device_id);
            lastTime = itemView.findViewById(R.id.last_time_device);
            power = itemView.findViewById(R.id.power_device);
            input = itemView.findViewById(R.id.inputText);
            output = itemView.findViewById(R.id.outputText);
            testButton = itemView.findViewById(R.id.button_test_message);
        }
    }

}
