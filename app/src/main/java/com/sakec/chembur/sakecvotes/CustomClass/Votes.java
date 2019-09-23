package com.sakec.chembur.sakecvotes.CustomClass;

import android.support.annotation.Nullable;

public class Votes {

    private String user;
    private String pollID;

    public Votes(){}

    public Votes(String user, String pollID) {
        this.user = user;
        this.pollID = pollID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPollID() {
        return pollID;
    }

    public void setPollID(String pollID) {
        this.pollID = pollID;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj instanceof Votes)
        {
            if (this.pollID.equals(((Votes) obj).pollID))
                if (this.user.equals(((Votes) obj).user))
                    return true;
        }

        return false;
    }
}
