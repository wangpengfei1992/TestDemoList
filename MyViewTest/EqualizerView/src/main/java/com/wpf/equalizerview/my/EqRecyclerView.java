package com.wpf.equalizerview.my;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wpf.equalizerview.R;
import com.wpf.equalizerview.util.ResUtils;
import com.wpf.equalizerview.view.EqSquareBars;
import com.wpf.equalizerview.view.EqSquareProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: feipeng.wang
 * Time:   2021/6/23
 * Description : This is description.
 */
public class EqRecyclerView extends RecyclerView {
    public EqRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EqRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EqRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        init(context);
    }

    private void init(Context context) {
        setItemAnimator(null);
        setHasFixedSize(true);
        adapter = new ProgressAdapter();
        dataBeans = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.eq_titles_array);
        for (int i = 0; i < titles.length; i++) {
            DataBean dataBean = new DataBean();
            dataBean.title = titles[i];
            this.dataBeans.add(dataBean);
        }
        setAdapter(adapter);
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private ProgressAdapter adapter;
    private List<DataBean> dataBeans;
    // 调节的是哪个区域，即第几条Bar
    private int region;
    private OnRegionChangeListener regionChangeListener;
    public void setOnRegionChangeListener(OnRegionChangeListener regionChangeListener) {
        this.regionChangeListener = regionChangeListener;
    }

    private class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.VHolder> implements View.OnClickListener {
        @NonNull
        @Override
        public ProgressAdapter.VHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.my_eq_item, null);
            ProgressAdapter.VHolder holder = new ProgressAdapter.VHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProgressAdapter.VHolder vHolder, int position) {
            DataBean dataBean = dataBeans.get(position);
            vHolder.setTitle(dataBean.title);
            vHolder.setProgress(dataBean.value);
            vHolder.setValue(dataBean.value);
            vHolder.itemView.setTag(position);
            vHolder.itemView.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return dataBeans.size();
        }

        @Override
        public void onClick(View view) {
            if (view.getTag() instanceof Integer) {
                region = (int) view.getTag();
                notifyDataSetChanged();
                if (regionChangeListener != null) {
                    regionChangeListener.onRegionChanged(region);
                }
            }
        }

        private class VHolder extends ViewHolder {
            private TextView tvValue;
            private VerticalSeekBar2 eqProgress;
            private TextView tvTitle;

            private VHolder(@NonNull View itemView) {
                super(itemView);
                tvValue = (TextView) itemView.findViewById(R.id.tv_value);
                eqProgress = (VerticalSeekBar2) itemView.findViewById(R.id.pb_progress);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }

            private void setTitle(String title) {
                tvTitle.setText(title);
            }

            private void setValue(int value) {
                tvValue.setText(String.valueOf(value));
            }

            private void setProgress(int progress) {
                eqProgress.setProgress(progress);
            }
            
        }
    }

    private class DataBean {
        int value = 50;
        String title;
    }

    public interface OnRegionChangeListener {
        void onRegionChanged(int region);
    }
}
