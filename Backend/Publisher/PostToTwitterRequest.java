package Publisher;

import java.util.concurrent.Callable;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class PostToTwitterRequest implements Callable<String> {
	
	private String text;
	
	public PostToTwitterRequest(String text) {
		this.text = text;
	}

	public String call() throws TwitterException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("P3wqXcdRlGmiLPfavPmFTQGfp")
		  .setOAuthConsumerSecret("OFSYf4OqzuwwPYK30Lx3LYrm6N94oLrRbkteO0Q4EmKz7eSHz4")
		  .setOAuthAccessToken("3252572254-gLd2JHxWmoeO9kEBFPIrSScXoS1wNRXZSQRLuhz")
		  .setOAuthAccessTokenSecret("BWY0cZH5kQeQnTlESigjbI4FKVdyqf6DyIabySWQOxSn4");
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
	    Status status = twitter.updateStatus(text);
	    String result = "Successfully updated the status to [" + status.getText() + "].";
		return result;
	}
	
}
