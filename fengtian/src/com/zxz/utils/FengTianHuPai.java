package com.zxz.utils;



import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.zxz.domain.OneRoom;
import com.zxz.domain.User;


public class FengTianHuPai {

	private static Logger logger = Logger.getLogger(FengTianHuPai.class);
	//房间属性
    public  int propertyForRoom_roomId;             //房间号
    public  int propertyForRoom_quanTotal;          //总圈数
    public  int propertyForRoom_quanCurrent;        //当前圈数
    public  int propertyForRoom_gameStartDir;       //游戏开始时，庄家的方向
    public  long propertyForRoom_timeForRoomBuild;     //开放时间
    public  long propertyForRoom_timeForGameStart;     //游戏开始时间
    public  long propertyForRoom_timeForGmaeFinish;    //游戏结束时间
    
    //房间规则
    public  boolean propertyForRoom_pengkaimen= true;    //只能碰开门
    public  boolean propertyForRoom_fengding= true;      //封顶上限
    public  boolean propertyForRoom_dianpao= true;      //带点炮
    public  boolean propertyForRoom_yitouting= false;    //带一头听
    public  boolean propertyForRoom_shoulouzi= false;   //带手搂子
    public  boolean propertyForRoom_qiduihun = false;       //带混7对
    public  boolean propertyForRoom_qiduichun = true;      //带纯7对
    public  boolean propertyForRoom_piaohun = false;       //带混飘
    public  boolean propertyForRoom_piaochun = true;      //带纯飘
    public  boolean propertyForRoom_qingyise = true;      //带清一色
    public  boolean propertyForRoom_guodan = true;       //带过蛋0
//    public  boolean propertyForRoom_zhanhu = true;       //站着胡
    
    //胡牌类型
    public static int hupaiType = -1;
    //混七对
    public static final int hupaiType_7duiHun = 1;
    //纯七对
    public static final int hupaiType_7duiChun = 2;
    //混票
    public static final int hupaiType_piaoHun = 3;
    //纯飘
    public static final int hupaiType_piaoChun = 4;
    //清一色
    public static final int hupaiType_qingyise = 5;
    //单砸
    public static final int hupaiType_danZa = 6;
    //夹胡
    public static final int hupaiType_jiaHu = 7;
    //37夹胡
    public static final int hupaiType_37JiaHu = 8;
    //单胡幺九
    public static final int hupaiType_danHuYaoJiu = 9;
    //平胡
    public static final int hupaiType_pingHu = 10;
    
    public static List<MsgForCardsType> arrayListForCardsType = new ArrayList<MsgForCardsType>();
    
	public static void main(String[] args) {
		FengTianHuPai fengTianHuPai = new FengTianHuPai();
		//房间属性
		fengTianHuPai.propertyForRoom_roomId = 0;             //房间号
	    fengTianHuPai.propertyForRoom_quanTotal = 0;          //总圈数
	    fengTianHuPai.propertyForRoom_quanCurrent = 0;        //当前圈数
	    fengTianHuPai.propertyForRoom_gameStartDir = 0;       //游戏开始时，庄家的方向
	    fengTianHuPai.propertyForRoom_timeForRoomBuild = 0;     //开放时间
	    fengTianHuPai.propertyForRoom_timeForGameStart = 0;     //游戏开始时间
	    fengTianHuPai.propertyForRoom_timeForGmaeFinish = 0;    //游戏结束时间
	    
	    
	    //牌型
	    arrayListForCardsType = new ArrayList<MsgForCardsType>();
		//80, 81, 82, 83
	    int intArrayForHand[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 
				 0, 0, 0, 0};
	    int intArrayForChi[]  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 
				 0, 0, 0, 0};

