package com.zxz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.json.JSONObject;


























































import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.dao.SumScoreDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.dao.VedioDao;
import com.zxz.domain.ChiCard;
import com.zxz.domain.Game;
import com.zxz.domain.GangCard;
import com.zxz.domain.OneRoom;
import com.zxz.domain.PengCard;
import com.zxz.domain.Priority;
import com.zxz.domain.SumScore;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.domain.Vedio;
import com.zxz.domain.XuChangScore;
import com.zxz.domain.XuanFengGangCard;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.DateUtils;
import com.zxz.utils.FengTianHuPai;
import com.zxz.utils.HuPai;
import com.zxz.utils.MathUtil;
import com.zxz.utils.NotifyTool;
import com.zxz.utils.PlayCardUtils;
import com.zxz.utils.RecordScoreThread;
import com.zxz.utils.RecordScoreThreadPool;
import com.zxz.utils.TypeUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PlayGameService extends BaseService implements Constant{

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlayGameService.class); 
	static UserScoreDao userScoreDao = UserScoreDao.getInstance();
	static SumScoreDao sumScoreDao = SumScoreDao.getInstance();
	
	public void playGame(JSONObject jsonObject, ChannelHandlerContext ctx) {
		String type = jsonObject.getString("type");//出牌，杠牌，碰牌,胡牌
		switch (type) {
		case "paiChu":
			chuPai(jsonObject, ctx);//出牌
			break;
		case "chuPeng":
			peng(jsonObject,ctx);//碰牌
			break;
		case "chuChi":
			chi(jsonObject,ctx);//吃牌
			break;
		case "realChi":
			realchi(jsonObject,ctx);//吃牌
			break;
		case "chuGang":   //杠别人的
			gang(jsonObject,ctx);//杠牌  碰杠
			break;
		case "zhualiang":
			xuanFengGang(jsonObject,ctx);//旋风杠牌
			break;
		case "realXuanFengGang":
			realXuanFengGang(jsonObject,ctx);//指定旋风杠牌 牌型
			break;
		case "zhuagangP":
			gongGang(jsonObject,ctx);//公杠  也称 明杠
			break;
		case "zhuagangA":
			anGang(jsonObject,ctx);//暗杠
			break;
		case "chuHu":
			huPai(jsonObject,ctx);//出胡
			break;
		case "zhuahu":
			zhuaHu(jsonObject,ctx);//抓胡
			break;	
		case "guo":
			fangqi(jsonObject,ctx);//不碰也不杠 不吃
			break;
		case "tingFirst":
			tingFirst(jsonObject,ctx);//听牌 第一步
			break;
		case "tingSecond":
			tingSecond(jsonObject,ctx);//听牌 第二步
			break;
		case "zhuating":
			ting(jsonObject,ctx);//听牌 
			break;
		case "changeStatus":
			changeStatus(jsonObject,ctx);//改变听牌的状态
			break;
		}
	}
	/**
	 * 抓胡
	 * {"m":"zhuahu","userDir":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 */
	private void zhuaHu(JSONObject jsonObject, ChannelHandlerContext ctx) {
		int userDir = jsonObject.getInt("userDir");
		int cardId = jsonObject.getInt("cardId");
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(ctx);
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		gamingUser.setCanWin(false);
		gamingUser.clearAllCan();
		userZhuaWin(game,cardId,gamingUser,userDir);
	}
	/**
	 * 用户抓胡
	 * @param game
	 * @param cardId
	 * @param gamingUser
	 * @param userDir
	 */
	void userZhuaWin(Game game, int cardId, User gamingUser, int userDir) {
		//修改玩家的当前分数
		FengTianHuPai fengTianHuPai = game.getRoom().getFengTianHuPai();
		int myHuType = getMyHuType(gamingUser, cardId, fengTianHuPai,game.getRoom().isQingYiSe());
		modifyUserCurrentScore(game,gamingUser,cardId,true,myHuType);
		int roomId = game.getRoom().getId();
		HuiFangUitl.getHuPai(game.getHuiFang(), gamingUser, gamingUser.getCards(),game.getRemainCards());
		String string = game.getHuiFang().toString();
		try {
			RecordScoreThread recordScoreThread = new RecordScoreThread(game,string);
			ExecutorService executorService = RecordScoreThreadPool.getExecutorService();
			executorService.execute(recordScoreThread);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally{
			boolean isHaveNextGame = false;
			if(!isGameOver(game.getRoom())){
				isHaveNextGame = true;
			}
			notifyUserZhuaWin(gamingUser,game,cardId,userDir,myHuType,isHaveNextGame);//通知用户抓赢
			initializeUser(gamingUser);//初始化用户的数据
			boolean gameOver = setCurrentGameOver(game,roomId);//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
			if(gameOver){
				return;
			}
			setNewBank(gamingUser,game);//设置新的庄家
		}
	}
	/**
	 * @param gamingUser
	 * @param game
	 * @param cardId
	 * @param userDir
	 * @param myHuType 
	 */
	public static void notifyUserZhuaWin(User gamingUser, Game game, int cardId,
			int userDir, int myHuType,boolean isHaveNextGame) {
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhuahu");
		outJsonObject.put("userDir",userDir);
		outJsonObject.put("cardId", cardId);
		outJsonObject.put("ishuangzhuang", false);
		outJsonObject.put("isHaveNextGame", isHaveNextGame);
		JSONArray userJsonArray = getUserJSONArray(room,gamingUser,game,true,myHuType,false);
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(gamingUser.getRoomId()+""), outJsonObject);
	}
	/**
	 * @param gamingUser
	 * @param game
	 * @param cardId
	 * @param userDir
	 */
	public static void notifyUserZhuaWinByJieSan(User gamingUser, Game game, int cardId,
			int userDir,boolean isHaveNextGame) {
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhuahu");
		outJsonObject.put("userDir",userDir);
		outJsonObject.put("cardId", cardId);
		outJsonObject.put("ishuangzhuang", true);
		outJsonObject.put("isHaveNextGame", isHaveNextGame);
		JSONArray userJsonArray = getUserJSONArrayByChouZhuang(room,gamingUser,game,false,-1);
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(gamingUser.getRoomId()+""), outJsonObject);
	}
	/**
	 * 听牌(打一张牌 然后听牌)
	 * @param jsonObject
	 * @param ctx
	 * {"m":"zhuating","userDir":0,"cardId":0}
	 */
	private void ting(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		
		int cardId = jsonObject.getInt("cardId");
		int userDir = jsonObject.getInt("userDir");
		//出牌操作
		Map<String, User> seatMap = game.getSeatMap();
		String direction = gamingUser.getDirection();//得到当前的座次
		User user = seatMap.get(direction);
		int removeCardId = user.chuPai(new Integer(cardId));//出牌
		user.getMyPlays().add(removeCardId);
		game.setLastMap(user,cardId);
		if(removeCardId<0){
			notifyUserError(ctx, "同时出牌出错");
			return;
		}
		user.clearAllCan();
		JSONObject outJsonObject = getChuPaiOutJSONObject(cardId, user);
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
		HuiFangUitl.getTingPai(game.getHuiFang(), user, user.getCards());
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(sessionUser.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		//修改用户状态 听牌状态  通知所有用户该用户听牌
		notifyUserTing(gamingUser, userDir,game);
		//判断其他人是否可以胡牌
		newanalysis(cardId, gamingUser, game);//继续分析是下一个人出牌还是能够碰牌和杠牌
	}
	/**
	 * 通知用户听牌
	 * 	{"m":"zhuating","userDir":0,"cardId":0,"array":{0,1,2,3}}
	 * @param gamingUser
	 * @param userDir
	 * @param game
	 */
	private void notifyUserTing(User gamingUser, int userDir, Game game) {
		OneRoom room = game.getRoom();
		gamingUser.setTingpaiState(1);
		JSONObject tingJsonObject = new JSONObject();
		tingJsonObject.put("m", "zhuating");
		tingJsonObject.put("userDir", userDir);
		tingJsonObject.put("array", gamingUser.getCards());
		NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), tingJsonObject);
	}
	/**
	 * 接收到指定的旋风杠牌型  亮牌
	 * @param jsonObject
	 * @param ctx
	 */
	private void realXuanFengGang(JSONObject jsonObject,
			ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		String string = "";
		if(jsonObject.has("liangArray")){ //吃牌的牌型
			string = jsonObject.getString("liangArray");
		}
		String[] split = string.split(",");
		List<Integer> cards = gamingUser.getCards();
		List<Integer> liangList = new ArrayList<>();
		boolean isHave = false;
		if(split.length==3){
			isHave = checkCardsIsHaveLiangCardType(cards,split,liangList,3);
		}else if(split.length==4){
			isHave = checkCardsIsHaveLiangCardType(cards,split,liangList,4);
		}
		if(!isHave){//没有该类型旋风杠
			notifyUserError(ctx, "没有该类型旋风杠");
			return;
		}
		clearAllUserCan(game);
		if(liangList.size()==3){
			//手牌中移除该 中发白旋风杠
			cards.removeAll(liangList);
			//放到旋风杠的位置
			gamingUser.addXuanFengGang(liangList);
			gamingUser.setUserCanPlay(true);//该玩家可以出牌
			game.setGameStatus(1);//游戏的状态变为出牌
			game.setDirec(gamingUser.getDirection());
			gamingUser.setLastChuPaiDate(new Date());
			//记录玩家碰的牌
			HuiFangUitl.getBaiFengGang(game.getHuiFang(), gamingUser, liangList);
			int total = 0;
			total = PlayCardUtils.userNotWin(game, game.getRemainCards(), gamingUser,total);
			if(total>0){
				//抓牌提示
				notifyUserZhaPaiTip(game,gamingUser,0);
			}else{
				notifyUserChuPai(gamingUser,game);
			};
			notifAllUserXuanFengGang(game,liangList,gamingUser,0,0);
		}else if(liangList.size()==4){
			//手牌中移除该 中发白旋风杠
			cards.removeAll(liangList);
			game.setGameStatus(0);
			//放到旋风杠的位置
			gamingUser.addXuanFengGang(liangList);
			notifAllUserXuanFengGang(game,liangList,gamingUser,0,0);//通知所有的玩家旋风杠的牌
			//记录玩家杠的牌
			HuiFangUitl.getHeiFengGang(game.getHuiFang(), gamingUser, liangList);
			//该玩家在抓一张牌 
			userDrawCard(game, gamingUser.getDirection(),false);
		}
	}
	/**
	 * 检查手牌中是否有要亮的牌
	 * @param cards
	 * @param split
	 * @param liangList
	 * @return
	 */
	private boolean checkCardsIsHaveLiangCardType(List<Integer> cards,
			String[] split, List<Integer> liangList,Integer type) {
		for (String string : split) {
			liangList.add(Integer.parseInt(string));
		}
		Collections.sort(liangList);
		int num = 0;
		for (Integer integer : liangList) {
			if(cards.contains(integer)){
				num ++;
			}
		}
		if(num==type){
			return true;
		}
		return false;
	}
	/**
	 * 多牌型吃牌  例如 123  345  234
	 * @param jsonObject
	 * @param ctx
	 */
	private void realchi(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		boolean checkIsCanRun = checkIsCanRun(game,jsonObject,ctx,1);
		//真去吃的第二步
		if(checkIsCanRun){
			nextRealChi(jsonObject, ctx, game,null);
		}
	}
	/**
	 *吃的第二步
	 * @param jsonObject
	 * @param ctx
	 * @param game
	 * @param direction
	 */
	private void nextRealChi(JSONObject jsonObject, ChannelHandlerContext ctx,
			Game game, String direction) {
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		if(direction!=null){
			gamingUser = game.getGamingUser(direction);
		}
		String string = jsonObject.getString("chiArray");
		int cardId = jsonObject.getInt("cardId");
		String[] split = string.split(",");
		List<Integer> cards = gamingUser.getCards();//获取手牌
		//判断手牌之中是否有该类型的牌
		List<Integer> chiList = new ArrayList<>();
		boolean isHave = checkCardsIsHaveChiCardType(cards,split,chiList,cardId);
		if(!isHave){//没有该类型吃牌
			notifyUserError(ctx, "没有该类型的吃牌");
			return;
		}
		//移除手牌中的吃牌 添加到吃牌中
		chiList = gamingUser.realUserChiCards(chiList);//玩家吃牌
		chiList.add(1,cardId);
//		Collections.sort(chiList);
		gamingUser.addUserChiCards(chiList,game.getFangChiUser().getDirection());//用户添加吃出的牌
		gamingUser.setUserCanPlay(true);//该玩家可以出牌
		game.setGameStatus(1);//游戏的状态变为出牌
		game.setYaoPaiStatus(3);
		game.setDirec(gamingUser.getDirection());
		gamingUser.setLastChuPaiDate(new Date());
		OneRoom room = game.getRoom();
		if(!room.isChiKaiMen()){//不选择只碰开门
			gamingUser.setKaiMen(true);
		}
		clearAllUserCan(game);
		game.clearPriority();
		//记录玩家吃的牌
		notifyAllUserChi(game, chiList,gamingUser,TypeUtils.getUserDir(game.getFangChiUser().getDirection()),TypeUtils.getUserDir(gamingUser.getDirection()),cardId);
		int indexOf = chiList.indexOf(cardId);
		chiList.remove(indexOf);
		chiList.add(0, cardId);
		HuiFangUitl.getChiPai(game.getHuiFang(), gamingUser, chiList,game.getFangChiUser().getDirection());
		int total = 0;
		total = PlayCardUtils.userNotWin(game, game.getRemainCards(), gamingUser,total);
		if(total>0){
			//抓牌提示
			notifyUserZhaPaiTip(game,gamingUser,cardId);
		}else{
			notifyUserChuPai(gamingUser,game);
		};
	}
	/**
	 * 判断手牌之中是否有该类型的牌
	 * @param cards 手牌
	 * @param split 客户端传来的类型
	 * @param arrayList 
	 * @param cardId 
	 * @return
	 */
	private boolean checkCardsIsHaveChiCardType(List<Integer> cards, String[] split, List<Integer> arrayList, int cardId) {
		
		for (String string : split) {
			arrayList.add(Integer.parseInt(string));
		}
		arrayList.remove(arrayList.indexOf(cardId));
		Collections.sort(arrayList);
		int num = 0;
		for (Integer integer : arrayList) {
			if(cards.contains(integer)){
				num ++;
			}
		}
		if(num==2){
			return true;
		}
		return false;
	}
	private void changeStatus(JSONObject jsonObject, ChannelHandlerContext ctx) {
		int status = jsonObject.getInt("status");
		Game game = getGame(ctx);
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		gamingUser.setTingpaiState(status);
		notifyAllUserTingStatus(game, gamingUser);
	}

	
	private void notifyAllUserTingStatus(Game game, User gamingUser) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "changeStatus");
		outJsonObject.put("direction",gamingUser.getDirection());
		outJsonObject.put("status",gamingUser.getTingpaiState());
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}



	/**通知所有的玩家听牌的方向
	 * @param game
	 * @param user 
	 */
	public static void notifyAllUserTingThird(Game game, User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "tingThird");
		outJsonObject.put("direction",user.getDirection());
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**听牌第二步
	 * @param jsonObject
	 * @param ctx
	 */
	private void tingSecond(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		gamingUser.setTingpaiState(2);
		notifyAllUserTingSecond(game, gamingUser);
	}


	
	/**通知用户听牌
	 * @param jsonObject
	 * @param ctx
	 */
	private void tingFirst(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		gamingUser.setTingpaiState(1);
		notifyAllUserTingFirst(game, gamingUser);
	}

	
	
	/**通知所有的玩家听牌的方向
	 * @param game
	 * @param user 
	 */
	public static void notifyAllUserTingSecond(Game game, User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "tingSecond");
		outJsonObject.put("direction",user.getDirection());
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**通知所有的玩家听牌的方向
	 * @param game
	 * @param user 
	 */
	public static void notifyAllUserTingFirst(Game game, User user) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playGame");
		outJsonObject.put(type, "tingFirst");
		outJsonObject.put("direction",user.getDirection());
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	/**用户选择胡牌 被别人胡
	 * @param jsonObject
	 * @param ctx
	 */
	private void huPai(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		int userDirChu = jsonObject.getInt("userDirChu");
		int userDirTip = jsonObject.getInt("userDirTip");
		OneRoom room = game.getRoom();
		int type = 0;
		String stringDir = TypeUtils.getStringDir(userDirChu);//出牌人方向
		String dir = TypeUtils.getStringDir(userDirTip);//出牌人方向
		//提示人方向
		String nextDirection = getNextDirection(stringDir, room.getTotalUser());
		String otherDirection = getNextDirection(nextDirection, room.getTotalUser());
		String direction = getNextDirection(otherDirection, room.getTotalUser());
		if(dir.equals(nextDirection)){
			type=6;
		}else if(dir.equals(otherDirection)){
			type = 5;
		}else if(dir.equals(direction)){
			type = 4;
		}
		boolean checkIsCanRun = checkIsCanRun(game, jsonObject, ctx,type);
		if(checkIsCanRun){
			nextHu(jsonObject,ctx, game,null);
		}
		
	}
	
	/**
	 * 胡的第二步     出胡 胡别人的牌
	 * {"m":"chuHu","userDirChu":0,"userDirTip":0,"cardId":0}
	 * @param jsonObject 
	 * @param ctx
	 * @param game
	 * @param direction 胡的人方向  优先级里面有的话是优先级里面的user
	 */
	private void nextHu(JSONObject jsonObject, ChannelHandlerContext ctx, Game game, String direction) {
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int cardId = jsonObject.getInt("cardId");
		int userDirChu = jsonObject.getInt("userDirChu");
		int userDirTip = jsonObject.getInt("userDirTip");
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		//如果是优先级里面的用户
		if(direction!=null&&!direction.equals("")){
			gamingUser = game.getSeatMap().get(direction);
		}
		if(game.isCanLiuLei()){
			game.setLiuLei(true);
		}
		//把放炮人的出牌数组里面移除该牌
		User user = game.getSeatMap().get(TypeUtils.getStringDir(userDirChu));
		List<Integer> myPlays = user.getMyPlays();
		int indexOf = myPlays.indexOf(cardId);
		if(indexOf!=-1){
			myPlays.remove(indexOf);
		}else{
			List<Integer> cards = user.getCards();
			int of = cards.indexOf(cardId);
			if (of != -1) {
				cards.remove(of);
				notifyAllUserQiangGang(game.getRoom(),cardId,userDirChu,userDirTip);
			}
		}
		
		gamingUser.setCanWin(false);
		game.clearPriority();
		clearAllUserCan(game);		//清空用户所有可以进行的操作 比如可以听 可以吃 可以胡 可以碰 都置成false
		gamingUser.getCards().add(cardId);//胡的人手牌里面加入该牌
		userWin(game,cardId,gamingUser,userDirChu,userDirTip,cardId,false);
	}
	/**
	 * 通知所有人被抢杠
	 * @param room
	 * @param cardId
	 * @param userDirChu
	 * @param userDirTip 
	 */
	private void notifyAllUserQiangGang(OneRoom room, int cardId, int userDirChu, int userDirTip) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "qiangGang");
		outJsonObject.put("cardId", cardId);
		outJsonObject.put("beiQiangDir", userDirChu);
		outJsonObject.put("qiangDir", userDirTip);
		List<ChannelHandlerContext> userIoSessionList = room.getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
		
	}
	/**暗杠
	 * {"m":"zhuagangA","userDir":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 */
	private void anGang(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		Integer cardId = jsonObject.getInt("cardId");
		Integer userDir = jsonObject.getInt("userDir");
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User user = game.getGamingUser(sessionUser.getDirection());
		List<Integer> cards = user.getCards();
		List<Integer> gangCards = getGangList(cards, cardId);//杠的牌
		if(gangCards.size()<4){
			notifyUserError(ctx, "异常暗杠");
			return;
		}
		user.userGangCards(gangCards);
		game.setGameStatus(0);
		//记录玩家杠的牌
		user.recordUserGangCards(1, gangCards);
		user.setCanAnGang(false);
		game.setCanAnGang(false);
		game.setAfterGang(true);
		user.clearAllCan();
		//如果有人暗杠中发白算四个
		if(cardId>=108&&cardId<=119){
			user.setGangMnum(user.getGangAnum()+2);
		}else{
			user.setGangAnum(user.getGangAnum()+1);
		}
		notifyAllUserAnGang(game, gangCards,user,cardId,userDir);//通知所有的玩家杠的牌 
		//记录玩家公杠
		HuiFangUitl.getAnGang(game.getHuiFang(), user, gangCards);
		if(gangCards.size()==4){
			//该玩家在抓一张牌 
			userDrawCard(game, user.getDirection(),false);
		}else{
			game.setDirec(user.getDirection());//把当前出牌的方向改变
			game.setGameStatus(0);//设置成出牌的状态
		}
	}


	/**公杠
	 * {"m":"zhuagangP","userDir":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 */
	private void gongGang(JSONObject jsonObject, ChannelHandlerContext ctx) {
		int cardId = jsonObject.getInt("cardId");//得到的那张牌
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(ctx);
		User gamingUser = getGamingUser(game, user.getDirection());
		boolean canHu = checkOtherUserIsCanHu(game,gamingUser,cardId);
		if(canHu){
			//有胡牌杠牌碰牌吃牌的命令
			//给用户发送指令
			Priority priority = game.getPriority();
			 Map<String, Integer> seatMap2 = priority.getSeatMap();
			if(seatMap2 ==null){
				seatMap2= new HashMap<String, Integer>();
			}
			int size =	seatMap2.size();
			priority.setTotal(size);
			notifyAllUserChuTip(user,game.getRoom(),cardId);//通知用户出牌提示
			game.setBeiQiangGangJsonObject(jsonObject);
			game.setBeiQiangGangCard(cardId);
			game.setBeiQiangGangUser(gamingUser);
			return;
		}
		nextPengGang(jsonObject, ctx ,null);
	}
	/**
	 * 碰杠第二步
	 * @param jsonObject
	 * @param ctx
	 * @param direction
	 */
	private void nextPengGang(JSONObject jsonObject, ChannelHandlerContext ctx, String direction) {
		int cardId = jsonObject.getInt("cardId");//得到的那张牌
		int userDir = jsonObject.getInt("userDir");//得到方向
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(ctx);
		User gamingUser = getGamingUser(game, user.getDirection());
		if(direction!=null&&!direction.equals("")){
			gamingUser = game.getSeatMap().get(direction);
			game.setBeiQiangGangJsonObject(null);
			game.setBeiQiangGangCard(-1);
			game.setBeiQiangGangUser(null);
		}
		List<Integer> pengCards = gamingUser.getUserPengCardsId();
		List<Integer> removeList = getRemoveList(cardId, pengCards);
		if(removeList.size()<3){
			notifyUserError(ctx, "异常碰杠");
			return;
		}
		List<PengCard> pengs = gamingUser.getPengCards();
		gamingUser.setCanGang(false);
		game.setCanGang(false);
		game.setAfterGang(true);
		clearAllUserCan(game);
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				break;
			}
		}
		
		if(cardId>=108&&cardId<=119){
			gamingUser.setGangMnum(user.getGangMnum()+2);
		}else{
			gamingUser.setGangMnum(user.getGangMnum()+1);
		}
		
