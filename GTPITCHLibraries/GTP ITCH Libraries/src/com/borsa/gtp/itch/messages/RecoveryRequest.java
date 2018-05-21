package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

/*
8.4.3	Recovery Request
	Field			Offset	Length	Type	Description
	Length			0		2		UInt16	Length of message including this field.
	Message Type	2		1		Byte	Hex		Meaning 	
											0x81	Recovery Request
	Request Level	3	1	UInt8	Defines the level of the request
									Value	Meaning
										0	Instrument 
										1	Group (Segment)
										2	Channel

	Instrument		4	8	UInt64	MMD Instrument identifier if Request Level is 0. Blank if not.
	Group ID		12	6	Alpha	Group/segment ID if Request Level is 1. Blank if not.
	Order Book Type	18	1	UInt8	Only considered if the Request Level is 0. If specified, only data related to the specified order book type is provided, If not data for all available book types for the instrument are provided. 
									Please refer the Additional Field Values section of this document for valid values
	Source Venue	19	2	UInt16	Mandatory field if Request Level is 1. Not considered for other Request Levels.

	Recovery Type	21	1	UInt8	The type of messages to be replayed.
									Value	Meaning
										0	Instrument Directory 
										1	Order book
										2	All Trades
										3	Statistics
										4	Instrument Status
										5	Announcements

	Sequence Number	22	4	UInt32	Only valid if Recovery Type = 2 (Trades) or 5 (Announcements). If specified, the trades or announcements reported with an equal or higher sequence will be sent. 
		Request ID	26	4	UInt32	The value set in this will be echoed back in the corresponding Recovery Response and Recovery Complete. The system will not validate uniqueness of the set value.  

*/

public class RecoveryRequest  implements ITCHApplicationMessage {
	
	private static short LENGTH = 30;
	private static byte TYPE = ITCHMessageTypes.RecoveryRequest_Enum.getKey();
	
	public byte request_level;
	public long instrument_id;
	public byte[] group_id = new byte[6];
	public byte book_type;
	public short source_venue;
	public byte recovery_type;
	public int sequence_num;
	public int request_id;
	
	public short getLength() {
		// TODO Auto-generated method stub
		return LENGTH;
	}

	public byte getType() {
		// TODO Auto-generated method stub
		return ITCHMessageTypes.RecoveryRequest_Enum.getKey();
	}
	


	public void parse(byte[] msgPayload,ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.wrap(msgPayload);
		buffer.order(order);
		buffer.getShort();
		buffer.get();				
		
		request_level = buffer.get();
		instrument_id = buffer.getLong();
		buffer.get(group_id,0,6);
		book_type = buffer.get();
		source_venue = buffer.getShort();
		recovery_type = buffer.get();
		sequence_num = buffer.getInt();
		request_id = buffer.getInt();
		
		
	}

	public byte[] getEncodedBytes(ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		
		buffer.put(request_level);
		buffer.putLong(instrument_id);
		buffer.put(group_id,0,6);
		buffer.put(book_type);
		buffer.putShort(source_venue);
		buffer.put(recovery_type);
		buffer.putInt(sequence_num);
		buffer.putInt(request_id);	
		
		return buffer.array();
	}

}
