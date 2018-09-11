package com.translate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.translate.R;
import com.translate.bean.WordsBean;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    private List<WordsBean> wordsBeans;
    private OnItemClickListener onItemClickListener;

    public WordsAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public WordsAdapter setWordsBeans(List<WordsBean> wordsBeans) {
        this.wordsBeans = wordsBeans;
        return this;
    }

    @NonNull
    @Override
    public WordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.word_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.ViewHolder viewHolder, final int i) {
        WordsBean wordsBean = wordsBeans.get(i);
        viewHolder.bindData(wordsBean);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordsBeans == null ? 0 : wordsBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView chinese, english;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            chinese = itemView.findViewById(R.id.chinese);
            english = itemView.findViewById(R.id.english);
        }

        public void bindData(WordsBean wordsBean) {
            chinese.setText(wordsBean.getChinese());
            english.setText(wordsBean.getEnglish());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
