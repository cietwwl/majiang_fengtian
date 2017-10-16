package com.zxz.domain;

import java.util.ArrayList;
import java.util.List;

public class ChiCard {
	private String chuDir;//被吃牌人方向
	private List<Integer> cards= new ArrayList<Integer>();//吃的牌
	private Integer chicard;//吃的牌
	public void addChiCard(List<Integer> chiCards,String chuDir){
		for(int i=0;i<chiCards.size();i++){
			cards.add(chiCards.get(i));
		}
		this.chuDir = chuDir;
	}

	public String getChuDir() {
		return chuDir;
	}

	public void setChuDir(String chuDir) {
		this.chuDir = chuDir;
	}

	public Integer getChicard() {
		return chicard;
	}

	public void setChicard(Integer chicard) {
		this.chicard = chicard;
	}

	public List<Integer> getCards() {
		return cards;
	}
	
}
