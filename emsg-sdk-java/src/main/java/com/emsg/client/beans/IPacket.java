package com.emsg.client.beans;

public interface IPacket<T> {
	
	public void setEnvelope(IEnvelope envelope);
	
	public IEnvelope getEnvelope();
	
	public void setPayload(T payload);

	public T getPayload();
	
	public void setEntity(Entity entity);

	public Entity getEntity();
	
	public void setVsn(String vsn);
	
	public String getVsn();
	
}
