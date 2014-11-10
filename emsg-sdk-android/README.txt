EMSG SDK android应用开发：
准备工作：
1、创建android应用工程

2、在工程中创建libs目录

3、将emsg_sdk.jar拷贝到libs目录

EMSG SDK使用：



在Application中初始化消息服务引擎

/**
 * DemoApplication 名称可根据项目具体情况修改 ，但是不要忘记在AndroidManifest.xml 的<applicaion>
 * 节点下注册android:name=""
 */
public class DemoApplication extends Application {
    EmsgClient mEmsgClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mEmsgClient = EmsgClient.getInstance();
        mEmsgClient.init(this);
    }

    public EmsgClient getEmsgClient() {
        return mEmsgClient;
  }






登录认证


    //context：上下文对象
    EmsgApplication mAppLication = (EmsgApplication) context.getApplication();
    EmsgClient mEmsgClient = mAppLication.getEmsgClient();
    String uid = "您的登陆账户";
    String pwd = "登陆密码";
    mEmsgClient.auth(uid, pwd, new EmsgCallBack() {

        @Override
        public void onSuccess(String resutMsg) {
        }

        @Override
        public void onError(int errorCode, String mErrorMsg) {
        }
    });

    



发送消息：


    获取emsg消息服务引擎对象方法如下：
    context:为上下文对象,能够支持在任意组件中调用消息服务
    EmsgApplication mAppLication = (EmsgApplication) context.getApplication();
    EmsgClient mEmsgClient = mAppLication.getEmsgClient();


发送文本消息


    String toAccount = "发送给对方的账户名";
    String msgDital = "消息内容";
    mAppLication.getEmsgClient().sendMessage(toAccount, msgDital,
    new EmsgCallBack() {

    @Override
     public void onError(int errorCode, String mErrorMsg) {
        }
     @Override
    public void onSuccess(String resutMsg) {
    }
    });


发送图片

    String toAccount = "发送给对方的账户名";
    Uri uri; // 图片文件URI地址
    Map<String,String> mMap; //用于消息扩展
    mAppLication.getEmsgClient().sendImageMessage(uri, toAccount, mMap,
            new EmsgCallBack() {
                    @Override
        public void onSuccess(String result) {
                }
                    @Override
        public void onError(int errorCode, String mErrorMsg) {
                }
    });
    
    
    
发送语音消息
 

    String toAccount = "发送给对方的账户名";
    int mVoiceDuration = 3; // 语音文件时长 3s
    Uri uri; // 语音文件URI地址
    Map<String,String> mMap; //用于消息扩展
    mAppLication.getEmsgClient().sendAudioMessage(uri, mVoiceDuration,
        toAccount, mMap, new EmsgCallBack() {

         @Override
        public void onError(int errorCode, String mErrorMsg) {
                }
        @Override
        public void onSuccess(String resutMsg) {
            //resutMsg 为语音信息的地址
                    }
    });



接收消息

    IntentFilter mIntentFiter = new IntentFilter();
    //注册即时消息接收广播
    mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECDATA);
    //接收离线消息广播
    mIntentFiter.addAction(EmsgConstants.MSG_ACTION_RECOFFLINEDATA);
    //接收消息服务开启广播即session连接成功
    mIntentFiter.addAction(EmsgConstants.MSG_ACTION_SESSONOPENED);
    //对应的上下文对象
    context.registerReceiver(new BroadcastReceiver() {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            Message message = (Message) mIntent.getParcelableExtra("message");
            //根据接收到具体消息类型执行相关处理
        }
    }, mIntentFiter);


 Message 的具体结构


   见接收消息部分Message是在接收到消息广播后通过(Message) mIntent.getParcelableExtra("message");
   Message中被序列化的参数如下(即可以在接收到的Message对象中获取如下属性)：
   String jid_from ; 消息发送方账号
   String jid_to ;   消息接收方账号
   String gid ;
   long ct ;           消息发送时间戳
   String contentType; 消息类型 分为"text","image","audio"
   String contentLength ;语音时长
   String content;      文本消息的具体内容,图片及语音的加密路径名称


下载图片

    Message message = ‘接收到图片信息的Message对象’ ;
    String content = message.getContent();
    图片下载路径：http://emsg.qiniudn.com/ + content+ "imageView2/2/w/200/h/200";


下载语音
    
 同下载图片但是语音下载地址如下：
 http://emsg.qiniudn.com/+ content



关闭emsg消息服务引擎
     注意：若退出应用时不接受消息则需要关闭消息服务否则程序会仍然在后台运行
     mAppLication.getEmsgClient().close();



配置文件相关

    权限需求：

    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK"/>

    注册消息服务：

    <service android:name="com.emsg.sdk.EmsgService" ></service>

