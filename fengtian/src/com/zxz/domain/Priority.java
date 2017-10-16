package com.zxz.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * 决策优先级
 * @author Administrator
 *
 */
public class Priority {
	public Integer total = 0;//总执行数量
	public Integer currentNumber = 0;//已经接收到的数量
	private String chiDir;//吃牌方向
	private Integer chiType = 0;//吃的状态  0未操作  1吃  2取消
	private Integer pengType = 0;//碰的状态  0未操作  1吃  2取消
	private Integer gangType = 0;//杠的状态  0未操作  1吃  2取消
	private Integer huType = 0;//胡的状态  0未操作  1吃  2取消
	private String pengDir;//碰牌方向
	private String gangDir;//杠牌方向
	private List<String> huDir = new ArrayList<String>();//可以胡牌的方向集合 按照优先级存储最靠前的优先胡牌
	
	private Map<String, Integer> seatMap = new HashMap<String, Integer>();//可以进行操作的人数   key是方向 value是优先级

	private Map<Integer, String> typeMap = new HashMap<>();//所有的操作 key是类型 value是方向    1:吃  2:碰 3:杠 4:上家胡 5:对家胡 6:下家胡
	
	private Map<Integer,JSONObject> userInfo = new HashMap<>();//存放所有用户传来的信息  value存放的是json数据
	
	public Map<Integer, JSONObject> getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(Map<Integer, JSONObject> userInfo) {
		this.userInfo = userInfo;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getCurrentNumber() {
		return currentNumber;
	}
	public void setCurrentNumber(Integer currentNumber) {
		this.currentNumber = currentNumber;
	}
	public Map<Integer, String> getTypeMap() {
		return typeMap;
	}
	public void setTypeMap(Map<Integer, String> typeMap) {
		this.typeMap = typeMap;
	}
	public Map<String, Integer> getSeatMap() {
		return seatMap;
	}
	public void setSeatMap(Map<String, Integer> seatMap) {
		this.seatMap = seatMap;
	}
	public String getChiDir() {
		return chiDir;
	}
	public void setChiDir(String chiDir) {
		this.chiDir = chiDir;
	}
	public String getPengDir() {
		return pengDir;
	}
	public void setPengDir(String pengDir) {
		this.pengDir = pengDir;
	}
	public String getGangDir() {
		return gangDir;
	}
	public void setGangDir(String gangDir) {
		this.gangDir = gangDir;
	}
	public List<String> getHuDir() {
		return huDir;
	}
	public void setHuDir(List<String> huDir) {
		this.huDir = huDir;
	}
	public Integer getChiType() {
		return chiType;
	}
	public void setChiType(Integer chiType) {
		this.chiType = chiType;
	}
	public Integer getPengType() {
		return pengType;
	}
	public void setPengType(Integer pengType) {
		this.pengType = pengType;
	}
	public Integer getGangType() {
		return gangType;
	}
	public void setGangType(Integer gangType) {
		this.gangType = gangType;
	}
	public Integer getHuType() {
		return huType;
	}
	public void setHuType(Integer huType) {
		this.huType = huType;
	}
	
}
