package com.zxz.service;

import java.util.List;

import com.zxz.domain.User;

public class HuiFangUitl {

	
	public static void getBase(StringBuffer sb,User u,boolean isEnd){
		String direction = getDirection(u);
		if(isEnd){
			sb.append(direction+","+u.getHeadimgurl()+","+u.getNickName()+","+Integer.parseInt(u.getSex())+","+u.getId()+"|");
		}else{
			sb.append(direction+","+u.getHeadimgurl()+","+u.getNickName()+","+Integer.parseInt(u.getSex())+","+u.getId()+";");
		}
	}
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getFaPai(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_fa:"+cards+"|");
	}
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getAnGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_gang_a:"+cards+"|");
	}
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getChuPai(StringBuffer sb,User u,Integer cardId){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_chu:"+cardId+"|");
	}
	
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getHuPai(StringBuffer sb,User u,List<Integer> cardIds,List<Integer> remainCards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_hu:"+cardIds+"|r:rIds:"+remainCards+"|");
	}
	/**
	 * 听牌
	 * @param sb
	 * @param u
	 * @param cardIds
	 * @param remainCards
	 */
	public static void getTingPai(StringBuffer sb,User u,List<Integer> cardIds){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_ting:"+cardIds+"|");
	}
	
	

	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param chuDir 
	 * @param cardId
	 */
	public static void getPengPai(StringBuffer sb,User u,List<Integer> cards, String chudir){
		String direction = getDirection(u);
		String chuDir = getDirection(chudir);
		sb.append(direction+","+chuDir+":"+"pai_peng:"+cards+"|");
	}
	/**记录吃牌
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getChiPai(StringBuffer sb,User u,List<Integer> cards, String chudir){
		String direction = getDirection(u);
		String chuDir = getDirection(chudir);
		sb.append(direction+","+chuDir+":"+"pai_chi:"+cards+"|");
	}
	
	
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getGangPai(StringBuffer sb,User u,List<Integer> cards, String chudir){
		String direction = getDirection(u);
		String chuDir = getDirection(chudir);
		sb.append(direction+","+chuDir+":"+"pai_gang_d:"+cards+"|");
	}
	
	/**锟矫碉拷锟斤拷锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getGongGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_gang_m:"+cards+"|");
	}
	/**
	 * 记录旋风杠
	 * @param sb
	 * @param u
	 * @param cards
	 */
	public static void getHeiFengGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_liang_h:"+cards+"|");
	}
	/**
	 * 记录旋风杠
	 * @param sb
	 * @param u
	 * @param cards
	 */
	public static void getBaiFengGang(StringBuffer sb,User u,List<Integer> cards){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_liang_b:"+cards+"|");
	}
	
	
	/**锟矫碉拷抓锟斤拷
	 * @param sb
	 * @param u
	 * @param cardId
	 */
	public static void getZhuaPai(StringBuffer sb,User u,Integer cardId){
		String direction = getDirection(u);
		sb.append(direction+":"+"pai_zhua:"+cardId+"|");
	}
	
	
	/**
	 * 锟矫碉拷锟斤拷锟斤拷
	 * 
	 * @param u
	 * @return
	 */
	public static String getDirection(User u) {
		switch (u.getDirection()) {
		case "east":
			return "e";
		case "west":
			return "w";
		case "north":
			return "n";
		case "south":
			return "s";
		}
		return "";
	}
	/**
	 * 锟矫碉拷锟斤拷锟斤拷
	 * 
	 * @param u
	 * @return
	 */
	public static String getDirection(String dir) {
		switch (dir) {
		case "east":
			return "e";
		case "west":
			return "w";
		case "north":
			return "n";
		case "south":
			return "s";
		}
		return "";
	}

}
