package org.soc.common.gaeBlobstore;

import java.util.Date;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ChannelAPI {
	
	public static String getChannelKey(String shopId){
		String result = shopId +"_"+ (new Date().getTime());
		if(result.length()>62)
			result = result.substring(0,62);
		return result;
	}
	
	public static String getToken(String channelKey){
		ChannelService channelService = ChannelServiceFactory.getChannelService();
	    return channelService.createChannel(channelKey);
	}
	
	public static void sendMessage(String channelKey,String message) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		channelService.sendMessage(new ChannelMessage(channelKey,message));  
	}
}
