package com.emsg.client.beans;

import net.sf.json.JSONObject;

import com.emsg.client.Define;

public class DefProvider implements IProvider<DefPayload> {

	@Override
	public String encode(IPacket<DefPayload> packet) {
		IEnvelope envelope = packet.getEnvelope();
		DefPayload payload = packet.getPayload();
		JSONObject j_envelope = JSONObject.fromObject(envelope);
		JSONObject j_payload = JSONObject.fromObject(payload);
		JSONObject j_packet = new JSONObject();
		j_packet.put("envelope", j_envelope);
		j_packet.put("payload", j_payload);
		j_packet.put("vsn", Define.VSN);
		return j_packet.toString();
	}

	@Override
	public IPacket<DefPayload> decode(String packet) {
		JSONObject jp = JSONObject.fromObject(packet);
		JSONObject jenvelope = jp.getJSONObject("envelope");
		JSONObject jpayload = jp.getJSONObject("payload");
		String vsn = Define.VSN;
		if(jp.has("vsn")){
			vsn = jp.getString("vsn");
		}
		
		JSONObject jentity = null;
		if(jp.has("entity")){
			jentity = jp.getJSONObject("entity");
		}
		
		IEnvelope envelope = (IEnvelope)JSONObject.toBean(jenvelope, Envelope.class);
		DefPayload payload = null;
		Entity entity = null;
		IPacket<DefPayload> r = new DefPacket(envelope,payload);
		r.setVsn(vsn);
		if(jpayload!=null){
			payload = (DefPayload)JSONObject.toBean(jpayload, DefPayload.class);
			r.setPayload(payload);
		}
		if(jentity!=null){
			entity = (Entity)JSONObject.toBean(jentity, Entity.class);
			r.setEntity(entity);
		}
		return r;
	}

}
