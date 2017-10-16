package com.zxz.domain;

import java.util.List;

public class XuanFengGangCard {
	
	private List<Integer> cards;//旋风杠出去的牌

	
	public XuanFengGangCard(List<Integer> cards) {
		super();
		this.cards = cards;
	}

	public List<Integer> getCards() {
		return cards;
	}

	public void setCards(List<Integer> cards) {
		this.cards = cards;
	}

	@Override
	public String toString() {
		return "XuanFengGangCard [cards=" + cards + "]";
	}
	
}
