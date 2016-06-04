package com.microsoft.wiki.services;

import com.microsoft.wiki.http.HttpConnection;
import com.microsoft.wiki.listener.SearchItemListener;
import com.microsoft.wiki.models.Error;
import com.microsoft.wiki.models.PageDetails;
import com.microsoft.wiki.models.Pages;
import com.microsoft.wiki.models.Query;
import com.microsoft.wiki.models.SearchResponse;
import com.microsoft.wiki.models.Thumbnail;
import com.microsoft.wiki.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchDataHandler {

    /**
     * Callback for search result
     */
    private SearchItemListener mSearchItemListener;

    /**
     * Execute the given search request
     *
     * @param requestData requested search data
     */
    public void executeRequest(String requestData) {
        HttpConnection httpConnection = new HttpConnection();
        byte[] responseData = httpConnection.execute(requestData);

        if (mSearchItemListener != null) {
            try {
                SearchResponse searchResponse = parseData(new String(responseData));
                // Check whether response is valid or not
                if (searchResponse.error != null) {
                    mSearchItemListener.onSearchFail();
                } else {
                    mSearchItemListener.onSearchSuccess(searchResponse);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mSearchItemListener.onSearchFail();
            }
        }
    }

    /**
     * Set the search listener
     *
     * @param listener Instance of SearchItemListener
     */
    public void setSearchItemListener(SearchItemListener listener) {
        this.mSearchItemListener = listener;
    }

    /**
     * Parse the search response
     *
     * @param responseData response data string
     * @return Instance of SearchResponse
     * @throws JSONException
     */
    private SearchResponse parseData(String responseData) throws JSONException {

        SearchResponse searchResponse = new SearchResponse();

        JSONObject responseJsonObject = new JSONObject(responseData);
        if (responseJsonObject.has(Constants.ERROR)) {
            JSONObject errorObject = responseJsonObject.getJSONObject(Constants.ERROR);
            Error error = new Error();
            error.code = (String) errorObject.get(Constants.ERROR_CODE);
            searchResponse.error = error;
        }
        if (responseJsonObject.has(Constants.QUERY)) {
            // Get the query object from response JSON
            JSONObject queryObject = responseJsonObject.getJSONObject(Constants.QUERY);
            // Get the pages object from response JSON
            JSONObject pagesObject = queryObject.getJSONObject(Constants.PAGES);

            // Prepare the search response object
            searchResponse.query = new Query();
            searchResponse.query.pages = new Pages();
            searchResponse.query.pages.pageDetails = new ArrayList<>();

            Iterator<?> keys = pagesObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (pagesObject.get(key) instanceof JSONObject) {

                    String pageResponseJson = pagesObject.optString(key);

                    // Parse the objects of pages JSON
                    JSONObject pageResponse = new JSONObject(pageResponseJson);
                    Integer pageId = (Integer) pageResponse.get(Constants.PAGE_ID);
                    String title = (String) pageResponse.get(Constants.TITLE);
                    PageDetails pageDetails = new PageDetails();
                    pageDetails.pageId = pageId;
                    pageDetails.title = title;

                    // Parse the thumbnail object from pages JSON
                    Thumbnail thumbnail = new Thumbnail();
                    if (pageResponse.has(Constants.THUMBNAIL)) {
                        JSONObject thumbnailObj = pageResponse.getJSONObject(Constants.THUMBNAIL);
                        thumbnail.source = getStringObjectValue(Constants.SOURCE, thumbnailObj);
                        thumbnail.width = getIntegerObjectValue(Constants.WIDTH, thumbnailObj);
                        thumbnail.height = getIntegerObjectValue(Constants.HEIGHT, thumbnailObj);
                        pageDetails.thumbnail = thumbnail;
                    }
                    searchResponse.query.pages.pageDetails.add(pageDetails);
                }
            }
        }
        return searchResponse;
    }

    /**
     * Get response value of given object
     *
     * @param objectName name of the object which needs to parse
     * @param object     json object
     * @return response value of input object
     * @throws JSONException
     */
    private String getStringObjectValue(String objectName, JSONObject object) throws JSONException {
        boolean isValid = object.has(objectName);
        return isValid ? object.get(objectName).toString() : null;
    }

    /**
     * Get response value of given object
     *
     * @param objectName name of the object which needs to parse
     * @param object     json object
     * @return response value of input object
     * @throws JSONException
     */
    private int getIntegerObjectValue(String objectName, JSONObject object) throws JSONException {
        boolean isValid = object.has(objectName);
        return isValid ? (Integer) object.get(objectName) : 0;
    }
}
