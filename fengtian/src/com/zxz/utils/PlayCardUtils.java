package com.zxz.utils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.service.BaseService;
import com.zxz.service.HuiFangUitl;
import com.zxz.service.PlayGameService;

public class PlayCardUtils {
	
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlayCardUtils.class); 

	PlayGameService playGameService = new PlayGameService();
	public static void main(String[] args) {
//		test1();  
		Map<String, Integer> seatMap = new HashMap<String, Integer>();
		Integer integer = seatMap.get("east");
		if (integer==null) {
			System.out.println(1);
		}
		System.out.println(integer);
	}
	private static void test1() {
		long temp = 1;  
        long sum = 0;  
        for (int i = 1; i <= 20; i++)  
        {  
            temp = temp * i;  
            sum += temp;  
        }  
        System.out.println("sum=" + sum);
	}
	/**
	 * 如果用户可以胡牌  判断还阔以进行什么操作提示
	 * @param game
	 * @param isGang
	 * @param remainCards
	 * @param removeCard
	 * @param user
	 * @param total
	 * @return
	 */
	private static int userWinPlay(Game game, boolean isGang,
			List<Integer> remainCards, Integer removeCard, User user, int total) {
		total ++;
		user.setCanWin(true);
		if(remainCards.size()<=3){//海底捞月
			user.setCanHai(true);
		}
		List<Integer> gongGangCards = PlayGameService.analysisUserIsCanGongGang(removeCard, user);//用户是否可以公杠
		List<List<Integer>> xuanFengGang = PlayGameService.analysisUserIsCanXuanFengGang(user);//用户是否可以旋风杠
		game.setCanHuUser(user);
		user.setCanWin(true);
		if(xuanFengGang.size()>0){
			//可以旋风杠
			game.setCanXuanFengGangUser(user);
			for (List<Integer> xuan : xuanFengGang) {
				if(xuan.size()==3){
					user.setCanBaiFeng(true);
					game.setXuanFengBai(true);
				}else if(xuan.size()==4){
					user.setCanHeiFeng(true);
					game.setXuanFengHei(true);
				}
			}
		}	
		if (gongGangCards.size() > 0) {// 如果可以公杠
			user.setCanGang(true);
			game.setIsCanGangType(0);
			game.setGongGangCardId(removeCard);

		} else {
			List<Integer> anGangCards = PlayGameService.isUserCanAnGang(user);
			if (anGangCards.size() > 0) {// 可以暗杠
				user.setCanAnGang(true);
				game.setIsCanGangType(1);
				game.setAnGangCards(anGangCards);
				game.setCanAnGang(true);
			}
		}
		return total;
	}
	/**用户没有赢牌  可以进行的操作提示
	 * @param game
	 * @param remainCards  游戏剩余牌
	 * @param removeCard   抓的牌
	 * @param user
	 * @param total 
	 */
	public static int userNotWin(Game game, List<Integer> remainCards, User user, int total) {
		user.setCanWin(false);
		user.setUserCanPlay(true);//该用户可以打牌
		//分析用户可不可以旋风杠   //中发白 OR 东南西北
		//旋风杠也可以明杠 也可以暗杠
		List<List<Integer>> xuanFengGang = PlayGameService.analysisUserIsCanXuanFengGang(user);
		boolean checkUserIsCanTing = PlayGameService.checkUserIsCanTing(user, game);
		if(checkUserIsCanTing){
			user.setCanTing(true);
			game.setCanTingUser(user);
			total++;
		}
		if(xuanFengGang.size()>0){
			//可以旋风杠
			game.setCanXuanFengGangUser(user);
			for (List<Integer> xuan : xuanFengGang) {
				if(xuan.size()==3){
					user.setCanBaiFeng(true);
					game.setXuanFengBai(true);
				}else if(xuan.size()==4){
					user.setCanHeiFeng(true);
					game.setXuanFengHei(true);
				}
			}
			total++;
		}
		
//		List<Integer> gongGangCards = PlayGameService.analysisUserIsCanGongGang(removeCard,user);
		Integer pengGang = PlayGameService.analysisUserIsCanPengGang(user);
		if(pengGang!=-1){//可以公杠
			user.setCanGang(true);
			game.setGongGangCardId(pengGang);
			game.setCanGongGangUser(user);
			total++;
		}else{
			//分析该用户是否可以暗杠
			List<Integer> anGangCards = PlayGameService.isUserCanAnGang(user);
			if(anGangCards.size()>0){//设置出牌的状态为暗杠
				user.setCanAnGang(true);
				game.setAnGangCards(anGangCards);
				game.setCanAnGangUser(user);
				game.setCanAnGang(true);
				total++;
			}
		}
		
		return total;
		
	}



}
