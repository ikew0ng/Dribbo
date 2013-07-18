
package com.refactech.driibo.type.dribble;

import com.google.gson.Gson;

/**
 * Created by Issac on 7/18/13.
 */
public class BaseType {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
