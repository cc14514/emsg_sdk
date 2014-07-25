EMSG SDK android应用开发：
准备工作：
1、创建android应用工程

2、在工程中创建libs目录

3、将emsg_sdk.jar拷贝到libs目录

EMSG SDK使用：
1、EMSG SDK初始化

String host = "222.128.11.38";
int port = 4222;

EmsgClient<DefPayload> emsgClient = new EmsgClient<DefPayload>(host, port);
emsgClient.setProvider(new DefProvider());
emsgClient.setHeartBeat(1000*10);
emsgClient.setPacketListener(new PacketListener<DefPayload>() {

	@Override
	public void processPacket(IPacket<DefPayload> packet) {
		...
	}
});
emsgClient.auth(params[0],"123123");


2、EMSG SDK实现登录
String username = "user@test.com/123";
String password = "123456";
emsgClient.setPacketListener(new PacketListener<DefPayload>() {

	@Override
	public void processPacket(IPacket<DefPayload> packet) {
		if (packet.getEnvelope().getType() == 0) {
			if (packet.getReturn().getResult().equalsIgnoreCase("ok")) {
				// 登录成功
			} else {
				// 登录失败
			}
			return ;
		}
	}
});
emsgClient.auth(params[0],"123123");


3、EMSG SDK发送消息

String to = "to@test.com/123";
DefPacket defPacket = new DefPacket(to, "你好！", 1);
emsgClient.send(defPacket);


4、EMSG SDK接收消息
emsgClient.setPacketListener(new PacketListener<DefPayload>() {

	@Override
	public void processPacket(IPacket<DefPayload> packet) {
		...
		// 发送方账号
		String from = packet.getEnvelope().getFrom();
		// 消息内容
		String text = packet.getPayload().getContent();
	}
});

5、EMSG SDK清理
emsgClient.close();