//		gamingUser.setGangMnum(user.getGangMnum()+1);
		gamingUser.setKaiMen(true);
		game.setGameStatus(0);
		removeList.add(cardId);
		//从自己的牌中移除公杠的那张牌
		gamingUser.removeCardFromGongGang(cardId);
		//记录玩家杠的牌
		gamingUser.recordUserGangCards(2, removeList);
		notifyAllUserGongGang(game, removeList,gamingUser,cardId,userDir);//通知所有的玩家杠的牌 
		//记录玩家公杠的牌
		int index = removeList.indexOf(cardId);
		removeList.remove(index);
		removeList.add(0, cardId);
		HuiFangUitl.getGongGang(game.getHuiFang(), gamingUser, removeList);
		//该玩家在抓一张牌 
		userDrawCard(game, gamingUser.getDirection(),false);
	}
	/**
	 * 检测其他人能不能抢杠胡这张牌
	 * @param game
	 * @param gamingUser
	 * @param cardId
	 */
	private boolean checkOtherUserIsCanHu(Game game, User gamingUser, int cardId) {
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator = seatMap.keySet().iterator();
		int total = 0;		
		while (iterator.hasNext()) {
			String key =  iterator.next();
			User user = seatMap.get(key);
			if(user.getId()!=gamingUser.getId()){
				boolean hu = useIsCanHu(cardId, game, user);
				if(hu){
					game.setGameStatus(0);
					user.setCanWin(true);
					//加入决策优先级 可以胡牌  
					Priority priority = game.getPriority();
					int sort = getSort(gamingUser,user,game.getRoom());
					jueCeMap(user, priority,sort);
					List<String> huDir = new ArrayList<String>();
					huDir.add(user.getDirection());
					priority.setHuDir(huDir);
					game.setPriority(priority);
					game.setFangPaoUser(gamingUser);
					game.setCanWin(true);
					game.setCanHuUser(user);
					total++;	
				}
			}
		}
		if(total>0){
			return true;
		}
		return false;
	}
	/**旋风杠
	 * @param jsonObject
	 * @param ctx
	 */
	private void xuanFengGang(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
//		checkIsCanRun(game, jsonObject, ctx);
		liang(jsonObject,ctx, game);

	}
	/**
	 * 亮牌
	 * @param jsonObject
	 * @param ctx
	 * @param game
	 * {"m":"zhualiang","userDir":0,"cardId":0}
	 */
	private void liang(JSONObject jsonObject, ChannelHandlerContext ctx, Game game) {
		//首先判断能不能旋风杠  返回一个可以旋风杠的牌型的数组  每组代表可以旋风杠的牌  
		//一组直接亮牌   大于一组返回可以旋风杠的牌型
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int userDir = jsonObject.getInt("userDir");
		int cardId = jsonObject.getInt("cardId");
		User gamingUser = game.getGamingUser(sessionUser.getDirection());
		//获取旋风杠的牌型
		List<List<Integer>> xuanFengGang = analysisUserIsCanXuanFengGang(gamingUser);
		if(xuanFengGang.size()<=0){//不可以旋风杠
			notifyUserError(ctx, "异常旋风杠");
			return;
		}else{
			if(xuanFengGang.size()==1){//只有一个旋风杠牌  直接旋风杠
				LiangYiZhong(game, userDir, cardId, gamingUser, xuanFengGang);
			}else{//有好几套旋风杠牌型]
				LiangYiZhong(game, userDir, cardId, gamingUser, xuanFengGang);
			}
		}
	}
	private void LiangYiZhong(Game game, int userDir, int cardId,
			User gamingUser, List<List<Integer>> xuanFengGang) {
		List<Integer> list = xuanFengGang.get(0);
		if(list.size()==3){
			//手牌中移除该 中发白旋风杠
			List<Integer> cards = gamingUser.getCards();
			cards.removeAll(list);
			//放到旋风杠的位置
			gamingUser.addXuanFengGang(list);
			gamingUser.setUserCanPlay(true);//该玩家可以出牌
			game.setGameStatus(1);//游戏的状态变为出牌
			game.setDirec(gamingUser.getDirection());
			gamingUser.setLastChuPaiDate(new Date());
			gamingUser.setCanBaiFeng(false);
			game.setHaveLiangBai(true);
			//记录玩家碰的牌
			HuiFangUitl.getBaiFengGang(game.getHuiFang(), gamingUser, list);
			notifAllUserXuanFengGang(game,list,gamingUser,userDir,cardId);
			int total = 0;
			UserLiangScore(game,gamingUser);
			total = PlayCardUtils.userNotWin(game, game.getRemainCards(), gamingUser,total);
			if(total>0){
				//抓牌提示
				notifyUserZhaPaiTip(game,gamingUser,cardId);
			}else{
				notifyUserChuPai(gamingUser,game);
			};
			
		}else if(list.size()==4){
			//手牌中移除该 中发白旋风杠
			List<Integer> cards = gamingUser.getCards();
			cards.removeAll(list);
			//放到旋风杠的位置
			game.setGameStatus(0);
			gamingUser.addXuanFengGang(list);
			gamingUser.setCanHeiFeng(false);
			game.setHaveLiangHei(true);
			notifAllUserXuanFengGang(game,list,gamingUser,userDir,cardId);//通知所有的玩家旋风杠的牌
			//记录玩家杠的牌
			HuiFangUitl.getHeiFengGang(game.getHuiFang(), gamingUser, list);
			UserLiangScore(game,gamingUser);
			//该玩家在抓一张牌 
			userDrawCard(game, gamingUser.getDirection(),false);
		}
		clearAllUserCan(game);
	}
	/**
	 * 用户亮牌相当于一个暗杠
	 * @param game
	 * @param gamingUser
	 */
	private void UserLiangScore(Game game, User gamingUser) {
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator = seatMap.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			User user = seatMap.get(string);
			if(user.getId()==gamingUser.getId()){
				user.setGangAnum(user.getGangAnum()+1);
				break;
			}
		}
	}
	/**
	 * 通知用户出牌
	 * @param game
	 * @param gamingUser
	 * @param game 
	 */
	static void notifyUserChuPai(User gamingUser, Game game) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "ygncp");
		jsonObject.put("userDir", TypeUtils.getUserDir(gamingUser.getDirection()));
		gamingUser.setLastChuPaiDate(new Date());//设置庄家第一次出牌时间
		NotifyTool.notifyIoSessionList(game.getRoom().getUserIoSessionList(), jsonObject);
	}
	/**
	 * 通知所有用户可以旋风杠
	 * @param game
	 * @param list  旋风杠的牌
	 * @param cardId 
	 * @param userDir 
	 * @param gamingUser旋风杠的人
	 * {"m":"zhualiang","userDir":0,"cardId":0,"array":{0,1,2,3}}
	 */
	private void notifAllUserXuanFengGang(Game game, List<Integer> list,
			User gamingUser, int userDir, int cardId) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhualiang");
		outJsonObject.put("userDir", userDir);
		outJsonObject.put("cardId",cardId);
		outJsonObject.put("array", list);
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	/**得到需要从碰的牌中移除的集合
	 * @param card
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getRemoveList(int card,List<Integer> pengCards){
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			Integer pengCard = pengCards.get(i);
			if(card/4 == pengCard/4){
				list.add(pengCard);
			}
		}
		return list;
	}
	


	/**不碰也不杠 不吃 不胡
	 * @param jsonObject
	 * @param ctx
	 */
	private void fangqi(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = GameManager.getGameWithRoomNumber(user.getRoomId());
		User gamingUser = getGamingUser(game, user.getDirection());
		int userDir = jsonObject.getInt("userDir");		
		Priority priority = game.getPriority();
		Map<String, Integer> seatMap = priority.getSeatMap();
		if(seatMap.size()>0&&seatMap.get(gamingUser.getDirection())!=null){//优先级里面有该操作
			priority.setCurrentNumber(priority.getCurrentNumber()+1);
			//移除该用户所有可以的操作
			Iterator<Integer> iterator = priority.getTypeMap().keySet().iterator();
			List<Integer> list = new ArrayList<>();
			while (iterator.hasNext()) {
				Integer integer = iterator.next();
				if(priority.getTypeMap().get(integer).equals(gamingUser.getDirection())){
					list.add(integer);
				}
			}
			for (Integer integer : list) {
				priority.getTypeMap().remove(integer);
			}
			seatMap.remove(gamingUser.getDirection());
			if(seatMap.size()<=0){
				String stringDir = TypeUtils.getStringDir(game.getLastMap().get("dir"));
				String direction = getNextDirection(stringDir,game.getRoom().getTotalUser());
				gamingUser.clearAllCan();
				JSONObject object = game.getBeiQiangGangJsonObject();
				if(object!=null){
					nextPengGang(object, ctx, game.getBeiQiangGangUser().getDirection());
					return;
				}else{
					
					userDrawCard(game, direction,false);
				}
				return;
			}
			if(priority.currentNumber>=priority.total){
				Map<Integer, String> typeMap = priority.getTypeMap();
				if(typeMap.size()>0){
					int number = 0;
					Iterator<Integer> iterator1 = typeMap.keySet().iterator();
					while (iterator1.hasNext()) {
						Integer next = iterator1.next();
						if(next>number){
							number = next;
						}
					}
					//number是优先级最高的操作  执行这个操作  根据number
					String direction = typeMap.get(number);
					judgeNumberToRun(number,direction,jsonObject,ctx,game);
				}else{
					game.clearPriority();
					JSONObject object = game.getBeiQiangGangJsonObject();
					if(object!=null){
						nextPengGang(object, ctx, game.getBeiQiangGangUser().getDirection());
						return;
					}else{
						logger.info("被抢杠人的JsonObect为空了");
					}
					//上一个出牌用户的下家抓牌
					int status = game.getGameStatus();
					clearAllUserCan(game);
					game.clearUserCan();
					doWithPengOrGang(user, game, status);
				}
			}
		}else{
			int status = game.getGameStatus();
			gamingUser.clearAllCan();
			doWithPengOrGang(user, game, status);
		}
	}

	
	private void doWithPengOrGang(User user, Game game, int status) {
		User gamingUser = getGamingUser(game, user.getDirection());
		if(status==0){//出牌后抓牌钱的状态
			setUserGiveUp(gamingUser);//用户放弃出牌
			if(chouZhuang(game.getRemainCards(),game)){
				afterChouZhuang(game, user);//臭庄之后处理
				return;
			}else{
				String direction = getNextDirection(TypeUtils.getStringDir(game.getLastMap().get("dir")),game.getRoom().getTotalUser());
				userDrawCard(game, direction,false);
			}
		}else if(status==1){
			//抓拍后出牌前的状态
			User chuPaiUser = game.getSeatMap().get(game.getDirec());
			if(chuPaiUser.getTingpaiState()==1){//听牌后  自动出牌
				notifyUserChuPai(chuPaiUser,game);
				autoChuPai(game,false);
			}else{
				notifyUserChuPai(chuPaiUser,game);
			}
		}
	}
	
	
	/**用户放弃出牌
	 * @param user
	 */
	private void setUserGiveUp(User user){
		user.setCanGang(false);
		user.setCanPeng(false);
		user.setCanChi(false);
		user.setCanAnGang(false);
		user.setCanHeiFeng(false);
		user.setCanBaiFeng(false);
		user.setCanPengGang(false);
	}
	
	
	/**杠牌
	 * 
	 * @param jsonObject
	 * @param ctx
	 */
	private void gang(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		boolean checkIsCanRun = checkIsCanRun(game, jsonObject, ctx,3);
		if(checkIsCanRun){
			nextGang(jsonObject, ctx, game,null);
		}
	}
	/**
	 * 杠的第二步
	 * {"m":"chuGang","userDirChu":0,"userDirTip":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 * @param game
	 * @param direction 
	 */
	private void nextGang(JSONObject jsonObject, ChannelHandlerContext ctx,
			Game game, String direction) {
		int cardId = jsonObject.getInt("cardId");
		int userDirChu = jsonObject.getInt("userDirChu");
		int userDirTip = jsonObject.getInt("userDirTip");
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User user = getGamingUser(game, sessionUser.getDirection());
		if(direction!=null&&!direction.equals("")){
			user = game.getSeatMap().get(direction);
		}
		boolean isUserCanGang = checkUserIsCanGang(user);
		if(!isUserCanGang){
			notifyUserError(ctx, "不可接杠");
			return;
		}
		user.setUserCanPlay(true);
		user.setCanPengGang(false);
		game.setCanPengGang(false);
		Map<String, User> seatMap = game.getSeatMap();
		User u = seatMap.get(user.getDirection());
		List<Integer> cards = u.getCards();
		List<Integer> gangCards = getGangList(cards, cardId);//杠的牌
		gangCards.add(cardId);
		if(gangCards.size()!=4){
			notifyUserError(ctx, "接杠异常");
			return;
		}
//		u.clearAllCan();
		clearAllUserCan(game);
		
		if(cardId>=108&&cardId<=119){
			u.setGangPnum(u.getGangPnum()+2);
		}else{
			u.setGangPnum(u.getGangPnum()+1);
		}
		
//		u.setGangPnum(u.getGangPnum()+1);
		game.setYaoPaiStatus(3);//出牌后有人要牌
		game.setGameStatus(0);
		game.setAfterGang(true);
		u.userGangCards(gangCards);
		u.setKaiMen(true);
		//记录玩家杠的牌
		u.recordUserGangCards(0, gangCards);
		game.clearPriority();
		notifyAllUserGang(game, gangCards,user,userDirChu,userDirTip,cardId);//通知所有的玩家杠的牌
		//记录玩家杠的牌
		int index = gangCards.indexOf(cardId);
		gangCards.remove(index);
		gangCards.add(0, cardId);
		HuiFangUitl.getGangPai(game.getHuiFang(), u, gangCards,TypeUtils.getStringDir(userDirChu));
		//该玩家在抓一张牌 
		userDrawCard(game, u.getDirection(),true);
	}
	
	/**通知用户现在的成绩
	 * @param game
	 *//*
	private static void notifyAllUserCurrentScore(Game game) {
		JSONObject jsonObject =  new JSONObject();
		jsonObject.put("method", "changeChip");
		JSONArray userArray =  new JSONArray();
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			JSONObject userJson =  new JSONObject();
			userJson.put("userId", user.getId());
			userJson.put("userDirection", user.getDirection());
			userJson.put("userChip", user.getCurrentScore());
			userArray.put(userJson);
		}
		jsonObject.put("changeUsersChip", userArray);
		NotifyTool.notifyIoSessionList(game.getIoSessionList(), jsonObject);
	}*/

