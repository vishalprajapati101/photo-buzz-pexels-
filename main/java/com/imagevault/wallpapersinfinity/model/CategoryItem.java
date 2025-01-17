package com.imagevault.wallpapersinfinity.model;

public class CategoryItem {

    private String categoryName;
    private String categoryImageUrl; // URL for the image, or you could use an integer for drawable resources

    public CategoryItem(String categoryName, String categoryImageUrl) {
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }
}
