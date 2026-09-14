package com.termux.app.lumina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.termux.R;

import java.util.List;

/** ListView adapter for the store catalog. */
public class LuminaStoreAdapter extends BaseAdapter {

    public interface OnInstallClicked {
        void onClick(LuminaCatalog.Item item);
    }

    private final Context context;
    private final List<LuminaCatalog.Item> items;
    private final OnInstallClicked listener;

    public LuminaStoreAdapter(Context context, List<LuminaCatalog.Item> items, OnInstallClicked listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lumina_store, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.lumina_item_name);
            holder.tagline = convertView.findViewById(R.id.lumina_item_tagline);
            holder.category = convertView.findViewById(R.id.lumina_item_category);
            holder.size = convertView.findViewById(R.id.lumina_item_size);
            holder.button = convertView.findViewById(R.id.lumina_item_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final LuminaCatalog.Item item = items.get(position);
        holder.name.setText(item.name);
        holder.tagline.setText(item.tagline);
        holder.category.setText(item.category);
        holder.size.setText(item.sizeHint);

        if (item.available) {
            holder.button.setText(R.string.lumina_action_install);
            holder.button.setEnabled(true);
            holder.button.setOnClickListener(v -> listener.onClick(item));
        } else {
            holder.button.setText(R.string.lumina_action_soon);
            holder.button.setEnabled(false);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView tagline;
        TextView category;
        TextView size;
        MaterialButton button;
    }
}
