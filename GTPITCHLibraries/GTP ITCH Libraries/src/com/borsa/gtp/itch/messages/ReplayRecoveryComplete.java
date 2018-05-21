package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

/*
8.5.5	Replay and Recovery Complete
		Field			Offset	Length	Type	Description
		Length			0		2		UInt16	Length of message including this field.
		Message Type	2		1		Byte	Hex		Meaning
												0x83	Replay and Recovery Complete

		Request ID		3		4		UInt32	Will include the value set as Request ID in the Snapshot Request message. 
		Trading Status	7		1		Byte	Current Trading status of the Instrument. Populated only when the message is sent at the end of individual order book snapshots.
												Please refer the Additional Field Values section of this document for valid values

  

*/

public class ReplayRecoveryComplete  implements ITCHApplicationMessage {
	
	private static short LENGTH = 8;
	private static byte TYPE =  ITCHMessageTypes.ReplayRecoveryComplete_Enum.getKey();
	
	public int request_id;
	public byte trading_status;
	
	
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
		
		request_id = buffer.getInt();
		trading_status = buffer.get();
		
		
	}

	public byte[] getEncodedBytes(ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);		
		
		buffer.putInt(request_id);	
		buffer.put(trading_status);	
		
		return buffer.array();
	}



}
