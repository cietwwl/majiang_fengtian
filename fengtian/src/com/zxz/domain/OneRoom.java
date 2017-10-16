package com.zxz.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.zxz.utils.Constant;
import com.zxz.utils.FengTianHuPai;
import com.zxz.utils.NotifyTool;

import io.netty.channel.ChannelHandlerContext;


public class OneRoom  implements Constant{
	private int id;	//房间id
	private int roomNumber;//房间号
	private List<User> userList = new LinkedList<>();//房间里面的人
	private int total;//游戏总局数(圈数)
	private User createUser;//创建人
	private int createUserId;//创建人的id 
	private boolean isUse = false;//房间是否已经占用
	private Set<String> directionSet = new HashSet<>();//房间的方向
	private boolean isPay = false;//是否扣除房卡
	private Date createDate;//房间创建时间
	private Date endDate;//房间结束时间
	private int invertal;//查询创建房间时候的间隔值  房间号
	private boolean isDisband = false;//房间是否解散
	private int alreadyTotalGame = 0;//已经玩的局数
	private int auto = 1;// 是否托管
	private Integer fengDingNum = 0;//封顶分数
	private boolean chiKaiMen = true;//是否只碰开门
	private boolean dianPao =  true;//是否点炮
	private boolean yiTouTing =  false;//是否一头听
	private boolean shouLouZi =  false;//是否手搂子
	private boolean qingYiSe =  true;//是否带清一色
	private boolean guoDan =  true;//是否带过蛋
	private boolean hunPiao =  false;//是否混飘
	private boolean chunPiao =  true;//是否混飘
	private Integer totalUser;//房间的玩法(二人/四人)
	private boolean hunQiDui =  false;//是否混七对
	private boolean chunQiDui =  true;//是否纯七对
	private Integer currentTotal;//当前圈数

	private FengTianHuPai fengTianHuPai = new FengTianHuPai();
	

	public boolean isChunPiao() {
		return chunPiao;
	}

	public void setChunPiao(boolean chunPiao) {
		this.chunPiao = chunPiao;
	}

	public boolean isChunQiDui() {
		return chunQiDui;
	}

	public void setChunQiDui(boolean chunQiDui) {
		this.chunQiDui = chunQiDui;
	}

	public FengTianHuPai getFengTianHuPai() {
		return fengTianHuPai;
	}

	public void setFengTianHuPai(FengTianHuPai fengTianHuPai) {
		this.fengTianHuPai = fengTianHuPai;
	}

	public User getRoomUserByGamingUser(Game game,User gamingUser){
		User roomUser = new User();
		List<User> userList = game.getRoom().getUserList();
		for (User u : userList) {
			if(u.getDirection().equals(gamingUser.getDirection())){
				roomUser = u;
				break;
			}
		}
		return roomUser;
	}
	
	public Integer getCurrentTotal() {
		return currentTotal;
	}
	public void setCurrentTotal(Integer currentTotal) {
		this.currentTotal = currentTotal;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public boolean isHunPiao() {
		return hunPiao;
	}
	public void setHunPiao(boolean hunPiao) {
		this.hunPiao = hunPiao;
	}
	public boolean isHunQiDui() {
		return hunQiDui;
	}
	public void setHunQiDui(boolean hunQiDui) {
		this.hunQiDui = hunQiDui;
	}
	public int getFengDingNum() {
		return fengDingNum;
	}
	public void setFengDingNum(Integer fengDingNum) {
		this.fengDingNum = fengDingNum;
	}

	public boolean isChiKaiMen() {
		return chiKaiMen;
	}
	public void setChiKaiMen(boolean chiKaiMen) {
		this.chiKaiMen = chiKaiMen;
	}
	public boolean isDianPao() {
		return dianPao;
	}
	public void setDianPao(boolean dianPao) {
		this.dianPao = dianPao;
	}
	public boolean isYiTouTing() {
		return yiTouTing;
	}
	public void setYiTouTing(boolean yiTouTing) {
		this.yiTouTing = yiTouTing;
	}
	public boolean isShouLouZi() {
		return shouLouZi;
	}
	public void setShouLouZi(boolean shouLouZi) {
		this.shouLouZi = shouLouZi;
	}
	public boolean isQingYiSe() {
		return qingYiSe;
	}
	public void setQingYiSe(boolean qingYiSe) {
		this.qingYiSe = qingYiSe;
	}
	public boolean isGuoDan() {
		return guoDan;
	}
	public void setGuoDan(boolean guoDan) {
		this.guoDan = guoDan;
	}


	public int getTotalUser() {
		return totalUser;
	}
	public void setTotalUser(Integer totalUser) {
		this.totalUser = totalUser;
	}

	public int getInvertal() {
		return invertal;
	}
	public void setInvertal(int invertal) {
		this.invertal = invertal;
	}
	public int getAlreadyTotalGame() {
		return alreadyTotalGame;
	}
	public void setAlreadyTotalGame(int alreadyTotalGame) {
		this.alreadyTotalGame = alreadyTotalGame;
	}
	public int getAuto() {
		return auto;
	}
	public void setAuto(int auto) {
		this.auto = auto;
	}
	
	public boolean isDisband() {
		return isDisband;
	}
	public void setDisband(boolean isDisband) {
		this.isDisband = isDisband;
	}
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public boolean isPay() {
		return isPay;
	}
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}
	public Set<String> getDirectionSet() {
		return directionSet;
	}
	public void setDirectionSet(Set<String> directionSet) {
		this.directionSet = directionSet;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public boolean isUse() {
		return isUse;
	}
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	} 
	
	public void addUser(User user){
		userList.add(user);
	}
	
	public List<ChannelHandlerContext> getUserIoSessionList(){
		List<ChannelHandlerContext> list =  new ArrayList<ChannelHandlerContext>();
		for(User user : userList){
			list.add(user.getIoSession());
		}
		return list;
	}
	
	/**用户离开房间
	 * @param user
	 * @return
	 */
	public boolean userLeaveRoom(User user){
		boolean result = false;
		for(User u:userList){
			if(u.getId()==user.getId()){
				result = userList.remove(u);
				break;
			}
		}
		return result;
	}
	
	

	
	/**通知房间里用户
	 * @param jsonObject
	 */
	public void noticeUsersWithJsonObject(JSONObject jsonObject){
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			ChannelHandlerContext channelHandlerContext = user.getIoSession();
			NotifyTool.notify(channelHandlerContext, jsonObject);
		}
	}
	
	/**
	 * 得到用户的位置0123 对应  东北西南
	 * @param dir
	 */
	public int getUserDir(String dir){
		if(dir.equals("east")){
			return 0;
		}else if(dir.equals("north")){
			return 1;
		}else if(dir.equals("west")){
			return 2;
		}else if(dir.equals("south")){
			return 3;
		}
		return 99;
	}
}
