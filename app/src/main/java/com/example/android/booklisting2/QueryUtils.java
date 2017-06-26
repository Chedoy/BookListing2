package com.example.android.booklisting2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cedo on 26.6.2017..
 *
 * Helper methods related to requesting and receiving book data.
 */


public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the  dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchData(String query) {
        // Create URL object
        URL url = createUrl(query);

        // Perform HTTP request to the URL and receive a JSON response back
        String response = null;
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with the HTTP request", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s

        List<Book> books = extractFromJson(response);

        // Return the list of {@link Book}s

        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String query) {
        URL url = null;
        try {
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=10");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL creation failed", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection connection = null;
        InputStream stream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (connection.getResponseCode() == 200) {

                stream = connection.getInputStream();
                jsonResponse = readFromStream(stream);
            } else {
                Log.e(LOG_TAG, "Error Response Code: " + connection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (stream != null) {
                stream.close();
            }
        }
        return jsonResponse;

    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */

    private static String readFromStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (stream != null) {

            InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }

        }
        return builder.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */

    private static List<Book> extractFromJson(String responseJson) {
        if (TextUtils.isEmpty(responseJson)) {

            // If the JSON string is empty or null, then return early.
            return null;
        }

        // Create an empty ArrayList that we can start adding book to
        List<Book> books = new ArrayList<Book>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject volumes = new JSONObject(responseJson);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray items = volumes.getJSONArray("items");

            // For each book in the booksArray, create an {@link Book} object
            for (int i = 0; i < items.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject item = items.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all the book information
                // for that book.
                JSONObject volume = item.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = volume.getString("title");
                String author;

                // Extract the value for the key called "authors"
                // If it doesn't have a value for the key called "authors" display "Unknown author"
                if (volume.has("authors")) {
                    author = volume.getJSONArray("authors").get(0).toString();
                } else {
                    author = "Unknown author";
                }

                // Extract the value for the key called "infoLink"
                String link = volume.getString("infoLink");

                // Create a new {@link Book} object with the title, author
                // and url from the JSON response.
                Book book = new Book(author, title, link);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }


        } catch (JSONException e) {

            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        // Return the list of books.
        return books;

    }

}



