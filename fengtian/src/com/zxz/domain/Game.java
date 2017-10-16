package com.zxz.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.zxz.service.HuiFangUitl;
import com.zxz.service.PlayGameService;
import com.zxz.utils.Constant;
import com.zxz.utils.MathUtil;
import com.zxz.utils.TypeUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class Game implements Constant{
	
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlayGameService.class); 
	
	OneRoom room;
	private List<Integer> cardList;
	int totalGame;//游戏的局数
	private Map<String, User> seatMap;//座次
	String direc;//出牌方向
	List<Integer> remainCards;//剩余的牌
	private String beforeTingOrGangOrHuDirection;//玩家可以碰或杠或胡的时候出牌的方向
	private int alreadyTotalGame = 0;//已经万的游戏局数,默认为0
	boolean isGameOver = false;//游戏是否结束
	/**
	 * 游戏的状态,0游戏进行中,1游戏等待开始
	 */
	private int status;
	List<Integer> anGangCards;//暗杠的牌
	Integer gongGangCardId;//(公杠)明杠的牌
	List<List<Integer>> xuanFengGangCards;//旋风杠的牌
	private Map<Integer, String> gameStatusMap = new HashMap<>();//当局是否结束
	private User fangGangUser;//放杠的用户
	private User fangPengUser;//放碰的用户
	private User fangPaoUser;//放炮的用户
	private User fangChiUser;//放吃的用户
	private Integer autoPengCardId;//自动的碰牌的牌号
	private Integer autoGangCardId;//自动的杠牌的牌号
	private Integer autoChiCardId;//自动的吃牌的牌号
	private Integer autoHuCardId;//自动的胡牌的牌号
	private User canPengUser;//可以碰牌的用户
	private User canGangUser;//可以杠牌的用户
	private User canChiUser;//可以吃牌的用户
	private User canXuanFengGangUser;//可以旋风杠的用户
	private User canGongGangUser;//公杠的用户
	private User canAnGangUser;//暗杠的用户 
	private User canHuUser;//可以胡牌的玩家
	private User canTingUser;//可以胡牌的玩家
	int gameStatus;//碰牌，听牌，杠牌,出牌,吃牌
	int yaoPaiStatus = 2;//2没人要  3有人要
	private boolean isDisband = false; //游戏是否解散 
	private StringBuffer huiFang = new StringBuffer();//游戏回放
