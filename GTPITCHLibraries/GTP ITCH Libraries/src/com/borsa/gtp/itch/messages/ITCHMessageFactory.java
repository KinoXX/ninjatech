package com.borsa.gtp.itch.messages;

import java.nio.ByteOrder;

import com.borsa.gtp.itch.tracers.ITCHMessageTracerStruct;

public class ITCHMessageFactory {

	
	static ByteOrder mByteOrder = ByteOrder.LITTLE_ENDIAN;
	
	public enum ITCHMessageTypes {

		LoginRequest_Enum((byte)0x01),
		LoginResponse_Enum((byte)0x02),
		ReplayRequest_Enum ((byte)0x03),
		ReplayResponse_Enum((byte)0x04),
		RecoveryRequest_Enum((byte)0x81),
		RecoveryResponse_Enum((byte)0x82), 
		ReplayRecoveryComplete_Enum((byte)0x83),
		
		InstrumentDirectoryEquities_Enum((byte)0x52);
		
		
		private byte key;
		
		private ITCHMessageTypes (byte key)
		{
			this.key = key;
		}
		
		public byte getKey() 
		{
		    return key;
		}
		
		public static ITCHMessageTypes fromValue(byte thisKey) {
		    for (ITCHMessageTypes msgType : ITCHMessageTypes.values()) {
		        if (msgType.key == thisKey) return msgType;
		    }
		    throw new IllegalArgumentException();
		}
	}
	

	

	public static ITCHApplicationMessage getParsedMessage(byte[] msgPayload, byte sub_msg_type) 
	{
		ITCHApplicationMessage msg = null;
		try
		{
			switch (ITCHMessageTypes.fromValue(sub_msg_type))
			{
				case LoginRequest_Enum :			
					break;			
				case LoginResponse_Enum :
					msg = new LoginResponse();
					msg.parse(msgPayload,mByteOrder);
					break;
				case RecoveryResponse_Enum :
					msg = new RecoveryResponse();
					msg.parse(msgPayload, mByteOrder);
					break;
				case ReplayResponse_Enum :
					msg = new ReplayResponse();
					msg.parse(msgPayload, mByteOrder);
					break;
				case ReplayRecoveryComplete_Enum :
					msg = new ReplayRecoveryComplete();
					msg.parse(msgPayload, mByteOrder);
					break;
				case InstrumentDirectoryEquities_Enum :
					msg = new InstrumentDirectoryEquities();
					msg.parse(msgPayload, mByteOrder);
					break;
				default :
					break;
			
			}
		}
		catch (IllegalArgumentException ex)
		{
			//System.out.println("Message [" + sub_msg_type + "] of Unknown type");
		}
		return msg;
	}

}
