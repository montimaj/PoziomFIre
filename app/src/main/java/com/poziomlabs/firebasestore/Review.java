package com.poziomlabs.firebasestore;

import java.math.BigInteger;
import java.security.SecureRandom;

class Review {
    private String mReviewTitle;
    private String mReviewBody;
    private String mBssid;
    private String mReviewId;

    Review() {}

    Review(String title, String body) {
        mReviewTitle = title;
        mReviewBody = body;
        mReviewId = generateReviewId();
    }

    private String generateReviewId() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(new BigInteger(30, random).toString(32));
        for (int i = 0; i < stringBuilder.length(); i++) {
            char ch = stringBuilder.charAt(i);
            if (Character.isLetter(ch) && Character.isLowerCase(ch) && random.nextFloat() < 0.5) {
                stringBuilder.setCharAt(i, Character.toUpperCase(ch));
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return mReviewTitle + "\n" + mReviewBody;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Review other = (Review) obj;
        return mReviewId.equals(other.mReviewId);
    }


    void setTitle(String title) { mReviewTitle = title; }
    void setBody(String body) { mReviewBody = body; }
    void setBssid(String bssid) { mBssid = bssid; }
    void setReviewId(String id) { mReviewId = id; }

    String getTitle() { return mReviewTitle; }
    String getBody() { return mReviewBody; }
    String getBssid() { return mBssid; }
    String getReviewId() { return mReviewId; }
}