//	int chiState;  //0不可以吃  1可以吃
	private boolean  isCanChi  = false;
	private boolean  isCanPeng = false;
	private boolean  isCanGang = false;
	private boolean  isCanWin  = false;
	private boolean isCanPengGang = false;
	private boolean isCanAnGang = false;
	
	/**
	 * 胡牌的时候可以杠牌的类型 0 是公杠 1是暗杠
	 */
	private int isCanGangType;
	private int fanpai;//百搭的牌号
	private boolean isOver = false;//游戏是否结束
	
	private Priority priority = new Priority();
	
	private boolean isHaveLiang = false; //游戏有没有人亮牌
	private Map<String, Integer> lastMap = new HashMap<>();//存最后出的方向 跟出的牌  两个属性
	
	private Date startDate = new Date();//游戏开始时间
	private Date endDate = new Date();//游戏结束时间
	
	private boolean isXuanFengBai = false;//游戏有没有旋风中发白
	private boolean isXuanFengHei = false;//游戏有没有旋风东南西北
	private boolean isCanLiuLei = false;//游戏有没有流泪
	private boolean haveLiangBai = false;//该局游戏有没有人亮白风牌
	private boolean haveLiangHei = false;//该局游戏有没有人亮黑风牌
	
	private boolean isZhangMao = false;//长毛
	private boolean isLiuLei = false;//流泪

	private boolean isAfterGang =false;//是否是杠后
	
	
	private JSONObject beiQiangGangJsonObject = null;
	
	private Integer beiQiangGangCard = -1;

	private User beiQiangGangUser=null;
	
	public boolean isAfterGang() {
		return isAfterGang;
	}


	public void setAfterGang(boolean isAfterGang) {
		this.isAfterGang = isAfterGang;
	}


	public boolean isLiuLei() {
		return isLiuLei;
	}


	public void setLiuLei(boolean isLiuLei) {
		this.isLiuLei = isLiuLei;
	}

	

	public User getBeiQiangGangUser() {
		return beiQiangGangUser;
	}


	public void setBeiQiangGangUser(User beiQiangGangUser) {
		this.beiQiangGangUser = beiQiangGangUser;
	}


	public Integer getBeiQiangGangCard() {
		return beiQiangGangCard;
	}


	public void setBeiQiangGangCard(Integer beiQiangGangCard) {
		this.beiQiangGangCard = beiQiangGangCard;
	}


	public JSONObject getBeiQiangGangJsonObject() {
		return beiQiangGangJsonObject;
	}


	public void setBeiQiangGangJsonObject(JSONObject beiQiangGangJsonObject) {
		this.beiQiangGangJsonObject = beiQiangGangJsonObject;
	}


	public boolean isZhangMao(){
		return isZhangMao;
	}


	public void setZhangMao(boolean isZhangMao) {
		this.isZhangMao = isZhangMao;
	}


	public boolean isHaveLiangBai() {
		return haveLiangBai;
	}


	public void setHaveLiangBai(boolean haveLiangBai) {
		this.haveLiangBai = haveLiangBai;
	}


	public boolean isHaveLiangHei() {
		return haveLiangHei;
	}


	public void setHaveLiangHei(boolean haveLiangHei) {
		this.haveLiangHei = haveLiangHei;
	}


	public boolean isCanLiuLei() {
		return isCanLiuLei;
	}


	public void setCanLiuLei(boolean isCanLiuLei) {
		this.isCanLiuLei = isCanLiuLei;
	}


	public boolean isCanAnGang() {
		return isCanAnGang;
	}


	public void setCanAnGang(boolean isCanAnGang) {
		this.isCanAnGang = isCanAnGang;
	}


	public User getCanTingUser() {
		return canTingUser;
	}


	public void setCanTingUser(User canTingUser) {
		this.canTingUser = canTingUser;
	}


	public boolean isCanPengGang() {
		return isCanPengGang;
	}


	public void setCanPengGang(boolean isCanPengGang) {
		this.isCanPengGang = isCanPengGang;
	}





	public boolean isCanWin() {
		return isCanWin;
	}


	public void setCanWin(boolean isCanWin) {
		this.isCanWin = isCanWin;
	}


	public boolean isCanChi() {
		return isCanChi;
	}


	public void setCanChi(boolean isCanChi) {
		this.isCanChi = isCanChi;
	}


	public boolean isCanPeng() {
		return isCanPeng;
	}


	public void setCanPeng(boolean isCanPeng) {
		this.isCanPeng = isCanPeng;
	}


	public boolean isCanGang() {
		return isCanGang;
	}


	public void setCanGang(boolean isCanGang) {
		this.isCanGang = isCanGang;
	}


	public boolean isXuanFengBai() {
		return isXuanFengBai;
	}


	public void setXuanFengBai(boolean isXuanFengBai) {
		this.isXuanFengBai = isXuanFengBai;
	}


	public boolean isXuanFengHei() {
		return isXuanFengHei;
	}


	public void setXuanFengHei(boolean isXuanFengHei) {
		this.isXuanFengHei = isXuanFengHei;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public boolean isHaveLiang() {
		return isHaveLiang;
	}


	public void setHaveLiang(boolean isHaveLiang) {
		this.isHaveLiang = isHaveLiang;
	}

	/**
	 * 等待胡牌的时候的状态,7 胡牌 可胡可碰的时候选择了碰 8 可胡可杠的时候选择了杠
	 */
	private int waitHuStatus = 0;
	
	/**
	 * 胡牌的时候选择碰牌或杠牌的人的方向
	 */
	private String waitDirection;
	
	
	/**
	 * 牌剩余的位置
	 */
	private int endRemainCards = 0;
	
	public Game(OneRoom room) {
		super();
		this.room = room;
		this.totalGame = room.getTotal();
	}
	
	/**
	 * 设置最后的一次的出牌方向跟出的牌
	 * @param user
	 * @param cardId
	 */
	public void setLastMap(User user,Integer cardId) {
		Map<String, Integer> lastMap = new HashMap<String, Integer>();
		lastMap.put("dir", TypeUtils.getUserDir(user.getDirection()));
		lastMap.put("cardId", cardId);
		this.lastMap = lastMap;
	}
	public void clearLastMap(){
		this.lastMap = new HashMap<String, Integer>();
	}
	public Map<String, Integer> getLastMap() {
		return lastMap;
	}


	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public void clearPriority(){
		Priority priority = new Priority();
		this.priority = priority;
	}
	
	public int getYaoPaiStatus() {
		return yaoPaiStatus;
	}


	public void setYaoPaiStatus(int yaoPaiStatus) {
		this.yaoPaiStatus = yaoPaiStatus;
	}


	public User getCanXuanFengGangUser() {
		return canXuanFengGangUser;
	}

	public void setCanXuanFengGangUser(User canXuanFengGangUser) {
		this.canXuanFengGangUser = canXuanFengGangUser;
	}

	public List<List<Integer>> getXuanFengGangCards() {
		return xuanFengGangCards;
	}

	public void setXuanFengGangCards(List<List<Integer>> xuanFengGang) {
		this.xuanFengGangCards = xuanFengGang;
	}

	public User getFangChiUser() {
		return fangChiUser;
	}

	public void setFangChiUser(User fangChiUser) {
		this.fangChiUser = fangChiUser;
	}

	public User getCanChiUser() {
		return canChiUser;
	}

	public void setCanChiUser(User canChiUser) {
		this.canChiUser = canChiUser;
	}

/*	public int getChiState() {
		return chiState;
	}

	public void setChiState(int chiState) {
		this.chiState = chiState;
	}*/

	public int getEndRemainCards() {
		return endRemainCards;
	}

	public void setEndRemainCards(int endRemainCards) {
		this.endRemainCards = endRemainCards;
	}

	public String getWaitDirection() {
		return waitDirection;
	}

	public void setWaitDirection(String waitDirection) {
		this.waitDirection = waitDirection;
	}

	public int getWaitHuStatus() {
		return waitHuStatus;
	}

	public void setWaitHuStatus(int waitHuStatus) {
		this.waitHuStatus = waitHuStatus;
	}

	public int getFanpai() {
		return fanpai;
	}

	public void setFanpai(int fanpai) {
		this.fanpai = fanpai;
	}

	public Integer getAutoHuCardId() {
		return autoHuCardId;
	}

	public void setAutoHuCardId(Integer autoHuCardId) {
		this.autoHuCardId = autoHuCardId;
	}
	
	public Integer getAutoChiCardId() {
		return autoChiCardId;
	}

	public void setAutoChiCardId(Integer autoChiCardId) {
		this.autoChiCardId = autoChiCardId;
	}

	public User getFangPaoUser() {
		return fangPaoUser;
	}


	public void setFangPaoUser(User fangPaoUser) {
		this.fangPaoUser = fangPaoUser;
	}

	public boolean isOver() {
		return isOver;
	}


	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}


