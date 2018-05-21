package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

/*

*/

public class ReplayRequest  implements ITCHApplicationMessage {
	
	private static short LENGTH = 15;
	private static byte TYPE = ITCHMessageTypes.ReplayRequest_Enum.getKey();
	
	public int first_request;
	public int count;
	public int request_id;
	
	public short getLength() {
		// TODO Auto-generated method stub
		return LENGTH;
	}

	public byte getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	


	public void parse(byte[] msgPayload,ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.wrap(msgPayload);
		buffer.order(order);
		buffer.getShort();
		buffer.get();				
		
		first_request = buffer.getInt();
		count = buffer.getInt();
		request_id = buffer.getInt();
		
		
	}

	public byte[] getEncodedBytes(ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		
		buffer.putInt(first_request);
		buffer.putInt(count);
		buffer.putInt(request_id);	
		
		return buffer.array();
	}

}
