package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;


public class LoginRequest implements ITCHApplicationMessage {

	private static short LENGTH = 11; 
	private static byte TYPE = ITCHMessageTypes.LoginRequest_Enum.getKey();
	
	public byte[] username =  new byte[8];
	

	public short getLength() {
		// TODO Auto-generated method stub
		return LENGTH;
	}

	public byte getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}


	public void parse(byte[] msgPayload, ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.wrap(msgPayload);
		buffer.order(order);
		buffer.getShort();
		buffer.get();		
		buffer.get(username);
		
	}


	public byte[] getEncodedBytes(ByteOrder order) {

		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		buffer.put(username,0,8);
		
		return buffer.array();
	}
	
}
