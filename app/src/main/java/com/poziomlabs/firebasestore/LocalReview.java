package com.poziomlabs.firebasestore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

class LocalReview extends Review {
    private String mDate;

    LocalReview() {
        mReviewTitle = "Untitled";
        mReviewId = generateReviewId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        LocalReview other = (LocalReview) obj;
        return mReviewId.equals(other.mReviewId);
    }

    private String generateReviewId() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(new BigInteger(36, 0, random).toString(Character.MAX_RADIX));
        while(stringBuilder.length() > 6) {
            stringBuilder.deleteCharAt(random.nextInt(stringBuilder.length()));
        }
        for (int i = 0; i < stringBuilder.length(); i++) {
            char ch = stringBuilder.charAt(i);
            if (Character.isLetter(ch) && Character.isLowerCase(ch) && random.nextFloat() < 0.5) {
                stringBuilder.setCharAt(i, Character.toUpperCase(ch));
            }
        }
        return stringBuilder.toString();
    }

    void updateTime() { mDate = new Date().toString(); }
    String getDate() { return mDate; }
}
