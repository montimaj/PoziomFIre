package com.poziomlabs.firebasestore;

import com.google.firebase.database.DatabaseReference;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

class LocalReview extends Review {
    private ArrayList<String> mSelectedWifis;
    private DatabaseReference mMyRef;
    private Date mDate;

    LocalReview() { mReviewId = generateReviewId(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        LocalReview other = (LocalReview) obj;
        return mReviewId.equals(other.mReviewId);
    }

    @Override
    public String toString() {
        System.out.println(mReviewTitle);
        return mReviewTitle.isEmpty() ? "Blank Review " + mDate.toString() : mReviewTitle + " " + mDate.toString();
    }

    private String generateReviewId() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(new BigInteger(48, random).toString(36));
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

    void setSelectedWifis(ArrayList<String> wifis) { mSelectedWifis = wifis; }
    void setDatabaseReference(DatabaseReference reference) { mMyRef = reference; }
    void updateTime() { mDate = new Date(); }

    ArrayList<String> getSelectedWifis() { return  mSelectedWifis; }
    DatabaseReference getDatabaseReference() { return mMyRef; }
}
