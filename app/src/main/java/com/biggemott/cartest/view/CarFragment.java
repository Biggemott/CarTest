package com.biggemott.cartest.view;

import com.biggemott.cartest.CarContract;
import com.biggemott.cartest.R;
import com.biggemott.cartest.data.CarRepository;
import com.biggemott.cartest.presenter.CarPresenter;
import com.biggemott.cartest.utils.L;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CarFragment extends Fragment implements CarContract.View {

    private static final String TAG = "CarFragment";

    private CarContract.Presenter mPresenter;
    private View mCarView;
    private int mCarWidth;
    private int mCarHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // in our case there is no reason to recreate the fragment
        setRetainInstance(true);
        // we could use DI instead of instantiating components here
        mPresenter = new CarPresenter(new CarRepository());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        L.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_car, container, false);
        mCarView = v.findViewById(R.id.car_view);
        mCarWidth = getResources().getDimensionPixelSize(R.dimen.car_width);
        mCarHeight = getResources().getDimensionPixelSize(R.dimen.car_height);
        setupGestureDetector(v.findViewById(R.id.touch_view));

        mPresenter.attach(this);
        return v;
    }

    private void setupGestureDetector(View view) {
        final GestureDetector detector = new GestureDetector(view.getContext(),
                new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mPresenter.onDoubleTap(e.getX(), e.getY());
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                mPresenter.onTap(e.getX(), e.getY());
                return true;
            }
        });
        detector.setIsLongpressEnabled(false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        L.d(TAG, "onDestroyView");
        mPresenter.detach();
        mCarView = null;
        super.onDestroyView();
    }

    @Override
    public void updateCarPosition(int x, int y, float direction) {
        mCarView.setX(x - mCarWidth / 2f);
        mCarView.setY(y - mCarHeight / 2f);
        mCarView.setRotation(direction);
    }
}
