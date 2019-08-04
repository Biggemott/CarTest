package com.biggemott.cartest.presenter;

import com.biggemott.cartest.CarContract;
import com.biggemott.cartest.data.Car;
import com.biggemott.cartest.utils.L;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class CarPresenter implements CarContract.Presenter {

    private static final String TAG = "CarPresenter";

    /**
     * Rotating speed in degrees/sec
     */
    private static final float CAR_ROTATING_SPEED = 360f;

    /**
     * Moving speed in pixels/sec
     */
    private static final float CAR_MOVING_SPEED = 1000f;

    private CarContract.Repository mRepository;
    private CarContract.View mView;
    private Animator mCurrentAnim;
    private OnPositionListener mPositionListener = new OnPositionListener() {
        @Override
        public void onPositionChanged(int x, int y) {
            setCarLocation(x, y);
        }

        @Override
        public void onPositionChanged(float direction) {
            setCarDirection(direction);
        }

        @Override
        public void onPositionChanged(int x, int y, float direction) {
            setCarPosition(x, y, direction);
        }
    };
    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {

        boolean isCanceled = false;

        @Override
        public void onAnimationCancel(Animator animation) {
            L.d(TAG, "onAnimationCancel");
            isCanceled = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            L.d(TAG, "onAnimationEnd");
            if (!isCanceled) mCurrentAnim = null;
        }
    };

    public CarPresenter(CarContract.Repository repository) {
        mRepository = repository;
    }

    @Override
    public void attach(@NonNull CarContract.View view) {
        L.d(TAG, "attach");
        mView = view;
        Car car = mRepository.getCar();
        Point location = car.getLocation();
        mView.updateCarPosition(location.x, location.y, car.getDirection());
    }

    @Override
    public void detach() {
        L.d(TAG, "detach");
        mView = null;
    }

    @Override
    public void onTap(float x, float y) {
        L.d(TAG, "onTap - " + x + ", " + y);
        if (mCurrentAnim != null) mCurrentAnim.cancel();

        Car car = mRepository.getCar();
        int endX = Math.round(x);
        int endY = Math.round(y);

        mCurrentAnim = new SimpleMovementMethod(car.getLocation().x, car.getLocation().y,
                endX, endY, car.getDirection(), mPositionListener).getAnimator();
        mCurrentAnim.addListener(mAnimatorListener);
        mCurrentAnim.start();
    }

    @Override
    public void onDoubleTap(float x, float y) {
        L.d(TAG, "onDoubleTap - " + x + ", " + y);
        if (mCurrentAnim != null) mCurrentAnim.cancel();

        Car car = mRepository.getCar();
        int endX = Math.round(x);
        int endY = Math.round(y);

        mCurrentAnim = new RealisticMovementMethod(car.getLocation().x, car.getLocation().y,
                endX, endY, car.getDirection(), mPositionListener).getAnimator();
        mCurrentAnim.addListener(mAnimatorListener);
        mCurrentAnim.start();
    }

    private void setCarLocation(int x, int y) {
        Car car = mRepository.getCar();
        setCarPosition(x, y, car.getDirection());
    }

    private void setCarDirection(float direction) {
        Car car = mRepository.getCar();
        int x = car.getLocation().x;
        int y = car.getLocation().y;
        setCarPosition(x, y, direction);
    }

    private void setCarPosition(int x, int y, float direction) {
        mRepository.setCarPosition(x, y, direction);
        if (mView != null) {
            mView.updateCarPosition(x, y, direction);
        }
    }



    private interface OnPositionListener {
        void onPositionChanged(int x, int y);
        void onPositionChanged(float direction);
        void onPositionChanged(int x, int y, float direction);
    }

    private static class SimpleMovementMethod {

        private final OnPositionListener mListener;
        private final int mStartX;
        private final int mStartY;
        private final int mEndX;
        private final int mEndY;
        private final float mStartAngle;

        private SimpleMovementMethod(int startX, int startY, int endX, int endY, float startAngle,
                                     @NonNull OnPositionListener listener) {
            mStartX = startX;
            mStartY = startY;
            mEndX = endX;
            mEndY = endY;
            mStartAngle = startAngle;
            mListener = listener;
        }

        Animator getAnimator() {
            float endAngle = (float) Math.toDegrees(Math.atan2((mStartX - mEndX), (mStartY - mEndY))) * -1f;

            AnimatorSet set = new AnimatorSet();
            set.playSequentially(getRotationAnim(mStartAngle, endAngle),
                    getLinearMovingAnim(mStartX, mStartY, mEndX, mEndY));
            return set;
        }

        private Animator getRotationAnim(final float startAngle, final float endAngle) {
            L.d(TAG, "getRotationAnim - startAngle=" + startAngle + ", endAngle=" + endAngle);
            float angleDiff = endAngle - startAngle;
            if (Math.abs(angleDiff) > 180) {
                // rotate the other way
                angleDiff = (360 - Math.abs(angleDiff)) * Math.signum(angleDiff) * -1f;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(0, angleDiff);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(Math.round(Math.abs(angleDiff) / CAR_ROTATING_SPEED * 1000f));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float angle = startAngle + (float) animation.getAnimatedValue();
                    if (Math.abs(angle) > 180) {
                        angle = (360 - Math.abs(angle)) * Math.signum(angle) * -1f;
                    }
                    mListener.onPositionChanged(angle);
                }
            });
            return animator;
        }

        private Animator getLinearMovingAnim(int startX, int startY, int endX, int endY) {
            L.d(TAG, "getLinearMovingAnim - startX=" + startX + ", startY=" + startY +
                    ", endX=" + endX + ", endY=" + endY);
            final Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            final PathMeasure pathMeasure = new PathMeasure(path, false);
            final float pathLength = pathMeasure.getLength();
            ValueAnimator pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            if (pathLength < CAR_MOVING_SPEED / 1f) {
                // linear looks more realistic over short distances
                pathAnimator.setInterpolator(new LinearInterpolator());
            } else {
                pathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            pathAnimator.setDuration(Math.round(pathLength / CAR_MOVING_SPEED * 1000f));
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                private float[] point = new float[2];

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pathMeasure.getPosTan(pathMeasure.getLength() * animation.getAnimatedFraction(),
                            point, null);
                    mListener.onPositionChanged(Math.round(point[0]), Math.round(point[1]));
                }
            });
            return pathAnimator;
        }
    }

    private static class RealisticMovementMethod {

        private final OnPositionListener mListener;
        private final int mStartX;
        private final int mStartY;
        private final int mEndX;
        private final int mEndY;
        private final float mStartAngle;

        private RealisticMovementMethod(int startX, int startY, int endX, int endY, float startAngle,
                                        @NonNull OnPositionListener listener) {
            mStartX = startX;
            mStartY = startY;
            mEndX = endX;
            mEndY = endY;
            mStartAngle = startAngle;
            mListener = listener;
        }

        Animator getAnimator() {
            return getQuadMovingAnim(mStartX, mStartY, mEndX, mEndY, mStartAngle);
        }

        private Animator getQuadMovingAnim(final int startX, final int startY, int endX, int endY,
                                           float startAngle) {
            L.d(TAG, "getQuadMovingAnim - startX=" + startX + ", startY=" + startY +
                    ", endX=" + endX + ", endY=" + endY + ", startAngle=" + startAngle);
            final Path path = new Path();
            path.moveTo(startX, startY);
            double hypot = Math.hypot(startX - endX, startY - endY);
            float angle;
            if (Math.abs(startAngle) > 90) {
                angle = 180 - Math.abs(startAngle);
            } else {
                angle = Math.abs(startAngle);
            }
            int dx = (int)Math.round((hypot * Math.sin(Math.toRadians(angle))));
            int dy = (int)Math.round((hypot * Math.cos(Math.toRadians(angle))));
            int controlX = startX;
            if (startAngle < 0) {
                controlX -= dx;
            } else {
                controlX += dx;
            }
            int controlY = startY;
            if (Math.abs(startAngle) < 90) {
                controlY -= dy;
            } else {
                controlY += dy;
            }
            path.quadTo(controlX, controlY, endX, endY);

            final PathMeasure pathMeasure = new PathMeasure(path, false);
            final float pathLength = pathMeasure.getLength();
            ValueAnimator pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            if (pathLength < CAR_MOVING_SPEED / 1f) {
                // linear looks more realistic over short distances
                pathAnimator.setInterpolator(new LinearInterpolator());
            } else {
                pathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            pathAnimator.setDuration(Math.round(pathLength / CAR_MOVING_SPEED * 1000f));
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                private static final float MOVE_THRESHOLD = 0.01f;
                private float[] mNext = new float[2];
                private float[] mLast = new float[] {startX, startY};

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pathMeasure.getPosTan(pathLength * animation.getAnimatedFraction(), mNext,
                            null);
                    if (Math.abs(mNext[0] - mLast[0]) > MOVE_THRESHOLD ||
                            Math.abs(mNext[1] - mLast[1]) > MOVE_THRESHOLD) {
                        float angle = (float) Math.toDegrees(Math.atan2(mLast[0] - mNext[0],
                                mLast[1] - mNext[1])) * -1f;
                        if (Math.abs(angle) > 180) {
                            angle = (360 - Math.abs(angle)) * Math.signum(angle) * -1f;
                        }
                        mListener.onPositionChanged(Math.round(mNext[0]), Math.round(mNext[1]),
                                angle);
                        mLast[0] = mNext[0];
                        mLast[1] = mNext[1];
                    }
                }
            });
            return pathAnimator;
        }
    }
}