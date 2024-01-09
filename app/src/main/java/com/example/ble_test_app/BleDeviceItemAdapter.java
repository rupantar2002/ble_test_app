package com.example.ble_test_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BleDeviceItemAdapter extends RecyclerView.Adapter<BleDeviceItemAdapter.BleDeviceItemViewHolder> {

    private ArrayList<BleDeviceItem> mbBleDeviceList;

    public static class BleDeviceItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mBleIconImage;
        public TextView mDeviceNameText;
        public TextView mDeviceMacText;

        public BleDeviceItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mBleIconImage=itemView.findViewById(R.id.bluetooth_icon_image);
            mDeviceNameText=itemView.findViewById(R.id.device_name_text);
            mDeviceMacText=itemView.findViewById(R.id.device_mac_text);
        }
    }

    public BleDeviceItemAdapter(ArrayList<BleDeviceItem> deviceList){
        mbBleDeviceList=deviceList;
    }

    @NonNull
    @Override
    public BleDeviceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.ble_device_item,parent,false);
        BleDeviceItemViewHolder deviceItemViewHolder = new BleDeviceItemViewHolder(view);
        return deviceItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceItemViewHolder holder, int position) {
        BleDeviceItem deviceItem=mbBleDeviceList.get(position);
        holder.mDeviceNameText.setText(deviceItem.getDeviceName());
        holder.mDeviceMacText.setText(deviceItem.getDeviceMac());
    }

    @Override
    public int getItemCount() {
        return mbBleDeviceList.size();
    }
}
