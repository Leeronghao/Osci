package com.example.suer.uibestpractice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SUER on 2018/4/17.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> msgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView rightMsg;
        TextView leftMsg;
        public ViewHolder(View itemView) {
            super(itemView);
            leftlayout = (LinearLayout) itemView.findViewById(R.id.left_layout);
            rightlayout =(LinearLayout) itemView.findViewById(R.id.right_layout);
            rightMsg   = (TextView)     itemView.findViewById(R.id.right_msg);
            leftMsg    =  (TextView)    itemView.findViewById(R.id.left_msg);
        }
    }
    public MsgAdapter(List<Msg> msgList_1){
        msgList = msgList_1;
    }
    @NonNull
    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgAdapter.ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        if(msg.getType()==Msg.TYPE_RECEIVED){
            holder.leftlayout.setVisibility(View.VISIBLE);
            holder.rightlayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else {
            holder.rightlayout.setVisibility(View.VISIBLE);
            holder.leftlayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
