package com.zxz.domain;

import java.util.Date;

public class SumScore extends Score {

	int id;
	String roomNumber;//鎴块棿鍙�
	int userid;//鐢ㄦ埛id
	Date createDate;//鏃堕棿
	int fangZhu ;// 0 不是房主 1房主
	int total;//局数
	int fengDingNum;
	String nickName;
	


	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getFengDingNum() {
		return fengDingNum;
	}
	public void setFengDingNum(int fengDingNum) {
		this.fengDingNum = fengDingNum;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getFangZhu() {
		return fangZhu;
	}
	public void setFangZhu(int fangZhu) {
		this.fangZhu = fangZhu;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
