package com.jasmini.test.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jasmini.test.R;
import com.jasmini.test.model.ListDetails;
import com.jasmini.test.utils.RecyclerViewClick;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<ListDetails> listDetailses = null;
    private RecyclerViewClick recyclerViewClick = null;


    private String selectedMember = null;

    public DetailsRecyclerAdapter(Context context, List<ListDetails> listDetailses, RecyclerViewClick recyclerViewClick) {
        this.listDetailses = listDetailses;
        this.mContext = context;
        this.recyclerViewClick = recyclerViewClick;
    }

    @Override
    public DetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        return new DetailsRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailsRecyclerAdapter.MyViewHolder holder, final int position) {

        final ListDetails listDetails = listDetailses.get(position);
        final ListDetails list = listDetailses.get(0);
        if (listDetails != null) {
            holder.userName.setText("User Name:" + listDetails.getUser_name());
            holder.userDescription.setText(listDetails.getDescription());
            Glide.with(mContext)
                    .load(listDetails.getUser_image()).placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp)
                    .fitCenter()
                    .dontAnimate().dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.profileImage);
            recyclerViewClick.onClick(list.getDescription());
        }
        holder.userDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = mContext.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    holder.userDescription.setMaxLines(Integer.MAX_VALUE);
                } else {
                    recyclerViewClick.onClick(holder.userDescription.getText().toString());
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return listDetailses == null ? 0 : listDetailses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileImage;
        public TextView userName, userDescription;

        public MyViewHolder(View view) {
            super(view);

            profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
            userName = (TextView) view.findViewById(R.id.userName);
            userDescription = (TextView) view.findViewById(R.id.description);

        }
    }

}
