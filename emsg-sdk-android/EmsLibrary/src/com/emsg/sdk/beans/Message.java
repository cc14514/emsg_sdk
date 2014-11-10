
package com.emsg.sdk.beans;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/*	
 CREATE TABLE message (
 _id INTEGER PRIMARY KEY, 
 mid TEXT unique,
 jid_from TEXT,
 jid_to TEXT,
 gid TEXT, 
 type INTEGER, 
 ct INTEGER, 
 content_type TEXT,
 content_length TEXT,
 content TEXT	
 )
 */
public class Message implements Parcelable {

    /**
	 * 
	 */

    private long id;
    private String mid = null;
    private String jid_from = null;
    private String jid_to = null;
    private String gid = null;
    private int type = 0;
    private long ct = 0;
    private String contentType = null;
    private String contentLength = null;
    private String content = null;
    private Uri mFileUri;

    public Uri getmFileUri() {
        return mFileUri;
    }

    public void setmFileUri(Uri mFileUri) {
        this.mFileUri = mFileUri;
    }

    public Message() {
        super();
    }

    public Message(String mid, String jid_from, String jid_to, String gid, int type, long ct) {
        super();
        this.mid = mid;
        this.jid_from = jid_from;
        this.jid_to = jid_to;
        this.gid = gid;
        this.type = type;
        this.ct = ct;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getJid_from() {
        return jid_from;
    }

    public void setJid_from(String jid_from) {
        this.jid_from = jid_from;
    }

    public String getJid_to() {
        return jid_to;
    }

    public void setJid_to(String jid_to) {
        this.jid_to = jid_to;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel mParcel, int arg1) {
        mParcel.writeString(mid);
        mParcel.writeString(jid_from);
        mParcel.writeString(jid_to);
        mParcel.writeString(gid);
        mParcel.writeString(contentType);
        mParcel.writeString(content);
        mParcel.writeString(contentLength);
        mParcel.writeInt(type);
        mParcel.writeLong(ct);
        
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            Message message = new Message();
            message.mid = source.readString();
            message.jid_from = source.readString();
            message.jid_to = source.readString();
            message.gid = source.readString();
            message.contentType = source.readString();
            message.content = source.readString();
            message.contentLength = source.readString();
            message.type = source.readInt();
            message.ct = source.readLong();
            return message;
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
