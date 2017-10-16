package com.zxz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.dao.Location;
import com.zxz.dao.SumScoreDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.domain.Game;
import com.zxz.domain.SumScore;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.utils.Constant;
import com.zxz.utils.DateUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ScoreService extends BaseService implements Constant{
	SumScoreDao sumScoreDao = SumScoreDao.getInstance();
	UserScoreDao userScoreDao = UserScoreDao.getInstance();//单局结算成绩
	
	
	/**得到用户的战绩
	 * @param jsonObject
	 * @param session
	 */
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext session) {
		JSONObject outJsonObject = getEveryScoreJsonObject(jsonObject, session);
		session.write(outJsonObject.toString());
	}
	
	/**根据页码数得到每一局游戏jsonObject 
	 * @param jsonObject
	 * @param ctx
	 * @return
	 */
	private JSONObject getEveryScoreJsonObject(JSONObject jsonObject, ChannelHandlerContext ctx) {
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		int index = jsonObject.getInt("index");
		int pageSzie = 20;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", user.getId());
		map.put("pageIndex", (index-1)*pageSzie);
		map.put("pageSize", pageSzie);
		List<UserScore> userScoreList = userScoreDao.findUserScore(map);
		JSONObject outJsonObject = new JSONObject();
		JSONArray scoreArray = new JSONArray();
		for(int i=0;i<userScoreList.size();i++){
			UserScore userScore = userScoreList.get(i);
			int roomid = userScore.getRoomid();
			int currentGame = userScore.getCurrentGame();
			int score = userScore.getScore();
			String createDate = DateUtils.getFormatDate(userScore.getCreateDate(), "yyyy/MM/dd hh:mm:ss");
			JSONObject everyScore = new JSONObject();
			everyScore.put("roomid", roomid);
			everyScore.put("currentGame", currentGame);
			everyScore.put("score", score);
			everyScore.put("createDate", createDate);
			scoreArray.put(everyScore);
		}
		outJsonObject.put("userScores", scoreArray);
		outJsonObject.put(method, "getUserScore");
		outJsonObject.put(discription, "得到单局用户的战绩");
		outJsonObject.put("type", "everyScore");
		return outJsonObject;
	}


	/**得到用户最终的成绩(总结算)
	 * @param jsonObject
	 * @param session
	 */
	private void getUserFinalScore(JSONObject jsonObject, ChannelHandlerContext session) {
		//User user = (User) session.getAttribute("user");
		int index = jsonObject.getInt("index");
		int pageSzie = 10;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", 19);
		map.put("pageIndex", (index-1)*pageSzie);
		map.put("pageSize", pageSzie);
		List<SumScore> sumScoreList = sumScoreDao.findSumScore(map);
		JSONObject outJSONObject = new JSONObject();
		JSONArray scoreArray = new JSONArray();
		for(int i=0;i<sumScoreList.size();i++){
			SumScore sumScore = sumScoreList.get(i);
			JSONObject score = new JSONObject();
			score.put("zhongMa", sumScore.getZhongMaTotal());
			score.put("roomNumber", sumScore.getRoomNumber());
			score.put("huPaiTotal", sumScore.getHuPaiTotal());
			score.put("jieGangTotal", sumScore.getJieGangTotal());
			score.put("anGangTotal", sumScore.getAnGangTotal());
			score.put("zhongMaTotal", sumScore.getZhongMaTotal());
			score.put("finalScore", sumScore.getFinalScore());
			score.put("fangGangTotal", sumScore.getFangGangTotal());
			score.put("mingGangtotal", sumScore.getMingGangtotal());
			score.put("createDate", DateUtils.getFormatDate(sumScore.getCreateDate(), "yyyy/MM/dd hh:mm:ss"));
			scoreArray.put(score);
		}
		outJSONObject.put("userScores", scoreArray);
		outJSONObject.put(method, "getUserScore");
		outJSONObject.put(discription, "得到用户的战绩");
		System.out.println(outJSONObject);
	}
	
	
	public static void main(String[] args) {
		
	}

	public void userLocation(JSONObject jsonObject, ChannelHandlerContext ctx) {
		
	}



	
	
}