//	/**得到游戏中的玩家
//	 * @param userId
//	 * @param roomId
//	 */
//	public static User getGamingUser(int userId,String roomId){
//		Game game = GameManager.getGameWithRoomNumber(roomId);
//		List<User> userList = game.getRoom().getUserList();
//		for(int i=0;i<userList.size();i++){
//			User user = userList.get(i);
//			if(user.getId()==userId){
//				return user; 
//			}
//		}
//		logger.fatal("未找到游戏中的人................");
//		return null;
//	}
	/**得到游戏中的玩家
	 * @param userId
	 * @param roomId
	 */
	public static User getGamingUser(Game game,String direction){
		User user = game.getSeatMap().get(direction);
		return user;
	}
	


	
	/**
	 * 抓牌
	 * 
	 * @param game
	 * @param direction  抓牌用户的方向
	 * @param isGang
	 *            是否可以杠上开花
	 * @param isPengGang  //是否是碰杠后的抓牌
	 */
	public static void userDrawCard(Game game, String direction, boolean isPengGang) {

		//过蛋用参数
		int guodanDir = TypeUtils.getUserDir(direction);
		Integer guodanCard = null;
		
		List<Integer> remainCards = game.getRemainCards();//得到剩余牌
		User user = game.getSeatMap().get(direction);
		int lastIndex = remainCards.size() - 1;
		// 通知该玩家抓到的牌
		Integer removeCard = remainCards.remove(lastIndex);
		guodanCard = removeCard;
		
		game.setDirec(direction);// 把当前出牌的方向改变
		game.setGameStatus(1);// 设置抓牌后的状态  
		game.setYaoPaiStatus(2);
		boolean isWin = user.zhuaPai(removeCard);// 抓牌
		if(!isPengGang){
			game.setYaoPaiStatus(2);//上一轮出的牌没人要
		}
		if(game.getRoom().isGuoDan()){
			//如果是过蛋算分
			int suanFen = guoDanSuanFen(game.isAfterGang(), removeCard,user,game);
			if(suanFen>0){//过蛋了 通知客户端 
				notifyUserGuoDan(guodanDir, game.getRoom(), guodanCard);
			}
		}
		// 胡牌的时候
		// 1.如果用户托管的时候用户直接胡牌
		// 2.如果用户没有托管，判断用户是否可以公杠和暗杠，如果可以公杠和暗杠给出用户：【杠】【胡】的提示
		user.clearAllCan();
		int total = userNotWin(game, remainCards, removeCard, user,isWin);
		if(total>0){
			notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
			notifyUserZhaPaiTip(game,user,removeCard);
		}else if(total==-1){
			return;
		}else{
			notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
			notifyUserChuPai(user,game);
		}
	}
	/**
	 * 通知用户过蛋
	 * @param userDir
	 * @param room
	 * @param removeCard 
	 */
	private static void notifyUserGuoDan(int userDir, OneRoom room, Integer removeCard) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "noticeGuoDan");
		jsonObject.put("userDir", userDir);
		jsonObject.put("cardId", removeCard);
		NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), jsonObject);
	}
	/**
	 * 过蛋计算分
	 * @param isGang
	 * @param removeCard
	 * @param user
	 * @param game 
	 */
	private static int guoDanSuanFen(boolean isGang, Integer removeCard, User user, Game game) {
		int guoDan = 0;
		if(isGang){//如果是杠牌后   抓到的牌是饼牌
			if(removeCard>=36&&removeCard<=71){//一到九筒  几筒几分
				guoDan = removeCard/4-8;
				modifyUserGuoDanScore(removeCard, user, game, guoDan);
			}else if(removeCard/4==18){//如果是一条  十分
				guoDan = 10;
				modifyUserGuoDanScore(removeCard, user, game, guoDan);
			}else if(removeCard/4==28){//如果是发财 十一分
				guoDan = 11;
				modifyUserGuoDanScore(removeCard, user, game, guoDan);
			}
		}
		return guoDan;
	}
	private static void modifyUserGuoDanScore(Integer removeCard, User user,
			Game game, int guoDan) {
		int total = game.getRoom().getTotalUser();
		//自己过蛋分增加
		user.setGuoDanScore(user.getGuoDanScore()+(guoDan*(total-1)));
		user.getGuoDanList().add(removeCard);
		//其他人过蛋分减少
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator = seatMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			User u = seatMap.get(key);
			if(u.getId()!=user.getId()){
				u.setGuoDanScore(u.getGuoDanScore()-guoDan);
			}
		}
	}
	/**
	 * 通知用户抓牌提示
	 * {"m":"tipForZhuapai","userDir":0,"cardId":0,"isGangP":false,"isGangA":false,"isLiangBai":false,"isTing":false,"isHu":false,"isLiangHei":false}
	 * @param game
	 * @param user
	 * @param removeCard
	 */
	static void notifyUserZhaPaiTip(Game game, User user,
			Integer removeCard) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "tipForZhuapai");
		jsonObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
		jsonObject.put("cardId", removeCard);
		jsonObject.put("isGangP", user.isCanGang());
		jsonObject.put("isGangA", user.isCanAnGang());
		jsonObject.put("isLiangBai", user.isCanBaiFeng());
		jsonObject.put("isLiangHei", user.isCanHeiFeng());
		jsonObject.put("isTing", user.isCanTing());
		jsonObject.put("isHu", user.isCanWin());
		if(user.isCanAnGang()){
			Integer integer = game.getAnGangCards().get(0);
			jsonObject.put("cardidForUse", integer);
		}else if(user.isCanGang()){
			Integer gongGangCardId = game.getGongGangCardId();
			jsonObject.put("cardidForUse", gongGangCardId);
		}else{
			jsonObject.put("cardidForUse", removeCard);
		}
		NotifyTool.notify(user.getIoSession(), jsonObject);
	}

	/**用户没有赢牌
	 * @param game
	 * @param remainCards  游戏剩余牌
	 * @param removeCard   抓的牌
	 * @param user
	 * @param total 
	 * @param isWin 
	 * @param isGang 
	 */
	private static int userNotWin(Game game, List<Integer> remainCards, Integer removeCard, User user, boolean isWin) {
		int total = 0;
		user.setUserCanPlay(true);//该用户可以打牌
		//分析用户可不可以旋风杠   //中发白 OR 东南西北
		//旋风杠也可以明杠 也可以暗杠
		List<List<Integer>> xuanFengGang = analysisUserIsCanXuanFengGang(user);
		//分析用户是否可以公杠
		Integer pengGang = analysisUserIsCanPengGang(user);
		if(user.getTingpaiState()==1){//听牌 后只能明暗杠
			if(isWin){
				user.clearAllCan();
				user.setCanWin(true);
				if(remainCards.size()<=3){//海底捞月
					user.setCanHai(true);
				}
				if(game.isAfterGang()){//可以杠上开花
					user.setCanKai(true);
				}
				game.setCanHuUser(user);
				game.setCanWin(true);
				user.setCanWin(true);
				total++;
			}
			if(pengGang!=-1){//可以公杠
				user.setCanGang(true);
				game.setGongGangCardId(pengGang);
				game.setCanGongGangUser(user);
				total++;
			}else{
				//分析该用户是否可以暗杠
				List<Integer> anGangCards = isUserCanAnGang(user);
				////可以暗杠
				if(anGangCards.size()>0){
					List<Integer> cards = user.getCards();
					List<Integer> newCards = HuPai.getNewListFromOldList(cards);
					List<PengCard> newPengCards = user.getPengCards();
					List<GangCard> newGangCards = user.getGangCards();
					HuPai huPai = new HuPai();
					List<ChiCard> chiCards = user.getChiCards();
					List<XuanFengGangCard> baiFengGangCards = user.getBaiFengGangCards();
					List<XuanFengGangCard> heiFengGangCards = user.getHeiFengGangCards();
					List<GangCard> newGangCardsCopy = new ArrayList<GangCard>();
					for(int i=0;i<newGangCards.size();i++){
						GangCard gangCard = newGangCards.get(i);
						newGangCardsCopy.add(gangCard);
					}
					GangCard gangCard = new GangCard(1, anGangCards);
					newGangCardsCopy.add(gangCard);
					for(int i=0;i<anGangCards.size();i++){
						Integer everyAnGangCard = anGangCards.get(i);
						int indexOf = newCards.indexOf(everyAnGangCard);
						if(indexOf>-1){
							newCards.remove(indexOf);
						}
					}
					boolean ting = huPai.isTing(newCards, newPengCards, newGangCardsCopy, chiCards, baiFengGangCards, heiFengGangCards,game.getRoom());
					if(ting){//不影响听牌
						game.setAnGangCards(anGangCards);
						game.setCanAnGangUser(user);
						user.setCanAnGang(true);
						game.setCanAnGang(true);
						total++;
					}else{
						if(total>0){
							notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
							notifyUserZhaPaiTip(game,user,removeCard);
						}else{
							notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
							notifyUserChuPai(user,game);
							autoChuPai(game,false);//自动出牌
						}
						return -1;
					}
				}else{
					//玩家不可以暗杠
					if(total>0){
						notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
						notifyUserZhaPaiTip(game,user,removeCard);
					}else{
						notifyUserDrawDirection(removeCard, user);//通知抓牌的方向
						notifyUserChuPai(user,game);
						autoChuPai(game,false);//自动出牌
					}
					return -1;
				}
			}	
		}else{//没听牌
			total = userNotTingPai(game, remainCards, user, total, isWin,
					xuanFengGang, pengGang);
		}
		return total;
	}
	/**
	 * 
	 * @param game
	 * @param remainCards
	 * @param user
	 * @param total
	 * @param isWin
	 * @param xuanFengGang
	 * @param pengGang
	 * @return
	 */
	private static int userNotTingPai(Game game, List<Integer> remainCards,
			User user, int total, boolean isWin,
			List<List<Integer>> xuanFengGang, Integer pengGang) {
		if(isWin){
			user.clearAllCan();
			user.setCanWin(true);
			if(remainCards.size()<=3){//海底捞月
				user.setCanHai(true);
			}
			if(game.isAfterGang()){//可以杠上开花
				user.setCanKai(true);
			}
			game.setCanHuUser(user);
			game.setCanWin(true);
			user.setCanWin(true);
			total++;
		}
		boolean checkUserIsCanTing = checkUserIsCanTing(user, game);
		if(checkUserIsCanTing){
			user.setCanTing(true);
			game.setCanTingUser(user);
			total++;
		}
		if(xuanFengGang.size()>0){
			//可以旋风杠
			game.setCanXuanFengGangUser(user);
			for (List<Integer> xuan : xuanFengGang) {
				if(xuan.size()==3){
					user.setCanBaiFeng(true);
					game.setXuanFengBai(true);		
				}else if(xuan.size()==4){
					user.setCanHeiFeng(true);
					game.setXuanFengHei(true);
				}
			}
			total++;
		}	
		if(pengGang!=-1){//可以公杠
			user.setCanGang(true);
			game.setGongGangCardId(pengGang);
			game.setCanGongGangUser(user);
			total++;
		}else{
			//分析该用户是否可以暗杠
			List<Integer> anGangCards = isUserCanAnGang(user);
			if(anGangCards.size()>0){//设置出牌的状态为暗杠
				user.setCanAnGang(true);
				game.setAnGangCards(anGangCards);
				game.setCanAnGangUser(user);
				game.setCanAnGang(true);
				total++;
			}
		}
		return total;
	}

	/**
	 * 分析用户可不可以旋风杠
	 * @param removeCard  
	 * @param user
	 * @return
	 */
	public static List<List<Integer>> analysisUserIsCanXuanFengGang(User user) {
		List<List<Integer>> xuanFengGangCards = new ArrayList<>();
		List<Integer> newList = new ArrayList<>();//临时保存的集合 四张牌一个类型  类型集合
		List<Integer> oldCards = user.getCards();
		List<Integer> cards = new ArrayList<>();
		for (int i = 0; i < oldCards.size(); i++) {
			cards.add(oldCards.get(i));
		}
		for (int i = 0; i < cards.size(); i++) {
			newList.add(cards.get(i)/4);
		}
		List<Integer> baiFengList = new ArrayList<>();
		baiFengList.add(27);//中
		baiFengList.add(28);//发
		baiFengList.add(29);//白
		List<Integer> heiFengList = new ArrayList<>();
		heiFengList.add(30);//东
		heiFengList.add(31);//南
		heiFengList.add(32);//西
		heiFengList.add(33);//北
		//遍历手牌  如果包含同时中发白 或东南西北  把他们取出来放到数组中,存到集合里面 
		//,然后把手牌中的这些牌清掉  然后在继续遍历 直到没有了结束循环   
		for (int i = 0; i < 4; i++) {
			if(newList.containsAll(baiFengList)){
				List<Integer> baifeng = new ArrayList<>();
				for (int j = 0; j < baiFengList.size(); j++) {
					int indexOf = newList.indexOf(baiFengList.get(j));
					baifeng.add(cards.get(indexOf));
				}
				xuanFengGangCards.add(baifeng);
				for (int j = baiFengList.size()-1; j >=0; j--) {
					int indexOf = newList.indexOf(baiFengList.get(j));
					newList.remove(indexOf);
					cards.remove(indexOf);
				}
			}else if(newList.containsAll(heiFengList)){
				List<Integer> heifeng = new ArrayList<>();
				for (int j = 0; j < heiFengList.size(); j++) {
					int indexOf = newList.indexOf(heiFengList.get(j));
					heifeng.add(cards.get(indexOf));
				}
				xuanFengGangCards.add(heifeng);
				for (int j = heiFengList.size()-1; j >=0; j--) {
					int indexOf = newList.indexOf(heiFengList.get(j));
					newList.remove(indexOf);
					cards.remove(indexOf);
				}
			}else{
				break;
			}
		}
		return xuanFengGangCards;
	}


	/**自动暗杠
	 * @param game
	 * @param user
	 * @param anGangCards
	 */
	public static  void autoAnGang(Game game) {
		User canAnGangUser = game.getCanAnGangUser();
		List<Integer> anGangCards = game.getAnGangCards();
		canAnGangUser.userGangCards(anGangCards);
		//记录玩家暗杠的牌
		HuiFangUitl.getAnGang(game.getHuiFang(), canAnGangUser, anGangCards);
		//logger.info("自动出牌...暗杠.................:"+NewAI.showPai(anGangCards));
		canAnGangUser.recordUserGangCards(1, anGangCards);
//		PlayGameService.notifyAllUserAnGang(game, anGangCards,canAnGangUser);//通知所有的玩家杠的牌 
//		PlayGameService.modifyUserScoreForAnGang(game, canAnGangUser);//修改玩家暗杠得分
		if(anGangCards.size()==4){//暗杠
			//该玩家在抓一张牌 
			PlayGameService.userDrawCard(game, canAnGangUser.getDirection(),false);
		}else{//翻牌杠,在出牌
//			autoChuPai(game);
		}
	}

	/**托管自动公杠
	 * @param game
	 */
	public static  void autoGongGang(Game game){
		User user = game.getCanGongGangUser();
		Integer cardId = game.getGongGangCardId();
		List<Integer> pengCards = user.getUserPengCardsId();//用户碰的牌
		List<Integer> removeList = PlayGameService.getRemoveList(cardId, pengCards);
		
		List<PengCard> pengs = user.getPengCards();
		for(int i=0;i<pengs.size();i++){
			PengCard pengCard = pengs.get(i);
			List<Integer> cards = pengCard.getCards();
			if(cards.get(0)/4==removeList.get(0)/4){
				pengs.remove(pengCard);
				break;
			}
		}
		
		removeList.add(cardId);
		//logger.info("托管自动公杠....................:"+NewAI.showPai(removeList));
		//从自己的牌中移除公杠的那张牌
		user.removeCardFromGongGang(cardId);
		//记录玩家杠的牌
		user.recordUserGangCards(2, removeList);
		HuiFangUitl.getGongGang(game.getHuiFang(), user, removeList);
		//该玩家在抓一张牌 
		userDrawCard(game, user.getDirection(),false);
	}
	
	
	/**
	 * 在本局臭庄之后，计算用户的得分，依然把最后的结算发送给用户
	 */
	public static void afterChouZhuang(Game game,User lastGetCardUser){
		//修改玩家的当前分数
		OneRoom room = game.getRoom();
		boolean isHaveNextGame = false;
		if(!isGameOver(game.getRoom())){
			isHaveNextGame = true;
		}
		if(game.getRoom().isGuoDan()){
			//过蛋算分
			guoDanFen(game);
		}
		int roomId = room.getId();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhuahu");
		outJsonObject.put("userDir",TypeUtils.getUserDir(lastGetCardUser.getDirection()));
		outJsonObject.put("cardId", 0);
		outJsonObject.put("ishuangzhuang", true);
		outJsonObject.put("isHaveNextGame", isHaveNextGame);
		JSONArray userJsonArray = getUserJSONArray(room,lastGetCardUser,game,false,-1,false);
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(lastGetCardUser.getRoomId()+""), outJsonObject);
		recordUserScore(game);
		initializeUser(lastGetCardUser);//初始化用户的数据
		boolean gameOver = setCurrentGameOver(game,roomId);//设置当前的游戏结束
		if(gameOver){
			return;
		}
		game.setZhangMao(true);
	}
	/**
	 * 荒庄 解散房间 的只计算过蛋分
	 * @param game
	 */
	static void guoDanFen(Game game) {
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator = seatMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key =  iterator.next();
			User user = seatMap.get(key);
			user.getXuChangScore().setCurrentScore(user.getXuChangScore().getCurrentScore()+user.getGuoDanScore());//总分
			user.getXuChangScore().setScore(user.getGuoDanScore());//当前局分
		}
	}
	
	/**臭庄
	 * @return
	 */
	public static boolean chouZhuang(List<Integer> remainCards,Game game){
		if(remainCards.size()<=0){
			return true;
		}else{
			return false;
		}
	}
	

	
	/**用户赢牌 
	 * @param game 
	 * @param removeCard 最后抓的牌
	 * @param user 赢牌的玩家
	 * @param userDirTip 
	 * @param userDirChu 
	 * @param cardId 
	 * @param isZiMo 
	 */
	public static void userWin(Game game, Integer removeCard, User user, int userDirChu, int userDirTip, int cardId, boolean isZiMo) {
		
		OneRoom room = game.getRoom();
		boolean qingYiSe = room.isQingYiSe();
		
		//修改玩家的当前分数
		FengTianHuPai fengTianHuPai = room.getFengTianHuPai();
		int myHuType = getMyHuType(user, cardId, fengTianHuPai,qingYiSe);
		modifyUserCurrentScoreByDianPao(game,user,cardId,isZiMo,myHuType);
		HuiFangUitl.getHuPai(game.getHuiFang(), user, user.getCards(),game.getRemainCards());
		String huiFang = game.getHuiFang().toString();
		int roomId = game.getRoom().getId();
		try {
			RecordScoreThread recordScoreThread = new RecordScoreThread(game,huiFang);
			ExecutorService executorService = RecordScoreThreadPool.getExecutorService();
			executorService.execute(recordScoreThread);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally{	
			boolean isHaveNextGame = false;
			if(!isGameOver(game.getRoom())){
				isHaveNextGame = true;
			}
			notifyUserChuWin(user,game,removeCard,userDirChu,userDirTip,isZiMo,myHuType,true,isHaveNextGame);//通知用户出胡
			initializeUser(user);//初始化用户的数据
			setCurrentGameOver(game,roomId);//设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
			setNewBank(user,game);//设置新的庄家
		}
	}
	/**
	 * 通知
	 * @param user
	 * @param game
	 * @param removeCard 胡的牌
	 * {"m":"chuHu","userDirChu":0,"userDirTip":0,
	 * "cardId":0,"ishuangzhuang":false,
	 * "users":[{"arrayLiangHei":{},"arrayLiangBai":{},
	 * "arrayGang":{},"arrayPeng":{},"arrayChi":{},"arrayHand":{},
	 * "userId":0,"userDir":0,"scoreByAdd":0,"scoreTotal":0,"userName":"", 
	 * "descGang":"","descHu":""}]}}
	 * @param userDirTip 
	 * @param userDirChu 
	 * @param isZiMo 
	 * @param myHuType 
	 * @param chuHu 
	 */
	private static void notifyUserChuWin(User user, Game game,
			Integer removeCard, int userDirChu, int userDirTip, boolean isZiMo, int myHuType, boolean chuHu,boolean isHaveNextGame) {
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "chuHu");
		outJsonObject.put("userDirTip",userDirTip);
		outJsonObject.put("userDirChu",userDirChu);
		outJsonObject.put("cardId", removeCard);
		outJsonObject.put("ishuangzhuang", false);
		outJsonObject.put("isHaveNextGame", isHaveNextGame);
		JSONArray userJsonArray = getUserJSONArray(room,user,game,isZiMo,myHuType,chuHu);
		outJsonObject.put("users", userJsonArray);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()+""), outJsonObject);
		
	}


	/**修改用户当前的分数
	 * @param game
	 * @param user //赢家
	 * @param cardId 
	 * @param myHuType 
	 */
	public static void modifyUserCurrentScore(Game game, User user, int cardId,boolean isZiMo, int myHuType) {
		OneRoom room = game.getRoom();
		modifyUserLiangScore(game);//修改用户亮分
		FengTianHuPai fengTianHuPai = room.getFengTianHuPai();
		boolean isZhuangWin =false;
		if(user.isBanker()){
			isZhuangWin = true;
		}
		boolean isTingPai=false;
		if(user.getTingpaiState()==1){
			isTingPai = true;
		}
		boolean shouBaYi = isShouBaYi(user);
		int siGuiYi = siGuiYi(user);
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator1 = seatMap.keySet().iterator();
		int sanQing = 0;
		while (iterator1.hasNext()) {//其他人的分数计算
			String key =  iterator1.next();
			User u = seatMap.get(key);
			if(u.getId()!=user.getId()){
				if(!u.isKaiMen()){
					sanQing ++;
				}
			}
		}		
		int siQing = 1;
		if(!user.isKaiMen()){
			siQing += sanQing;
		}
		Iterator<String> iterator = seatMap.keySet().iterator();
		Integer gangAnum = user.getGangAnum();//暗杠
		Integer gangMnum = user.getGangMnum();
		Integer gangPnum = user.getGangPnum();
		saveGangCiShu(user, gangAnum, gangMnum, gangPnum);
		Integer mingGang = gangMnum + gangPnum;//明杠

		int score_win_hu = 0;//赢牌人的分
		while (iterator.hasNext()) {//其他人的分数计算
			String key =  iterator.next();
			User u = seatMap.get(key);
			if(u.getId()!=user.getId()){

				boolean isZhuangLose =false;
				if(u.isBanker()){
					isZhuangLose=true;
				}
				//胡分
				int score_lose_hu = fengTianHuPai.getScore_lose_hu(isZhuangWin,
						isZhuangLose, isZiMo, false, game.isZhangMao(),
						user.isCanKai(), user.isCanHai(), isTingPai, myHuType,
						room.getFengDingNum(), room.isDianPao(), u.isKaiMen(),
						false, gangAnum, mingGang, sanQing, siQing, u, room, siGuiYi,
						shouBaYi);
				//杠次数 计算翻倍 暗杠两番 明杠一番
				int zong = 0;
				if(room.isGuoDan()){//是否带过蛋
					int guoDan_lose = u.getGuoDanScore();
					zong = score_lose_hu + guoDan_lose;
				}else{
					zong = score_lose_hu;
				}
				u.getXuChangScore().setCurrentScore(u.getXuChangScore().getCurrentScore()+zong);//总分
				u.getXuChangScore().setScore(zong);//当前局分
				score_win_hu += zong;
			}
		}
		user.getXuChangScore().setCurrentScore(user.getXuChangScore().getCurrentScore()+Math.abs(score_win_hu));//总分
		user.getXuChangScore().setScore(Math.abs(score_win_hu));//当前局分
	}
	/**修改用户当前的分数
	 * @param game
	 * @param user //赢家
	 * @param cardId 
	 * @param myHuType 
	 */
	public static void modifyUserCurrentScoreByDianPao(Game game, User user, int cardId,boolean isZiMo, int myHuType) {
		OneRoom room = game.getRoom();
		modifyUserLiangScore(game);//修改用户杠牌数量
		FengTianHuPai fengTianHuPai = room.getFengTianHuPai();
		boolean isZhuangWin =false;
		if(user.isBanker()){
			isZhuangWin = true;
		}
		boolean isTingPai =false;
		if(user.getTingpaiState()==1){
			isTingPai= true;
		}
		//判断赢牌的人是不是四归一
		int siGuiYi = siGuiYi(user);
		//判断赢牌的人是不是手把一
		boolean isShouBaYi = isShouBaYi(user);
		
		Map<String, User> seatMap = game.getSeatMap();
		Iterator<String> iterator1 = seatMap.keySet().iterator();
		int sanQing = 0;
		while (iterator1.hasNext()) {//其他人的分数计算
			String key =  iterator1.next();
			User u = seatMap.get(key);
			if(u.getId()!=user.getId()){
				if(!u.isKaiMen()){
					sanQing ++;
				}
			}
		}
		int siQing = 1;
		if(!user.isKaiMen()){
			siQing += sanQing;
		}
		Iterator<String> iterator = seatMap.keySet().iterator();
		Integer gangAnum = user.getGangAnum();//暗杠
		Integer gangMnum = user.getGangMnum();
		Integer gangPnum = user.getGangPnum();
		saveGangCiShu(user, gangAnum, gangMnum, gangPnum);
		Integer mingGang = gangMnum + gangPnum;;//明杠
		int score_win_hu = 0;//赢牌人的分
		User fangPaoUser = game.getFangPaoUser();
		while (iterator.hasNext()) {//其他人的分数计算
			String key =  iterator.next();
			User u = seatMap.get(key);
			if(u.getId()!=user.getId()){
				if(fangPaoUser!=null&&u.getId()==fangPaoUser.getId()){
					boolean isDianPaoByMySelf = true;
					XuChangScore xuChangScore = u.getXuChangScore();
					xuChangScore.setDianpaoNum(xuChangScore.getDianpaoNum()+1);
					boolean isZhuangLose =false;
					if(u.isBanker()){
						isZhuangLose=true;
					}
					//胡分
					int score_lose_hu = fengTianHuPai.getScore_lose_hu(
							isZhuangWin, isZhuangLose, isZiMo,
							isDianPaoByMySelf, game.isZhangMao(),
							user.isCanKai(), user.isCanHai(), isTingPai,
							myHuType, room.getFengDingNum(), room.isDianPao(),
							u.isKaiMen(), game.isLiuLei(), gangAnum, mingGang,
							sanQing,siQing,u,room,siGuiYi,isShouBaYi);
					int zong = 0;

					if(room.isGuoDan()){//是否带过蛋
						int guoDan_lose = u.getGuoDanScore();
						zong = score_lose_hu + guoDan_lose;
					}else{
						zong = score_lose_hu;
					}
					u.getXuChangScore().setCurrentScore(u.getXuChangScore().getCurrentScore()+zong);//总分
					u.getXuChangScore().setScore(zong);//当前局分
					score_win_hu += zong;
				}else{
					int score_lose_hu = 0;
					int zong = 0;
					if(room.isGuoDan()){//是否带过蛋
						int guoDan_lose = u.getGuoDanScore();
						zong = score_lose_hu + guoDan_lose;
					}else{
						zong = score_lose_hu;
					}
					u.getXuChangScore().setCurrentScore(u.getXuChangScore().getCurrentScore()+zong);//总分
					u.getXuChangScore().setScore(zong);//当前局分
					score_win_hu += zong;
				}
			}
		}
		int WinTotal = -score_win_hu;
		user.getXuChangScore().setCurrentScore(user.getXuChangScore().getCurrentScore()+WinTotal);//总分
		user.getXuChangScore().setScore(WinTotal);//当前局分
	}
	/**
	 * 判断赢牌人是不是手把一  胡牌时候手里只有一颗拍
	 * @param user
	 * @return
	 */
	private static boolean isShouBaYi(User user) {
		List<Integer> cards = user.getCards();
		if(cards.size()<=2){
			user.setShouBaYi(true);
			return true;
		}
		return false;
	}
	/**
	 * 判断赢牌的人是不是四归一(杠牌不算四归一)
	 * @param user
	 * @return
	 */
	private static int siGuiYi(User user) {

		//key:牌/4  value:次数
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		//碰牌
		List<PengCard> pengCards = user.getPengCards();
		for (PengCard pengCard : pengCards) {
			Integer card = pengCard.getCards().get(0)/4;
			if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
                map.put(card, map.get(card).intValue() + 3);
            }else{
                map.put(card, 3);
            }
		}
		//吃牌
		List<ChiCard> chiCards = user.getChiCards();
		for (ChiCard chiCard : chiCards) {
			List<Integer> cards = chiCard.getCards();
			for (Integer integer : cards) {
				Integer card = integer/4;
				if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
	                map.put(card, map.get(card).intValue() + 1);
	            }else{
	                map.put(card, 1);
	            }
			}
		}
		//亮牌
