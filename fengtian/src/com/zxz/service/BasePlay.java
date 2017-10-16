package com.zxz.service;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

public abstract class BasePlay extends BaseService implements PlayOfHongZhong{

	
	SettingService settingService = new SettingService();
	ServerService serverService = new ServerService();
	ScoreService scoreService = new ScoreService();
	MessageService messageService = new MessageService();
	
	
	@Override
	public void getSetting(JSONObject jsonObject, ChannelHandlerContext ctx) {
		settingService.getSetting(jsonObject, ctx);
	}


	@Override
	public void getServerInfo(JSONObject jsonObject, ChannelHandlerContext ctx) {
		serverService.getServerInfo(jsonObject, ctx);
	}
	
	@Override
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext ctx) {
		scoreService.getUserScore(jsonObject,ctx);
	}
	
	/**得到总的战绩
	 * @param jsonObject
	 * @param session
	 */
	public void userLocation(JSONObject jsonObject, ChannelHandlerContext ctx){
		scoreService.userLocation(jsonObject,ctx);
	}
	
	
	@Override
	public void queryLocation(JSONObject jsonObject, ChannelHandlerContext ctx) {
		settingService.queryLocation(jsonObject,ctx);
	}
	
	
	@Override
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext ctx) {
		messageService.playAudio(jsonObject,ctx);
	}


	@Override
	public void setSetting(JSONObject jsonObject, ChannelHandlerContext ctx) {
		serverService.setSetting(jsonObject,ctx);
	}


	@Override
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext ctx) {
		messageService.requestJiesan(jsonObject,ctx);
	}


	@Override
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		messageService.jiesanRoom(jsonObject,ctx);
	}
}
