package com.zxz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.StringUtils;
import com.zxz.controller.RoomManager;
import com.zxz.dao.Location;
import com.zxz.dao.MessageDao;
import com.zxz.dao.UserDao;
import com.zxz.domain.Message;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.Constant;
import com.zxz.utils.DateUtils;
import com.zxz.utils.NotifyTool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**得到用户的设置信息
 * @author Administrator
 *
 */
public class SettingService implements Constant{
	
	UserDao userDao = UserDao.getInstance();
	MessageDao messageDao = new MessageDao();
	
	
	public void getSetting(JSONObject jsonObject, ChannelHandlerContext ctx){
		String type = jsonObject.getString("type");
		if(type.equals("getIsOwner")){//得到是否是房主
			getIsOwner(jsonObject,ctx);
		}else if(type.equals("checkUserIsInRoomBuild")){//创建房间检测用户是否在房间里
			checkUserIsInRoomBuild(jsonObject,ctx);
		}else if(type.equals("checkUserIsInRoomEnter")){//进入房间检测用户是否在房间里面
			checkUserIsInRoomEnter(jsonObject,ctx);
		}else if(type.equals("getBuyCardMessage")){
			getBuyCardMessage(jsonObject,ctx);//得到购买房卡的通知 
		}
	}

	

	/**得到购买放房卡的通知
	 * @param jsonObject
	 * @param session
	 */
	private void getBuyCardMessage(JSONObject jsonObject, ChannelHandlerContext session) {
		String redisMessage = RedisUtil.getKey("message",REDIS_DB_FENGTIAN);
		if(redisMessage!=null){
			session.write(redisMessage);
		}else{
			Map<String, Object> map = new HashMap<>();
			map.put("type", 0);
			map.put("rowStart", 0);
			map.put("pageSize", 1);
			List<Message> list = messageDao.selectListByMap(map);
			if(list.size()>0){
				Message message = list.get(0);
				JSONObject outJsonObejct = new JSONObject();
				outJsonObejct.put(method, "getSettingInfo");
				outJsonObejct.put(type, "getBuyCardMessage");
				outJsonObejct.put("message", message.getMessage());
				outJsonObejct.put("createDate", DateUtils.getFormatDate(message.getCreateDate(), "yyyy/MM/dd hh:mm:ss"));
				session.write(outJsonObejct);
				RedisUtil.setKey("message", outJsonObejct.toString(),REDIS_DB_FENGTIAN);
			}
		}
	}



	/**进入房间
	 * @param jsonObject
	 * @param ctx
	 */
	private void checkUserIsInRoomEnter(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		boolean isInRoom = cheekUserInRoom(user);
		notifyUserIsInRoom(user,ctx,isInRoom,"checkUserIsInRoomEnter");
	}



	/**检测用户是否在房间里
	 * @param jsonObject
	 * @param ctx
	 */
	private void checkUserIsInRoomBuild(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		boolean isInRoom = cheekUserInRoom(user);
		notifyUserIsInRoom(user,ctx,isInRoom,"checkUserIsInRoomBuild");
//		if(isInRoom){//如果在房间里面
//			UserService.getRoomInfoAndReplaceUserSession(user);
//		}
	}



	/**检测用户是否在房间里面
	 * @param user
	 * @return
	 */
	public  boolean cheekUserInRoom(User user) {
		boolean isInRoom = false;
		String roomId = user.getRoomId();
		if(roomId!=null&&!"".equals(roomId)){
			OneRoom room = RoomManager.getRoomWithRoomId(roomId);
			//如果游戏未开始，但是在房间里面game==null
			//如果game!=null,表示游戏已经开始玩了
			if(room!=null){
				isInRoom = true;
			}
		}
		return isInRoom;
	}

	/**用户没有在房间里面，通知用户可以进入房间
	 * @param user
	 * @param isInRoom 
	 */
	private void notifyUserIsInRoom(User user,ChannelHandlerContext session, boolean isInRoom,String type) {
		JSONObject outJSONObject = new JSONObject();
		outJSONObject.put(method, "getSettingInfo");
		outJSONObject.put(SettingService.type, type);
		outJSONObject.put("isInRoom", isInRoom);
		outJSONObject.put(discription, "是否断线的接口");
		NotifyTool.notify(session, outJSONObject);;
	}

	/**得到是否是房主
	 * @param jsonObject
	 * @param ctx
	 */
	private void getIsOwner(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "getSettingInfo");
		outJsonObject.put(type, "getIsBank");
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		//防止卡死在房间里面,用户点击设置的时候空指针
		if(oneRoom==null){
			outJsonObject.put("isOwner", false);
			NotifyTool.notify(ctx, outJsonObject);
			return;
		}
		int createUserId = oneRoom.getCreateUserId();
		if(user.getId()==createUserId){
			outJsonObject.put("isOwner", true);
		}else{
			outJsonObject.put("isOwner", false);
		}
		NotifyTool.notify(ctx, outJsonObject);
	}



	/**用户修改推荐号
	 * @param jsonObject
	 * @param ctx
	 */
	public void queryLocation(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int qlID = jsonObject.getInt("qlID"); //被查询人ID
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		List<User> userList = oneRoom.getUserList();
		JSONObject outJson = new  JSONObject();
		outJson.put("method", "QLocInfo");
		outJson.put("qlID", qlID);
		JSONArray array =  new JSONArray();
		for(int i=0;i<userList.size();i++){
		
			JSONObject userLocation = new JSONObject();
			array.put(userLocation);
		}
		outJson.put("QLocInfos", outJson);
		NotifyTool.notify(ctx, outJson);
	}
	
}
