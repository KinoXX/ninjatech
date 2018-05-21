package com.borsa.gtp.itch.messages;

import java.nio.ByteOrder;

public interface ITCHApplicationMessage 
{
	short getLength();
	byte getType();
	void parse(byte[] msgPayload,ByteOrder order);
	byte[] getEncodedBytes(ByteOrder order);
	
}
