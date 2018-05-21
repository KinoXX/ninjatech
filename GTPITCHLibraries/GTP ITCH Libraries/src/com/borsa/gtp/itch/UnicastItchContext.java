package com.borsa.gtp.itch;

import com.borsa.gtp.itch.messages.ITCHApplicationMessage;

public interface UnicastItchContext extends Runnable {


	void addMessage(ITCHApplicationMessage message);
	void connect(String username, String password, String hostIp, int port);
	String getName();

}
