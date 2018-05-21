package com.borsa.gtp.itch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ITCHIOException extends IOException{
	  private static final long serialVersionUID = 1L;
		
	  private String message = null;

	  public ITCHIOException(String reason, Class callingclass) {

	    StringBuffer sbTrace;
	    StringWriter sw = new StringWriter();
	    printStackTrace(new PrintWriter(sw));

	    sbTrace = sw.getBuffer();

	    BufferedReader br = new BufferedReader(new StringReader(new String(
	        sbTrace)));

	    String className = callingclass.toString();
	    className = className.substring(className.indexOf("class") +
	                                    "class".length());

	    Pattern pattern = Pattern.compile(className);
	    Matcher matcher = pattern.matcher("");

	    String code_line = null;
	    try {
	      while ( (code_line = br.readLine()) != null) {
	        matcher.reset(code_line);
	        if (matcher.find()) {
	          break;
	        }
	      }
	      message = "StackTrace: "+ code_line + "\n" +
	      			"Reason: " + reason + "\n";

	    }
	    catch (IOException ex1) {
	      ex1.printStackTrace();
	    }
	  }

	  public String getMessage() {
	    return message;
	  }
}
