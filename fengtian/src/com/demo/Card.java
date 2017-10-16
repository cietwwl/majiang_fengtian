/**  
 * @Title: Card.java
 * @Package com.milan.majiang
 * @Description: TODO
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0 
 */
package com.demo;

import java.util.Arrays;


/**
 * ClassName: Card 
 * @Description: TODO
 * @author Clark
 * @date 2016年9月19日 下午10:42:49
 * @Version 1.0
 *
 */
public class Card {
	
	private static final String COLOR_WAN = "wan";
	private static final String COLOR_TIAO = "tiao";
	private static final String COLOR_TONG = "tong";
	
	private static final String DONG = "dong";
	private static final String NAN = "nan";
	private static final String XI = "xi";
	private static final String BEI = "bei";
	
	private static final String ZHONG = "zhong";
	private static final String FA = "fa";
	private static final String BAI = "bai";
	
	private static String[] shouOfEast  = new String[13];
	private static String[] shouOfSouth  = new String[13];
	private static String[] shouOfWest  = new String[13];
	private static String[] shouOfNorth  = new String[13];
	
	
	
	//做一副牌
	public static String[] printMahjong(){
		String[] mahjong = new String[136];
		for (int i = 0; i < mahjong.length; i++) {
			if (i<36) {
				mahjong[i] = COLOR_WAN;
			}else if (i>=36 && i<72) {
				mahjong[i] = COLOR_TIAO;
			}else if (i>=72 && i<108){
				mahjong[i] = COLOR_TONG;
			}else if (i>=108 && i<112){
				mahjong[i] = DONG;
			}else if (i>=112 && i<116){
				mahjong[i] = NAN;
			}else if (i>=116 && i<120){
				mahjong[i] = XI;
			}else if (i>=120 && i<124){
				mahjong[i] = BEI;
			}else if (i>=124 && i<128){
				mahjong[i] = ZHONG;
			}else if (i>=128 && i<132){
				mahjong[i] = FA;
			}else if (i>=132 && i<136){
				mahjong[i] = BAI;
			}	
		}
		for (int i = 0; i < 108; i++) {
			mahjong[i] = mahjong[i] + (i%9+1);
		}
		return mahjong;
	}
	
	//洗牌
	public static String[] xipai(String[] mahjong){
		for( int i=0; i<136; i++)
	     {
	          int j=((int)(Math.random()*10000))%(136-i)+i;
	          String temp;
	          temp=mahjong[i];
	          mahjong[i]=mahjong[j];
	          mahjong[j]=temp;
	     }
		return mahjong;
	}
	
	//发牌
	public static void fapai(String[] mahjong){
		for (int i = 0; i < 13; i++) {
			shouOfEast[i] =mahjong[i*4];
			shouOfSouth[i] = mahjong[i*4+1];
			shouOfWest[i] = mahjong[i*4+2];
			shouOfNorth[i] = mahjong[i*4+3];
		}
	}
	
	//排序
	public static String[] paixu(String[] mahjong){
		
		String[] newMahjong = new String[13];
		Arrays.sort(mahjong);
		int point = 0;
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("wan")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("tiao")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("tong")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("dong")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("nan")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("xi")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("bei")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("zhong")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("fa")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		for (int i = 0; i < mahjong.length; i++) {
			if (mahjong[i].contains("bai")) {
				newMahjong[point] = mahjong[i];
				point++;
			}
		}
		return newMahjong;
	}
	
	
	public static void main(String[] args) {
		String[] majiang = xipai(printMahjong());
		for (String string : majiang) {
			System.out.println(string);
		}
		fapai(majiang);
		System.out.println("===========================");
		System.out.println(Arrays.toString(shouOfEast));
		System.out.println(Arrays.toString(shouOfSouth));
		System.out.println(Arrays.toString(shouOfWest));
		System.out.println(Arrays.toString(shouOfNorth));
		System.out.println("===========================");
		
		String[] newMahjong = {"tiao6", "tiao7", "tiao1", "nan", "wan5", "wan2", "tiao9", "bai", "tong6", "tiao4", "tong9", "wan2", "nan"};
		System.out.println(Arrays.toString(paixu(newMahjong)));
		
		System.out.println(Integer.MAX_VALUE);
		
	}
	
}
