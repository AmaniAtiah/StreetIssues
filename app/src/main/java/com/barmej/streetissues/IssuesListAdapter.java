package com.barmej.streetissues;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class IssuesListAdapter extends RecyclerView.Adapter<IssuesListAdapter.IssueViewHolder> {

    public interface OnIssuesClickListener {
        void onIssuesClick(Issues issues);
    }

    private List<Issues> mIssuesList;
    private OnIssuesClickListener mOnIssuesClickListener;

    public IssuesListAdapter(List<Issues> mIssuesList, OnIssuesClickListener mOnIssuesClickListener) {
        this.mIssuesList = mIssuesList;
        this.mOnIssuesClickListener = mOnIssuesClickListener;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder,int position) {
        holder.bind(mIssuesList.get(position));

    }

    @Override
    public int getItemCount() {
        return mIssuesList.size();
    }

    public class IssueViewHolder extends RecyclerView.ViewHolder{
        TextView issueTitleTextView;
        ImageView issuePhotoImageView;
        Issues issues;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTitleTextView = itemView.findViewById(R.id.text_view_issues_title);
            issuePhotoImageView = itemView.findViewById(R.id.image_view_issues_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnIssuesClickListener.onIssuesClick(issues);
                }
            });

        }
        public void bind(Issues issues) {
            this.issues = issues;
            issueTitleTextView.setText(issues.getTitle());
            Glide.with(issuePhotoImageView).load(issues.getPhoto()).into(issuePhotoImageView);

        }
    }
}
