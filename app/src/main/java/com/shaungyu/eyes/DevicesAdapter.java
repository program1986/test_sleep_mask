package com.shaungyu.eyes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    private List<BLEDeviceBean> devicesList;
    private Context context;

    private DevicesItemClickListener mListener;

    public void setmListener(DevicesItemClickListener mListener) {
        this.mListener = mListener;
    }

    public DevicesAdapter(Context context, List<BLEDeviceBean> devicesList) {
        this.context = context;
        this.devicesList = devicesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final BLEDeviceBean bleDeviceBean = devicesList.get(position);
        //holder.textView.setText(bleDeviceBean.getName()==null?bleDeviceBean.getAddress():bleDeviceBean.getName());
        holder.textView.setText(bleDeviceBean.getName());
        holder.txt_mac.setText(bleDeviceBean.getAddress());
        //holder.txt_rssi.setText(bleDeviceBean.getRssi());
        holder.layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onItemClick(bleDeviceBean);
                }
            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return devicesList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View layoutView;
        private TextView txt_mac,txt_rssi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutView = itemView;
            textView = itemView.findViewById(R.id.name);
            txt_mac = itemView.findViewById(R.id.txt_mac_address);
            txt_rssi = itemView.findViewById(R.id.txt_rssi);
        }

    }

    interface DevicesItemClickListener {
        void onItemClick(BLEDeviceBean item);
    }
}
