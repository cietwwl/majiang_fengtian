package com.zxz.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.mysql.jdbc.StringUtils;
import com.zxz.config.utils.Config;
import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.dao.MessageDao;
import com.zxz.dao.OneRoomDao;
import com.zxz.dao.UserDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.domain.Game;
import com.zxz.domain.GangCard;
import com.zxz.domain.Message;
import com.zxz.domain.OneRoom;
import com.zxz.domain.PengCard;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.CountDownThread;
import com.zxz.utils.DateUtils;
import com.zxz.utils.EmojiUtil;
import com.zxz.utils.HuPai;
import com.zxz.utils.MathUtil;
import com.zxz.utils.NotifyTool;
import com.zxz.utils.RoomCardUtils;
import com.zxz.utils.RoomNumberUtil;
import com.zxz.utils.TypeUtils;
import com.zxz.utils.WeiXinUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserService extends BasePlay implements PlayOfHongZhong, Constant,Serializable,RoomCardUtils {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(UserService.class);

	OneRoomDao roomDao = OneRoomDao.getInstance();
	static UserDao userDao = UserDao.getInstance();
	PlayGameService playGameService = new PlayGameService();
	DateServiceImpl dateService = new DateServiceImpl();// 统计相关

	/**
	 * 登录
	 * 
	 * @param jsonObject
	 * @param ctx
	 * @return
	 */
	public boolean login(JSONObject jsonObject, ChannelHandlerContext ctx) {
		boolean hasUnionid = jsonObject.has("unionid");// 是否含有hasUnionid
		boolean loginResult = false;
		boolean addLoginUserData = true;// 是否添加用户数
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		if (user != null) {// 已经登录
			addLoginUserData = false;
			String roomId = user.getRoomId();
			boolean result = loginWithUnionid(jsonObject, ctx, roomId);
			doOtherThings(ctx);
			return result;
		}
		if (hasUnionid) {
			loginResult = loginWithUnionid(jsonObject, ctx, null);
		} else {
			loginResult = loginWithCode(jsonObject, ctx);
		}
		// 用户登录的时候添加用户数
		if (loginResult) {
			// 1.添加用户数
			if (addLoginUserData) {
				dateService.addLoginUser();
			}
			// 2.检测用户是否在房间里面,如果在房间里面,下载用户的信息,并替换用户的session
			doOtherThings(ctx);
		}
		return loginResult;
	}

	/**
	 * @param user
	 * @return
	 */
	public JSONObject createLoginJsonObjectWithUser(User user) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("method", "login");
		jsonObject.put("unionid", user.getUnionid());
		return jsonObject;
	}

	/**
	 * 检测用户是否在房间里面,如果在房间里面,通知用户进入房间,如果没有
	 */
	public void doOtherThings(ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		SettingService settingService = new SettingService();
		boolean isInRoom = settingService.cheekUserInRoom(user);
		if (isInRoom) {
			downGameInfo(ctx);
		}
	}

	private void downGameInfo(ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		JSONObject roomInfo = getRoomInfo(user);
		if (roomInfo == null) {// 游戏没开始，通知用户进入房间
			// 得到房间里的用户信息
			OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("method", "enterRoom");
			outJsonObject.put("code", "success");
			getRoomInfo(outJsonObject, oneRoom,user);
			ctx.write(outJsonObject.toString());
			replaceUserIoSession(user, oneRoom);
			return;
		}
		nofiyUserRoomInfo(roomInfo, ctx);
		// Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		replaceUserIoSession(user, oneRoom);
	}

	/**
	 * 用unionid登录,说明用户已经注册过
	 * 
	 * @param jsonObject
	 * @param session
	 * @param roomId
	 * @return
	 */
	private boolean loginWithUnionid(JSONObject jsonObject, ChannelHandlerContext session, String roomId) {
		String unionid = jsonObject.getString("unionid");
		switch (unionid) {
		case "obhqFxAmLRLMv1njQnWFsl_npjPw":// 顾双
		case "obhqFxIRabSd9B2qhT_ThzsXMU58":// 周益雄
		case "obhqFxCzFVH5UkKJRIH-AqePEnZ8":// 张森
		case "obhqFxHtB3emb506Q-FsZwW4_Py4":// 尤海涛s
			User user = new User();
			user.setUnionid(unionid);
			;
			user = userDao.findUser2(user);
			testLogin(unionid, session, roomId);
			return true;
		}
		User user = new User();
		user.setUnionid(unionid);
		;
		user = userDao.findUser2(user);
		if (user != null) {// 用户不存在
			String refreshToken = user.getRefreshToken();
			try {
				String accesstoekn = WeiXinUtil.getAccessTokenWithRefreshToken(refreshToken);
				JSONObject userInfo = WeiXinUtil.getUserInfo(accesstoekn, user.getOpenid());
				setUserWithUserInfoJson(userInfo, user);// 封装用户的信息
				if (!StringUtils.isNullOrEmpty(roomId)) {
					user.setRoomId(roomId);
				}
				notifyUserLoginSuccess(user, session, false);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				notifyUserLoginFail(session, "errorRefreshToken", "微信refreshToken过期请重新授权");
				logger.fatal("微信refreshToken过期");
				return false;
			}
		} else {
			notifyUserLoginFail(session, "errorUnionId", "unionId不存在");
			return false;
		}

	}

	private void testLogin(String unionid, ChannelHandlerContext session, String roomId) {
		User user = new User();
		user.setUnionid(unionid);
		;
		user = userDao.findUser2(user);
		if (user != null) {
			try {

				if (!StringUtils.isNullOrEmpty(roomId)) {
					user.setRoomId(roomId);
				}

				JSONObject userInfo = new JSONObject();
				user.setHeadimgurl(
						"http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/06/c1/16396010_1354784049718.jpg");
				userInfo.put("userId", user.getId());
				userInfo.put("userName", user.getNickName());
				// userInfo.put("headimgurl","http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1212/06/c1/16396010_1354784049718.jpg");
				userInfo.put("roomCard", user.getRoomCard());// 剩余的房卡数
				userInfo.put("unionid", user.getUnionid());// 唯一的unionid
				userInfo.put("nickname", user.getUserName());// 唯一的unionid
				notifyUserLoginSuccess(user, session, false);
			} catch (Exception e) {
				e.printStackTrace();
				notifyUserLoginFail(session, "errorRefreshToken", "微信refreshToken过期请重新授权");
				logger.fatal("微信refreshToken过期");
			}
		} else {
			notifyUserLoginFail(session, "errorUnionId", "unionId不存在");
		}
	}

	/**
	 * 用code登录
	 * 
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	private boolean loginWithCode(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String code = jsonObject.getString("code");
		try {
			JSONObject accessTokenJson = WeiXinUtil.getAccessTokenJson(code);
			String refreshToken = accessTokenJson.getString("refresh_token");
			String openid = accessTokenJson.getString("openid");
			String accesstoken = accessTokenJson.getString("access_token");
			JSONObject userInfoJson = WeiXinUtil.getUserInfo(accesstoken, openid);
			String openId = userInfoJson.getString("openid");
			User findUser = new User();
			findUser.setOpenid(openId);
			User user = userDao.findUser2(findUser);
			findUser.setRefreshToken(refreshToken);
			setUserWithUserInfoJson(userInfoJson, findUser);// 封装用户的信息
			boolean isFirstLogin = true;
			if (user == null) {// 没有注册
				registUser(userInfoJson, findUser);
			} else {// 已经注册获取用户的房卡数量
					// 修改用户的refreshToken
				User modifyUser = new User();
				modifyUser.setId(user.getId());
				modifyUser.setRefreshToken(refreshToken);
				userDao.modifyUser(modifyUser);
				findUser.setId(user.getId());
				findUser.setRoomCard(user.getRoomCard());
				findUser.setRoomId(user.getRoomId());
				isFirstLogin = false;// 不是第一次登陆
			}
			ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
			notifyUserLoginSuccess(findUser, ctx, isFirstLogin);
		} catch (Exception e) {
			logger.info("微信登录失败");
			logger.fatal(e);
			notifyUserLoginFail(ctx, "errorCode", "CODE传递不正确");
			return false;
		}
		return true;
	}

	/**
	 * 通知用户登录失败
	 * 
	 * @param session
	 */
	private void notifyUserLoginFail(ChannelHandlerContext session, String errorCode, String discription) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("login", false);
		outJsonObject.put("method", "login");
		outJsonObject.put("errorCode", errorCode);
		outJsonObject.put(UserService.discription, discription);
		session.write(outJsonObject.toString());
	}

	/**
	 * 通知用户登录成功
	 * 
	 * @param findUser
	 * @param session
	 * @param isFirstLogin
	 *            是否第一次注册
	 */
	private void notifyUserLoginSuccess(User findUser, ChannelHandlerContext ctx, boolean isFirstLogin) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "login");
		findUser.setIoSession(ctx);
		ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
		outJsonObject.put("login", true);
		outJsonObject.put("userId", findUser.getId());
		outJsonObject.put("userName", findUser.getNickName());
		outJsonObject.put("headimgurl", findUser.getHeadimgurl());
		outJsonObject.put("roomCard", findUser.getRoomCard());// 剩余的房卡数
		outJsonObject.put("unionid", findUser.getUnionid());// 唯一的unionid
		outJsonObject.put("isFirstLogin", isFirstLogin);// 是否第一次登陆，也就是注册
		outJsonObject.put("sex", Integer.parseInt(findUser.getSex()));// 性别
		outJsonObject.put("description", "登录成功!");
		String remoteAddress = ctx.channel().remoteAddress().toString();
		outJsonObject.put("ip", remoteAddress.replaceAll("/", ""));// ip地址
		ctx.write(outJsonObject.toString());
	}

	/**
	 * 注册用户
	 * 
	 * @param userInfoJson
	 * @param findUser
	 */
	private void registUser(JSONObject userInfoJson, User findUser) {
		// findUser.setNickName(""); //过滤掉特殊字符
		// 设置用户默认的房卡数量
		findUser.setRoomCard(DEFAULT_USER_REGIST_ROOMCARD);
		findUser.setCreateDate(new Date());
		userDao.saveUser(findUser);
	}

	/**
	 * 封装用户的信息
	 * 
	 * @param userInfoJson
	 * @param findUser
	 */
	private void setUserWithUserInfoJson(JSONObject userInfoJson, User findUser) {
		logger.info("userInfoJson:" + userInfoJson);
		String nickName = userInfoJson.getString("nickname");// 昵称
		String unionid = userInfoJson.getString("unionid");
		String city = userInfoJson.getString("city");// 城市
		String headimgurl = userInfoJson.getString("headimgurl");// 头像
		String province = userInfoJson.getString("province");// 省份
		int sex = userInfoJson.getInt("sex");// 性别
		// String refreshToken = userInfoJson.getString("refresh_token");
		findUser.setCity(city);
		findUser.setHeadimgurl(headimgurl);
		findUser.setUnionid(unionid);
		// findUser.setNickName(nickName);
		findUser.setNickName(EmojiUtil.resolveToByteFromEmoji(nickName)); // 过滤掉特殊字符
		findUser.setProvince(province);
		findUser.setSex(sex + "");
		// findUser.setRefreshToken(refreshToken);
	}

	

	/**
	 * 创建房间
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public void createRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		int userid = jsonObject.getInt("userId");
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+userid,REDIS_DB_FENGTIAN);
		String userInfo = hashMap.get("baseInfo");
		User user = new User();
		if (userInfo != null) {
			User infoUser = getUserFromUserInfo(userInfo, userid + "");
			if (infoUser != null) {
				user = infoUser;
			} else {
				user.setId(userid);
				user = userDao.findUser2(user);
			}
		} else {
			user.setId(userid);
			user = userDao.findUser2(user);
		}
		if (user != null) {
			int total = jsonObject.getInt("quanNum");// 圈数
			boolean isCanCreateRoom = checkUserCanCreateRoom(user, total);
			if (isCanCreateRoom) {
				// 用unionid从微信获取用户的信息
//				changeUserHeadimageWithUserUnionid(user);
				User u = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
				if (u == null) {
					ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(user);
					user.setIoSession(ctx);
					dateService.addLoginUser();
				}
				realCreateRoom(jsonObject, ctx);
			}else{
				logger.info("房卡不足,创建房间失败!");
				
			}
		}
	}
	/**
	 * 代理创建房间
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public int daiLiCreateRoom(JSONObject jsonObject) {
		int userid = jsonObject.getInt("userId");
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+userid,REDIS_DB_FENGTIAN);
		String userInfo = hashMap.get("baseInfo");
		User user = new User();
		if (userInfo != null) {
			User infoUser = getUserFromUserInfo(userInfo, userid + "");
			if (infoUser != null) {
				user = infoUser;
			} else {
				user.setId(userid);
				user = userDao.findUser2(user);
			}
		} else {
			user.setId(userid);
			user = userDao.findUser2(user);
		}
		if (user != null) {
			int total = jsonObject.getInt("quanNum");// 圈数
			boolean isCanCreateRoom = checkUserCanCreateRoom(user, total);
			if (isCanCreateRoom) {
				daiLiRealCreateRoom(jsonObject,user);
				return 1;
			}
		}
		return 0;
	}


	@Override
	public void updateRinfo(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = sessionUser.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		
		int total = jsonObject.getInt("total");
		int totalUser = jsonObject.getInt("totalUser");
		int fengDingNum = jsonObject.getInt("fengDingNum");
		boolean pengKaiMen = jsonObject.getBoolean("pengKaiMen");
		boolean dianPao = jsonObject.getBoolean("dianPao");
		boolean yiTouTing = jsonObject.getBoolean("yiTouTing");
		boolean shouLouZi = jsonObject.getBoolean("shouLouZi");
		boolean qingYiSe = jsonObject.getBoolean("qingYiSe");
		boolean guoDan = jsonObject.getBoolean("guoDan");
		boolean qiDui = jsonObject.getBoolean("qiDui");
		boolean daiPiao = jsonObject.getBoolean("daiPiao");

		
		oneRoom.setTotal(total);
		oneRoom.setTotalUser(totalUser);
		oneRoom.setFengDingNum(fengDingNum);
//		oneRoom.setPengKaiMen(pengKaiMen);
		oneRoom.setDianPao(dianPao);
		oneRoom.setYiTouTing(yiTouTing);
		oneRoom.setShouLouZi(shouLouZi);
		oneRoom.setQingYiSe(qingYiSe);
		oneRoom.setGuoDan(guoDan);
	/*	oneRoom.setQiDui(qiDui);
		oneRoom.setDaiPiao(daiPiao);*/
		
		JSONObject outJson = new JSONObject();
		outJson.put("total", oneRoom.getTotal());
		outJson.put("totalUser", oneRoom.getTotalUser());
		outJson.put("setFengDingNum", oneRoom.getFengDingNum());
