package com.emsg.client.beans;

import java.util.ArrayList;
import java.util.List;

import com.emsg.client.Define;
import com.emsg.client.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
		JsonObject jdelay = JsonUtil.getAsJsonObject(jPacket, "delay");
		IPacket<DefPayload> r = decode(elPacket);
		Delay<DefPayload> delay = delay(jdelay);
		r.setDelay(delay);
		return r;
	}

	public IPacket<DefPayload> decode(JsonElement elPacket) {
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
	
	private Delay<DefPayload> delay(JsonObject jdelay){
		Delay<DefPayload> delay = null;
		if(jdelay!=null){
			int total = jdelay.get("total").getAsInt();
			if(total>0){
				delay = new Delay<DefPayload>();
				delay.setTotal(total);
				List<IPacket<DefPayload>> packets = new ArrayList<IPacket<DefPayload>>();
				JsonArray jpackets = jdelay.get("packets").getAsJsonArray();
				for(int i=0;i<jpackets.size();i++){
					JsonElement e = jpackets.get(i);
					IPacket<DefPayload> p = decode(e);
					packets.add(p);
				}
				delay.setPackets(packets);
			}
		}
		return delay;
	}
	
}
