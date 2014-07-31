package com.emsg.client.beans;

import java.util.Map;
import java.util.UUID;

import com.emsg.client.Define;

/*	
{
"envelope":{
  "id":"UUID，要求必须唯一"
  "from":"发送人JID",
  "to":"接收人JID",
  "type":"int型，含义是 消息类型",
  "ack":"int型，0 或空是不必响应，1 必须响应",
  "ct":"13位时间戳，由服务器来补充此值",
  "pwd":"只有当 type=0 时，即 opensession 时，才会使用此属性",
  "gid":"群ID，只在 type=2 时会用到此属性"
},
"vsn":"消息版本(预留属性)",
"payload":{...},
"return":{"result":"","reason":""}
}	
*/
/**
 * 一个数据包，包含envelope和一个payload
 * @author liangc
 *
 */
public class DefPacket implements IPacket<DefPayload>{
	
	private IEnvelope envelope = null;
	
	private DefPayload payload = null;
	
	private Entity entity = null;
	
	private String vsn = Define.VSN;
	
	public DefPacket() {
		super();
	}
	
	public DefPacket(String to,String content,Integer type){
		quickBuild(to,content,type,Define.ACK_ENABLE,null);
	}
	
	public DefPacket(String to,String content,Integer type,Integer ack){
		quickBuild(to,content,type,ack,null);
	}
	
	public DefPacket(String to,String content,Integer type,Integer ack,Map<String,String> attrs){
		quickBuild(to,content,type,ack,attrs);
	}
	
	private void quickBuild(String to,String content,Integer type,Integer ack,Map<String,String> attrs){
		IEnvelope envelope = new Envelope();
		envelope.setId(UUID.randomUUID().toString());
		envelope.setAck(ack);
		envelope.setTo(to);
		envelope.setType(type);
		
		DefPayload payload = new DefPayload();
		payload.setContent(content);
		payload.setAttrs(attrs);
		
		this.envelope = envelope;
		this.payload = payload;
	}
	
	public DefPacket(IEnvelope envelope, DefPayload payload) {
		super();
		this.envelope = envelope;
		this.payload = payload;
	}

	@Override
	public void setEnvelope(IEnvelope envelope) {
		this.envelope = envelope;
	}

	@Override
	public void setPayload(DefPayload payload) {
		this.payload = payload;
	}

	@Override
	public IEnvelope getEnvelope() {
		return envelope;
	}

	@Override
	public DefPayload getPayload() {
		return payload;
	}

	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public String getVsn() {
		return vsn;
	}

	@Override
	public void setVsn(String vsn) {
		this.vsn = vsn;
	}

	@Override
	public String toString() {
		return "Packet [envelope=" + envelope + ", payload=" + payload
				+ ", entity=" + entity + ", vsn=" + vsn + "]";
	}
	
}