//		outJson.put("pengKaiMen", oneRoom.isPengKaiMen());
		outJson.put("dianPao", oneRoom.isDianPao());
		outJson.put("yiTouTing", oneRoom.isYiTouTing());
		outJson.put("shouLouZi", oneRoom.isShouLouZi());
		outJson.put("qingYiSe", oneRoom.isQingYiSe());
		outJson.put("guoDan", oneRoom.isGuoDan());
		/*outJson.put("qiDui", oneRoom.isQiDui());
		outJson.put("daiPiao", oneRoom.isDaiPiao());*/
		outJson.put("method", "updateRinfo");
		List<ChannelHandlerContext> userIoSessionList = oneRoom.getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJson);
		
	}
	
	
	/**
	 * 从userInfo中得到用户
	 * 
	 * @param userInfo
	 * @return
	 */
	private User getUserFromUserInfo(String userInfo, String unionid) {
		try {
			JSONObject jsonObject = new JSONObject(userInfo);
			User user = new User();
			user.setId(jsonObject.getInt("userId"));
			user.setUnionid(jsonObject.getString("unionid"));
			user.setUserName(jsonObject.getString("userName"));
			user.setNickName(jsonObject.getString("userName"));
			user.setHeadimgurl(jsonObject.getString("headimgurl"));
			user.setRoomCard(jsonObject.getInt("roomCard"));
			user.setSex(jsonObject.getString("sex"));
			user.setRefreshToken(jsonObject.getString("refreshToken"));
			return user;
		} catch (Exception e) {
			logger.info(e);
//			RedisUtil.delKey(unionid,REDIS_DB_SANGUO);
		}
		return null;
	}

	/**
	 * 用unionid从微信获取用户的信息
	 * 
	 * @param user
	 */
	private void changeUserHeadimageWithUserUnionid(User user) {
		// String unionid = user.getUnionid();
		// switch (unionid) {
		// case "obhqFxAmLRLMv1njQnWFsl_npjPw"://顾双
		// case "obhqFxIRabSd9B2qhT_ThzsXMU58"://周益雄
		// case "obhqFxCzFVH5UkKJRIH-AqePEnZ8"://张森
		// case "obhqFxHtB3emb506Q-FsZwW4_Py4"://尤海涛s
		// return;
		// }

		String refreshToken = user.getRefreshToken();
		try {
			String accesstoekn = WeiXinUtil.getAccessTokenWithRefreshToken(refreshToken);
			JSONObject userInfo = WeiXinUtil.getUserInfo(accesstoekn, user.getOpenid());
			setUserWithUserInfoJson(userInfo, user);// 封装用户的信息
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("微信refreshToken过期");
		}
	}

	/**
	 * 检测用户是否可以创建房间
	 * 
	 * @param session
	 * @return true 可以 false 不可以
	 */
	private boolean checkUserCanCreateRoom(User user, int total) {
		int id = user.getId();
		Map<Object, Object> findUser = userDao.findZjUserByUserId(id);
		int roomCard = (int) findUser.get("roomCard");// 房卡的数量
		user.setRoomCard(roomCard);
		int consumeCardNum = MINCARD;
		if (total >=16) {
			consumeCardNum = MAXCARD;
		}
		if (roomCard >= consumeCardNum) {
			return true;
		}
		return false;
	}

	/**
	 * 在进行验证完之后,用户可以创建房间
	 * @param jsonObject
	 * @param ctx
	 */
	private void realCreateRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		OneRoom room = setRoomAttribute(jsonObject);//设置房间属性
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		room.setCreateUserId(user.getId());// 创建人的ID,房主
		room.setInvertal(Config.getConfig().getInterval());
		int roomId = RoomNumberUtil.getOneRoomNumber();
		room.setId(roomId);
