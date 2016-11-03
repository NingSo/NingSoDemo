package com.ningso.ningsodemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ningso.ningsodemo.anim.ExplosionField;
import com.ningso.ningsodemo.anim.Rotate3dAnimation;
import com.xinmei365.litegame.link.LinkCountDownView;

public class TestActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    int[] mIntegerList;
    CardRecyclerViewAdapter mCardRecyclerViewAdapter;
    private ExplosionField mExplosionField;

    private TextView mTimeTextView;
    private LinkCountDownView mLinkCountDownView;

    private int clickCount = 0; //点击次数
    private int[] ClickID = new int[2]; //记录两次分别点击的ID
    long alartTime;//总耗时
    private long defaultTime = 5 * 1000;
    private int socore = 0;
    private View contentView;
    private boolean isPreView = true;
    private boolean canTouch;

    public static int[] ImageSources = {R.mipmap.emoji1, R.mipmap.emoji2, R.mipmap.emoji3, R.mipmap.emoji4, R.mipmap.emoji5, R.mipmap.emoji6,
            R.mipmap.emoji7, R.mipmap.emoji8, R.mipmap.emoji9, R.mipmap.emoji10, R.mipmap.emoji11, R.mipmap.emoji12,
            R.mipmap.emoji13, R.mipmap.emoji14, R.mipmap.emoji15, R.mipmap.emoji16, R.mipmap.emoji17};

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerlist);
        mTimeTextView = (TextView) findViewById(R.id.tv_timeset);
        contentView = findViewById(R.id.activity_test);
        mLinkCountDownView = (LinkCountDownView) findViewById(R.id.countdownview);

        mIntegerList = CardIndexManager.getIndexNum(18);
        mGridLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mCardRecyclerViewAdapter = new CardRecyclerViewAdapter();
        mRecyclerView.addItemDecoration(new SpacingDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()), false));
        mRecyclerView.setAdapter(mCardRecyclerViewAdapter);
        mExplosionField = ExplosionField.attach2Window(this);
        startGame();
        // mHandler.postDelayed(mRunnable, 5000);
    }

    private void startGame() {

        mLinkCountDownView.start(3, 0, 2500, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mCardRecyclerViewAdapter != null) {
                    mExplosionField.clear();
                    isPreView = false;
                    mIntegerList = CardIndexManager.getIndexNum(18);
                    mCardRecyclerViewAdapter.notifyDataSetChanged();
                    mRecyclerView.requestLayout();
                    mCountDownTimer.start();
                }
            }
        });
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isPreView) {
                isPreView = false;
                mCardRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTimer.cancel();
    }


    private void showPopwindow() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popView = mLayoutInflater.inflate(R.layout.game_fail, null);
        final PopupWindow popWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        popWindow.showAsDropDown(contentView, 0, 0);
    }


    private CountDownTimer mCountDownTimer = new CountDownTimer(defaultTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("", "alartTime: " + millisUntilFinished);
            alartTime = millisUntilFinished / 1000;
            mTimeTextView.setText(alartTime < 10 ? "0" + alartTime : "" + alartTime);
        }

        @Override
        public void onFinish() {
            mTimeTextView.setText("00");
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
            Log.e("@@", "ImageSources: " + ImageSources[mIntegerList[position]] + " mIntegerList[position]: " + mIntegerList[position]);
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
                                    Toast.makeText(v.getContext(), "胜利", Toast.LENGTH_SHORT).show();
                                }
                                clickCount = 0;
                            } else if (ClickID[0] == ClickID[1]) {
                                clickCount = 1;
                            } else {
                                notifyItemChanged(ClickID[0]);
                                notifyItemChanged(ClickID[1]);
                                clickCount = 0;
                            }
                        }
                    }
                }
            });
        }

        private synchronized void startFlipCardAnim(final ImageView view) {

            Rotate3dAnimation openAnimation = new Rotate3dAnimation(0, 90, view.getWidth() / 2, view.getHeight() / 2, 50, false);
            openAnimation.setDuration(320);
//            openAnimation.setFillAfter(true);
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
                    rotateAnimation.setDuration(320);
//                    rotateAnimation.setFillAfter(true);
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
