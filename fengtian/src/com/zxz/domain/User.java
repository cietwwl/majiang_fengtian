package com.zxz.domain;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.StringUtils;
import com.zxz.controller.RoomManager;
import com.zxz.dao.Location;
import com.zxz.service.PlayGameService;
import com.zxz.service.UserService;
import com.zxz.utils.CardsMap;
import com.zxz.utils.Constant;
import com.zxz.utils.FengTest;
import com.zxz.utils.HuPai;
import com.zxz.utils.ScoreType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class User implements Constant{
	
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(User.class); 
	
	int id;
	/******************微信的所有东西**********************/
	String openid;//普通用户的标识，对当前开发者帐号唯一
	String nickName;//昵称 
	private String sex;//普通用户性别，1为男性，2为女性
	String province	;//普通用户个人资料填写的省份
	String city	;//普通用户个人资料填写的城市
	String country	;//国家，如中国为CN
	String unionid;//用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
	String headimgurl;//头像
	String refreshToken;//refresh_token拥有较长的有效期（30天），当refresh_token失效的后，需要用户重新授权。
	/*******************微信的所有东西*********************/
	Date createDate;//注册时间
	private int roomCard;//房卡的数量
	String userName;
	String password;
	private Integer sumScoreId = 0;
	private String roomId;//房间号 
	private boolean ready;//是否准备
	private ChannelHandlerContext ioSession;
	private List<Integer> cards;//自己手中的牌
	private String direction;//东 西 南 北
	private boolean banker;//庄
	private boolean isAuto = false;//用户是否托管
	private boolean isDropLine = false;//用户是否掉线
	private int pengOrGang=0;// 0 不可以牌和杠和吃 1 可以碰 2 可以杠  
	private int chi=0;// 0 不可以吃 1可以吃
	private List<Integer> myPlays = new ArrayList<>();//已经出的的牌
	
	private List<PengCard> pengCards = new ArrayList<>();//碰出的牌
	private List<GangCard> gangCards = new ArrayList<>();//杠出去的牌
	private List<XuanFengGangCard> baiFengGangCards = new ArrayList<>();//杠出去的牌
	private List<XuanFengGangCard> heiFengGangCards = new ArrayList<>();//杠出去的牌
	private List<ChiCard> chiCards = new ArrayList<>();//吃出去的牌
	
	private Integer myGrabCard;//我抓到的牌
	private boolean isCanPeng = false;//是不是可以碰
	private boolean isCanGang = false;//是不是可以明杠
	private boolean isCanAnGang = false;//是不是可以暗杠
	private boolean isCanPengGang = false;//是否可以碰杠
	private boolean isCanChi = false;//是不是可以吃
	private boolean isCanWin = false;//是否可以胡牌
	private boolean isCanBaiFeng = false;//是否可以亮白风
	private boolean isCanHeiFeng = false;//是否可以亮黑风
	private boolean isCanTing = false;//是否可以亮黑风
	
	/**
	 * 当前所在的局数
	 */
	private int currentGame;
	private Date lastChuPaiDate;//最后一次的出牌时间
	private List<Integer> pengOrGangList;//可以碰或者杠的牌
	private boolean isUserCanPlay = false;//false 说明:此变量是为了，解决当用户托管的时候，用户出牌和系统托管的时候，一起出牌，造成打两次牌的情况
	private int totalNotPlay = 0;//用户没有出牌的次数
	private boolean isWin = false;//用户是否赢牌
	private List<Integer> zhongMaCards;// 中码的牌
	private Integer huCardId = -1;//胡牌的时候抓的最后一张牌
	private int isAgreeDisbandType = 0;//是否同意解散房间 0 选择中 1同意 2不同意
	private int totalRequetDisbanRoom = 0;//设置解散房间的次数
	private int chuPaiCiShu = 0;//是不是第一次出牌
	private boolean isFangZhu = false;//是否是房主

    private int tingpaiState = 0; //0没有听牌 1听牌
    
    private XuChangScore xuChangScore = new XuChangScore();
	
    private boolean isCanHai= false;//是否可以海底捞月
    
    private boolean isCanKai = false;//是否可以杠上开花
    
	private Integer gangMnum = 0;//明杠次数
	private Integer gangPnum = 0;//碰杠次数
	private Integer gangAnum = 0;//暗杠次数
    
	private List<Integer> guoDanList = new ArrayList<>();
	private Integer guoDanScore = 0;//过蛋分计算
	
	private boolean isKaiMen = false;//是否开门  碰牌 明杠 碰杠算开门
	private int isSiGuiYi = 0;//四归一
	private boolean isShouBaYi = false;//手把一
	
	
	//GPS信息
	private Double jingdu = 0.0;//经度
	private Double weidu = 0.0;//纬度

	
	


	public int getIsSiGuiYi() {
		return isSiGuiYi;
	}

	public void setIsSiGuiYi(int isSiGuiYi) {
		this.isSiGuiYi = isSiGuiYi;
	}

	public boolean isShouBaYi() {
		return isShouBaYi;
	}

	public void setShouBaYi(boolean isShouBaYi) {
		this.isShouBaYi = isShouBaYi;
	}

	public Double getJingdu() {
		return jingdu;
	}

	public void setJingdu(Double jingdu) {
		this.jingdu = jingdu;
	}

	public Double getWeidu() {
		return weidu;
	}

	public void setWeidu(Double weidu) {
		this.weidu = weidu;
	}

	public boolean isKaiMen() {
		return isKaiMen;
	}

	public void setKaiMen(boolean isKaiMen) {
		this.isKaiMen = isKaiMen;
	}


	public Integer getGuoDanScore() {
		return guoDanScore;
	}

	public void setGuoDanScore(Integer guoDanScore) {
		this.guoDanScore = guoDanScore;
	}

	public List<Integer> getGuoDanList() {
		return guoDanList;
	}

	public void setGuoDanList(List<Integer> guoDanList) {
		this.guoDanList = guoDanList;
	}

	public boolean isCanTing() {
		return isCanTing;
	}

	public void setCanTing(boolean isCanTing) {
		this.isCanTing = isCanTing;
	}

	public Integer getGangMnum() {
		return gangMnum;
	}

	public void setGangMnum(Integer gangMnum) {
		this.gangMnum = gangMnum;
	}

	public Integer getGangPnum() {
		return gangPnum;
	}

	public void setGangPnum(Integer gangPnum) {
		this.gangPnum = gangPnum;
	}

	public Integer getGangAnum() {
		return gangAnum;
	}

	public void setGangAnum(Integer gangAnum) {
		this.gangAnum = gangAnum;
	}

	public boolean isCanPengGang() {
		return isCanPengGang;
	}

	public void setCanPengGang(boolean isCanPengGang) {
		this.isCanPengGang = isCanPengGang;
	}

	public boolean isCanAnGang() {
		return isCanAnGang;
	}

	public void setCanAnGang(boolean isCanAnGang) {
		this.isCanAnGang = isCanAnGang;
	}

	public boolean isCanBaiFeng() {
		return isCanBaiFeng;
	}

	public void setCanBaiFeng(boolean isCanBaiFeng) {
		this.isCanBaiFeng = isCanBaiFeng;
	}

	public Integer getSumScoreId() {
		return sumScoreId;
	}

	public void setSumScoreId(Integer sumScoreId) {
		this.sumScoreId = sumScoreId;
	}

	public boolean isCanHeiFeng() {
		return isCanHeiFeng;
	}

	public void setCanHeiFeng(boolean isCanHeiFeng) {
		this.isCanHeiFeng = isCanHeiFeng;
	}

	public boolean isCanKai() {
		return isCanKai;
	}

	public void setCanKai(boolean isCanKai) {
		this.isCanKai = isCanKai;
	}

	public boolean isCanHai() {
		return isCanHai;
	}

	public void setCanHai(boolean isCanHai) {
		this.isCanHai = isCanHai;
	}

	public List<XuanFengGangCard> getBaiFengGangCards() {
		return baiFengGangCards;
	}

	public void setBaiFengGangCards(List<XuanFengGangCard> baiFengGangCards) {
		this.baiFengGangCards = baiFengGangCards;
	}

	public List<XuanFengGangCard> getHeiFengGangCards() {
		return heiFengGangCards;
	}

	public void setHeiFengGangCards(List<XuanFengGangCard> heiFengGangCards) {
		this.heiFengGangCards = heiFengGangCards;
	}

	public int getChi() {
		return chi;
	}

	public void setChi(int chi) {
		this.chi = chi;
	}

	public boolean isCanChi() {
		return isCanChi;
	}

	public void setCanChi(boolean isCanChi) {
		this.isCanChi = isCanChi;
	}

	public XuChangScore getXuChangScore() {
		return xuChangScore;
	}

	public void setXuChangScore(XuChangScore xuChangScore) {
		this.xuChangScore = xuChangScore;
	}

	public int getTingpaiState() {
		return tingpaiState;
	}

	public void setTingpaiState(int tingpaiState) {
		this.tingpaiState = tingpaiState;
	}


	public boolean isDropLine() {
		return isDropLine;
	}

	public void setDropLine(boolean isDropLine) {
		this.isDropLine = isDropLine;
	}

	
	public boolean isFangZhu() {
		return isFangZhu;
	}


	public void setFangZhu(boolean isFangZhu) {
		this.isFangZhu = isFangZhu;
	}



	public int getChuPaiCiShu() {
		return chuPaiCiShu;
	}


	public void setChuPaiCiShu(int chuPaiCiShu) {
		this.chuPaiCiShu = chuPaiCiShu;
	}


	public int getTotalRequetDisbanRoom() {
		return totalRequetDisbanRoom;
	}


	public void setTotalRequetDisbanRoom(int totalRequetDisbanRoom) {
		this.totalRequetDisbanRoom = totalRequetDisbanRoom;
	}



	public int getIsAgreeDisbandType() {
		return isAgreeDisbandType;
	}

	public void setIsAgreeDisbandType(int isAgreeDisbandType) {
		this.isAgreeDisbandType = isAgreeDisbandType;
	}

	public Integer getHuCardId() {
		return huCardId;
	}


	public void setHuCardId(Integer huCardId) {
		this.huCardId = huCardId;
	}


	public List<Integer> getZhongMaCards() {
		return zhongMaCards;
	}


	public void setZhongMaCards(List<Integer> zhongMaCards) {
		this.zhongMaCards = zhongMaCards;
	}


	public boolean isWin() {
		return isWin;
	}


	public void setWin(boolean isWin) {
		this.isWin = isWin;
	}


	public int getTotalNotPlay() {
		return totalNotPlay;
	}


	public void setTotalNotPlay(int totalNotPlay) {
		this.totalNotPlay = totalNotPlay;
	}


	/**
	 * 清空所有的属性
	 */
	public void clearAll(){
		this.roomId = null;
		this.isAuto = false;
		this.currentGame = 0;
		this.setPengCards(new ArrayList<PengCard>());
		this.setChiCards(new ArrayList<ChiCard>());
		this.setBaiFengGangCards(new ArrayList<XuanFengGangCard>());
		this.setHeiFengGangCards(new ArrayList<XuanFengGangCard>());
		List<GangCard> gangCards = new ArrayList<>();//杠的牌
		this.setGangCards(gangCards);
		this.myGrabCard = null;//我找到的牌
		this.isCanPeng = false;//是不是可以碰
		this.isCanGang = false;//是不是可以杠
		this.isCanChi = false;//是不是可以吃
		this.isCanAnGang =false;
		this.isCanPengGang=false;
		this.isCanBaiFeng = false;
		this.isCanHeiFeng = false;
		this.isFangZhu = false;
		this.isUserCanPlay= false;
		this.isCanKai = false;
		this.isCanHai = false;
		this.isCanWin=false;
		this.isCanTing=false;
		this.banker = false;//是否是房主
		this.currentGame = 0;//当前的局数设置为0
		this.myPlays = new ArrayList<Integer>();//打出的牌清空
		this.totalNotPlay = 0;//用户没有出牌的次数清零
		this.ready = false;//取消准备
		this.isWin = false;//没有赢牌
		this.setZhongMaCards(new ArrayList<Integer>());//中码的牌
		this.huCardId = -1;
		this.isAgreeDisbandType = 0;
		this.chuPaiCiShu = 0;
		this.tingpaiState = 0;
		totalRequetDisbanRoom = 0;
		this.setGuoDanList(new ArrayList<Integer>());
		this.guoDanScore =0;
		this.gangAnum=0;
		this.gangMnum=0;
		this.gangPnum=0;

		logger.info("清空后当前用户已经打出的牌:" + this.getMyPlays());
	}
	

	public synchronized boolean isCanWin() {
		return isCanWin;
	}


	public synchronized void setCanWin(boolean isCanWin) {
		this.isCanWin = isCanWin;
	}


	public synchronized boolean isUserCanPlay() {
		return isUserCanPlay;
	}

	public synchronized void setUserCanPlay(boolean isUserCanPlay) {
		this.isUserCanPlay = isUserCanPlay;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSex() {
		if(sex==null){
			return "0";//男
		}
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public List<Integer> getPengOrGangList() {
		return pengOrGangList;
	}

	public void setPengOrGangList(List<Integer> pengOrGangList) {
		this.pengOrGangList = pengOrGangList;
	}

	public List<ChiCard> getChiCards() {
		return chiCards;
	}

	public void setChiCards(List<ChiCard> chiCards) {
		this.chiCards = chiCards;
	}

	public Date getLastChuPaiDate() {
		return lastChuPaiDate;
	}

	public void setLastChuPaiDate(Date lastChuPaiDate) {
		this.lastChuPaiDate = lastChuPaiDate;
	}
	

	public List<PengCard> getPengCards() {
		return pengCards;
	}

	
	/**得到用户所有碰的牌
	 * @return
	 */
	public List<Integer> getUserPengCardsId(){
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			PengCard pengCard = pengCards.get(i);
			List<Integer> cards2 = pengCard.getCards();
			for(int j=0;j<cards2.size();j++){
				list.add(cards2.get(j));
			}
		}
		return list;
	}
	
	/**得到用户所有碰的牌方向
	 * @return
	 */
	public List<String> getUserPengDirs(){
		List<String> dirs = new ArrayList<>();
		for(int i=0;i<pengCards.size();i++){
			PengCard pengCard = pengCards.get(i);
			dirs.add(pengCard.getChuDir());
		}
		return dirs;
	}
	
	
	public void setPengCards(List<PengCard> pengCards) {
		this.pengCards = pengCards;
	}

	public List<GangCard> getGangCards() {
		return gangCards;
	}

	public void setGangCards(List<GangCard> gangCards) {
		this.gangCards = gangCards;
	}

	public int getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}


	public int getRoomCard() {
		return roomCard;
	}

	public void setRoomCard(int roomCard) {
		this.roomCard = roomCard;
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

	public Integer getMyGrabCard() {
		return myGrabCard;
	}

	public void setMyGrabCard(Integer myGrabCard) {
		this.myGrabCard = myGrabCard;
	}

	public List<Integer> getMyPlays() {
		return myPlays;
	}

	public void setMyPlays(List<Integer> myPlays) {
		this.myPlays = myPlays;
	}



	public int getPengOrGang() {
		return pengOrGang;
	}

	public void setPengOrGang(int pengOrGang) {
		this.pengOrGang = pengOrGang;
	}

	public String getNickName() {
		if(!StringUtils.isNullOrEmpty(nickName)){
			if(nickName.length()>4){
				return nickName.substring(0, 4);
			}else{
				return nickName;
			}
		}
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public User() {
	}
	
	public boolean isBanker() {
		return banker;
	}
	public void setBanker(boolean banker) {
		this.banker = banker;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public List<Integer> getCards() {
		return cards;
	}
	public void setCards(List<Integer> cards) {
		this.cards = cards;
	}
	public ChannelHandlerContext getIoSession() {
		return ioSession;
	}
	public void setIoSession(ChannelHandlerContext ioSession) {
		this.ioSession = ioSession;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return getNickName();
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	
	
	/**得到用户的详细信息,包括,是否托管、已经打出的牌、手中的牌、碰的牌、杠的牌、自己方向 
	 * @param showMyCards 是否显示我手中的牌，true显示,false不显示
	 * @return
	 */
	public JSONObject getMyInfo(boolean showMyCards){
		JSONObject infoJSONObject = new JSONObject();
		infoJSONObject.put("isAuto", isAuto);//是否托管
		infoJSONObject.put("myPlays", myPlays);//已经打出的牌
		if(showMyCards){
			infoJSONObject.put("cards", cards);//手中的牌
		}
		infoJSONObject.put("pengCards", getUserPengCardsId());
		infoJSONObject.put("chuDirs", getUserPengDirs());
		JSONArray gangCardArray = new JSONArray();//杠牌的数组
		for(int i=0;i<gangCards.size();i++){
			GangCard gangCard = gangCards.get(i);
			JSONObject gangJsonObject = new JSONObject();
			int type = gangCard.getType();
			if (type==0) {
				gangJsonObject.put("gangType", "jieGang");
			}else if(type == 1){
				gangJsonObject.put("gangType", "anGang");
			}else if(type == 2){
				gangJsonObject.put("gangType", "gongGang");
			}
			List<Integer> gangCards = gangCard.getCards();
			gangJsonObject.put("gangCards", gangCards);
			gangCardArray.put(gangJsonObject);
		}
		infoJSONObject.put("userid",id);
		infoJSONObject.put("headimgurl", headimgurl);
		infoJSONObject.put("userName", getUserName());
		infoJSONObject.put("gangCardArray", gangCardArray);
		infoJSONObject.put("direction", direction);
		infoJSONObject.put("ready", ready);
		String userIp = getIp().replaceAll("/", "").split(":")[0];
		infoJSONObject.put("ip", userIp);//ip地址
		infoJSONObject.put("isD", isDropLine());//ip地址
//		int playerScoreByAdd = UserService.getUserCurrentGameScore(this);//当前的分数
		int playerScoreByAdd = this.getXuChangScore().getCurrentScore();
		infoJSONObject.put("playerScoreByAdd", playerScoreByAdd);
		infoJSONObject.put("sex", Integer.parseInt(getSex()));
		infoJSONObject.put("isFz", isFangZhu());//是否房主
		
		infoJSONObject.put("tingpaiState", getTingpaiState());//得到听牌的状态
		return infoJSONObject;
	}
	
	
	
	


	/**出牌
	 * @param cardId 出牌的牌号
	 */
	public int chuPai(Integer cardId) {
		int indexOf = cards.indexOf(cardId);
		Integer remove = cards.remove(indexOf);
		if(remove.equals(cardId)){
			chuPaiCiShu = chuPaiCiShu + 1;
			//如果出的牌正好是我抓到的牌，则把我抓到的牌置空
			if(myGrabCard!=null&&cardId==myGrabCard){
				myGrabCard = null;
			}
			return cardId;
		}
		return -1;
	}
	
	
	/**智能出牌
	 * @return
	 */
	public  int autoChuPai(){
		chuPaiCiShu ++;
		Integer myGrabCard = this.getMyGrabCard();//抓到的牌
		boolean containMyGrabCard = cards.contains(myGrabCard);
		if(containMyGrabCard){
			cards.remove(myGrabCard);
			return myGrabCard;
		}
		return cards.remove(0);
	}
	



	/**计算出哪一张牌最合适
	 * @param array
	 * @return
	 */
	public static Integer getWitchCardToPlay(int[] array) {
		return array[array.length-1];
	}
	
	
	/**判断用户是否听牌
	 * @param array
	 * @return
	 */
	public boolean isTingPai(int array[]){
		
		
		return false;
	}
	
	
	/**得到该张牌的权重
	 * @param intervalPre
	 * @param intervalNext
	 * @return
	 */
	private static int getCardWeight(int intervalPre, int intervalNext) {
		int weight = 0;
		//如果是孤立的一张牌
		if(intervalPre==-1&&intervalNext==-1){
			weight = 10000;
		}else{
			if(intervalPre==1){//可能是一句话
				weight = weight-5000;
			}else if(intervalPre==0){//可能组成坎
				weight = weight - 10000;
			}else if(intervalPre == 2){
				weight = weight - 3000;
			}else{
				weight = weight+intervalPre;
			}
			if(intervalNext == 1){//可能是一句话
				weight = weight - 5000;
			}else if(intervalNext==0){//可能组成坎
				weight = weight - 10000;
			}else if(intervalNext==2){
				weight = weight - 3000;
			}else{
				weight = weight+intervalNext;
			}
		}
		return weight;
	}
	
	
	/**抓牌
	 * @param cardId
	 */
	public boolean zhuaPai(int cardId){
		cards.add(cardId);
		Collections.sort(cards);//抓完牌排序
		setLastChuPaiDate(new Date());//设置本次的出牌时间
		this.myGrabCard = cardId;//当前抓到的牌
		HuPai huPai = new HuPai();		
		boolean hu = huPai.isHu(cards, pengCards,gangCards,chiCards,baiFengGangCards,heiFengGangCards,RoomManager.getRoomWithRoomId(this.getRoomId()));
		return hu;
	}
	
	
	
	/**碰牌
	 * @param list 需要碰掉的牌
	 */
	public void userPengCards(List<Integer> list){
		for(int i=0;i<list.size();i++){
			Integer removeCard = list.get(i);
			cards.remove(removeCard);
		}
		setCanPeng(false);
	}
	/**
	 * 吃牌
	 * @param list 需要吃掉的牌
	 * @param cardId 
	 * @param game 
	 */
	public List<Integer> userChiCards(List<Integer> list, Integer cardId, Game game){
		
		game.getFangChiUser().getMyPlays().remove(cardId);
		List<Integer> newList = new ArrayList<>();
		List<Integer> arrayList = new ArrayList<>();
		for (int i = 0; i < cards.size(); i++) {
			arrayList.add(cards.get(i)/4);
		}
		for(int i=0;i<list.size();i++){
			int indexOf = arrayList.indexOf(list.get(i));
			newList.add(cards.get(indexOf));
		}
		cards.removeAll(newList);
		newList.add(cardId);
		Collections.sort(newList);
		setCanChi(false);
		return newList;
	}
	
	/**添加用户碰出的牌
	 * @param cards
	 */
	public void addUserPengCards(List<Integer> cards,String chuDir){
		PengCard pengCard = new PengCard();
		pengCard.addPengCard(cards, chuDir);
		this.pengCards.add(pengCard);
	}
	
	/**添加用户吃出的牌
	 * @param cards
	 */
	public void addUserChiCards(List<Integer> cards,String chuDir){
		ChiCard chiCard = new ChiCard();
		chiCard.addChiCard(cards, chuDir);
		this.chiCards.add(chiCard);
	}
	
	/**
	 * 添加旋风杠 
	 */
	public void addXuanFengGang(List<Integer> cards){
		XuanFengGangCard xuanFengGangCard = new XuanFengGangCard(cards);
		if(cards.size()==3){
			baiFengGangCards.add(xuanFengGangCard);
		}else if(cards.size()==4){
			heiFengGangCards.add(xuanFengGangCard);
		}
	}
	/**杠牌
	 * @param list 需要杠掉的牌
	 */
	public void userGangCards(List<Integer> list){
		for(int i=0;i<list.size();i++){
			Integer removeCard = list.get(i);
			cards.remove(removeCard);
		}
		setCanAnGang(false);
	}
	

	
	

	

	

	
	

	
	
	


	

	/**
	 * 暗杠减分
	 * @param otherXuanPao
	 * @param gangPao 是否杠跑
	 * @return
	 */
	public int reduceScoreForAnGang(int otherXuanPao){
		int result = 0;
		result =  Math.abs(ScoreType.REDUCE_SCORE_FOR_ANGANG);
	
/*		if(gangPao){
			if(this.xuanPao!=-1){
				result =result+ xuanPao;
				this.xuChangScore.setGangFen(this.xuChangScore.getGangFen()-xuanPao);
			}
			if(otherXuanPao!=-1){
				result =result+ otherXuanPao;
				this.xuChangScore.setGangFen(this.xuChangScore.getGangFen()-otherXuanPao);
			}
		}*/
		return result;
	}

	
	

	
	
	

	
	/**记录用户杠出的牌
	 * @param type 0 放杠  1、暗杠  2、公杠 /明杠
	 * @param cards 杠出的牌
	 */
	public void recordUserGangCards(int type,List<Integer> cards){
		GangCard gangCard = new GangCard(type, cards);
		gangCards.add(gangCard);
	}
	
	
	
	/**从自己的牌中移除公杠的那张牌
	 * @param card
	 */
	public void removeCardFromGongGang(Integer cardId){
		cards.remove(cardId);
	}

	
	/**将牌转成数组
	 * @return
	 */
	public int[] getCardsArray(){
		int arr[]= new int[cards.size()];
		for(int i=0;i<cards.size();i++){
			arr[i] = cards.get(i);
		}
		return arr;
	}
	

	/**得到用户的IP
	 * @return
	 */
	public String getIp(){
		SocketAddress remoteAddress = this.ioSession.channel().localAddress();
		return remoteAddress.toString().replaceAll("/", "");
	}

	public List<Integer> realUserChiCards(List<Integer> chiList) {
		for (int i = 0; i < chiList.size(); i++) {
			cards.remove(chiList.get(i));
		}
		return chiList;
	}

	public void clearAllCan() {
		this.isCanAnGang= false;
		this.isCanBaiFeng=false;
		this.isCanHeiFeng=false;
		this.isCanChi=false;
		this.isCanGang=false;
		this.isCanPeng=false;
		this.isCanPengGang=false;
		this.isCanWin=false;
		this.isCanTing =false;
	}
	
}