//		roomDao.saveRoom(room);
		room.setRoomNumber(roomId);// 设置房间号
		room.setCreateUser(user);// 创建房间的人
		room.setCreateDate(new Date());
		user.setDirection("east");
		user.setRoomId(roomId + "");
		user.setCurrentGame(0);
		user.setAuto(false);// 不是自动准备
		user.setFangZhu(true);
		user.setReady(true);
		Set<String> directionSet = room.getDirectionSet();
		directionSet.add("east");
		room.addUser(user);
		RoomManager.addRoomMap(roomId + "", room);
		JSONObject outJsonObject = new JSONObject();	
		outJsonObject.put("m", "createRoom");
		outJsonObject.put("isFz", user.isFangZhu());// 是否是房主
		outJsonObject.put("isSuccess", true);// 创建成功
		outJsonObject.put("roomId", roomId);
		outJsonObject.put("fangzhuId", user.getId());//房主id
		outJsonObject.put("roleNum", room.getTotalUser());//房间人数
		outJsonObject.put("quanNum", room.getTotal());//圈数
		outJsonObject.put("fengdingNum", room.getFengDingNum());//封顶数
		outJsonObject.put("daiyitouting", room.isYiTouTing());//是否一头听
		outJsonObject.put("daidianpao", room.isDianPao());//是否点炮
		outJsonObject.put("daichikaimen", room.isChiKaiMen());//是否吃开门
		outJsonObject.put("daiqiduihun", room.isHunQiDui());//是否混七对
		outJsonObject.put("daiqiduichun", room.isChunQiDui());//是否纯七对
		outJsonObject.put("daipiaohun", room.isHunPiao());//是否混飘
		outJsonObject.put("daipiaochun", room.isChunPiao());//是否纯飘
		outJsonObject.put("daishoulouzi", room.isShouLouZi());//是否手搂子
		outJsonObject.put("daiqingyise", room.isQingYiSe());//是否清一色
		outJsonObject.put("daiguodan", room.isGuoDan());//是否过蛋	
		outJsonObject.put("roleDir", TypeUtils.getUserDir(user.getDirection()));//玩家在房间内的方向
		outJsonObject.put("isReady", user.isReady());
		outJsonObject.put("userSex", Integer.parseInt(user.getSex()));
		outJsonObject.put("userHead", user.getHeadimgurl());
		outJsonObject.put("userName", user.getNickName());
		outJsonObject.put("userId", user.getId());
		String ipAddress = user.getIoSession().channel().remoteAddress().toString();
		String userIp = ipAddress.replaceAll("/", "").split(":")[0];
		outJsonObject.put("userIp", userIp);
		outJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
		outJsonObject.put("isDrop", user.isDropLine());
		if(user.getTingpaiState()==1){
			outJsonObject.put("isAlreadyTing", true);
		}else{
			outJsonObject.put("isAlreadyTing", false);
		}
		outJsonObject.put("description", "创建房间成功!");
		ctx.write(outJsonObject.toString());
		RedisUtil.setKey("usRoomId"+user.getId(), roomId+"", REDIS_DB_FENGTIAN);
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+user.getId(),REDIS_DB_FENGTIAN);
		hashMap.put("roomId", roomId+"");
		RedisUtil.setHashMap("uid"+user.getId(), hashMap, REDIS_DB_FENGTIAN,TIME_TO_USER_ROOM);
		autoDisbandRoom(roomId + "", user);
		putRoomInfoToRedis(roomId);
	}
	
	/**
	 * 在进行验证完之后,用户可以创建房间
	 * @param jsonObject
	 * @param user 
	 * @param ctx
	 */
	private void daiLiRealCreateRoom(JSONObject jsonObject, User user) {
		OneRoom room = setRoomAttribute(jsonObject);//设置房间属性
		room.setCreateUserId(user.getId());// 创建人的ID,房主
		room.setInvertal(Config.getConfig().getInterval());
		int roomId = RoomNumberUtil.getOneRoomNumber();
		room.setId(roomId);
		room.setRoomNumber(roomId);// 设置房间号
		room.setCreateUser(user);// 创建房间的人
		room.setCreateDate(new Date());
		RoomManager.addRoomMap(roomId + "", room);
		daiLiAutoDisbandRoom(roomId + "", user);
		putRoomInfoToRedis(roomId);
	}

	
	
	
	/**设置房间的属性
	 * @param jsonObject
	 * @return
	 */
	private OneRoom setRoomAttribute(JSONObject jsonObject) {
		OneRoom room = new OneRoom();
		room.setCreateDate(new Date());// 房间创建时间
		int total = jsonObject.getInt("quanNum");// 局数
		int auto = 0;
		int totalUser=0;
		room.setTotal(total);
		
		if (jsonObject.has("roleNum")) {//房间人数
			totalUser = jsonObject.getInt("roleNum");
			room.setTotalUser(totalUser);
		}
		if (jsonObject.has("auto")) {
			auto = jsonObject.getInt("auto");
			room.setAuto(auto);
		}
		int fengDingNum = 0;
		if (jsonObject.has("fengdingNum")) {
			fengDingNum = jsonObject.getInt("fengdingNum");
			room.setFengDingNum(fengDingNum);
		}
		
		boolean chiKaiMen = true;
		if (jsonObject.has("daichikaimen")) {
			chiKaiMen = jsonObject.getBoolean("daichikaimen");
			room.getFengTianHuPai().propertyForRoom_pengkaimen = chiKaiMen;
			room.setChiKaiMen(chiKaiMen);
		}
		boolean dianPao = true;
		if (jsonObject.has("daidianpao")) {
			dianPao = jsonObject.getBoolean("daidianpao");
			room.getFengTianHuPai().propertyForRoom_dianpao = dianPao;
			room.setDianPao(dianPao);
		}
		boolean yiTouTing = true;
		if (jsonObject.has("daiyitouting")) {
			yiTouTing = jsonObject.getBoolean("daiyitouting");
			room.getFengTianHuPai().propertyForRoom_yitouting = yiTouTing;
			room.setYiTouTing(yiTouTing);
		}
		boolean shouLouZi = true;
		if (jsonObject.has("daishoulouzi")) {
			shouLouZi = jsonObject.getBoolean("daishoulouzi");
			room.getFengTianHuPai().propertyForRoom_shoulouzi = shouLouZi;
			room.setShouLouZi(shouLouZi);
		}
		boolean qingYiSe = true;
		if (jsonObject.has("daiqingyise")) {
			qingYiSe = jsonObject.getBoolean("daiqingyise");
			room.getFengTianHuPai().propertyForRoom_qingyise  = qingYiSe;
			room.setQingYiSe(qingYiSe);
		}
		boolean guoDan = true;
		if (jsonObject.has("daiguodan")) {
			guoDan = jsonObject.getBoolean("daiguodan");
			room.getFengTianHuPai().propertyForRoom_guodan = guoDan;
			room.setGuoDan(guoDan);
		}
		boolean hunQiDui = false;
		if(jsonObject.has("daiqiduihun")){
			hunQiDui = jsonObject.getBoolean("daiqiduihun");
			room.getFengTianHuPai().propertyForRoom_qiduihun = hunQiDui;
			room.setHunQiDui(hunQiDui);
		}
		boolean daichunqidui = false;
		if(jsonObject.has("daichunqidui")){
			daichunqidui = jsonObject.getBoolean("daichunqidui");
			room.getFengTianHuPai().propertyForRoom_qiduichun = daichunqidui;
			room.setChunQiDui(daichunqidui);
		}
		boolean hunPiao = false;
		if(jsonObject.has("daipiaohun")){
			hunPiao = jsonObject.getBoolean("daipiaohun");
			room.getFengTianHuPai().propertyForRoom_piaohun = hunPiao;
			room.setHunPiao(hunPiao);
		}
/*		boolean zhanHu = false;
		if(jsonObject.has("zhanHu")){
			zhanHu = jsonObject.getBoolean("zhanHu");
			room.getFengTianHuPai().propertyForRoom_zhanhu = zhanHu;
			room.setZhanHu(zhanHu);
		}*/
		return room;
	}



	/**
	 * 把房间信息放入redis中
	 * 
	 * @param roomId
	 */
	private void putRoomInfoToRedis(int roomId) {
		// 将房间的地址放入redis中
		JSONObject roomJsonObject = new JSONObject();
		Config config = Config.getConfig();
		roomJsonObject.put("bestServer", config.getLocalIp());
		roomJsonObject.put("port", config.getPort());
		roomJsonObject.put("localRPCPort", config.getRPcPort());
		RedisUtil.setKey("ftRoomId"+roomId, roomJsonObject.toString(),REDIS_DB_FENGTIAN);
	}

	/**
	 * 如果10分钟游戏开没有开始则自动解散房间
	 * 
	 * @param game
	 */
	private static void autoDisbandRoom(final String roomId, final User user) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				OneRoom room = RoomManager.getRoomWithRoomId(roomId);
				if (room != null) {
					boolean disband = room.isDisband();// 并且房间没有解散
					if (!room.isPay() && !disband) {// 房间还没有玩，并且还没有解散
						int roomNumber = room.getRoomNumber();
						realDisbandRoom(user, roomNumber, room);
						logger.info("房间" + TIME_TO_DISBAND_ROOM + "s未使用,自动解散房间" + roomNumber);
					}
				}
			}
		}, TIME_TO_DISBAND_ROOM);
	}
	/**
	 * 如果10分钟游戏开没有开始则自动解散房间
	 * 
	 * @param game
	 */
	private static void daiLiAutoDisbandRoom(final String roomId, final User user) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				OneRoom room = RoomManager.getRoomWithRoomId(roomId);
				if (room != null) {
					boolean disband = room.isDisband();// 并且房间没有解散
					if (!room.isPay() && !disband) {// 房间还没有玩，并且还没有解散
						int roomNumber = room.getRoomNumber();
						daiLiRealDisbandRoom(user, roomNumber, room);
						logger.info("房间" + TIME_TO_DISBAND_ROOM + "s未使用,自动解散房间" + roomNumber);
					}
				}
			}
		}, TIME_TO_DISBAND_ROOM);
	}

	/**
	 * 进入房间
	 * 
	 * @param jsonObject
	 * @param session
	 */
	public void enterRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		enterRoomWithUnionid(jsonObject, session);
	}

	/**
	 * 得到房间里的信息，并且替换掉死去的session,并且通知玩家当前的游戏信息
	 * 
	 * @param user
	 */
	public static JSONObject getRoomInfo(User user) {
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		JSONObject roomInfoJson = null;
		if (game != null) {// 游戏已经开始
			int status = game.getStatus();
			if (status == GAGME_STATUS_OF_IS_GAMING) {//游戏进行中
				roomInfoJson = getRoomInfoGaming(user, game);
			} else if (status == GAGME_STATUS_OF_WAIT_START) {//游戏未开始
				roomInfoJson = getRoomInfoGameWaitOrXuanPao(user, game,"gameForReady");
			}else {
				logger.fatal("得到游戏信息时候除了这两个难道还有别的吗?应该永远不会进入到这个逻辑里面吧");
			}
		}
		return roomInfoJson;
	}
	
	private static JSONObject getRoomInfoGameWaitOrXuanPao(User user, Game game,String status) {
		user.setReady(true);
		List<User> userList = game.getRoom().getUserList();
		JSONArray userJsonArray = new JSONArray();
		OneRoom room = game.getRoom();
		int zhuangDir = 0;
		for (int i = 0; i < userList.size(); i++) {
			JSONObject userJSONObject = new JSONObject();
			User u = userList.get(i);
			if(u.isBanker()){
				zhuangDir = TypeUtils.getUserDir(u.getDirection());
			}
			boolean ready = u.isReady();
			userJSONObject.put("isReady", ready);
			userJSONObject.put("userId", u.getId());
			userJSONObject.put("userName", u.getUserName());
			userJSONObject.put("userHead", u.getHeadimgurl());
			userJSONObject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
			userJSONObject.put("isDrop", u.isDropLine());
			userJSONObject.put("userSex", Integer.parseInt(u.getSex()));
			String userIp = u.getIp().replaceAll("/", "").split(":")[0];
			userJSONObject.put("userIp", userIp);
			userJSONObject.put("isAlreadyTing", false);
			userJSONObject.put("scoreByAdd", u.getXuChangScore().getScore());//本局分数
			userJSONObject.put("scoreTotal", u.getXuChangScore().getCurrentScore());//总分数
			// 得到用户当前的分数
			userJsonArray.put(userJSONObject);
		}
		JSONObject infoJsonObject = new JSONObject();
		infoJsonObject.put("m", "reConnect");
		infoJsonObject.put("description", "玩家断线重连");
		infoJsonObject.put("isSuccess", true);
		infoJsonObject.put("roomId", room.getRoomNumber()); // 房间号
		infoJsonObject.put("fangzhuId", room.getCreateUserId()); // 房主ID
		infoJsonObject.put("roleNum", room.getTotalUser()); // 人数
		infoJsonObject.put("quanNum", room.getTotal()); // 圈数
		infoJsonObject.put("zhuangDir", zhuangDir); // 圈数
		infoJsonObject.put("quanCurrent", room.getAlreadyTotalGame()+1); // 当前圈数
		infoJsonObject.put("timeForRoomBuild", DateUtils.getDateByFormat(room.getCreateDate())); // 当前圈数
		infoJsonObject.put("timeForGameStart", DateUtils.getDateByFormat(new Date())); //
		infoJsonObject.put("timeForGameFinish", "0"); // 
		
		infoJsonObject.put("fengdingNum", room.getFengDingNum());//房间封顶数
		infoJsonObject.put("daiyitouting", room.isYiTouTing());//是否一头听
		infoJsonObject.put("daidianpao", room.isDianPao());//是否点炮
		infoJsonObject.put("daichikaimen", room.isChiKaiMen());//是否吃开门
		infoJsonObject.put("daiqiduihun", room.isHunQiDui());//是否混七对
		infoJsonObject.put("daiqiduichun", room.isChunQiDui());//是否纯七对
		infoJsonObject.put("daipiaohun", room.isHunPiao());//是否混飘
		infoJsonObject.put("daipiaochun", room.isChunPiao());//是否纯飘
		infoJsonObject.put("daishoulouzi", room.isShouLouZi());//是否手搂子
		infoJsonObject.put("daiqingyise", room.isQingYiSe());//是否清一色
		infoJsonObject.put("daiguodan", room.isGuoDan());//是否过蛋	
		
		infoJsonObject.put("elseCards", 0);	
		infoJsonObject.put("roleDir", TypeUtils.getUserDir(user.getDirection())); //玩家在房间内的方向
		infoJsonObject.put("gamgState", "fapaiPre");
		infoJsonObject.put("userInfo", userJsonArray);
		return infoJsonObject;
	}
	
	
	/**
	 * 得到当前用户当前分数
	 * 
	 * @param u
	 *            当前用户
	 * @return 当前的分数
	 */
	public static int getUserCurrentGameScore(User u) {
		UserScoreDao userScoreDao = UserScoreDao.getInstance();// 用户分数
		UserScore userScore = new UserScore();
		userScore.setUserid(u.getId());
		userScore.setRoomid(Integer.parseInt(u.getRoomId()));
		int playerScoreByAdd = u.getXuChangScore().getCurrentScore();// 用户当前的分数
		return playerScoreByAdd;
	}

	/**
	 * 替换掉用户的ChannelHandlerContext
	 * 
	 * @param user
	 * @param oneRoom
	 *            房间
	 */
	private static void replaceUserIoSession(User user, OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {

				u.setDropLine(false);
				u.setIoSession(user.getIoSession());
				user.setDirection(u.getDirection());// 方向也变
				user.setAuto(u.isAuto());// 是否托管
				user.setRoomId(u.getRoomId());// 房间号传过来
				user.setCards(u.getCards());// 牌改变过来
			}
		}
		
	}

	/**
	 * 游戏进行中，通知玩家
	 * 
	 * @param roomInfoJson
	 * @param ctx 
	 * @param user
	 */
	private static void nofiyUserRoomInfo(JSONObject roomInfoJson, ChannelHandlerContext ctx) {
		NotifyTool.notify(ctx, roomInfoJson);
	}

	/**
	 * 断线重连的时候游戏没有开始,
	 * 
	 * @param user
	 * @param oneRoom
	 */
	private static void gameNotStartReplaceUserSession(User user, OneRoom oneRoom) {
		JSONObject outJsonObject = new JSONObject();
		getRoomInfo(outJsonObject, oneRoom,null);
		outJsonObject.put("method", "enterRoom");
		outJsonObject.put("code", "success");
		NotifyTool.notify(user.getIoSession(), outJsonObject);// 通知他本人房间里的信息
		replaceRoomSession(oneRoom, user);
	}

	/**
	 * 替换掉原来用户
	 * 
	 * @param oneRoom
	 * @param user
	 */
	public static void replaceRoomSession(OneRoom oneRoom, User user) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {
				u.setIoSession(user.getIoSession());// 替换它的ioSession
			}
		}
	}

	/**
	 * 游戏已经开始(游戏进行中)
	 * 
	 * @param user
	 * @param game
	 */
	private static JSONObject getRoomInfoGamingOld(User user, Game game) {
		OneRoom room = game.getRoom();
		JSONObject roomInfoJSONObject = new JSONObject();// 房间信息
		JSONArray userInfoJsonArray = new JSONArray();
		String direc = game.getDirec();
		roomInfoJSONObject.put("nowDirection", direc);
		Date lastChuPaiDate = game.getSeatMap().get(direc).getLastChuPaiDate();
		Date nowDate = new Date();
		long interval = CountDownThread.getInterval(lastChuPaiDate, nowDate);
		int status = game.getGameStatus();
		long intervalOfDate = getIntervalOfDate(status, interval);// 倒计时从哪里开始
		int d = (int) (intervalOfDate / 1000);
		roomInfoJSONObject.put("countDownTime", d);
		roomInfoJSONObject.put(method, "userDropLine");
		roomInfoJSONObject.put(type, "gameStart");
		roomInfoJSONObject.put("roomId", room.getRoomNumber()); // 房间号
		roomInfoJSONObject.put("remainCardsTotal", game.getRemainCards().size());// 剩余的牌数
		List<User> userList = room.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			JSONObject myInfo;
			if (u.getId() == user.getId()) {
				myInfo = u.getMyInfo(true);
			} else {
				myInfo = u.getMyInfo(true);
			}
			userInfoJsonArray.put(myInfo);
		}
		int totalGame = game.getTotalGame();// 总共的局数
		int bankUserId = getBankUserId(userList);// 庄家的userid
		int alreadyTotalGame = game.getAlreadyTotalGame();// 已经玩的局数
		roomInfoJSONObject.put("totalGame", totalGame);
		roomInfoJSONObject.put("alreadyTotalGame", alreadyTotalGame);
		roomInfoJSONObject.put("users", userInfoJsonArray);
		roomInfoJSONObject.put("bankUserId", bankUserId);
		roomInfoJSONObject.put(discription, "掉线重连,玩的过程中掉线");
		return roomInfoJSONObject;
	}

	/**
	 * 游戏已经开始(游戏进行中)
	 * @param user
	 * @param game
	 * {"isSuccess":true,"description":"描述",
	 * "roomId":999999,"fangzhuId":10020,
	 * "roleNum":4,"roleDir":0,"quanNum":2,
	 * "fengdingNum":32,"daiyitouting":true,
	 * "daidianpao":true,"daichikaimen":true,
	 * "daiqiduihun":true,"daipiaohun":true,
	 * "daishoulouzi":true,"daiqingyise":true,"daiguodan":true}
	 */
	private static JSONObject getRoomInfoGaming(User user1, Game game) {
		User user = game.getSeatMap().get(user1.getDirection());
		OneRoom room = game.getRoom();
		JSONObject roomInfoJSONObject = new JSONObject();// 房间信息
		int status = game.getGameStatus();
		int yaoPaiStatus = game.getYaoPaiStatus();
		JSONArray jsonArray = new JSONArray();
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator = seatMap.keySet().iterator();
		int zhuangDir = 0;
		while (iterator.hasNext()) {
			String nextDir = iterator.next();
			User u = seatMap.get(nextDir);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isReady", u.isReady());
			jsonObject.put("isDrop", u.isDropLine());
			jsonObject.put("userSex", Integer.parseInt(u.getSex()));
			jsonObject.put("userHead", u.getHeadimgurl());
			jsonObject.put("userName", u.getUserName());
			jsonObject.put("userId", u.getId());
			if(u.isBanker()){
				zhuangDir = TypeUtils.getUserDir(u.getDirection());
			}
			boolean isAlreadyTing = false;
			if(u.getTingpaiState()==1){
				isAlreadyTing = true;
			}
			jsonObject.put("isAlreadyTing", isAlreadyTing);
			String userIp = u.getIp().replaceAll("/", "").split(":")[0];
			jsonObject.put("userIp", userIp);
			jsonObject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
			jsonObject.put("scoreByAdd", u.getXuChangScore().getScore());//本局分数
			jsonObject.put("scoreTotal", u.getXuChangScore().getCurrentScore());//总分数
			jsonArray.put(jsonObject);
		}
		roomInfoJSONObject.put("m", "reConnect");
		roomInfoJSONObject.put("description", "玩家断线重连");
		roomInfoJSONObject.put("isSuccess", true);
		roomInfoJSONObject.put("roomId", room.getRoomNumber()); // 房间号
		roomInfoJSONObject.put("fangzhuId", room.getCreateUserId()); // 房主ID
		roomInfoJSONObject.put("roleNum", room.getTotalUser()); // 人数
		roomInfoJSONObject.put("quanNum", room.getTotal()); // 圈数
		roomInfoJSONObject.put("zhuangDir", zhuangDir); // 圈数
		roomInfoJSONObject.put("quanCurrent", room.getAlreadyTotalGame()+1); // 当前圈数
		roomInfoJSONObject.put("timeForRoomBuild", DateUtils.getDateByFormat(room.getCreateDate())); // 
		roomInfoJSONObject.put("timeForGameStart", DateUtils.getDateByFormat(game.getStartDate())); //
		roomInfoJSONObject.put("timeForGameFinish", DateUtils.getDateByFormat(game.getEndDate())); // 
		
		roomInfoJSONObject.put("fengdingNum", room.getFengDingNum());//房间封顶数
		roomInfoJSONObject.put("daiyitouting", room.isYiTouTing());//是否一头听
		roomInfoJSONObject.put("daidianpao", room.isDianPao());//是否点炮
		roomInfoJSONObject.put("daichikaimen", room.isChiKaiMen());//是否吃开门
		roomInfoJSONObject.put("daiqiduihun", room.isHunQiDui());//是否混七对
		roomInfoJSONObject.put("daiqiduichun", room.isChunQiDui());//是否纯七对
		roomInfoJSONObject.put("daipiaohun", room.isHunPiao());//是否混飘
		roomInfoJSONObject.put("daipiaochun", room.isChunPiao());//是否纯飘
		roomInfoJSONObject.put("daishoulouzi", room.isShouLouZi());//是否手搂子
		roomInfoJSONObject.put("daiqingyise", room.isQingYiSe());//是否清一色
		roomInfoJSONObject.put("daiguodan", room.isGuoDan());//是否过蛋
		
		roomInfoJSONObject.put("elseCards", game.getRemainCards().size());	
		roomInfoJSONObject.put("roleDir", TypeUtils.getUserDir(user.getDirection())); //玩家在房间内的方向
		

		// 得到游戏的状态碰牌、出牌、和杠牌、胡牌
		roomInfoJSONObject.put("gamgState", "fapaiNext");// 游戏状态  发牌后
		roomInfoJSONObject.put("userInfo", jsonArray);
		JSONArray jsonArrayMess = new JSONArray();
		List<User> userList = room.getUserList();
		for (User u : userList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("arrayLiangHei", TypeUtils.getFengList(u.getHeiFengGangCards()));
			jsonObject.put("arrayLiangBai", TypeUtils.getFengList(u.getBaiFengGangCards()));
			
			jsonObject.put("arrayGangM", TypeUtils.getGangMList(u.getGangCards()));
			jsonObject.put("arrayGangA", TypeUtils.getGangAList(u.getGangCards()));
			
			jsonObject.put("arrayPeng", TypeUtils.getPengList(u.getPengCards()));
			jsonObject.put("arrayChi",TypeUtils.getChiList(u.getChiCards()));
			jsonObject.put("arrayThrow", u.getMyPlays());
			jsonObject.put("arrayHand", u.getCards());
			jsonObject.put("userId", u.getId());
			jsonObject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
			jsonArrayMess.put(jsonObject);
		}
		roomInfoJSONObject.put("userCards", jsonArrayMess);
		if(status==1){//抓牌后的状态
			roomInfoJSONObject.put("playCardStare", "afterZhua");
			if(yaoPaiStatus==2){//出的牌没人要
				roomInfoJSONObject.put("afterZhuaState", "meirenYao");
				  Integer integer2 = game.getLastMap().get("dir");
				  if(integer2==null||integer2.equals("")){
					  integer2 = TypeUtils.getUserDir(game.getDirec());
				  }
				roomInfoJSONObject.put("dirChu",integer2);//出牌方向
				Integer integer = game.getLastMap().get("cardId");
				if(integer==null){
					roomInfoJSONObject.put("cardIdChu", 0);//出的牌ID
				}else{
					roomInfoJSONObject.put("cardIdChu", integer);//出的牌ID
				}
				roomInfoJSONObject.put("isGangP",user.isCanGang());//用户是否可以碰杠
				roomInfoJSONObject.put("isGangA", user.isCanAnGang());//用户是否可以暗杠
				roomInfoJSONObject.put("isLiangHei", user.isCanHeiFeng());//用户是否可以亮黑
				roomInfoJSONObject.put("isLiangBai", user.isCanBaiFeng());//用户是否可以亮白
				roomInfoJSONObject.put("isTing", user.isCanTing());
				roomInfoJSONObject.put("isTingType", user.getTingpaiState());
				roomInfoJSONObject.put("isHu", user.isCanWin());
/*				Integer gongGangCardId = game.getGongGangCardId();
				if(gongGangCardId==null){
					gongGangCardId = 0;
				}*/ 
				if(user.isCanAnGang()){
					Integer integer3 = game.getAnGangCards().get(0);
					roomInfoJSONObject.put("cardidForUse", integer3);	
				}else if(user.getMyGrabCard()!=null){
					roomInfoJSONObject.put("cardidForUse", user.getMyGrabCard());	
				}else{
					roomInfoJSONObject.put("cardidForUse", 0);	
				}
				roomInfoJSONObject.put("dirZhua", TypeUtils.getUserDir(game.getDirec()));
				roomInfoJSONObject.put("cardIdZhua", 0);
				
			}else if(yaoPaiStatus==3){//出的牌有人要
				roomInfoJSONObject.put("afterZhuaState", "yourenYao");
				roomInfoJSONObject.put("dirZhua", TypeUtils.getUserDir(game.getDirec()));
				roomInfoJSONObject.put("cardIdZhua", 0);
				roomInfoJSONObject.put("isGangP",user.isCanGang());//用户是否可以碰杠
				roomInfoJSONObject.put("isGangA", user.isCanAnGang());//用户是否可以暗杠
				roomInfoJSONObject.put("isLiangHei", user.isCanHeiFeng());//用户是否可以亮黑
				roomInfoJSONObject.put("isLiangBai", user.isCanBaiFeng());//用户是否可以亮黑
				roomInfoJSONObject.put("isTing", user.isCanTing());
				roomInfoJSONObject.put("isTingType", user.getTingpaiState());
				roomInfoJSONObject.put("isHu", user.isCanWin());
				if(user.isCanAnGang()){
					Integer integer3 = game.getAnGangCards().get(0);
					roomInfoJSONObject.put("cardidForUse", integer3);	
				}else if(user.getMyGrabCard()!=null){
					roomInfoJSONObject.put("cardidForUse", user.getMyGrabCard());	
				}else{
					roomInfoJSONObject.put("cardidForUse", 0);	
				}
				
			}
		}else if(status==0){//出牌后的状态
			roomInfoJSONObject.put("playCardStare", "afterChu");
			roomInfoJSONObject.put("dirChu", TypeUtils.getUserDir(game.getDirec()));
			roomInfoJSONObject.put("cardIdChu", game.getLastMap().get("cardId"));
			roomInfoJSONObject.put("isChi", user.isCanChi());
			roomInfoJSONObject.put("isPeng", user.isCanPeng());
			roomInfoJSONObject.put("isGangM", user.isCanPengGang());
			roomInfoJSONObject.put("isHu", user.isCanWin());
		}
		return roomInfoJSONObject;
	}

	
	
	/**
	 * @param cardId 出的那张牌
	 * @param cards 手中的牌
	 * @return 2 可以杠 1可以碰
	 */
	public static int isCanPengOrCanGang(int cardId,List<Integer> cards){
		int number =  cardId /4;
		int total = 0;
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==number){
				total++;
			}
			if(total==4){
				break;
			}
		}
		if(total==3){
			return 2;
		}else if(total==2){
			return 1;
		}
		return 0;
	}
	
	
	
	/**
	 * 得到断线重连的时间，倒计时从哪里开始
	 * 
	 * @param status
	 * @param interval
	 * @return
	 */
	public static long getIntervalOfDate(int status, long interval) {
		long time = 0;
		switch (status) {
		case GAGME_STATUS_OF_CHUPAI:
			time = 30000 - interval - 200;
			break;
		case GAGME_STATUS_OF_PENGPAI:
		case GAGME_STATUS_OF_GANGPAI:
		case GAGME_STATUS_OF_ANGANG:
		case GAGME_STATUS_OF_GONG_GANG:
			time = 10000 - interval - 200;
			break;
		}
		return time;
	}

	/**
	 * 得到房间里的庄家id
	 * 
	 * @param list
	 * @return
	 */
	public static int getBankUserId(List<User> list) {
		for (int i = 0; i < list.size(); i++) {
			User user = list.get(i);
			if (user.isBanker() == true) {
				return user.getId();
			}
		}
		logger.fatal("难道说没有庄家吗??????????????");
		return -1;
	}

	/**
	 * 检测用户是否在房间里
	 * 
	 * @return
	 */
	public boolean checkUserIsInRoom(User user) {
		String roomId = user.getRoomId();
		if (roomId != null && !"".equals(roomId)) {
			OneRoom room = RoomManager.getRoomWithRoomId(roomId);
			if (room != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * {"m":"enterRoom","isSuccess":true,"description":"描述",
	 * "roomId":999999,"roleNum":4,"roleDir":0,"quanNum":2,
	 * "fengdingNum":32,"daiyitouting":true,"daidianpao":true,
	 * "daichikaimen":true,"daiqiduihun":true,"daipiaohun":true,
	 * "daishoulouzi":true,"daiqingyise":true,"daiguodan":true,
	 * "dir":0",users":[{"isReady":true,"userSex":0,
	 * "userIp":"192.168.1.205”,”userHead”:”头像地址”,
	 * "userName":"yht", "userId":10020,"userDir":0}]}
	 * 用unionid进入房间
	 * @param jsonObject
	 * @param ctx
	 */
	private void enterRoomWithUnionid(JSONObject jsonObject, ChannelHandlerContext ctx) {
		int roomId = jsonObject.getInt("roomId");
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		int userid = jsonObject.getInt("userId");
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+userid,REDIS_DB_FENGTIAN);
		String userInfo = hashMap.get("baseInfo");
		User user = new User();
		if (userInfo != null) {
			User infoUser = getUserFromUserInfo(userInfo, userid + "");
			if (infoUser != null) {
				user = infoUser;
			} else {
				user.setId(userid);
				user = userDao.findUser2(user);
			}
		} else {
			user.setId(userid);
			user = userDao.findUser2(user);
		}
		if (user == null) {
			return;
		} else {
			user.setIoSession(ctx);
		}
		boolean userCanEnterRoom = isUserCanEnterRoom(user, oneRoom, ctx);
		if (userCanEnterRoom) {// 如果可以进入房间
			int size = oneRoom.getUserList().size();
			if (size < oneRoom.getTotalUser()) {// 房间人数小于房间规定人数
				ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(user);
				dateService.addLoginUser();
				setUserDircetion(user, oneRoom);// 设置用户的方向
				user.setRoomId(roomId + "");
				user.setCurrentGame(0);
				if(oneRoom.getCreateUserId()==user.getId()){
					user.setFangZhu(true);
				}
				user.setAuto(false);
				user.setReady(true);
				oneRoom.addUser(user);
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("m", "enterRoom");
				List<User> userList = oneRoom.getUserList();
				for (User user2 : userList) {
					if(user2.isFangZhu()){
						outJsonObject.put("fangzhuId", user2.getId());
					}
				}
				getRoomInfo(outJsonObject, oneRoom,user);
				outJsonObject.put("isSuccess", true);
				outJsonObject.put("roleDir", oneRoom.getUserDir(user.getDirection()));//用户方向
				outJsonObject.put("description", "成功加入房间!");
				modifyUserRoomNumber(user);// 修改玩家的房间号
				NotifyTool.notify(ctx, outJsonObject);// 通知他本人房间里的信息
				notifyOtherUserEnterRoom(oneRoom, user);
				int totalReady = getTotalReady(userList);	
				if (totalReady == oneRoom.getTotalUser()) {// 开始游戏		
					Game game = getGame(oneRoom);
					if(game.getSeatMap()==null){
						game.generateSeat(oneRoom);//生成座次
					}
					List<User> userList1 = oneRoom.getUserList();
					for (User user1 : userList1) {
						if(user1.getDirection().equals("east")){
							user1.setBanker(true);
						}
					}
					startGame(oneRoom, ctx);
					return;
				}
				
			} else if (oneRoom.getUserList().size() >= oneRoom.getTotalUser()) {
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put("m", "enterRoom");
				outJsonObject.put("isSuccess", false);
				outJsonObject.put("description", "房间已满");
				ctx.write(outJsonObject.toString());
			}
		}
	}


	/**
	 * 修改玩家的房间号
	 * 
	 * @param user
	 */
	private static void modifyUserRoomNumber(User user) {
		User modifyUser = new User();
		modifyUser.setId(user.getId());
		modifyUser.setRoomId(user.getRoomId());
		userDao.modifyUser(user.getId(),user.getRoomId()+"");// 记录下用户的房间号
		Map<String, String> hashMap = RedisUtil.getHashMap("uid"+user.getId(),REDIS_DB_FENGTIAN);
		String roomId = user.getRoomId();
		boolean nullOrEmpty = StringUtils.isNullOrEmpty(roomId);
		if (nullOrEmpty) {
			roomId = 0 + "";
		}
		hashMap.put("roomId", roomId);
		RedisUtil.setHashMap("uid"+user.getId(), hashMap,REDIS_DB_FENGTIAN,TIME_TO_USER_ROOM);
	}

	/**
	 * 通知其他玩家，有人进入房间
	 * 
	 * @param oneRoom
	 * @param currentUser
	 */
	public static void notifyOtherUserEnterRoom(OneRoom oneRoom, User currentUser) {
		List<User> userList = oneRoom.getUserList();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "enterRoomOther");
		int userId = currentUser.getId();
		jsonObject.put("userId", userId);
		String userName = currentUser.getUserName();
		boolean ready = currentUser.isReady();
		jsonObject.put("userName", userName);
		jsonObject.put("isReady", ready);
		jsonObject.put("isDrop", currentUser.isDropLine());
		jsonObject.put("userHead", currentUser.getHeadimgurl());
		jsonObject.put("userDir",oneRoom.getUserDir(currentUser.getDirection()));
		jsonObject.put("userSex", Integer.parseInt(currentUser.getSex()));
		String ipAddress = currentUser.getIoSession().channel().remoteAddress().toString();
		String userIp = ipAddress.replaceAll("/", "").split(":")[0];
		jsonObject.put("userIp", userIp);
		if(currentUser.getTingpaiState()==1){
			jsonObject.put("isAlreadyTing", true);
		}else{
			jsonObject.put("isAlreadyTing", false);
		}
		jsonObject.put("scoreByAdd", currentUser.getXuChangScore().getScore());//本局分数
		jsonObject.put("scoreTotal", currentUser.getXuChangScore().getCurrentScore());//总分数
		
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.getId() != currentUser.getId()) {
				ChannelHandlerContext ioSession = user.getIoSession();
				NotifyTool.notify(ioSession, jsonObject);
			}
		}
	}

	/**
	 * 检测用户是否可以进入房间
	 * 
	 * @param user
	 *            被检测的用户
	 * @param oneRoom
	 *            房间
	 * @param session
	 * @return
	 */
	public boolean isUserCanEnterRoom(User user, OneRoom oneRoom, ChannelHandlerContext session) {
		boolean result = true;// 可以进入房间
		JSONObject outJsonObject = new JSONObject();
		if (oneRoom == null) {
			outJsonObject.put("m", "enterRoom");
			outJsonObject.put("isSuccess",false);
			outJsonObject.put("description", "房间不存在");
			session.write(outJsonObject.toString());
			// session.close();//房间不存在主动关闭session
			return false;
		}

		boolean userInRoom = isUserInRoom(oneRoom, user);
		if (userInRoom) {
			getRoomInfo(outJsonObject, oneRoom,user);
			outJsonObject.put("m", "enterRoom");
			outJsonObject.put("isSuccess", false);
			outJsonObject.put("fangzhuId", oneRoom.getCreateUserId());
//			outJsonObject.put("roleDir", TypeUtils.getUserDir(user.getDirection()));
			outJsonObject.put("description", "用户已经在房间里面了");
			session.write(outJsonObject.toString());
			return false;
		}
		return result;
	}

	/**
	 * 检验用户是否在这个房间里
	 * 
	 * @param oneRoom
	 * @param user
	 * @return
	 */
	public boolean isUserInRoom(OneRoom oneRoom, User user) {
		// 查看该用户是否已经在房间里
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (user.getId() == u.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置用户在房间的方向
	 * 
	 * @param user
	 * @param userSize
	 */
	public void setUserDircetion(User user, OneRoom oneRoom) {
		Set<String> directionSet = oneRoom.getDirectionSet();
		if (!directionSet.contains("east")) {
			user.setDirection("east");
			directionSet.add("east");
		} else if (!directionSet.contains("north")) {
			user.setDirection("north");
			directionSet.add("north");
		} else if (!directionSet.contains("west")) {
			user.setDirection("west");
			directionSet.add("west");
		} else if (!directionSet.contains("south")) {
			user.setDirection("south");
			directionSet.add("south");
		}
	}

	/**
	 * 设置用户在房间的方向(二人麻将)
	 * 
	 * @param user
	 * @param userSize
	 */
	public void setUserDircetionByTwo(User user, OneRoom oneRoom) {
		Set<String> directionSet = oneRoom.getDirectionSet();
		if (!directionSet.contains("east")) {
			user.setDirection("east");
			directionSet.add("east");
		} else  {
			user.setDirection("west");
			directionSet.add("west");
		}
	}
	/**
	 * 得到房间的信息
	 * 
	 * @param outJsonObject
	 * @param session
	 * @param oneRoom
	 * @param user 
	 */
	private static void getRoomInfo(JSONObject outJsonObject, OneRoom oneRoom, User user2) {
		List<User> userList = oneRoom.getUserList();
		JSONArray userArray = new JSONArray();
		int zhuangDir = 0;
		for (int i = 0; i < userList.size(); i++) {
			JSONObject userJsonObject = new JSONObject();
			User user = userList.get(i);
			if(user.isBanker()){
				zhuangDir = TypeUtils.getUserDir(user.getDirection());
			}
			int userId = user.getId();
			userJsonObject.put("userId", userId);
			String userName = user.getUserName();
			boolean ready = user.isReady();
			userJsonObject.put("userName", userName);
			userJsonObject.put("userHead", user.getHeadimgurl());// 头像
			userJsonObject.put("isReady", ready);
			userJsonObject.put("isDrop", user.isDropLine());
			userJsonObject.put("isAlreadyTing", false);
			userJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			userJsonObject.put("userSex", Integer.parseInt(user.getSex()));
			String ipAddress = user.getIoSession().channel().remoteAddress().toString();
			String userIp = ipAddress.replaceAll("/", "").split(":")[0];
			userJsonObject.put("userIp", userIp);
			if(user.getTingpaiState()==1){
				userJsonObject.put("isAlreadyTing", true);
			}else{
				userJsonObject.put("isAlreadyTing", false);
			}
			userJsonObject.put("scoreByAdd", user.getXuChangScore().getScore());//本局分数
			userJsonObject.put("scoreTotal", user.getXuChangScore().getCurrentScore());//总分数
			userArray.put(userJsonObject);
		}
		Game game = GameManager.getGameWithRoomNumber(oneRoom.getId()+"");
		outJsonObject.put("userInfo", userArray);
		outJsonObject.put("fangzhuId", oneRoom.getCreateUserId()); 
		outJsonObject.put("roomId", oneRoom.getRoomNumber());//房间人数
		outJsonObject.put("roleNum", oneRoom.getTotalUser());//房间人数
		outJsonObject.put("quanNum", oneRoom.getTotal());//房间圈数
		outJsonObject.put("zhuangDir", zhuangDir);
		outJsonObject.put("fengdingNum", oneRoom.getFengDingNum());//房间封顶数
		outJsonObject.put("daiyitouting", oneRoom.isYiTouTing());//是否一头听
		outJsonObject.put("daidianpao", oneRoom.isDianPao());//是否点炮
		outJsonObject.put("daichikaimen", oneRoom.isChiKaiMen());//是否吃开门
		outJsonObject.put("daiqiduihun", oneRoom.isHunQiDui());//是否混七对
		outJsonObject.put("daiqiduichun", oneRoom.isChunQiDui());//是否纯七对
		outJsonObject.put("daipiaohun", oneRoom.isHunPiao());//是否混飘
		outJsonObject.put("daipiaochun", oneRoom.isChunPiao());//是否纯飘
		outJsonObject.put("daishoulouzi", oneRoom.isShouLouZi());//是否手搂子
		outJsonObject.put("daiqingyise", oneRoom.isQingYiSe());//是否清一色
		outJsonObject.put("daiguodan", oneRoom.isGuoDan());//是否过蛋	
//		outJsonObject.put("zhanhu", oneRoom.isZhanHu());//是否站胡
		outJsonObject.put("elseCards", 0);	
		outJsonObject.put("quanCurrent", oneRoom.getAlreadyTotalGame()); // 当前圈数
		outJsonObject.put("timeForRoomBuild", DateUtils.getDateByFormat(oneRoom.getCreateDate())); // 当前圈数// 当前圈数
		if(game==null){
			outJsonObject.put("timeForGameStart", DateUtils.getDateByFormat(new Date()));
			outJsonObject.put("timeForGameFinish", "0");
		}else{
			outJsonObject.put("timeForGameStart", DateUtils.getDateByFormat(game.getStartDate())); 
			outJsonObject.put("timeForGameFinish", DateUtils.getDateByFormat(game.getEndDate()));  
		}
		if(user2!=null){
			outJsonObject.put("roleDir", TypeUtils.getUserDir(user2.getDirection())); //玩家在房间内的方向
		}else{
			outJsonObject.put("roleDir", 0); //玩家在房间内的方向
		}
		outJsonObject.put("gamgState", "fapaiPre");
	}


	
	/**通知玩家选跑
	 * @param oneRoom
	 */
	public static void notifyUserXuanPao(OneRoom oneRoom) {
		JSONObject xuanPao = new JSONObject();
		xuanPao.put(method, "xpao");
		xuanPao.put("canXp", true);
		List<ChannelHandlerContext> userIoSessionList = oneRoom.getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, xuanPao);
	}
	
	
	
	

	/**
	 * 得到准备jsonObject
	 * 
	 * @param user
	 * @param oneRoom 
	 * @return
	 */
	public static JSONObject getReadyJsonObject(User user, OneRoom oneRoom) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
		outJsonObject.put("ready", true);
		outJsonObject.put("m", "prepare");
		outJsonObject.put("discription", "准备游戏");
		return outJsonObject;
	}

	/**
	 * 开始游戏
	 * @param oneRoom
	 */
	public void startGame(OneRoom oneRoom, ChannelHandlerContext session) {
		boolean deductionBankUserCard = true;
		if (!oneRoom.isPay()) {
			// 扣除房卡
			deductionBankUserCard = deductionBankUserCard(oneRoom);
			oneRoom.setPay(true);
		}
		if (deductionBankUserCard) {
			beginGame(oneRoom);// 开始游戏
		}
	}

	/**
	 * 扣除房主的房卡,如果是4圈扣除6张,其他局扣除3张
	 * 
	 * @param oneRoom
	 */
	private boolean deductionBankUserCard(OneRoom oneRoom) {
		User createUser = oneRoom.getCreateUser();
		int total = oneRoom.getTotal();
		boolean canCreateRoom = checkUserCanCreateRoom(createUser, total);
		if (!canCreateRoom) {// 房主房卡不足解散房间
			realDisbandRoom(createUser, oneRoom.getRoomNumber(), oneRoom);
			return false;
		}
		int userCard = 0;
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", oneRoom.getRoomNumber());// 房间号
		map.put("userId", oneRoom.getCreateUserId());// 用户id
		map.put("totalGame", oneRoom.getTotal());// 总局数
		map.put("totalUser", oneRoom.getTotalUser());// 房间人数
		map.put("type", 1);// 充值0,消费 1
		map.put("total", 0);// 充值的房卡数量
		userCard = roomDao.userConsumeCard(map);
/*		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "getUserCard");
		outJsonObject.put(discription, "剩余的房卡数");
		outJsonObject.put("userCard", userCard);
		notifyUserInfo(outJsonObject, createUser.getIoSession());*/
		try {
			// 更改缓存里面用户的房卡数
			Map<String, String> hashMap = RedisUtil.getHashMap("uid"+createUser.getId(),REDIS_DB_FENGTIAN);
			String userInfo = hashMap.get("baseInfo");
			if (!StringUtils.isNullOrEmpty(userInfo)) {
				JSONObject userInfoJson = new JSONObject(userInfo);
				userInfoJson.put("roomCard", userCard);
				hashMap.put("baseInfo", userInfoJson.toString());
				RedisUtil.setHashMap("uid"+createUser.getId(), hashMap,REDIS_DB_FENGTIAN,TIME_TO_USER_ROOM);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
		}
		return true;
	}

	/**
	 * 通知用户的房卡信息
	 * 
	 * @param outJsonObject
	 * @param ctx
	 */
	private void notifyUserInfo(JSONObject outJsonObject, ChannelHandlerContext ctx) {
		ctx.write(outJsonObject.toString());
	}

	/**
	 * 开始游戏
	 * 
	 * @param oneRoom
	 */
	public void beginGame(OneRoom oneRoom) {
		Game game = getGame(oneRoom);
		dealCard(oneRoom, game);
	}

	/**定东风
	 * @param game
	 */
	private void dingDongFeng(Game game) {
		// 定东风
//		System.out.println("12");
		int dongFengCard = MathUtil.getDongFeng();
		String eastDirection = getEastWithDongFengNumber(dongFengCard);
			setGameSeat(game, eastDirection);
		JSONObject dongFengJsonObject =  new JSONObject();
		dongFengJsonObject.put("discription", "定东风");
		dongFengJsonObject.put("method", "setUserDirection");
		dongFengJsonObject.put("dirEastId", dongFengCard);
		JSONArray userDir =  new JSONArray();
		List<User> userlist = game.getUserList();
		for(int i=0;i<userlist.size();i++){
			User user = userlist.get(i);
			String direction = user.getDirection();
			int userid = user.getId();
			JSONObject userJson =  new JSONObject();
			userJson.put("userId", userid);
			userJson.put("direction", direction);
			userDir.put(userJson);
		}
		dongFengJsonObject.put("userDir", userDir);
		NotifyTool.notifyIoSessionList(game.getIoSessionList(), dongFengJsonObject);
	}

	private void setGameSeat(Game game, String eastDirection) {
		User eastUser = game.getGamingUser("east");
		User northUser = game.getGamingUser("north");
		User westUser = game.getGamingUser("west");
		User southUser = game.getGamingUser("south");
		Map<String, User> seatMap = game.getSeatMap();
		switch (eastDirection) {
		case "east":
			eastUser.setBanker(true);
			break;
		case "north":
			northUser.setDirection("east");
			westUser.setDirection("north");
			southUser.setDirection("west");
			eastUser.setDirection("south");
			northUser.setBanker(true);
			seatMap.put("east", northUser);
			seatMap.put("north", westUser);
			seatMap.put("west", southUser);
			seatMap.put("south", eastUser);
			break;
		case "west":
			westUser.setBanker(true);
			westUser.setDirection("east");
			southUser.setDirection("north");
			eastUser.setDirection("west");
			northUser.setDirection("south");
			seatMap.put("east", westUser);
			seatMap.put("north", southUser);
			seatMap.put("west", eastUser);
			seatMap.put("south", northUser);
			break;
		case "south":
			southUser.setBanker(true);
			southUser.setDirection("east");
			eastUser.setDirection("north");
			northUser.setDirection("west");
			westUser.setDirection("south");
			seatMap.put("east", southUser);
			seatMap.put("north", eastUser);
			seatMap.put("west", northUser);
			seatMap.put("south", westUser);
			break;
		}
	}

	public String getEastWithDongFengNumber(int dongFengCard) {
		String direction = "";
		switch (dongFengCard) {
		// 自已为东
		case 5:
		case 9:
			direction = "east";
			break;
		// 下家为东
		case 2:
		case 6:
		case 10:
			direction = "north";
			break;
		// 对家为东
		case 3:
		case 7:
		case 11:
			direction = "west";
			break;
		// 上家为东
		case 4:
		case 8:
		case 12:
			direction = "south";
			break;
		}
		return direction;
	}

	/**
	 * 发牌
	 * @param oneRoom
	 * @param game
	 */
	public void dealCard(OneRoom oneRoom, Game game) {
		game.setGameStatus(1);// 游戏的状态
		game.setStatus(GAGME_STATUS_OF_IS_GAMING);// 游戏进行中
		game.setStartDate(new Date());
		game.setYaoPaiStatus(2);
		game.playGame();
		String nowDirection = getFirstDrection(oneRoom);// 得到第一次出牌的方向,也就是庄家的方向
		User bankUser = game.getSeatMap().get(nowDirection);
		List<Integer> bankCards = bankUser.getCards();
		HuPai huPai = new HuPai();
		huPai.isHu(bankCards,bankUser.getPengCards(),bankUser.getGangCards(),bankUser.getChiCards(),bankUser.getBaiFengGangCards(),bankUser.getHeiFengGangCards(),oneRoom);
		boolean win = huPai.isHu();
		notifyUserStartGame(game, win);// 通知用户开启游戏,并且设置游戏的状态
		notifyUserFaPai(game);
		// 设置用户的的准备状态为未准备
		setUserReadyFalse(oneRoom);
		int size = bankCards.size();
		Integer removeCard  = bankCards.get(size - 1);
		game.setDirec(bankUser.getDirection());
		int total = 0;
		if (win) {// 如果天胡		
			bankUser.setCanWin(true);
			game.setCanWin(true);
			game.setCanHuUser(bankUser);
			total++;
		}
		//判断是否能听牌
		boolean checkUserIsCanTing = PlayGameService.checkUserIsCanTing(bankUser, game);
		if(checkUserIsCanTing){
			bankUser.setCanTing(true);
			game.setCanTingUser(bankUser);
			total++;
		}
		List<List<Integer>> xuanFengGang = playGameService.analysisUserIsCanXuanFengGang(bankUser);
		if(xuanFengGang.size()>0){
			//可以旋风杠
			game.setCanXuanFengGangUser(bankUser);
			for (List<Integer> xuan : xuanFengGang) {
				if(xuan.size()==3){
					bankUser.setCanBaiFeng(true);
					game.setXuanFengBai(true);
					
				}else if(xuan.size()==4){
					bankUser.setCanHeiFeng(true);
					game.setXuanFengHei(true);
				}
				System.out.println(xuan);
			}
			total++;
		}	
		List<Integer> canAnGang = PlayGameService.isUserCanAnGang(bankUser);
		if(canAnGang.size()>0){//判断是否能暗杠
			bankUser.setCanAnGang(true);
			game.setAnGangCards(canAnGang);
			game.setCanAnGangUser(bankUser);
			game.setCanAnGang(true);
			total++;
		}
		if(total>0){
			PlayGameService.notifyUserZhaPaiTip(game,bankUser,removeCard);
		}else{
			PlayGameService.notifyUserChuPai(bankUser,game);
			game.getGameStatusMap().put(game.getAlreadyTotalGame() + 1, "START");
		}
		
	}


	/**
	 * 通知用户发牌
	 * {"m":"paiFa","array":{1,1,1,1,1,1,1,1,1,1},"userDir":0}
	 * @param game
	 */
	private void notifyUserFaPai(Game game) {
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		for (User user : userList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("m", "paiFa");
			jsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			jsonObject.put("array", user.getCards());
			NotifyTool.notify(user.getIoSession(), jsonObject);
		}
	}

	/**
	 * 得到游戏如果是第一句则创建游戏，否则从GameManager中获取
	 * 
	 * @param roomId
	 * @return
	 */
	private Game getGame(OneRoom oneRoom) {
		Game game = GameManager.getGameWithRoomNumber(oneRoom.getId() + "");
		if (game != null) {
			return game;
		} else {
			game = new Game(oneRoom);// 创建一个游戏
			GameManager.addGameMap(oneRoom.getId() + "", game);// 用
																// gameMap管理这个游戏
			return game;
		}
	}

	/**
	 * 得到庄家是不是托管
	 * 
	 * @param oneRoom
	 * @return
	 */
	public boolean getBankUserIsAuto(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.isBanker()) {
				user.setUserCanPlay(true);
				return user.isAuto();
			}
		}
		return false;
	}

	/**
	 * 通知用户开启游戏
	 * 
	 * @param game
	 * @param isWin
	 *            
	 */
	public static void notifyUserStartGame(Game game, boolean isWin) {
		OneRoom oneRoom = game.getRoom();
		List<User> userList = oneRoom.getUserList();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "gameStart");
		int alreadyTotalGame = oneRoom.getAlreadyTotalGame()+1;
		outJsonObject.put("juCur", alreadyTotalGame);
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if(user.isBanker()){
				outJsonObject.put("zhuangDir", TypeUtils.getUserDir(user.getDirection()));
				break;
			}
		}
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
	}



	/**
	 * 设置房间里的人的准备状态为未准备
	 * 
	 * @param oneRoom
	 */
	public void setUserReadyFalse(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (User user : userList) {
			user.setReady(false);
		}
	}


	/**
	 * @param oneRoom
	 * @return
	 */
	public static String getFirstDrection(OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.isBanker()) {
				return user.getDirection();
			}
		}
		return "east";
	}

	/**
	 * 通知其他玩家
	 * 
	 * @param userList
	 * @return
	 */
	private int getTotalReady(List<User> userList) {
		int totalReady = 0;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.isReady() == true) {
				totalReady++;
			}
		}
		return totalReady;
	}

	/**
	 * 得到我自己的信息
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public void getMyInfo(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		if (roomId == null || "".equals(roomId)) {
			ctx.write("啥都没有");
			return;
		}
		Map<String, Game> gameMap = GameManager.getGameMap();
		Game game = gameMap.get(roomId);
		Map<String, User> seatMap = game.getSeatMap();
		User user2 = seatMap.get(user.getDirection());
		List<Integer> cards1 = user.getCards();
		List<Integer> cards2 = user2.getCards();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("sessionCards", cards1);
		outJsonObject.put("GameManagerCards", cards2);
		ctx.write(outJsonObject.toString());

		StringBuffer sb = new StringBuffer("  ");
		for (int i = 0; i < cards1.size(); i++) {
			Integer card = cards1.get(i);
			String cardType = CardsMap.getCardType(card);
			sb.append(cardType + "");
		}

		StringBuffer sb2 = new StringBuffer("  ");
		for (int i = 0; i < cards2.size(); i++) {
			Integer card = cards1.get(i);
			String cardType = CardsMap.getCardType(card);
			sb2.append(cardType + "");
		}
		ctx.write("  sessionCards:" + sb + "   GameManagerCards:" + sb2);
	}

	/**
	 * 离开房间
	 * @param jsonObject
	 * @param ctx
	 */
	public void leaveRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		leave(user, ctx);
	}

	private void leave(User user, ChannelHandlerContext session) {
		OneRoom room = getUserRoom(user);
		if (room == null) {
			// FIXME
			// 这里需要封装成解散房间的算法-------------------------------------------------
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(method, "disbandRoom");
			jsonObject.put(code, "success");
			jsonObject.put(discription, "离开房间成功");
			NotifyTool.notify(user.getIoSession(), jsonObject);
			return;
		}
		
		boolean use = room.isUse();
		if(use){
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put(method, "requestJiesan");
			outJsonObject.put("userId", user.getId());
			outJsonObject.put(discription,"申请解散房间");
			List<User> userList = room.getUserList();
			for(int i=0;i<userList.size();i++){
				User u = userList.get(i);
				u.setIsAgreeDisbandType(0);
			}
			NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), outJsonObject);
