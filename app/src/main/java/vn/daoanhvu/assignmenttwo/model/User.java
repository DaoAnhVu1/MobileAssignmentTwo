package vn.daoanhvu.assignmenttwo.model;

import java.util.List;

public class User {
    private String id;
    private String username;
    private String email;
    private List<String> joinedSiteIdList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getJoinedSiteIdList() {
        return joinedSiteIdList;
    }

    public void setJoinedSiteIdList(List<String> joinedSiteIdList) {
        this.joinedSiteIdList = joinedSiteIdList;
    }
}
