package com.microsoft.wiki.api;

import com.microsoft.wiki.listener.SearchItemListener;
import com.microsoft.wiki.models.SearchRequest;
import com.microsoft.wiki.services.AsyncTaskExecutor;

public class WikiApi {
    /**
     * Call this API to search data in wikipedia
     *
     * @param searchItemListener callback for search result
     * @param searchRequest      request to search
     */
    public static void searchItem(SearchItemListener searchItemListener, SearchRequest
            searchRequest) {
        String searchQuery = prepareSearchQuery(searchRequest);
        new AsyncTaskExecutor(searchQuery, searchItemListener).execute();
    }

    /**
     * Prepare query format for wiki search
     *
     * @param searchRequest input search request
     * @return search query
     */
    private static String prepareSearchQuery(SearchRequest searchRequest) {
        String url = "https://en.wikipedia.org/w/api.php?";
        return url +
                "action=" + searchRequest.action + "&" +
                "prop=" + searchRequest.prop + "&" +
                "format=" + searchRequest.format + "&" +
                "piprop=" + searchRequest.piprop + "&" +
                "pithumbsize=" + searchRequest.piThumbSize + "&" +
                "pilimit=" + searchRequest.piLimit + "&" +
                "generator=" + searchRequest.generator + "&" +
                "gpslimit=" + searchRequest.gpsLimit + "&" +
                "gpssearch=" + searchRequest.gpsSearch;
    }
}
