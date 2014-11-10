
package com.emsg.sdk.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emsg.sdk.EmsgCallBack;
import com.emsg.sdk.EmsgClient;
import com.emsg.sdk.EmsgConstants;
import com.emsg.sdk.beans.Message;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    /** Called when the activity is first created. */
    private TextView mTvTitle;
    private Button mBtnSend;
    private TextView mBtnRcd;
    private Button mBtnBack;
    private ImageButton mIbLogin;
    private EditText mEditTextContent;
    private RelativeLayout mBottom;
    private ListView mListView;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private boolean isShosrt = false;
    private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
            voice_rcd_hint_tooshort;
    private ImageView img1, sc_img1;
    private SoundMeter mSensor;
    private View rcChat_popup;
    private LinearLayout del_re;
    private ImageView chatting_mode_btn, volume, sendImage;
    private boolean btn_vocie = false;
    private int flag = 1;
    private Handler mHandler = new Handler();
    private String voiceName;
    private long startVoiceT, endVoiceT;
    private ProgressDialog progressDialog;

    // emsg相关变量
    private DemoApplication mApplication;
    private EmsgClient mEmsgClient;
    private Handler handler = null;
    private ChatMsgEntity entity = null;
    private final String[] accounts = {
            "aaa@test.com/123", "bbb@test.com/222"
    };
    private int RESULT_LOAD_IMAGE = 111;

    private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    static {
        sWorkerThread.start();
    }
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private void runOnWorkerThread(Runnable r) {
        sWorker.post(r);
    }

    public void showProgressDialog(CharSequence title, CharSequence message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIcon(android.R.drawable.ic_dialog_info);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (DemoApplication) getApplication();
        mEmsgClient = mApplication.getEmsgClient();
        // 创建属于主线程的handler
        handler = new Handler();
        setContentView(R.layout.chat);
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();

        initData();
        registerMessageReciver();
    }

    public void initView() {
        mTvTitle = (TextView) findViewById(R.id.mTvTitle);
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
        mBtnSend.setOnClickListener(this);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mIbLogin = (ImageButton) findViewById(R.id.right_btn);
        mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
        mBtnBack.setOnClickListener(this);
        chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
        sendImage = (ImageView) this.findViewById(R.id.sendImage);
        volume = (ImageView) this.findViewById(R.id.volume);
        rcChat_popup = this.findViewById(R.id.rcChat_popup);
        img1 = (ImageView) this.findViewById(R.id.img1);
        sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
        del_re = (LinearLayout) this.findViewById(R.id.del_re);
        voice_rcd_hint_rcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding);
        voice_rcd_hint_loading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading);
        voice_rcd_hint_tooshort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort);
        mSensor = new SoundMeter();
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);

        sendImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View sendImage) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }

        });

        // 语音文字切换按钮
        chatting_mode_btn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (btn_vocie) {
                    mBtnRcd.setVisibility(View.GONE);
                    mBottom.setVisibility(View.VISIBLE);
                    btn_vocie = false;
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_msg_btn);

                } else {
                    mBtnRcd.setVisibility(View.VISIBLE);
                    mBottom.setVisibility(View.GONE);
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_voice_btn);
                    btn_vocie = true;
                }
            }
        });
        mBtnRcd.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // 按下语音录制按钮时返回false执行父类OnTouch
                return false;
            }
        });

        mIbLogin.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // 走登录子线程
                showLoginDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != imageReturnedIntent) {
            final Uri imageUri = imageReturnedIntent.getData();
            sendImage(imageUri);
        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择测试账号");
        builder.setItems(accounts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String account = accounts[which];
                runOnWorkerThread(new Runnable() {

                    @Override
                    public void run() {
                        mEmsgClient.auth(account, "123123", new EmsgCallBack() {

                            @Override
                            public void onSuccess(String resutMsg) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "登录成功，开始聊天吧.",
                                                Toast.LENGTH_SHORT).show();
                                        mTvTitle.setText(mEmsgClient.getJid());
                                    }
                                });
                            }

                            @Override
                            public void onError(int errorCode, String mErrorMsg) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "登录失败",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        builder.create().show();
    }

    private final static int COUNT = 1;

    public void initData() {
        for (int i = 0; i < COUNT; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            if (i % 2 == 0) {
                entity.setName("系统管理员");
                entity.setMsgType(true);
            } else {
                entity.setName("系统管理员");
                entity.setMsgType(false);
            }

            entity.setText("欢迎试用EMSG DEMO，梦想之旅从此开始");
            mDataArrays.add(entity);
        }

        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                send();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void send() {
        final String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("我");
            entity.setMsgType(false);
            entity.setText(contString);

            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();

            mEditTextContent.setText("");

            mListView.setSelection(mListView.getCount() - 1);

            try {
                String to = "";
                if (mEmsgClient.getJid().equals(accounts[0]))
                    to = "bbb@test.com";
                if (mEmsgClient.getJid().equals(accounts[1]))
                    to = "aaa@test.com";
                final String msgTo = to;
                runOnWorkerThread(new Runnable() {

                    @Override
                    public void run() {
                        mEmsgClient.sendMessage(msgTo, contString, new EmsgCallBack() {

                            @Override
                            public void onSuccess(String resutMsg) {

                            }

                            @Override
                            public void onError(int errorCode, String mErrorMsg) {

                            }
                        });
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getDate() {
        return DateFormat.format("yyyy-MM-dd kk:mm", new Date()).toString();
    }

    // 按下语音录制按钮时
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
            return false;
        }

        if (btn_vocie) {
            System.out.println("1");
            int[] location = new int[2];
            mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int[] del_location = new int[2];
            del_re.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
                    return false;
                }
                System.out.println("2");
                if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
                    System.out.println("3");
                    mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
                    rcChat_popup.setVisibility(View.VISIBLE);
                    voice_rcd_hint_loading.setVisibility(View.VISIBLE);
                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    voice_rcd_hint_tooshort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                voice_rcd_hint_loading.setVisibility(View.GONE);
                                voice_rcd_hint_rcding
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    startVoiceT = System.currentTimeMillis();
                    voiceName = startVoiceT + ".amr";
                    start(voiceName);
                    flag = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
                System.out.println("4");
                mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) {
                    rcChat_popup.setVisibility(View.GONE);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    stop();
                    flag = 1;
                    File file = new File(android.os.Environment.getExternalStorageDirectory() + "/"
                            + voiceName);
                    Log.d("debug", file.getAbsolutePath());
                    if (file.exists()) {
                        file.delete();
                    }
                } else {

                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    stop();
                    endVoiceT = System.currentTimeMillis();
                    flag = 1;
                    int time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (time < 1) {
                        isShosrt = true;
                        voice_rcd_hint_loading.setVisibility(View.GONE);
                        voice_rcd_hint_rcding.setVisibility(View.GONE);
                        voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                voice_rcd_hint_tooshort
                                        .setVisibility(View.GONE);
                                rcChat_popup.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        return false;
                    }
                    File file = new File(android.os.Environment.getExternalStorageDirectory() + "/"
                            + voiceName);
                    sendVoice(file, time);
                    rcChat_popup.setVisibility(View.GONE);

                }
            }
            if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
                System.out.println("5");
                Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.cancel_rc2);
                img1.setVisibility(View.GONE);
                del_re.setVisibility(View.VISIBLE);
                del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) {
                    del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
                    sc_img1.startAnimation(mLitteAnimation);
                    sc_img1.startAnimation(mBigAnimation);
                }
            } else {

                img1.setVisibility(View.VISIBLE);
                del_re.setVisibility(View.GONE);
                del_re.setBackgroundResource(0);
            }
        }
        return super.onTouchEvent(event);
    }

    private static final int POLL_INTERVAL = 300;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        volume.setImageResource(R.drawable.amp1);
    }

    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    private void registerMessageReciver() {
        IntentFilter mIntentFiter = new IntentFilter();
        // 注册即时消息接收广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECDATA);
        // 接收离线消息广播
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECOFFLINEDATA);
        // 接收消息服务开启广播即session连接成功
        mIntentFiter.addAction(EmsgConstants.MSG_ACTION_SESSONOPENED);
        // 对应的上下文对象
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context mContext, Intent mIntent) {
                Message message = (Message) mIntent.getParcelableExtra("message");
                if (message == null)
                    return;
                entity = new ChatMsgEntity();
                entity.setDate(getDate());
                entity.setName(message.getJid_from());
                entity.setMsgType(true);
                String type = message.getContentType();
                if (type != null) {
                    entity.setType(type);
                } else {
                    entity.setType("text");
                }

                entity.setText(message.getContent());

                mDataArrays.add(entity);
                handler.post(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(mListView.getCount() - 1);
                    }
                });
            }

        }, mIntentFiter);
    }

    void sendVoice(final File file, final int length) {
        String to = "";
        if (mEmsgClient.getJid().equals(accounts[0]))
            to = "bbb@test.com";
        if (mEmsgClient.getJid().equals(accounts[1]))
            to = "aaa@test.com";
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setDate(getDate());
        entity.setName("高富帅");
        entity.setMsgType(false);
        entity.setType("audio");
        entity.setTime(length + "\"");
        entity.setText(voiceName);
        mDataArrays.add(entity);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mListView.getCount() - 1);
        final String mEmsgTo = to;
        runOnWorkerThread(new Runnable() {

            @Override
            public void run() {

                mEmsgClient.sendAudioMessage(Uri.fromFile(file), length, mEmsgTo, null,
                        new EmsgCallBack() {

                            @Override
                            public void onSuccess(String resutMsg) {

                            }

                            @Override
                            public void onError(int errorCode, String mErrorMsg) {
                            }
                        });
            }
        });

    }

    private void sendImage(final Uri imageUri) {

        String to = "";
        if (mEmsgClient.getJid().equals(accounts[0]))
            to = "bbb@test.com";
        if (mEmsgClient.getJid().equals(accounts[1]))
            to = "aaa@test.com";
        final String mEmsgTo = to;
        runOnWorkerThread(new Runnable() {

            @Override
            public void run() {
                mEmsgClient.sendImageMessage(imageUri, mEmsgTo, null, new EmsgCallBack() {

                    @Override
                    public void onSuccess(String resutMsg) {

                    }

                    @Override
                    public void onError(int errorCode, String mErrorMsg) {

                    }
                });
            }
        });

        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setDate(getDate());
        entity.setName("高富帅");
        entity.setMsgType(false);
        entity.setType("image");
        entity.setText(getRealPathFromURI(imageUri));
        mDataArrays.add(entity);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mListView.getCount() - 1);

    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    Runnable runnableUi = new Runnable() {
        public void run() {
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mListView.getCount() - 1);
        }

    };

    @Override
    public void onDestroy() {
        try {
            mEmsgClient.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
