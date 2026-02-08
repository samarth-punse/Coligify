package com.example.coligify.Model;

import java.util.Objects;

public class CourseModel {

    private int image;
    private String title;
    private String rating;
    private String level;
    private boolean liked;
    private boolean bookmarked;

    public CourseModel(int image, String title, String rating, String level) {
        this.image = image;
        this.title = title;
        this.rating = rating;
        this.level = level;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getLevel() {
        return level;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    // Needed for contains(), remove()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseModel)) return false;
        CourseModel that = (CourseModel) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
