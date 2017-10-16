package com.zxz.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zxz.domain.ChiCard;
import com.zxz.domain.GangCard;
import com.zxz.domain.PengCard;
import com.zxz.domain.XuanFengGangCard;

/**
 * 把吃碰杠牌转换成数组
 * @author Administrator
 *
 */
public class TypeUtils {
	
//	public int intArrayForHand[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int intArrayForChi[]  = {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int intArrayForPeng[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int intArrayForGang[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int intArrayForZFB[]  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int intArrayForDNXB[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	
	public static void main(String[] args) {
		String stringDir2 = getStringDir(0);
		System.out.println(stringDir2);
/*		//吃测试
 		List<ChiCard> arrayList = new ArrayList<>();
		ChiCard chiCard = new ChiCard();
		List<Integer> chiCards = new ArrayList<>();
		chiCards.add(65);
		chiCards.add(66);
		chiCards.add(67);
		chiCard.addChiCard(chiCards , "east");
		arrayList.add(chiCard);
		ChiCard chiCard1 = new ChiCard();
		List<Integer> chiCards1 = new ArrayList<>();
		chiCards1.add(0);
		chiCards1.add(4);
		chiCards1.add(8);
		chiCard1.addChiCard(chiCards1 , "east");
		arrayList.add(chiCard1);
		int[] array = getChiArray(arrayList);
		for (int i : array) {
			System.out.print(i+" ");
		}*/
		
/*		//碰测试
		List<PengCard> arrayList = new ArrayList<>();
		PengCard chiCard = new PengCard();
		List<Integer> chiCards = new ArrayList<>();
		chiCards.add(65);
		chiCards.add(66);
		chiCards.add(67);
		chiCard.addPengCard(chiCards , "east");
		arrayList.add(chiCard);
		PengCard chiCard1 = new PengCard();
		List<Integer> chiCards1 = new ArrayList<>();
		chiCards1.add(0);
		chiCards1.add(1);
		chiCards1.add(2);
		chiCard1.addPengCard(chiCards1 , "east");
		arrayList.add(chiCard1);
		int[] array = getPengArray(arrayList);
		for (int i : array) {
			System.out.print(i+" ");
		}*/
/*		//杠测试
		List<GangCard> arrayList = new ArrayList<>();
		
		List<Integer> chiCards = new ArrayList<>();
		chiCards.add(64);
		chiCards.add(65);
		chiCards.add(66);
		chiCards.add(67);
		GangCard chiCard = new GangCard(2, chiCards);
		arrayList.add(chiCard);
		
		List<Integer> chiCards1 = new ArrayList<>();
		chiCards1.add(0);
		chiCards1.add(1);
		chiCards1.add(2);
		chiCards1.add(3);
		GangCard chiCard1 = new GangCard(2, chiCards1);
		arrayList.add(chiCard1);
		int[] array = getGangArray(arrayList);
		for (int i : array) {
			System.out.print(i+" ");
		}*/
		
/*		//白风测试
		List<XuanFengGangCard> arrayList = new ArrayList<>();
		
		List<Integer> chiCards = new ArrayList<>();
		chiCards.add(109);
		chiCards.add(113);
		chiCards.add(117);
		XuanFengGangCard xuanFengGangCard = new XuanFengGangCard(chiCards);
		arrayList.add(xuanFengGangCard);
		
		List<Integer> chiCards1 = new ArrayList<>();
		chiCards1.add(108);
		chiCards1.add(112);
		chiCards1.add(116);
		XuanFengGangCard xuanFengGangCard1 = new XuanFengGangCard(chiCards1);
		arrayList.add(xuanFengGangCard1);
		int[] array = getBaiFengArray(arrayList);
		for (int i : array) {
			System.out.print(i+" ");
		}*/
		//黑风
		List<XuanFengGangCard> arrayList = new ArrayList<>();
/*		List<Integer> chiCards = new ArrayList<>();
		chiCards.add(120);
		chiCards.add(124);
		chiCards.add(128);
		chiCards.add(132);
		XuanFengGangCard xuanFengGangCard = new XuanFengGangCard(chiCards);
		arrayList.add(xuanFengGangCard);
		
		List<Integer> chiCards1 = new ArrayList<>();
		chiCards1.add(120);
		chiCards1.add(124);
		chiCards1.add(128);
		chiCards1.add(132);
		XuanFengGangCard xuanFengGangCard1 = new XuanFengGangCard(chiCards1);
		arrayList.add(xuanFengGangCard1);*/
		int[] array = getHeiFengArray(arrayList);
		for (int i : array) {
			System.out.print(i+" ");
		}
		
	}
	/**
	 * 得到吃的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getChiArray(List<ChiCard> chiCards){
		int[] chi = new int[chiCards.size()*3];
		int num = 0;
		for (int i = 0; i < chiCards.size(); i++) {
			ChiCard chiCard = chiCards.get(i);
			List<Integer> cards = chiCard.getCards();
			for (int j = 0; j < cards.size(); j++) {
				chi[num] = cards.get(j);
				num++;
			}
		}
		int[] intArrayForChi = getArray(chi);
		return intArrayForChi;
	}
	/**
	 * 得到碰的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getPengArray(List<PengCard> pengCards){
		int[] chi = new int[pengCards.size()*3];
		int num = 0;
		for (int i = 0; i < pengCards.size(); i++) {
			PengCard pengCard = pengCards.get(i);
			List<Integer> cards = pengCard.getCards();
			for (int j = 0; j < cards.size(); j++) {
				chi[num] = cards.get(j);
				num++;
			}
		}
		int[] intArray = getArray(chi);
		return intArray;
	}
	
	/**
	 * 得到点杠的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getDianGangArray(List<GangCard> gangCards){
		int anGang = 0;
		for (int i = 0; i < gangCards.size(); i++) {
			GangCard gangCard = gangCards.get(i);
			if(gangCard.getType()==1){
				anGang++;
			}
		}
		int[] gang = new int[gangCards.size()*4-anGang*4];
		int num = 0;
		for (int i = 0; i < gangCards.size(); i++) {
			GangCard gangCard = gangCards.get(i);
			if(gangCard.getType()!=1){
				List<Integer> cards = gangCard.getCards();
				for (int j = 0; j < cards.size(); j++) {
					gang[num] = cards.get(j);
					num++;
				}
			}
		}
		int[] intArray = getArray(gang);
		return intArray;
	}
	/**
	 * 得到暗杠的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getAnGangArray(List<GangCard> gangCards){
		int anGang = 0;
		for (int i = 0; i < gangCards.size(); i++) {
			GangCard gangCard = gangCards.get(i);
			if(gangCard.getType()==1){
				anGang++;
			}
		}
		int[] gang = new int[anGang*4];
		int num = 0;
		for (int i = 0; i < gangCards.size(); i++) {
			GangCard gangCard = gangCards.get(i);
			if(gangCard.getType()==1){
				List<Integer> cards = gangCard.getCards();
				for (int j = 0; j < cards.size(); j++) {
					gang[num] = cards.get(j);
					num++;
				}
			}
		}
		int[] intArray = getArray(gang);
		return intArray;
	}
	
	/**
	 * 得到白风的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getBaiFengArray(List<XuanFengGangCard> baiFeng){
		int[] chi = new int[baiFeng.size()*3];
		int num = 0;
		for (int i = 0; i < baiFeng.size(); i++) {
			XuanFengGangCard xuanFengGangCard = baiFeng.get(i);
			List<Integer> cards = xuanFengGangCard.getCards();
			for (int j = 0; j < cards.size(); j++) {
				chi[num] = cards.get(j);
				num++;
			}
		}
		int[] intArray = getArray(chi);
		return intArray;
	}
	/**
	 * 得到黑风的牌的数组
	 * @param chiCards
	 * @return
	 */
	public static  int[] getHeiFengArray(List<XuanFengGangCard> heiFeng){
		int[] chi = new int[heiFeng.size()*4];
		int num = 0;
		for (int i = 0; i < heiFeng.size(); i++) {
			XuanFengGangCard xuanFengGangCard = heiFeng.get(i);
			List<Integer> cards = xuanFengGangCard.getCards();
			for (int j = 0; j < cards.size(); j++) {
				chi[num] = cards.get(j);
				num++;
			}
		}
		int[] intArray = getArray(chi);
		return intArray;
	}
	
	
	/**
	 * 把拥有的数组类型 的 牌转成指定样式
	 * @param num
	 * @return
	 */
	public static int[] getArray(int[] num) {
		Map<Integer,Integer> hashMap = new HashMap<>();
		for (int i : num) {
			int temp = i/4;
			Integer value = hashMap.get(temp);
			if(value!=null){
				hashMap.put(temp, ++value);
			}else{
				hashMap.put(temp, 1);
			}
		}
		Iterator<Integer> iterator = hashMap.keySet().iterator();
		int intArray[]  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			Integer val = hashMap.get(key);
			intArray[key] = val;
		}
		return intArray;
	}
	public static String[] stringDir = new String[]{"east","north","west","south"};
	public static int[] intDir = new int[]{0,1,2,3};
	public static String getStringDir(int dir){
		return stringDir[dir];
	}
	
	/**
	 * 得到用户的位置0123 对应  东北西南
	 * @param dir
	 */
	public static int getUserDir(String dir){
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
	/**
	 * 得到碰牌集合
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getPengList(List<PengCard> pengCards){
		List<Integer> arrayList = new ArrayList<>();
		for (PengCard pengCard : pengCards) {
			List<Integer> cards = pengCard.getCards();
			for (Integer integer : cards) {
				arrayList.add(integer);
			}
		}
		return arrayList;
	}
	
	/**
	 * 得到杠牌集合
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getGangList(List<GangCard> gangCards){
		List<Integer> arrayList = new ArrayList<>();
		for (GangCard gangCard : gangCards) {
			List<Integer> cards = gangCard.getCards();
			for (Integer integer : cards) {
				arrayList.add(integer);
			}
		}
		return arrayList;
	}
	
	/**
	 * 得到明杠牌集合
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getGangMList(List<GangCard> gangCards){
		List<Integer> arrayList = new ArrayList<>();
		for (GangCard gangCard : gangCards) {
			if(gangCard.getType()!=1){
				List<Integer> cards = gangCard.getCards();
				for (Integer integer : cards) {
					arrayList.add(integer);
				}
			}
			
		}
		return arrayList;
	}
	/**
	 * 得到暗杠牌集合
	 * @param pengCards
	 * @return
	 */
	public static List<Integer> getGangAList(List<GangCard> gangCards){
		List<Integer> arrayList = new ArrayList<>();
		for (GangCard gangCard : gangCards) {
			if(gangCard.getType()==1){
				List<Integer> cards = gangCard.getCards();
				for (Integer integer : cards) {
					arrayList.add(integer);
				}
			}
		}
		return arrayList;
	}
	
	
	
	/**
	 * 得到吃的集合
	 * @param gangCards
	 * @return
	 */
	public static List<Integer> getChiList(List<ChiCard> gangCards){
		List<Integer> arrayList = new ArrayList<>();
		for (ChiCard chiCard : gangCards) {
			List<Integer> cards = chiCard.getCards();
			for (Integer integer : cards) {
				arrayList.add(integer);
			}
		}
		return arrayList;
	}
	
	/**
	 * 得到白风的集合
	 * @param gangCards
	 * @return
	 */
	public static List<Integer> getFengList(List<XuanFengGangCard> gangCards){
		List<Integer> arrayList = new ArrayList<>();
		for (XuanFengGangCard xuanFengGangCard : gangCards) {
			List<Integer> cards = xuanFengGangCard.getCards();
			for (Integer integer : cards) {
				arrayList.add(integer);
			}
		}
		return arrayList;
	}
}