/*		List<XuanFengGangCard> baiFengGangCards = user.getBaiFengGangCards();
		for (XuanFengGangCard baiFengGangCard : baiFengGangCards) {
			List<Integer> cards = baiFengGangCard.getCards();
			for (Integer integer : cards) {
				Integer card = integer/4;
				if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
	                map.put(card, map.get(card).intValue() + 1);
	            }else{
	                map.put(card, 1);
	            }
			}
		}
		List<XuanFengGangCard> heiFengGangCards = user.getHeiFengGangCards();
		for (XuanFengGangCard heiFengGangCard : heiFengGangCards) {
			List<Integer> cards = heiFengGangCard.getCards();
			for (Integer integer : cards) {
				Integer card = integer/4;
				if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
	                map.put(card, map.get(card).intValue() + 1);
	            }else{
	                map.put(card, 1);
	            }
			}
		}*/
		//手牌中
		List<Integer> cards = user.getCards();
		for (Integer integer : cards) {
			Integer card = integer/4;
			if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
                map.put(card, map.get(card).intValue() + 1);
            }else{
                map.put(card, 1);
            }
		}
		Iterator<Integer> iterator = map.keySet().iterator();
		int siguiyi = 0;
		while (iterator.hasNext()) {
			Integer integer = iterator.next();
			Integer value = map.get(integer);
			if(value>=4){
				
				siguiyi++;
			}
		}
		user.setIsSiGuiYi(siguiyi);
		return siguiyi;
	}
	/**
	 * 保存杠牌次数
	 * @param user
	 * @param gangAnum
	 * @param gangMnum
	 * @param gangPnum
	 */
	private static void saveGangCiShu(User user, Integer gangAnum,
			Integer gangMnum, Integer gangPnum) {
		XuChangScore xuChangScore = user.getXuChangScore();
		xuChangScore.setGangAnum(xuChangScore.getGangAnum()+gangAnum);
		xuChangScore.setGangPnum(xuChangScore.getGangPnum()+gangPnum);
		xuChangScore.setGangMnum(xuChangScore.getGangMnum()+gangMnum);
		xuChangScore.setHuNum(xuChangScore.getHuNum()+1);
	}
	/**
	 * 得到用户的杠牌数量  计算中发白碰
	 * @param u
	 * @param game 
	 * @return
	 */
	private static void modifyUserLiangScore(Game game) {
		OneRoom room = game.getRoom();
		Map<String, User> seatMap = game.getSeatMap();
		if(game.isHaveLiangHei()){//有人亮东南西北
			liangHeiFeng(seatMap);
		}
		if(game.isHaveLiangBai()){//有人亮中发白
			int pengBai = 2;
			pengBaiFengScore(room, seatMap, pengBai);
		}else{
			int pengBai = 1;
			pengBaiFengScore(room, seatMap, pengBai);
		}
	}
	/**
	 * 有人亮东南西北 的时候对碰东南西北的处理
	 * @param seatMap
	 */
	private static void liangHeiFeng(Map<String, User> seatMap) {
		Iterator<String> iterator = seatMap.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			User user = seatMap.get(string);
			List<PengCard> pengCards = user.getPengCards();
			int pengNum = 0;
			//碰牌数组里面 东南西北
			for (PengCard pengCard : pengCards) {
				Integer integer = pengCard.getCards().get(0);
				if(integer>=120&&integer<=135){//东南西北碰
					pengNum++;
				}
			}
				//手牌中有东南西北的碰牌 
			List<Integer> cards = user.getCards();
			int dong = 0;
			int nan = 0;
			int xi = 0;
			int bei = 0;
			for (Integer card : cards) {
				if(card>=120&&card<=123){
					dong++;
				}else if(card>=124&&card<=127){
					nan++;
				}else if(card>=128&&card<=131){
					xi++;
				}else if(card>=132&&card<=135){
					bei++;
				}
			}
			if(dong>=3){
				pengNum++;
			}
			if(nan>=3){
				pengNum++;
			}
			if(xi>=3){
				pengNum++;
			}	
			if(bei>=3){
				pengNum++;
			}	
			
			if(pengNum>0){//碰中发白的个数
				user.setGangPnum(user.getGangPnum()+pengNum);
			}
		}
	}
	/**
	 * 有人亮 跟没有人亮 中发白   对碰中发白的处理
	 * @param room
	 * @param seatMap
	 * @param pengBai
	 */
	private static void pengBaiFengScore(OneRoom room,
			Map<String, User> seatMap, int pengBai) {
		Iterator<String> iterator = seatMap.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			User user = seatMap.get(string);
			List<PengCard> pengCards = user.getPengCards();
			int pengNum = 0;
			for (PengCard pengCard : pengCards) {
				Integer integer = pengCard.getCards().get(0);
				if(integer>=108&&integer<=119){//中发白碰
					pengNum++;
				}
			}
			//手牌中三个一样的中发白的数量
			List<Integer> cards = user.getCards();
			int zhong = 0;
			int fa = 0;
			int bai = 0;
			for (Integer card : cards) {
				if(card>=108&&card<=111){
					zhong++;
				}else if(card>=112&&card<=115){
					fa++;
				}else if(card>=116&&card<=119){
					bai++;
				}
			}
			if(zhong>=3){
				pengNum++;
			}
			if(fa>=3){
				pengNum++;
			}
			if(bai>=3){
				pengNum++;
			}		
			if(pengNum>0){//碰中发白的个数
				user.setGangPnum(user.getGangPnum()+(pengBai*pengNum));//一个亮后的中发白算四个明杠
			}
		}
	}
	/**
	 * 得到自己胡牌的类型(int)
	 * @param user
	 * @param cardId
	 * @param fengTianHuPai
	 * @return
	 */
	private static int getMyHuType(User user, int cardId,
			FengTianHuPai fengTianHuPai,boolean isDaiQingYiSe) {
		int[] intArrayForChi = TypeUtils.getChiArray(user.getChiCards());
		int[] intArrayForPeng = TypeUtils.getPengArray(user.getPengCards());
		int[] intArrayForGangM = TypeUtils.getDianGangArray(user.getGangCards());
		int[] intArrayForGangA = TypeUtils.getAnGangArray(user.getGangCards());
		int[] intArrayForDNXB = TypeUtils.getHeiFengArray(user.getHeiFengGangCards());
		int[] intArrayForZFB = TypeUtils.getBaiFengArray(user.getBaiFengGangCards());
		int[] intArrayForHand = TypeUtils.getArray(getArrayByList(user.getCards()));
		
		int huType = fengTianHuPai.getHuType(cardId/4, intArrayForHand, intArrayForChi, intArrayForPeng, intArrayForGangM,intArrayForGangA, intArrayForDNXB, intArrayForZFB,isDaiQingYiSe);
		return huType;
	}
	
	/**记录下用户的得分
	 * @param game
	 */
	public static void recordUserScore(Game game){
		List<User> userList = game.getUserList();
		int roomid = game.getRoom().getId();//房间号
		Date createDate = new Date();	
		VedioDao vedioDao = new VedioDao();
		Vedio vedio = new Vedio();
		String string = game.getHuiFang().toString();
		vedio.setRecord(string);
		vedioDao.saveVedio(vedio);
		int currentGame = game.getAlreadyTotalGame();//当前的局数
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			int score = user.getXuChangScore().getScore();
			int userid = user.getId();//用户的ID
			if(currentGame==1){//如果是第一局
				SumScoreDao sumScoreDao = new SumScoreDao();
				SumScore sumScore = new SumScore();
				sumScore.setRoomNumber(roomid+"");
				sumScore.setUserid(userid);
				sumScore.setHuPaiTotal(0);//胡牌次数
				sumScore.setJieGangTotal(0); //接杠次数
				sumScore.setAnGangTotal(0);//暗杠次数
				sumScore.setFinalScore(0);//最终成绩
				sumScore.setFangGangTotal(0);//放杠次数
				sumScore.setMingGangtotal(0);//明杠也称公杠次数
				sumScore.setCreateDate(createDate);
				sumScore.setNickName(user.getNickName());
				sumScore.setTotal(game.getRoom().getTotal());
				sumScore.setFengDingNum(game.getRoom().getFengDingNum());
				sumScore.setFangZhu(game.getRoom().getCreateUserId());
				sumScoreDao.saveSumScore(sumScore);//保存用户的房间号
				user.setSumScoreId(sumScore.getId());
			}
			UserScore userScore = new UserScore(userid, roomid,currentGame,score,createDate,vedio.getId(),user.getSumScoreId());
			userScoreDao.saveUserScore(userScore);
		}
	}
	

	/**设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
	 * @param game
	 * @param roomId 
	 */
	public static boolean setCurrentGameOver(Game game, int roomId) {
		int alreadyTotalGame = game.getAlreadyTotalGame();
		game.getGameStatusMap().put(alreadyTotalGame, GAME_END);
		game.setGameStatus(1);//游戏的状态变成出牌
		game.setStatus(GAGME_STATUS_OF_WAIT_START);//游戏等待
		game.setEndDate(new Date());
		//判断当前的游戏是否结束
		OneRoom room = game.getRoom();
//		int roomTotal = room.getTotal();
		if(isGameOverZong(room)){//游戏结束
			summarizedAll(game,roomId);
			game.setOver(true);//游戏结束
			game = null;
			return true;
		}
		return false;
	
	}
	/**设置当前的游戏结束,并且等待游戏开局，8/16局结束的时候结束游戏
	 * @param game
	 * @param roomId 
	 */
	public static void setCurrentGameOverByJieSan(Game game, int roomId) {
		int alreadyTotalGame = game.getAlreadyTotalGame();
		game.getGameStatusMap().put(alreadyTotalGame, GAME_END);
		game.setGameStatus(1);//游戏的状态变成出牌
		game.setStatus(GAGME_STATUS_OF_WAIT_START);//游戏等待
		game.setEndRemainCards(20);
		game.setEndDate(new Date());
		//判断当前的游戏是否结束
		summarizedAll(game,roomId);//总结算
		game.setOver(true);//游戏结束
		game = null;
		
	}
	
	/**判断游戏是否结束
	 * @param oneRoom
	 * @return
	 */
	private static boolean isGameOver(OneRoom oneRoom){
		boolean isOver = false;
		int alreadyGame = oneRoom.getAlreadyTotalGame()+1;
		int totalGame = oneRoom.getTotal();
		if(alreadyGame>=totalGame ){
			isOver = true;
		}
		return isOver;	
	}
	/**判断游戏是否结束
	 * @param oneRoom
	 * @return
	 */
	private static boolean isGameOverZong(OneRoom oneRoom){
		boolean isOver = false;
		int alreadyGame = oneRoom.getAlreadyTotalGame();
		int totalGame = oneRoom.getTotal();
		if(alreadyGame>=totalGame ){
			isOver = true;
		}
		return isOver;	
	}

	/**总结算，房间解散 
	 * @param game
	 * @param roomId 
	 */
	private static void summarizedAll(Game game, int roomId) {
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		int total = room.getAlreadyTotalGame();
		JSONObject outJSONObject = getSummarizeJsonObject(userList,total,game,roomId);
		JSONObject outJSONObjectWithJieSuan = getSummarizeJsonObjectWithJieSuan(userList,total,room);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(room.getId()+""), outJSONObject);
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.clearAll();//清空用户所有的属性
			try {
				RedisUtil.delKey("usRoomId" + user.getId(),REDIS_DB_FENGTIAN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		RedisUtil.delKey("ftRoomId"+room.getId(), REDIS_DB_FENGTIAN);
		//记录玩家的总成绩  
		recoredUserScore(outJSONObjectWithJieSuan, game);
		//先移除游戏中的map,后移除房间中的map 否则有空指针异常,顺序不可颠倒
		GameManager.removeGameWithRoomNumber(room.getId()+"");
		RoomManager.removeOneRoomByRoomId(room.getId()+"");
	}
	
	/**
	 * 记录玩家的总成绩
	 */
	public static void recoredUserScore(JSONObject jsonObject,Game game){
		JSONArray userArray = jsonObject.getJSONArray("userScoreArray");
		int roomNumber = game.getRoom().getRoomNumber();
		for(int i=0;i<userArray.length();i++){
			JSONObject user = userArray.getJSONObject(i);
			SumScore sumScore = new SumScore();
			sumScore.setRoomNumber(roomNumber+"");
			sumScore.setUserid(user.getInt("userId"));
			sumScore.setHuPaiTotal(0);//胡牌次数
			sumScore.setJieGangTotal(0); //接杠次数
			sumScore.setAnGangTotal(0);//暗杠次数
			sumScore.setFinalScore(user.getInt("finallyScore"));//最终成绩
			sumScore.setFangGangTotal(0);//放杠次数
			sumScore.setMingGangtotal(0);//明杠也称公杠次数
			sumScore.setCreateDate((Date)user.get("createDate"));
			sumScore.setTotal(user.getInt("juNum"));
			sumScore.setFengDingNum(user.getInt("fengDingNum"));
			sumScore.setId(user.getInt("sumScoreId"));
			sumScoreDao.modifySumScore(sumScore);
		}
	}
	
	/**得到结算的jsonObejct
	 * @param userList
	 * @param alreadyTotalGame 总圈数+1
	 * @param room 
	 * @return
	 */
	public static JSONObject getSummarizeJsonObjectWithJieSuan(List<User> userList,int alreadyTotalGame, OneRoom room) {
		JSONObject outJSONObject = new JSONObject();
		JSONArray userScoreArray = new JSONArray();
		int juNum = alreadyTotalGame; //当前圈数+1
		Date date = new Date();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			JSONObject userScoreJSONObject = new JSONObject();
			userScoreJSONObject.put("juNum", juNum);
			userScoreJSONObject.put("finallyScore", user.getXuChangScore().getCurrentScore());
			userScoreJSONObject.put("userId", user.getId());
			userScoreJSONObject.put("fengDingNum", room.getFengDingNum());
			userScoreJSONObject.put("createDate", date);
			userScoreJSONObject.put("sumScoreId", user.getSumScoreId());
			boolean fangZhu = user.isFangZhu();
			if(fangZhu){
				userScoreJSONObject.put("fangZhu", 1);
			}else{
				userScoreJSONObject.put("fangZhu", 0);
			}
			userScoreJSONObject.put(direction, user.getDirection());
			userScoreArray.put(userScoreJSONObject);
		}
		outJSONObject.put("userScoreArray", userScoreArray);
		return outJSONObject;
	}
	
	/**得到结算的jsonObejct
	 * @param userList
	 * @param alreadyTotalGame 总圈数+1
	 * @param game 
	 * @param roomId 
	 * @return
	 * {"m":"huTotal","userDir":0,
	 * "users":[{"userDir":0,"gangMnum":0,"gangPnum":0,"gangAnum":0,"scoreTotal":0,"timeCur":"","timeLast":""}]}}
	 */
	public static JSONObject getSummarizeJsonObject(List<User> userList,int alreadyTotalGame, Game game, int roomId) {
		JSONObject outJSONObject = new JSONObject();
		JSONArray userScoreArray = new JSONArray();
		outJSONObject.put("m", "huTotal");
		outJSONObject.put("userDir", 0);
		outJSONObject.put("roomId", roomId);
		outJSONObject.put("timeCur", DateUtils.getCurrentDate());
		outJSONObject.put("timeLast", DateUtils.getTimeLast(game.getStartDate(), game.getEndDate()));
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			JSONObject userScoreJSONObject = new JSONObject();
			userScoreJSONObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			userScoreJSONObject.put("huNum", user.getXuChangScore().getHuNum());
			userScoreJSONObject.put("dianpaoNum", user.getXuChangScore().getDianpaoNum());//明杠数
			userScoreJSONObject.put("gangPnum", user.getXuChangScore().getGangPnum());//明杠数
			userScoreJSONObject.put("gangMnum", user.getXuChangScore().getGangMnum());//碰杠数
			userScoreJSONObject.put("gangAnum", user.getXuChangScore().getGangAnum());//暗杠数
			userScoreJSONObject.put("scoreTotal", user.getXuChangScore().getCurrentScore());
			userScoreArray.put(userScoreJSONObject);
		}
		outJSONObject.put("users", userScoreArray);
		return outJSONObject;
	}
	
	/**得到结算的jsonObejct
	 * @param userList
	 * @param alreadyTotalGame 总圈数+1
	 * @param game 
	 * @param roomId 
	 * @return
	 * {"m":"huTotal","userDir":0,
	 * "users":[{"userDir":0,"gangMnum":0,"gangPnum":0,"gangAnum":0,"scoreTotal":0,"timeCur":"","timeLast":""}]}}
	 */
	public static JSONObject getSummarizeJsonObjectByHouTai(List<User> userList,int alreadyTotalGame, int roomId) {
		JSONObject outJSONObject = new JSONObject();
		JSONArray userScoreArray = new JSONArray();
		outJSONObject.put("m", "huTotal");
		outJSONObject.put("userDir", 0);
		outJSONObject.put("roomId", roomId);
		outJSONObject.put("timeCur", DateUtils.getCurrentDate());
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			JSONObject userScoreJSONObject = new JSONObject();
			userScoreJSONObject.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			userScoreJSONObject.put("huNum", user.getXuChangScore().getHuNum());
			userScoreJSONObject.put("dianpaoNum", user.getXuChangScore().getDianpaoNum());//明杠数
			userScoreJSONObject.put("gangMnum", user.getXuChangScore().getGangPnum());//明杠数
			userScoreJSONObject.put("gangPnum", user.getXuChangScore().getGangMnum());//碰杠数
			userScoreJSONObject.put("gangAnum", user.getXuChangScore().getGangAnum());//暗杠数
			userScoreJSONObject.put("scoreTotal", user.getXuChangScore().getCurrentScore());
			userScoreArray.put(userScoreJSONObject);
		}
		outJSONObject.put("users", userScoreArray);
		return outJSONObject;
	}

	public static List<Integer> getNewCards(List<Integer> myCards,int myGrabCard){
		List<Integer> myCardsCopyList =  new ArrayList<>();
		for(int i=0;i<myCards.size();i++){
			Integer card = myCards.get(i);
			if(card!=myGrabCard){
				myCardsCopyList.add(myCards.get(i));
			}
		}
		return myCardsCopyList;
	}
	
	public static int getCardType(int card){
		int result = 0;
	    if (card < 9 ) {//万
	    	result = 1;
		} else if (card < 18 ) {//筒
			result = 2;
		} else if (card < 27 ) {//条
			result = 3;
		} else if (card < 30 ) {//中发白
			result = 4;
		} else if (card < 34 ) {//东、南、西、北
			result = 5;
		}
		return result;
	}	
	
	/**通知所有的玩家 暗杠的牌  抓拍后的暗杠
	 * {"m":"zhuagangA","userDir":0,"cardId":0,"array":{0,1,2,3}}
	 * @param game
	 * @param removeCard 
	 * @param userDir 
	 * @param pengCards
	 */
	public static void notifyAllUserAnGang(Game game, List<Integer> gangCards,User user, Integer removeCard, Integer userDir) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhuagangA");
		outJsonObject.put("array", gangCards);
		outJsonObject.put("cardId", removeCard);
		outJsonObject.put("userDir", userDir);
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**通知所有的玩家杠的牌
	 * {"m":"chuGang","userDirChu":0,"userDirTip":0,"cardId":0,"array":{0,1,23}}
	 * @param game
	 * @param gangCards
	 * @param user
	 * @param userDirChu
	 * @param userDirTip
	 * @param cardId
	 */
	public static void notifyAllUserGang(Game game, List<Integer> gangCards,User user, int userDirChu, int userDirTip, int cardId) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("array", gangCards);
		outJsonObject.put("m", "chuGang");
		outJsonObject.put("userDirChu", userDirChu);
		outJsonObject.put("userDirTip", userDirTip);
		outJsonObject.put("cardId", cardId);
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	
	/**通知所有的玩家公杠的牌
	 * @param game
	 * @param userDir 
	 * @param cardId 
	 * @param pengCards
	 * {"m":"zhuagangP","userDir":0,"cardId":0,"array":{0,1,2,3}}
	 */
	public static void notifyAllUserGongGang(Game game, List<Integer> gangCards,User user, int cardId, int userDir) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("array", gangCards);
		outJsonObject.put("m", "zhuagangP");
		outJsonObject.put("userDir", userDir);
		outJsonObject.put("cardId", cardId);
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	/**
	 * 吃牌
	 * @param jsonObject
	 * @param ctx
	 */
	private void chi(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		boolean checkIsCanRun = checkIsCanRun(game,jsonObject,ctx,1);
		if(checkIsCanRun){
			nextChi(jsonObject, ctx, game,null);
		}	
	}
	/**
	 * 吃的第二步
	 * {"m":"chuChi","userDirChu":0,"userDirTip":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 * @param game
	 * @param direction 
	 */
	private void nextChi(JSONObject jsonObject, ChannelHandlerContext ctx,
			Game game, String direction) {
		System.out.println("nextChi--------");
		int cardId = jsonObject.getInt("cardId");
		int userDirChu = jsonObject.getInt("userDirChu");//出牌人方向
		int userDirTip = jsonObject.getInt("userDirTip");//提示人方向
		
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = getGamingUser(game, sessionUser.getDirection());
		if(direction!=null&&!direction.equals("")){
			gamingUser = game.getSeatMap().get(direction);
		}
		gamingUser.setUserCanPlay(true);
		boolean canChi = checkUserIsCanChi(gamingUser);
		if(!canChi){
			notifyUserError(ctx, "不可以吃");
			return;
		}
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(gamingUser.getDirection());
		List<Integer> cards = user.getCards();
		List<Integer> chiList = getChiList(cards, cardId);//得到可以吃的集合跟吃的类型数量
		if(chiList.size()<2){
			notifyUserError(ctx, "异常吃牌");
			return;
		}else if(chiList.size()<4){//只可以吃一套牌
			List<Integer> userChiCards = user.userChiCards(chiList,cardId,game);
			user.addUserChiCards(userChiCards,game.getFangChiUser().getDirection());
			user.setUserCanPlay(true);//该玩家可以出牌
			game.setGameStatus(1);//游戏的状态变为出牌
			game.setYaoPaiStatus(3);//要牌状态
			game.setDirec(user.getDirection());
			user.setLastChuPaiDate(new Date());
			user.setCanChi(false);
			game.setCanChi(false);
			user.clearAllCan();
			//记录玩家吃的牌
			notifyAllUserChi(game, userChiCards,user,userDirChu,userDirTip,cardId);
			int indexOf = userChiCards.indexOf(cardId);
			userChiCards.remove(indexOf);
			userChiCards.add(0, cardId);
			HuiFangUitl.getChiPai(game.getHuiFang(), gamingUser, userChiCards,TypeUtils.getStringDir(userDirChu));
			//判断用户能不能 亮 杠
			int total =userNotWin(game, game.getRemainCards(), cardId, user,false);
			if(total>0){
				//抓牌提示
				notifyUserZhaPaiTip(game,user,cardId);
			}else{
				notifyUserChuPai(user,game);
			}
		}else{
			//通知用户可以吃的牌型
//			notifyUserCanChiType(cardId, gamingUser, game, canChi,chiList);
		}
	}
	
	/**
	 * 检查用户是否可以吃
	 */
	private boolean checkUserIsCanChi(User user) {
		boolean result =  user.isCanChi();
		return result;
	}

	/**碰牌或杠牌 ,出完牌后改变出牌的状态
	 * @param jsonObject
	 * @param ctx
	 */
	private void peng(JSONObject jsonObject, ChannelHandlerContext ctx) {
		Game game = getGame(ctx);
		boolean checkIsCanRun = checkIsCanRun(game,jsonObject,ctx,2);
		if(checkIsCanRun){
			nextPeng(jsonObject, ctx, game,null);
		}
	}
	/**
	 * 碰的第二步
	 * {"m":"chuPeng","userDirChu":0,"userDirTip":0,"cardId":0}
	 * @param jsonObject
	 * @param ctx
	 * @param game
	 * @param direction 
	 */
	private void nextPeng(JSONObject jsonObject, ChannelHandlerContext ctx,
			Game game, String direction) {
		System.out.println("nextPeng--------");
		int cardId = jsonObject.getInt("cardId");
		int userDirChu = jsonObject.getInt("userDirChu");
		int userDirTip = jsonObject.getInt("userDirTip");
		
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = getGamingUser(game, sessionUser.getDirection());
		if(direction!=null&&!direction.equals("")){
			gamingUser = game.getSeatMap().get(direction);
		}
		gamingUser.setUserCanPlay(true);
		boolean canPeng = checkUserIsCanPeng(gamingUser);
		if(!canPeng){
			notifyUserError(ctx, "不可以碰");
			return;
		}
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(gamingUser.getDirection());
		List<Integer> cards = user.getCards();
		List<Integer> pengList = getPengList(cards, cardId);//得到可以碰的集合
		if(pengList.size()!=2){
			notifyUserError(ctx, "异常碰牌");
			return;
		}
		user.userPengCards(pengList);//玩家碰牌
		pengList.add(cardId);
		user.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
		//移除放碰的人被碰出的牌cardId
		List<Integer> myPlays = game.getFangPengUser().getMyPlays();
		int indexOf = myPlays.indexOf(cardId);
		myPlays.remove(indexOf);
		user.setUserCanPlay(true);//该玩家可以出牌
		game.setGameStatus(1);//游戏的状态变为出牌
		game.setYaoPaiStatus(3);//要牌状态
		game.setDirec(user.getDirection());
		game.setLastMap(user, cardId);
		user.setLastChuPaiDate(new Date());
		user.setKaiMen(true);
		clearAllUserCan(game);
		game.clearPriority();
		//记录玩家碰的牌
		notifyAllUserPeng(game, pengList,user,userDirChu,userDirTip,cardId);
		int index = pengList.indexOf(cardId);
		pengList.remove(index);
		pengList.add(0, cardId);
		HuiFangUitl.getPengPai(game.getHuiFang(), gamingUser, pengList,TypeUtils.getStringDir(userDirChu));
		int total = 0;
		//判断用户有没有其他可以的
		total = PlayCardUtils.userNotWin(game, game.getRemainCards(), user, total);
		if(total>0){
			notifyUserZhaPaiTip(game,user,cardId);
		}else{
			notifyUserChuPai(user,game);
		}
	}
	/**
	 * 清空用户所有可以进行的操作  比如可以听 可以吃 可以胡 可以碰 都置成false
	 * @param game
	 */
	private void clearAllUserCan(Game game) {
		Map<String, User> seatMap2 = game.getSeatMap();
		Iterator<String> iterator = seatMap2.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			User user2 = seatMap2.get(key);
			user2.clearAllCan();
			user2.setCanKai(false);
		}
	}
	
	/**
	 * 检测游戏是否可以继续进行
	 * @param game
	 * @param ctx 
	 * @param jsonObject 
	 * @param type  操作的类型  1是碰牌
	 */
	private boolean checkIsCanRun(Game game, JSONObject jsonObject, ChannelHandlerContext ctx,int type) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		User gamingUser = getGamingUser(game, user.getDirection());
		gamingUser.setCanChi(false);
