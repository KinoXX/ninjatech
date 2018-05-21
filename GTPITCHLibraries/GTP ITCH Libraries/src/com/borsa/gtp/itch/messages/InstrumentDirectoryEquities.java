package com.borsa.gtp.itch.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.borsa.gtp.itch.messages.ITCHMessageFactory.ITCHMessageTypes;

/*
8.6.2	Instrument Directory - Equities
Field	Offset	Length	Type	Description
Length	0	2	UInt16	Length of message including this field.
Message Type	2	1	Byte	Hex	Meaning
0x52	Instrument Directory – Equities 

Timestamp	3	8	UDT	Time the message was generated
Instrument	11	8	UInt64	MMD Instrument identifier.
ISIN	19	12	Alpha	ISIN code of the instrument
SEDOL	31	8	Alpha	SEDOL code of the instrument
Allowed Book Types	39	1	Bit Field	Defines the order-book types that are allowed for the instrument. Each designated bit represents a book type. 0 Means not allowed and 1 means allowed. 
Bit	Name
0	Reserved
1	Firm Quote
2 	Off-book
3	Electronic

Source Venue	40	2	UInt16	 Venue from which market data is received for the instrument. Restricted to one currently.
Please refer the Additional Field Values section for valid values. 
Venue Instrument ID	42	11	Alpha	Instrument identifier used by the source venue
Group ID	53	6	Alpha	Segment or instrument group ID as identified by the trading market
Currency	59	3	Alpha	The possible values will be the ISO 4217 codes for currency. However the following additional values too can be disseminated.
Value	Meaning
GBX	GB Pennies
ZAC	100th of RAND (cents) 
ITL	Italian LIRA

Tick ID	62	2	Alpha	The tick structure applicable for the instrument
Previous Day’s Closing Price	64	8	Price	Closing price reported for the previous trading day
Price Band Tolerances (%)	72	8	Price	Price Band Tolerance (%) of the instrument
Dynamic Circuit Breaker Tolerances (%)	80	8	Price	Dynamic Circuit Breaker Tolerance (%) of the instrument
Static Circuit Breaker Tolerances (%)	88	8	Price	Static Circuit Breaker Tolerance (%) of the instrument
Flags	96	1	Bit Field	Bit	Name	Meaning
0		
<Define if any flags that are required>
Security Subtype	97	1	UInt8	Different instrument security subset types.
Please refer the Additional Field Values section of this document for valid values
Expiration Date	98	8	Date	Expiration date 
Listing Start Date	106	8	Date	Listing start date 
Listing End Date	114	8	Date	Listing end date 
Minimum Lot	122	8	Size	Indicates the minimum quantity/nominal value tradable on the market for a security.
Last Price In Preceding Session	130	8	Price	Last trade's execution price in a session prior to the current day's session
Last Price In Preceding Session Date	138	8	Date	Last trade's execution day 
Settlement System	146	1	UInt8	Settlement system type.
Value	Meaning
1	RRG
2	Express I
3	Express II
4	Clear stream
5	Undefined value

Last Validity Date	147	8	Date	Last validity date in the DDMMYYYY format
Settlement Date	155	8	Date	Settlement date in the DDMMYYYY format
Ex Marker Code	163	2	Alpha	The value of an Ex-Marker pertaining to a tradable instrument.
Security Type	165	1	UInt8	Type of security
Please refer the Additional Field Values section of this document for valid values
Country Of Register	166	3	Alpha	Country of Register
Exchange Market Size 	169	8	Size	The Exchange Market Size (EMS) is set to show the minimum size a market maker must quote in an individual security for all executable and non executable quotes.
Minimum Peak Size Multiplier	177	8	Size	Used to specify the minimum size of an iceberg peak for an instrument in conjunction with EMS.
Security Maximum Spread	185	8	Price	This field informs Participants of the maximum spread allowable for an instrument when submitting quote messages, calculated as a percentage of mid-price.
Clearing Type	193	1	UInt8	Indicates the settlement mode of the security.
Value	Meaning
0	Not Cleared
1	Cleared

Strike Price	194	8	Price	Strike Price (exercise price for warrants)
Venue Underlying  ID	202	11	Alpha	Venue specified instrument ID of the underlying
Underlying ISIN Code	213	12	Alpha	Underlying ISIN code
Underlying Type	225	1	UInt8	Underlying Type
Value	Meaning
0	Underlying type is not codified in basic data
1	Share
2	Foreign currency
3	Indices
4	Commodity
5	Foreign Indices
6	Future
7	Foreign Share
8	Basket
9	Exchange Rate

Number Of Shares In Circulation	226	8	UInt64	Indicates the number of shares which constitute the share capital. A value is set for shares only.
Leverage Certificates Barrier	234	8	Size	Leverage Certificates Barrier
Option Style	242	1	Byte	Instrument’s option style
Value	Meaning
E	European option style
A	American option style
P	Periodic option style

Parity	243	8	Size	The parity of the instrument
Static Reference Price	251	8	Price	Reference Price as reported by the source venue.
Dynamic Reference Price	259	8	Price	Reference Price as reported by the source venue.


*/

public class InstrumentDirectoryEquities  implements ITCHApplicationMessage {
	
	private static short LENGTH = 267;
	private static byte TYPE = ITCHMessageTypes.InstrumentDirectoryEquities_Enum.getKey();
	
	public long timestamp;
	public long instrument_id;
	public byte[] isin = new byte[12];
	public byte[] sedol = new byte[8];
	public byte allowed_book_types;
	public short source_venue;

	
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
		
		
		timestamp = buffer.getLong();
		instrument_id = buffer.getLong();
		buffer.get(isin);
		buffer.get(sedol);
		allowed_book_types = buffer.get();
		source_venue = buffer.getShort();
		byte[] tmp = new byte[225];
		buffer.get(tmp);
		
		
		
	}

	public byte[] getEncodedBytes(ByteOrder order) {
		
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
		buffer.order(order);
		buffer.putShort(LENGTH);
		buffer.put(TYPE);
		
		buffer.putLong(timestamp);
		buffer.putLong(instrument_id);
		buffer.put(isin);
		buffer.put(sedol);
		buffer.put(allowed_book_types);
		buffer.putShort(source_venue);
		
		byte[] tmp = new byte[225];
		buffer.put(tmp);
		
		
		return buffer.array();
	}



}
