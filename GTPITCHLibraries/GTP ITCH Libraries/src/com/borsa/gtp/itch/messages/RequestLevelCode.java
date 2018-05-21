package com.borsa.gtp.itch.messages;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

public enum RequestLevelCode {
	
	Instrument((byte)0),
	Group((byte)1),
	Channel((byte)2);
	
	private byte key;
	
	private RequestLevelCode (byte key)
	{
		this.key = key;
	}
	
	public byte getKey() 
	{
	    return key;
	}
	
	public static RequestLevelCode fromValue(byte thisKey) {
	    for (RequestLevelCode msgType : RequestLevelCode.values()) {
	        if (msgType.key == thisKey) return msgType;
	    }
	    throw new IllegalArgumentException();
	}

}
