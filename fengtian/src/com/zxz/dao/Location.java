package com.zxz.dao;

/**用户经纬度查询 
 * @author Administrator
 */
public class Location {

	Double jingDu = 0.0;
	Double weiDu = 0.0;
	boolean isKaiQi = false;
	public Double getJingDu() {
		return jingDu;
	}
	public void setJingDu(Double jingDu) {
		this.jingDu = jingDu;
	}
	public Double getWeiDu() {
		return weiDu;
	}
	public void setWeiDu(Double weiDu) {
		this.weiDu = weiDu;
	}
	public boolean isKaiQi() {
		return isKaiQi;
	}
	public void setKaiQi(boolean isKaiQi) {
		this.isKaiQi = isKaiQi;
	}
}
