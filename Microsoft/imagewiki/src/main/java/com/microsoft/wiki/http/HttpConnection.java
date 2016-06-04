package com.microsoft.wiki.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection {
    /**
     * Instance of HttpURLConnection
     */
    private HttpURLConnection mHttpURLConnection;
    /**
     * Bytes of http response data
     */
    private byte[] mInputBytes;

    /**
     * Execute the http request
     *
     * @param request http request data
     * @return response data
     */
    public byte[] execute(String request) {
        try {
            URL serverUrl = new URL(request);
            mHttpURLConnection = (HttpURLConnection) serverUrl.openConnection();
            mHttpURLConnection.connect();
            int responseCode = mHttpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException();
            }
            mInputBytes = readAll(mHttpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mHttpURLConnection != null) {
                mHttpURLConnection.disconnect();
            }
        }
        return mInputBytes;
    }

    /**
     * Get the response bytes from stream
     *
     * @param inputStream input stream
     * @return bytes of given input data
     * @throws IOException
     */
    private byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        byte[] sBuffer = new byte[2048];
        // Read response into a buffered stream
        int readBytes;
        try {
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
        } catch (Exception e) {
            throw new IOException("");
        }
        return content.toByteArray();
    }
}