/*	public int getBaida() {
		return baida;
	}


	public void setBaida(int baida) {
		this.baida = baida;
	}*/

	public User getFangPengUser() {
		return fangPengUser;
	}

	public void setFangPengUser(User fangPengUser) {
		this.fangPengUser = fangPengUser;
	}

	public int getIsCanGangType() {
		return isCanGangType;
	}

	public void setIsCanGangType(int isCanGangType) {
		this.isCanGangType = isCanGangType;
	}

	public StringBuffer getHuiFang() {
		return huiFang;
	}

	public void setHuiFang(StringBuffer huiFang) {
		this.huiFang = huiFang;
	}


	/**
	 * @return true 解散  false 没有解散
	 */
	public boolean isDisband() {
		return isDisband;
	}


	public void setDisband(boolean isDisband) {
		this.isDisband = isDisband;
	}


	public User getCanHuUser() {
		return canHuUser;
	}


	public void setCanHuUser(User canHuUser) {
		this.canHuUser = canHuUser;
	}

	public synchronized int getGameStatus() {
		return gameStatus;
	}

	/**设置出牌的状态 ，出牌、听牌、杠牌、暗杠
	 * @param gameStatus
	 */
	public synchronized void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}

	
	public User getCanGongGangUser() {
		return canGongGangUser;
	}

	public void setCanGongGangUser(User canGongGangUser) {
		this.canGongGangUser = canGongGangUser;
	}

	public User getCanAnGangUser() {
		return canAnGangUser;
	}

	public void setCanAnGangUser(User canAnGangUser) {
		this.canAnGangUser = canAnGangUser;
	}

	public Integer getAutoGangCardId() {
		return autoGangCardId;
	}

	public void setAutoGangCardId(Integer autoGangCardId) {
		this.autoGangCardId = autoGangCardId;
	}

	public User getCanGangUser() {
		return canGangUser;
	}

	public void setCanGangUser(User canGangUser) {
		this.canGangUser = canGangUser;
	}

	public User getCanPengUser() {
		return canPengUser;
	}

	public void setCanPengUser(User canPengUser) {
		this.canPengUser = canPengUser;
	}

	public Integer getAutoPengCardId() {
		return autoPengCardId;
	}

	public void setAutoPengCardId(Integer autoPengCardId) {
		this.autoPengCardId = autoPengCardId;
	}

	
	/**
	 * 得到游戏中的玩家
	 */
	public User getUserInRoomList(int userId){
		List<User> userList = this.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(user.getId()==userId){
				return user;
			}
		}
		logger.error("加减分的时候没有找到用户:");
		return null;
	}
	
	
	public User getFangGangUser() {
		return fangGangUser;
	}

	public void setFangGangUser(User fangGangUser) {
		this.fangGangUser = fangGangUser;
	}

	public Map<Integer, String> getGameStatusMap() {
		return gameStatusMap;
	}

	public void setGameStatusMap(Map<Integer, String> gameStatusMap) {
		this.gameStatusMap = gameStatusMap;
	}

	

	public Integer getGongGangCardId() {
		return gongGangCardId;
	}

	public void setGongGangCardId(Integer gongGangCardId) {
		this.gongGangCardId = gongGangCardId;
	}

	public List<Integer> getAnGangCards() {
		return anGangCards;
	}

	public void setAnGangCards(List<Integer> anGangCards) {
		this.anGangCards = anGangCards;
	}

	/**游戏的状态，等待游戏开始或游戏进行中
	 * @return
	 */
	public synchronized int getStatus() {
		return status;
	}

	/**游戏等待或者是游戏开始
	 * @param status
	 */
	public synchronized void setStatus(int status) {
		this.status = status;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

	public int getAlreadyTotalGame() {
		return alreadyTotalGame;
	}

	public void setAlreadyTotalGame(int alreadyTotalGame) {
		this.alreadyTotalGame = alreadyTotalGame;
		//设置房间已经玩的局数
		this.room.setAlreadyTotalGame(alreadyTotalGame);
	}

	public String getBeforeTingOrGangOrHuDirection() {
		return beforeTingOrGangOrHuDirection;
	}

	public void setBeforeTingOrGangOrHuDirection(String beforeTingOrGangOrHuDirection) {
		this.beforeTingOrGangOrHuDirection = beforeTingOrGangOrHuDirection;
	}

	public void playGame() {
		room.setUse(true);//房间已经使用 
		cardList = MathUtil.creatRandom(0, 135);//生成牌
		remainCards = dealCard(room,cardList);//发牌
		seatMap = generateSeat(room);//得到座次Map
		direc = getDirPaly(room);//出牌的方向
		//修改用户的当前的局数
		moifyUserCurrentGame(this);
//		logger.info("发出牌后，玩家剩余的牌："+remainCards + " 总张数:"+remainCards.size());
	}
	
	
	
	/**修改用户当前的局数,并且设置当前的分数
	 * @param game
	 */
	public void moifyUserCurrentGame(Game game){
		List<User> userList = game.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.setCurrentGame(user.getCurrentGame()+1);
			user.getXuChangScore().initScore();
		}
	}


	public List<Integer> getRemainCards() {
		return remainCards;
	}


	public void setRemainCards(List<Integer> remainCards) {
		this.remainCards = remainCards;
	}


	public OneRoom getRoom() {
		return room;
	}

	public void setRoom(OneRoom room) {
		this.room = room;
	}

	public List<Integer> getCardList() {
		return cardList;
	}


	public void setCardList(List<Integer> cardList) {
		this.cardList = cardList;
	}


	public int getTotalGame() {
		return totalGame;
	}


	public void setTotalGame(int totalGame) {
		this.totalGame = totalGame;
	}


	public Map<String, User> getSeatMap() {
		return seatMap;
	}


	public void setSeatMap(Map<String, User> seatMap) {
		this.seatMap = seatMap;
	}


	public synchronized String getDirec() {
		return direc;
	}


	public synchronized void setDirec(String direc) {
		this.direc = direc;
	}


	/**得到出牌的方向
	 * @param room
	 * @return
	 */
	private String getDirPaly(OneRoom room){
		List<User> userList = room.getUserList();
		for(User user:userList){
			if(user.isBanker()){
				return user.getDirection();
			}
		}
		return "east";
	}
		
	/**生成座次
	 * @param game 
	 * @param room
	 * @return 
	 */
	public Map<String, User> generateSeat(OneRoom room) {
		List<User> users = room.getUserList();
		Map<String, User> map = new HashMap<String, User>();
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			String direction = user.getDirection();
			map.put(direction, user);
		}
		this.seatMap = map;
		return map;
	}	
	
	/**得到所有的用户
	 * @return
	 */
	public List<User> getUserList(){
		List<User> userList = room.getUserList();
		return userList;
	}

	/**得到房间里所有的IoSession
	 * @return
	 */
	public List<ChannelHandlerContext> getIoSessionList(){
		List<User> userList = getUserList();
		List<ChannelHandlerContext> sessionList =  new ArrayList<>();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			sessionList.add(user.getIoSession());
		}
		return sessionList;
	}
	
	/**
	 * 得到基础的回放信息
	 */
	public void getBaseHuiFang(List<User> userList){
		getHuiFang().append("f:rInfo:"+room.getRoomNumber()+","+0+","+(alreadyTotalGame+1)+","+totalGame+","+0+"|f:info:");
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(i==userList.size()-1){
				HuiFangUitl.getBase(getHuiFang(), user,true);
			}else{
				HuiFangUitl.getBase(getHuiFang(), user,false);
			}
		}
	}
	
	/**发牌
	 * @param room
	 * @param cardList
	 * @return remainList 剩余的牌
	 */
	private List<Integer> dealCard(OneRoom room, List<Integer> cardList) {
		List<User> userList = room.getUserList();
		int begin = 0 ;
		getBaseHuiFang(userList);
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			List<Integer> cards = new LinkedList<Integer>();
			boolean banker = user.isBanker();//如果是庄家
			if(banker){
				int end = begin +14;
				for(int j=begin;j<end;j++){
					cards.add(cardList.get(j));
					begin ++;
				}
			}else{
				int end = begin +13;
				for(int j=begin;j<end;j++){
					cards.add(cardList.get(j));
					begin ++;
				}
			}
			Collections.sort(cards);//给牌排序
			user.setCards(cards);
			//记录发牌
			HuiFangUitl.getFaPai(getHuiFang(), user, cards);
		}
		List<Integer> remainList = new LinkedList<Integer>();
		for (int i=room.getTotalUser()*13+1;i<cardList.size();i++){
			remainList.add(cardList.get(i));
		}
		return remainList;
	}
