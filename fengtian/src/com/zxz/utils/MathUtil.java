package com.zxz.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.zxz.algorithm.InitialPuKe;

public class MathUtil {


	/**
	 * 鍚戜竴涓暟缁勪腑锛屾坊鍔犱竴涓暟锛屽苟涓斾娇瀹冧粛鐒舵湁搴�
	 * 
	 * @param array
	 * @param number
	 * @return
	 */
	public static int[] insertSortArray(int array[], int number) {
		int begin = 0;
		int end = array.length;
		boolean isEnd = false;
		int index = -1;
		int[] copyArray = new int[array.length + 1];
		if (number >= array[array.length - 1]) {
			index = array.length;
		} else if (number <= array[0]) {
			index = 0;
		} else {
			while (!isEnd) {
				int middle = (begin + end) / 2;
				int temp = array[middle];
				if (temp == number) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle] && number <= array[middle + 1]) {
					index = middle + 1;
					isEnd = true;
				} else if (number < array[middle] && number >= array[middle - 1]) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle]) {
					begin = middle;
				} else if (number < array[middle]) {
					end = middle;
				}
				if (begin >= end) {
					isEnd = true;
				}
			}
		}

		if (index == array.length) {
			for (int i = 0; i < array.length; i++) {
				copyArray[i] = array[i];
			}
			copyArray[array.length] = number;
		} else if (index == 0) {
			copyArray[0] = number;
			for (int i = 0; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		} else {
			for (int i = 0; i <= index - 1; i++) {
				copyArray[i] = array[i];
			}

			copyArray[index] = number;
			copyArray[index + 1] = array[index];
			for (int i = index + 1; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		}

		return copyArray;
	}

	/**
	 * 鍚戜竴涓暟缁勪腑锛屾坊鍔犱竴涓暟锛屽苟涓斾娇瀹冧粛鐒舵湁搴�
	 * 
	 * @param array
	 * @param number
	 * @return
	 */
	public static int[] removeSortArray(int array[], int number) {
		int begin = 0;
		int end = array.length;
		boolean isEnd = false;
		int index = -1;
		int[] copyArray = new int[array.length + 1];
		if (number >= array[array.length - 1]) {
			index = array.length;
		} else if (number <= array[0]) {
			index = 0;
		} else {
			while (!isEnd) {
				int middle = (begin + end) / 2;
				int temp = array[middle];
				if (temp == number) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle] && number <= array[middle + 1]) {
					index = middle + 1;
					isEnd = true;
				} else if (number < array[middle] && number >= array[middle - 1]) {
					index = middle;
					isEnd = true;
				} else if (number > array[middle]) {
					begin = middle;
				} else if (number < array[middle]) {
					end = middle;
				}
				if (begin >= end) {
					isEnd = true;
				}
			}
		}

		if (index == array.length) {
			for (int i = 0; i < array.length; i++) {
				copyArray[i] = array[i];
			}
			copyArray[array.length] = number;
		} else if (index == 0) {
			copyArray[0] = number;
			for (int i = 0; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		} else {
			for (int i = 0; i <= index - 1; i++) {
				copyArray[i] = array[i];
			}

			copyArray[index] = number;
			copyArray[index + 1] = array[index];
			for (int i = index + 1; i < array.length; i++) {
				copyArray[i + 1] = array[i];
			}
		}

		return copyArray;
	}

	public static int binarySearch(int des, int[] srcArray) {
		// 绗竴涓綅缃�.
		int low = 0;
		// 鏈�楂樹綅缃�.鏁扮粍闀垮害-1,鍥犱负涓嬫爣鏄粠0寮�濮嬬殑.
		int high = srcArray.length - 1;
		// 褰搇ow"鎸囬拡"鍜宧igh涓嶉噸澶嶇殑鏃跺��.
		while (low <= high) {
			// 涓棿浣嶇疆璁＄畻,low+ 鏈�楂樹綅缃噺鍘绘渶浣庝綅缃�,鍙崇Щ涓�浣�,鐩稿綋浜庨櫎2.涔熷彲浠ョ敤(high+low)/2
			int middle = low + ((high - low) >> 1);
			// 涓庢渶涓棿鐨勬暟瀛楄繘琛屽垽鏂�,鏄惁鐩哥瓑,鐩哥瓑鐨勮瘽灏辫繑鍥炲搴旂殑鏁扮粍涓嬫爣.
			if (des == srcArray[middle]) {
				return middle;
				// 濡傛灉灏忎簬鐨勮瘽鍒欑Щ鍔ㄦ渶楂樺眰鐨�"鎸囬拡"
			} else if (des < srcArray[middle]) {
				high = middle - 1;
				// 绉诲姩鏈�浣庣殑"鎸囬拡"
			} else {
				low = middle + 1;
			}
		}
		return -1;
	}

	public static int binarySearch(int des, List<Integer> list) {
		Integer[] srcArray = list.toArray(new Integer[list.size()]);
		// 绗竴涓綅缃�.
		int low = 0;
		// 鏈�楂樹綅缃�.鏁扮粍闀垮害-1,鍥犱负涓嬫爣鏄粠0寮�濮嬬殑.
		int high = srcArray.length - 1;
		// 褰搇ow"鎸囬拡"鍜宧igh涓嶉噸澶嶇殑鏃跺��.
		while (low <= high) {
			// 涓棿浣嶇疆璁＄畻,low+ 鏈�楂樹綅缃噺鍘绘渶浣庝綅缃�,鍙崇Щ涓�浣�,鐩稿綋浜庨櫎2.涔熷彲浠ョ敤(high+low)/2
			int middle = low + ((high - low) >> 1);
			// 涓庢渶涓棿鐨勬暟瀛楄繘琛屽垽鏂�,鏄惁鐩哥瓑,鐩哥瓑鐨勮瘽灏辫繑鍥炲搴旂殑鏁扮粍涓嬫爣.
			if (des == srcArray[middle]) {
				return middle;
				// 濡傛灉灏忎簬鐨勮瘽鍒欑Щ鍔ㄦ渶楂樺眰鐨�"鎸囬拡"
			} else if (des < srcArray[middle]) {
				high = middle - 1;
				// 绉诲姩鏈�浣庣殑"鎸囬拡"
			} else {
				low = middle + 1;
			}
		}
		return -1;
	}

	/**
	 * 鐢熸垚鎸囧畾鑼冨洿鐨勬暟
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<Integer> creatRandom(int begin, int end) {
		List<Integer> list = new LinkedList<Integer>();
		for (int i = begin; i <= end; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}

	/**
	 * 瀹氫笢椋� 灞�锛氫竴灏�4锛堜笢鍗楀洓鍖楋級鍦堬紝鐗屽眬寮�濮嬫幏楠板瓙瀹氫笢椋庯紝5銆�9鑷凡涓轰笢锛�2銆�6銆�10涓嬪涓轰笢锛�3銆�7銆�11瀵瑰涓轰笢锛�4銆�8銆�12涓婂涓轰笢銆�
	 * 
	 * @return
	 */
	public static int getDongFeng() {
		int result = (int) (Math.random() * 11 + 2);
		return result;
	}

	/**
	 * 寰楀埌鐧炬惌鐨勯偅寮犵墝
	 * 
	 * @return
	 */
	public static int getBaiDa() {
		int result = (int) (Math.random() * 107);
		return result;
	}

	
	public static void main(String[] args) {
		for(int i=0;i<=135;i++){
			String cardType = InitialPuKe.getCardType(i);
			int nextCard = getNextCard(i);
			String nextCardType = InitialPuKe.getCardType(nextCard);
			System.out.println(cardType+" "+nextCardType);
		}
	}
	
	
	/**
	 * 寰楀埌璇ュ紶鐗岀殑涓嬩竴寮犵墝
	 * 
	 * @param card
	 * @return
	 */
	public static int getNextCard(int card) {
		int cardNumber = card / 4;
		int baiDaNumber = 0;
		if (cardNumber < 9) {// 涓�
			if (cardNumber == 8) {
				baiDaNumber = 0 * 4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 18) {// 绛�
			if (cardNumber == 17) {
				baiDaNumber = 9*4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 27) {// 鏉�
			if (cardNumber == 26) {
				baiDaNumber = 18*4;
			} else {
				baiDaNumber = (cardNumber + 1) * 4;
			}
		} else if (cardNumber < 28) {// 涓�
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 29) {// 鍙�
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 30) {// 鐧�
			baiDaNumber = 27 * 4;
		} else if (cardNumber < 31) {// 涓�
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 32) {// 鍗�
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 33) {// 瑗�
			baiDaNumber = (cardNumber + 1) * 4;
		} else if (cardNumber < 34) {// 鍖�
			baiDaNumber = 30 * 4;
		}
		return baiDaNumber;
	}

}
