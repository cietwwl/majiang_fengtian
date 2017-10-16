package com.zxz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zxz.algorithm.InitialPuKe;
import com.zxz.domain.GangCard;
import com.zxz.domain.PengCard;

public class FengTest {

	private boolean isHu = false;
	private boolean isSiHu = false;
	private boolean isQiDui = false;
	List<Integer> jiangList = new ArrayList<>();// 将的集合

	public static void main(String[] args) {
		FengTest fengTest = new FengTest();
		int[] arrayToList = {0,1,16,17,32,33,34,35,82,83,94,95,108,109};
		showPai(arrayToList);
		boolean hu = fengTest.isHu(arrayToList, 0);
		boolean qiDui = fengTest.isQiDui();
		System.out.println(hu+":"+qiDui);
		List<Integer> cards = arrayToList(arrayToList);
//		int totalFengMo = fengTest.getTotalFengMo(cards, 0);
//		System.out.println(totalFengMo);
		
	}

	/**
	 * @return
	 */
	public List<Integer> removeBdListWithWanTongTiao(List<Integer> list, List<Integer> bdList) {
		if (list.size() == 0) {
			return bdList;
		}

		if (list.size() == 1) {
			if (bdList.size() >= 2) {
				bdList.remove(0);
				bdList.remove(0);
			}
			return bdList;
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
			return removeBdListWithWanTongTiao(list, bdList);
		}

		if (next != null && nnext != null) {
			if (next - now == 1 && nnext - next == 1) {
				list.remove(now);
				list.remove(next);
				list.remove(nnext);
				return removeBdListWithWanTongTiao(list, bdList);
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
				return removeBdListWithWanTongTiao(list, bdList);
			}
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			if (bdList.size() >= 1) {
				bdList.remove(0);
			}
			return removeBdListWithWanTongTiao(list, bdList);
		}

		if (next != null && next - now <= 2) {
			if (bdList.size() >= 1) {
				bdList.remove(0);
			}
			// 没有找到顺子
			list.remove(now);
			list.remove(next);
			return removeBdListWithWanTongTiao(list, bdList);
		} else {
			if (bdList.size() >= 2) {
				bdList.remove(0);
				bdList.remove(0);
			}
			list.remove(now);
			return removeBdListWithWanTongTiao(list, bdList);
		}
	}

	/**
	 * @return 风的个数
	 */
	public int getFengWithDongNanXiBei(List<Integer> list, int totalFengMo, List<Integer> bdList) {
		if (list.size() == 0) {
			return totalFengMo;
		}
		if (list.size() == 1) {
			if (bdList.size() >= 2) {
				bdList.remove(0);
				bdList.remove(0);
				totalFengMo = totalFengMo + 1;
			}
			list.remove(0);
			return totalFengMo;
		}
		if (list.size() == 2) {
			Integer now = list.get(0);
			Integer next = list.get(1);
			if (next - now >= 1) {
				if (bdList.size() >= 1) {
					totalFengMo++;
					bdList.remove(0);
				}
			}
			list.remove(now);
			list.remove(next);
			return totalFengMo;
		}

		Integer now = list.get(0);
		Integer next = list.get(1);
		Integer nnext = list.get(2);

		if (next - 1 >= now && nnext - 1 >= next) {
			list.remove(now);
			list.remove(next);
			list.remove(nnext);
			totalFengMo++;
			return getFengWithDongNanXiBei(list, totalFengMo, bdList);
		}

		Integer nextNumber = null;
		Integer nnextNumber = null;
		for (int i = 1; i < list.size(); i++) {
			Integer n = list.get(i);
			if (nextNumber == null && n - now >= 1) {
				nextNumber = n;
				continue;
			}
			if (nnextNumber == null && nextNumber != null && n - nextNumber >= 1) {
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
			totalFengMo++;
			return getFengWithDongNanXiBei(list, totalFengMo, bdList);
		}
		list.remove(now);
		list.remove(next);
		list.remove(nnext);
		return getFengWithDongNanXiBei(list, totalFengMo, bdList);
	}

	/**
	 * @return 风的个数
	 */
	public int getFengWithZhongFaBai(List<Integer> list, int totalFengMo, List<Integer> bdList) {
		if (list.size() == 0) {
			return totalFengMo;
		}
		if (list.size() == 1) {
			if (bdList.size() >= 2) {
				bdList.remove(0);
				bdList.remove(0);
				totalFengMo = totalFengMo + 1;
			}
			list.remove(0);
			return totalFengMo;
		}
		if (list.size() == 2) {
			Integer now = list.get(0);
			Integer next = list.get(1);
			if (next - now >= 1) {
				if (bdList.size() >= 1) {
					totalFengMo++;
					bdList.remove(0);
				}
			}
			list.remove(now);
			list.remove(next);
			return totalFengMo;
		}

		Integer now = list.get(0);
		Integer next = list.get(1);
		Integer nnext = list.get(2);

		if (next - 1 == now && nnext - 1 == next) {
			list.remove(now);
			list.remove(next);
			list.remove(nnext);
			totalFengMo++;
			return getFengWithZhongFaBai(list, totalFengMo, bdList);
		}

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
			totalFengMo++;
			return getFengWithZhongFaBai(list, totalFengMo, bdList);
		}
		list.remove(now);
		list.remove(next);
		list.remove(nnext);
		return getFengWithZhongFaBai(list, totalFengMo, bdList);
	}