/*	*//**发牌
	 * @param room
	 * @param cardList
	 * @return remainList 剩余的牌
	 *//*
	private List<Integer> dealCard(OneRoom room, List<Integer> cardList) {
		List<User> userList = room.getUserList();
		int begin = 0 ;
		getBaseHuiFang(userList);
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			List<Integer> cards = new LinkedList<Integer>();
			boolean banker = user.isBanker();//如果是庄家
			if(banker){
				int end = begin +14;
				int[] myCards = new int[]{16,17,18,29,36,40,45,48,49,52,56,80,86,90};
				for (int j : myCards) {
					cards.add(j);
					begin ++;
				}
			}else if(TypeUtils.getUserDir(user.getDirection())==1){
				int end = begin +13;
				int[] myCards = new int[]{4,39,10,36,42,45,47,52,72,94,103,107,113,};				
				for (int j : myCards) {
					cards.add(j);
					begin ++;
				}
			}else if(TypeUtils.getUserDir(user.getDirection())==2){
				int end = begin +13;
				int[] myCards = new int[]{3,1,47,18,20,34,38,61,33,74,108,117,118};
				for (int j : myCards) {
					cards.add(j);
					begin ++;
				}
			}else{
				int end = begin +13;
				for(int j=begin;j<end;j++){
					cards.add(cardList.get(j));
					begin ++;
				}
			}
			Collections.sort(cards);//给牌排序
			user.setCards(cards);
			//记录发牌
			HuiFangUitl.getFaPai(getHuiFang(), user, cards);
		}
		List<Integer> remainList = new LinkedList<Integer>();
		for (int i=room.getTotalUser()*13+1;i<cardList.size()-4;i++){
			
			remainList.add(cardList.get(i));
		}
		remainList.add(39);
		remainList.add(39);
		remainList.add(39);
		remainList.add(39);
		return remainList;
	}*/


	/**得到游戏中的玩家
	 * @param direction 玩家的方向
	 * @return
	 */
	public  User getGamingUser(String direction){
		User user = seatMap.get(direction);
		return user;
	}
	
	@Override
	public String toString() {
		return "Game [autoPengCardId=" + autoPengCardId + ", autoGangCardId=" + autoGangCardId + ", canPengUser="
				+ canPengUser + ", canGangUser=" + canGangUser + ", canGongGangUser=" + canGongGangUser
				+ ", canAnGangUser=" + canAnGangUser + "]";
	}


	public void clearUserCan() {
		this.setCanAnGang(false);
		this.setCanWin(false);
		this.setCanChi(false);
		this.setCanPeng(false);
		this.setCanPengGang(false);
		this.setCanGang(false);
		this.setCanTingUser(null);
		this.setCanChiUser(null);
		this.setCanGangUser(null);
		this.setCanAnGangUser(null);
		this.setCanHuUser(null);
		this.setCanGongGangUser(null);
		this.setCanPengUser(null);
		this.setCanXuanFengGangUser(null);
		this.setXuanFengBai(false);
		this.setXuanFengHei(false);
		this.setXuanFengGangCards(null);
		this.setFangPaoUser(null);
		this.setCanLiuLei(false);
		this.setHaveLiangBai(false);
		this.setHaveLiangHei(false);
//		this.setZhangMao(false);
		this.setBeiQiangGangJsonObject(null);
	}






	
}
