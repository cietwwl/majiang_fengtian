package com.zxz.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.Constant;
import com.zxz.utils.NotifyTool;
import com.zxz.utils.TypeUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class MessageService implements Constant{

	private static Logger logger = Logger.getLogger(MessageService.class);
	
	/**发送消息
	 * @param jsonObject
	 * @param ctx
	 */
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String type = jsonObject.getString("type");
		int messageId = jsonObject.getInt("messageId");
		playAudio(messageId,ctx,type);
	}

	/**发送文字
	 * @param messageId
	 * @param ctx
	 */
	private void playAudio(int messageId, ChannelHandlerContext ctx,String type) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playAudio");
		outJsonObject.put(Constant.type, type);
		outJsonObject.put("messageId", messageId);
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
	}

	/**申请解散房间
	 * {"m":"requestJsRoom","userDir":0}
	 * @param jsonObject
	 * @param ctx
	 */
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
		Game game = GameManager.getGameWithRoomNumber(roomId);
		User gamingUser = game.getGamingUser(user.getDirection());
		int totalRequetDisbanRoom = gamingUser.getTotalRequetDisbanRoom();
		gamingUser.setTotalRequetDisbanRoom(totalRequetDisbanRoom+1);;
		gamingUser.setIsAgreeDisbandType(1);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "requestJsRoom");
		outJsonObject.put("userDir", TypeUtils.getUserDir(gamingUser.getDirection()));
		List<User> userList = oneRoom.getUserList();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()!=gamingUser.getId()){
				u.setIsAgreeDisbandType(0);
			}
		}
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
	}
	
	/**等待解散
	 * @param userDir 
	 * @param game
	 * {"m":"sqRoom","isSuccess":true,"userDir":0,"discription":"解散成功"}
	 */
	public static void waitJieSan(final String roomId, final int userDir) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
				if(oneRoom!=null){
					List<User> userList = oneRoom.getUserList();
					int totalAgree = 0;
					for (User user : userList) {
						int agreeDisband = user.getIsAgreeDisbandType();
						logger.info(user.getNickName()+" type:"+ agreeDisband);
						if(agreeDisband==1||agreeDisband==0){//同意解散
							totalAgree = totalAgree+1;
						}
					}
					if(totalAgree==oneRoom.getTotalUser()){//如果是房间人全部同意 
						Game game = GameManager.getGameWithRoomNumber(roomId);
						game.setDisband(true);//游戏解散
						//修改玩家的当前分数
						User gamingUser = game.getSeatMap().get(TypeUtils.getStringDir(userDir));
						int dir = userDir;
						PlayGameService.guoDanFen(game);
						PlayGameService.notifyUserZhuaWinByJieSan(gamingUser,game,0,dir,false);
						PlayGameService.setCurrentGameOverByJieSan(game,Integer.parseInt(roomId));//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
						jieSanRoom(oneRoom);
					}else{
						logger.info(totalAgree);
						return;
					}
				}
			}
		}, TIME_TO_WAITE_JIESAN);
	}

	/**解散房间
	 * @param jsonObject
	 * @param ctx
	 */
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		Game game = GameManager.getGameWithRoomNumber(roomId);
		String direction = user.getDirection();
		User gameUser = game.getSeatMap().get(direction);
		boolean isAgree = jsonObject.getBoolean("isAgree");
		int userDir = jsonObject.getInt("userDir");
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "isAgreeJsRoom");
		outJsonObject.put("isAgree", isAgree);
		outJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		if(!isAgree){
			for (User u : oneRoom.getUserList()) {
				if(TypeUtils.getUserDir(u.getDirection())==userDir){
					u.setIsAgreeDisbandType(2);
					break;
				}
			}
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put("discription", "解散房间失败");
			outJSONbject.put("m", "jsRoom");
			outJSONbject.put("userDir", userDir);
			outJSONbject.put("isSuccess", false);
			NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJSONbject);
			return;
		}
		gameUser.setIsAgreeDisbandType(1);
		for (User u : oneRoom.getUserList()) {
			if(u.getId()==gameUser.getId()){
				u.setIsAgreeDisbandType(1);
			}
		}
		int totalAgree = 0;
		for (User u : oneRoom.getUserList()) {
			int agreeDisband = u.getIsAgreeDisbandType();
			if(agreeDisband==1){//同意解散
				totalAgree = totalAgree+1;
			}
		}
		if(totalAgree==oneRoom.getTotalUser()){
			Game game1 = GameManager.getGameWithRoomNumber(oneRoom.getId()+"");
			game1.setDisband(true);//游戏解散
			//修改玩家的当前分数
			User gamingUser1 = game1.getSeatMap().get(TypeUtils.getStringDir(userDir));
			int dir = userDir;
			PlayGameService.guoDanFen(game1);
			PlayGameService.notifyUserZhuaWinByJieSan(gamingUser1,game1,0,dir,false);
			PlayGameService.setCurrentGameOverByJieSan(game1,Integer.parseInt(roomId));//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
			MessageService.jieSanRoom(oneRoom);
			return;
		}
	}

	/**解散房间
	 * @param user
	 * @param type
	 * @param oneRoom
	 * @param dir 
	 */
	public static void jieSanRoom( OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.clearAll();//清空用户所有的属性
			RedisUtil.delKey("usRoomId"+user.getId(), REDIS_DB_FENGTIAN);
		}
		logger.info("投票解散房间成功,游戏停止");
	}
}
