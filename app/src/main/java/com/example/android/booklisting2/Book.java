package com.example.android.booklisting2;

/**
 * Created by Cedo on 26.6.2017..
 */


public class Book {
    /**
     * Title of the book
     */
    private String mTitle;
    /**
     * Author of the book
     */
    private String mAuthor;
    /**
     * Website url of the book
     */
    private String mBookLink;


    /**
     * Constructs a new {@link Book} object.
     *
     * @param title    is the title of the book
     * @param author   is the author of the book
     * @param bookLink is the website URL to find more details about the book
     */


    public Book(String author, String title, String bookLink) {
        mTitle = title;
        mAuthor = author;
        mBookLink = bookLink;
    }

    //Returns the title of the book
    public String getTitle() {
        return mTitle;
    }

    //Returns the author of the book
    public String getAuthor() {
        return mAuthor;
    }

    //Returns the website URL to find more details on the book
    public String getBookLink() {
        return mBookLink;
    }
}