package com.zxz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;


public class Test2 {

	public static void main(String[] args) {

/*		int[] count = new int[27];//TDW排列
		int[] tmp = new int[27];//TDW排列
		List<String> huPai = new ArrayList<String>();
		for(int i=0;i<27;i++){
			count[i]=0;
		}
		Scanner cin = new Scanner(System.in);
		String line = cin.nextLine();
		int len = line.length();
		for(int i=0;i<len;i += 2){
			if(line.charAt(i+1)=='T'){
				int num = Integer.parseInt(line.charAt(i)+"")-1;
				count[num]++;
			}else if(line.charAt(i+1)=='D'){
				int num = Integer.parseInt(line.charAt(i)+"")-1;
				count[num+9]++;
			}else if(line.charAt(i+1)=='W'){
				int num = Integer.parseInt(line.charAt(i)+"")-1;
				count[num+9*2]++;
			}
		}
		
		for(int i=0;i<27;i++){
			count[i]++;
			for(int j=0;j<27;j++)
				tmp[j] = count[j];
			boolean flag = isHU(tmp);
			if(flag)
				huPai.add(translate(i));
			count[i]--;
		}
		System.out.println(huPai.size());
		for(String x:huPai)
			System.out.print(x);*/
	/*	int intArrayForCard[] = {0,53,54,55,96,100,104,108,112,116,120,124,130,135};
		boolean tryHU = tryHU(intArrayForCard, 14);
		System.out.println(tryHU);*/
/*		List<Object> newList = new ArrayList<>();
		newList.add(9);
		List<Object> arrayList = new ArrayList<>();
		arrayList.add(0);
		arrayList.add(0);
		arrayList.add(0);
		arrayList.add(0);
		arrayList.add(0);
		newList = arrayList;
		System.out.println(newList);
		newList.add("aaaaa");
		System.out.println(arrayList);*/
		List<Integer> chiList = new ArrayList<>();
		chiList.add(36);
		chiList.add(40);
		chiList.add(50);
		chiList.add(53);
		
		int card = 45;
		JSONArray jsonArray = new JSONArray();
		int len = chiList.size()/2;
		for (int i = 0; i < len; i++) {
			List<Integer> arrayList = new ArrayList<>();
			for (int ii = 0; ii < 2; ii++) {
				arrayList.add(chiList.get(ii)); 
			}
			arrayList.add(card);
			jsonArray.put(arrayList);
			chiList.removeAll(arrayList);
		}
		System.out.println(jsonArray);
	}

	private static String translate(int i) {
		int n = i/9;
		int pre = i%9+1;
		String r = null;
		switch (n) {
		case 0:
			r = pre + "T";
			break;
		case 1:
			r = pre + "D";
			break;
		case 2:
			r = pre + "W";
			break;
		default:
			break;
		}
		return r;
	}

	private static boolean isHU(int[] count) {
		// TODO Auto-generated method stub
		boolean result = tryHU(count, 14);
		return result;
	}

	private static boolean tryHU(int[] count, int len) {
		if(len==0)
			return true;
		if(len%3==2){//说明对牌没出现
			for(int i=0;i<27;i++){
				if(count[i]>=2){
					count[i] -= 2;
					if(tryHU(count, len - 2))
						return true;
					count[i] += 2;
				}
			}
		}
		else{
			//三个一样的
			for(int i=0;i<27;i++){
				if(count[i]>=3){
					count[i] -= 3;
					if(tryHU(count, len - 3))
						return true;
					count[i] += 3;
				}
			}
			//是否是顺子
			for(int i=0;i<27-2;i++){
				if(count[i]>0&&count[i+1]>0&&count[i+2]>0){
					count[i] -= 1;
					count[i+1] -= 1;
					count[i+2] -= 1;
					if(tryHU(count, len - 3))
						return true;
					count[i] += 1;
					count[i+1] += 1;
					count[i+2] += 1;
				}
			}
		}
		return false;
	}

}

