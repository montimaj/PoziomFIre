package com.poziomlabs.firebasestore;

/**
 * Created by monti on 18/3/17.
 */

class Review {
    private String mReviewTitle;
    private String mReviewBody;
    private int mReviewId;

    private static int sNumReviews;

    Review() {}

    Review(String title, String body) {
        mReviewTitle = title;
        mReviewBody = body;
        mReviewId = sNumReviews++;
    }

    @Override
    public String toString() { return mReviewTitle + "\n" + mReviewBody; }

    void setTitle(String title) { mReviewTitle = title; }
    void setBody(String body) { mReviewBody = body; }
    void setReviewId() {}

    String getTitle() { return mReviewTitle; }
    String getBody() { return mReviewBody; }
    int getReviewId() { return mReviewId; }

}
