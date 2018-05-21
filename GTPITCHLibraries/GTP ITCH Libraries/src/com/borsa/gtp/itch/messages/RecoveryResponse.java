package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

/*
8.5.3	Replay Response
		Field			Offset	Length	Type	Description
		Length			0		2		UInt16	Length of message including this field.
		Message Type	2		1		Byte	Hex		Meaning
												0x04	Replay Response

		First Message	3		4		UInt32	Sequence number of the first message in range to be retransmitted. This will be zero if Status is not “A”.
		Count			7		4		UInt32	Number of messages to be resent. This will be zero if Status is not “A”.
		Status			11		1		Byte	Value	Meaning
													A	Request Accepted
													D	Request Limit Reached
													O	Out of Range
													U	Replay Unavailable
													c	Concurrent Limit Reached
													e	Failed (Other)

		Request ID		12		4		UInt32	Will include the value set as Request ID in the Replay Request message. 
  

*/

public class RecoveryResponse  implements ITCHApplicationMessage {
	
	private static short LENGTH = 16;
	private static byte TYPE = ITCHMessageTypes.RecoveryResponse_Enum.getKey();
	
	public int first_sequence_num;
	public int count;
	public byte status;
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
		
		
		first_sequence_num = buffer.getInt();
		count = buffer.getInt();
		status = buffer.get();
		request_id = buffer.getInt();
		
		
	}

	public byte[] getEncodedBytes(ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		
		buffer.putInt(first_sequence_num);
		buffer.putInt(count);
		buffer.put(status);
		
		buffer.putInt(request_id);	
		
		return buffer.array();
	}



}
