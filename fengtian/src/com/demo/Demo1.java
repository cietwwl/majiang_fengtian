package com.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Demo1 {
	public static void main(String[] args) {
//		test1();
//		jsonArrayTest();
		JSONObject jsonObject = new JSONObject();
//		JSONArray jsonArray = new JSONArray();
		List<Integer> arrayList = new ArrayList<>();
		arrayList.add(12);
		arrayList.add(14);
		arrayList.add(18);
//		jsonArray.put(arrayList);
		jsonObject.put("array", arrayList);
		System.out.println(jsonObject);
		
	}

	private static void jsonArrayTest() {
		JSONArray jsonArray = new JSONArray();
		List<Integer> liang = new ArrayList<>();
		liang.add(121);
		liang.add(129);
		liang.add(111);
		jsonArray.put(liang);
		jsonArray.put(liang);
		System.out.println(jsonArray);
	}

	private static void test1() {
		Map<Integer,String> hashMap = new HashMap<>();
		hashMap.put(1, "east");
		hashMap.put(2, "east");
		hashMap.put(3, "north");
		Iterator<Integer> iterator = hashMap.keySet().iterator();
		List<Integer> list = new ArrayList<>();
		while (iterator.hasNext()) {
			Integer integer = iterator.next();
			if(hashMap.get(integer).equals("east")){
				list.add(integer);
			}
		}
		for (Integer integer : list) {
			hashMap.remove(integer);
		}
		System.out.println(hashMap);
	}
}
