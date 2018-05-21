package com.borsa.gtp.itch.connection;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.logging.Logger;


import com.borsa.gtp.itch.ITCHIOException;
import com.borsa.gtp.itch.ITCHMessageArray;
import com.borsa.gtp.itch.UnicastItchContext;
import com.borsa.gtp.itch.messages.ITCHApplicationMessage;
import com.borsa.gtp.itch.messages.ITCHMessageFactory;
import com.borsa.utils.SemaphoreUtil;



public class ITCHTCPTransport {

	private Socket socket;
	private DataOutputStream os;
	//private DataInputStream is;
	private BufferedInputStream is;
	private static Logger logger = Logger.getLogger("bitsystems.ddmplus.emapi.DDMContext");

	private static SemaphoreUtil socket_read_semaphore = new SemaphoreUtil(1);

	private UnicastItchContext itchContext;
	private boolean active = false;
	private long uncompressed_bytes_count;
	private long prev_compressed_bytes_count;
	private long prev_compressed_in;

	private static short PRE_MSG_HEADER_LEN = 3;
	public ITCHTCPTransport(UnicastItchContext context) {
		this.itchContext = context;
	}

	public void createSocket(String TCPaddress, int mPort) throws
	ITCHIOException {
		try {
			socket = new Socket(TCPaddress, mPort);
			is = new BufferedInputStream(socket.getInputStream());  
			os = new DataOutputStream(socket.getOutputStream());      			
//			socket.setSoTimeout(5000);
			//For Nagel's algorithm
			socket.setTcpNoDelay(true);

		}
		catch (IOException ex) {
			logger.fine("TcpTransport::Open Fail 0\n");
			System.err.println("Connecting to:" + TCPaddress + ":" +mPort );
			
			throw new ITCHIOException("createSocket() failed",
					this.getClass());
		}

		System.out.println("Connected to:" + TCPaddress + ":" +mPort );
		this.active  = true;
	}

	public void destroySocket() {	
		try {
			//Chiusura dello Stream e del Socket
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			if (socket != null) {				
				socket.close();
			}     
		}
		catch (IOException ioe) {
			System.err.println("ERROR closing streams: " + ioe.getMessage());      
		}
				
		this.active = false;
	}

	public void receive() throws ITCHIOException {

		ITCHMessageArray messageArray = null;

		byte[] itchHeader = new byte[ITCHMessageArray.MessageHeader.LENGTH];

		int nextLength = ITCHMessageArray.MessageHeader.LENGTH;

		if (is != null) {
			itchHeader = ReadFully(is, nextLength);
		}

		try { socket_read_semaphore.acquire(); } catch (InterruptedException ex2) { ex2.printStackTrace();}

		messageArray = new ITCHMessageArray();
		ITCHMessageArray.MessageHeader messageHeader = messageArray.getHeader();
		ArrayList<ITCHApplicationMessage> messageBody = new ArrayList<ITCHApplicationMessage>();

		messageHeader.parse(itchHeader, ByteOrder.LITTLE_ENDIAN);

		// gestisco il contenuto del messaggio
		if ( (nextLength = messageHeader.getMMessageLength()) > 0) {  
			for (byte msg_id = 0; msg_id < messageHeader.getmMsgCount(); msg_id++)
			{
				byte[] sub_msg_length_and_type_bytes = ReadFully(is,PRE_MSG_HEADER_LEN);
				ByteBuffer buffer = ByteBuffer.wrap(sub_msg_length_and_type_bytes);
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				
				short sub_msg_length= (short) (buffer.getShort() - PRE_MSG_HEADER_LEN);
				byte sub_msg_type = buffer.get();
				byte[] msgPayload = new byte[sub_msg_length];				
				msgPayload = ReadFully(is, sub_msg_length);
				
				buffer = ByteBuffer.allocate(sub_msg_length + PRE_MSG_HEADER_LEN);
				buffer.put(sub_msg_length_and_type_bytes);
				buffer.put(msgPayload);
				
				ITCHApplicationMessage message = ITCHMessageFactory.getParsedMessage(buffer.array(),sub_msg_type);
				if (message!=null)
					itchContext.addMessage(message);
			}
		}	
		
		socket_read_semaphore.release();		
	}

	public void send(ITCHMessageArray message) throws ITCHIOException {

		try {
			if (os == null) {
				throw new ITCHIOException("Transport Connection send() failed",
						itchContext.getClass());
			}
			
//			byte[] ab = new byte[( message.getHeader().toString().getBytes().length + message.getBody().getEncodedBytes().length)];
//			System.arraycopy(message.getHeader().toString().getBytes(), 0, ab, 0,message.getHeader().toString().getBytes().length);
//			System.arraycopy(message.getBody().getEncodedBytes(), 0, ab, message.getHeader().toString().getBytes().length,message.getBody().getEncodedBytes().length);

			
			os.write(message.getHeader().getEncodedBytes(ByteOrder.LITTLE_ENDIAN));
			for (ITCHApplicationMessage iterable_element : message.getPayload()) {
				os.write(iterable_element.getEncodedBytes(ByteOrder.LITTLE_ENDIAN));
			
			}

//			os.write(ab);
			
//			uncompressed_bytes_count += message.getHeader().toString().getBytes().length 
//							+ message.getPayload().getEncodedBytes().length;
		}
		catch (IOException ex) {
			throw new ITCHIOException("Transport Connection send() failed",
					itchContext.getClass());
		}
	}

	public boolean isActive() {
		return active;
	}
	
	
	public long getUncompressedBytesCount() {
		return uncompressed_bytes_count;
	}
	
	
	public void resetBytesCount() {

		uncompressed_bytes_count=0;
	}
	
	
	byte[] ReadFully(BufferedInputStream stream, int numBytes) throws ITCHIOException
	{
	  byte[] ret = new byte[numBytes];
	  int offset = 0;
	  
	  while (numBytes > 0)
	  {
	    int received=0;
		try {
			received = stream.read(ret, offset, numBytes);
			if (received == -1) 
				throw new IOException();
		    offset += received;
		    numBytes -= received;
		}
		catch (IOException e) {
			socket_read_semaphore.release();
			throw new ITCHIOException("receive() failed. Error on input stream. Reading: [" + new String(ret,0, offset )+"]",this.getClass());
		}
		catch (IndexOutOfBoundsException e) {
			socket_read_semaphore.release();
			throw new ITCHIOException("receive() failed. Unexpected Size ["+numBytes+"]",	this.getClass());
		}
	  }
	  
	  
	  uncompressed_bytes_count += ret.length;
	  return ret;
	}
	
}