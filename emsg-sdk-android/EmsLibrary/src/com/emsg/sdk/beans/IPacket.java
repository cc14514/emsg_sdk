package com.emsg.sdk.beans;

public interface IPacket<T> {
	
	public void setEnvelope(IEnvelope envelope);
	
	public IEnvelope getEnvelope();
	
	public void setPayload(T payload);

	public T getPayload();
	
	public void setEntity(Entity entity);

	public Entity getEntity();
	
	public void setVsn(String vsn);
	
	public String getVsn();
	
	public void setDelay(Delay<T> delay);
	
	public Delay<T> getDelay();
	
	public void setPubsub(Pubsub pubsub);
	
	public Pubsub getPubsub();
	
}
