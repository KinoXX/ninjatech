package com.borsa.gtp.itch.tracers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.borsa.gtp.itch.messages.ITCHApplicationMessage;
import com.borsa.gtp.itch.messages.ReplayRecoveryStatusCode;



public class ITCHMessageTracerStruct {

	private static final int MAX_TABS = 4;
	private static final int TAB_SIZE = 8;
	private static final long CONSTANT_UPPER_TIME =  System.currentTimeMillis() & 0xFFFFFFFF80000000L;
	private static String TIME_FORMAT = "HH:mm:ss.SSS";	
	private static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(TIME_FORMAT);
	
	static synchronized public String trace(ITCHApplicationMessage message) {
		//System.out.println("Trace called on " + ddmmessage); //DEBUG
		StringBuffer buffer = new StringBuffer();		
		
		buffer.append(class_fmt(message.getClass().getSimpleName(),1)+eol());
		if (message.getClass().getFields().length > 0)
			buffer.append(printFields(message.getClass().getFields(), (ITCHApplicationMessage)message, 2));

		
		//SPECIFIC MBL_SNAPSHOT CLASS Fields
//		if (ddmmessage instanceof DdmMarketByLevel ) {
//			DdmMarketByLevel msg = (DdmMarketByLevel) ddmmessage;							
//			buffer.append("|-----------------------------------------------|"+eol());
//			buffer.append("|NR\t|QTY\t|BID\t|ASK\t|QTY\t|NR\t|"+eol());
//			buffer.append("|-----------------------|-----------------------|"+eol());
//			for (int i = 0; i < msg.levels ; i++) {
//				String bid = msg.buyLevels.length > i ? msg.buyLevels[i].numberOfOrders + "\t" +msg.buyLevels[i].quantity +"\t" + DdmNumberFormatter.trace(msg.buyLevels[i].price) : "\t\t";
//				String ask = msg.sellLevels.length > i ? DdmNumberFormatter.trace(msg.sellLevels[i].price) +"\t"+ msg.sellLevels[i].quantity + "\t" + msg.sellLevels[i].numberOfOrders : "\t\t";
//				buffer.append("|" + bid + "\t|" + ask + "\t|"+eol());
//			}
//			buffer.append("|-----------------------------------------------|"+eol());
//		}
//		//SPECIFIC OB_SNAPSHOT CLASS Fields
//		if (ddmmessage instanceof DdmOrderBookSnapshotPublic ) {
//			DdmOrderBookSnapshotPublic msg = (DdmOrderBookSnapshotPublic) ddmmessage;
//			buffer.append("|-----------------------------------------------|"+eol());
//			buffer.append("|USR\t|QTY\t|BID\t|ASK\t|QTY\t|USR\t|"+eol());
//			buffer.append("|-----------------------|-----------------------|"+eol());			
//			for (int i = 0; i < msg.levels ; i++) {
//				String bid = msg.buyOrders.length > i ? (msg.buyOrders[i].companyCode ==null? "" : msg.buyOrders[i].companyCode.trim()) + "\t" +msg.buyOrders[i].quantity +"\t" + DdmNumberFormatter.trace(msg.buyOrders[i].price) : "\t\t";
//				String ask = msg.sellOrders.length > i ? DdmNumberFormatter.trace(msg.sellOrders[i].price) +"\t"+ msg.sellOrders[i].quantity + "\t" + (msg.sellOrders[i].companyCode ==null? "" : msg.sellOrders[i].companyCode.trim()) : "\t\t";
//				buffer.append("|" + bid + "\t|" + ask + "\t|"+eol());
//			}
//			buffer.append("|-----------------------------------------------|"+eol());
//		}		
		
		return buffer.toString(); 	
	}


//	static public String trace(XMLMessage ddmmessage) {		
//
//		return "  [XMLMessage]"+eol() +
//		"    [Message]\t"+ ddmmessage.getXml_message()+eol() ;
//	}	

