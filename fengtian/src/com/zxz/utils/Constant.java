package com.zxz.utils;

public interface Constant {

	public static final String method="method";//方法名
	public static final String code ="code";
	public static final String success ="success";
	public static final String error = "error";//错误
	public static final String discription = "discription";//描述
	public static final String direction = "dirction";//方向
	public static final String type = "type";//类型
	public static final int GONG_GANG = 0;//可以公杠
	public static final int AN_GANG = 1;//可以暗杠 
	
	/**
	 * 出牌的状态
	 */
	public static final int GAGME_STATUS_OF_CHUPAI = 0;//出牌的状态
	
	/**
	 * //明杠的分
	 */
	public static final int MINGGANG_SCORE = 2;
	/**
	 * 碰杠的分
	 */
	public static final int PENGGANG_SCORE = 2;
	
	/**
	 * 暗杠的分
	 */
	public static final int ANGANG_SCORE = 4;
	
	
	/**
	 * 暗杠 中发白的分
	 */
	public static final int ANGANG_BAI_SCORE = 8;
	/**
	 * 碰牌的状态
	 */
	public static final int GAGME_STATUS_OF_PENGPAI = 1;//碰牌的状态
	
	/**
	 * 杠牌的状态 (接杠)
	 */
	public static final int GAGME_STATUS_OF_GANGPAI = 2;//普通杠牌
	
	/**
	 * 吃牌的状态 
	 */
	public static final int GAGME_STATUS_OF_CHI = 11;//吃牌
	/**
	 * 暗杠的状态 
	 */
	public static final int GAGME_STATUS_OF_ANGANG = 3;//暗杠
	
	/**
	 * 公杠的状态 
	 */
	public static final int GAGME_STATUS_OF_GONG_GANG = 4;//公杠
	
	
	/**
	 * 旋风杠
	 */
	public static final int GAGME_STATUS_OF_XUANFENG_GANG = 12;//公杠
	
	
	/**
	 * 吃牌
	 */
	public static final int GAME_STATUS_OF_CHI = 11;
	
	/**
	 * 等待胡牌的状态 
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU = 5;//等待胡牌
	
	/**
	 * 等待胡牌的状态 
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW = 6;//等待胡牌
	
	/**
	 * 用户即可胡又可以碰的时候选择了碰牌
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_PENG = 7;//等待胡牌又可以碰
	
	
	/**
	 * 用户即可胡又可以杠的时候选择了杠牌
	 */
	public static final int GAGME_STATUS_OF_WAIT_HU_NEW_WAIT_GANG = 8;//等待胡牌又可以杠
	
	
	/**
	 * 游戏等待开始
	 */
	public static final int GAGME_STATUS_OF_WAIT_START = 0;
	
	
	/**
	 * 游戏不可以吃牌
	 */
	public static final int GAGME_NOT_CAN_CHI= 0;
	/**
	 * 游戏可以吃牌
	 */
	public static final int GAGME_CAN_CHI= 1;
	/**
	 * 游戏进行中
	 */
	public static final int GAGME_STATUS_OF_IS_GAMING = 1;
	
	/**
	 * 游戏进行中
	 */
	public static final int GAGME_STATUS_OF_IS_WAITE_XUANPAO = 2;
	
	/**
	 * 游戏是否结束 
	 */
	public static final String GAME_END = "GAME_END";//游戏是否结束
	
	
	/**
	 * 用户注册的时候默认的房卡数量
	 */
	public static final int DEFAULT_USER_REGIST_ROOMCARD = 5;
	
	/**
	 * 游戏结束倒计时
	 */
	public static final int TIME_TO_START_GAME = 10000;//2分钟
	
	/**
	 * 游戏结束倒计时
	 */
	public static final int TIME_TO_WAITE_JIESAN = 30000;//30秒
	
	
	/**
	 * 用户自动开始返回桌面的时间
	 */
	public static final int AUTO_USER_TIME_TO_START_GAME = 10000;
	
	
	/**
	 * 测试的游戏局数  1000
	 */
	public static final int TEST_TOTAL_GAME = 1000;
	
	/**
	 * 用户赢牌的时候等待1000秒钟
	 */
	public static final int TIME_WAIT_WIN = 100;
	
	/**
	 * 通知用户出牌的时候等待500毫秒
	 */
	public static final int TIME_WAIT_CHUPAI = 100;
	
	
	/**
	 * 房间创建10分钟还没有开始，则自动解散房间
	 */
	public static final int TIME_TO_DISBAND_ROOM = 60000*10*3;//30分钟
	
	
	/**
	 * 红中玩法
	 */
	public static final int playTypeOfHongZhong = 0;
	
	/**
	 * 血战玩法
	 */
	public static final int playTypeOfXueZhan = 1;
	
	
	/**
	 * 等待扎码
	 */
	public static final int TIME_TO_WAIT_ZHAMA = 1000*7;//等待扎码
	
	
	
	/**
	 * redis中记录用户房间的时间
	 */
	public static final int TIME_TO_USER_ROOM = 60*60*5;
	
	
	 /**
     * 奉天棋牌
     */
    public static final int REDIS_DB_FENGTIAN = 5;
	
    /**
     * 读超时
     */
    public static final int READ_IDEL_TIME_OUT = 10; 
    
    /**
     * 写超时
     */
    public static final int WRITE_IDEL_TIME_OUT = 10;
    
    /**
     * 所有超时
     */
    public static final int ALL_IDEL_TIME_OUT = 10*30; 
}
