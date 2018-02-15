import java.io.IOException;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class GetSIQuotesMain {

	public static void main(String[] args) {

		try {
		
			String login_url = "https://quotes.tradecho.com/users/sign_in";
		    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36";
		    
		    Connection.Response welcome_res = Jsoup.connect(login_url).method(Method.GET).execute();
	        Document welcomePage = welcome_res.parse();
	        Map<String, String>  cookies = welcome_res.cookies();
	        
		    Elements metaTags = welcomePage.getElementsByTag("meta");
		    
		    String securityTokenKey = null;
		    String securityTokenValue = null;
		    
		    //Get Authenticity Tokens
		    for (Element metaTag : metaTags) {
		    	String TokenKey = metaTag.attr("name");
		    	String TokenValue = metaTag.attr("content");
		        if (TokenKey.compareTo("csrf-param") == 0)
		        	securityTokenKey = TokenValue;
		        if (TokenKey.compareTo("csrf-token") == 0)
		        	securityTokenValue = TokenValue;
		    }
		    
		    //Submit Login with Authenticity Token
		    Connection.Response login_response = Jsoup.connect(login_url)
		            .data("user[email]", "marco.romano@borsaitaliana.it", 
		            		"user[password]", "QU:8V?$J/", 
		            		"user[remember_me]", "0", 
		            		"commit", "Log in")
		            .data(securityTokenKey, securityTokenValue)
		            .cookies(cookies)
		            .method(Method.POST)
		            .userAgent(userAgent)
		            .execute();

		    
		    //Get login cookies
		    cookies = login_response.cookies();
		     

		    //Scan page after login
		    Connection.Response mainpage_response = Jsoup.connect("https://quotes.tradecho.com/")
		    		  .method(Method.GET)
			          .cookies(cookies)
			          .referrer("https://quotes.tradecho.com/users/sign_in")
			          .followRedirects(true)
			          .userAgent(userAgent)
		    		  .execute();
		    
		    cookies = mainpage_response.cookies();
		    
		    //Get quotes for members
		    Connection.Response member_quotes_response = Jsoup.connect("https://quotes.tradecho.com/quotes_lists")
		    		.data("member", "HANDSESS")          
                    .method(Method.GET)
                    .cookies(cookies)
                    .referrer("https://quotes.tradecho.com")
			        .followRedirects(true)
			        .userAgent(userAgent)
			        .execute();
		    
		    cookies = member_quotes_response.cookies();
		    
		    System.out.println(member_quotes_response.parse());
		    

		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

}
