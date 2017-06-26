package com.example.android.booklisting2;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Cedo on 26.6.2017..
 * <p>
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */


public class BookLoader extends AsyncTaskLoader<List<Book>> {


    private String mQuery;


    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param query   to load data from
     */


    public BookLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {

        if (mQuery == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of books.
        List<Book> books = QueryUtils.fetchData(mQuery);
        return books;
    }
}

