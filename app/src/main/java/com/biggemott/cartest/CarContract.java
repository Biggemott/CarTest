package com.biggemott.cartest;

import com.biggemott.cartest.data.Car;

import android.support.annotation.NonNull;

public interface CarContract {

    interface Repository {

        Car getCar();

        void setCarPosition(int x, int y, float direction);
    }

    interface View {

        void updateCarPosition(int x, int y, float direction);
    }

    interface Presenter {

        void attach(@NonNull View view);

        void detach();

        void onTap(float x, float y);

        void onDoubleTap(float x, float y);
    }
}
