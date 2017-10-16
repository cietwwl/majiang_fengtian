package com.zxz.utils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Date;
import java.util.List;

import com.zxz.dao.SumScoreDao;
import com.zxz.dao.UserScoreDao;
import com.zxz.dao.VedioDao;
import com.zxz.domain.Game;
import com.zxz.domain.SumScore;
import com.zxz.domain.User;
import com.zxz.domain.UserScore;
import com.zxz.domain.Vedio;
import com.zxz.service.PlayGameService;

public class RecordScoreThread implements Runnable{

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(RecordScoreThread.class); 
	private Game game;
	private String huiFang;
	static UserScoreDao userScoreDao = UserScoreDao.getInstance();
	
	public RecordScoreThread(Game game, String huiFang) {
		this.game = game;
		this.huiFang = huiFang;
	}
	
	@Override
	public void run() {
		List<User> userList = game.getUserList();
		int roomid = game.getRoom().getId();//房间号
		Date createDate = new Date();
		VedioDao vedioDao = new VedioDao();
		Vedio vedio = new Vedio();
		String string = huiFang;
		vedio.setRecord(string);
		vedioDao.saveVedio(vedio);
		int currentGame = game.getAlreadyTotalGame();//当前的局数
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			int score = user.getXuChangScore().getScore();
			int userid = user.getId();//用户的ID
			if(currentGame==1){//如果是第一局
				SumScoreDao sumScoreDao = new SumScoreDao();
				SumScore sumScore = new SumScore();
				sumScore.setRoomNumber(roomid+"");
				sumScore.setUserid(userid);
				sumScore.setHuPaiTotal(0);//胡牌次数
				sumScore.setJieGangTotal(0); //接杠次数
				sumScore.setAnGangTotal(0);//暗杠次数
				sumScore.setFinalScore(0);//最终成绩
				sumScore.setFangGangTotal(0);//放杠次数
				sumScore.setMingGangtotal(0);//明杠也称公杠次数
				sumScore.setCreateDate(createDate);
				sumScore.setTotal(game.getRoom().getTotal());
				sumScore.setFengDingNum(game.getRoom().getFengDingNum());
				sumScore.setNickName(user.getNickName());
				sumScore.setFangZhu(game.getRoom().getCreateUserId());
				int saveSumScore = sumScoreDao.saveSumScore(sumScore);//保存用户的房间号
				user.setSumScoreId(sumScore.getId());
			}
			UserScore userScore = new UserScore(userid, roomid,currentGame,score,createDate,vedio.getId(),user.getSumScoreId());
			userScoreDao.saveUserScore(userScore);
		}
	}

}
