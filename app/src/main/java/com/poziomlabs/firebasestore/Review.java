package com.poziomlabs.firebasestore;

class Review {
    private boolean mModeratorFlag;

    protected String mReviewTitle;
    protected String mReviewBody;
    protected String mReviewId;
    protected float mRating;

    Review() {}

    @Override
    public String toString() { return mReviewTitle + "\n\n" + mReviewBody; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        Review other = (Review) obj;
        return mReviewId.equals(other.mReviewId);
    }

    public void setTitle(String title) { mReviewTitle = title; }
    public void setBody(String body) { mReviewBody = body; }
    public void setReviewId(String id) { mReviewId = id; }
    public void setModeratorFlag(boolean value) { mModeratorFlag = value; }
    public void setRating(float rating) { mRating = rating; }

    public String getTitle() { return mReviewTitle; }
    public String getBody() { return mReviewBody; }
    public String getReviewId() { return mReviewId; }
    public float getRating() { return mRating; }
    public boolean getModeratorFlag() { return mModeratorFlag; }
}