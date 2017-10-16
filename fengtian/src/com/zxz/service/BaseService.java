package com.zxz.service;

import java.util.Map;

import com.zxz.controller.GameManager;
import com.zxz.domain.Game;
import com.zxz.domain.User;

public class BaseService {

	/**得到游戏
	 * @param user
	 * @return
	 */
	public static Game getGame(User user) {
		Map<String, Game> gameMap = GameManager.getGameMap();
		String roomId = user.getRoomId();
		Game game = gameMap.get(roomId);
		return game;
	}
	
	
	/**得到游戏中的玩家
	 * @param userId
	 * @param roomId
	 */
	public static User getGamingUser(Game game,String direction){
		User user = game.getSeatMap().get(direction);
		return user;
	}
	
}