//		检测是否有可以操作的用户没有操作  比如 吃碰胡  必须全部点击完成才可以进行操作
		Priority priority = game.getPriority();
		priority.setCurrentNumber(priority.getCurrentNumber()+1);
		priority.getUserInfo().put(type, jsonObject);
		//首先把该用户选择的类型以外的类型都移除掉
		Map<Integer, String> typeMap = priority.getTypeMap();
		Iterator<Integer> iterator = typeMap.keySet().iterator();
		if(typeMap.size()>0){
			List<Integer> list = new ArrayList<>();
			while (iterator.hasNext()) {
				Integer key = iterator.next();
				String dir = typeMap.get(key);
				if(key!=type&&dir.equals(user.getDirection())){
					list.add(key);
				}
			}
			for (Integer integer : list) {
				typeMap.remove(integer);
			}
		}
		if(priority.getCurrentNumber()<priority.getTotal()){
			return false;
		}else{//可以进行优先级逻辑
			if(typeMap.size()>0){
				//有用户的操作
				int number = 0;
				Iterator<Integer> iterator1 = typeMap.keySet().iterator();
				while (iterator1.hasNext()) {
					Integer next = iterator1.next();
					if(next>number){
						number = next;
					}
				}
				//number是优先级最高的操作  执行这个操作  根据number
				String direction = typeMap.get(number);
				judgeNumberToRun(number,direction,jsonObject,ctx,game);
				return false;
			}else{
				game.clearPriority();
				return true;
			}
		}
	}
	/**
	 * 根据最高优先级  去执行不同的操作
	 * @param number   优先级数 1吃 2碰 3杠 4上家胡 5对家胡 6下家胡
	 * @param direction
	 * @param ctx 
	 * @param jsonObject 
	 * @param game 
	 */
	private void judgeNumberToRun(int number, String direction, JSONObject jsonObject, ChannelHandlerContext ctx, Game game) {
		JSONObject object = game.getPriority().getUserInfo().get(number);
		jsonObject = object;
		if(number==1){//吃
			nextRealChi(jsonObject, ctx, game,direction);
		}else if(number==2){//碰
			nextPeng(jsonObject, ctx, game,direction);
		}else if(number==3){//杠
			nextGang(jsonObject, ctx, game,direction);
		}else if(number==4){//上家胡
			nextHu(jsonObject,ctx, game,direction);
		}else if(number==5){//对家胡
			nextHu(jsonObject,ctx, game,direction);
		}else if(number==6){//下家胡
			nextHu(jsonObject,ctx, game,direction);
		}else{
			logger.info("没有该优先级操作======"+number);
		}
		game.clearPriority();
	}
	/**通知用户不可碰
	 * @param ctx
	 * @param type 1 、不可以碰，2、不可以杠
	 */
	public void notifyUserCanNotPengOrGang(ChannelHandlerContext ctx,int type){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(code, error);
		switch (type) {
		case 1:
			jsonObject.put(discription, "不可以碰");
			logger.error("不可以碰");
			break;
		case 2:
			jsonObject.put(discription, "不可以杠");
			logger.error("不可以杠");
			break;
		}
		ctx.write(jsonObject.toString());
	}
	
	public boolean checkUserIsCanPeng(User user){
		boolean result =  user.isCanPeng();
		return result;
	}
	
	/**检测用户是否可以杠
	 * @param user
	 * @return
	 */
	public boolean checkUserIsCanGang(User user){
		boolean result =  user.isCanPengGang();
		return result;
	}
	
	/**得到可以碰的集合
	 * @param cards
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getPengList(List<Integer> cards,int cardId){
		List<Integer> list = new ArrayList<>();
		int total = 0;
		for(Integer card:cards){
			if(cardId/4==card/4&&total<2){
				list.add(card);
				total ++ ;
			}
			if(total==2){
				break;
			}
		}
		return list;
	}
	/**得到可以吃的集合
	 * @param cards
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getChiList(List<Integer> cards,int cardId){
		List<Integer> list = new ArrayList<>();

		if(cardId<=35){//万牌
			for (Integer card : cards) {
				if(card<=35&&card>=0){
					list.add(card/4);
				}
			}
		}else if(cardId<=71){//筒
			for (Integer card : cards) {
				if(card<=71&&card>=36){
					list.add(card/4);
				}
			}
		}else if(cardId<=107){//条
			for (Integer card : cards) {
				if(card<=107&&card>=72){
					list.add(card/4);
				}
			}
		}
		
		List<Integer> arrayList = new ArrayList<>();
		if(list.contains(cardId/4+1)&&list.contains(cardId/4+2)){
			arrayList.add(cardId/4+1);
			arrayList.add(cardId/4+2);
		}
		if(list.contains(cardId/4-1)&&list.contains(cardId/4-2)){
			arrayList.add(cardId/4-1);
			arrayList.add(cardId/4-2);
		}
		if(list.contains(cardId/4-1)&&list.contains(cardId/4+1)){
			arrayList.add(cardId/4-1);
			arrayList.add(cardId/4+1);
		}
		return arrayList;
	}
	
	
	/**得到可以碰的集合
	 * @param cards
	 * @param cardId
	 * @return
	 */
	public static List<Integer> getGangList(List<Integer> cards,int cardId){
		List<Integer> list = new ArrayList<>();
		int total = 0;
		for(Integer card:cards){
			if(card/4==cardId/4&&total<4){//4444 万 到第四个4停止
				list.add(card);
				total ++ ;
			}
		}
		return list;
	}
	
	/**通知所有的玩家游戏方向改变
	 * @param user
	 */
	public void notifyAllUserDirectionChange(User user){
		Game game = getGame(user);
		String nextDirection = user.getDirection();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playDirection");
		outJsonObject.put("direction", nextDirection);
		outJsonObject.put("description", "出牌的的方向");
		game.setDirec(user.getDirection());//出牌的方向改变
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()+""), outJsonObject);
	}

	/**通知所有的玩家碰的牌
	 * @param game
	 * @param pengCards
	 * @param user 
	 * @param cardId 
	 * @param userDirTip 
	 * @param userDirChu 
	 */
	public static void notifyAllUserPeng(Game game, List<Integer> pengCards, User user, int userDirChu, int userDirTip, int cardId) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "chuPeng");
		outJsonObject.put("userDirChu", userDirChu);
		outJsonObject.put("userDirTip", userDirTip);
		outJsonObject.put("cardId",cardId);
		outJsonObject.put("array", pengCards);
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	/**通知所有的玩家吃的牌
	 * @param game
	 * @param pengCards
	 * @param user 
	 * @param userDirTip 
	 * @param userDirChu 
	 * @param cardId 
	 * @param chiList 
	 */
	public static void notifyAllUserChi(Game game, List<Integer> chiCards, User user, int userDirChu, int userDirTip, int cardId) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "chuChi");
		outJsonObject.put("userDirChu",userDirChu);
		outJsonObject.put("userDirTip", userDirTip);
		outJsonObject.put("cardId", cardId);
		outJsonObject.put("array", chiCards);//吃牌数组
		List<ChannelHandlerContext> userIoSessionList = game.getRoom().getUserIoSessionList();
		NotifyTool.notifyIoSessionList(userIoSessionList, outJsonObject);
	}
	
	/**得到游戏
	 * @param session
	 * @return
	 */
	public static Game getGame(ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(user);
		return game;
	}
	
	/**出牌,清空该用户的没有出牌次数
	 * @param jsonObject
	 * @param ctx
	 */
	public void chuPai(JSONObject jsonObject, ChannelHandlerContext ctx){
		int cardId = jsonObject.getInt("cardId");//出牌的牌号
		User sessionUser = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		Game game = getGame(sessionUser);
		User gamingUser = getGamingUser(game, sessionUser.getDirection());
		Map<String, User> seatMap = game.getSeatMap();
		String direction = gamingUser.getDirection();//得到当前的座次
		User user = seatMap.get(direction);
		int removeCardId = user.chuPai(new Integer(cardId));//出牌
		user.getMyPlays().add(removeCardId);
		game.setLastMap(user,cardId);
		game.setGameStatus(0);
		if(removeCardId<0){
			notifyUserError(ctx, "同时出牌出错");
			return;
		}
		JSONObject outJsonObject = getChuPaiOutJSONObject(cardId, user);
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
//		String huiFang = game.getHuiFang().toString();
//		logger.info(huiFang.toString());
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(sessionUser.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		game.clearPriority();
//		game.setCanLiuLei(false);
		//判断其他人是否可以胡牌
		newanalysis(cardId, gamingUser, game);//继续分析是下一个人出牌还是能够碰牌和杠牌
		
	}

	/**
	 * 分析有没有用户可以胡牌 杠牌 碰牌 吃牌
	 * @param cardId
	 * @param user   出牌的用户
	 * @param game
	 * @param isAfterGang 
	 */
	private static void newanalysis(int cardId, User user, Game game) {
		//判断后三个用户可不可以进行操作 (吃碰杠胡等)
		//依次判断用户可不可以胡 碰杠 吃
		Map<String, User> seatMap = game.getSeatMap();
		OneRoom room = game.getRoom();
		Iterator<String> iterator = seatMap.keySet().iterator();
		//计算可以碰牌或杠牌的人
		String nextDirection = getNextDirection(user.getDirection(), game.getRoom().getTotalUser());
		int caozuo = 0;
		while (iterator.hasNext()) {
			String nextDir = iterator.next();
			User u = seatMap.get(nextDir);
			u.clearAllCan();
			if(user.getId()!=u.getId()){
				//先判断用户有没有听牌
				if(u.getTingpaiState()==1){//已经听牌
					//判断该牌能不能胡牌
					boolean hu = useIsCanHu(cardId, game, u);
					if(hu){//可以胡
						if(game.isAfterGang()){
							game.setCanLiuLei(true);
						}
						game.setGameStatus(0);
						u.setCanWin(true);
						//加入决策优先级 可以胡牌  
						Priority priority = game.getPriority();
						List<String> huDir = new ArrayList<String>();
						huDir.add(u.getDirection());
						priority.setHuDir(huDir);
						int sort = getSort(user,u,room);
						jueCeMap(u, priority,sort);
						game.setPriority(priority);
						game.setCanWin(true);
						game.setFangPaoUser(user);
						game.setCanHuUser(u);
						caozuo++;
					}else{
						//不能胡 判断能不能杠不影响听牌
						List<Integer> pengOrGang = checkUserIsCanPeng(cardId, u);
						if(pengOrGang.size()==3){//可以杠
							//有杠牌用户
							//是自己并且可以杠  去判断影不影响胡牌
							//不影响胡牌就可以杠
							HuPai huPai = new HuPai();
							List<Integer> newCards = HuPai.getNewListFromOldList(u.getCards());
							List<PengCard> pengCards = HuPai.getNewTypeListFromOldList(u.getPengCards());
							List<GangCard> newGangCards = HuPai.getNewTypeListFromOldList(u.getGangCards());
							List<ChiCard> chiCards1= HuPai.getNewTypeListFromOldList(u.getChiCards());
							List<XuanFengGangCard> baiGangCards = HuPai.getNewTypeListFromOldList(u.getBaiFengGangCards());
							List<XuanFengGangCard> heiGangCards = HuPai.getNewTypeListFromOldList(u.getHeiFengGangCards());
							Collections.sort(newCards);
							//手牌中移除可以杠的牌
							for(int i=0;i<pengOrGang.size();i++){
								Integer everyGangCard = pengOrGang.get(i);
								int indexOf = newCards.indexOf(everyGangCard);
								if(indexOf>-1){
									newCards.remove(indexOf);
								}
							}
							//添加到杠牌数组
							pengOrGang.add(cardId);
							GangCard gangCard = new GangCard(0, pengOrGang);
							newGangCards.add(gangCard);
							boolean ting = huPai.isTing(newCards, pengCards, newGangCards, chiCards1, baiGangCards, heiGangCards,room);
							if(ting){
								game.setGameStatus(0);//可以杠牌状态 
								u.setCanPengGang(true);
								//加入决策优先级可以杠牌
								Priority priority = game.getPriority();
								jueCeMap(u, priority,3);
								priority.setGangDir(u.getDirection());
								game.setPriority(priority);
								game.setFangGangUser(user);
								game.setCanGangUser(u);
								game.setCanPengGang(true);
								caozuo++;
							}
						}
					}
				}else{//没有听牌
					//判断能不能胡牌
					boolean hu = useIsCanHu(cardId, game, u);
					if(hu){//可以胡 
						if(game.isAfterGang()){
							game.setCanLiuLei(true);
						}
						game.setGameStatus(0);
						u.setCanWin(true);
						//加入决策优先级 可以胡牌  
						Priority priority = game.getPriority();
						int sort = getSort(user,u,room);
						jueCeMap(u, priority,sort);
						List<String> huDir = new ArrayList<String>();
						huDir.add(u.getDirection());
						priority.setHuDir(huDir);
						game.setPriority(priority);
						game.setFangPaoUser(user);
						game.setCanWin(true);
						game.setCanHuUser(u);
						caozuo++;	
					}
					//判断能不能杠或者碰
					List<Integer> pengGang = checkUserIsCanPeng(cardId, u);
					if(pengGang.size()==3){//可以公杠
						game.setGameStatus(0);
						u.setCanPengGang(true);
						//加入决策优先级可以杠牌
						Priority priority = game.getPriority();
						jueCeMap(u, priority,3);
						priority.setGangDir(u.getDirection());
						game.setPriority(priority);
						game.setFangGangUser(user);
						game.setCanPengGang(true);
						game.setCanGangUser(u);
						caozuo++;	
						u.setCanPeng(true);
						//加入决策优先级可以碰牌
						jueCeMap(u, priority,2);
						priority.setPengDir(u.getDirection());
						game.setPriority(priority);
						game.setFangPengUser(user);
						game.setCanPeng(true);
						game.setCanPengUser(u);
						caozuo++;
					}else if(pengGang.size()==2){//可以碰
						game.setGameStatus(0);//可以碰牌状态 
						u.setCanPeng(true);
						//加入决策优先级可以碰牌
						Priority priority = game.getPriority();
						jueCeMap(u, priority,2);
						priority.setPengDir(u.getDirection());
						game.setPriority(priority);
						game.setFangPengUser(user);
						game.setCanPeng(true);
						game.setCanPengUser(u);
						caozuo++;
					}
					if(room.isChiKaiMen()){//如果用户选择了只碰开门 
						if(u.getDirection().equals(nextDirection)&&u.isKaiMen()){//是出牌人的下家
							//判断可不可以吃牌
							boolean canChi = judgeUserIsCanChi(u,cardId,game);
							if(canChi){//可以吃牌
								u.setCanChi(true);
								game.setGameStatus(0);
								//加入决策优先级
								Priority priority = game.getPriority();
								jueCeMap(u, priority,1);
								priority.setChiDir(u.getDirection());
								game.setPriority(priority);
								game.setCanChi(true);
								game.setFangChiUser(user);
								game.setCanChiUser(u);
								caozuo++;
							}
						}
					}else{
						if(u.getDirection().equals(nextDirection)){//是出牌人的下家
							//判断可不可以吃牌
							boolean canChi = judgeUserIsCanChi(u,cardId,game);
							if(canChi){//可以吃牌
								u.setCanChi(true);
								game.setGameStatus(0);
								//加入决策优先级
								Priority priority = game.getPriority();
								jueCeMap(u, priority,1);
								priority.setChiDir(u.getDirection());
								game.setPriority(priority);
								game.setCanChi(true);
								game.setFangChiUser(user);
								game.setCanChiUser(u);
								caozuo++;
							}
						}
					}
					
				}
				
			}
		}
		game.setAfterGang(false);
		if(caozuo>0){
			//有胡牌杠牌碰牌吃牌的命令
			//给用户发送指令
			Priority priority = game.getPriority();
			Map<String, Integer> seatMap2 = priority.getSeatMap();
			int size =	seatMap2.size();
			priority.setTotal(size);
			priority.setCurrentNumber(0);
			notifyAllUserChuTip(user,room,cardId);//通知用户出牌提示
		}else{//下个用户抓牌
			String direc = game.getDirec();
			String nextDirection2 = getNextDirection(direc, room.getTotalUser());
			game.setDirec(nextDirection2);
			if(chouZhuang(game.getRemainCards(),game)){
				afterChouZhuang(game, user);//臭庄之后处理
				return;
			}
			nextUserDrawCards(cardId, user, game);
		}		
	}
	
	/**
	 * 通知用户出牌
	 * {"m":"ygncp","userDir":0}
	 * @param game
	 * @param win
	 */
	private static void notifyUserChuPai(Game game, User chuPaiUser) {
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		JSONObject jsonObject = new JSONObject();
		for (User user : userList) {
			if(user.getId()==chuPaiUser.getId()){
				jsonObject.put("m", "ygncp");
				jsonObject.put("userDir", room.getUserDir(user.getDirection()));
				user.setLastChuPaiDate(new Date());//设置庄家第一次出牌时间
				break;
			}
		}
		NotifyTool.notifyIoSessionList(room.getUserIoSessionList(), jsonObject);
	}
	/**
	 * 根据胡牌方向得到优先级
	 * @param user 出牌用户
	 * @param u	分析的用户
	 * @return
	 */
	private static int getSort(User user,User u,OneRoom room) {
		String nowDirection = user.getDirection();
		if(u.getDirection().equals(nowDirection)){
			return 7;
		}
		if(room.getTotalUser()==4){//四人
			String nextDirection = getNextDirection(nowDirection, room.getTotalUser());
			String otherDirection = getNextDirection(nextDirection, room.getTotalUser());
			if(u.getDirection().equals(nextDirection)){
				return 6;
			}else if(u.getDirection().equals(otherDirection)){
				return 5;
			}else{
				return 4;
			}
		}else if(room.getTotalUser()==2){//二人
			return 6;
		}
		return 0;
	}
	/**
	 * 加入决策
	 * @param u
	 * @param priority
	 * @param sort
	 */
	private static void jueCeMap(User u, Priority priority,Integer sort) {
		Map<String, Integer> seatMap = priority.getSeatMap();
		Map<Integer, String> typeMap = priority.getTypeMap();
		typeMap.put(sort, u.getDirection());
		seatMap.put(u.getDirection(), sort);
	}
	/**
	 * 通知其余玩家的出牌提示  吃碰杠胡 chuTip
	 * {"m":"tipForChupai","userDirChu":0,"userDirTip":0,"cardId":0,"isChi":false,"isPeng":false,"isGangM":false,"isHu":false}
	 * @param room 
	 * @param cardId 
	 * @param isAfterGang 
	 * @param canPengOrGangUser
	 */
	private static void notifyAllUserChuTip(User user, OneRoom room, int cardId) {
		List<User> userList = room.getUserList();
		for (User u : userList) {
			if(user.getId()!=u.getId()){
				if(u.isCanChi()||u.isCanPeng()||u.isCanPengGang()||u.isCanWin()){
					JSONObject jsonO = new JSONObject();
					jsonO.put("m", "tipForChupai");
					jsonO.put("userDirChu", TypeUtils.getUserDir(user.getDirection()));
					jsonO.put("userDirTip", TypeUtils.getUserDir(u.getDirection()));
					jsonO.put("isChi", u.isCanChi());
					jsonO.put("isPeng", u.isCanPeng());
					jsonO.put("isGangM", u.isCanPengGang());
					jsonO.put("isHu", u.isCanWin());
					jsonO.put("cardId", cardId);
					NotifyTool.notify(u.getIoSession(), jsonO);
				}else{
					continue;
				}
			}
		}
	}
	/**
	 * 用户拿到这张牌可不可以胡牌
	 * @param cardId
	 * @param game
	 * @param u
	 * @return
	 */
	private static boolean useIsCanHu(int cardId, Game game, User u){
		HuPai huPai = new HuPai();
		List<Integer> newCards = HuPai.getNewListFromOldList(u.getCards());
		List<PengCard> pengCards = HuPai.getNewTypeListFromOldList(u.getPengCards());
		List<GangCard> gangCards = HuPai.getNewTypeListFromOldList(u.getGangCards());
		List<ChiCard> chiCards = HuPai.getNewTypeListFromOldList(u.getChiCards());
		List<XuanFengGangCard> baiFengGangCards = HuPai.getNewTypeListFromOldList(u.getBaiFengGangCards());
		List<XuanFengGangCard> heiFengGangCards = HuPai.getNewTypeListFromOldList(u.getHeiFengGangCards());
		newCards.add(cardId);
		Collections.sort(newCards);
		boolean hu = huPai.isHu(newCards, pengCards, gangCards, chiCards,baiFengGangCards,heiFengGangCards, game.getRoom());
		return hu;
	}
	
	/**判断是否有搭牌
	 * @param cards
	 * @param baiDa
	 * @return
	 */
	public static boolean isHaveDaPai(List<Integer> cards,int baiDa){
		boolean result =  false;
		int number = baiDa /4;
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==number){
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**给用户提示错误信息,不可以出牌
	 * @param session
	 * @param result 
	 */
	public void notifyUserError(ChannelHandlerContext session, String discription){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(method, "gameError");
		session.write(jsonObject.toString());
		logger.error(discription);
	}
	
	
	/**检测用户的权限
	 * @param cardId 
	 * @return -1 不该出牌 -2牌不存在 -3 不可以出牌
	 */
	public int checkPower(Game game,User user, int cardId){
		int result = 0;
		User gamingUser = game.getGamingUser(user.getDirection());
		String direc = game.getDirec();
		if(!direc.equals(gamingUser.getDirection())){
			return -1;
		}
		List<Integer> cards = game.getSeatMap().get(gamingUser.getDirection()).getCards();
		int index = MathUtil.binarySearch(cardId, cards);
		if(index<0){
			return -2;
		}
		return result;
	}
	
	/**得到出牌的json对象
	 * @param cardId
	 * @param u
	 * @return
	 */
	public static JSONObject getChuPaiOutJSONObject(int cardId, User u) {
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("cardId", cardId);//打出的牌
		outJsonObject.put("userDir", TypeUtils.getUserDir(u.getDirection()));
		outJsonObject.put("m", "paiChu");
		return outJsonObject;
	}

	

	/**
	 * 判断用户是不是可以吃牌
	 * @param nextUser 下家
	 * @param cardId 上家打出的牌  需判断能不能吃牌
	 * @param game 
	 * @return
	 */
	private static boolean judgeUserIsCanChi(User nextUser,Integer cardId, Game game) {
		if(cardId<108){//是三色才可以吃
			List<Integer> cards = nextUser.getCards();//获得用户手牌
			LinkedList<Integer> wanList = new LinkedList<>();
			LinkedList<Integer> tongList = new LinkedList<>();
			LinkedList<Integer> tiaoList = new LinkedList<>();
			for (Integer card : cards) {
				if(card<=35){//万牌
					wanList.add(card/4);
				}else if(card<=71){//筒
					tongList.add(card/4);
				}else if(card<=107){//条
					tiaoList.add(card/4);
				}
			}
			boolean wanBool = false;
			boolean tongBool = false;
			boolean tiaoBool = false;
			if(cardId<=35){//万牌
				//判断集合里面有没有可以吃的牌
				wanBool = judgeListHaveChi(wanList,cardId);
			}else if(cardId<=71){//筒
				tongBool = judgeListHaveChi(tongList,cardId);
			}else if(cardId<=107){//条
				tiaoBool = judgeListHaveChi(tiaoList,cardId);
			}
			if(wanBool||tongBool||tiaoBool){
				nextUser.setChi(1);
				nextUser.setCanChi(true);
				game.setCanChi(true);
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args) {
/*		HuPai huPai = new HuPai();
		int[] array = {0,1,4,5,8,9,12,13,19,20,24};
		List<Integer> arrayToList = huPai.arrayToList(array);
		User user = new User();
		user.setCards(arrayToList);
		boolean judgeUserIsCanChi = judgeUserIsCanChi(user, 35);
		System.out.println(judgeUserIsCanChi);*/
		
/*		int[] A = {1,5,7,2,4}; 
		int[] B = {4,2,3,7,9,10,12}; 
		List<Integer> aList = new ArrayList<>();
		List<Integer> bList = new ArrayList<>();
		int anum = 0;
		for (int i : A) {
			if(i<5){
				anum++;
			}
			aList.add(i);
		}
		int bnum = 0;
		for (int i : B) {
			if(i>5){
				bnum++;
			}
			bList.add(i);
		}
		
		if(anum>bnum){
			Collections.sort(aList);
			System.out.println(aList);
		}else{
			Collections.sort(bList);
			System.out.println(bList);
		}*/
	}
	
	/**
	 * 判断集合里面有没有可以吃的牌
	 * @param wanList
	 * @param cardId
	 * @return
	 */
	private static boolean judgeListHaveChi(LinkedList<Integer> list,
			Integer cardId) {
		boolean flag = (list.contains(cardId/4+1)&&list.contains(cardId/4+2))||(list.contains(cardId/4-1)&&list.contains(cardId/4-2))||(list.contains(cardId/4+1)&&list.contains(cardId/4-1));
		if(flag){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param newCards 当前手牌
	 * @param cardId 出的牌
	 * @param canPengOrGangUser可以碰或杠的用户 
	 * @param newPengCards
	 * @param newGangCards
	 * @param user出牌的用户
	 */
	public static void removeGangCardAndAddBaiDa(List<Integer> newCards,int cardId,User canPengOrGangUser,List<PengCard> newPengCards,List<GangCard> newGangCards,User user){
		int cardNum = cardId/4;
		List<Integer> removeList = new ArrayList<>();
		for(int i=0;i<newCards.size();i++){
			Integer card = newCards.get(i);
			if(card/4==cardNum){
				removeList.add(card);
			}
		}
		removeList.add(cardId);
		if(removeList.size()==4){//是杠
			newGangCards = canPengOrGangUser.getGangCards();
			GangCard gangCard = new GangCard(2, removeList);
			newGangCards.add(gangCard);
		}else if(removeList.size()==3){//是碰
			newPengCards = canPengOrGangUser.getPengCards();
			PengCard pengCard = new PengCard();
			pengCard.addPengCard(removeList, user.getDirection());
			newPengCards.add(pengCard);
		}
		removeList.remove(removeList.indexOf(cardId));
		newCards.removeAll(removeList);
		Collections.sort(newCards);
	}
	
	/**下一个用户抓牌
	 * 
	 * @param cardId 当前的用户出的牌
	 * @param user 当前的用户
	 * @param game
	 */
	private static void nextUserDrawCards(int cardId, User user, Game game) {
		String nextDirection = getNextDirection(user.getDirection(),game.getRoom().getTotalUser());
		userDrawCard(game, nextDirection,false);//用户抓牌
	}
	
	/**自动碰牌和杠牌
	 * @param canPengOrGangUser
	 */
	public static void autoPengOrGang(User canPengOrGangUser,Game game) {
		if(canPengOrGangUser.getPengOrGang()==1){//可以碰牌
			List<Integer> pengList = canPengOrGangUser.getPengOrGangList();
			canPengOrGangUser.userPengCards(pengList);
			canPengOrGangUser.addUserPengCards(pengList,game.getFangPengUser().getDirection());//用户添加碰出的牌
			//记录玩家碰的牌
			HuiFangUitl.getPengPai(game.getHuiFang(), canPengOrGangUser, pengList,game.getFangPengUser().getDirection());
			game.setDirec(canPengOrGangUser.getDirection());//重新改变游戏的方向
			//碰牌后游戏的状态变为出牌
			game.setGameStatus(0);
			canPengOrGangUser.setLastChuPaiDate(new Date());
			if(canPengOrGangUser.isAuto()){
//				autoChuPai(game);//自动出牌
			}
		}else if(canPengOrGangUser.getPengOrGang()==2){//可以杠牌
			List<Integer> gangCards = canPengOrGangUser.getPengOrGangList();
			canPengOrGangUser.userGangCards(gangCards); 
			//记录玩家杠的牌
			canPengOrGangUser.recordUserGangCards(0, gangCards);
			//记录玩家杠的牌
			HuiFangUitl.getGangPai(game.getHuiFang(), canPengOrGangUser, gangCards,game.getFangGangUser().getDirection());

			//该玩家在抓一张牌 
			userDrawCard(game, canPengOrGangUser.getDirection(),false);
		}
	}
	
	/**
	 * 自动出牌
	 * @param isAfterGang 
	 */
	public static void autoChuPai(Game game, boolean isAfterGang){
		OneRoom room = game.getRoom();
		String direc = game.getDirec();
		Map<String, User> seatMap = game.getSeatMap();
		User user = seatMap.get(direc);
		user.setUserCanPlay(true);
		int cardId = user.autoChuPai();//自动出的牌
		user.getMyPlays().add(cardId);
		game.setGameStatus(0);
		game.setLastMap(user,cardId);
		if(cardId<0){
			logger.info("可能在同一时间打牌了");
			return;
		}
		try {
			Thread.currentThread().sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//记录玩家出的牌
		HuiFangUitl.getChuPai(game.getHuiFang(), user, cardId);
		JSONObject outJsonObject = PlayGameService.getChuPaiOutJSONObject(cardId, user);
		NotifyTool.notifyIoSessionList(GameManager.getSessionListWithRoomNumber(user.getRoomId()), outJsonObject);//通知所有用户打出的牌 是什么
		try {
			Thread.currentThread().sleep(TIME_WAIT_CHUPAI);//自动出牌等待1秒钟
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//判断其他人是否可以胡牌
		newanalysis(cardId, user, game);//继续分析是下一个人出牌还是能够碰牌和杠牌
	}


	/**分析用户是否可以暗杠
	 * @param user
	 * @param baiDa 百搭的那张牌
	 * @return
	 */
	public static List<Integer> isUserCanAnGang(User user){
		List<Integer> cards = user.getCards();
		int type = cards.get(0)/4;
		int total = 0;
		int compareCard = cards.get(0);
		List<Integer> anGangCards = new ArrayList<>();
		for(int i=1;i<cards.size();i++){
			Integer card = cards.get(i);
			int currentType = card/4;
			if(type == currentType){
				total++;
				anGangCards.add(card);
				if(total==3){
					anGangCards.add(compareCard);
					break;
				}
			}else{
				type = currentType;
				total = 0;//计数清零
				anGangCards = new ArrayList<>();
				compareCard = card;
			}
		}
		if(anGangCards.size()!=4){
			return new ArrayList<Integer>();
		}
		Collections.sort(anGangCards);
		return anGangCards;
	}
	

	/**分析用户是否可以公杠
	 * @param removeCard
	 * @param user
	 */
	public static List<Integer> analysisUserIsCanGongGang(Integer removeCard, User user) {
		List<Integer> pengCards = user.getUserPengCardsId();//碰出的牌
		int total = 0;
		List<Integer> gongGangCards =  new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			Integer card = pengCards.get(i);
			if(card/4==removeCard/4){
				gongGangCards.add(card);
				total ++;
			}
			if(total==3){
				break;
			}
		}
		return gongGangCards;
	}

	/**
	 * 分析用户是否可以公杠
	 */
	public static Integer analysisUserIsCanPengGang(User user){
		List<Integer> cards2 = user.getCards();//手牌
		List<Integer> list = new ArrayList<Integer>();
		for (Integer integer : cards2) {
			Integer card = integer/4;
			list.add(card);
		}
		List<PengCard> pengCards = user.getPengCards();
		for (PengCard pengCard : pengCards) {
			List<Integer> cards = pengCard.getCards();
			Integer card = cards.get(0)/4;
			if(list.contains(card)){
				int indexOf = list.indexOf(card);
				Integer integer = cards2.get(indexOf);
				return integer;
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param room
	 * @param user
	 * @param game
	 * @param isZiMo
	 * @param myHuType
	 * @param chuHu 
	 * @return
	 */
	public static JSONArray getUserJSONArray(OneRoom room, User user, Game game, boolean isZiMo, int myHuType, boolean chuHu) {
		FengTianHuPai fengTianHuPai = room.getFengTianHuPai();
		JSONArray jsonArray = new JSONArray();
		List<User> userList = room.getUserList();
		int sanQing = 0;
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()!=user.getId()){
				if(!u.isKaiMen()){
					sanQing++;
				}
			}
		}
		int siQing = 1;
		if(!user.isKaiMen()){
			siQing += sanQing;
		}
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("arrayLiangHei", TypeUtils.getFengList(u.getHeiFengGangCards()));
			jsonObject.put("arrayLiangBai", TypeUtils.getFengList(u.getBaiFengGangCards()));
			jsonObject.put("arrayGang", TypeUtils.getGangList(u.getGangCards()));
			jsonObject.put("arrayPeng", TypeUtils.getPengList(u.getPengCards()));
			jsonObject.put("arrayChi", TypeUtils.getChiList(u.getChiCards()));
			jsonObject.put("arrayHand", u.getCards());
			jsonObject.put("userId", u.getId());
			jsonObject.put("userDir", room.getUserDir(u.getDirection()));
			jsonObject.put("scoreByAdd", u.getXuChangScore().getScore());//本局分数
			jsonObject.put("scoreTotal", u.getXuChangScore().getCurrentScore());//总分数
			jsonObject.put("userName", u.getNickName());
			String stringForDescByHu = "";
			String stringForDescByGang ="";
			if(u.getId()==user.getId()){
				//胡派人的描述
				stringForDescByHu = fengTianHuPai.getStringForDescByHu(myHuType, user.isBanker(), game.isZhangMao(), isZiMo, user.isCanHai(),user.isCanKai());
				stringForDescByGang = fengTianHuPai.getStringForDescByGang(u.getGangAnum(), u.getGangMnum(), u.getGangPnum());
				if(user.getTingpaiState()==1){
					stringForDescByHu = stringForDescByHu +" 报听1番";
				}
				if(siQing==4){
					stringForDescByHu = stringForDescByHu +" 闭大四";
				}else if(sanQing==3){
					stringForDescByHu = stringForDescByHu +" 闷大山1番";
				}

				if(user.getIsSiGuiYi()>0){
					stringForDescByHu = stringForDescByHu +" 四归一*"+user.getIsSiGuiYi();
				}
				if(user.isShouBaYi()){
					stringForDescByHu = stringForDescByHu +" 手把一1番";
				}
				if(room.isGuoDan()){
					if(u.getGuoDanScore()!=0){
						stringForDescByHu = stringForDescByHu+" 过蛋分"+u.getGuoDanScore();
					}
				}
				
			}else{
				if(room.isGuoDan()){
					if(u.getGuoDanScore()!=0){
						stringForDescByHu = stringForDescByHu+" 过蛋分"+u.getGuoDanScore();
					}
				}
				if(chuHu){//如果是点炮胡
					if(game.getFangPaoUser()!=null&&u.getId()==game.getFangPaoUser().getId()){
						if(u.isBanker()){
							stringForDescByHu = stringForDescByHu + " 庄1番";
						}
						if(game.isLiuLei()){
							stringForDescByHu = stringForDescByHu + " 流泪1番";
						}
						if(room.isDianPao()){
							stringForDescByHu = stringForDescByHu + " 放炮X2";
						}else{
							stringForDescByHu = stringForDescByHu + " 放炮";
						}
					}
				}else{
					if(u.isBanker()){
						stringForDescByHu = stringForDescByHu + " 庄1番";
					}
				}
				//输分人是不是门清(开没开门)
				if(!u.isKaiMen()){//没开门
					stringForDescByHu = stringForDescByHu + " 门清";
				}
				
			}
			jsonObject.put("descGang", stringForDescByGang);//杠描述
			jsonObject.put("descHu", stringForDescByHu);//胡描述
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}
	/**
	 * 臭庄array
	 * @param room
	 * @param user
	 * @param game
	 * @param isZiMo
	 * @param myHuType
	 * @return
	 */	
	public static JSONArray getUserJSONArrayByChouZhuang(OneRoom room, User user, Game game, boolean isZiMo, int myHuType) {
		JSONArray jsonArray = new JSONArray();
		List<User> userList = room.getUserList();
			for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("arrayLiangHei", TypeUtils.getFengList(u.getHeiFengGangCards()));
			jsonObject.put("arrayLiangBai", TypeUtils.getFengList(u.getBaiFengGangCards()));
			jsonObject.put("arrayGang", TypeUtils.getGangList(u.getGangCards()));
			jsonObject.put("arrayPeng", TypeUtils.getPengList(u.getPengCards()));
			jsonObject.put("arrayChi", TypeUtils.getChiList(u.getChiCards()));
			jsonObject.put("arrayHand", u.getCards());
			jsonObject.put("userId", u.getId());
			jsonObject.put("userDir", room.getUserDir(u.getDirection()));
			jsonObject.put("scoreByAdd", u.getXuChangScore().getScore());//本局分数
			jsonObject.put("scoreTotal", u.getXuChangScore().getCurrentScore());//总分数
			jsonObject.put("userName", u.getNickName());
			String stringForDescByHu = "";
			if(room.isGuoDan()){
				if(u.getGuoDanScore()!=0){
					stringForDescByHu = stringForDescByHu+" 过蛋分"+u.getGuoDanScore();
				}
			}
			jsonObject.put("descGang", "");//杠描述
			jsonObject.put("descHu", stringForDescByHu);//胡描述
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}
	
	/**查看牌中是否含有红中
	 * @param cards 
	 * @return
	 */
	public static boolean isHaveHongZhong(List<Integer> cards){
		//倒序遍历，因为最大的是红中
		for(int i=cards.size()-1;i>=0;i--){
			Integer card = cards.get(i);
			if(CardsMap.getCardType(card).equals("红中")){
				return true;
			}
		}
		return false;
	}
	
	/**在用户赢牌以后,初始化用户的一些数据,设置新的庄家
	 * @param user,胜利的玩家，或者是最后抓牌的玩家
	 */
	public static void initializeUser(User user) {
		Game game = getGame(user);
		int alreadyTotalGame = game.getAlreadyTotalGame();
		try{
			
			List<User> userList = game.getUserList();
			for(int i=0;i<userList.size();i++){
				User u = userList.get(i);
				List<Integer> myPlays = new ArrayList<>();//出去的牌
				u.setCards(new ArrayList<Integer>());
				u.setMyPlays(myPlays);
				u.setPengCards(new ArrayList<PengCard>());
				List<GangCard> gangCards = new ArrayList<>();//杠的牌
				u.setGangCards(gangCards);
				u.setBaiFengGangCards(new ArrayList<XuanFengGangCard>());
				u.setHeiFengGangCards(new ArrayList<XuanFengGangCard>());
				u.setChiCards(new ArrayList<ChiCard>());
				u.setReady(false);
				u.setChuPaiCiShu(0);//出牌次数清零
				u.setCanKai(false);
				u.setCanHai(false);
				u.setCanBaiFeng(false);
				u.setCanHeiFeng(false);
				u.setCanChi(false);
				u.setCanGang(false);
				u.setCanPengGang(false);
				u.setCanAnGang(false);
				u.setCanPeng(false);
				u.setCanWin(false);
				u.setUserCanPlay(false);
				u.setTingpaiState(0);
				u.setCanTing(false);
				u.setGuoDanList(new ArrayList<Integer>());
				u.setGuoDanScore(0);
				u.setGangMnum(0);
				u.setGangAnum(0);
				u.setGangPnum(0);
				u.setKaiMen(false);
				u.setIsSiGuiYi(0);
				u.setShouBaYi(false);
			}
		}finally{
			game.setAlreadyTotalGame(alreadyTotalGame+1);//设置已经玩的游戏局数 
			game.setDirec("");
			game.setBeforeTingOrGangOrHuDirection("");
			game.clearLastMap();
			game.clearPriority();
			game.clearUserCan();
			game.setCanLiuLei(false);
			game.setLiuLei(false);
			game.setBeiQiangGangJsonObject(null);
			game.setZhangMao(false);
			game.setAfterGang(false);
			game.setRemainCards(new ArrayList<Integer>());
			game.setHuiFang(new StringBuffer());
			game.setBeiQiangGangUser(null);
			game.setBeiQiangGangCard(-1);
		}
	}

	/**设置新的庄家
	 * @param user
	 */
	public static void setNewBank(User user,Game game) {	
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		String nextDirection = null;
		for(User u:userList){
			if(u.isBanker()){
				if(user.getId()==u.getId()){
					u.setBanker(true);
					return;
				}else{
					u.setBanker(false);
					nextDirection = getNextDirection(u.getDirection(), room.getTotalUser());
				}
				break;
			}			
		}
		for (User user2 : userList) {
			if(user2.getDirection().equals(nextDirection)){
				user2.setBanker(true);
				break;
			}else{
				user2.setBanker(false);
			}
		}	
	}
	
	/**通知抓牌的方向
	 * @param removeCard
	 * @param nextUser 抓牌的玩家
	 * @param xuanFengGang2 
	 * {"m":"zhuapai","userDir":0,"cardId":0}
	 */
	static void notifyUserDrawDirection(Integer removeCard, User nextUser) {
		Game game = getGame(nextUser);
		List<User> userList = getUserListWithGame(game);
		//得到抓牌
		HuiFangUitl.getZhuaPai(game.getHuiFang(),nextUser, removeCard);
		for(User  user : userList){
			JSONObject outJsonObject = new JSONObject();
			//通知他抓到的牌
			outJsonObject.put("cardId", removeCard);
			outJsonObject.put("userDir", TypeUtils.getUserDir(nextUser.getDirection()));
			outJsonObject.put("m", "zhuapai");
			NotifyTool.notify(user.getIoSession(), outJsonObject);
		}
	}

	private static List<User> getUserListWithGame(Game game){
		OneRoom room = game.getRoom();
		List<User> userList = room.getUserList();
		return userList;
	}
	
	/**计算出可以碰牌或杠牌的人
	 * @param cardId 上一个人打出的牌
	 * @param seatMap
	 * @param nowUser
	 * @param baiDa
	 * @param totalChuPaiCiShu 上一个人的出牌次数
	 * @return
	 */
	public static User getPengOrGangCardUser(int cardId,Map<String, User> seatMap,User nowUser,int totalChuPaiCiShu){
		Iterator<String> it = seatMap.keySet().iterator();
		while(it.hasNext()){
			String next = it.next();
			User u = seatMap.get(next);
			if(u.getId()!=nowUser.getId()){
				List<Integer> cards = u.getCards();//获得该用户手牌
				int total = 0;
				List<Integer> pengOrGangList = new ArrayList<>();
				for(Integer cId:cards){
					if(cId/4==cardId/4){//一样牌的数量
						total ++;
						pengOrGangList.add(cId);
					}
				}
				if(total>=2){
					pengOrGangList.add(cardId);
					u.setPengOrGangList(pengOrGangList);
					if(total==2){
						u.setPengOrGang(1);//用户可以碰牌
					}else if(total == 3){
						u.setPengOrGang(2);//用户可以杠牌
					}
					return u;
				}
			}
		}
		return null;
	}
	/**
	 * 检查用户能不能碰牌
	 * @param cardId
	 * @param nowUser
	 * @return
	 */
	public static List<Integer> checkUserIsCanPeng(int cardId,User nowUser){
		List<Integer> cards = nowUser.getCards();
		int peng = 0;
		List<Integer> pengOrGangList = new ArrayList<>();
		for(Integer cId:cards){
			if(cId/4==cardId/4){//一样牌的数量
				peng ++;
				pengOrGangList.add(cId);
			}
		}
		return pengOrGangList;
	}
	
	/**得到下一个玩家的方向
	 * @param nowDirection 现在的方向
	 * @return
	 */
	public static String getNextDirection(String nowDirection,Integer totalUser){
		String direction = "";//方向
		if(totalUser==4){//四人麻将
			switch (nowDirection) {
			case "east":
				direction = "north";
				break;
			case "north":
				direction = "west";
				break;
			case "west":
				direction = "south";
				break;
			case "south":
				direction = "east";
				break;
			default:
				break;
			}
		}else if(totalUser==2){//二人麻将
			switch (nowDirection) {
			case "east":
				direction = "west";
				break;
			case "west":
				direction = "east";
				break;
			default:
				break;
			}
		}
		return direction;
	}

	/**
	 * 检测用户是否可以听牌
	 * @param bankUser
	 * @param game
	 * @return
	 */
	public static boolean checkUserIsCanTing(User bankUser, Game game) {
		FengTianHuPai fengTianHuPai = game.getRoom().getFengTianHuPai();
		int[] array = getArrayByList(bankUser.getCards());
		int[] intArrayForHand = TypeUtils.getArray(array);
		int[] intArrayForChi = TypeUtils.getChiArray(bankUser.getChiCards());
		int[] intArrayForPeng = TypeUtils.getPengArray(bankUser.getPengCards());
		int[] intArrayForGangM = TypeUtils.getDianGangArray(bankUser.getGangCards());
		int[] intArrayForGangA = TypeUtils.getAnGangArray(bankUser.getGangCards());
		int[] intArrayForZFB = TypeUtils.getBaiFengArray(bankUser.getBaiFengGangCards());
		int[] intArrayForDNXB = TypeUtils.getHeiFengArray(bankUser.getHeiFengGangCards());
		boolean checkTingTip = fengTianHuPai.checkTingTip(intArrayForHand, intArrayForChi, intArrayForPeng, intArrayForGangM, intArrayForGangA,intArrayForDNXB, intArrayForZFB);
		return checkTingTip;
	}
	/**
	 * 把list转换成数组
	 * @param cards
	 * @return
	 */
	private static int[] getArrayByList(List<Integer> cards) {
		int[] card = new int[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			card[i] = cards.get(i);
		}
		return card;
	}
	/**
	 * 通知用户听牌(抓牌后听牌)
	 * @param game
	 * @param bankUser
	 * @param removeCard 抓的牌
	 */
	public static void notifyAllUserZhuaTing(Game game, User bankUser, Integer removeCard) {
		OneRoom room = game.getRoom();
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("m", "zhuating");
		outJsonObject.put("cardId", removeCard);
		outJsonObject.put("userDir",room.getUserDir(bankUser.getDirection()));
		NotifyTool.notify(bankUser.getIoSession(), outJsonObject);
	}
}
