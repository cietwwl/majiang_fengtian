package com.demo;

import java.util.ArrayList;
import java.util.List;

public class Demo4 {
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		list.add(0);
		list.add(4);
		list.add(6);
		String a = "cCard:"+list+"|";
		System.out.println(a);
	}
}
