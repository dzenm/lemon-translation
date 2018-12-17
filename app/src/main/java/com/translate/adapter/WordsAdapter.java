package com.translate.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.translate.BR;
import com.translate.R;
import com.translate.bean.WordsBean;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private List<WordsBean> wordsBeans;
    private OnItemClickListener onItemClickListener;

    /**
     * 设置点击事件
     * @param onItemClickListener
     * @return
     */
    public WordsAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * 设置数据
     * @param wordsBeans
     * @return
     */
    public WordsAdapter setWordsBeans(List<WordsBean> wordsBeans) {
        this.wordsBeans = wordsBeans;
        return this;
    }

    @NonNull
    @Override
    public WordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.word_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.getBinding().setVariable(BR.wordsBean, wordsBeans.get(i));
        viewHolder.getBinding().executePendingBindings();
        if (onItemClickListener == null) {
            return;
        }
        viewHolder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(i);
            }
        });
        viewHolder.getBinding().getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemLongClick(i);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordsBeans == null ? 0 : wordsBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onItemLongClick(int position);
    }
}