	public boolean isQiDui() {
		return isQiDui;
	}

	public void setQiDui(boolean isQiDui) {
		this.isQiDui = isQiDui;
	}

	private static void testTing() {
		long currentTimeMillis = System.currentTimeMillis();
		int[] array = { 16, 19, 20, 20, 24, 26, 44, 46, 49, 50, 98, 99, 120, 121 };
		showPai(array);
		FengTest huPai = new FengTest();
		boolean hu = huPai.isHu(array, 108);
		System.out.println(hu);
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
	}



	/**
	 * 是否是七对
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isQiDui(List<Integer> cards, int baiDa) {
		boolean result = true;
		if(cards.size()!=14){
			return false;
		}
		int totalBaiDa = 0;
		int totalNeed = 0;
		baiDa = baiDa/4;
		List<Integer> removeList =  new ArrayList<>();
		for(int i=0;i<cards.size();i++){
			Integer card = cards.get(i);
			if(card/4==baiDa){
				removeList.add(card);
			}
		}
		totalBaiDa = removeList.size();
		List<Integer> newCards = getNewListFromOldList(cards);
		
		for(int i=0;i<removeList.size();i++){
			Integer removeCard = removeList.get(i);
			newCards.remove(removeCard);
		}
		int step = 2;
		for(int i=0;i<newCards.size()-1;i=i+step){
			Integer card = newCards.get(i);
			Integer next = null;
			
			if(i+1<newCards.size()){
				next = newCards.get(i+1);
			}
			if(next!=null){
				if(card/4!=next/4){
					totalNeed+=1;
					step = 1;
				}else{
					step =2;
				}
			}
		}
		if(totalNeed>totalBaiDa){
			return false;
		}
		return result;
	}

	public boolean isHu() {
		return isHu;
	}

	public void setHu(boolean isHu) {
		this.isHu = isHu;
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

	/**
	 * 是否胡牌
	 * 
	 * @param arrayToList
	 * @param bdNumber
	 * @return
	 */
	public boolean isHu(List<Integer> arrayToList, int bdNumber) {
		boolean siHun = isSiHun(arrayToList, bdNumber);
		if (siHun) {
			this.isHu = true;
			
			return true;
		}
		boolean qiDui = isQiDui(arrayToList, bdNumber);
		if (qiDui) {
			this.isQiDui = true;
			this.isHu = true;
			return true;
		} else {
			boolean huWithoutQiDui = isHuWithoutQiDui(arrayToList, bdNumber);
			return huWithoutQiDui;
		}
	}

