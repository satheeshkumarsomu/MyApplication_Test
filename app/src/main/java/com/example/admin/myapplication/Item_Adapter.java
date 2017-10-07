package com.example.admin.myapplication;

import java.util.ArrayList;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Item_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static Activity context;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    ArrayList<ItemModel> item_model;

    RecyclerView recyclerView;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;



    public Item_Adapter(Activity context, ArrayList<ItemModel> item_model, RecyclerView recyclerView) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.item_model = item_model;
        this.recyclerView = recyclerView;


        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }


                }
            });

        }

    }

    @Override
    public int getItemViewType(int position) {
        return item_model.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return item_model.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        Log.d("ssk", "viewType=" + viewType);
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return new MyViewHolder(itemView);
        } else if (viewType == VIEW_PROG) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return new ProgressViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        // TODO Auto-generated method stub
        if (holder1 instanceof MyViewHolder) {

            final MyViewHolder holder = (MyViewHolder) holder1;
            final ItemModel model = item_model.get(position);
            holder.tv_name.setText(model.getName());
            holder.tv_gender.setText(model.getGender());



        } else {
            ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder1;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }



    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_gender;
        public  ImageView iv_person;

        public MyViewHolder(View convertView) {
            super(convertView);

            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_gender = (TextView) convertView.findViewById(R.id.tv_gender);


            iv_person = (ImageView) convertView.findViewById(R.id.iv_person);


        }
    }




}
