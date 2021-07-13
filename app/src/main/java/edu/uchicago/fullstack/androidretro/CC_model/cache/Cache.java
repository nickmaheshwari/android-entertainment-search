package edu.uchicago.fullstack.androidretro.CC_model.cache;

//singleton cache
public class Cache {

    private String keyword;

    private static Cache cache;

    private Cache() {
    }

    public static Cache getInstance() {
        if (null == cache) {
            cache = new Cache();
            return cache;
        } else {
            return cache;
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;

    }


}