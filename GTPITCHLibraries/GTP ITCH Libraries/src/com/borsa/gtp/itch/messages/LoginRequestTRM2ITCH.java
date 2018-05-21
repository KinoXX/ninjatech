package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;


public class LoginRequestTRM2ITCH implements ITCHApplicationMessage {

	private static short LENGTH = 11; 
	private static byte TYPE = ITCHMessageTypes.LoginRequest_Enum.getKey();
	
	public byte[] username =  new byte[8];
	public byte[] password =  new byte[10];

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
		buffer.get(password);
		
	}


	public byte[] getEncodedBytes(ByteOrder order) {

		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		buffer.put(username,0,8);
		buffer.put(password,0,10);
		return buffer.array();
	}
	
}