	static private String printFields(Field[] fields, ITCHApplicationMessage message, int indent_value) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < fields.length; i++) {
			try {
				//ARRAYS
				if (fields[i].getType().isArray() && fields[i].get(message)!=null) {					
					if (fields[i].getType().getComponentType().equals(byte.class))
					{						
						buffer.append(field_value_fmt(fields[i].getName(),message,fields[i].get(message), indent_value) + eol());
					}
					else
					{
						buffer.append(field_value_fmt(fields[i].getName()+"Len", message,Array.getLength(fields[i].get(message)),indent_value)+eol());					
						for (int j = 0; j< Array.getLength(fields[i].get(message));j++) {							
	
							if (Array.get(fields[i].get(message),j) instanceof ITCHApplicationMessage) {
								buffer.append(class_fmt(fields[i].getName()+"#"+j+ " :" +Array.get(fields[i].get(message),j).getClass().getSimpleName(),indent_value)+eol());
								buffer.append(printFields(Array.get(fields[i].get(message),j).getClass().getFields(), (ITCHApplicationMessage)Array.get(fields[i].get(message),j), indent_value+1));
							}
							else							
								buffer.append(field_value_fmt(fields[i].getName()+"#"+j,message, Array.get(fields[i].get(message),j), indent_value) + eol());
						}
					}
				}
				
				
				//MESSAGE IF CLASS (recursive trace) (remove static types)
				else if ( fields[i].get(message) instanceof ITCHApplicationMessage && !Modifier.isStatic(fields[i].getModifiers()) ) {					
					buffer.append(class_fmt(fields[i].getName()+ " :" +fields[i].get(message).getClass().getSimpleName(),indent_value)+eol());
					if (fields[i].get(message).getClass().getDeclaredFields().length > 0)
						buffer.append(printFields(fields[i].get(message).getClass().getDeclaredFields(), (ITCHApplicationMessage)fields[i].get(message), indent_value+1));

				//EXCEPTIONS ON MESSAGEIF
//					if (fields[i].get(message) instanceof DdmNumber) {
//						DdmNumber num = (message)fields[i].get(message);						
//						buffer.append(field_value_fmt("+STRING_FORMAT",message,DdmNumberFormatter.trace(num), indent_value+1) + eol());
//					}	
//					if (fields[i].get(message) instanceof DdmFlexibleNumber) {
//						DdmFlexibleNumber num = (DdmFlexibleNumber)fields[i].get(message);						
//						buffer.append(field_value_fmt("+STRING_FORMAT",message,DdmNumberFormatter.trace(num), indent_value+1) + eol());						
//					}	
				}
				
				//GENERIC CLASS (remove static types)
				else if (!Modifier.isStatic(fields[i].getModifiers())){
					buffer.append(field_value_fmt(fields[i].getName(),message,fields[i].get(message), indent_value) + eol());
				}
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return buffer.toString();
	}
	
	static private String indent(int spaces) {
		String indent = "";
		for (int i = 0; i < spaces; i++) indent+="  ";
		return indent;
	}
	

	static private String class_fmt(String class_name, int indent_len) {
		return indent(indent_len)+"·[" + class_name + "]";
	}
	
	static private String field_value_fmt(String field, ITCHApplicationMessage msg, Object value ,int indent_len) {		
		
		StringBuffer buffer = new StringBuffer();
		String transcodedValue = null;
		//Add class to buffer
		int tabs = MAX_TABS - ((indent(indent_len).length())/*+ 1*/ + (field.length()) /*+ 3*/)/TAB_SIZE;
		
		String tabs_to_append = "";
		
		for (int i = 0; i< tabs; i++) 
			tabs_to_append +="\t";
		
		buffer.append(indent(indent_len)/*+"·"*/ +field +/*" = "+ */tabs_to_append);

		//Add field to buffer
		if (value != null){
//			String value_type = value.getClass().getSimpleName();	
			String value_string = null;
			if (value instanceof byte[])
			{
				value_string = new String((byte[])value);
			}

			else
			{
				value_string = value.toString();
			}

//			if (value instanceof Integer){
//				value_type = "int";
//			}
//			if (value instanceof Long){
//				value_type = "long";
//			}
//			if (value instanceof Short){
//				value_type = "Short";
//			}
//			if (value instanceof Double){
//				value_type = "double";
//			}
//			if (value instanceof Float){
//				value_type = "float";
//			}
//			if (value instanceof Boolean){
//				value_type = "boolean";
//			}
//			if (value instanceof Character){
//				value_type = "char";
//			}
//			if (value instanceof Byte){
//				value_type = "byte";
//			}
			
			buffer.append("<" +value_string + ">"/* :" + value_type*/);
			

			//APPEND Idem orders quadword
//			if (field.equals("orderId") ||
//					field.equals("previousOrderId")) 
//			{		
//				
//				buffer.append(" (IDEM: " + longToHex((Long)value)+")");
//				buffer.append(" (LSE: " + Long.toString((Long)value,36).toUpperCase() +")");
//				buffer.append(" (MIT: " + 'O' + Base62.encode((Long)value) +")");
//			}
//			
			 Method methlist[] = msg.getClass().getDeclaredMethods();
			 
			 for (int i = 0; i < methlist.length; i++) 
			 {
				 Method m = methlist[i];
				 if (m.getName().compareTo("toString")==0)
				 {
					 Class pvec[] = m.getParameterTypes();
					 
					 if (pvec.length == 2 && 
							 pvec[0].getSimpleName().compareTo("String") ==0 &&
							 pvec[1].getSimpleName().compareTo("Object") ==0)
					 {
						 Object arglist[] = new Object[2];
				         arglist[0] = field;
				         arglist[1] = value;
						 try {					            
							 transcodedValue = (String)m.invoke(msg, arglist);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				 }
			 }

			 
			//APPEND Order flow codes
			if (field.equals("status"))
				buffer.append(" (" + ReplayRecoveryStatusCode.fromValue((Byte)value)+")");
						
			if (transcodedValue !=null && transcodedValue.compareTo(value_string)!=0)
			{
				buffer.append(" (" + transcodedValue +")");
			}
			
//			//APPEND Idem series status and trading status
//			if (field.equals("seriesStatus"))
//				buffer.append(" (" + DdmIdemSeriesStatusType.Get.getString((Byte)value)+")");
//			if (field.equals("tradingState"))
//				buffer.append(" (" + DdmIdemTradingStateType.Get.getString((Integer)value)+")");
//			if (field.matches("ts#\\d") && (Integer)value !=0) {
//				Date time = new Date((Integer)value +  CONSTANT_UPPER_TIME);				
//				buffer.append(" (" + sdf.format(time)+")");
//			}
		}
		else
			buffer.append("<" +"NULL" + ">");
		
		return buffer.toString();
		
	}
		
	
	static private String eol() {
		return "\n";
	}

	static private String longToHex(long value) {
		int lo = (int) (value);
		int hi = (int) (value >> 32);
		return Integer.toHexString(hi).toUpperCase()+":"+Integer.toHexString(lo).toUpperCase();
	}

}
