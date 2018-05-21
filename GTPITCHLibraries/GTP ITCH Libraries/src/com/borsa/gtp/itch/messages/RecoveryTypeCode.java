package com.borsa.gtp.itch.messages;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

public enum RecoveryTypeCode {
	
	InstrumentDirectory((byte)0x00),
	OrderBook((byte)0x01),
	AllTrades((byte)0x02),
	Statistics((byte)0x03),
	InstrumentStatus((byte)0x04),
	Announcements((byte)0x05);
	
	private byte key;
	
	private RecoveryTypeCode (byte key)
	{
		this.key = key;
	}
	
	public byte getKey() 
	{
	    return key;
	}
	
	public static RecoveryTypeCode fromValue(byte thisKey) {
	    for (RecoveryTypeCode msgType : RecoveryTypeCode.values()) {
	        if (msgType.key == thisKey) return msgType;
	    }
	    throw new IllegalArgumentException();
	}

}
