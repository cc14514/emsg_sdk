package com.emsg.client.beans;

import com.emsg.client.Define;
import com.emsg.client.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DefProvider implements IProvider<DefPayload> {

	@Override
	public String encode(IPacket<DefPayload> packet) {
		return new Gson().toJson(packet);
	}

	@Override
	public IPacket<DefPayload> decode(String packet) {
		JsonParser parser = new JsonParser();
		JsonElement elPacket = parser.parse(packet);
		JsonObject jPacket = elPacket.getAsJsonObject();
		
		JsonObject jenvelope = JsonUtil.getAsJsonObject(jPacket, "envelope");
		JsonObject jpayload = JsonUtil.getAsJsonObject(jPacket, "payload");
		
		String vsn = Define.VSN;
		if(elPacket.getAsJsonObject().has("vsn")){
			vsn = JsonUtil.getAsString(jPacket, "vsn");
		}
		
		JsonObject jentity = JsonUtil.getAsJsonObject(jPacket, "entity");
		Gson gson = new Gson();
		gson.fromJson(jenvelope, Envelope.class);
		IEnvelope envelope = gson.fromJson(jenvelope, Envelope.class);
		DefPayload payload = null;
		Entity entity = null;
		IPacket<DefPayload> r = new DefPacket(envelope,payload);
		r.setVsn(vsn);
		if(jpayload!=null){
			payload = gson.fromJson(jpayload, DefPayload.class);
			r.setPayload(payload);
		}
		if(jentity!=null){
			entity = gson.fromJson(jentity, Entity.class);
			r.setEntity(entity);
		}
		return r;
	}

}
