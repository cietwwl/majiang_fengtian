package com.zxz.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mysql.fabric.xmlrpc.base.Array;
import com.zxz.algorithm.InitialPuKe;
import com.zxz.domain.ChiCard;
import com.zxz.domain.GangCard;
import com.zxz.domain.OneRoom;
import com.zxz.domain.PengCard;
import com.zxz.domain.XuanFengGangCard;
import com.zxz.service.PlayGameService;

public class HuPai {

	private boolean isDiaoJiang = false;
	private boolean isHu = false;
	private boolean isQiDui = false;
	
	int totalFeng = 0;
	
	public static void main(String[] args) {
//		testTing();
//		int[] array = {0,1,4,5,8,9,12,13,18,19,22,23,26,56};
//		showPai(array);
//		List<Integer> cards = arrayToList(array);
//		boolean qiDui = isQiDui(cards,26);
//		System.out.println(qiDui);
		int[] array = {0,1,2,8,9,10,16,17,18,66,67,72,73,74};
		List<Integer> arrayToList = arrayToList(array);
/*		boolean qiDui = isQiDui(arrayToList);
		System.out.println(qiDui);*/
/*		boolean haveKeOrPeng = isHaveKeOrPeng(arrayToList, null, null);
		System.out.println(haveKeOrPeng);*/
//		boolean queYiMen = isQueYiMen(arrayToList, null, null);
//		System.out.println(queYiMen);
/*		boolean haveYaoJiu = isHaveYaoJiu(arrayToList,null,null);
		System.out.println(haveYaoJiu);*/
/*		boolean piao = isPiao(arrayToList, null, null,null);
		System.out.println(piao);*/
/*		int[] yaojiu = new int[]{0,8,9,17,18,26};
		List<Integer> arrayToList2 = arrayToList(yaojiu);
		if(arrayToList2.contains(0)){
			System.out.println(true);
		}*/
/*		HuPai huPai = new HuPai();
		List<PengCard> pengCards = new ArrayList<>();
		List<GangCard> gangCards = new ArrayList<>();
		List<ChiCard> chiCards = new ArrayList<>();
		OneRoom oneRoom = new OneRoom();
		boolean hu = huPai.isHu(arrayToList, pengCards, gangCards, chiCards, oneRoom);
		System.out.println(hu);*/
/*		ArrayList<Integer> arrayList = new ArrayList<>();
		arrayList.add(0);
		arrayList.add(2);
		arrayList.add(5);
		arrayList.add(9);
		ArrayList<Object> paixunList = new ArrayList<>();
		paixunList.add(33);
		paixunList.addAll(arrayList);
		System.out.println(paixunList);*/
		HuPai test1 = new HuPai();
		ArrayList<Integer> arrayList = new ArrayList<>();
		/*arrayList.add(120);
		arrayList.add(124);
		arrayList.add(125);
		arrayList.add(126);
		arrayList.add(128);
		arrayList.add(129);
		arrayList.add(132);
		arrayList.add(133);*/
/*		System.out.println(arrayList);
		for (Integer integer : arrayList) {
			if(integer==124){
				arrayList.remove(integer);
				break;
			}
		}
		System.out.println(arrayList);*/
//		digui(arrayList);
//		boolean jigefeng = test1.jigefeng(arrayList);
//		System.out.println(jigefeng);
		
		HuPai huPai = new HuPai();
//		24,25,27,29,39,42,46
		arrayList.add(24);
		arrayList.add(25);
		arrayList.add(27);
		arrayList.add(29);
		arrayList.add(39);
		arrayList.add(42);
		arrayList.add(46);
		List<ChiCard> list = new ArrayList<>();
		ChiCard chiCard = new ChiCard();
		List<Integer> arrayList2 = new ArrayList<>();
		arrayList2.add(90);
		arrayList2.add(95);
		arrayList2.add(99);
		chiCard.addChiCard(arrayList2, "east");
		list.add(chiCard);
		huPai.isHu(arrayToList, null, null, list, null, null, null);
	}
	/**
	 * 
	 * @param cards
	 */
	public  boolean jigefeng(List<Integer> cards){
		ArrayList<Object> lll = new ArrayList<>();
		boolean jieguo = false;
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		ArrayList<Integer> fengList = new ArrayList<Integer>();
		int type= 0;
		int feng = 0;
		for (int i = 0; i < cards.size(); i++) {
			int num  = cards.get(i)/4;
			if(num>=30&&num<=33){
				feng ++;
				fengList.add(num);
				if(!arrayList.contains(num)){
					type ++;
				}
			}
		}
		if(type>2&&feng>=5){//两种类型以上 进行判断
			
			List<Integer> digui = digui(fengList);
			
			System.out.println(digui);
			
			boolean hu2 = isHuNoDui1(digui, 0);
			if (hu2) {
				this.isHu = true;
				return true;
			}
			
		}
		return jieguo;
	}
	

	
	private boolean isHuNoDui1(List<Integer> cards, int haveHongZhong) {
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
				int totalNeed = getTotalNeedNoDui1(newListFromOldList, 0);
				if (haveHongZhong - totalNeed >= 0) {
					if (haveHongZhong - totalNeed > 0) {
						this.isDiaoJiang = true;
					}
					result = true;
				}
				step = 1;
			} else {// 无将
				int totalNeed = getTotalNeedNoDui1(newListFromOldList, 1);
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
	private static List<Integer> digui(ArrayList<Integer> fengList) {
		LinkedList<Integer> linkedList1 = new LinkedList<>();
		LinkedList<Integer> linkedList2 = new LinkedList<>();
		LinkedList<Integer> linkedList3 = new LinkedList<>();
		LinkedList<Integer> linkedList4 = new LinkedList<>();
		ArrayList<Integer> arrayList = new ArrayList<>();
		ArrayList<Integer> paixunList = new ArrayList<>();
		
		for (Integer feng : fengList) {
			if(feng==30){
				linkedList1.add(feng);
			}else if(feng==31){
				linkedList2.add(feng);
			}else if(feng==32){
				linkedList3.add(feng);
			}else if(feng==33){
				linkedList4.add(feng);
			}
		}
		if(linkedList1.size()>0){
			arrayList.add(linkedList1.size());
		}
		if(linkedList2.size()>0){
			arrayList.add(linkedList2.size());
		}		
		if(linkedList3.size()>0){
			arrayList.add(linkedList3.size());
		}		
		if(linkedList4.size()>0){
			arrayList.add(linkedList4.size());
		}
		if(arrayList.size()>2){
			Collections.sort(arrayList,Collections.reverseOrder());
			for (Integer integer : arrayList) {
				if(integer==linkedList1.size()){
					paixunList.addAll(linkedList1);
					linkedList1.clear();
				}
				if(integer==linkedList2.size()){
					paixunList.addAll(linkedList2);
					linkedList2.clear();
				}
				if(integer==linkedList3.size()){
					paixunList.addAll(linkedList3);
					linkedList3.clear();
				}
				if(integer==linkedList4.size()){
					paixunList.addAll(linkedList4);
					linkedList4.clear();
				}
			}
		}
		System.out.println(paixunList);
		//FIXME  去进行抽取风
		return paixunList;
	}
	
	private static void testTing() {
		long currentTimeMillis = System.currentTimeMillis();
//		int[] array = {0,1,2,3,12,24,36,48,60,72,84,96,108,130};
		int[] array = {0,1,4,5,8,9,12,13,16,17,20,21,24};
		showPai(array);
		HuPai huPai = new HuPai();
		List<Integer> arrayToList = arrayToList(array);
		List<PengCard> pengCards = new ArrayList<>();
		List<GangCard> gangCards = new ArrayList<>();
		List<ChiCard> chiCards = new ArrayList<>();
		huPai.isHu(arrayToList, pengCards , gangCards,chiCards,null,null,null);
		boolean hu = huPai.isHu();
		System.out.println(hu);
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println(currentTimeMillis2 - currentTimeMillis);
	}
	
	



	public boolean isQiDui() {
		return isQiDui;
	}
	public void setQiDui(boolean isQiDui) {
		this.isQiDui = isQiDui;
	}
	/**是否是七对
	 * @param cards
	 * @return
	 */
	public static boolean isQiDui(List<Integer> cards){
		boolean result = true;
		if(cards.size()!=14){
			return false;
		}
		List<Integer> newCards = getNewListFromOldList(cards);
		int step = 2;
		for(int i=0;i<newCards.size()-1;i=i+step){
			Integer card = newCards.get(i);
			Integer next = null;		
			if(i+1<newCards.size()){
				next = newCards.get(i+1);
			}
			if(next!=null){
				if(card/4==next/4){
					step =2;
				}else{
					result = false;
				}
			}
		}

		return result;
	}
	
	/**是否有刻字或者碰牌
	 * @param cards
	 * @return
	 */
	public static boolean isHaveKeOrPeng(List<Integer> cards, List<PengCard> pengCards,List<GangCard> gangCards){
		boolean result = false;
		int step = 3;
		if(cards.size()>=2){
			for(int i=0;i<cards.size();i=i+step){
				Integer card = cards.get(i);
				Integer next = null;
				Integer next2 = null;
				if(i+2<cards.size()){
					next = cards.get(i+1);
					next2 = cards.get(i+2);
				}
				if(next!=null&&next2!=null){
					if(card/4==next/4&&card/4==next2/4){
						return true;
					}else{
						step = 1;
					}
				}
			}
			for(int i=0;i<cards.size();i++){
				Integer card = cards.get(i);
				Integer next = null;
				if(i+1<cards.size()){
					next = cards.get(i+1);
				}
				if(next!=null){
					if(card>=108&&card<=119){
						if(card/4==next/4){
							return true;
						}
					}	
				}
			}
		}
		if((pengCards!=null&&pengCards.size()>0)||(gangCards!=null&&gangCards.size()>0)){
			return true;
		}
		return result ;
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
	
	
	public boolean isTing(List<Integer> arrayToList, List<PengCard> pengCards,List<GangCard> gangCards,List<ChiCard> chiCards,List<XuanFengGangCard> baiFengGangCards,List<XuanFengGangCard> heiFengGangCards, OneRoom room){
		FengTianHuPai fengTianHuPai = room.getFengTianHuPai();
		int[] intArrayForChi = TypeUtils.getChiArray(chiCards);
		int[] intArrayForPeng = TypeUtils.getPengArray(pengCards);
		int[] intArrayForGangM = TypeUtils.getDianGangArray(gangCards);
		int[] intArrayForGangA = TypeUtils.getAnGangArray(gangCards);
		int[] intArrayForDNXB = TypeUtils.getHeiFengArray(heiFengGangCards);
		int[] intArrayForZFB = TypeUtils.getBaiFengArray(baiFengGangCards);
		int[] intArrayForHand = TypeUtils.getArray(getArrayByList(arrayToList));
		boolean checkTingPai = fengTianHuPai.checkTingPai(intArrayForHand, intArrayForChi, intArrayForPeng, intArrayForGangM,intArrayForGangA, intArrayForDNXB, intArrayForZFB);
		return checkTingPai;
	}
	
	/**
	 * 
	 * @param arrayToList  手牌
	 * @param pengCards  碰牌
	 * @param gangCards	杠牌
	 * @param chiCards 吃牌
	 * @param xuanFengGangCards 
	 * @param oneRoom 房间
	 * @return
	 */
	public boolean isHu(List<Integer> arrayToList, List<PengCard> pengCards,List<GangCard> gangCards,List<ChiCard> chiCards,List<XuanFengGangCard> baiFengGangCards,List<XuanFengGangCard> heiFengGangCards, OneRoom oneRoom) {
		//判断是否缺一门和 是否有幺九
		boolean huWithoutQiDui = isHuWithNotQiDui(arrayToList,pengCards,gangCards,chiCards,baiFengGangCards,heiFengGangCards,oneRoom);
		isHu = huWithoutQiDui;
		return huWithoutQiDui;
		
		

	}

	/**
	 * 判断是否可以胡牌(带吃开门的胡牌  之判断的有没有开门)
	 * @param arrayToList
	 * @param pengCards
	 * @param gangCards
	 * @param chiCards
	 * @param baiFengGangCards
	 * @param heiFengGangCards
	 * @param oneRoom 
	 * @return
	 */
	private boolean isHuWithNotQiDui(List<Integer> arrayToList,
			List<PengCard> pengCards, List<GangCard> gangCards,
			List<ChiCard> chiCards, List<XuanFengGangCard> baiFengGangCards,
			List<XuanFengGangCard> heiFengGangCards, OneRoom oneRoom) {
		FengTianHuPai fengTianHuPai = oneRoom.getFengTianHuPai();
		int[] intArrayForChi = TypeUtils.getChiArray(chiCards);
		int[] intArrayForPeng = TypeUtils.getPengArray(pengCards);
		int[] intArrayForGangM = TypeUtils.getDianGangArray(gangCards);
		int[] intArrayForGangA = TypeUtils.getAnGangArray(gangCards);
		int[] intArrayForDNXB = TypeUtils.getHeiFengArray(heiFengGangCards);
		int[] intArrayForZFB = TypeUtils.getBaiFengArray(baiFengGangCards);
		int[] intArrayForHand = TypeUtils.getArray(getArrayByList(arrayToList));
		fengTianHuPai.propertyForRoom_qingyise =  oneRoom.isQingYiSe();
		boolean checkHuPai = fengTianHuPai.checkHuPai(0, intArrayForHand, intArrayForChi, intArrayForPeng, intArrayForGangM,intArrayForGangA, intArrayForDNXB, intArrayForZFB, false, false);
		return checkHuPai;
	}
	/**
	 * 把集合转换成数组
	 * @param cards
	 * @return
	 */
	private static int[] getArrayByList(List<Integer> cards) {
		int[] card = new int[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			card[i] = cards.get(i);
		}
		return card;
	}
	/**
	 * @param arrayToList
	 * @param bdNumber
	 * @return
	 */
	public static boolean isSiHun(List<Integer> arrayToList,int bdNumber){
		if(bdNumber>135){
			return false;
		}
		int b = bdNumber/4;
		int totalHui =0;
		for(int i=0;i<arrayToList.size();i++){
			Integer card = arrayToList.get(i);
			if(card/4==b){
				totalHui++;
			}
		}
		if(totalHui>=4){
			return true;
		}
		return false;
	}
	
	
	
	/**判断是否缺一门
	 * @param cards
	 * @return
	 */
	private static boolean isQueYiMen(List<Integer> cards){
		boolean result = true;
		boolean haveWan = false;
		boolean bingWan = false;
		boolean tiaoWan = false;
		for(int i=0;i<cards.size();i++){
			int card = cards.get(i) / 4;
			if (card < 9) {//万
				haveWan = true;
			} else if (card < 18) {//筒
				bingWan = true;
			} else if (card < 27) {//条
				tiaoWan = true;
			} 
		}
		
		if(haveWan&&bingWan&&tiaoWan){
			return false;
		}
		return result;
	}
	
	
	/**判断是否缺一门
	 * @param cards
	 * @return
	 */
	public static boolean isQueYiMen(List<Integer> cards,List<PengCard> pengCards,List<GangCard> gangCards,List<ChiCard> chiCards){
		boolean result = true;
		boolean haveWan = false;
		boolean bingWan = false;
		boolean tiaoWan = false;
		for(int i=0;i<cards.size();i++){
			int card = cards.get(i) / 4;
			if (card < 9) {//万
				haveWan = true;
			} else if (card < 18) {//筒
				bingWan = true;
			} else if (card < 27) {//条
				tiaoWan = true;
			} 
		}
		if(pengCards!=null&&pengCards.size()>0){
			for(int i=0;i<pengCards.size();i++){
				PengCard pengCard = pengCards.get(i);
				List<Integer> pengs = pengCard.getCards();
				Integer peng = pengs.get(0);
				int card = peng / 4;
				if (card < 9) {//万
					haveWan = true;
				} else if (card < 18) {//筒
					bingWan = true;
				} else if (card < 27) {//条
					tiaoWan = true;
				} 
			}
		}
		if(chiCards!=null&&chiCards.size()>0){
			for (int i = 0; i < chiCards.size(); i++) {
				List<Integer> chi = chiCards.get(i).getCards();
				Integer integer = chi.get(0);
				int card = integer/4;
				if (card < 9) {//万
					haveWan = true;
				} else if (card < 18) {//筒
					bingWan = true;
				} else if (card < 27) {//条
					tiaoWan = true;
				} 
			}
		}
		if(gangCards!=null&&gangCards.size()>0){
			for(int i=0;i<gangCards.size();i++){
				GangCard gangCard = gangCards.get(i);
				List<Integer> gangs = gangCard.getCards();
				Integer gang = gangs.get(0);
				int card = gang / 4;
				if (card < 9) {//万
					haveWan = true;
				} else if (card < 18) {//筒
					bingWan = true;
				} else if (card < 27) {//条
					tiaoWan = true;
				} 
			}
		}
		
		if(haveWan&&bingWan&&tiaoWan){
			return false;
		}
		return result;
	}
	/**
	 * 是否是飘
	 * @return
	 */
	public static boolean isPiao(List<Integer> cards,List<PengCard> pengCards,List<GangCard> gangCards,List<ChiCard> chiCards){
		boolean piao = false;
		int piaoNum = 0;
		int step = 3;
		if(cards.size()>=2){
			for (int i = 0; i < cards.size(); i=i+step) {
				Integer card = cards.get(i);
				Integer next = null;
				Integer next2 = null;
				if(i+2<cards.size()){
					next = cards.get(i+1);
					next2 = cards.get(i+2);
				}
				if(next!=null&&next2!=null){
					if(card/4==next/4&&card/4==next2/4){
						piaoNum ++;
						step = 3;
					}else{
						step = 1;
					}
				}
			}
		}
		if(chiCards!=null&&chiCards.size()>0){
			return false;
		}
		if(pengCards!=null&&pengCards.size()>0){
			piaoNum = piaoNum + pengCards.size();
		}
		if(gangCards!=null&&gangCards.size()>0){
			piaoNum = piaoNum + gangCards.size();
		}
		if(piaoNum>=4){
			piao = true;
		}
		return piao;
	}
	
	/**
	 * 是否有幺九
	 * @return
	 */
	public static boolean isHaveYaoJiu(List<Integer> cards,List<PengCard> pengCards,List<GangCard> gangCards,List<ChiCard> chiCards) {
		int[] yaojiu = new int[]{0,8,9,17,18,26};
		List<Integer> arrayToList = arrayToList(yaojiu);
		boolean haveYaoJiu = false;
		for(int i=0;i<cards.size();i++){
			int card = cards.get(i) / 4;
			//判断是不是有幺九
			if(arrayToList.contains(card)){
				return true;
			}

			//中发白或者风牌可以替代幺九 
			//判断有没有中发白或者风牌
			if(card>=27){
				haveYaoJiu = true;
			}
			
		}
		if(pengCards!=null&&pengCards.size()>0){
			for (int i = 0; i < pengCards.size(); i++) {
				Integer card = pengCards.get(i).getCards().get(0)/4;
				//判断是不是有幺九
				if(arrayToList.contains(card)){
					return true;
				}
				if(card>=27){
					haveYaoJiu = true;
				}
			}
		}
		if(gangCards!=null&&gangCards.size()>0){
			for (int i = 0; i < gangCards.size(); i++) {
				Integer card = gangCards.get(i).getCards().get(0)/4;
				//判断是不是有幺九
				if(arrayToList.contains(card)){
					return true;
				}
				if(card>=27){
					haveYaoJiu = true;
				}
			}
		}
		if(chiCards!=null&&chiCards.size()>0){
			for (int i = 0; i < chiCards.size(); i++) {
				List<Integer> chi = chiCards.get(i).getCards();
				if(arrayToList.contains(chi.get(0)/4)||arrayToList.contains(chi.get(1)/4)||arrayToList.contains(chi.get(2)/4)){
					return true;
				}
			}
		}
		return haveYaoJiu;
	}
	
	
	
	//非七对胡牌
	private boolean isHuWithoutQiDui(List<Integer> arrayToList,OneRoom oneRoom) {
		if(oneRoom.isShouLouZi()){
			return isShouLouZi(arrayToList);
		}else{
			return isNotShouLouZi(arrayToList);
		}
		
	}
	/**
	 * 不是手搂子的非七对胡牌
	 * @param arrayToList
	 * @return
	 */
	private boolean isNotShouLouZi(List<Integer> arrayToList) {
		Map<Integer, List<Integer>> cardMap = getCardMap1(arrayToList);
		int wanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(1)), 0);//万
		int bingNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(2)), 0);//筒
		int tiaoNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(3)), 0);//条
		int zhongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(4)), 0);//中
		int faNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(5)), 0);//发财
		int baiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(6)), 0);//白板
		int dongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(7)), 0);//东
		int nanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(8)), 0);//南
		int xiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(9)), 0);//西
		int beiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(10)), 0);//北
		int remainsHongZhong = 0;
		int haveHongZhong = cardMap.get(0).size();
		remainsHongZhong = haveHongZhong - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		// 将在万中
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(1), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		// 将在饼中
		remainsHongZhong = haveHongZhong - wanNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(2), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		// 将在条中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(3), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		// 将在红中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(4), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		// 将在发财中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - baiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		// 将在白板中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - faNeed - zhongNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(6), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在东风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(7), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在南风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(8), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在西风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(9), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在北风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongNeed - faNeed - baiNeed - dongNeed - nanNeed - xiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(10), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		return false;
	}
	/**
	 * 是手搂子的非七对胡牌
	 * @param arrayToList
	 * @return
	 */
	private boolean isShouLouZi(List<Integer> arrayToList) {
		Map<Integer, List<Integer>> cardMap = getCardMap(arrayToList);
		int wanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(1)), 0);//万
		int bingNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(2)), 0);//筒
		int tiaoNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(3)), 0);//条
		int zhongFaBaiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(4)), 0);//中
		int dongNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(5)), 0);//东
		int nanNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(6)), 0);//南
		int xiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(7)), 0);//西
		int beiNeed = getTotalNeedNoDui(getNewListFromOldList(cardMap.get(8)), 0);//北
		int remainsHongZhong = 0;
		int haveHongZhong = cardMap.get(0).size();
		remainsHongZhong = haveHongZhong - bingNeed - tiaoNeed - zhongFaBaiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		// 将在万中
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(1), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在饼中
		remainsHongZhong = haveHongZhong - wanNeed - tiaoNeed - zhongFaBaiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(2), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		
		// 将在条中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - zhongFaBaiNeed - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(3), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

		// 将在红中发白
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed  - dongNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(4), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}

