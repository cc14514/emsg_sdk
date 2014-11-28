
package com.emsg.sdk.client.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.emsg.sdk.client.utils.ThumbExtractor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMsgViewAdapter extends BaseAdapter {

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;

    private LayoutInflater mInflater;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        ChatMsgEntity entity = coll.get(position);

        if (entity.getMsgType()) {
            return IMsgViewType.IMVT_COM_MSG;
        } else {
            return IMsgViewType.IMVT_TO_MSG;
        }

    }

    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatMsgEntity entity = coll.get(position);
        final boolean isComMsg = entity.getMsgType();

        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (isComMsg) {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_left, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_right, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView
                    .findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView
                    .findViewById(R.id.tv_username);
            viewHolder.tvText = (TextView) convertView
                    .findViewById(R.id.tv_chat_text);
            viewHolder.tvImage = (ImageView) convertView
                    .findViewById(R.id.tv_chat_image);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tv_time);
            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvSendTime.setText(entity.getDate());

        if (entity.getType() != null && entity.getType().equals("audio")) {
            new File(android.os.Environment.getExternalStorageDirectory() + "/emsg/receive/audio/")
                    .mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory() + "/emsg/send/audio/")
                    .mkdirs();
            viewHolder.tvText.setText("");
            viewHolder.tvText.setVisibility(View.VISIBLE);
            viewHolder.tvImage.setVisibility(View.GONE);
            viewHolder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.chatto_voice_playing, 0);
            viewHolder.tvTime.setText(entity.getTime());

            if (isComMsg) {
                String key = entity.getText();
                String filename = android.os.Environment.getExternalStorageDirectory()
                        + "/emsg/receive/audio/" + key;
                if (!new File(filename).exists()) {
                    new AudioTask(filename).execute(key);
                }
            }
            viewHolder.tvText.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    if (isComMsg) {
                        playMusic(android.os.Environment.getExternalStorageDirectory()
                                + "/emsg/receive/audio/" + entity.getText());
                    } else {
                        playMusic(android.os.Environment.getExternalStorageDirectory() + "/"
                                + entity.getText());
                    }
                }
            });
        } else if (entity.getType() != null && entity.getType().equals("image")) {
            viewHolder.tvText.setVisibility(View.GONE);
            viewHolder.tvImage.setVisibility(View.VISIBLE);
            viewHolder.tvTime.setText("");

            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/receive/image/thumb/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/receive/image/original/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/send/image/thumb/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/send/image/original/").mkdirs();

            if (isComMsg) {
                String key = entity.getText();
                String filename = android.os.Environment.getExternalStorageDirectory()
                        + "/emsg/receive/image/thumb/" + key;
                if (new File(filename).exists()) {
                    Bitmap bMap = BitmapFactory.decodeFile(filename);
                    viewHolder.tvImage.setImageBitmap(bMap);
                } else {
                    viewHolder.tvImage.setImageResource(R.drawable.loading);
                    String options = "imageView2/2/w/200/h/200";
                    new ImageTask(viewHolder, filename).execute(filename, key, options);
                }
            } else {
                String filename = entity.getText();
                Bitmap bMap = BitmapFactory.decodeFile(filename);
                viewHolder.tvImage.setImageBitmap(ThumbExtractor.extractMiniThumb(bMap, 200, 200));
            }

            viewHolder.tvImage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (isComMsg) {

                    }
                }
            });
        } else {
            viewHolder.tvText.setText(entity.getText());
            viewHolder.tvText.setVisibility(View.VISIBLE);
            viewHolder.tvImage.setVisibility(View.GONE);
            viewHolder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            viewHolder.tvTime.setText("");
        }

        viewHolder.tvUserName.setText(entity.getName());

        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvText;
        public ImageView tvImage;

        public TextView tvTime;
        public boolean isComMsg = true;
    }

    /**
     * @Description
     * @param name
     */
    private void playMusic(String name) {
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(name);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stop() {

    }

    class ImageTask extends AsyncTask<String, Void, Boolean> {
        ViewHolder viewHolder;
        String filename;

        public ImageTask(ViewHolder viewHolder, String filename) {
            this.viewHolder = viewHolder;
            this.filename = filename;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = params[1];

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);

                InputStream is = response.getEntity().getContent();

                FileOutputStream fos = new FileOutputStream(filename);
                int ch = 0;
                while ((ch = is.read()) != -1) {
                    fos.write(ch);
                }
                fos.close();

                return true;
            } catch (Exception ex) {
                Log.e(TAG, "发送异常." + ex.getMessage(), ex);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Bitmap bMap = BitmapFactory.decodeFile(filename);
                viewHolder.tvImage.setImageBitmap(bMap);
            }
        }
    }

    class AudioTask extends AsyncTask<String, Void, Boolean> {
        String filename;

        public AudioTask(String filename) {
            this.filename = filename;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = params[0];

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);

                InputStream is = response.getEntity().getContent();

                FileOutputStream fos = new FileOutputStream(filename);
                int ch = 0;
                while ((ch = is.read()) != -1) {
                    fos.write(ch);
                }
                fos.close();

                return true;
            } catch (Exception ex) {
                Log.e(TAG, "发送异常." + ex.getMessage(), ex);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}
