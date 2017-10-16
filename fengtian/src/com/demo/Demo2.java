package com.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.zxz.domain.User;

public class Demo2 {
	public static void main(String[] args) {
//		test1();
//		test2();
		/**
        19,
                31,
                46,
                47,
                56,
                57,
                76,
                103,
                112,
                121,
                126,
                129,
                131,
                134
		 */
//		test3();
//		test4();
		HashMap<Integer, String> map = new HashMap<>();
		
		map.put(1, "east");
		map.put(2, "east");
		map.put(3, "east");
		System.out.println(map.size());
		Iterator<Integer> iterator = map.keySet().iterator();
		List<Integer> list = new ArrayList<>();
		while (iterator.hasNext()) {
			
			Integer integer = (Integer) iterator.next();
			String string = map.get(integer);
			if(integer!=2&&string.equals("east")){
				list.add(integer);
			}
		}
		for (Integer integer : list) {
			map.remove(integer);
		}
		System.out.println(map);
	}
	private static void test4() {
		List<Integer> list = new ArrayList();
		list.add(5);
		list.add(10);
		list.add(13);
		list.add(26);
		list.add(30);
		list.add(35);
		list.add(48);
		list.add(53);
		list.add(57);
		
		list.add(59);
		list.add(105);
//		list.add(128);
//		list.add(134);
		
		List<List<Integer>> analysisUserIsCanXuanFengGang = analysisUserIsCanXuanFengGang(list);
		System.out.println(analysisUserIsCanXuanFengGang.size());
		
		for (List<Integer> list2 : analysisUserIsCanXuanFengGang) {
			System.out.println(list2);
		}
	}
	private static List<List<Integer>> analysisUserIsCanXuanFengGang(List<Integer> oldCards) {
		List<List<Integer>> xuanFengGangCards = new ArrayList<>();
		List<Integer> newList = new ArrayList<>();//临时保存的集合 四张牌一个类型  类型集合
		
//		List<Integer> oldCards = user.getCards();
		
		List<Integer> cards = new ArrayList<>();
		cards = oldCards;
		for (int i = 0; i < cards.size(); i++) {
			newList.add(cards.get(i)/4);
		}
		List<Integer> baiFengList = new ArrayList<>();
		baiFengList.add(27);//中
		baiFengList.add(28);//发
		baiFengList.add(29);//白
		List<Integer> heiFengList = new ArrayList<>();
		heiFengList.add(30);//东
		heiFengList.add(31);//南
		heiFengList.add(32);//西
		heiFengList.add(33);//北
		//遍历手牌  如果包含同时中发白 或东南西北  把他们取出来放到数组中,存到集合里面 
		//,然后把手牌中的这些牌清掉  然后在继续遍历 直到没有了结束循环   
		for (int i = 0; i < 4; i++) {
			if(newList.containsAll(baiFengList)){
				List<Integer> baifeng = new ArrayList<>();
				for (int j = 0; j < baiFengList.size(); j++) {
					int indexOf = newList.indexOf(baiFengList.get(j));
					baifeng.add(cards.get(indexOf));
				}
				xuanFengGangCards.add(baifeng);
				for (int j = baiFengList.size()-1; j >=0; j--) {
					int indexOf = newList.indexOf(baiFengList.get(j));
					newList.remove(indexOf);
					cards.remove(indexOf);
				}
			}else if(newList.containsAll(heiFengList)){
				List<Integer> heifeng = new ArrayList<>();
				for (int j = 0; j < heiFengList.size(); j++) {
					int indexOf = newList.indexOf(heiFengList.get(j));
					heifeng.add(cards.get(indexOf));
				}
				xuanFengGangCards.add(heifeng);
				for (int j = heiFengList.size()-1; j >=0; j--) {
/*					int cardId = heiFengList.get(j).intValue();
					
					for (int k = 0; k < newList.size(); k++) {
						if (cardId == newList.get(k).intValue()) {
							newList.remove(k);
							break;
						}
					}*/
					int indexOf = newList.indexOf(heiFengList.get(j));
					newList.remove(indexOf);
					cards.remove(indexOf);
				}
			}else{
				break;
			}
		}
		return xuanFengGangCards;
	}
	
	
	
	private static void test3() {
		int[] a = new int[]{0,11,42,43,104,105,106,107};
		List<Integer> arrayList = new ArrayList<>();
		for (int i : a) {
			arrayList.add(i);
		}
		List<Integer> userCanAnGang = isUserCanAnGang(arrayList);
		System.out.println(userCanAnGang);
	}
	public static List<Integer> isUserCanAnGang(List<Integer> cards){
		int type = cards.get(0)/4;
		int total = 0;
		int compareCard = cards.get(0);
		List<Integer> anGangCards = new ArrayList<>();
		for(int i=1;i<cards.size();i++){
			Integer card = cards.get(i);
			int currentType = card/4;
			if(type == currentType){
				total++;
				anGangCards.add(card);
				if(total==3){
					anGangCards.add(compareCard);
					break;
				}
			}else{
				type = currentType;
				total = 0;//计数清零
				anGangCards = new ArrayList<>();
				compareCard = card;
			}
		}
		if(anGangCards.size()!=4){
			return new ArrayList<Integer>();
		}
		Collections.sort(anGangCards);
		return anGangCards;
	}
	private static void test2() {
		List<Integer> heiFengList = new ArrayList<>();
		heiFengList.add(30);//东
		heiFengList.add(31);//南
		heiFengList.add(32);//西
		heiFengList.add(33);//北
		
		List<Integer> cardList = new ArrayList<>();
		cardList.add(30);
		cardList.add(30);
		cardList.add(31);
		cardList.add(32);
		cardList.add(33);
		cardList.add(33);
		cardList.add(34);
//		cardList.removeAll(heiFengList);
//		cardList.remove(heiFengList);
		for (int i = 0; i < heiFengList.size(); i++) {
			int indexOf = cardList.indexOf(heiFengList.get(i));
			cardList.remove(indexOf);
		}
		
		System.out.println(cardList);
	}

	private static void test1() {
		List<String> oldList = new ArrayList<>();
		oldList.add("小小");
		oldList.add("小毛");
		oldList.add("小猪");
		oldList.add("小小");
		oldList.add("小猪");
		oldList.add("小猪");
		List<String> newList = new ArrayList<>();
		List<String> newList2 = new ArrayList<>();
		for (int i = 0; i < oldList.size(); i++) {
			if(!newList.contains(oldList.get(i))){
				newList.add(oldList.get(i));
			}else{
				newList2.add(oldList.get(i));
			}
		}
		newList.removeAll(newList2);
		System.out.println(newList);
	}
}
