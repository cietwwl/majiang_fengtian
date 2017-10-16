package com.zxz.utils;

import org.json.JSONObject;

import com.zxz.service.ServiceMaster;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class SessionMessage {


	public void setSessionMessage(ChannelHandlerContext ctx, Object message){
		StringBuffer mess = ctx.channel().attr(AttributeKey.<StringBuffer>valueOf("OtherMesage")).get();
		if(mess == null){
			mess = new StringBuffer(message.toString());
			ctx.channel().attr(AttributeKey.<StringBuffer>valueOf("OtherMesage")).set(mess);
			otherCheck(ctx, mess);
		}else{
			mess.append(message.toString());
			otherCheck(ctx, mess);
		}
	}
	
	public void otherCheck(ChannelHandlerContext ctx,StringBuffer mess){
		String str = mess.toString();
		int begin = -1;
		int end = 0;
		for(int i=0;i<str.length();i++){
			char charAt = str.charAt(i);
			if(charAt=='{'){
				begin = i;
				continue;
			}else if(charAt=='}'){
				end = i;
				if(begin>=0&&begin<end){
					String jsonString = str.substring(begin, end+1);
					JSONObject jsonObject = new JSONObject(jsonString);
					try {
						new ServiceMaster().serviceStart(ctx, jsonObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mess = new StringBuffer(mess.substring(end+1, mess.length()));
					ctx.channel().attr(AttributeKey.<StringBuffer>valueOf("OtherMesage"));
				}
			}
		}
		
	}
	
}
