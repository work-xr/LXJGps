package com.hsf1002.sky.xljgps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hsf1002.sky.xljgps.R;
import com.hsf1002.sky.xljgps.app.XLJGpsApplication;
import com.hsf1002.sky.xljgps.ui.MainActivity;

import java.util.List;

/**
 * Created by hefeng on 18-6-6.
 */

public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.ViewHolder> {
    private List<String> list;
    private onItemClickListener listener;
    private int selectedPos = -1;
    private int oldPos = -1;

    public MainRecycleAdapter(List<String> list)
    {
        this.list = list;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder
    {
        TextView itemName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.list_item_tv);
        }
    }

    public interface onItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final ViewHolder holder = new MainRecycleAdapter.ViewHolder(view);
        holder.itemName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.itemName, holder.getAdapterPosition());
            }
        });

        holder.itemName.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick(holder.itemName, holder.getAdapterPosition());
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = list.get(position);
        holder.itemName.setText(name);

        if(selectedPos == holder.getPosition()) {
            holder.itemName.setBackgroundColor(XLJGpsApplication.getAppContext().getResources().getColor(R.color.list_item_focuse));
        } else {
            holder.itemName.setBackgroundColor(XLJGpsApplication.getAppContext().getResources().getColor(R.color.background_holo_light));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
    *  author:  hefeng
    *  created: 18-7-31 下午4:38
    *  desc:    为了给list添加选中效果
    *  param:
    *  return:
    */
    public void refreshItem(int position) {
        if (selectedPos != -1) {
            oldPos  = selectedPos;
        }

        selectedPos = position;

        if (oldPos != -1) {
            notifyItemChanged(oldPos);
        }
        notifyItemChanged(selectedPos);
    }
}
