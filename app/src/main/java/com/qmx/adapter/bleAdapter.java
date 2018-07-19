package com.qmx.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmx.blewifi.R;
import com.qmx.blewifi.WifiConfigActivity;
import com.qmx.entity.bleViewMode;

import me.drakeet.multitype.ItemViewBinder;

/**
 * 展示蓝牙列表
 */
public class bleAdapter extends ItemViewBinder<bleViewMode,bleAdapter.TextHolder> {

    @NonNull
    @Override
    protected TextHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_ble, parent, false);
        return new TextHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TextHolder holder, @NonNull final bleViewMode item) {
        holder.text.setText("hello: " + item.getDevice().getName());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(holder.text.getContext(), WifiConfigActivity.class);
                intent.putExtra(item.getDevice().getClass().getSimpleName(),item.getDevice());
                holder.text.getContext().startActivity(intent);
            }
        });
    }

    static class TextHolder extends RecyclerView.ViewHolder {

        private @NonNull
        final TextView text;

        TextHolder(@NonNull View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.tx_blename);
        }
    }
}
