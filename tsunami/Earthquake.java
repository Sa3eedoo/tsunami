package com.example.tsunami;

public class Earthquake {
    private String title;
    private long time;
    private int tsunami;
    private String url;

    public Earthquake(String title, long time, int tsunami, String url) {
        this.title = title;
        this.time = time;
        this.tsunami = tsunami;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }

    public int getTsunami() {
        return tsunami;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", tsunami=" + tsunami +
                ", url='" + url + '\'' +
                '}';
    }
}
