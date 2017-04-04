package com.poziomlabs.firebasestore;

public class User {
    private String mUserId;
    private String mUserEmail;
    private String mUserName;

    User() {}

    @Override
    public String toString() {
        return "UserId: " + mUserId + "\nEmail: " + mUserEmail + "\nName: " + mUserName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        User other = (User) obj;
        return mUserId.equals(other.mUserId);
    }

    public void setUserId(String id) { mUserId = id; }
    public void setUserEmail(String email) { mUserEmail = email; }
    public void setUserName(String name) { mUserName = name; }

    public String getUserId() { return mUserId; }
    public String getUserEmail() { return mUserEmail; }
    public String getUserName() { return mUserName; }
}
