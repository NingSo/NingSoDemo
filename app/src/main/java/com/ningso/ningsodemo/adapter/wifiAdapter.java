package com.ningso.ningsodemo.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ningso.ningsodemo.R;
import com.ningso.ningsodemo.models.WiFiBean;

import java.util.List;

/**
 * Created by NingSo on 15/12/3.下午2:41
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class WiFiAdapter extends RecyclerView.Adapter<WiFiAdapter.WifiViewHolder> {
    private List<WiFiBean> mlist;

    public WiFiAdapter(List<WiFiBean> mlist) {
        this.mlist = mlist;
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WifiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, null, false));
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, int position) {
        WiFiBean wiFiBean = mlist.get(position);
        holder.tv_ssid.setText(wiFiBean.getSsid());
        if (TextUtils.isEmpty(wiFiBean.getKey())) {
            holder.tv_key.setText("未设置");
        } else {
            holder.tv_key.setText(wiFiBean.getKey());
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class WifiViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tv_ssid;
        private AppCompatTextView tv_key;

        public WifiViewHolder(View itemView) {
            super(itemView);
            tv_ssid = (AppCompatTextView) itemView.findViewById(R.id.tv_ssid);
            tv_key = (AppCompatTextView) itemView.findViewById(R.id.tv_key);
        }
    }
}

