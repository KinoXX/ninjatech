package com.borsa.gtp.itch.messages;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

public enum ReplayRecoveryStatusCode {
	
	RequestedAccepted((byte)'A'),
	ReplayLimitReached((byte)'D'),
	OutOfRange((byte)'O'),
	ReplayAnavailable((byte)'U'),
	InvalidGroup((byte)'a'),
	RecoveryLimitReached((byte)'b'),	
	ConcurrentLimitReached((byte)'c'),
	InvalidRecoveryRequest((byte)'d'),
	OtherFailure((byte)'e');
	
	private byte key;
	
	private ReplayRecoveryStatusCode (byte key)
	{
		this.key = key;
	}
	
	public byte getKey() 
	{
	    return key;
	}
	
	public static ReplayRecoveryStatusCode fromValue(byte thisKey) {
	    for (ReplayRecoveryStatusCode msgType : ReplayRecoveryStatusCode.values()) {
	        if (msgType.key == thisKey) return msgType;
	    }
	    throw new IllegalArgumentException();
	}

}
