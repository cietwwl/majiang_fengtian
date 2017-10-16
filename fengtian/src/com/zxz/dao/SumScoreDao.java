package com.zxz.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zxz.domain.SumScore;

public class SumScoreDao extends BaseDao<SumScore>{
	
	static SumScoreDao sumScoreDao;
	static int id=0;
	
	public SumScoreDao() {
	}
	
	public static SumScoreDao getInstance(){
		if(sumScoreDao!=null){
			return sumScoreDao;
		}else{
			synchronized (SumScoreDao.class) {
				sumScoreDao = new SumScoreDao();
				return sumScoreDao;
			}
		}
	}
	
	public static void main(String[] args) {
		SumScoreDao sumScoreDao = new SumScoreDao();
		SumScore sumScore = new SumScore();
		sumScore.setRoomNumber(100010+"");
		sumScore.setUserid(10024);
		sumScore.setHuPaiTotal(0);//胡牌次数
		sumScore.setJieGangTotal(0); //接杠次数
		sumScore.setAnGangTotal(0);//暗杠次数
		sumScore.setFinalScore(0);//最终成绩
		sumScore.setFangGangTotal(0);//放杠次数
		sumScore.setMingGangtotal(0);//明杠也称公杠次数
		sumScore.setCreateDate(new Date());
		sumScore.setTotal(9);
		sumScore.setFengDingNum(32);
		sumScore.setFangZhu(10024);
		int saveSumScore = sumScoreDao.saveSumScore(sumScore);//保存用户的房间号
		System.out.println(sumScore.getId());
	}
	
	/**
	 * @param SumScore
	 * @return
	 */
	public List<SumScore> findSumScore(Map map) {
		return super.queryForList("SumScore.query", map);
	}
	
	
	/**保存用户
	 * @param SumScore
	 * @return
	 */
	public int saveSumScore(SumScore sumScore) {
		int id = super.insert("SumScore.save", sumScore);
		return id;
	}
	
	
	/**修改用户
	 * @param SumScore
	 * @return
	 */
	public int modifySumScore(SumScore sumScore) {
		int count = 0;
		count = super.update("SumScore.modify", sumScore);
		return count;
	}
	
	public List<SumScore> queryList(SumScore sumScore) {
		return super.queryForList("SumScore.queryList", sumScore);
	}
}
