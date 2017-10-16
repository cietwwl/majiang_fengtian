package com.zxz.service;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.config.utils.Config;
import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.controller.StartServer;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.utils.DateUtils;
import com.zxz.utils.RoomCardUtils;

public class DateServiceImpl implements DateService,Serializable,RoomCardUtils {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(StartServer.class);
	
	/**
	 * 总的在线用户
	 */
	private static int totalOnLineUser = 0;
	
	/**
	 *总的登录用户 
	 */
	private static int totalLoginUser = 0;
	
	
	
	/**
	 * 是否等待重启
	 */
	private static boolean isWaitRestart = false;
	
	
	
	
	public static boolean isWaitRestart() {
		return isWaitRestart;
	}


	public static void setWaitRestart(boolean isWaitRestart) {
		DateServiceImpl.isWaitRestart = isWaitRestart;
	}


	/**
	 * 用户建立连接的时候，增加在线的用户数
	 */
	public void addOnLineUser(){
		totalOnLineUser ++;
	}
	
	
	/**
	 * 用户离线的时候,减少在线的用户数
	 */
	public void subOnLineUser(){
		totalOnLineUser --;
	}
	
	
	/**
	 * 用户登录的时候添加用户数
	 */
	public void addLoginUser(){
		totalLoginUser ++;
		logger.info("在线人数+1,现在总的在线人数是"+totalLoginUser);
	}
	
	/**
	 * 如果用户在登录状态离线的时候减小登录数
	 */
	public void subLoginUser(){
		totalLoginUser --;
		logger.warn("在线人数-1,现在总的在线人数是"+totalLoginUser);
	}
	
	@Override
	public int getTotalOneLineUser() {
		return totalOnLineUser;
	}

	@Override
	public int getTotalLoginLineUser() {
		return totalLoginUser;
	}

	@Override
	public JSONObject getGameJSONObject() {
		return null;
	}

	@Override
	public String getRoomJSONObject() {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		Iterator<String> iterator = roomMap.keySet().iterator();
		JSONArray jsonArray = new JSONArray();
		while(iterator.hasNext()){
			String key = iterator.next();
			OneRoom oneRoom = roomMap.get(key);
			int roomNumber = oneRoom.getRoomNumber();//房间号
			String userName = oneRoom.getCreateUser().getUserName();//房主昵称
			int total = oneRoom.getTotal();//局数
			Date createDate = oneRoom.getCreateDate();//创建时间
			String sCreateDate = DateUtils.getFormatDate(createDate, "yyyy/MM/dd HH:mm:ss");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("roomNumber", roomNumber);
			jsonObject.put("userName", userName);
			jsonObject.put("total", total);
			jsonObject.put("createDate", sCreateDate);
			jsonObject.put("createUserId", oneRoom.getCreateUserId());
			jsonObject.put("alreadyTotalGame", oneRoom.getAlreadyTotalGame());
			jsonObject.put("totalUser", oneRoom.getUserList().size());
			jsonArray.put(jsonObject);
		}
		return jsonArray.toString();
	}


	@Override
	public void sendFilePathToRoom(String roomId,String fileName,String userId) {
		OneRoom room = RoomManager.getRoomWithRoomId(roomId);
		if(room==null){
			logger.info("发送语音的时候房间不存在:"+roomId+" "+fileName+" "+userId);
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "downloadFile");
		jsonObject.put("userId", userId);
		jsonObject.put("fileName", fileName);
		room.noticeUsersWithJsonObject(jsonObject);
	}