//			MessageService.waitJieSan(room.getId()+"");
			return;
		}
		Set<String> directionSet = room.getDirectionSet();
		directionSet.remove(user.getDirection());
		notifyUserLeaveRoom(room, user,0);
		user.setRoomId(null);
		room.userLeaveRoom(user);// 用户离开房间
		user.setDirection(null);
		user.setReady(false);
		RedisUtil.delKey("usRoomId"+user.getId(),REDIS_DB_FENGTIAN);
		if (session != null) {
			session.close();
		}
	}

	/**
	 * 用户离开房间
	 */
	public void leaveRoom(User user) {
		leave(user, null);
	}

	/**
	 * 得到当前游戏玩家的方向
	 * 
	 * @param user
	 * @return
	 */
	public OneRoom getUserRoom(User user) {
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId);
		return oneRoom;
	}

	/**
	 * 通知房间的玩家离开游戏
	 * 
	 * @param room
	 *            当前的房间
	 * @param user
	 *            离开的玩家
	 * @param userDir 
	 */
	private void notifyUserLeaveRoom(OneRoom room, User user, int userDir) {
		List<User> userList = room.getUserList();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "tcRoom");
		outJsonObject.put("isSuccess", true);
		outJsonObject.put("userDir", userDir);
		outJsonObject.put("discription", "玩家:"+user.getNickName()+"退出房间");
		for (User u : userList) {
			ChannelHandlerContext ioSession = u.getIoSession();
			NotifyTool.notify(ioSession, outJsonObject);
		}
	}

	/**
	 * 房主解散房间
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	@SuppressWarnings("deprecation")
	public void disbandRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		disband(ctx, user);
	}

	private void disband(ChannelHandlerContext session, User user) {
		int roomId = Integer.parseInt(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
		// 检测房间是否存在
		if (oneRoom == null) {
			return;
		}
		if (oneRoom.isUse()) {
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put(discription, "游戏已开始，开心的玩吧");
			outJSONbject.put(method, "disbandRoom");
			outJSONbject.put(code, error);
			NotifyTool.notify(session, outJSONbject);
			;
			return;
		}
		int createUserId = oneRoom.getCreateUserId();
		if (createUserId != user.getId()) {
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put(discription, "只有房主可以解散房间");
			outJSONbject.put(method, "disbandRoom");
			outJSONbject.put(code, error);
			NotifyTool.notify(session, outJSONbject);
			return;
		}

		boolean disband = oneRoom.isDisband();
		if (!disband) {
			realDisbandRoom(user, roomId, oneRoom);
		}
	}

	/**
	 * 解散房间
	 * 
	 * @param user
	 *            房主
	 * @param roomId
	 * @param oneRoom
	 */
	public static void realDisbandRoom(User user, int roomId, OneRoom oneRoom) {
		oneRoom.setDisband(true);// 房间解散
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			u.setReady(false);// 不准备
			u.setAuto(false);// 不托管
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put("m", "jsRoom");
			outJSONbject.put("isSuccess", true);
			outJSONbject.put("discription", "房间解散不扣除房卡");
			outJSONbject.put("userDir", TypeUtils.getUserDir(u.getDirection()));

			ChannelHandlerContext ioSession = u.getIoSession();
			NotifyTool.notify(ioSession, outJSONbject);
		}
		// 从RoomManager中移除房间
		RoomManager.removeOneRoomByRoomId(roomId + "");
		user.setRoomId("");
		user.setBanker(false);
		// redis移除房间信息
		RedisUtil.delKey("ftRoomId"+roomId,REDIS_DB_FENGTIAN);
	}
	/**
	 * 解散房间
	 * 
	 * @param user
	 *            房主
	 * @param roomId
	 * @param oneRoom
	 */
	public static void daiLiRealDisbandRoom(User user, int roomId, OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			u.setReady(false);// 不准备
			u.setAuto(false);// 不托管
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put("m", "jsRoom");
			outJSONbject.put("isSuccess", true);
			outJSONbject.put("discription", "房间解散不扣除房卡");
			outJSONbject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
			ChannelHandlerContext ioSession = u.getIoSession();
			NotifyTool.notify(ioSession, outJSONbject);
		}
		oneRoom.setDisband(true);// 房间解散
		RoomManager.removeOneRoomByRoomId(roomId + "");
		user.setRoomId("");
		user.setBanker(false);
		// redis移除房间信息
		RedisUtil.delKey("ftRoomId"+roomId,REDIS_DB_FENGTIAN);
	}
	/**
	 * 继续游戏
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public void continueGame(JSONObject jsonObject, ChannelHandlerContext ctx) {
		//{"m":"prepare","ready":true,"userDir":0,"description":"准备游戏"}
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		Game game = PlayGameService.getGame(user);
		OneRoom oneRoom = RoomManager.getRoomMap().get(roomId);
		List<User> userList = oneRoom.getUserList();
		for (User user2 : userList) {
			if(user2.getId()==user.getId()){
				user2.setReady(true);
				JSONObject outJSONObject = new JSONObject();
				outJSONObject.put("m", "prepare");
				outJSONObject.put("ready", user2.isReady());
				outJSONObject.put("userDir", TypeUtils.getUserDir(user2.getDirection()));
				outJSONObject.put("description", "准备游戏");
				NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJSONObject);
				break;
			}
		}
		int totalReady = getTotalReady(userList);
		if (totalReady == oneRoom.getTotalUser()) {// 开始游戏
			game = getGame(oneRoom);
			startGame(oneRoom, ctx);
			return;
		}
	}
	/**
	 * 设置托管
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public void settingAuto(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		// 如果游戏已经开始，则设置游戏里面的用户托管，否则设置,房间里的用户托管
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		if (game == null) {
			return;
		}
		User gamingUser = null;
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()==user.getId()){
				gamingUser = u;
			}
		}
		if(gamingUser == null){
			return;
		}
		boolean auto = gamingUser.isAuto();
		if (!auto) {
			setGamingUserAuto(game, gamingUser);
		}
		JSONObject outJsonObject = getAutoJsonObject(gamingUser);
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		// 如果房间为空，说明已经离开房间了
		if (oneRoom == null) {
			ctx.write(outJsonObject);
			return;
		}
		oneRoom.noticeUsersWithJsonObject(outJsonObject);
		// 如果当前游戏的方向移动到该玩家的方向,则自动替他出牌,或杠牌,或碰牌
		palyIfTheGameDirectionIsMyDirection(gamingUser);
	}

	/**
	 * 得到托管的返回数据
	 * 
	 * @param user
	 * @return
	 */
	public static JSONObject getAutoJsonObject(User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "settingAuto");
		outJsonObject.put(discription, "设置托管成功");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		return outJsonObject;
	}

	/**
	 * 设置游戏中的玩家准备
	 * 
	 * @param game
	 * @param user
	 */
	private void setGamingUserAuto(Game game, User user) {
		Map<String, User> seatMap = game.getSeatMap();
		if (seatMap != null) {
			User seatMapUser = seatMap.get(user.getDirection());
			seatMapUser.setAuto(true);
			user.setAuto(true);
		}
	}

	/**
	 * 设置房间里的玩家是否托管
	 * 
	 * @param user
	 * @param status
	 *            true准备,false取消准备
	 */
	private void setRoomUserStatus(User user, boolean status) {
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		if (oneRoom == null) {
			return;
		}
		List<User> userList = oneRoom.getUserList();
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			if (u.getId() == user.getId()) {
				u.setAuto(status);
				user.setAuto(status);
				user.setTotalNotPlay(0);// 用户没有出牌的次数清零
			}
		}
	}

	/**
	 * 如果当前游戏的方向移动到该玩家的方向,则自动替他出牌,或杠牌,或碰牌
	 * 
	 * @param user
	 */
	private void palyIfTheGameDirectionIsMyDirection(User user) {
		Game game = PlayGameService.getGame(user);
		if (game != null) {
			String gameDirection = game.getDirec();// 游戏的方向
			if (gameDirection != null && gameDirection.equals(user.getDirection())) {
				int status = game.getGameStatus();
				switch (status) {
				case GAGME_STATUS_OF_CHUPAI:// 出牌
//					PlayGameService.autoChuPai(game);// 出牌s
					break;
				case GAGME_STATUS_OF_PENGPAI:// 碰牌
				case GAGME_STATUS_OF_GANGPAI:// 杠牌
					PlayGameService.autoPengOrGang(user, game);
					break;
				case GAGME_STATUS_OF_ANGANG:// 暗杠
					PlayGameService.autoAnGang(game);
					break;
				case GAGME_STATUS_OF_GONG_GANG:// 公杠
					PlayGameService.autoGongGang(game);
					break;
				}
			}
		}
	}

	/**
	 * 取消托管
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	public void cancelAuto(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		setRoomUserStatus(user, false);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "cancelAuto");
		outJsonObject.put(discription, "取消托管成功");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		OneRoom oneRoom = RoomManager.getRoomMap().get(user.getRoomId());
		// 防止用户卡死在房间里面点击托管
		if (oneRoom == null) {
			ctx.write(outJsonObject);
			return;
		}
		oneRoom.noticeUsersWithJsonObject(outJsonObject);
	}

	@Test
	public void test() {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "cancelAuto");
		outJsonObject.put(discription, "设置托管成功");
		outJsonObject.put("userId", 1);
		outJsonObject.put(direction, "east");
		System.out.println(outJsonObject);
	}

	@Override
	public void playGame(JSONObject jsonObject, ChannelHandlerContext session) {
		playGameService.playGame(jsonObject, session);
	}
	/**
	 * 断线重连
	 */
	@Override
	public void downGameInfoWithUnionid(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int ui = jsonObject.getInt("userId");
		if (user == null) {// 用户还没有登录
			Map<String, String> hashMap = RedisUtil.getHashMap("uid"+ui,REDIS_DB_FENGTIAN);
			String userInfo = hashMap.get("baseInfo");
			if (!StringUtils.isNullOrEmpty(userInfo)) {
				User infoUser = getUserFromUserInfo(userInfo, ui+"");
				if (infoUser != null) {
					user = infoUser;
					user.setRoomId(hashMap.get("roomId"));
				}
			} else {
				user = new User();
				user.setId(ui);
				user = userDao.findUser2(user);
			}
			if (user != null) {
				ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(user);;
				user.setIoSession(ctx);
				dateService.addLoginUser();
			}
		}
		if (user == null) {
			// session.close();
			return;
		}
		
		User user3 = new User();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		List<User> userList = oneRoom.getUserList();
		for (User user2 : userList) {
			if(user2.getId()==user.getId()){
				user3 = user2;
			}
		}
		replaceUserIoSession(user, oneRoom);
		JSONObject roomInfo = getRoomInfo(user3);// 得到房间信息
		if (roomInfo == null) {// 游戏还没开始
			// 得到房间里的用户信息
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("m", "reConnect");
			outJsonObject.put("isSuccess", true);
			outJsonObject.put("description", "描述");
			getRoomInfo(outJsonObject, oneRoom,user);
			ctx.write(outJsonObject.toString());
			userDropTip(user, oneRoom);
		} else {
			nofiyUserRoomInfo(roomInfo, ctx);
			userDropTip(user, oneRoom);
			Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
			int gameStatus = game.getStatus();
			if(gameStatus==GAGME_STATUS_OF_WAIT_START){
				String roomId = user.getRoomId();
				game = PlayGameService.getGame(user);
				OneRoom oneRoom1 = RoomManager.getRoomMap().get(roomId);
				List<User> userList1 = oneRoom1.getUserList();
				for (User user2 : userList1) {
					if(user2.getId()==user.getId()){
						user2.setReady(true);
						JSONObject outJSONObject = new JSONObject();
						outJSONObject.put("m", "prepare");
						outJSONObject.put("ready", user2.isReady());
						outJSONObject.put("userDir", TypeUtils.getUserDir(user2.getDirection()));
						outJSONObject.put("description", "准备游戏");
						NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJSONObject);
						break;
					}
				}
				UserService userService = new UserService();
				int totalReady = userService.getTotalReady(userList);
				if (totalReady == oneRoom.getTotalUser()) {// 开始游戏
					game = userService.getGame(oneRoom);
					userService.startGame(oneRoom, null);
				}
			}
			
		}
		
	}
	/**
	 * 用户掉线提示
	 * @param user
	 * @param oneRoom
	 */
	private void userDropTip(User user, OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		boolean isDropLine = false;
		for (int i = 0; i < userList.size(); i++) {
			User u = userList.get(i);
			ChannelHandlerContext newIoSession = user.getIoSession();
			ChannelHandlerContext oldIoSession = u.getIoSession();
			if (oldIoSession.hashCode() != newIoSession.hashCode()) {
				isDropLine = true;
			}
		}
		if (isDropLine) {//掉线
			JSONObject dropLine = new JSONObject();
			dropLine.put("m", "drop");
			dropLine.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			dropLine.put("type", 0);
			for (int i = 0; i < userList.size(); i++) {
				User u = userList.get(i);
				u.getIoSession().write(dropLine.toString());
			}
		}
	}

	@Override
	public void downGameInfo(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		JSONObject roomInfo = getRoomInfo(user);
		if (roomInfo == null) {
			// 修改玩家的房间号
			User modifyUser = new User();
			modifyUser.setId(user.getId());
			modifyUser.setRoomId("0");
			userDao.modifyUser(user.getId(),"0");// 记录下用户的房间号
			logger.info("在房间信息为空的时候修改用户的房间号:" + user.getRoomId());
			user.setRoomId("0");// 用户的ID置空
			return;
		}
		nofiyUserRoomInfo(roomInfo, ctx);
		// Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		replaceUserIoSession(user, oneRoom);
	}

	@Override
	public void disbandRoom(User user) {
		disband(user.getIoSession(), user);
	}
	/**
	 * 解散房间
	 */
	@Override
	public void jsRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String userDir = null;
		int uDir = 0;
		if(jsonObject.has("userDir")){
			uDir = jsonObject.getInt("userDir");
			userDir = TypeUtils.getStringDir(uDir);
		}
		if(userDir!=null){//没有得到方向
			User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
			int roomId = Integer.parseInt(user.getRoomId());
			OneRoom oneRoom = RoomManager.getRoomMap().get(roomId + "");
			ChannelHandlerContext session = user.getIoSession();
			// 检测房间是否存在
			if (oneRoom == null) {
				logger.info("房间不存在");
				return;
			}
			if (oneRoom.isUse()) {
				JSONObject outJSONbject = new JSONObject();
				outJSONbject.put("discription", "游戏已开始，开心的玩吧");
				outJSONbject.put("m", "jsRoom");
				outJSONbject.put("userDir", uDir);
				outJSONbject.put("isSuccess", false);
				NotifyTool.notify(session, outJSONbject);
				return;
			}
			int createUserId = oneRoom.getCreateUserId();
			if (createUserId != user.getId()) {
				JSONObject outJSONbject = new JSONObject();
				outJSONbject.put("discription", "只有房主可以解散房间");
				outJSONbject.put("m", "jsRoom");
				outJSONbject.put("userDir", uDir);
				outJSONbject.put("isSuccess", false);
				NotifyTool.notify(session, outJSONbject);
				return;
			}

			boolean disband = oneRoom.isDisband();
			if (!disband) {
//				realDisbandRoom(user, roomId, oneRoom);
				realJsRoom(user,roomId,oneRoom);
			}
		}
	}
	/**
	 * 真的去解散房间
	 * @param user
	 * @param roomId
	 * @param oneRoom
	 */
	private void realJsRoom(User user, int roomId, OneRoom oneRoom) {
		oneRoom.setDisband(true);//房间解散
		List<User> userList = oneRoom.getUserList();
		for (User u : userList) {
			u.setReady(false);// 不准备
			u.setAuto(false);// 不托管
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put("m", "jsRoom");
			outJSONbject.put("isSuccess", true);
			outJSONbject.put("discription", "解散房间成功");
			outJSONbject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
			NotifyTool.notify(u.getIoSession(), outJSONbject);
		}
		// 从RoomManager中移除房间
		RoomManager.removeOneRoomByRoomId(roomId + "");
		user.setRoomId("");
		user.setBanker(false);
		user.setFangZhu(false);
		user.setDirection(null);
		// redis移除房间信息
		RedisUtil.delKey("ftRoomId"+roomId,REDIS_DB_FENGTIAN);
	}

	/**
	 * 退出房间
	 */
	@Override
	public void tcRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		OneRoom room = getUserRoom(user);
		int userDir = jsonObject.getInt("userDir");
		if (room == null) {
			// FIXME
			// 这里需要封装成解散房间的算法-------------------------------------------------
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("m", "tcRoom");
			outJsonObject.put("isSuccess", false);
			outJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			jsonObject.put("discription", "离开房间失败,房间不存在");
			NotifyTool.notify(user.getIoSession(), jsonObject);
			return;
		}
		Game game = GameManager.getGameWithRoomNumber(room.getId()+"");
		if(game==null||game.getStatus()==GAGME_STATUS_OF_IS_GAMING||room.getAlreadyTotalGame()>0){
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("m", "tcRoom");
			outJsonObject.put("isSuccess", false);
			outJsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			jsonObject.put("discription", "离开房间失败,游戏已经开始");
			NotifyTool.notify(user.getIoSession(), jsonObject);
			return;
		}
		tuiChuRoom(ctx, user, room, userDir);
	}
	/**
	 * 心跳检测
	 */
	@Override
	public void checkHeart(JSONObject jsonObject, ChannelHandlerContext ctx) {
//		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		if(jsonObject.has("userDir")){
			ctx.write(jsonObject.toString());
		}
		
	}


	/**
	 * 播放音效
	 * {"m":"playSound","userDir":0"soundId":0}
	 */
	@Override
	public void playSound(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();		
	    OneRoom room = RoomManager.getRoomWithRoomId(user.getRoomId());	
		NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), jsonObject);
	}

	@Override
	public void tcORjsORsq(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		int userDir = jsonObject.getInt("userDir");
		ChannelHandlerContext session = user.getIoSession();
		if(oneRoom==null){
			//房间不存在
//			logger.info("房间不存在");
			JSONObject outJSONbject = new JSONObject();
			outJSONbject.put("discription", "房间不存在");
			outJSONbject.put("m", "jsRoom");
			outJSONbject.put("userDir", userDir);
			outJSONbject.put("isSuccess", false);
			NotifyTool.notify(session, outJSONbject);
			return;
		}
		if (game != null) {// 游戏已经开始
			//申请解散
			shenQingJieSan(user, game, oneRoom, userDir);
		}else{
			//游戏没有开始
			//判断是否是房主  
			if(oneRoom.getCreateUserId()==user.getId()){
				//是房主 解散房间
				boolean disband = oneRoom.isDisband();
				if (!disband) {
					realJsRoom(user,oneRoom.getId(),oneRoom);
				}
			}else{
				//不是房主  退出房间
				tuiChuRoom(ctx, user, oneRoom, userDir);
			}
		}
	}
	/**
	 * 玩家申请解散
	 * @param user
	 * @param game
	 * @param oneRoom
	 * @param userDir
	 */
	private void shenQingJieSan(User user, Game game, OneRoom oneRoom,
			int userDir) {
		User gamingUser = game.getGamingUser(user.getDirection());
		int totalRequetDisbanRoom = gamingUser.getTotalRequetDisbanRoom();
		gamingUser.setTotalRequetDisbanRoom(totalRequetDisbanRoom+1);;
		gamingUser.setIsAgreeDisbandType(1);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "sqRoom");
		outJsonObject.put("isSuccess", true);
		outJsonObject.put("discription", "投票申请");
		outJsonObject.put("userDir", TypeUtils.getUserDir(gamingUser.getDirection()));
		List<User> userList = oneRoom.getUserList();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()!=gamingUser.getId()){
				u.setIsAgreeDisbandType(0);
			}
		}
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		MessageService.waitJieSan(oneRoom.getId()+"",userDir);
	}
	/**
	 * 玩家退出房间
	 * @param ctx
	 * @param user
	 * @param oneRoom
	 * @param userDir
	 */
	private void tuiChuRoom(ChannelHandlerContext ctx, User user,
			OneRoom oneRoom, int userDir) {
		Set<String> directionSet = oneRoom.getDirectionSet();
		directionSet.remove(user.getDirection());
		notifyUserLeaveRoom(oneRoom, user,userDir);
		user.setRoomId(null);
		oneRoom.userLeaveRoom(user);// 用户离开房间
		user.setDirection(null);
		user.setReady(false);
		RedisUtil.delKey("usRoomId"+user.getId(),REDIS_DB_FENGTIAN);
/*		if (ctx != null) {
			ctx.close();
		}*/
	}
	/**
	 * 代开房间(奉天)  分模式土豪模式 普通模式
	 */
	@Override
	public void dkRoom(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		//
		
	}
	/**
	 * 得到公告信息
	 */
	@Override
	public void getSysMsg(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String redisMessage = RedisUtil.getKey("message",REDIS_DB_FENGTIAN);
		if(redisMessage!=null){
			ctx.write(redisMessage);
		}else{
			Map<String, Object> map = new HashMap<>();
			map.put("type", 0);
			map.put("rowStart", 0);
			map.put("pageSize", 1);
			MessageDao messageDao = new MessageDao();
			List<Message> list = messageDao.selectListByMap(map);
			if(list.size()>0){
				Message message = list.get(0);
				JSONObject outJsonObejct = new JSONObject();
				outJsonObejct.put("m", "getSysMsg");
//				outJsonObejct.put(type, "getBuyCardMessage");
				outJsonObejct.put("message", message.getMessage());
//				outJsonObejct.put("createDate", DateUtils.getFormatDate(message.getCreateDate(), "yyyy/MM/dd hh:mm:ss"));
				ctx.write(outJsonObejct);
				RedisUtil.setKey("message", outJsonObejct.toString(),REDIS_DB_FENGTIAN);
			}
		}
	}
	
	/**
	 * 代理解散或退出
	 */
	@Override
	public void DLjcORsq(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int userDir = jsonObject.getInt("userDir");
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		if(game!=null){//游戏已经开始
			shenQingJieSan(user, game, oneRoom, userDir);
		}else{//游戏未开始
			tuiChuRoom(ctx, user, oneRoom, userDir);
		}
	}
	/**
	 * gps
	 */
	@Override
	public void sendGps(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int userDir = jsonObject.getInt("userDir");
		double jingdu = jsonObject.getDouble("jingdu");
		double weidu = jsonObject.getDouble("weidu");
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		if(oneRoom!=null){//房间存在
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put("m", "gpsInfo");
			outJsonObject.put("userDir",userDir);
			JSONArray jsonArray = new JSONArray();
			List<User> list = oneRoom.getUserList();
			
			for (User u : list) {
				if(user.getId()==u.getId()){
					u.setJingdu(jingdu);
					u.setWeidu(weidu);
				}
				JSONObject object = new JSONObject();
				Double jingD = u.getJingdu();
				Double weiD = u.getWeidu();
				object.put("userDir", TypeUtils.getUserDir(u.getDirection()));
				object.put("jingdu", jingD);
				object.put("weidu", weiD);
				jsonArray.put(object);
			}
			outJsonObject.put("users",jsonArray);
			ctx.write(outJsonObject.toString());
		}
	}

}
