package com.microsoft.wiki.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.wiki.R;
import com.microsoft.wiki.adapter.SearchItemsListAdapter;
import com.microsoft.wiki.api.WikiApi;
import com.microsoft.wiki.listener.SearchItemListener;
import com.microsoft.wiki.models.PageDetails;
import com.microsoft.wiki.models.SearchRequest;
import com.microsoft.wiki.models.SearchResponse;
import com.microsoft.wiki.utils.Constants;
import com.microsoft.wiki.utils.PermissionHelper;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchItemListener, TextWatcher {
    /**
     * Edit Text field for input search item
     */
    private EditText mEditTextSearch;
    /**
     * Searched list items to display
     */
    private ArrayList<PageDetails> mPageDetails;
    /**
     * List view instance to display searched items
     */
    private ListView mSearchListView;
    /**
     * Permission request code
     */
    private static final int PERMISSION_REQUEST_CODE = 4323243;
    /**
     * Custom search list adapter
     */
    private SearchItemsListAdapter mSearchItemsListAdapter;
    /**
     * No search result found text view
     */
    private TextView mTextNoResult;
    /**
     * Progress bar
     */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String[] permissionRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionHelper.INSTANCE.requestPermissions(permissionRequired, this,
                PERMISSION_REQUEST_CODE);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextNoResult = (TextView) findViewById(R.id.textNoResult);
        mSearchListView = (ListView) findViewById(R.id.listViewSearch);
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                PageDetails pageDetails = (PageDetails) mSearchListView.getItemAtPosition(position);
                Intent detailsIntent = new Intent(SearchActivity.this, DetailActivity.class);
                detailsIntent.putExtra(Constants.TITLE, pageDetails.title);
                startActivity(detailsIntent);
            }
        });

        mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);
        mEditTextSearch.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        final Handler mHandler = new Handler();
        Runnable userStoppedTyping = new Runnable() {

            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                String searchItem = mEditTextSearch.getText().toString();
                SearchRequest searchRequest = prepareSearchRequest(searchItem);
                WikiApi.searchItem(SearchActivity.this, searchRequest);
            }
        };
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(userStoppedTyping, 500);
    }


    @Override
    public void onSearchSuccess(SearchResponse searchResponse) {
        mPageDetails = searchResponse.query.pages.pageDetails;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextNoResult.setVisibility(View.GONE);
                mSearchListView.setVisibility(View.VISIBLE);
                mSearchItemsListAdapter = new SearchItemsListAdapter(SearchActivity.this,
                        mPageDetails);
                mSearchListView.setAdapter(mSearchItemsListAdapter);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSearchFail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextNoResult.setVisibility(View.VISIBLE);
                mSearchListView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Prepare search request
     *
     * @param search data to search
     * @return instance of SearchRequest
     */
    private static SearchRequest prepareSearchRequest(String search) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.action = "query";
        searchRequest.prop = "pageimages";
        searchRequest.format = "json";
        searchRequest.piprop = "thumbnail";
        searchRequest.piThumbSize = 50;
        searchRequest.piLimit = 50;
        searchRequest.generator = "prefixsearch";
        searchRequest.gpsSearch = search;
        searchRequest.gpsLimit = 50;
        return searchRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    switch (permissions[i]) {
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.main_permissions_no_read_storage),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchItemsListAdapter != null) {
            mSearchItemsListAdapter.clearCache();
        }
    }

}
