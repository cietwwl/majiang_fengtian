package com.zxz.domain;

public class XuChangScore {

	int currentScore = 0; //总的分数
	int score = 0;//单局

	private Integer huNum = 0;//胡拍次数
	
	private Integer dianpaoNum = 0;//放炮次数
	
	private Integer gangMnum = 0;//碰杠次数
	private Integer gangPnum = 0;//明杠次数
	private Integer gangAnum = 0;//暗杠次数

	
	
	public Integer getHuNum() {
		return huNum;
	}
	public void setHuNum(Integer huNum) {
		this.huNum = huNum;
	}
	public Integer getDianpaoNum() {
		return dianpaoNum;
	}
	public void setDianpaoNum(Integer dianpaoNum) {
		this.dianpaoNum = dianpaoNum;
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
	void initScore() {
		this.score=0;

	}
	public int getCurrentScore() {
		return currentScore;
	}


	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}


	@Override
	public String toString() {
		return "XuChangScore [currentScore=" + currentScore + ", score="
				+ score + "]";
	}
	
	
	

	
	
}
