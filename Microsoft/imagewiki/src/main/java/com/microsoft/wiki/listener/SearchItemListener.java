package com.microsoft.wiki.listener;


import com.microsoft.wiki.models.SearchResponse;

public interface SearchItemListener {
    /**
     * This will invoke when search successfully completed
     *
     * @param searchResponse response data
     */
    void onSearchSuccess(SearchResponse searchResponse);

    /**
     * This will invoke when search failed
     */
    void onSearchFail();
}
