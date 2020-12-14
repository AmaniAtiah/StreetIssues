package com.barmej.streetissues;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssuesListFragment extends Fragment implements IssuesListAdapter.OnIssuesClickListener {
    private RecyclerView mRecyclerViewIssues;
    private IssuesListAdapter mIssuesListAdapter;
    private List<Issues> mIssuesList;
    private FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issues_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mRecyclerViewIssues = view.findViewById(R.id.recyclerView_issue);
        mRecyclerViewIssues.setLayoutManager(new LinearLayoutManager(getContext()));

        mIssuesList = new ArrayList<>();
        mIssuesListAdapter = new IssuesListAdapter(mIssuesList, IssuesListFragment.this);
        mRecyclerViewIssues.setAdapter(mIssuesListAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("issues").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,@Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    mIssuesList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        mIssuesList.add(documentSnapshot.toObject(Issues.class));
                    }
                    mIssuesListAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public void onIssuesClick(Issues issues) {
        Intent intent = new Intent(getContext(), IssuesDetailsActivity.class);
        intent.putExtra(IssuesDetailsActivity.ISSUES_DATA, issues);
        startActivity(intent);
    }
}
