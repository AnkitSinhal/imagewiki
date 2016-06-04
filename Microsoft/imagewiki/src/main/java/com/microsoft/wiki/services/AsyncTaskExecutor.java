package com.microsoft.wiki.services;

import android.os.AsyncTask;

import com.microsoft.wiki.listener.SearchItemListener;

public class AsyncTaskExecutor extends AsyncTask<Void, String, String> {
    /**
     * Callback for search result
     */
    private SearchItemListener mSearchItemListener;
    /**
     * Search request
     */
    private String mSearchRequest;

    public AsyncTaskExecutor(String searchRequest, SearchItemListener searchItemListener) {
        mSearchRequest = searchRequest;
        mSearchItemListener = searchItemListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        SearchDataHandler searchDataHandler = new SearchDataHandler();
        searchDataHandler.setSearchItemListener(mSearchItemListener);
        searchDataHandler.executeRequest(mSearchRequest);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
