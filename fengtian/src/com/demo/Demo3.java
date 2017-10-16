package com.demo;

import java.util.ArrayList;
import java.util.List;

import com.testhupai.HuPai;

public class Demo3 {
	public static void main(String[] args) {
		int[] a = new int[]{2,19,22,25,31,59,66,87,107,108,111,115,122,131};
		HuPai huPai = new HuPai();
		List<Integer> arrayToList = huPai.arrayToList(a);
//		System.out.println(arrayToList);
		int chuPai = chuPai(2,arrayToList);
		System.out.println(chuPai);
	}
	public static int chuPai(Integer cardId,List<Integer> arrayToList) {
		int indexOf = arrayToList.indexOf(cardId);
		Integer remove = arrayToList.remove(indexOf);
		System.out.println(remove+"出牌");
		if(remove==cardId){
			return cardId;
		}
		return -1;
	}
}
