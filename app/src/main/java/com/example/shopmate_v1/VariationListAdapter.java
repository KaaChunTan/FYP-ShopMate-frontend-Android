package com.example.shopmate_v1;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VariationListAdapter extends RecyclerView.Adapter<VariationListAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<VariationData> dataArrayList;

    private OnItemsClickListener listener = null;
    int selected_position = -1;

    //constructor
    public VariationListAdapter(Activity activity, ArrayList<VariationData> dataArrayList) {
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }

    void setOnItemClickListener(OnItemsClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.variation_list_card_view,parent,false);
         return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariationListAdapter.MyViewHolder holder, int position) {
        VariationData data = dataArrayList.get(position);

        //this is for the first rendering only
        if(dataArrayList.get(position).getIs_selected()==true){
            selected_position = position;
            //need to change back
            dataArrayList.get(position).setIs_selected(false);
        }

        holder.variation_text_view.setText(data.getVariation_name());
        holder.variation_list_card_view.setBackgroundColor((selected_position == position)? Color.parseColor("#E4E4E4"):Color.parseColor("#FFFFFFFF"));
        holder.variation_list_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(data);
                }
                selected_position=position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CardView variation_list_card_view;
        TextView variation_text_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            variation_list_card_view = itemView.findViewById(R.id.variation_list_card_view);
            variation_text_view = itemView.findViewById(R.id.variation_text_view);



        }

    }

    //need for communication between activity and the recyclerview
    public interface OnItemsClickListener{
        void onItemClick(VariationData variation);
    }
}
