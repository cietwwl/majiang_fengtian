package com.zxz.service;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

public class ServiceMaster {

	JSONObject jsonObject;
	public ServiceMaster() {
	}
	public ServiceMaster(JSONObject jsonObject) {
		super();
		this.jsonObject = jsonObject;
	}

	public void serviceStart(ChannelHandlerContext ctx, JSONObject jsonObject) {
		String method = jsonObject.getString("m");
		if (method == null) {
			ctx.write("method is null");
			return;
		}
		hongZhongMaster(ctx, jsonObject);
	}
	
	private void hongZhongMaster(ChannelHandlerContext ctx, JSONObject jsonObject) {
		String method = jsonObject.getString("m");
		PlayOfHongZhong playOfHongZhong = new UserService();
		switch (method) {
		case "createRoom" :
			playOfHongZhong.createRoom(jsonObject, ctx);// 创建房间
			break;
		case "updateRinfo":
			playOfHongZhong.updateRinfo(jsonObject, ctx);// 更改房间的信息
			break;
		case "enterRoom":
			playOfHongZhong.enterRoom(jsonObject, ctx);// 进入房间
			break;
		case "playGame":
			playOfHongZhong.playGame(jsonObject, ctx);// 开始打牌
			break;
		case "gpsInfo":
			playOfHongZhong.sendGps(jsonObject, ctx);//gps
			break;
		case "jsRoom":
			playOfHongZhong.jsRoom(jsonObject, ctx);//解散房间(奉天)
			break;
		case "tcRoom":
			playOfHongZhong.tcRoom(jsonObject, ctx);//退出房间(奉天)
			break;
		case "daikai":
			playOfHongZhong.dkRoom(jsonObject, ctx);//代开房间(奉天)
			break;
		case "tcORjsORsq":
			playOfHongZhong.tcORjsORsq(jsonObject, ctx);//退出房间、解散房间、申请解散房间(奉天)
			break;
		case "DLjcORsq":
			playOfHongZhong.DLjcORsq(jsonObject, ctx);//代理退出房间(奉天)
			break;
		case "disbandRoom":
			playOfHongZhong.disbandRoom(jsonObject, ctx);// 解散房间
			break;
		case "getMyInfo":
			playOfHongZhong.getMyInfo(jsonObject, ctx);// 得到我自己的信息
			break;
		case "leaveRoom":
			playOfHongZhong.leaveRoom(jsonObject, ctx);// 离开房间
			break;
		case "getSettingInfo":
			playOfHongZhong.getSetting(jsonObject, ctx);// 得到自己的设置信息
			break;
		case "getSysMsg":
			playOfHongZhong.getSysMsg(jsonObject, ctx);// 得到自己的设置信息
			break;
		case "getServerInfo":
			playOfHongZhong.getServerInfo(jsonObject, ctx);// 得到剩余的牌
			break;
		case "prepare":
			playOfHongZhong.continueGame(jsonObject, ctx);// 继续游戏
			break;
		case "settingAuto":
			playOfHongZhong.settingAuto(jsonObject, ctx);// 设置托管
			break;
		case "cancelAuto":
			playOfHongZhong.cancelAuto(jsonObject, ctx);// 取消托管
			break;
		case "reConnect":
//			playOfHongZhong.downGameInfo(jsonObject, session);// 断线重连
			playOfHongZhong.downGameInfoWithUnionid(jsonObject, ctx);// 断线重连
			break;
		case "getUserScore":
			playOfHongZhong.getUserScore(jsonObject, ctx);// 得到战绩
			break;
		case "userLocation":
			playOfHongZhong.userLocation(jsonObject, ctx);// 得到总战绩
			break;
		case "queryLocation":
			playOfHongZhong.queryLocation(jsonObject, ctx);//请求查询定位
			break;
		case "playAudio":
			playOfHongZhong.playAudio(jsonObject, ctx);//发送消息
			break;
		case "setSetting":
			playOfHongZhong.setSetting(jsonObject, ctx);//设置自己的信息
			break;
		case "requestJsRoom":
			playOfHongZhong.requestJiesan(jsonObject, ctx);//申请解散房间
			break;
		case "isAgreeJsRoom":
			playOfHongZhong.jiesanRoom(jsonObject, ctx);//同意解散房间
			break;
		case "playSound":
			playOfHongZhong.playSound(jsonObject, ctx);
			break;
		case "checkHeart":
			//心跳检测
			playOfHongZhong.checkHeart(jsonObject, ctx);
			break;
		}	
	}
}
