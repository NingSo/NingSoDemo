package com.ningso.ningsodemo;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ningso.ningsodemo.anim.ExplosionField;
import com.ningso.ningsodemo.services.EffectService;
import com.ningso.ningsodemo.services.SoundService;

public class GameActivity extends AppCompatActivity {

    private ExplosionField mExplosionField;

    static ImageView[] PictureBoxs = new ImageView[12];

    static int[] Index = new int[12];

    private int Count = 0;

    private int[] ClickID = new int[2];


    private int alstart = 0;
    int alltime = 0;
    private TextView tvUseTime;
    private TextView tvErrorTimes;
    private int socore = 0;

    int wrongNum = 0;

    int setMaxWrongNum = 3;
    int setMaxTime = 60;
    int setMemoryTime = 3;
    Boolean canTouch = false;

    SharedPreferences preferences;
    SharedPreferences.Editor sp_edit;
    private static final String MAXWRONGNUMBER = "max-wrong-number";
    private static final String MAXGAMETIME = "max-game-time";
    private static final String MAXMEMORYTIME = "max-memory-time";

    public static int[] ImageSource = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6,
            R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11,
            R.drawable.image12, R.drawable.image13, R.drawable.image14, R.drawable.image15, R.drawable.image16,
            R.drawable.image17, R.drawable.image18, R.drawable.image19, R.drawable.image20, R.drawable.image21,
            R.drawable.image22, R.drawable.image23, R.drawable.image24, R.drawable.image25, R.drawable.image26,
            R.drawable.image27, R.drawable.image28, R.drawable.image29, R.drawable.image30, R.drawable.image31,
            R.drawable.image32, R.drawable.image33, R.drawable.image34, R.drawable.image35, R.drawable.image36,
            R.drawable.image36};

    private void init() {
        PictureBoxs[0] = (ImageView) findViewById(R.id.ImageView00);
        PictureBoxs[1] = (ImageView) findViewById(R.id.ImageView01);
        PictureBoxs[2] = (ImageView) findViewById(R.id.ImageView02);
        PictureBoxs[3] = (ImageView) findViewById(R.id.ImageView03);
        PictureBoxs[4] = (ImageView) findViewById(R.id.ImageView04);
        PictureBoxs[5] = (ImageView) findViewById(R.id.ImageView05);
        PictureBoxs[6] = (ImageView) findViewById(R.id.ImageView06);
        PictureBoxs[7] = (ImageView) findViewById(R.id.ImageView07);
        PictureBoxs[8] = (ImageView) findViewById(R.id.ImageView08);
        PictureBoxs[9] = (ImageView) findViewById(R.id.ImageView09);
        PictureBoxs[10] = (ImageView) findViewById(R.id.ImageView10);
        PictureBoxs[11] = (ImageView) findViewById(R.id.ImageView11);
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_game);
        findViewById(R.id.btn_Exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExitGame();
            }
        });
        findViewById(R.id.btn_newgame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alltime = 0;
                wrongNum = 0;
                socore = 0;
                canTouch = false;
                tvErrorTimes.setText("错误次数: " + wrongNum);
                Index = GetIndex.Getnum();

                if (alstart != 0) {
                    reset(findViewById(R.id.IV_Group));
                    mHandler.removeCallbacks(mRunnable);
                }

                for (int i = 0; i < 12; i++) {
                    PictureBoxs[i].setImageResource(ImageSource[Index[i]]);
                }

                mHandler.post(mRunnable);
                Intent intent = new Intent(GameActivity.this, SoundService.class);
                intent.putExtra("playing", true);
                startService(intent);
                alstart++;
            }
        });

        preferences = getSharedPreferences("CardsGame", MODE_PRIVATE);
        sp_edit = preferences.edit();

        setMaxWrongNum = preferences.getInt(MAXWRONGNUMBER, 3);
        setMaxTime = preferences.getInt(MAXGAMETIME, 100);
        setMemoryTime = preferences.getInt(MAXMEMORYTIME, 3);

        tvUseTime = (TextView) findViewById(R.id.tv_usetime);
        tvErrorTimes = (TextView) findViewById(R.id.tv_error_num);

        mExplosionField = ExplosionField.attach2Window(this);

        init();
    }


    public void settingB(View source) {
        final LinearLayout set = (LinearLayout) getLayoutInflater().inflate(R.layout.settings, null);
        EditText etw = (EditText) set.findViewById(R.id.ev_wrongnum);
        EditText ett = (EditText) set.findViewById(R.id.ev_time);
        EditText etm = (EditText) set.findViewById(R.id.ev_mem);
        etw.setText("" + setMaxWrongNum);
        ett.setText("" + setMaxTime);
        etm.setText("" + setMemoryTime);
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setView(set)
                .setPositiveButton("确认", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etw = (EditText) set.findViewById(R.id.ev_wrongnum);
                        EditText ett = (EditText) set.findViewById(R.id.ev_time);
                        EditText etm = (EditText) set.findViewById(R.id.ev_mem);
                        setMaxWrongNum = Integer.parseInt(etw.getText().toString());
                        setMaxTime = Integer.parseInt(ett.getText().toString());
                        setMemoryTime = Integer.parseInt(etm.getText().toString());
                        sp_edit.putInt(MAXWRONGNUMBER, setMaxWrongNum);
                        sp_edit.putInt(MAXGAMETIME, setMaxTime);
                        sp_edit.putInt(MAXMEMORYTIME, setMemoryTime);
                        sp_edit.commit();
                        Toast.makeText(getApplicationContext(), "已存", Toast.LENGTH_LONG).show();
                    }
                })

                .setNegativeButton("取消", null)
                .create().show();

    }

    void stopmusic() {
        Intent intent = new Intent(GameActivity.this, EffectService.class);
        intent.putExtra("what", "quit");
        startService(intent);

        Intent nintent = new Intent(GameActivity.this, SoundService.class);
        nintent.putExtra("quit", true);
        startService(nintent);
    }

    private void reset(View root) {
        mExplosionField.clear();
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                reset(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }


    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        public void run() {
            mHandler.postDelayed(this, 1000);
            tvUseTime.setText("使用时间: " + Integer.toString(alltime++));
            if (alltime == setMemoryTime) {
                CardTurn();
                canTouch = true;
            } else if (alltime == setMaxTime) {
                canTouch = false;
                Intent nintent = new Intent(GameActivity.this, SoundService.class);
                nintent.putExtra("quit", true);
                startService(nintent);
                Intent intent = new Intent(GameActivity.this, EffectService.class);
                intent.putExtra("what", "lose");
                startService(intent);
                Toast.makeText(getApplicationContext(), "没时间了～～～", Toast.LENGTH_LONG).show();
                tvUseTime.setText("使用时间: " + alltime);
                alltime = 0;
                mHandler.removeCallbacks(mRunnable);
            }
        }
    };

    public static void CardTurn() {
        for (int i = 0; i < 12; i++) {
            PictureBoxs[i].setImageResource(R.drawable.card);
        }
    }

    public void CardTouch(View v) {
        if (canTouch) {
            Intent intent = new Intent(GameActivity.this, EffectService.class);
            intent.putExtra("what", "selected");
            startService(intent);
            if (alstart == 0 || wrongNum == setMaxWrongNum + 1) {
                Toast.makeText(this, "时间到或者错误太多了～", Toast.LENGTH_SHORT).show();
            } else {
                Count++;
                if (Count > 2) {
                    Count = 2;
                } else {
                    switch (v.getId()) {
                        case R.id.ImageView00:
                            ClickID[Count - 1] = 0;
                            PictureBoxs[0].setImageResource(ImageSource[Index[0]]);
                            break;
                        case R.id.ImageView01:
                            ClickID[Count - 1] = 1;
                            PictureBoxs[1].setImageResource(ImageSource[Index[1]]);
                            break;
                        case R.id.ImageView02:
                            ClickID[Count - 1] = 2;
                            PictureBoxs[2].setImageResource(ImageSource[Index[2]]);
                            break;
                        case R.id.ImageView03:
                            ClickID[Count - 1] = 3;
                            PictureBoxs[3].setImageResource(ImageSource[Index[3]]);
                            break;
                        case R.id.ImageView04:
                            ClickID[Count - 1] = 4;
                            PictureBoxs[4].setImageResource(ImageSource[Index[4]]);
                            break;
                        case R.id.ImageView05:
                            ClickID[Count - 1] = 5;
                            PictureBoxs[5].setImageResource(ImageSource[Index[5]]);
                            break;
                        case R.id.ImageView06:
                            ClickID[Count - 1] = 6;
                            PictureBoxs[6].setImageResource(ImageSource[Index[6]]);
                            break;
                        case R.id.ImageView07:
                            ClickID[Count - 1] = 7;
                            PictureBoxs[7].setImageResource(ImageSource[Index[7]]);
                            break;
                        case R.id.ImageView08:
                            ClickID[Count - 1] = 8;
                            PictureBoxs[8].setImageResource(ImageSource[Index[8]]);
                            break;
                        case R.id.ImageView09:
                            ClickID[Count - 1] = 9;
                            PictureBoxs[9].setImageResource(ImageSource[Index[9]]);
                            break;
                        case R.id.ImageView10:
                            ClickID[Count - 1] = 10;
                            PictureBoxs[10].setImageResource(ImageSource[Index[10]]);
                            break;
                        case R.id.ImageView11:
                            ClickID[Count - 1] = 11;
                            PictureBoxs[11].setImageResource(ImageSource[Index[11]]);
                            break;
                        default:
                            break;
                    }
                }

                if (Count == 2) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (ClickID[0] != ClickID[1] && Index[ClickID[0]] == Index[ClickID[1]]) {
                                mExplosionField.explode(PictureBoxs[ClickID[0]]);
                                mExplosionField.explode(PictureBoxs[ClickID[1]]);
                                socore++;
                                Intent eintent = new Intent(GameActivity.this, EffectService.class);
                                eintent.putExtra("what", "effect");
                                startService(eintent);
                                if (socore == 6) {
                                    Intent nintent = new Intent(GameActivity.this, SoundService.class);
                                    nintent.putExtra("quit", true);
                                    startService(nintent);
                                    Intent intent = new Intent(GameActivity.this, EffectService.class);
                                    intent.putExtra("what", "win");
                                    startService(intent);
                                    Toast.makeText(getApplicationContext(), "胜利", Toast.LENGTH_LONG).show();
                                    tvUseTime.setText("使用时间: " + alltime);
                                    mHandler.removeCallbacks(mRunnable);
                                }
                            } else {
                                PictureBoxs[ClickID[0]].setImageResource(R.drawable.card);
                                PictureBoxs[ClickID[1]].setImageResource(R.drawable.card);
                                wrongNum++;
                                if (wrongNum == (setMaxWrongNum + 1)) {
                                    tvErrorTimes.setText("错误次数: " + (wrongNum - 1));
                                } else {
                                    tvErrorTimes.setText("错误次数: " + wrongNum);
                                }
                                if (wrongNum == (setMaxWrongNum + 1)) {
                                    Intent nintent = new Intent(GameActivity.this, SoundService.class);
                                    nintent.putExtra("quit", true);
                                    startService(nintent);
                                    Intent intent = new Intent(GameActivity.this, EffectService.class);
                                    intent.putExtra("what", "lose");
                                    startService(intent);
                                    Toast.makeText(getApplicationContext(), "错误次数用完了", Toast.LENGTH_LONG).show();
                                    tvUseTime.setText("使用时间: " + alltime);
                                    alltime = 0;
                                    mHandler.removeCallbacks(mRunnable);
                                    canTouch = false;
                                }
                            }
                            Count = 0;
                        }

                    }, 500);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doExitGame();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doExitGame() {
        new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage("确认退出？")
                .setPositiveButton("再玩会", null)
                .setNegativeButton("退出", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.removeCallbacks(mRunnable);
                        stopmusic();
                        finish();
                    }
                })
                .create()
                .show();
    }

}
