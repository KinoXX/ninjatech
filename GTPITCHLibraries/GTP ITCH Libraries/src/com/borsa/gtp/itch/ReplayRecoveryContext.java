package com.borsa.gtp.itch;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.logging.Level;


import com.borsa.gtp.itch.connection.ITCHTCPTransport;
import com.borsa.gtp.itch.messages.ITCHApplicationMessage;
import com.borsa.gtp.itch.messages.LoginRequest;
import com.borsa.gtp.itch.messages.LoginRequestTRM2ITCH;
import com.borsa.gtp.itch.messages.LoginResponse;
import com.borsa.gtp.itch.messages.RecoveryRequest;
import com.borsa.gtp.itch.messages.RecoveryResponse;
import com.borsa.gtp.itch.messages.RecoveryTypeCode;
import com.borsa.gtp.itch.messages.ReplayRecoveryStatusCode;
import com.borsa.gtp.itch.messages.ReplayRequest;
import com.borsa.gtp.itch.messages.RequestLevelCode;
import com.borsa.gtp.itch.tracers.ITCHMessageTracerStruct;


public class ReplayRecoveryContext implements UnicastItchContext {


	ITCHTCPTransport mTransport;
	private boolean dead;
	private boolean sleeping;
	private boolean preloggedin;
	private boolean connected;
	private int m_reqId;
	private int mIpport;
	private String mIphost;
	private String mUsername;
	private String mPassword;
	
	private ITCHMessagesDispatcher dispatcher_queue;
	private boolean catch_ioexecption_onreceive;
	private String mCtxName;
	private byte mMktDataGroup;

	
	public ReplayRecoveryContext(byte dataGroup)
	{
		setName("ITCHIContext"+getName());
		
		mMktDataGroup =  dataGroup;
		dispatcher_queue = new ITCHMessagesDispatcher();
		mTransport = new ITCHTCPTransport(this);
		
		dispatcher_queue.start();
		Thread thread = new Thread(this);
		thread.start();  
	}
	

	private void setName(String string) {
		
		mCtxName = string;
	}


	public void connect(String username, String hostIp, int port) 
	{
		mIphost = hostIp;
		mIpport = port;
		mUsername = username;
		
		sleeping = false;
		//notify();
	}
	
	
	public void connect(String username, String password, String hostIp, int port) 
	{
		mIphost = hostIp;
		mIpport = port;
		mUsername = username;
		mPassword = password;
		
		sleeping = false;
		//notify();
	}

	public void addMessage(ITCHApplicationMessage message) {
		dispatcher_queue.push(message);

	}



