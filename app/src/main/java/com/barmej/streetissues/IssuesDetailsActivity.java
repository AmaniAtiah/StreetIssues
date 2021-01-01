package com.barmej.streetissues;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class IssuesDetailsActivity extends AppCompatActivity {
    public static final String ISSUES_DATA =  "issues_data";
    private ImageView mIssuesImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIssuesImageView = findViewById(R.id.image_view_issues_photo);
        mTitleTextView = findViewById(R.id.text_view_issues_title);
        mDescriptionTextView = findViewById(R.id.text_view_issues_description);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Issues issues = getIntent().getExtras().getParcelable(ISSUES_DATA);
            if (issues != null) {
                getSupportActionBar().setTitle(issues.getTitle());
                mTitleTextView.setText(issues.getTitle());
                mDescriptionTextView.setText(issues.getDescription());
                Glide.with(this).load(issues.getPhoto()).into(mIssuesImageView);
            }

        }
    }

}
