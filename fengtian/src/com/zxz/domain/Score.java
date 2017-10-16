package com.zxz.domain;

/**
 *鎬荤粨绠�
 */
public class Score {
	
	int huPaiTotal;//鑳＄墝鐨勬鏁�
	int jieGangTotal;//鎺ユ潬娆℃暟
	int anGangTotal;//鏆楁潬娆℃暟
	int zhongMaTotal;//涓爜涓暟
	int finalScore;//鎬绘垚缁�
	int fangGangTotal ;//鏀炬潬鐨勬鏁�
	int mingGangtotal;//鏄庢潬鐨勬鏁� 涔熺О鍏潬
	int zimoTotal;//鑷懜鐨勬鏁�
	int fangChongTotal;//鏀惧啿娆℃暟
	
	public int getFangChongTotal() {
		return fangChongTotal;
	}
	public void setFangChongTotal(int fangChongTotal) {
		this.fangChongTotal = fangChongTotal;
	}
	public int getZimoTotal() {
		return zimoTotal;
	}
	public void setZimoTotal(int zimoTotal) {
		this.zimoTotal = zimoTotal;
	}
	public int getMingGangtotal() {
		return mingGangtotal;
	}
	public void setMingGangtotal(int mingGangtotal) {
		this.mingGangtotal = mingGangtotal;
	}
	public int getFangGangTotal() {
		return fangGangTotal;
	}
	public void setFangGangTotal(int fangGangTotal) {
		this.fangGangTotal = fangGangTotal;
	}
	public int getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(int finalScore) {
		this.finalScore = finalScore;
	}
	public int getHuPaiTotal() {
		return huPaiTotal;
	}
	public void setHuPaiTotal(int huPaiTotal) {
		this.huPaiTotal = huPaiTotal;
	}
	public int getJieGangTotal() {
		return jieGangTotal;
	}
	public void setJieGangTotal(int jieGangTotal) {
		this.jieGangTotal = jieGangTotal;
	}
	public int getAnGangTotal() {
		return anGangTotal;
	}
	public void setAnGangTotal(int anGangTotal) {
		this.anGangTotal = anGangTotal;
	}
	public int getZhongMaTotal() {
		return zhongMaTotal;
	}
	public void setZhongMaTotal(int zhongMaTotal) {
		this.zhongMaTotal = zhongMaTotal;
	}
	@Override
	public String toString() {
		return "Score [huPaiTotal=" + huPaiTotal + ", jieGangTotal=" + jieGangTotal + ", anGangTotal=" + anGangTotal
				+ ", zhongMaTotal=" + zhongMaTotal + ", finalScore=" + finalScore + ", fangGangTotal=" + fangGangTotal
				+ ", mingGangtotal=" + mingGangtotal + ", jieGangTotal=" + jieGangTotal + "]";
	}
	
}
