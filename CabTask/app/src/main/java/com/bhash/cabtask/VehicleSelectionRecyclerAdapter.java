package com.bhash.cabtask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.List;



public class VehicleSelectionRecyclerAdapter extends RecyclerView.Adapter<VehicleSelectionRecyclerAdapter.VehicleSelectionViewHolder> {

    private static String TAG = VehicleSelectionRecyclerAdapter.class.getSimpleName();
    private List<VehicleSelectionAdapterModel> dataList;
    private Context context;
    private final VehicleClickListener vehicleClickListener;
    private VehicleSelectionViewHolder prevViewHolder;
    private int selectedLocation = -1;

    public VehicleSelectionRecyclerAdapter(List<VehicleSelectionAdapterModel> dataList, Context context, VehicleClickListener listener) {
        this.dataList = dataList;
        this.context = context;
        this.vehicleClickListener = listener;
    }

    /*public void setOnItemClickListener(VehicleClickListener VehicleClickListener) {
        this.vehicleClickListener = VehicleClickListener;
    }*/

    @Override
    public VehicleSelectionViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_car_selection_row, parent, false);

        return new VehicleSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VehicleSelectionViewHolder holder, final int position) {
        holder.itemImageView.setImageDrawable(context.getResources().getDrawable(dataList.get(position).getVehicleImageId()));
        holder.itemDataTextView.setText(dataList.get(position).getVehicleName());
        holder.getItemBarView().setVisibility(View.INVISIBLE);
        holder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.secondary_text));

        if(selectedLocation == position) {
            holder.getItemBarView().setVisibility(View.VISIBLE);
            holder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.colorAccent));

        }else {
            holder.getItemBarView().setVisibility(View.INVISIBLE);
            holder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.secondary_text));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicleClickListener.onItemClick(position, view);

                if(selectedLocation == position) {

                    selectedLocation = position;
                    holder.getItemBarView().setVisibility(View.INVISIBLE);
                    holder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.secondary_text));
                    // holder.getItemImageView().setVisibility(View.INVISIBLE);
                    notifyDataSetChanged();
                }else {
                    selectedLocation = position;
                    holder.getItemBarView().setVisibility(View.VISIBLE);
                    holder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.colorAccent));
                    // holder.getItemImageView().setBackground(context.getResources().getDrawable(R.drawable.car_hatchback_accent));
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void addItem(VehicleSelectionAdapterModel dataObj, int index) {
        dataList.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        dataList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class VehicleSelectionViewHolder extends RecyclerView.ViewHolder  {

        private ImageView itemImageView;
        private TextView itemDataTextView;
        private View itemBarView;
        LinearLayout linearLayout;

        public ImageView getItemImageView() {
            return itemImageView;
        }

        public void setItemImageView(ImageView itemImageView) {
            this.itemImageView = itemImageView;
        }

        public TextView getItemDataTextView() {
            return itemDataTextView;
        }

        public void setItemDataTextView(TextView itemDataTextView) {
            this.itemDataTextView = itemDataTextView;
        }

        public View getItemBarView() {
            return itemBarView;
        }

        public void setItemBarView(View itemBarView) {
            this.itemBarView = itemBarView;
        }

        public VehicleSelectionViewHolder(View view) {
            super(view);
            itemImageView = (ImageView) view.findViewById(R.id.car_selection_row_imageView);
            itemDataTextView = (TextView) view.findViewById(R.id.car_selection_row_textView);
            itemBarView = (View) view.findViewById(R.id.car_selection_row_tabView);
            linearLayout = (LinearLayout) view.findViewById(R.id.layout);
            //view.setOnClickListener(this);
        }

       /* @Override
        public void onClick(View view) {
            //itemDateTextView.setTextColor();
            setVehicleSelection(getPosition(), this);
            vehicleClickListener.onItemClick(getPosition(), view);
        }*/
    }

    private void setVehicleSelection(int location, VehicleSelectionViewHolder viewHolder) {
       /* this.selectedLocation = location;
        prevViewHolder.getItemBarView().setVisibility(View.INVISIBLE);
        prevViewHolder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.secondary_text));

        prevViewHolder=viewHolder;
        viewHolder.getItemBarView().setVisibility(View.VISIBLE);
        viewHolder.getItemDataTextView().setTextColor(context.getResources().getColor(R.color.colorAccent));*/
    }

    public interface VehicleClickListener {
        void onItemClick(int position, View view);
    }
}