	/**
	 * @param arrayToList
	 * @param bdNumber
	 * @return
	 */
	public boolean isSiHun(List<Integer> arrayToList, int bdNumber) {
		if (bdNumber > 135) {
			return false;
		}
		int b = bdNumber / 4;
		int totalHui = 0;
		for (int i = 0; i < arrayToList.size(); i++) {
			Integer card = arrayToList.get(i);
			if (card / 4 == b) {
				totalHui++;
			}
		}
		if (totalHui >= 4) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否缺一门
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean isQueYiMen(List<Integer> cards, int bdNumber) {
		boolean result = true;
		bdNumber = bdNumber / 4;
		boolean haveWan = false;
		boolean bingWan = false;
		boolean tiaoWan = false;
		for (int i = 0; i < cards.size(); i++) {
			int card = cards.get(i) / 4;
			if (card < 9 && card != bdNumber) {// 万
				haveWan = true;
			} else if (card < 18 && card != bdNumber) {// 筒
				bingWan = true;
			} else if (card < 27 && card != bdNumber) {// 条
				tiaoWan = true;
			}
		}
		if (haveWan && bingWan && tiaoWan) {
			return false;
		}
		return result;
	}

	// 非七对胡牌
	private boolean isHuWithoutQiDui(List<Integer> arrayToList, int bdNumber) {
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

		// 将在中发白
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - dongNanXiBeiNeed;
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
			if (card == bdNumber) {// 百搭
				map.get(0).add(card);
			} else if (card < 9 && card != bdNumber) {// 万
				map.get(1).add(card);
			} else if (card < 18 && card != bdNumber) {// 筒
				map.get(2).add(card);
			} else if (card < 27 && card != bdNumber) {// 条
				map.get(3).add(card);
			} else if (card < 30 && card != bdNumber) {// 中发白
				map.get(4).add(card);
			} else if (card < 34 && card != bdNumber) {// 东、南、西、北
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
				int remain = haveHongZhong - totalNeed;
				if (remain >= 0) {
					result = true;
					jiangList.add(now);
					jiangList.add(next);
					break;
				}
				step = 1;
			} else {// 无将
				int totalNeed = getTotalNeedNoDui(newListFromOldList, 1);
				int remain = haveHongZhong - totalNeed;
				if (remain >= 0) {
					jiangList.add(now);
					result = true;
					break;
				}
				step = 1;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public int getTotalNeedNoDui(List<Integer> list, int totalNeed) {
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
		// 东、南、西、北 任意三个都行
		if (now >= 30) {
			if (next != null && nnext != null) {
				if (Math.abs(next - now) >= 1 && Math.abs(nnext - next) >= 1) {
					list.remove(now);
					list.remove(next);
					list.remove(nnext);
					return getTotalNeedNoDui(list, totalNeed);
				}
			}
		} else {
			if (next != null && nnext != null) {
				if (next - now == 1 && nnext - next == 1) {
					list.remove(now);
					list.remove(next);
					list.remove(nnext);
					return getTotalNeedNoDui(list, totalNeed);
				}
			}
		}

		if (now >= 30) { // 东南西北
			if (now == next || next - 1 >= now) {// 必须有顺子
				Integer nextNumber = null;
				Integer nnextNumber = null;
				for (int i = 1; i < list.size(); i++) {
					Integer n = list.get(i);
					if (nextNumber == null && n - now >= 1) {
						nextNumber = n;
						continue;
					}
					if (nnextNumber == null && nextNumber != null && n - nextNumber >= 1) {
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

		// 东、南、西、北 任意三个都行
		if (next != null && now >= 30 && next - now <= 3) {
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

	public int getTotalFengMo(List<Integer> userCards, int baiDa) {
		List<Integer> cards = getNewListFromOldList(userCards);
		Integer jiang = jiangList.get(0);
		List<Integer> removeList = new ArrayList<>();

		if (jiangList.size() == 2) {
			for (int i = 0; i < cards.size(); i++) {
				Integer card = cards.get(i);
				if (card / 4 == jiang && removeList.size() < 2) {
					removeList.add(card);
				}
				if (removeList.size() >= 2) {
					break;
				}
			}
		}

		if (jiangList.size() == 1) {
			for (int i = 0; i < cards.size(); i++) {
				Integer card = cards.get(i);
				if (card / 4 == jiang && removeList.size() < 1) {
					removeList.add(card);
				}
				if (removeList.size() >= 1) {
					break;
				}
			}
		}
		// 去掉对子
		for (int i = 0; i < removeList.size(); i++) {
			Integer removeCard = removeList.get(i);
			cards.remove(removeCard);
		}
		// 得到中发白和东南西北
		Map<Integer, List<Integer>> cardMap = getCardMap(cards, baiDa);
		List<Integer> bdList = cardMap.get(0);
		if (jiangList.size() == 1 && bdList.size() >= 1) {
			bdList.remove(0);
		}
		List<Integer> wanList = cardMap.get(1);
		List<Integer> bingList = cardMap.get(2);
		List<Integer> tiaoList = cardMap.get(3);
		List<Integer> zhongFaBaiList = cardMap.get(4);
		List<Integer> dongXiNanBeiList = cardMap.get(5);
		removeBdListWithWanTongTiao(wanList, bdList);
		removeBdListWithWanTongTiao(bingList, bdList);
		removeBdListWithWanTongTiao(tiaoList, bdList);
		int totalFengWithZhongFaBai = getFengWithZhongFaBai(zhongFaBaiList, 0, bdList);
		int fengWithDongNanXiBei = getFengWithDongNanXiBei(dongXiNanBeiList, 0, bdList);
		int result = totalFengWithZhongFaBai + fengWithDongNanXiBei;
		return result;
	}
}
