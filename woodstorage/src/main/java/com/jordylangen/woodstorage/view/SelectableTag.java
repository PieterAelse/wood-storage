package com.jordylangen.woodstorage.view;

public class SelectableTag {

    private String tag;
    private boolean isSelected;

    public SelectableTag(String tag, boolean isSelected) {
        this.tag = tag;
        this.isSelected = isSelected;
    }

    public String getTag() {
        return tag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