/*		// 将在东南西北中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}*/
		//将在东风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed - nanNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(5), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在南风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed - dongNeed - xiNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(6), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在西风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed - dongNeed - nanNeed - beiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(7), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		//将在北风中
		remainsHongZhong = haveHongZhong - wanNeed - bingNeed - tiaoNeed - zhongFaBaiNeed - dongNeed - nanNeed - xiNeed;
		if (remainsHongZhong >= 0) {
			boolean hu2 = isHuNoDui(cardMap.get(8), remainsHongZhong);
			if (hu2) {
				this.isHu = true;
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 将牌按照类型分开  万 筒 条 中发白 风牌   选择手搂子
	 * @return
	 */
	public static Map<Integer, List<Integer>> getCardMap(List<Integer> cards) {
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
		map.put(0, list0);
		map.put(1, list1);
		map.put(2, list2);
		map.put(3, list3);
		map.put(4, list4);
		map.put(5, list5);
		map.put(6, list6);
		map.put(7, list7);
		map.put(8, list8);
		for (int i = 0; i < cards.size(); i++) {
			int card = cards.get(i) / 4;
			if (card < 9) {//万
				map.get(1).add(card);
			} else if (card < 18) {//筒
				map.get(2).add(card);
			} else if (card < 27) {//条
				map.get(3).add(card);
			} else if (card < 30) {//中发白
				map.get(4).add(card);
			} else if (card < 31) {//东
				map.get(5).add(card);
			} else if (card < 32) {//南
				map.get(6).add(card);
			} else if (card < 33) {//西
				map.get(7).add(card);
			} else if (card < 34) {//北
				map.get(8).add(card);
			}
		}
		return map;
	}
	/**
	 * 将牌按照类型分开  万 筒 条 中发白 风牌   选择不是手搂子
	 * @return
	 */
	public static Map<Integer, List<Integer>> getCardMap1(List<Integer> cards) {
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
			if (card < 9) {//万
				map.get(1).add(card);
			} else if (card < 18) {//筒
				map.get(2).add(card);
			} else if (card < 27) {//条
				map.get(3).add(card);
			}else if (card < 28) {//中
				map.get(4).add(card);
			}else if (card < 29) {//发
				map.get(5).add(card);
			} else if (card < 30) {//白
				map.get(6).add(card);
			} else if (card < 31) {//东
				map.get(7).add(card);
			} else if (card < 32) {//南
				map.get(8).add(card);
			} else if (card < 33) {//西
				map.get(9).add(card);
			} else if (card < 34) {//北
				map.get(10).add(card);
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

	private  boolean isHuNoDui(List<Integer> cards, int haveHongZhong) {
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
	public static int getTotalNeedNoDui1(List<Integer> list, int totalNeed) {
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
			return getTotalNeedNoDui1(list, totalNeed);
		}
		return 9;
	}

	/**
	 * 
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
				if (Math.abs(next - now) >= 1 && Math.abs(nnext - next) >=1) {
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
			if (now == next || Math.abs(next - 1 )>= now) {// 必须有顺子
				Integer nextNumber = null;
				Integer nnextNumber = null;
				for (int i = 1; i < list.size(); i++) {
					Integer n = list.get(i);
					if (nextNumber == null && Math.abs(n - now) >= 1) {
						nextNumber = n;
						continue;
					}
					if (nnextNumber == null && nextNumber!=null && Math.abs(n - nextNumber) >= 1) {
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
		
/*		if (now == next || next - 1 == now) {// 必须有顺子
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
		}*/
		
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
	public static <T> List<T> getNewTypeListFromOldList(List<T> cards) {
		List<T> arrayList = new ArrayList<>();
		for (T t : cards) {
			arrayList.add(t);
		}
		return arrayList;
	}

}