	@Override
	public String getRoomDetail(String roomId) {
		JSONObject jsonObject = new JSONObject();
		OneRoom room = RoomManager.getRoomWithRoomId(roomId);
		List<User> userList = room.getUserList();
		JSONArray userArray = new JSONArray();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			JSONObject userJson = new JSONObject();
			userJson.put("userId", user.getId());//用户ID
			userJson.put("nickName", user.getNickName());//用户昵称
			userJson.put("headimgurl", user.getHeadimgurl());//头像
			userArray.put(userJson);
		}
		jsonObject.put("userArray", userArray);
		return jsonObject.toString();
	}


	@Override
	public String getTotalOnlineUserAndTotalRoom() {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalLoginUser", totalLoginUser);//总的登录用户数
		jsonObject.put("totalRoom", roomMap.size());//总的房间人数
		Config localconfig = Config.getConfig();
		jsonObject.put("port", localconfig.getPort());//总的房间人数
		jsonObject.put("isWaitRestart", isWaitRestart);//总的房间人数
		return jsonObject.toString();
	}


	@Override
	public String isHaveRoomWithRoomId(String roomId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		
		OneRoom oneRoom = roomMap.get(roomId);
		JSONObject outJsonObject = new JSONObject();
		if(oneRoom!=null){
			int totalUser = oneRoom.getUserList().size();
			if(totalUser<4){
				outJsonObject.put("code", "success");
				outJsonObject.put("m", "beforeEnterRoom");
				outJsonObject.put("description", "房间存在");
				Config localconfig = Config.getConfig();
				outJsonObject.put("port", localconfig.getPort());
			}else{//房间已满
				outJsonObject.put("code", "error");
				outJsonObject.put("m", "beforeEnterRoom");
				outJsonObject.put("description", "房间已满");
			}
		}else{//房间不存在
			outJsonObject.put("code", "error");
			outJsonObject.put("m", "beforeEnterRoom");
			outJsonObject.put("description", "房间不存在");
		}
		return outJsonObject.toString();
	}


	@Override
	public String isUserInRoomWithRoomId(String roomId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		boolean result = roomMap.containsKey(roomId);
		JSONObject outJsonObject = new JSONObject();
		if(result){
			outJsonObject.put("isUserInRoom", true);
			Config localconfig = Config.getConfig();
			outJsonObject.put("port", localconfig.getPort());
		}else{
			outJsonObject.put("isUserInRoom", false);
		}
		return outJsonObject.toString();
	}

	
	@Override
	public String isUserInRoomWithRoomId(String roomId,String userId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		boolean result = roomMap.containsKey(roomId);
		JSONObject outJsonObject = new JSONObject();
		boolean isIn =false;
		if(result){
			OneRoom oneRoom = roomMap.get(roomId);
			List<User> userList = oneRoom.getUserList();
			for(int i=0;i<userList.size();i++){
				User user = userList.get(i);
				if((user.getId()+"").equals(userId)){
					isIn = true;
				}
			}
			outJsonObject.put("isUserInRoom", isIn);
			Config localconfig = Config.getConfig();
			outJsonObject.put("port", localconfig.getPort());
		}else{
			outJsonObject.put("isUserInRoom", isIn);
		}
		return outJsonObject.toString();
	}

	@Override
	public String isHaveRoom(String roomId){
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		OneRoom oneRoom = roomMap.get(roomId);
		JSONObject outJsonObject = new JSONObject();
		if(oneRoom!=null){
			outJsonObject.put("code", "success");
			outJsonObject.put("m", "beforeEnterRoom");
			outJsonObject.put("description", "房间存在");
			Config localconfig = Config.getConfig();
			outJsonObject.put("port", localconfig.getPort());
		}else{//房间不存在
			outJsonObject.put("code", "error");
			outJsonObject.put("m", "beforeEnterRoom");
			outJsonObject.put("description", "房间不存在");
		}
		return outJsonObject.toString();
	}
	
	
	
	@Override
	public int disbandRoom(String roomId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		OneRoom oneRoom2 = roomMap.get(roomId);
		System.out.println(oneRoom2);
		boolean contain = roomMap.containsKey(roomId);
		if(contain){
			OneRoom oneRoom = roomMap.get(roomId);
			List<User> userList = oneRoom.getUserList();
			Game game = GameManager.getGameWithRoomNumber(roomId);
			int total = oneRoom.getAlreadyTotalGame();
			JSONObject summarizeJsonObject = PlayGameService.getSummarizeJsonObjectByHouTai(userList,total+1,Integer.parseInt(roomId));
			if(game!=null){
				//记录玩家的总成绩
				PlayGameService.recoredUserScore(summarizeJsonObject, game);
			}
			MessageService messageService = new MessageService();
			messageService.jieSanRoom(oneRoom);
			return 1;
		}
		logger.warn("后台强制解散游戏:"+roomId);
		return 0;
	}

	
	@Override
	public String getRoomList(int userId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		Iterator<String> iterator = roomMap.keySet().iterator();
		JSONArray jsonArray = new JSONArray();
		while(iterator.hasNext()){
			String key = iterator.next();
			OneRoom oneRoom = roomMap.get(key);
			if(oneRoom!=null){
				int createUserId = oneRoom.getCreateUserId();
				if(createUserId==userId){
					int roomNumber = oneRoom.getRoomNumber();//房间号
					int total = oneRoom.getTotal();//局数
					Date createDate = oneRoom.getCreateDate();//创建时间
					String sCreateDate = DateUtils.getFormatDate(createDate, "yyyy/MM/dd HH:mm:ss");
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("roomId", roomNumber);
//					jsonObject.put("total", total);
					jsonObject.put("createDate", sCreateDate);
					jsonObject.put("now", oneRoom.getUserList().size());
					jsonObject.put("total", oneRoom.getTotalUser());
					//后加
					jsonObject.put("fengdingNum", oneRoom.getFengDingNum());
					jsonObject.put("fangzhuName", oneRoom.getCreateUser().getNickName());
					jsonObject.put("isDaiChikaimen", oneRoom.isChiKaiMen());
					jsonObject.put("isDaiGuodan", oneRoom.isGuoDan());
					jsonObject.put("isDaiQingyise", oneRoom.isQingYiSe());
					jsonObject.put("isDaiDianpao", oneRoom.isDianPao());
					
					
					jsonArray.put(jsonObject);
				}
			}
		}
		return jsonArray.toString();
	}
		
	
	@Override
	public int checkCard(int userId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		Iterator<String> iterator = roomMap.keySet().iterator();
		int totalCard = 0;
		while(iterator.hasNext()){
			String key = iterator.next();
			OneRoom oneRoom = roomMap.get(key);
			if(oneRoom!=null){
				boolean use = oneRoom.isUse();
				if(!use){
					int createUserId = oneRoom.getCreateUserId();
					if(createUserId==userId){
						int total = oneRoom.getTotal();//局数
						if(total<=8){
							totalCard = totalCard + MINCARD;
						}else{
							totalCard = totalCard + MAXCARD;
						}
					}
				}
			}
				
		}
		return totalCard;
	}


	@Override
	public String setKouFangKa(String isKouFangKa) {

		return null;
	}

	/**
	 * 代理创建空房间
	 */
	@Override
	public int daiLiCreateRoom(String serviceStr) {
		UserService userService = new UserService();
		JSONObject jsonObject = new JSONObject(serviceStr);
		int daiLiCreateRoom = userService.daiLiCreateRoom(jsonObject);
		return daiLiCreateRoom;
	}
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject("{\"roleNum\":4,\"daichunqidui\":false,\"fengdingNum\":32,\"daipengkaimen\":true,\"quanNum\":8,\"daiqingyise\":false,\"userId\":10020,\"daiguodan\":false}");
		DateServiceImpl dateServiceImpl = new DateServiceImpl();
		int daiLiCreateRoom = dateServiceImpl.daiLiCreateRoom(jsonObject.toString());
		System.out.println(daiLiCreateRoom);
		
	}

	/**
	 * 检测是否有房间里面有该用户
	 * @param userId
	 * @return
	 */
	@Override
	public boolean isRoomsHaveThisUser(int userId) {
		Map<String, OneRoom> roomMap = RoomManager.getRoomMap();
		Iterator<String> iterator = roomMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			OneRoom oneRoom = roomMap.get(key);
			List<User> userList = oneRoom.getUserList();
			for (User user : userList) {
				if(userId==user.getId()){
					return true;
				}
			}
		}
		return false;
	}
	
}
