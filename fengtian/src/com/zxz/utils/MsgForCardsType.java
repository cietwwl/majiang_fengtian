package com.zxz.utils;


public class MsgForCardsType {
	public static final int cardsType_11 = 1;
	public static final int cardsType_111 = 2;
	public static final int cardsType_123 = 3;
	
	public int cardsType;
	public int cardContent1;
	public int cardContent2;
	public int cardContent3;
	
	public MsgForCardsType(int cardsType, int content1, int content2){
		this.cardsType = cardsType;
		this.cardContent1 = content1;
		this.cardContent2 = content2;
		this.cardContent3 = -1;
	}
	
	public MsgForCardsType(int cardsType, int content1, int content2, int content3){
		this.cardsType = cardsType;
		this.cardContent1 = content1;
		this.cardContent2 = content2;
		this.cardContent3 = content3;
	}
}
