package com.example.android.booklisting2;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     *  SearchView widget
     */
    private SearchView search;

    /**
     * Query for the book data
     */
    private String mQuery;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.listView);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        bookListView.setEmptyView(mEmptyStateTextView);

        final List<Book> booksList = new ArrayList<Book>();

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, booksList);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        //Get the SearchView on the (@link search)
        //Enable the submit button
        //Set the hint on the search button
        search = (SearchView) findViewById(R.id.search);
        search.onActionViewExpanded();
        search.setIconified(false);
        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Search for a book title or author");


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        //Create a boolean for the connectivity status
        final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // If there is a network connection, initialize loader
        if (isConnected) {

            getLoaderManager().initLoader(0, null, this);

            //If there is no network connection
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.error_no_internet);
        }

        //Set the SearchView OnQueryTextListener on the search button
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // If there is a network connection
                if (isConnected) {
                    //Restart the Loader when the search is made,search
                    getLoaderManager().restartLoader(0, null, BookActivity.this);

                    return true;
                    //If there is no network connection
                } else {
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.error_no_internet);
                    return false;

                }
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getBookLink());

                // Create a new intent to view the book URI
                Intent webIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        //Get the search query given by the user
        mQuery = search.getQuery().toString();

        //Create a BookLoader with the query and return it
        BookLoader loader = new BookLoader(this, mQuery);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
        mEmptyStateTextView.setText(R.string.noDataFound);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //Get the query given by the user
        mQuery = search.getQuery().toString();

        outState.putString("query", mQuery);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mQuery = savedInstanceState.getString("query");

        //Initialize the Loader (execute the search)
        super.onRestoreInstanceState(savedInstanceState);
    }


}