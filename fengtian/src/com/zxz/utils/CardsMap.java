package com.zxz.utils;

import java.util.HashMap;
import java.util.Map;

public class CardsMap {
	
	public static Map<Integer, String> map;
	
	static{
		map = new HashMap<Integer, String>();
		putOne(map);
		putTong(map);
		putTiao(map);
		putHongZhong(map);
	}

	public Map<Integer, String> getPaiDictionary(){
		return map;
	}
	
	public static String getCardType(int cardId){
		return map.get(cardId);
	}
	
	
	private static void putHongZhong(Map<Integer, String> map) {
		map.put(108,"绾腑");
		map.put(109,"绾腑");
		map.put(110,"绾腑");
		map.put(111,"绾腑");
	}

	private static void putTong(Map<Integer, String> map) {
		map.put(36,"1绛�");
		map.put(37,"1绛�");
		map.put(38,"1绛�");
		map.put(39,"1绛�");
		map.put(40,"2绛�");
		map.put(41,"2绛�");
		map.put(42,"2绛�");
		map.put(43,"2绛�");
		map.put(44,"3绛�");
		map.put(45,"3绛�");
		map.put(46,"3绛�");
		map.put(47,"3绛�");
		map.put(48,"4绛�");
		map.put(49,"4绛�");
		map.put(50,"4绛�");
		map.put(51,"4绛�");
		map.put(52,"5绛�");
		map.put(53,"5绛�");
		map.put(54,"5绛�");
		map.put(55,"5绛�");
		map.put(56,"6绛�");
		map.put(57,"6绛�");
		map.put(58,"6绛�");
		map.put(59,"6绛�");
		map.put(60,"7绛�");
		map.put(61,"7绛�");
		map.put(62,"7绛�");
		map.put(63,"7绛�");
		map.put(64,"8绛�");
		map.put(65,"8绛�");
		map.put(66,"8绛�");
		map.put(67,"8绛�");
		map.put(68,"9绛�");
		map.put(69,"9绛�");
		map.put(70,"9绛�");
		map.put(71,"9绛�");
	}

	private static void putTiao(Map<Integer, String> map) {
		map.put(72,"1鏉�");
		map.put(73,"1鏉�");
		map.put(74,"1鏉�");
		map.put(75,"1鏉�");
		map.put(76,"2鏉�");
		map.put(77,"2鏉�");
		map.put(78,"2鏉�");
		map.put(79,"2鏉�");
		map.put(80,"3鏉�");
		map.put(81,"3鏉�");
		map.put(82,"3鏉�");
		map.put(83,"3鏉�");
		map.put(84,"4鏉�");
		map.put(85,"4鏉�");
		map.put(86,"4鏉�");
		map.put(87,"4鏉�");
		map.put(88,"5鏉�");
		map.put(89,"5鏉�");
		map.put(90,"5鏉�");
		map.put(91,"5鏉�");
		map.put(92,"6鏉�");
		map.put(93,"6鏉�");
		map.put(94,"6鏉�");
		map.put(95,"6鏉�");
		map.put(96,"7鏉�");
		map.put(97,"7鏉�");
		map.put(98,"7鏉�");
		map.put(99,"7鏉�");
		map.put(100,"8鏉�");
		map.put(101,"8鏉�");
		map.put(102,"8鏉�");
		map.put(103,"8鏉�");
		map.put(104,"9鏉�");
		map.put(105,"9鏉�");
		map.put(106,"9鏉�");
		map.put(107,"9鏉�");
	}

	private static void putOne(Map<Integer, String> map) {
		map.put(0,"1涓�");
		map.put(1,"1涓�");
		map.put(2,"1涓�");
		map.put(3,"1涓�");
		map.put(4,"2涓�");
		map.put(5,"2涓�");
		map.put(6,"2涓�");
		map.put(7,"2涓�");
		map.put(8,"3涓�");
		map.put(9,"3涓�");
		map.put(10,"3涓�");
		map.put(11,"3涓�");
		map.put(12,"4涓�");
		map.put(13,"4涓�");
		map.put(14,"4涓�");
		map.put(15,"4涓�");
		map.put(16,"5涓�");
		map.put(17,"5涓�");
		map.put(18,"5涓�");
		map.put(19,"5涓�");
		map.put(20,"6涓�");
		map.put(21,"6涓�");
		map.put(22,"6涓�");
		map.put(23,"6涓�");
		map.put(24,"7涓�");
		map.put(25,"7涓�");
		map.put(26,"7涓�");
		map.put(27,"7涓�");
		map.put(28,"8涓�");
		map.put(29,"8涓�");
		map.put(30,"8涓�");
		map.put(31,"8涓�");
		map.put(32,"9涓�");
		map.put(33,"9涓�");
		map.put(34,"9涓�");
		map.put(35,"9涓�");
	}
	
	public static void main(String[] args) {
//		int j=1;
//		for(int i=72;i<108;i++){
//			if(j==10){
//				j=1;
//			}
//			System.out.println("map.put("+i+","+"\""+(j)+"鏉");");
//			j++;
//		}
		
//		int cards[] = {105,48,88,35};
//		int cards[] = {74,16,79,87,22};
		int cards[] = {39,106,60};
		for(int i=0;i<cards.length;i++){
			int c = cards[i];
			String cardType = getCardType(c);
			System.out.println(c+":"+cardType);
		}
	}
}