	public void run() {
		while (!dead) {
			while(!sleeping) {
				try {
					if (mTransport.isActive()) {						
						mTransport.receive();
					}
					else if (!connected) {						
						logon(mIphost,mIpport);						
					} 
				}
				catch (ITCHIOException ex) {
					if (catch_ioexecption_onreceive) {						
						ctx_exit();
//						logger.fine(ex.getMessage());
//						FireListenerOnError(ex.getMessage() + "Local queue size is:" + dispatcher_queue.getSize());

					}
					System.out.println(ex.getMessage());
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		

	}
	
	private void logon(String host, int port) {

		try {			
			//if (logger.getLevel() == Level.FINEST)	logger.finest("[LOGIN PHASE] Connecting to " + host + ":"+ port);
			mTransport.createSocket(host, port);
			LoginRequestTRM2ITCH logon = new LoginRequestTRM2ITCH();
			logon.username = mUsername.getBytes();
			logon.password = mPassword.getBytes();
			//logon.password = mPassword;			
			send_request(logon,  0);

			//turn on main catch exception on main cycle receive  
			catch_ioexecption_onreceive = true;
		}
		catch (ITCHIOException ex) {
			ex.printStackTrace();
			System.exit(1);
			//ctx_exit();
			//FireListenerOnError(ex.getMessage());
		}

	}

	private void send_request(ITCHApplicationMessage pMsg, int i) {

		
		ITCHMessageArray message = new ITCHMessageArray();

		message.getHeader().setmMktDataGroup(mMktDataGroup);
		message.getHeader().setmSeqNumber(++m_reqId);
		//message.getHeader().setmMessageLength((short)(ITCHMessageArray.MessageHeader.LENGTH + pMsg.getLength()));

		message.addPayloadMessage(pMsg);
		
		System.out.println(ITCHMessageTracerStruct.trace(pMsg));
		
		/*LOG*/
//		if (logger.getLevel() == Level.FINEST)	logger.finest(message.getHeader().dump());						
//		else logger.fine("[Sending: " + message.getHeader().toString() + "] (" +messageif.getClass().getSimpleName()+")");						
//		logger.finer(DDMMessageTracerPlain.trace(message.getBody().getDecodedMessage()));
		
		try {
			mTransport.send(message);
		} catch (ITCHIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		this.pending_opID.put(new Integer(reqId),new Character(EMAPIMessage.DDMMessageHeader.DDM_MSGTYPE_RQST));
		
//		EMAPIEvent evt = new EMAPIEvent( message , this );			
//		FireListenerOnMessageSend(evt);
		
		
	}
	
	protected synchronized void runcallback(ITCHApplicationMessage message){

		
//		EMAPIEvent evt = new EMAPIEvent( message, this);
//		MessageIf ddmmessage = (MessageIf)evt.getMessage();		


		/*LOG*/
//		if (logger.getLevel() == Level.FINEST)	logger.finest(message.getHeader().dump());						
//		else logger.fine("[Received: " + message.getHeader().toString() + "] (" + ddmmessage.getClass().getSimpleName()+")");													
//		logger.finer(DDMMessageTracerPlain.trace(ddmmessage));
		System.out.println(ITCHMessageTracerStruct.trace(message));

//		if (heartbeat_timer !=null) heartbeat_timer.setGot_reply();
//
//		FireListenerOnMessageReceive(evt);
//
//
//		short operation = message.getHeader().getMRequestId();
//
//		if (pending_opID.containsKey(new Integer(operation))) {
//			if (message.getHeader().getMMessageType() == EMAPIMessage.DDMMessageHeader.DDM_MSGTYPE_RQST)
//				pending_opID.remove(new Integer(operation));
//		}		
//
//
		if (message instanceof LoginResponse) {
	
			LoginResponse login_message = (LoginResponse) message;
			if (login_message.status == ReplayRecoveryStatusCode.RequestedAccepted.getKey())
				manage_login_answer(message);
				
		}
//			
//
//		if (ddmmessage instanceof DdmLoginRsp) {			
//			DdmLoginRsp msg = (DdmLoginRsp)ddmmessage;			
//			if (msg.loginStatus == 0) {
//				this.connected = true;
//				if (msg.heartbeatInterval >0) {
//					heartbeat_timer = new EMAPIHeartbeatTimer(this, msg.heartbeatInterval * 1000, msg.maxLostHeartbeat);
//					heartbeat_timer.start();
//				}
//			}
//			else 
//				ctx_exit();
//
//		}
//
//		if (ddmmessage instanceof DdmHeartbeatRsp) {						
//			//heartbeat_timer.setGot_reply();
//		}
//
//		if (ddmmessage instanceof DdmAddSnapshotSubscriptionRsp) {
//
//		}
//

//		if (ddmmessage instanceof DdmResponse && 
//				operation == logout_reqId) {			
//			DdmResponse msg = (DdmResponse)ddmmessage;
//
//			switch (msg.status.code) {
//			case 0:
//			case 1:					
//				ctx_exit();
//				break;
//			}
//
//		}
//
//		if (ddmmessage instanceof DdmForcedLogout) {			
//			FireListenerOnError("Forced Logout");
//			ctx_exit();
//		}
	}
	
	private void manage_login_answer(ITCHApplicationMessage message) 
	{

		
//		RecoveryRequest request = new RecoveryRequest();
//		
//		request.book_type = (byte)0;
//		request.group_id = "      ".getBytes();
//		request.request_level = RequestLevelCode.Channel.getKey();
//		request.recovery_type = RecoveryTypeCode.InstrumentDirectory.getKey();
//		request.sequence_num = 0;
//		request.request_id = 1;
//		request.instrument_id = 0L;
//		request.source_venue = 2;
//		
//		//logon.password = mPassword;			
//		send_request(request,  0);
		
		

		ReplayRequest request = new ReplayRequest();
			
		request.first_request = 235000;
		request.count = 2000;
		request.request_id = 1;
	
			
		//logon.password = mPassword;			
		send_request(request,  0);
		
	}


	public void ctx_exit() {

		catch_ioexecption_onreceive = false;

		connected = false;

		if (mTransport != null) {		
			mTransport.destroySocket();
		}
						
		if (!sleeping) sleeping = true;
		if (!dead) dead = true;

		//notify the dispatcher that this context is dead
		if (dispatcher_queue!= null){
			dispatcher_queue.exit();
		}
		
		//notify the subscription queue that this context is dead
//		if (subscription_queue != null){
//			subscription_queue.exit();
//		}
	
		//clear listeners list of this Context
		//Let the users remove the listeners, otherwise comodification error in the remove
//		if (!callback_listeners.isEmpty())
//			callback_listeners.clear();

	}
	
	class ITCHMessagesDispatcher extends Thread{
		LinkedList<Object> s = new LinkedList<Object>();
		boolean dispatcher_dead = false;
		ITCHApplicationMessage msg;

		public ITCHMessagesDispatcher() {
			this.setName(ReplayRecoveryContext.this.getName()+"-MsgDispatcher");

		}

		public void run () {			
			while (!dispatcher_dead) {		
				if ( (msg = (ITCHApplicationMessage)pop())!=null) {
					runcallback(msg);
				}
//				if (s.size() < EMAPIContextSubscriptionQueue.QUEUE_LIMIT)
//					subscription_queue.unleashWaitingSubscription();
			}
		}
		public synchronized Object pop() {
			while(s.isEmpty() && !dispatcher_dead) {
				try {					
					wait();
				} catch (InterruptedException e) 
				{e.printStackTrace();
				}
			}
			return(s.poll());
		}

		protected int getSize() {
			return s.size();
		}

		public synchronized void push(Object o) {
			s.add(o);
			notify();
		}

		public synchronized void clear() {
			s.clear();
		}

		public synchronized void exit() {
			dispatcher_dead = true;			
			notify();
		}
	}



	public String getName() {
		// TODO Auto-generated method stub
		return mCtxName;
	}



}
