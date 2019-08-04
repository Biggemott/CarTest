package com.biggemott.cartest.data;

import android.graphics.Point;
import android.support.annotation.NonNull;

public class Car {

    private Point mLocation;
    private float mDirection;

    Car(@NonNull Point location, int direction) {
        mLocation = location;
        mDirection = direction;
    }

    public Point getLocation() {
        return mLocation;
    }

    void setLocation(@NonNull Point location) {
        mLocation = location;
    }

    void setLocation(int x, int y) {
        mLocation.set(x, y);
    }

    public float getDirection() {
        return mDirection;
    }

    void setDirection(float direction) {
        mDirection = direction;
    }
}
