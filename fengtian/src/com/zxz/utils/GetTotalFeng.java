package com.zxz.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetTotalFeng {

	
	/**
	 * @param cards 我自己的牌
	 * @param baidDa 百搭的牌
	 * @param jiangList 将牌
	 * @return
	 */
	public static int getTotalFeng(List<Integer> yuancards,int baidDa,List<Integer> jiangList){
		List<Integer> cards = HuPai.getNewListFromOldList(yuancards);
		Integer jiang1 = jiangList.get(0);
		Integer jiang2 = jiangList.get(1);
		
		List<Integer> removeList =  new ArrayList<>();
		
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==jiang1||card/4==jiang2){
				removeList.add(card);
			}
		}
		
		for(int i=0;i<removeList.size();i++){
			Integer removeCard = removeList.get(i);
			cards.remove(removeCard);
		}
		
		Map<Integer, List<Integer>> cardMap = getCardMap(cards, baidDa);
		int totalBaiDa = cardMap.get(0).size();
		if(totalBaiDa>=0&&((jiang1>200)||(jiang2>200))){
			totalBaiDa = totalBaiDa-1;
		}
		int zhong = cardMap.get(4).size();
		int fa = cardMap.get(5).size();
		int bai = cardMap.get(6).size();
		int dong = cardMap.get(7).size();
		int nan = cardMap.get(8).size();
		int xi = cardMap.get(9).size();
		int bei = cardMap.get(10).size();
		int minZhongFaBai =100;
		if(zhong>0&&fa>0&&bai>0){
			if(zhong<minZhongFaBai){
				minZhongFaBai = zhong;
			}
			if(fa<minZhongFaBai){
				minZhongFaBai = fa;
			}
			if(bai<minZhongFaBai){
				minZhongFaBai = bai;
			}
		}
		int totalFeng = 0 ;
		if(minZhongFaBai!=100){
			totalFeng = totalFeng + minZhongFaBai;
		}
		
		int minDongNanXiBei = 100;
		if(dong<minDongNanXiBei&&dong!=0){
			minDongNanXiBei = dong;
		}
		if(nan<minDongNanXiBei&&nan!=0){
			minDongNanXiBei = nan;
		}
		if(xi<minDongNanXiBei&&xi!=0){
			minDongNanXiBei = xi;
		}
		if(bei<minDongNanXiBei&&bei!=0){
			minDongNanXiBei = bei;
		}
		if(minDongNanXiBei!=100){
			totalFeng = totalFeng + minDongNanXiBei;
		}
		return totalFeng;
	}
	
	
	
	
	
	
	/**
	 * @return
	 */
	public static Map<Integer, List<Integer>> getCardMap(List<Integer> cards, int bdNumber) {
		bdNumber = bdNumber / 4;
		Map<Integer, List<Integer>> map = new LinkedHashMap<Integer, List<Integer>>();
		List<Integer> list0 = new LinkedList<Integer>();
		List<Integer> list1 = new LinkedList<Integer>();
		List<Integer> list2 = new LinkedList<Integer>();
		List<Integer> list3 = new LinkedList<Integer>();
		List<Integer> list4 = new LinkedList<Integer>();
		List<Integer> list5 = new LinkedList<Integer>();
		List<Integer> list6 = new LinkedList<Integer>();
		List<Integer> list7 = new LinkedList<Integer>();
		List<Integer> list8 = new LinkedList<Integer>();
		List<Integer> list9 = new LinkedList<Integer>();
		List<Integer> list10 = new LinkedList<Integer>();
		map.put(0, list0);
		map.put(1, list1);
		map.put(2, list2);
		map.put(3, list3);
		map.put(4, list4);
		map.put(5, list5);
		map.put(6, list6);
		map.put(7, list7);
		map.put(8, list8);
		map.put(9, list9);
		map.put(10, list10);
		for (int i = 0; i < cards.size(); i++) {
			int card = cards.get(i) / 4;
			if (card == bdNumber) {
				map.get(0).add(card);
			} else if (card < 9 && card != bdNumber) {
				map.get(1).add(card);
			} else if (card < 18 && card != bdNumber) {
				map.get(2).add(card);
			} else if (card < 27 && card != bdNumber) {
				map.get(3).add(card);
			} else if (card < 28 && card != bdNumber) {//中
				map.get(4).add(card);
			} else if (card < 29 && card != bdNumber) {//发
				map.get(5).add(card);
			} else if (card < 30 && card != bdNumber) {//白
				map.get(6).add(card);
			} else if (card < 31 && card != bdNumber) {//东
				map.get(7).add(card);
			} else if (card < 32 && card != bdNumber) {//南
				map.get(8).add(card);
			} else if (card < 33 && card != bdNumber) {//西
				map.get(9).add(card);
			} else if (card < 34 && card != bdNumber) {//北
				map.get(10).add(card);
			}
		}
		return map;
	}
	
}
