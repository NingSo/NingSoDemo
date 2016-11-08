package com.ningso.ningsodemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ningso.ningsodemo.anim.ExplosionField;
import com.ningso.ningsodemo.anim.Rotate3dAnimation;
import com.ningso.ningsodemo.widgets.LinkCountDownView;

public class TestActivity extends Activity {

    //view
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private CardRecyclerViewAdapter mCardRecyclerViewAdapter;
    private ExplosionField mExplosionField;
    private TextView mTimeTextView;
    private LinkCountDownView mLinkCountDownView;
    private ProgressBar mProgressBar;
    private ImageView[] stars;
    private ImageView closeBtn;

    private int clickCount = 0; //点击次数
    private int[] ClickID = new int[2]; //记录两次分别点击的ID
    private int[] mIntegerList;
    private long alartTime;//总耗时
    private long defaultTime = 60 * 1000;
    private int socore = 0;
    private View contentView;
    private boolean isPreView = true;
    private boolean canTouch;
    int starCount;
    public static int[] ImageSources = {R.mipmap.emoji1, R.mipmap.emoji2, R.mipmap.emoji3, R.mipmap.emoji4, R.mipmap.emoji5, R.mipmap.emoji6,
            R.mipmap.emoji7, R.mipmap.emoji8, R.mipmap.emoji9, R.mipmap.emoji10, R.mipmap.emoji11, R.mipmap.emoji12,
            R.mipmap.emoji13, R.mipmap.emoji14, R.mipmap.emoji15, R.mipmap.emoji16, R.mipmap.emoji17};

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        startGame();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerlist);
        mTimeTextView = (TextView) findViewById(R.id.time);
        contentView = findViewById(R.id.activity_test);
        mLinkCountDownView = (LinkCountDownView) findViewById(R.id.countdownview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        stars = new ImageView[]{(ImageView) findViewById(R.id.star1),
                (ImageView) findViewById(R.id.star2),
                (ImageView) findViewById(R.id.star3)};
        mGridLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()), false));
        closeBtn = (ImageView) findViewById(R.id.iv_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTimeLeft(60);
    }

    private void startGame() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        socore = 0;
        alartTime = 0;
        clickCount = 0;
        ClickID = new int[2];
        setTimeLeft(60);
        mLinkCountDownView.start(3, 0, 2500, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRecyclerView.setVisibility(View.VISIBLE);
                isPreView = true;
                mExplosionField = ExplosionField.attach2Window(TestActivity.this);
                mIntegerList = CardIndexManager.getIndexNum(18);
                mCardRecyclerViewAdapter = new CardRecyclerViewAdapter();
                mRecyclerView.setAdapter(mCardRecyclerViewAdapter);
                mHandler.postDelayed(mRunnable, 3000);
            }
        });
    }

    private void setTimeLeft(int seconds) {
        mProgressBar.setProgress(seconds * 100 / 60);
        mTimeTextView.setText(String.format("%02d", seconds));
        if (seconds == 60) {
            for (ImageView star : stars) {
                star.setImageResource(R.drawable.star_win_small);
            }
            starCount = 3;
        } else if (seconds == 0) {
            showLosePopupWindow();
        } else {
            if (seconds < 15 && starCount > 0) {
                grayStar(0);
            }
            if (seconds < 30 && starCount > 1) {
                grayStar(1);
            }
            if (seconds < 45 && starCount > 2) {
                grayStar(2);
            }
        }
    }

    private void grayStar(int n) {
        starCount = n;
        stars[n].setImageResource(R.drawable.star_lose_small_gray);
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isPreView) {
                isPreView = false;
                mCardRecyclerViewAdapter.notifyDataSetChanged();
                mCountDownTimer.start();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTimer.cancel();
    }


    private void showSuccessPopwindow(int starCount) {
        View popView = getLayoutInflater().inflate(R.layout.game_win, null);
        ImageView IvNext = (ImageView) popView.findViewById(R.id.btn_next);
        ImageView[] stars = new ImageView[]{
                (ImageView) popView.findViewById(R.id.star1),
                (ImageView) popView.findViewById(R.id.star2),
                (ImageView) popView.findViewById(R.id.star3)
        };
        for (int i = 0; i < starCount; i++) {
            if (i == 1) {
                stars[i].setImageResource(R.drawable.star_win_big);
            } else {
                stars[i].setImageResource(R.drawable.star_win_small);
            }
        }
        final PopupWindow popWindow = new PopupWindow(popView);
        popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 272, getResources().getDisplayMetrics()));
        popWindow.setFocusable(false);
        popWindow.setOutsideTouchable(false);
        popView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == KeyEvent.KEYCODE_BACK;
            }
        });
        popWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        IvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                startGame();
            }
        });
    }

    private void showLosePopupWindow() {
        View popView = getLayoutInflater().inflate(R.layout.game_fail, null);
        ImageView ivReplay = (ImageView) popView.findViewById(R.id.btn_replay);
        final PopupWindow popWindow = new PopupWindow(popView);
        popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 272, getResources().getDisplayMetrics()));
        popWindow.setFocusable(false);
        popWindow.setOutsideTouchable(false);
        popView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == KeyEvent.KEYCODE_BACK;
            }
        });
        popWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        ivReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                startGame();
            }
        });
    }


    private CountDownTimer mCountDownTimer = new CountDownTimer(defaultTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("", "alartTime: " + millisUntilFinished);
            alartTime = millisUntilFinished / 1000;
            if (alartTime > 0) {
                setTimeLeft((int) alartTime);
            }
        }

        @Override
        public void onFinish() {
            setTimeLeft(0);
        }
    };


    class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.CardViewHolder> {
        CardViewHolder mPreViewHolder;

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(final CardViewHolder holder, final int position) {
            if (isPreView) {
                holder.ivCard.setImageResource(ImageSources[mIntegerList[position]]);
            } else {
                startFlipCardAnim(holder.ivCard);
                holder.ivCard.setImageResource(R.mipmap.defultcard);
            }
            holder.ivCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isPreView && canTouch) {
                        clickCount++;
                        if (clickCount > 2) {
                            clickCount = 2;
                        } else {
                            ClickID[clickCount - 1] = position;
                            holder.ivCard.setImageResource(ImageSources[mIntegerList[position]]);
                            if (clickCount == 1) {
                                mPreViewHolder = holder;
                            }
                            if (ClickID[0] == ClickID[1]) {
                                clickCount = 1;
                                return;
                            }
                        }
                        if (clickCount == 2) {
                            if (ClickID[0] != ClickID[1] && mIntegerList[ClickID[0]] == mIntegerList[ClickID[1]]) {
                                mExplosionField.explode(mPreViewHolder.ivCard);
                                mExplosionField.explode(holder.ivCard);
                                mPreViewHolder.ivCard.setOnClickListener(null);
                                holder.ivCard.setOnClickListener(null);
                                socore++;
                                if (socore == 9) {
                                    socore = 0;
                                    if (starCount < 1) {
                                        showLosePopupWindow();
                                    } else {
                                        showSuccessPopwindow(starCount);
                                    }
                                    mCountDownTimer.cancel();
                                }
                                clickCount = 0;
                            } else if (ClickID[0] == ClickID[1]) {
                                clickCount = 1;
                            } else {
                                Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                                shake.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        canTouch = false;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        notifyItemChanged(ClickID[0]);
                                        notifyItemChanged(ClickID[1]);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                mPreViewHolder.ivCard.startAnimation(shake);
                                holder.ivCard.startAnimation(shake);
                                clickCount = 0;
                            }
                        }
                    }
                }
            });
        }

        private synchronized void startFlipCardAnim(final ImageView view) {
            Rotate3dAnimation openAnimation = new Rotate3dAnimation(0, 90, view.getWidth() / 2, view.getHeight() / 2, 50, true);
            openAnimation.setDuration(320);
            openAnimation.setFillAfter(true);
            openAnimation.setInterpolator(new AccelerateInterpolator());
            openAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    canTouch = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    canTouch = true;
                    Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(270, 360, view.getWidth() / 2, view.getHeight() / 2, 50, false);
                    rotateAnimation.setDuration(150);
                    rotateAnimation.setFillAfter(true);
                    rotateAnimation.setInterpolator(new DecelerateInterpolator());
                    view.startAnimation(rotateAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(openAnimation);
        }

        @Override
        public int getItemCount() {
            return mIntegerList.length;
        }

        class CardViewHolder extends RecyclerView.ViewHolder {

            ImageView ivCard;

            CardViewHolder(View itemView) {
                super(itemView);
                ivCard = (ImageView) itemView.findViewById(R.id.iv_card);

            }
        }
    }
}
