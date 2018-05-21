package com.borsa.gtp.itch;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;


import com.borsa.gtp.itch.messages.ITCHApplicationMessage;


public class ITCHMessageArray {

	private MessageHeader header;
	private ArrayList<ITCHApplicationMessage> body;

	public ITCHMessageArray ()
	{
		header = new MessageHeader();
		body = new ArrayList<ITCHApplicationMessage>();
	}
	
//	public class MessageBody {
//
//		public void parse(byte[] msgPayload) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		public byte[] getEncodedBytes() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//	}

	public class MessageHeader {

		public static final int LENGTH = 8;
		

		public short getmMessageLength() {
			return mMessageLength;
		}


		public int getmSeqNumber() {
			return mSeqNumber;
		}

		public void setmSeqNumber(int mSeqNumber) {
			this.mSeqNumber = mSeqNumber;
		}

		public byte getmMsgCount() {
			return mMsgCount;
		}

		public void setmMsgCount(byte mMsgCount) {
			this.mMsgCount = mMsgCount;
		}

		public byte getmMktDataGroup() {
			return mMktDataGroup;
		}

		public void setmMktDataGroup(byte mMktDataGroup) {
			this.mMktDataGroup = mMktDataGroup;
		}

		private short mMessageLength = MessageHeader.LENGTH;
		private int mSeqNumber = 0;
		private byte  mMsgCount = 0;
		private byte mMktDataGroup  = 0;
		
		
		public short getMMessageLength() {
			// TODO Auto-generated method stub
			return mMessageLength;
		}

		public void parse(byte[] itchHeader,ByteOrder order) {
			ByteBuffer buffer = ByteBuffer.wrap(itchHeader);
			buffer.order(order);
			mMessageLength = buffer.getShort();
			mMsgCount = buffer.get();
			mMktDataGroup = buffer.get();
			mSeqNumber = buffer.getInt();

		}
		
		public String toString() {
			return new StringBuffer()
			.append(mMessageLength)
			.append(mMsgCount)
			.append(mMktDataGroup)
			.append(mSeqNumber).toString();
		}
		
		public byte[] getEncodedBytes(ByteOrder order)
		{			
			ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
			buffer.order(order);
			buffer.putShort(mMessageLength);
			buffer.put(mMsgCount);
			buffer.put(mMktDataGroup);
			buffer.putInt(mSeqNumber);
			
			return buffer.array();
		}

	}

	public void addPayloadMessage(ITCHApplicationMessage pMsg)
	{
		body.add(pMsg);
		header.mMessageLength += pMsg.getLength();
		header.mMsgCount +=1;
	}
	
	public ITCHApplicationMessage[] getPayload() {
		// TODO Auto-generated method stub
		ITCHApplicationMessage[] msgs = new ITCHApplicationMessage[header.mMsgCount];
		return body.toArray(msgs);
	}

	public MessageHeader getHeader() {
		// TODO Auto-generated method stub
		return header;
	}

}
