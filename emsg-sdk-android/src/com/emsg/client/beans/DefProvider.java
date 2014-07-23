package com.emsg.client.beans;

import org.json.JSONException;
import org.json.JSONObject;

import com.emsg.client.Define;

public class DefProvider implements IProvider<DefPayload> {

	@Override
	public String encode(IPacket<DefPayload> packet) {
		IEnvelope envelope = packet.getEnvelope();
		DefPayload payload = packet.getPayload();
		try {
			
			JSONObject j_envelope = envelope.toJSON();
			JSONObject j_payload = payload.toJSON();
			JSONObject j_packet = new JSONObject();
			j_packet.put("payload", j_payload);
			j_packet.put("vsn", Define.VSN);
			j_packet.put("envelope", j_envelope);
			return j_packet.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public IPacket<DefPayload> decode(String packet) throws JSONException {
		
		JSONObject jp = new JSONObject(packet);
		String vsn = Define.VSN;
		if(jp.has("vsn")){
			vsn = jp.getString("vsn");
		}
		
		JSONObject jentity = null;
		Entity entity = null;
		if(jp.has("entity")){
			jentity = jp.getJSONObject("entity");
			entity = Entity.fromJSON(jentity);
		}
		
		IEnvelope envelope = null;
		if(jp.has("envelope")){
			JSONObject jenvelope = jp.getJSONObject("envelope");
			envelope = Envelope.fromJSON(jenvelope);
		}
		
		DefPayload payload = null;
		if(jp.has("payload")){
			JSONObject jpayload = jp.getJSONObject("payload");
			payload = DefPayload.fromJSON(jpayload);
		}
		
		IPacket<DefPayload> r = new DefPacket(envelope,payload);
		r.setVsn(vsn);
		r.setEntity(entity);
		return r;
	}

}
