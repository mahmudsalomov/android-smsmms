package com.klinker.android.messaging_sample;

public class Data {
    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", hex='" + hex + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    private int id;
    private String hex;
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
