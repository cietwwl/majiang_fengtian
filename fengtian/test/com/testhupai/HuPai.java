package com.testhupai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HuPai {

	private boolean isDiaoJiang = false;
	private boolean isHu = false;

	public static void main(String[] args) {
		testTing();
//		int[] array = {0,1,4,5,8,9,12,13,18,19,22,23,26,56};
//		showPai(array);
//		List<Integer> cards = arrayToList(array);
//		boolean qiDui = isQiDui(cards,26);
//		System.out.println(qiDui);
	}


	private static void testTing() {
		long currentTimeMillis = System.currentTimeMillis();
		int[] array = {6,7,10,23,24,48,51,61,67,76,90,97,127};
		showPai(array);
		HuPai huPai = new HuPai();
		boolean hu = huPai.isHu(array, 108);
		System.out.println(hu);
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
	}

	
	/**是否是七对
	 * @param cards
	 * @return
	 */
	public static boolean isQiDui(List<Integer> cards,int baiDa){
		boolean result = true;
		
		if(cards.size()!=14){
			return false;
		}
		
		int totalBaiDa = 0;
		int totalNeed = 0;
		
		baiDa = baiDa/4;
		for(int i=0;i<cards.size()-1;i=i+2){
			Integer card = cards.get(i);
			Integer next = null;
			if(card/4==baiDa){
				totalBaiDa ++;
			}
			if(i+1<cards.size()){
				next = cards.get(i+1);
			}
			if(next!=null){
				if(card/4!=next/4){
					totalNeed++;
				}
			}
		}
		if(totalNeed>totalBaiDa){
			return false;
		}
		return result;
	}
	
	
	/**得到听牌的集合
	 * @param myCards
	 * @param baiDa
	 * @return
	 */
	public static List<Integer> getTingList(List<Integer> myCards,int baiDa) {
		Integer temp = null;
		List<Integer> ting =  new ArrayList<Integer>();
		
		for(int i=0;i<=135;i=i+4){
			if(temp!=null){
				myCards.remove(temp);
			}
			myCards.add(new Integer(i));
			Collections.sort(myCards);
			temp = new Integer(i);
			HuPai huPai = new HuPai();
			boolean hu = huPai.isHu(myCards, baiDa);
			if(hu){
				ting.add(i);
			}
		}
		return ting;
	}

	
	
	public boolean isHu() {
		return isHu;
	}

	public void setHu(boolean isHu) {
		this.isHu = isHu;
	}

	public boolean isDiaoJiang() {
		return isDiaoJiang;
	}

	public void setDiaoJiang(boolean isDiaoJiang) {
		this.isDiaoJiang = isDiaoJiang;
	}

	public static String showPai(int array[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			System.out.print(InitialPuKe.map.get(array[i]) + " ");
			sb.append(InitialPuKe.map.get(array[i]) + " ");
		}
		System.out.println();
		return sb.toString();
	}

	public static String showPai(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			System.out.print(InitialPuKe.map.get(list.get(i)) + " ");
			sb.append(InitialPuKe.map.get(list.get(i)) + " ");
		}
		System.out.println();
		return sb.toString();
	}
	
	public boolean isHu(int[] array, int bdNumber) {
		List<Integer> arrayToList = arrayToList(array);
		return isHu(arrayToList, bdNumber);
	}

	public boolean isHu(List<Integer> arrayToList, int bdNumber) {
		Map<Integer, List<Integer>> cardMap = getCardMap(arrayToList, bdNumber);
		int wanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(1)), 0);
		int bingNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(2)), 0);
		int tiaoNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(3)), 0);
		int zhongFaBaiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(4)), 0);
		int dongNanXiBeiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(5)), 0);
		int remainsHongZhong = 0;
		int haveHongZhong = cardMap.get(0).size();
		remainsHongZhong = haveHongZhong - bingNeed - tiaoNeed - zhongFaBaiNeed - dongNanXiBeiNeed;
		// 将在万中
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(1), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在饼中
		remainsHongZhong = haveHongZhong - wanNeed - tiaoNeed - zhongFaBaiNeed - dongNanXiBeiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(2), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在条中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - zhongFaBaiNeed - dongNanXiBeiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(3), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在红中发白
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed  - dongNanXiBeiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(4), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在东南西北中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		return false;
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
		map.put(0, list0);
		map.put(1, list1);
		map.put(2, list2);
		map.put(3, list3);
		map.put(4, list4);
		map.put(5, list5);
		for (int i = 0; i < cards.size(); i++) {
			int card = cards.get(i) / 4;
			if (card == bdNumber) {//百搭
				map.get(0).add(card);
			} else if (card < 9 && card != bdNumber) {//万
				map.get(1).add(card);
			} else if (card < 18 && card != bdNumber) {//筒
				map.get(2).add(card);
			} else if (card < 27 && card != bdNumber) {//条
				map.get(3).add(card);
			} else if (card < 30 && card != bdNumber) {//中发白
				map.get(4).add(card);
			} else if (card < 34 && card != bdNumber) {//东、南、西、北
				map.get(5).add(card);
			}
		}
		return map;
	}

	public static List<Integer> arrayToList(int[] array) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static List<Integer> getNewListFromOldList(List<Integer> cards) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < cards.size(); i++) {
			list.add(cards.get(i));
		}
		return list;
	}

	private boolean isHuNoDui(List<Integer> cards, int haveHongZhong) {
		int step = 1;
		boolean result = false;
		for (int i = 0; i < cards.size(); i = i + step) {
			Integer now = cards.get(i);
			Integer next = null;
			if (i + 1 <= cards.size() - 1) {
				next = cards.get(i + 1);
			}
			List<Integer> newListFromOldList = getNewListFromOldList(cards);
			newListFromOldList.remove(now);
			if (now == next) {// 有将
				newListFromOldList.remove(next);
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 0);
				if (haveHongZhong - totalNeed >= 0) {
					if (haveHongZhong - totalNeed > 0) {
						this.isDiaoJiang = true;
					}
					result = true;
				}
				step = 1;
			} else {// 无将
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 1);
				if (haveHongZhong - totalNeed >= 0) {
					// 单调将
					this.isDiaoJiang = true;
					result = true;
				}
				step = 1;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public static int getTotalNeedNoDui(List<Integer> list, int totalNeed) {
		if (list.size() == 0) {
			return totalNeed;
		}

		if (list.size() == 1) {
			totalNeed = totalNeed + 2;
			return totalNeed;
		}

		Integer now = list.get(0);
		Integer next = null;
		if (1 <= list.size() - 1) {
			next = list.get(1);
		}
		Integer nnext = null;
		if (2 <= list.size() - 1) {
			nnext = list.get(2);
		}
		if (now == next && next == nnext) {
			list.remove(now);
			list.remove(next);
			list.remove(nnext);
			return getTotalNeedNoDui(list, totalNeed);
		}
		//东、南、西、北 任意三个都行
		if(now>=30){
			if (next != null && nnext != null) {
				if (Math.abs(next - now) <= 2 && Math.abs(nnext - next) <=2) {
					list.remove(now);
					list.remove(next);
					list.remove(nnext);
					return getTotalNeedNoDui(list, totalNeed);
				}
			}
		}else{
			if (next != null && nnext != null) {
				if (next - now == 1 && nnext - next == 1) {
					list.remove(now);
					list.remove(next);
					list.remove(nnext);
					return getTotalNeedNoDui(list, totalNeed);
				}
			}
		}
		
		if(now>=30){
			if (now == next || Math.abs(next - 1 )<= 3) {// 必须有顺子
				Integer nextNumber = null;
				Integer nnextNumber = null;
				for (int i = 1; i < list.size(); i++) {
					Integer n = list.get(i);
					if (nextNumber == null && Math.abs(n - now) <= 3) {
						nextNumber = n;
						continue;
					}
					if (nnextNumber == null && nextNumber!=null && Math.abs(n - nextNumber) <= 2) {
						nnextNumber = n;
					}
					if (nextNumber != null && nnextNumber != null) {
						break;
					}
				}
				if (now != null && nextNumber != null && nnextNumber != null) {
					list.remove(now);
					list.remove(nextNumber);
					list.remove(nnextNumber);
					return getTotalNeedNoDui(list, totalNeed);
				}
				// 没有找到顺子
				list.remove(now);
				list.remove(next);
				totalNeed = totalNeed + 1;
				return getTotalNeedNoDui(list, totalNeed);
			}
		}
		
		if (now == next || next - 1 == now) {// 必须有顺子
			Integer nextNumber = null;
			Integer nnextNumber = null;
			for (int i = 1; i < list.size(); i++) {
				Integer n = list.get(i);
				if (nextNumber == null && n == now + 1) {
					nextNumber = n;
					continue;
				}
				if (nnextNumber == null && n == now + 2) {
					nnextNumber = n;
				}
				if (nextNumber != null && nnextNumber != null) {
					break;
				}
			}
			if (now != null && nextNumber != null && nnextNumber != null) {
				list.remove(now);
				list.remove(nextNumber);
				list.remove(nnextNumber);
				return getTotalNeedNoDui(list, totalNeed);
			}
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			totalNeed = totalNeed + 1;
			return getTotalNeedNoDui(list, totalNeed);
		}
		
		//东、南、西、北 任意三个都行
		if(next != null && now>=30 && next - now <= 3){
			totalNeed = totalNeed + 1;
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			return getTotalNeedNoDui(list, totalNeed);
		}
		
		if (next != null && next - now <= 2) {
			totalNeed = totalNeed + 1;
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			return getTotalNeedNoDui(list, totalNeed);
		} else {
			totalNeed = totalNeed + 2;
			list.remove(now);
			return getTotalNeedNoDui(list, totalNeed);
		}
	}
}