	    int intArrayForPeng[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 3, 0, 0, 0, 
				 0, 0, 0, 
				 0, 0, 0, 0};
	    int intArrayForGangM[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				 0, 0, 0, 
				 0, 0, 0, 0};
	    int intArrayForGangA[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    		0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    		0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    		4, 0, 0, 
	    		0, 0, 0, 0};
		int intArrayForZFB[]  = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0};
		int intArrayForDNXB[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
//		int intArrayForCard[] = {0, 4, 8, 52, 53, 72, 76, 80, 96, 100, 104, 132, 133, 134};
		int intArrayForCard[] = {17,18,25,28,42,45,48};
		
		for (int i = 0; i < intArrayForCard.length; i++) {
			intArrayForHand[intArrayForCard[i]/4]++;
		}
		
		FengTianHuPai testForSYMJ = new FengTianHuPai();
		
		long curTime = System.currentTimeMillis();
		
		String stringForCheck = "";
		for (int i = 0; i < 34; i++) {
	        if (intArrayForHand[i] > 0) {
	            stringForCheck += testForSYMJ.getNameFromCardId(i);
	            stringForCheck += "(";
	            stringForCheck += intArrayForHand[i];
	            stringForCheck += ").";
	        }
	    }
		
		//*********听牌提示
		//听牌提示检测---检测是否听牌
//		boolean isTingTip = testForSYMJ.checkTingTip(intArrayForHand, intArrayForChi, intArrayForPeng, 
//				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB);
//		System.out.println(isTingTip ? "打出某张牌可以听牌-_-" : "打出什么牌， 都不能听_-_");
		
		//听牌数组检测---检测打出后可以听得牌
//		testForSYMJ.getArrayForTingTip(0, intArrayForHand, intArrayForChi, intArrayForPeng, 
//				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, true);
		
		
/*		int score_lose_hu = fengTianHuPai.getScore_lose_hu(false, true, true,
				false, false, false, true, true, 10, 128, true, true, false, 0,
				2, 0,null,null);*/
		//		fengTianHuPai.getScore_lose_hu(isZhuangWin, isZhuangLose, isZiMo, isDianPaoByMySelf, isZhangMao, isGangShangKaiHua, isHaiDiLaoYue, isTingPai, huType, fengdingNum, dianPao, isKaiMen, gangAnum, mingGang)
//		System.out.println(score_lose_hu);
		
		
		//*********听牌检测
		//听牌检测
		boolean isCanTing = testForSYMJ.checkTingPai(intArrayForHand, intArrayForChi, intArrayForPeng, 
				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB);
		System.out.println(isCanTing ? "抓到某张后可以听牌-_-" : "抓到什么牌，都不能听_-_");
		
		//听牌数组检测
		testForSYMJ.getArrayForTingPai(intArrayForHand, intArrayForChi, intArrayForPeng, 
				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, true, false, false);
		
		
		
		//*********胡牌检测
		//胡牌检测
//		boolean isCanHu = testForSYMJ.checkHuPai(0, intArrayForHand, intArrayForChi, intArrayForPeng, 
//				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, false, false);
//		System.out.println(isCanHu ? "此牌型可以胡-_-" : "此牌型不能胡_-_");
		
		//消除检测（手牌为3n + 2， 第一个boolean为是否有横牌）
//		boolean isCanXiaoChu = testForSYMJ.checkIsCanXiaoChu(0, intArrayForHand, false, true);
//		System.out.println(isCanXiaoChu ? "此牌型可以消除-_-" : "此牌型不能消除_-_");
		
		//*********胡牌类型检测
//		int huType = testForSYMJ.getHuType(13, intArrayForHand, intArrayForChi, intArrayForPeng, 
//				intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB);
//		System.out.println(testForSYMJ.getStringByHuType(huType) + "  huType:" + huType);
		
	    System.out.println("听牌提示检测时间：" + (System.currentTimeMillis() - curTime));
	    System.out.println("手牌(" + testForSYMJ.getCardsNumFormArray(intArrayForHand, 34) + "):" + stringForCheck);
	    
	}
	
	/**
	 *  听牌提示检测
	 *
	 *  @param intArray     检测数组
	 *  @param cardIdForHun 混id
	 *
	 *  @return 是否发送听牌提示
	 */
	public boolean checkTingTip(int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
			int[] intArrayForGangM, int[] intArrayForGangA, int[] intArrayForDNXB, int[] intArrayForZFB){
	    
		boolean isCanTing = false;
	    
	    int arrayNumForAll = 9*3 + 4 + 3;
	    int arrayForCheck[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
	    for (int i = 0; i < arrayNumForAll; i++) {
	        arrayForCheck[i] = intArrayForHand[i];
	    }
	    
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if (arrayForCheck[i] > 0) {
	            arrayForCheck[i] -= 1;
	            if (checkTingPai(arrayForCheck, intArrayForChi, intArrayForPeng, intArrayForGangM, intArrayForGangA, 
	            		intArrayForDNXB, intArrayForZFB)) {
	                isCanTing = true;
	                break;
	            }
	            arrayForCheck[i] += 1;
	        }
	    }
	    
	    return isCanTing;
	}

	/**
	 *  听牌提示检测
	 *
	 *  @param intArray        检测数组
	 *  @param cardIdForHun    混id
	 *  @param isLogForTingTip 是否输出log
	 *  @param isLogForTingPai 是否输出log
	 *  @param isLogForHuPai   是否输出log
	 *  @param isLogForCheckPu 是否输出log
	 *
	 *  @return 返回听牌提示数组
	 */
	public ArrayList<Integer> getArrayForTingTip(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, 
			int[] intArrayForPeng, int[] intArrayForGangM, int[] intArrayForGangA, 
			int[] intArrayForDNXB, int[] intArrayForZFB, boolean isLogForTingTip){
	    
		ArrayList<Integer> arrayListForTingTip = new ArrayList<Integer>();
	    
		int arrayNum = 34;
	    int arrayForCheck[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
	    for (int i = 0; i < arrayNum; i++) {
	        arrayForCheck[i] = intArrayForHand[i];
	    }
	    
	    for (int i = 0; i < arrayNum; i++) {
	        if (arrayForCheck[i] > 0) {
	            arrayForCheck[i] -= 1;
	            System.out.println("打出：" + getNameFromCardId(i) + "开始检测听牌");
	            if (checkTingPai(arrayForCheck, intArrayForChi, intArrayForPeng, 
	            		intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB)) {
	                arrayListForTingTip.add(new Integer(i));
	            }
	            arrayForCheck[i] += 1;
	        }
	    }
	    
	    if (isLogForTingTip) {
	        int arrayNumForTingTip = arrayListForTingTip.size();
	        int arrayNumForInit = getCardsNumFormArray(intArrayForHand, 34);
	        String stringForTing = "";
	        for (int i = 0; i < arrayNumForTingTip; i++) {
	        	((Integer)arrayListForTingTip.get(i)).intValue();
	            stringForTing += getNameFromCardId(((Integer)arrayListForTingTip.get(i)).intValue());
	            stringForTing += ".";
	        }
	        
	        String stringLog = "";
	        if (arrayNumForTingTip <= 0) {
	            stringLog = "不能听牌";
	        }else{
	            stringLog = stringForTing;
	        }
	        System.out.println("听牌提示检测  总牌数(" + arrayNumForInit + ")打出后可以听的牌(" + arrayNumForTingTip + "种):" + stringLog);
	    }
	    
	    return arrayListForTingTip;
	}

	/**
	 *  检测听牌（13张）
	 *
	 *  @param intArray     int数组
	 *  @param cardIdForHun 混id
	 *
	 *  @return 返回是否听牌
	 */
	public boolean checkTingPai(int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
			int[] intArrayForGangM, int[] intArrayForGangA, int[] intArrayForDNXB, int[] intArrayForZFB){
		boolean isCanTingPai = false;
	    
	    int arrayForCheck[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
	    for (int i = 0; i < 34; i++) {
	        arrayForCheck[i] = intArrayForHand[i];
	    }
	    
	    for (int i = 0; i < 34; i++) {
	        arrayForCheck[i] += 1;
	        if (checkHuPai(i, arrayForCheck, intArrayForChi, intArrayForPeng, 
	        		intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, false, false)) {
	            isCanTingPai = true;
	            break;
	        }
	        arrayForCheck[i] -= 1;
	    }
	    
	    return isCanTingPai;
	}

	/**
	 *  检测听牌
	 *
	 *  @param intArray        检测数组
	 *  @param cardIdForHun    混id
	 *  @param isLogForTingPai 是否输出log
	 *  @param isLogForHuPai   是否输出log
	 *  @param isLogForCheckPu 是否输出log
	 *
	 *  @return 返回听牌数组
	 */
	public ArrayList<Integer> getArrayForTingPai(int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
			int[] intArrayForGangM, int[] intArrayForGangA, int[] intArrayForDNXB, int[] intArrayForZFB, 
			boolean isLogForTingPai, boolean isLogForHuPai, boolean isLogForcheckIsCanXiao){
	    
	    ArrayList<Integer> arrayListForTingPai = new ArrayList<Integer>();
	    
	    int arrayNum = 34;
	    int arrayForCheck[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
	    for (int i = 0; i < arrayNum; i++) {
	        arrayForCheck[i] = intArrayForHand[i];
	    }
	    
	    for (int i = 0; i < arrayNum; i++) {
	        arrayForCheck[i] += 1;
	        if (checkHuPai(i, arrayForCheck, intArrayForChi, intArrayForPeng, 
	        		intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, 
	        		isLogForHuPai, isLogForcheckIsCanXiao)) {
	            arrayListForTingPai.add(new Integer(i));
	        }
	        arrayForCheck[i] -= 1;
	    }
	    
	    if (isLogForTingPai) {
	        int arrayNumForTingPai = arrayListForTingPai.size();
	        String stringForHu = "";
	        for (int i = 0; i < arrayNumForTingPai; i++) {
	            stringForHu += getNameFromCardId(((Integer)arrayListForTingPai.get(i)).intValue());
	            stringForHu += ".";
	        }
	        
	        String stringLog = "";
	        if (arrayNumForTingPai <= 0) {
	            stringLog = "没有可以胡的牌";
	        }else if (arrayNumForTingPai >= 34){
	            stringLog = "胡所有牌";
	        }else{
	            stringLog = stringForHu;
	        }
	        System.out.println("听牌检测  可以听的牌(" + arrayNumForTingPai + "):" + stringLog);
	    }
	    
	    return arrayListForTingPai;
	}

	/**
	 *  胡牌检测
	 *
	 *  @param intArray        检测数组
	 *  @param cardIdForHun    混id
	 *  @param isLogForHu      是否输出log
	 *  @param isLogForCheckPu 是否输出log
	 *
	 *  @return 返回是否胡牌
	 */
	public boolean checkHuPai(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
			int[] intArrayForGangM, int[] intArrayForGangA, 
			int[] intArrayForDNXB, int[] intArrayForZFB, boolean isLogForHu, boolean isLogForcheckIsCanXiao){
		if(isLogForHu){
			System.out.println("抓到的牌是：" + getNameFromCardId(cardIdForZhua) + " 开始进行胡牌检测");
		}
	    //牌样数
	    int arrayNumForAll = 9*3 + 4 + 3;
	    int intArrayCheck_wanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //万字数组
	    int intArrayCheck_tong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //筒字数组
	    int intArrayCheck_tiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //条字数组
	    int intArrayCheck_feng[] = {0, 0, 0, 0, 0, 0, 0};    //风字数组
	    //放入总牌数组中-手牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForHand[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForHand[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForHand[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForHand[i];
	        }
	    }
	    //放入总牌数组中-吃牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForChi[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForChi[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForChi[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForChi[i];
	        }
	    }
	    //放入总牌数组中-碰牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForPeng[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForPeng[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForPeng[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForPeng[i];
	        }
	    }
	    //放入总牌数组中-杠牌-明杠
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangM[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangM[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangM[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangM[i];
	        }
	    }
	    //放入总牌数组中-杠牌-暗杠
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangA[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangA[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangA[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangA[i];
	        }
	    }
	    //放入总牌数组中-风牌（东南西北）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForDNXB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForDNXB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForDNXB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForDNXB[i];
	        }
	    }
	    //放入总牌数组中-风牌（中发白）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForZFB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForZFB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForZFB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForZFB[i];
	        }
	    }
	    
	    int cardsNumForChi  	= getCardsNumFormArray(intArrayForChi,  34);    //牌数-吃牌
	    int cardsNumForPeng 	= getCardsNumFormArray(intArrayForPeng, 34);    //牌数-碰牌
	    int cardsNumForGangM 	= getCardsNumFormArray(intArrayForGangM, 34);    //牌数-杠牌-明杠
	    int cardsNumForGangA 	= getCardsNumFormArray(intArrayForGangA, 34);    //牌数-杠牌-暗杠
	    int cardsNumForDNXB 	= getCardsNumFormArray(intArrayForDNXB, 34);    //牌数-东南西北
	    int cardsNumForZFB  	= getCardsNumFormArray(intArrayForZFB,  34);    //牌数-中发白
	    
	    int cardsNumForWanz = getCardsNumFormArray(intArrayCheck_wanz, 9);  //牌数-万
	    int cardsNumForTong = getCardsNumFormArray(intArrayCheck_tong, 9);  //牌数-筒
	    int cardsNumForTiao = getCardsNumFormArray(intArrayCheck_tiao, 9);  //牌数-条
	    int cardsNumForFeng = getCardsNumFormArray(intArrayCheck_feng, 7);  //牌数-条
	    
	    //是否开门
	    boolean isKaiMen = checkIsKaiMen(cardsNumForChi, cardsNumForPeng, cardsNumForGangM);
	    //是否3门齐
	    boolean is3MenQi = checkIs3MenQi(cardsNumForWanz, cardsNumForTong, cardsNumForTiao);
	    //是否有幺九
	    boolean isYaoJiu = checkIsYaoJiu(intArrayCheck_wanz, intArrayCheck_tong, intArrayCheck_tiao, cardsNumForFeng);
	    //是否有横牌
	    boolean isHengPaiWai = checkIsHengPaiWai(cardsNumForPeng, cardsNumForGangM, cardsNumForGangA, cardsNumForDNXB, cardsNumForZFB);
	    //是否清一色
	    boolean isQingYiSe = false;
	    if (propertyForRoom_qingyise && checkIsQingYiSe(cardsNumForWanz, cardsNumForTong, cardsNumForTiao)) {
	    	isQingYiSe = true;
		}else{
			isQingYiSe = false;
		}
	    
	    return checkHuPaiElse(cardIdForZhua, intArrayForHand, intArrayForChi, isKaiMen, is3MenQi, isYaoJiu, isHengPaiWai, isQingYiSe, 
	    		isLogForHu, isLogForcheckIsCanXiao);
	}
	
	public boolean checkHuPaiElse(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, 
			boolean isKaiMen, boolean is3MenQi, boolean isYaoJiu, boolean isHengPaiWai, boolean isQingYiSe,
			boolean isLogForHu, boolean isLogForcheckIsCanXiao){
		//是否七对
	    if (propertyForRoom_qiduihun || propertyForRoom_qiduichun) {
	    	boolean isQiDui = check7Dui(intArrayForHand);
	        //纯7对判断
	        if (propertyForRoom_qiduichun && is3MenQi && isYaoJiu && isQiDui) {
	            if (isLogForHu) {
	            	System.out.println("===========可以胡----纯7对");
	            }
	            return true;
	        }
	        //混7对判断
	        if (propertyForRoom_qiduihun && isQiDui) {
	            if (isLogForHu) {
	                System.out.println("===========可以胡----混7对");
	            }
	            return true;
	        }
	    }
	    
	    //是否飘胡
	    if (propertyForRoom_piaohun || propertyForRoom_piaochun) {
	        boolean isPiao = checkPiao(intArrayForHand, intArrayForChi);
	        //纯飘判断
	        if (propertyForRoom_piaochun && isKaiMen && is3MenQi && isYaoJiu && isPiao) {
	            if (isLogForHu) {
	            	System.out.println("===========可以胡----纯飘");
	            }
	            return true;
	        }
	        //混飘判断
	        if (propertyForRoom_piaohun && isKaiMen && isPiao) {
	            if (isLogForHu) {
	            	System.out.println("===========可以胡----混飘");
	            }
	            return true;
	        }
	    }
	    //没有幺九不能普通胡牌
	    if (!isYaoJiu) {
	        if (isLogForHu) {
	        	System.out.println("===========没有幺九----普通胡牌不能胡");
	        }
	        return false;
	    }
	    
	    //满足11 111 123的形式
	    boolean isEnough_11_111_123 = checkIsCanXiaoChu(cardIdForZhua, intArrayForHand, isHengPaiWai, isLogForcheckIsCanXiao);
	    if (!isEnough_11_111_123) {
	        if (isLogForHu) {
	        	System.out.println("===========不能满足11 111 123的形式----");
	        }
	        return false;
	    }
	    
	    String stringForCheckHu = "0000  最终检测到的牌型：";
	    int arrayNumForHuType = arrayListForCardsType.size();
	    for (int i = 0; i < arrayNumForHuType; i++) {
	        MsgForCardsType msgForCardsType = (MsgForCardsType)arrayListForCardsType.get(i);
	        if (msgForCardsType.cardsType == MsgForCardsType.cardsType_11) {
	            stringForCheckHu += getNameFromCardId(msgForCardsType.cardContent1);
	            stringForCheckHu += "、";
	            stringForCheckHu += getNameFromCardId(msgForCardsType.cardContent2);
	        }else{
	            stringForCheckHu += "  ";
	            stringForCheckHu += getNameFromCardId(msgForCardsType.cardContent1);
	            stringForCheckHu += "、";
	            stringForCheckHu += getNameFromCardId(msgForCardsType.cardContent2);
	            stringForCheckHu += "、";
	            stringForCheckHu += getNameFromCardId(msgForCardsType.cardContent3);
	        }
	    }
	    if (isLogForHu) {
	    	System.out.println(stringForCheckHu);
	    }
	    
	    //满足清一色条件
	    if (isQingYiSe) {
	        if (isLogForHu) {
	        	System.out.println("===========清一色----可以不开门胡牌");
	        }
	        return true;
	    }
	    
	    //普通胡牌
	    if (isKaiMen && is3MenQi) {
	        if (isLogForHu) {
	        	System.out.println("===========有幺九、开门、三门齐、有横牌、满足11、111、123牌型");
	        }
	        return true;
	    }else{
	        if (isLogForHu) {
	        	System.out.println("===========不满足：开门并且三门齐");
	        }
	    }
	    
	    return false;
	}
	
	/**
	 *  是否开门
	 *
	 *  @param intArrayForChi
	 *  @param intArrayForPeng
	 *  @param intArrayForGang
	 *
	 *  @return
	 */
	public boolean checkIsKaiMen(int cardsNumForChi, int cardsNumForPeng, int cardsNumForGang){
/*	    boolean isKaiMen = false;
	    
	    if (cardsNumForChi + cardsNumForPeng + cardsNumForGang > 0) {
	        isKaiMen = true;
	    }else{
	        isKaiMen = false;
	    }*/
	    
	    return true;
	}

	/**
	 *  是否三门齐
	 *
	 *  @param intArrayForHand
	 *  @param intArrayForChi
	 *  @param intArrayForPeng
	 *  @param intArrayForGang
	 *
	 *  @return
	 */
	public boolean checkIs3MenQi(int cardsNumForWan, int cardsNumForTong, int cardsNumForTiao){
	    boolean is3MenQi = false;

	    if (cardsNumForWan > 0 && cardsNumForTong > 0 && cardsNumForTiao > 0) {
	        is3MenQi = true;
	    }else{
	        is3MenQi = false;
	    }
	    
	    return is3MenQi;
	}

	/**
	 *  是否有幺九
	 *
	 *  @param intArrayForHand
	 *  @param intArrayForChi
	 *  @param intArrayForPeng
	 *  @param intArrayForGang
	 *  @param intArrayForDNXB
	 *  @param intArrayForZFB
	 *
	 *  @return
	 */
	public boolean checkIsYaoJiu(int[] intArrayCheck_wan, int[] intArrayCheck_tong, int[] intArrayCheck_tiao, int cardsNumForFeng){
	    //寻找是否有幺九
	    if (cardsNumForFeng > 0) {
	        return true;
	    }
	    
	    if (intArrayCheck_wan[0] > 0 || intArrayCheck_wan[8] > 0) {
	        return true;
	    }
	    
	    if (intArrayCheck_tong[0] > 0 || intArrayCheck_tong[8] > 0) {
	        return true;
	    }
	    
	    if (intArrayCheck_tiao[0] > 0 || intArrayCheck_tiao[8] > 0) {
	        return true;
	    }
	    
	    return false;
	}

	/**
	 *  是否清一色
	 *
	 *  @param intArrayForHand
	 *  @param intArrayForChi
	 *  @param intArrayForPeng
	 *  @param intArrayForGang
	 *
	 *  @return
	 */
	public boolean checkIsQingYiSe(int cardsNumForWan, int cardsNumForTong, int cardsNumForTiao){
	    if (cardsNumForWan <= 0 && cardsNumForTong <= 0) {
	        return true;
	    }
	    if (cardsNumForWan <= 0 && cardsNumForTiao <= 0) {
	        return true;
	    }
	    if (cardsNumForTong <= 0 && cardsNumForTiao <= 0) {
	        return true;
	    }
	    return false;
	}

	/**
	 *  是否有横牌
	 *
	 *  @param intArrayForPeng
	 *  @param intArrayForGang
	 *  @param intArrayForDNXB
	 *  @param intArrayForZFB
	 *
	 *  @return
	 */
	public boolean checkIsHengPaiWai(int cardsNumForPeng, int cardsNumForGangM, int cardsNumForGangA, 
			int cardsNumForDNXB, int cardsNumForZFB){
	    if (cardsNumForPeng > 0 || cardsNumForGangM > 0 || cardsNumForGangA > 0 || cardsNumForDNXB > 0 || cardsNumForZFB > 0) {
	        return true;
	    }
	    
	    return false;
	}

	/**
	 *  是否能胡7对
	 *
	 *  @param intArrayForHand 手牌数组
	 *
	 *  @return
	 */
	public boolean check7Dui(int[] intArrayForHand){
	    boolean isCanHu = true;
	    int cardsNum = getCardsNumFormArray(intArrayForHand, 34);
	    
	    if (cardsNum < 14) {
	        isCanHu = false;
	    }else{
	        for (int i = 0; i < 34; i++) {
	            if (intArrayForHand[i] == 1 || intArrayForHand[i] == 3) {
	                isCanHu = false;
	                break;
	            }
	        }
	    }
	    
	    return isCanHu;
	}

	/**
	 *  是否能胡票
	 *
	 *  @param intArrayForHand 手牌数组
	 *  @param intArrayForChi  吃牌数组
	 *
	 *  @return
	 */
	public boolean checkPiao(int[] intArrayForHand, int[] intArrayForChi){
	    boolean isCanHu = true;
	    
	    int cardsNumForChi = getCardsNumFormArray(intArrayForChi, 34);
	    if (cardsNumForChi > 0) {
	        isCanHu = false;
	    }else{
	        int arrayNum = 34;
	        int intArray[] = {
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0};
	        //赋值检测数组
	        for (int i = 0; i < arrayNum; i++) {
	            intArray[i] = intArrayForHand[i];
	        }
	        
	        isCanHu = false;
	        for (int i = 0; i < 34; i++) {
	            if (intArray[i] == 2) {
	                intArray[i]-= 2;
	                isCanHu = true;
	                for (int j = 0; j < arrayNum; j++) {
	                    if (intArray[j] == 1 || intArray[j] == 2 || intArray[j] == 4) {
	                        isCanHu = false;
	                        break;
	                    }
	                }
	                break;
	            }
	        }
	    }
	    
	    return isCanHu;
	}
    
	/**
	 * 检测是否可以胡牌
	 * @param cardIdForZhua				抓到的牌
	 * @param intArrayForHand			手牌数组
	 * @param isHaveHengPai				是否有横牌
	 * @param isLogForcheckIsCanXiao	是否输出log
	 * @return
	 */
    public boolean checkIsCanXiaoChu(int cardIdForZhua,int[] intArrayForHand, boolean isHaveHengPai, boolean isLogForcheckIsCanXiao){
        
    	if(isLogForcheckIsCanXiao){
    		System.out.println("抓到的牌是：" + getNameFromCardId(cardIdForZhua) + " 开始进行消除检测");
    	}
    	
        int arrayNum = 34;
        int intArray[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
        //赋值检测数组
        for (int i = 0; i < arrayNum; i++) {
            intArray[i] = intArrayForHand[i];
        }
        
        boolean isHaveHengPaiCur = isHaveHengPai;
        
        for (int a = 0; a < arrayNum; a++) {
            if (intArray[a] >= 2) {
                arrayListForCardsType.clear();
                
                intArray[a] -= 2;
                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_11, a, a));
                if (a == 27 || a == 28 || a == 29) {
                    isHaveHengPaiCur = true;
                    if (isLogForcheckIsCanXiao) {
                        System.out.println("11牌型：" + getNameFromCardId(a) + " 做将    可以代替横牌， 继续检测++++++++");
                    }
                }else{
                    isHaveHengPaiCur = isHaveHengPai;
                    if (isLogForcheckIsCanXiao) {
                        System.out.println("11牌型：" + getNameFromCardId(a) + " 做将    继续检测++++++++");
                    }
                }
                //判断横牌---有
                if (isHaveHengPaiCur) {
                    for (int b = 0; b < arrayNum; b++) {
                        if (intArray[b] == 4) {
                            intArray[b] -= 3;
                            arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_111, b, b, b));
                            if (isLogForcheckIsCanXiao) {
                                System.out.println("有四张：111牌型：" + getNameFromCardId(a) + " 有横牌 继续检测++++++++");
                            }
                            //不能是字牌、牌面不能大于7（7万可以，8万和9万不行）
                            if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                                intArray[b] -= 1;
                                intArray[b + 1] -= 1;
                                intArray[b + 2] -= 1;
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有四张：123牌型：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " "
                                    		 +  getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                }
                            }else{
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有四张： " + getNameFromCardId(b) + "，不能完全消除， 不能胡牌，跳出当前层");
                                }
                                break;
                            }
                        }
                        else if (intArray[b] == 3){
                            intArray[b] -= 3;
                            arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_111, b, b, b));
                            if (isLogForcheckIsCanXiao) {
                                System.out.println("有三张：111牌型：" + getNameFromCardId(b) + "   可以消除，继续检测++++++++");
                            }
                        }
                        else if (intArray[b] == 2){
                            if (b < 27 && b%9 < 7 && intArray[b + 1] > 1 && intArray[b + 2] > 1) {
                                intArray[b] -= 2;
                                intArray[b + 1] -= 2;
                                intArray[b + 2] -= 2;
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有两张：123牌型*2：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " " + 
                                    		getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                }
                            }else{
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有两张：" + getNameFromCardId(b) + "   不满足123*2模式， 不能胡牌，跳出当前层");
                                }
                                break;
                            }
                        }
                        else if (intArray[b] == 1){
                            if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                                intArray[b] -= 1;
                                intArray[b + 1] -= 1;
                                intArray[b + 2] -= 1;
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有一张：123牌型*2：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " " + 
                                    		getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                }
                            }else{
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有一张 " + getNameFromCardId(b) + "：不满足123模式， 不能胡牌，跳出当前层");
                                }
                                break;
                            }
                        }
                        else{
                            continue;
                        }
                    }
                    
                    boolean isHuPai = getCardsNumFormArray(intArray, 34) <= 0 ? true : false;
                    
                    if (isHuPai) {
                        if (isLogForcheckIsCanXiao) {
                            System.out.println("￥￥￥￥￥￥￥￥￥￥" +  getNameFromCardId(a) + " 做将，完全消除，可以胡牌￥￥￥￥￥￥￥￥￥￥");
                        }
                        return true;
                    }else{
                        if (isLogForcheckIsCanXiao) {
                            System.out.println("￥￥￥￥￥￥￥￥￥￥" + getNameFromCardId(a) + " 做将，不能完全消除，不能胡牌，跳出当前层");
                        }
                    }
                }
                //**************判断横牌---没有------检测开始
                else{
                    for (int b = 0; b < arrayNum; b++) {
                        if (intArray[b] >= 3) {
                            intArray[b] -= 3;
                            arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_111, b, b, b));
                            isHaveHengPaiCur = true;
                            if (isLogForcheckIsCanXiao) {
                                System.out.println("111牌型：" + getNameFromCardId(b) + " 做横牌" + "    继续检测++++++++");
                            }
                            break;
                        }
                    }
                    
                    if (isHaveHengPaiCur) {
                        for (int b = 0; b < arrayNum; b++) {
                            if (intArray[b] == 4) {
                                intArray[b] -= 3;
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_111, b, b, b));
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有四张：111牌型：" + getNameFromCardId(a) + " 有横牌 继续检测++++++++");
                                }
                                //不能是字牌、牌面不能大于7（7万可以，8万和9万不行）
                                if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                                    intArray[b] -= 1;
                                    intArray[b + 1] -= 1;
                                    intArray[b + 2] -= 1;
                                    arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有四张：123牌型：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " "
                                        		 +  getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                    }
                                }else{
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有四张： " + getNameFromCardId(b) + "，不能完全消除， 不能胡牌，跳出当前层");
                                    }
                                    break;
                                }
                            }
                            else if (intArray[b] == 3){
                                intArray[b] -= 3;
                                arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_111, b, b, b));
                                if (isLogForcheckIsCanXiao) {
                                    System.out.println("有三张：111牌型：" + getNameFromCardId(b) + "   可以消除，继续检测++++++++");
                                }
                            }
                            else if (intArray[b] == 2){
                                if (b < 27 && b%9 < 7 && intArray[b + 1] > 1 && intArray[b + 2] > 1) {
                                    intArray[b] -= 2;
                                    intArray[b + 1] -= 2;
                                    intArray[b + 2] -= 2;
                                    arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                    arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有两张：123牌型*2：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " " + 
                                        		getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                    }
                                }else{
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有两张：" + getNameFromCardId(b) + "   不满足123*2模式， 不能胡牌，跳出当前层");
                                    }
                                    break;
                                }
                            }
                            else if (intArray[b] == 1){
                                if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                                    intArray[b] -= 1;
                                    intArray[b + 1] -= 1;
                                    intArray[b + 2] -= 1;
                                    arrayListForCardsType.add(new MsgForCardsType(MsgForCardsType.cardsType_123, b, b + 1, b + 2));
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有一张：123牌型*2：" + getNameFromCardId(b) + " " + getNameFromCardId(b + 1) + " " + 
                                        		getNameFromCardId(b + 2) + "   可以消除，继续检测++++++++");
                                    }
                                }else{
                                    if (isLogForcheckIsCanXiao) {
                                        System.out.println("有一张 " + getNameFromCardId(b) + "：不满足123模式， 不能胡牌，跳出当前层");
                                    }
                                    break;
                                }
                            }
                            else{
                                continue;
                            }
                        }
                        
                        boolean isHuPai = getCardsNumFormArray(intArray, 34) <= 0 ? true : false;
                        
                        if (isHuPai) {
                            if (isLogForcheckIsCanXiao) {
                                System.out.println("￥￥￥￥￥￥￥￥￥￥" +  getNameFromCardId(a) + " 做将，完全消除，可以胡牌￥￥￥￥￥￥￥￥￥￥");
                            }
                            return true;
                        }else{
                            if (isLogForcheckIsCanXiao) {
                                System.out.println("￥￥￥￥￥￥￥￥￥￥" + getNameFromCardId(a) + " 做将，不能完全消除，不能胡牌，跳出当前层");
                            }
                        }
                    }else{
                    	if (isLogForcheckIsCanXiao) {
                            System.out.println("￥￥￥￥￥￥￥￥￥￥" + getNameFromCardId(a) + " 做将，不能完全消除，不能胡牌，跳出当前层");
                        }
                    }
                }
                //**************判断横牌---没有------检测结束
                
                
                //赋值检测数组
                for (int i = 0; i < arrayNum; i++) {
                    intArray[i] = intArrayForHand[i];
                }
                
                if (isLogForcheckIsCanXiao) {
                    System.out.println("############################################# 重新赋值检测 当前位置 " + getNameFromCardId(a));
                }
            }
        }
        
        if (isLogForcheckIsCanXiao) {
            System.out.println("***************检测完成，不能胡牌*************");
        }
        
        return false;
    }

    
    public int getHuType(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
    		int[] intArrayForGangM, int[] intArrayForGangA, int[] intArrayForDNXB, int[] intArrayForZFB,
    		boolean isDaiQingYiSe){
    	System.out.println("抓到的牌是：" + getNameFromCardId(cardIdForZhua));
    	
    	//牌样数
	    int arrayNumForAll = 9*3 + 4 + 3;
	    int intArrayCheck_wanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //万字数组
	    int intArrayCheck_tong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //筒字数组
	    int intArrayCheck_tiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //条字数组
	    int intArrayCheck_feng[] = {0, 0, 0, 0, 0, 0, 0};    //风字数组
	    //放入总牌数组中-手牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForHand[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForHand[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForHand[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForHand[i];
	        }
	    }
	    //放入总牌数组中-吃牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForChi[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForChi[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForChi[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForChi[i];
	        }
	    }
	    //放入总牌数组中-碰牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForPeng[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForPeng[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForPeng[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForPeng[i];
	        }
	    }
	    //放入总牌数组中-杠牌-明杠
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangM[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangM[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangM[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangM[i];
	        }
	    }
	    //放入总牌数组中-杠牌-暗杠
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangA[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangA[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangA[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangA[i];
	        }
	    }
	    //放入总牌数组中-风牌（东南西北）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForDNXB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForDNXB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForDNXB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForDNXB[i];
	        }
	    }
	    //放入总牌数组中-风牌（中发白）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForZFB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForZFB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForZFB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForZFB[i];
	        }
	    }
	    
	    int cardsNumForChi  	= getCardsNumFormArray(intArrayForChi,  34);    //牌数-吃牌
	    int cardsNumForPeng		= getCardsNumFormArray(intArrayForPeng, 34);    //牌数-碰牌
	    int cardsNumForGangM 	= getCardsNumFormArray(intArrayForGangM, 34);    //牌数-杠牌
	    int cardsNumForGangA 	= getCardsNumFormArray(intArrayForGangA, 34);    //牌数-杠牌
	    int cardsNumForDNXB 	= getCardsNumFormArray(intArrayForDNXB, 34);    //牌数-东南西北
	    int cardsNumForZFB 		= getCardsNumFormArray(intArrayForZFB,  34);    //牌数-中发白
	    
	    int cardsNumForWanz = getCardsNumFormArray(intArrayCheck_wanz, 9);  //牌数-万
	    int cardsNumForTong = getCardsNumFormArray(intArrayCheck_tong, 9);  //牌数-筒
	    int cardsNumForTiao = getCardsNumFormArray(intArrayCheck_tiao, 9);  //牌数-条
	    int cardsNumForFeng = getCardsNumFormArray(intArrayCheck_feng, 7);  //牌数-条
	    
	    //是否开门
	    boolean isKaiMen = checkIsKaiMen(cardsNumForChi, cardsNumForPeng, cardsNumForGangM);
	    //是否3门齐
	    boolean is3MenQi = checkIs3MenQi(cardsNumForWanz, cardsNumForTong, cardsNumForTiao);
	    //是否有幺九
	    boolean isYaoJiu = checkIsYaoJiu(intArrayCheck_wanz, intArrayCheck_tong, intArrayCheck_tiao, cardsNumForFeng);
	    //是否有横牌
	    boolean isHengPaiWai = checkIsHengPaiWai(cardsNumForPeng, cardsNumForGangM, cardsNumForGangA, cardsNumForDNXB, cardsNumForZFB);
	    
	    //清一色检测
	    if (isDaiQingYiSe && checkHuTypeForQingYiSe(cardsNumForWanz, cardsNumForTong, cardsNumForTiao)) {
	    	return hupaiType_qingyise;
		}else{
//	    	System.out.println("不能胡：清一色");
	    }
	    
	    //7对检测
	    int intFor7Dui = checkHuTypeFor7Dui(intArrayForHand, is3MenQi, isYaoJiu);
	    if(intFor7Dui > 0){
	    	return intFor7Dui;
	    }else{
//	    	System.out.println("不能胡：七对");
	    }
	    
	    //飘胡检测
	    int intForPiao = checkHuTypeForPiao(intArrayForHand, intArrayForChi, isKaiMen, is3MenQi, isYaoJiu);
	    if(intForPiao > 0){
	    	return intForPiao;
	    }else{
//	    	System.out.println("不能胡：飘胡");
	    }
	    
	  	//单砸检测
	    int intArrayCheck_danza[]  = {
	    		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
	    		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    		0, 0, 0, 0};
	    for (int i = 0; i < intArrayForHand.length; i++) {
	    	intArrayCheck_danza[i] = intArrayForHand[i];
		}
	    intArrayCheck_danza[cardIdForZhua] -= 1;
	    ArrayList<Integer> huList = getArrayForTingPai(intArrayCheck_danza, intArrayForChi, intArrayForPeng, 
	    		intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB, false, false, false);
	    int huNum = huList.size();
	    int intForDanZa = 0;
	    if (huNum > 1) {
	    	intForDanZa = 0;
		}else{
			intForDanZa = checkHuTypeForDanZa(cardIdForZhua, intArrayForHand);
		}
	    if(intForDanZa > 0){
	    	return intForDanZa;
	    }else{
//	    	System.out.println("不能胡：单砸");
	    }
	    
	    //单胡幺九检测
	    int intForDanHuYaoJiu = checkHuTypeForDanHuYaoJiu(cardIdForZhua, intArrayForHand, 
	    		intArrayForChi, intArrayForPeng, intArrayForGangM, intArrayForGangA, intArrayForDNXB, intArrayForZFB);
	    if (intForDanHuYaoJiu > 0) {
	    	return intForDanHuYaoJiu;
		}else{
//	    	System.out.println("不能胡：单胡幺九");
	    }
	    
	    //夹胡检测(包含3、7夹胡检测)
	    int intForJiaHu = checkHuTypeForJiaHu(cardIdForZhua, intArrayForHand, intArrayForChi, isHengPaiWai);
	    if (intForJiaHu > 0) {
			return intForJiaHu;
		}else{
//	    	System.out.println("不能胡：夹胡");
	    }
	    
    	return hupaiType_pingHu;
    }
    
    /**
     * 番型检测----清一色
     * @param cardsNumForWan
     * @param cardsNumForTong
     * @param cardsNumForTiao
     * @return
     */
    public boolean checkHuTypeForQingYiSe(int cardsNumForWan, int cardsNumForTong, int cardsNumForTiao){
    	boolean isQingYiSe = false;
	    if (cardsNumForWan <= 0 && cardsNumForTong <= 0) {
	    	isQingYiSe = true;
	    }
	    if (cardsNumForWan <= 0 && cardsNumForTiao <= 0) {
	    	isQingYiSe = true;
	    }
	    if (cardsNumForTong <= 0 && cardsNumForTiao <= 0) {
	    	isQingYiSe = true;
	    }
	    return isQingYiSe;
	}
    
    /**
     * 番型检测----七对
     * @param intArrayForHand
     * @return	返回值：0不胡	1混七对	2纯7对
     */
    public int checkHuTypeFor7Dui(int[] intArrayForHand, boolean is3MenQi, boolean isYaoJiu){
    	//是否七对
	    if (propertyForRoom_qiduihun || propertyForRoom_qiduichun) {
	    	boolean isQiDui = check7Dui(intArrayForHand);
	        //混7对判断
	        if (propertyForRoom_qiduihun && (!is3MenQi || !isYaoJiu) && isQiDui) {
	            return hupaiType_7duiHun;
	        }
	        
	        //纯7对判断
	        if (propertyForRoom_qiduichun && is3MenQi && isYaoJiu && isQiDui) {
	            return hupaiType_7duiChun;
	        }
	    }
	    return -1;
    }
    
    /**
     * 番型检测----飘胡
     * @param intArrayForHand
     * @param intArrayForChi
     * @return 返回值：0不胡	1混飘	2纯飘
     */
    public int checkHuTypeForPiao(int[] intArrayForHand, int[] intArrayForChi, boolean isKaiMen, boolean is3MenQi, boolean isYaoJiu){
    	//是否飘胡
	    if (propertyForRoom_piaohun || propertyForRoom_piaochun ) {
	        boolean isPiao = checkPiao(intArrayForHand, intArrayForChi);
	        //混飘判断
	        if (propertyForRoom_piaohun  && isKaiMen && (!is3MenQi || !isYaoJiu) && isPiao) {
	            return hupaiType_piaoHun;
	        }
	        //纯飘判断
	        if (propertyForRoom_piaochun  && isKaiMen && is3MenQi && isYaoJiu && isPiao) {
	            return hupaiType_piaoChun;
	        }
	    }
    	return -1;
    }
    
    /**
     * 番型检测----单砸
     * @param cardIdForZhua
     * @param intArrayForHand
     * @return
     */
    public int checkHuTypeForDanZa(int cardIdForZhua, int[] intArrayForHand){
    	int arrayNum = 34;
        int intArray[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
        //赋值检测数组
        for (int i = 0; i < arrayNum; i++) {
            intArray[i] = intArrayForHand[i];
        }
        
        if (intArray[cardIdForZhua] < 2) {
			return -1;
		}else{
			intArray[cardIdForZhua] -= 2;
		}
    	
        for (int b = 0; b < arrayNum; b++) {
        	if (intArray[b] == 4) {
                intArray[b] -= 3;
                //不能是字牌、牌面不能大于7（7万可以，8万和9万不行）
                if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                    intArray[b] -= 1;
                    intArray[b + 1] -= 1;
                    intArray[b + 2] -= 1;
                }else{
                    break;
                }
            }
            else if (intArray[b] == 3){
                intArray[b] -= 3;
            }
            else if (intArray[b] == 2){
                if (b < 27 && b%9 < 7 && intArray[b + 1] > 1 && intArray[b + 2] > 1) {
                    intArray[b] -= 2;
                    intArray[b + 1] -= 2;
                    intArray[b + 2] -= 2;
                }else{
                    break;
                }
            }
            else if (intArray[b] == 1){
                if (b < 27 && b%9 < 7 && intArray[b + 1] > 0 && intArray[b + 2] > 0) {
                    intArray[b] -= 1;
                    intArray[b + 1] -= 1;
                    intArray[b + 2] -= 1;
                }else{
                    break;
                }
            }
            else{
                continue;
            }
		}
        
    	return getCardsNumFormArray(intArray, arrayNum) > 0 ? -1 : hupaiType_danZa;
    }
    
    /**
     * 番型检测----单胡幺九
     * @param cardIdForZhua
     * @param intArrayForHand
     * @return
     */
    public int checkHuTypeForDanHuYaoJiu(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, int[] intArrayForPeng, 
    		int[] intArrayForGangM, int[] intArrayForGangA, int[] intArrayForDNXB, int[] intArrayForZFB){
    	int intArrayForcheck[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    	for (int i = 0; i < intArrayForcheck.length; i++) {
    		intArrayForcheck[i] = intArrayForHand[i];
		}
    	
    	//牌样数
	    int arrayNumForAll = 9*3 + 4 + 3;
	    int intArrayCheck_wanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //万字数组
	    int intArrayCheck_tong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //筒字数组
	    int intArrayCheck_tiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};    //条字数组
	    int intArrayCheck_feng[] = {0, 0, 0, 0, 0, 0, 0};    //风字数组
	    //放入总牌数组中-手牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	    	if(cardIdForZhua == i){
	    		intArrayForcheck[i] -= 1;
	    	}
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForcheck[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForcheck[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForcheck[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForcheck[i];
	        }
	    }
	    //放入总牌数组中-吃牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForChi[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForChi[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForChi[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForChi[i];
	        }
	    }
	    //放入总牌数组中-碰牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForPeng[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForPeng[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForPeng[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForPeng[i];
	        }
	    }
	    //放入总牌数组中-杠牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangM[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangM[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangM[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangM[i];
	        }
	    }
	    //放入总牌数组中-杠牌
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForGangA[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForGangA[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForGangA[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForGangA[i];
	        }
	    }
	    //放入总牌数组中-风牌（东南西北）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForDNXB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForDNXB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForDNXB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForDNXB[i];
	        }
	    }
	    //放入总牌数组中-风牌（中发白）
	    for (int i = 0; i < arrayNumForAll; i++) {
	        if(i < 9*1) {
	            intArrayCheck_wanz[i] += intArrayForZFB[i];
	        }else if(i < 9*2){
	            intArrayCheck_tong[i - 9*1] += intArrayForZFB[i];
	        }else if(i < 9*3){
	            intArrayCheck_tiao[i - 9*2] += intArrayForZFB[i];
	        }else{
	            intArrayCheck_feng[i - 9*3] += intArrayForZFB[i];
	        }
	    }
	    
	    int cardsNumForFeng = getCardsNumFormArray(intArrayCheck_feng, 7);  //牌数-条
    	
	    //是否有幺九
	    boolean isYaoJiu = checkIsYaoJiu(intArrayCheck_wanz, intArrayCheck_tong, intArrayCheck_tiao, cardsNumForFeng);
	    
    	return  isYaoJiu ? -1 : hupaiType_danHuYaoJiu;
    }
    
    /**
     * 番型检测----夹胡
     * @param cardIdForZhua
     * @param intArrayForHand
     * @return
     */
    public int checkHuTypeForJiaHu(int cardIdForZhua, int[] intArrayForHand, int[] intArrayForChi, boolean isHengPaiWai){
    	if (cardIdForZhua > 26) {
			return -1;
		}
    	
    	int intArrayForcheck[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    	for (int i = 0; i < intArrayForcheck.length; i++) {
    		intArrayForcheck[i] = intArrayForHand[i];
		}
    	
    	int cardIndex1 = 0;
    	int cardIndex2 = 0;
    	int cardIndex3 = 0;
    	//抓到的牌为：2万、八万
    	if (cardIdForZhua%9 == 1 || cardIdForZhua%9 == 7) {
    		cardIndex1 = cardIdForZhua - 1;
    		cardIndex2 = cardIdForZhua;
    		cardIndex3 = cardIdForZhua + 1;
    		
			if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
				intArrayForcheck[cardIndex1] -= 1;
				intArrayForcheck[cardIndex2] -= 1;
				intArrayForcheck[cardIndex3] -= 1;
				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
				if (isCanHu) {
					return hupaiType_jiaHu;
				}else{
					intArrayForcheck[cardIndex1] += 1;
					intArrayForcheck[cardIndex2] += 1;
					intArrayForcheck[cardIndex3] += 1;
				}
			}
		}
    	
    	//抓到的牌为：3万、7万
    	if (cardIdForZhua%9 == 2 || cardIdForZhua%9 == 6) {
    		if (cardIdForZhua%9 == 2) {
    			System.out.println("抓到的是3");
    			cardIndex1 = cardIdForZhua - 2;
        		cardIndex2 = cardIdForZhua - 1;
        		cardIndex3 = cardIdForZhua;
    			if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
    				intArrayForcheck[cardIndex1] -= 1;
    				intArrayForcheck[cardIndex2] -= 1;
    				intArrayForcheck[cardIndex3] -= 1;
    				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
    				if (isCanHu) {
    					return hupaiType_37JiaHu;
    				}else{
    					intArrayForcheck[cardIndex1] += 1;
    					intArrayForcheck[cardIndex2] += 1;
    					intArrayForcheck[cardIndex3] += 1;
    				}
    			}
    			
    			cardIndex1 = cardIdForZhua - 1;
        		cardIndex2 = cardIdForZhua;
        		cardIndex3 = cardIdForZhua + 1;
        		if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
        			intArrayForcheck[cardIndex1] -= 1;
        			intArrayForcheck[cardIndex2] -= 1;
        			intArrayForcheck[cardIndex3] -= 1;
    				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
    				if (isCanHu) {
    					return hupaiType_jiaHu;
    				}else{
    					intArrayForcheck[cardIndex1] += 1;
    					intArrayForcheck[cardIndex2] += 1;
    					intArrayForcheck[cardIndex3] += 1;
    				}
    			}
    		}
        	
        	if (cardIdForZhua%9 == 6) {
        		System.out.println("抓到的是7");
        		cardIndex1 = cardIdForZhua;
        		cardIndex2 = cardIdForZhua + 1;
        		cardIndex3 = cardIdForZhua + 2;
    			if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
    				intArrayForcheck[cardIndex1] -= 1;
    				intArrayForcheck[cardIndex2] -= 1;
    				intArrayForcheck[cardIndex3] -= 1;
    				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
    				if (isCanHu) {
    					return hupaiType_37JiaHu;
    				}else{
    					intArrayForcheck[cardIndex1] += 1;
    					intArrayForcheck[cardIndex2] += 1;
    					intArrayForcheck[cardIndex3] += 1;
    				}
    			}
    			
    			cardIndex1 = cardIdForZhua - 1;
        		cardIndex2 = cardIdForZhua;
        		cardIndex3 = cardIdForZhua + 1;
        		if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
        			intArrayForcheck[cardIndex1] -= 1;
        			intArrayForcheck[cardIndex2] -= 1;
        			intArrayForcheck[cardIndex3] -= 1;
    				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
    				if (isCanHu) {
    					return hupaiType_jiaHu;
    				}else{
    					intArrayForcheck[cardIndex1] += 1;
    					intArrayForcheck[cardIndex2] += 1;
    					intArrayForcheck[cardIndex3] += 1;
    				}
    			}
    		}
		}
    	
    	//抓到的牌为：4万、6万
    	if (cardIdForZhua%9 >= 3 && cardIdForZhua%9 <= 5) {
    		System.out.println("抓到的是：" + cardIdForZhua%9 + 1);
    		cardIndex1 = cardIdForZhua - 1;
    		cardIndex2 = cardIdForZhua;
    		cardIndex3 = cardIdForZhua + 1;
			if (intArrayForcheck[cardIndex1] > 0 && intArrayForcheck[cardIndex2] > 0 && intArrayForcheck[cardIndex3] > 0) {
				intArrayForcheck[cardIndex1] -= 1;
				intArrayForcheck[cardIndex2] -= 1;
				intArrayForcheck[cardIndex3] -= 1;
				boolean isCanHu = checkHuPaiElse(cardIdForZhua, intArrayForcheck, intArrayForChi, true, true, true, isHengPaiWai, false, false, false);
				if (isCanHu) {
					return hupaiType_jiaHu;
				}else{
					intArrayForcheck[cardIndex1] += 1;
					intArrayForcheck[cardIndex2] += 1;
					intArrayForcheck[cardIndex3] += 1;
				}
			}
		}
    	
    	return -1;
    }

    public int getScore_win_total(int score_win_hu, int score_win_gang, int score_win_guodan){
    	return score_win_hu + score_win_gang + score_win_guodan;
    }
    
    public int getScore_win_hu(int lost_win_role_1, int lost_win_role_2, int lost_win_role_3){
    	return lost_win_role_1 + lost_win_role_2 + lost_win_role_3;
    }
    
    public int getScore_win_gang(int lost_gang_role_1, int lost_gang_role_2, int lost_gang_role_3){
    	return lost_gang_role_1 + lost_gang_role_2 + lost_gang_role_3;
    }
    
    public int getScore_win_guodan(int lost_guodan_role_1, int lost_guodan_role_2, int lost_guodan_role_3){
    	return lost_guodan_role_1 + lost_guodan_role_2 + lost_guodan_role_3;
    }
    
	public int getScore_lose_hu(boolean isZhuangWin, boolean isZhuangLose,
			boolean isZiMo, boolean isDianPaoByMySelf, boolean isZhangMao,
			boolean isGangShangKaiHua, boolean isHaiDiLaoYue,
			boolean isTingPai, int huType, int fengdingNum, boolean dianPao,
			boolean isKaiMen, boolean liuLei, Integer gangAnum,
			Integer mingGang, int sanQing, int siQing, User u, OneRoom room,
			int siGuiYi, boolean isShouBaYi) {
    	int fanNum = 0;
    	String stringForLose = "------------输分：";
    	//庄家胡牌
    	if (isZhuangWin) {
    		fanNum += 1;
    		stringForLose += "庄家胡牌、";
		}
    	//庄家输牌
    	if (isZhuangLose) {
			fanNum += 1;
			stringForLose += "我是庄家、";
		}
    	//是否长毛
    	if (isZhangMao) {
    		fanNum += 1;
    		stringForLose += "此局长毛、";
		}
    	if(liuLei){//是否流泪
    		fanNum += 1;
    		stringForLose += "流泪、";
    	}
    	//自模胡
    	if (isZiMo) {
			fanNum += 1;
			stringForLose += "自摸胡牌、";
		}
    	//是否杠上开花
    	if (isGangShangKaiHua) {
    		fanNum += 1;
			stringForLose += "杠上开花、";
		}
    	//海底捞月
    	if (isHaiDiLaoYue) {
    		fanNum += 1;
    		stringForLose += "海底捞月、";
		}
    	//门清
    	if(!isKaiMen){
    		fanNum += 1;
    		stringForLose += "门清、";
    	}
    	
    	if(siQing==4){
    		fanNum += 1;
    		stringForLose += "四清、";
    	}else if(sanQing==3){
    		fanNum += 1;
    		stringForLose += "三清、";
    	}

    	//我点炮
    	if (isDianPaoByMySelf) {
    		if(dianPao){
    			fanNum += 2;
    			stringForLose += "我点炮了X2、";
    		}else{
    			fanNum += 1;
    			stringForLose += "我点炮了、";
    		}
		}
    	if(siGuiYi>0){
    		fanNum += siGuiYi;
    		stringForLose += "四归一*"+siGuiYi+"、 ";
    	}
    	if(isShouBaYi){
    		fanNum += 1;
    		stringForLose += "手把一、";
    	}
    	//是不是听后胡
    	if(isTingPai){
    		fanNum += 1;
			stringForLose += "我报听了、";
    	}
    	if(gangAnum>0){
    		fanNum += (gangAnum*2);
    		stringForLose += "暗杠"+gangAnum+"次"+gangAnum*2+"番";
    	}
    	if(mingGang>0){
    		fanNum += mingGang;
    		stringForLose += "明杠"+mingGang+"次"+mingGang+"番";
    	}
    	stringForLose += "胡牌番数：";
    	stringForLose += fanNum;
    	
    	int difen = getIntByHuType(huType, fengdingNum);
    	for (int i = 0; i < fanNum; i++) {
    		difen = difen*2;
		}
    	stringForLose += "胡牌输分：";
    	stringForLose += difen;
    	stringForLose += "。";
    	logger.info("----------------------胡番检测:------------------------");
    	logger.info("胡番检测:"+stringForLose+" RID:"+room.getId()+" userId:"+u.getId());
    	logger.info("----------------------胡番检测:------------------------");
//    	logger.debug("胡番检测:"+stringForLose);
    	if (difen > fengdingNum) {
    		difen = fengdingNum;
		}
    	return -difen;
    }


    public int getScore_lose_gang(int roleNum, int aGangNum_my, int pGangNum_my, int mGangNum_my, int aGangNum_other, int pGangNum_other, int mGangNum_other){
    	int difen = 1;
    	int scroe_gang_my = (aGangNum_my*4*difen + pGangNum_my*2*difen + mGangNum_my*2*difen)*(roleNum - 1);
    	int scroe_gang_other = aGangNum_other*4*difen + pGangNum_other*2*difen + mGangNum_other*2*difen;
    	return scroe_gang_my - scroe_gang_other;
    }
    
    public int getScore_lose_guodan(int roleNum, int score_guodan_my, int score_guodan_other){
    	int difen = 1;
    	int scroe_guodan_my = score_guodan_my*difen*(roleNum - 1);
    	int scroe_guodan_other = score_guodan_other*difen;
    	return scroe_guodan_my - scroe_guodan_other;
    }
    
    public int getIntByHuType(int huType, int fengdingNum){
    	int hufen = 0;
    	switch (huType) {
		case hupaiType_pingHu:
			hufen = 1;
			break;
		case hupaiType_jiaHu:
			hufen = 2;
			break;
		case hupaiType_37JiaHu:
			hufen = 2;
			break;
		case hupaiType_danZa:
			hufen = 2;
			break;
		case hupaiType_danHuYaoJiu:
			hufen = 2;
			break;
		case hupaiType_piaoHun:
			hufen = 8;
			break;
		case hupaiType_piaoChun:
			hufen = 8;
			break;
		case hupaiType_7duiHun:
			hufen = fengdingNum;
			break;
		case hupaiType_7duiChun:
			hufen = fengdingNum;
			break;
		case hupaiType_qingyise:
			hufen = fengdingNum;
			break;
		default:
			hufen = 1;
			break;
		}
    	return hufen;
    }
    
    public String getStringForDescByHu(int huType, boolean isZhuangWin, boolean isZhangMao, boolean isZiMo, boolean isHaiDiLaoYue,boolean isGangShangKaiHua){
    	String stringForDesc = "";
    	if (huType == hupaiType_qingyise || huType == hupaiType_7duiHun || huType == hupaiType_7duiChun) {
    		stringForDesc += getStringByHuType(huType);
		}else{
			stringForDesc += getStringByHuType(huType);
			stringForDesc += getStringByElse(isZhuangWin, isZhangMao, isZiMo, isHaiDiLaoYue,isGangShangKaiHua);
		}
    	return stringForDesc;
    }
   
    public String getStringForDescByGang(int aGangNum_my, int pGangNum_my, int mGangNum_my){
    	String stringForDesc = "";
    	if (aGangNum_my > 0) {
    		stringForDesc += "暗杠*";
    		stringForDesc += aGangNum_my;
    		stringForDesc += " ";
		}
    	
    	if (pGangNum_my > 0) {
    		stringForDesc += "明杠*";
    		stringForDesc += pGangNum_my;
    		stringForDesc += " ";
		}
    	
    	if (mGangNum_my > 0) {
    		stringForDesc += "点杠*";
    		stringForDesc += mGangNum_my;
    		stringForDesc += " ";
		}
    	return stringForDesc;
    }
    
    public String getStringByHuType(int huType){
    	String stringForHuType = "";
    	switch (huType) {
		case hupaiType_pingHu:
			stringForHuType = "平胡 ";
			break;
		case hupaiType_37JiaHu:
			stringForHuType = "夹胡1番";
			break;
		case hupaiType_jiaHu:
			stringForHuType = "夹胡1番";
			break;
		case hupaiType_danZa:
			stringForHuType = "单砸1番";
			break;
		case hupaiType_danHuYaoJiu:
			stringForHuType = "单胡幺九1番";
			break;
		case hupaiType_piaoHun:
			stringForHuType = "混飘3番";
			break;
		case hupaiType_piaoChun:
			stringForHuType = "纯飘3番";
			break;
		case hupaiType_7duiHun:
			stringForHuType = "混七对封顶";
			break;
		case hupaiType_7duiChun:
			stringForHuType = "纯七对封顶";
			break;
		case hupaiType_qingyise:
			stringForHuType = "清一色封顶";
			break;
		default:
			stringForHuType = "荒庄";
			break;
		}
    	return stringForHuType;
    }
    
    public String getStringByElse(boolean isZhuangWin, boolean isZhangMao, boolean isZiMo, boolean isHaiDiLaoYue,boolean isGangShangKaiHua){
    	String stringForFan = "";
    	
    	if (isZhuangWin) {
    		stringForFan += " 庄1番 ";
		}
    	
    	if (isZhangMao) {
    		stringForFan += " 长毛1番 ";
		}
    	
    	if (isZiMo) {
    		stringForFan += " 自摸1番 ";
		}
    	
    	if (isHaiDiLaoYue) {
    		stringForFan += " 海底捞月1番 ";
		}
    	
    	if (isGangShangKaiHua) {
    		stringForFan += " 杠上开花1番 ";
    	}
    	
    	return stringForFan;
    }
    
    /**
     *  获取卡牌数量
     *
     *  @param intArray 检测数组
     *  @param arrayNum 数组长度
     *
     *  @return 返回卡牌数量
     */
    public int getCardsNumFormArray(int[] intArray, int arrayNum){
        int cardsNum = 0;
        for (int i = 0; i < arrayNum; i++) {
            cardsNum += intArray[i];
        }
        return cardsNum;
    }

    /**
     *  获取牌内容（log输出用）
     *
     *  @param cardId 牌id
     *
     *  @return 返回牌内容
     */
    public String getNameFromCardId(int cardId){
    	String stringName = "";
        switch (cardId) {
            case 0:
                stringName = "一万";
                break;
            case 1:
                stringName = "二万";
                break;
            case 2:
                stringName = "三万";
                break;
            case 3:
                stringName = "四万";
                break;
            case 4:
                stringName = "五万";
                break;
            case 5:
                stringName = "六万";
                break;
            case 6:
                stringName = "七万";
                break;
            case 7:
                stringName = "八万";
                break;
            case 8:
                stringName = "九万";
                break;
            case 9:
                stringName = "一筒";
                break;
            case 10:
                stringName = "二筒";
                break;
            case 11:
                stringName = "三筒";
                break;
            case 12:
                stringName = "四筒";
                break;
            case 13:
                stringName = "五筒";
                break;
            case 14:
                stringName = "六筒";
                break;
            case 15:
                stringName = "七筒";
                break;
            case 16:
                stringName = "八筒";
                break;
            case 17:
                stringName = "九筒";
                break;
            case 18:
                stringName = "一条";
                break;
            case 19:
                stringName = "二条";
                break;
            case 20:
                stringName = "三条";
                break;
            case 21:
                stringName = "四条";
                break;
            case 22:
                stringName = "五条";
                break;
            case 23:
                stringName = "六条";
                break;
            case 24:
                stringName = "七条";
                break;
            case 25:
                stringName = "八条";
                break;
            case 26:
                stringName = "九条";
                break;
            case 27:
                stringName = "红中";
                break;
            case 28:
                stringName = "?财";
                break;
            case 29:
                stringName = "白板";
                break;
            case 30:
                stringName = "东风";
                break;
            case 31:
                stringName = "南风";
                break;
            case 32:
                stringName = "西风";
                break;
            case 33:
                stringName = "北风";
                break;
            default:
                stringName = "error";
                break;
        }
        return stringName;
    }
}