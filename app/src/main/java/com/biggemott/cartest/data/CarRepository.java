package com.biggemott.cartest.data;

import com.biggemott.cartest.CarContract;
import com.biggemott.cartest.utils.L;

import android.graphics.Point;

public class CarRepository implements CarContract.Repository {

    private static final String TAG = "CarRepository";

    private Car mCar;

    public CarRepository() {
        mCar = new Car(new Point(200, 400), 180);
    }

    @Override
    public Car getCar() {
        return mCar;
    }

    @Override
    public void setCarPosition(int x, int y, float direction) {
        mCar.setLocation(x, y);
        mCar.setDirection(direction);
    }
}
