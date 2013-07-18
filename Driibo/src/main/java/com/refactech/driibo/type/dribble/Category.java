
package com.refactech.driibo.type.dribble;

public enum Category {
    popular("Popular"), everyone("Everyone"), debuts("Debuts");
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
