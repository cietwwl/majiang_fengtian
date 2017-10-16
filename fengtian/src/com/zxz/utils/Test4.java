package com.zxz.utils;


public class Test4 {

	public static void main(String[] args) {

		int intArrayForHand[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
		
		int intArrayForPeng[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
		
		int intArrayForGang[] = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0};
		
		int hunId = 101;
		
		//0,52,53,72,80,96,100,104,120,124,125,130,132,133   0
//				int intArrayForCard[] = {
//				0,1,53,54,55,96,
//				100,104,108,112,116,
//				120,124,130};		
				int intArrayForCard[] = {101,117,118,122,124,127,133,134};		
		for (int i = 0; i < intArrayForCard.length; i++) {
			intArrayForHand[intArrayForCard[i]/4]++;
		}
		
		Test4 test = new Test4();
		
		long curTime = System.currentTimeMillis();
		
		String stringForCheck = "";
		for (int i = 0; i < 34; i++) {
	        if (intArrayForHand[i] > 0) {
	            stringForCheck += test.getNameFromCardId(i);
	            stringForCheck += "(";
	            stringForCheck += intArrayForHand[i];
	            stringForCheck += ").";
	        }
	    }
		
		boolean isCanHu = test.checkHuPai(intArrayForHand, intArrayForPeng, intArrayForGang, hunId/4, true, true);
		
	    System.out.println("听牌提示检测时间：" + (System.currentTimeMillis() - curTime));
	    System.out.println("手牌(" + test.getCardsNumFormArray(intArrayForHand, 34) + "):" + stringForCheck);
	    if(isCanHu){
	    	System.out.println("此牌型可以胡-_-");
	    }else{
	    	System.out.println("此牌型不能胡_-_");
	    }
	}
	
	public int needHunMin = 0;
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
    public boolean checkHuPai(int[] intArrayForHand, int[] intArrayForPeng, int[] intArrayForGang, int cardIdForHun,
                         boolean isLogForHu, boolean isLogForCheckPu){
        
        int arrayNumForAll = 9*3 + 4 + 3;
        int intArrayForDuanMen_wanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForDuanMen_tong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForDuanMen_tiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        
        for (int i = 0; i < arrayNumForAll; i++) {
            if (i == cardIdForHun) {
                
            }else if(i < 9*1) {
                intArrayForDuanMen_wanz[i] += intArrayForHand[i];
            }else if(i < 9*2){
                intArrayForDuanMen_tong[i - 9*1] += intArrayForHand[i];
            }else if(i < 9*3){
                intArrayForDuanMen_tiao[i - 9*2] += intArrayForHand[i];
            }
        }
        for (int i = 0; i < arrayNumForAll; i++) {
            if (i == cardIdForHun) {
                
            }else if(i < 9*1) {
                intArrayForDuanMen_wanz[i] += intArrayForPeng[i];
            }else if(i < 9*2){
                intArrayForDuanMen_tong[i - 9*1] += intArrayForPeng[i];
            }else if(i < 9*3){
                intArrayForDuanMen_tiao[i - 9*2] += intArrayForPeng[i];
            }
        }
        for (int i = 0; i < arrayNumForAll; i++) {
            if (i == cardIdForHun) {
                
            }else if(i < 9*1) {
                intArrayForDuanMen_wanz[i] += intArrayForGang[i];
            }else if(i < 9*2){
                intArrayForDuanMen_tong[i - 9*1] += intArrayForGang[i];
            }else if(i < 9*3){
                intArrayForDuanMen_tiao[i - 9*2] += intArrayForGang[i];
            }
        }
        
        int wanzNumForDuanMen = getCardsNumFormArray(intArrayForDuanMen_wanz, 9);     //牌数-万
        int tongNumForDuanMen = getCardsNumFormArray(intArrayForDuanMen_tong, 9);     //牌数-筒
        int tiaoNumForDuanMen = getCardsNumFormArray(intArrayForDuanMen_tiao, 9);     //牌数-条
        
        if (wanzNumForDuanMen <= 0 || tongNumForDuanMen <= 0 || tiaoNumForDuanMen <= 0) {
            //断门可以胡牌
        }else{
            if (isLogForHu) {
                System.out.println("没有断门---不能胡牌");
            }
            return false;
        }
        
        int arrayNumTotal = getCardsNumFormArray(intArrayForHand, arrayNumForAll);
        if (isLogForHu) {
       	 System.out.println("检测胡牌     总牌数：" + arrayNumTotal);
        }
        
        //非七对胡牌
        int intArrayForWanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForTong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForTiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForHeiF[] = {0, 0, 0, 0};
        int intArrayForBaiF[] = {0, 0, 0};
        int cardsNumForHun = 0;
        
        //从数组中拆分类型
        int cardIndex = 0;
        for (int i = 0; i < arrayNumForAll; i++) {
            if (i == cardIdForHun) {
                cardsNumForHun += intArrayForHand[i];
            }else if(i < 9*1) {
                cardIndex = i;
                intArrayForWanz[cardIndex] += intArrayForHand[i];
            }else if(i < 9*2){
                cardIndex = i - 9*1;
                intArrayForTong[cardIndex] += intArrayForHand[i];
            }else if(i < 9*3){
                cardIndex = i - 9*2;
                intArrayForTiao[cardIndex] += intArrayForHand[i];
            }else if(i < 9*3 + 3){
                cardIndex = i - 9*3;
                intArrayForBaiF[cardIndex] += intArrayForHand[i];
            }else if(i < 9*3 + 3 + 4){
                cardIndex = i - 9*3 - 3;
                intArrayForHeiF[cardIndex] += intArrayForHand[i];
            }
        }
        
        int cardsNumForWanz = getCardsNumFormArray(intArrayForWanz, 9);     //牌数-万
        int cardsNumForTong = getCardsNumFormArray(intArrayForTong, 9);     //牌数-筒
        int cardsNumForTiao = getCardsNumFormArray(intArrayForTiao, 9);     //牌数-条
        int cardsNumForHeiF = getCardsNumFormArray(intArrayForHeiF, 4);     //牌数-黑风
        int cardsNumForBaiF = getCardsNumFormArray(intArrayForBaiF, 3);     //牌数-白风
        if (isLogForHu) {
       	 System.out.println("牌数     cardsNumForWanz:" + cardsNumForWanz);
       	 System.out.println("牌数     cardsNumForTong:" + cardsNumForTong);
       	 System.out.println("牌数     cardsNumForTiao:" + cardsNumForTiao);
       	 System.out.println("牌数     cardsNumForHeiF:" + cardsNumForHeiF);
       	 System.out.println("牌数     cardsNumForBaiF:" + cardsNumForBaiF);
       	 System.out.println("牌数     cardsNumForHun:" + cardsNumForHun);
        }
        
        //七对胡牌
        if (cardsHuFor7(intArrayForHand, arrayNumTotal, cardIdForHun)) {
            if (isLogForHu) {
           	 System.out.println("检测胡牌     七对胡牌");
            }
            return true;
        }
        
        int needHunNumForWanz = 0;
        int needHunNumForTong = 0;
        int needHunNumForTiao = 0;
        int needHunNumForHeiF = 0;
        int needHunNumForBaiF = 0;
        int needHunTotal = 0;
        
        //万字满足3n模式，需要的数量
        needHunMin = 4;
        getNeedHunNumToBePu(intArrayForWanz, 9, 0, isLogForCheckPu);
        needHunNumForWanz = needHunMin;
        
        //饼字满足3n模式，需要的数量
        needHunMin = 4;
        getNeedHunNumToBePu(intArrayForTong, 9, 0, isLogForCheckPu);
        needHunNumForTong = needHunMin;
        
        //条字满足3n模式，需要的数量
        needHunMin = 4;
        getNeedHunNumToBePu(intArrayForTiao, 9, 0, isLogForCheckPu);
        needHunNumForTiao = needHunMin;
        
        //白风满足3n模式，需要的数量
        needHunMin = 4;
        getNeedHunNumToBePu(intArrayForBaiF, 3, 0, isLogForCheckPu);
        needHunNumForBaiF = needHunMin;
     
        //黑风满足3n模式，需要的数量
        needHunMin = 4;
        getNeedHunNumToBePu(intArrayForHeiF, 4, 0, isLogForCheckPu);
        needHunNumForHeiF = needHunMin;
        
        if (isLogForHu) {
       	 System.out.println("需要混数     needHunNumForWanz:" + needHunNumForWanz);
       	 System.out.println("需要混数     needHunNumForTong:" + needHunNumForTong);
       	 System.out.println("需要混数     needHunNumForTiao:" + needHunNumForTiao);
       	 System.out.println("需要混数     needHunNumForHeiF:" + needHunNumForHeiF);
       	 System.out.println("需要混数     needHunNumForBaiF:" + needHunNumForBaiF);
        }
        
        //对子所在位置-万字
        needHunTotal = needHunNumForTong + needHunNumForTiao + needHunNumForHeiF + needHunNumForBaiF;
        if (needHunTotal <= cardsNumForHun)
        {
            if (isLogForHu) {
           	 System.out.println("##########  ##########  ##########  对子所在位置-万字");
            }
            
            if (cardsCanHu(intArrayForWanz, 9, cardsNumForHun - needHunTotal, isLogForCheckPu))
            {
                return true;
            }
        }
        
        //对子所在位置-筒字
        needHunTotal = needHunNumForWanz + needHunNumForTiao + needHunNumForHeiF + needHunNumForBaiF;
        if (needHunTotal <= cardsNumForHun)
        {
            if (isLogForHu) {
           	 System.out.println("##########  ##########  ##########  对子所在位置-筒字");
            }
            
            if (cardsCanHu(intArrayForTong, 9, cardsNumForHun - needHunTotal, isLogForCheckPu))
            {
                return true;
            }
        }
        
        //对子所在位置-条字
        needHunTotal = needHunNumForWanz + needHunNumForTong + needHunNumForHeiF + needHunNumForBaiF;
        if (needHunTotal <= cardsNumForHun)
        {
            if (isLogForHu) {
           	 System.out.println("##########  ##########  ##########  对子所在位置-条字");
            }
            if (cardsCanHu(intArrayForTiao, 9, cardsNumForHun - needHunTotal, isLogForCheckPu))
            {
                return true;
            }
        }
        
        //对子所在位置-白风
        needHunTotal = needHunNumForWanz + needHunNumForTong + needHunNumForTiao + needHunNumForHeiF;
        if (needHunTotal <= cardsNumForHun)
        {
            if (isLogForHu) {
           	 System.out.println("##########  ##########  ##########  对子所在位置-白风");
            }
            if (cardsCanHu(intArrayForBaiF, 3, cardsNumForHun - needHunTotal, isLogForCheckPu))
            {
                return true;
            }
        }
        
        //对子所在位置-黑风
        needHunTotal = needHunNumForWanz + needHunNumForTong + needHunNumForTiao + needHunNumForBaiF;
        if (needHunTotal <= cardsNumForHun)
        {
            if (isLogForHu) {
           	 System.out.println("##########  ##########  ##########  对子所在位置-黑风");
            }
            if (cardsCanHu(intArrayForHeiF, 4, cardsNumForHun - needHunTotal, isLogForCheckPu))
            {
                return true;
            }
        }
        
        return false;
    }

    public boolean checkIsDuanMen(int[] intArray, int cardIdForHun){
        boolean isDuanMen = false;
        
        //断门
        int intArrayForWanz[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForTong[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int intArrayForTiao[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        
        //从数组中拆分类型
        int cardIndex = 0;
        for (int i = 0; i < 34; i++) {
            if (i == cardIdForHun) {
            	
            }else if(i < 9*1) {
                cardIndex = i;
                intArrayForWanz[cardIndex] += intArray[i];
            }else if(i < 9*2){
                cardIndex = i - 9*1;
                intArrayForTong[cardIndex] += intArray[i];
            }else if(i < 9*3){
                cardIndex = i - 9*2;
                intArrayForTiao[cardIndex] += intArray[i];
            }
        }
        
        int cardsNumForWanz = getCardsNumFormArray(intArrayForWanz, 9);     //牌数-万
        int cardsNumForTong = getCardsNumFormArray(intArrayForTong, 9);     //牌数-筒
        int cardsNumForTiao = getCardsNumFormArray(intArrayForTiao, 9);     //牌数-条
        
        if (cardsNumForWanz <= 0 || cardsNumForTong <= 0 || cardsNumForTiao <= 0) {
            isDuanMen = false;
        }else{
            isDuanMen = true;
        }
        
        return isDuanMen;
    }

    /**
     *  是否可以胡-七对
     *
     *  @param intArray     检测数组
     *  @param cardIdForHun 混id
     *
     *  @return 返回是否可以胡
     */
    public boolean cardsHuFor7(int[] intArray, int arrayNum, int cardIdForHun){
        
        boolean isCanHu = false;
        
        if (arrayNum < 14) {
            isCanHu = false;
        }else{
            int arrayForCheck[] = {
            		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    				0, 0, 0, 0};
            int cardNumForHun = 0;
            for (int i = 0; i < 34; i++) {
                if (i == cardIdForHun) {
                    cardNumForHun += intArray[i];
                }else{
                    arrayForCheck[i] += intArray[i];
                }
            }
            
            int needHunMinCur = 0;
            for (int i = 0; i < 34; i++) {
                if (arrayForCheck[i] == 1 || arrayForCheck[i] == 3) {
                    needHunMinCur += 1;
                }
            }
            
            if (needHunMinCur <= cardNumForHun) {
                isCanHu = true;
            }else{
                isCanHu = false;
            }
        }
        
        return isCanHu;
    }

    /**
     *  计算出整扑需要的最少混
     *
     *  @param intArray        检测数组
     *  @param arrayNum        数组长度
     *  @param needNum         混数量
     *  @param isLogForCheckPu 是否输出log
     */
    public void getNeedHunNumToBePu(int[] intArray, int arrayNum, int needNum, boolean isLogForCheckPu){
        //整扑需要的红中数量小于
        if (needNum > needHunMin){
            needHunMin = needNum;
            return;
        }
        
        int cardNum = 0;
        for (int i = 0; i < arrayNum; i++) {
            cardNum += intArray[i];
        }
        
        if (cardNum == 0) {
            needHunMin = Math.min(needNum, needHunMin);
            return;
        }
        else if (cardNum == 1){
            needHunMin = Math.min(needNum + 2, needHunMin);
            return;
        }
        else if (cardNum == 2){
            int cardId1 = getCardIdFromArray(intArray, arrayNum);
            intArray[cardId1] -= 1;
            int cardId2 = getCardIdFromArray(intArray, arrayNum);
            intArray[cardId1] += 1;
            
            if (cardId2 - cardId1 < 3) {
                needHunMin = Math.min(needNum + 1, needHunMin);
            }else{
                needHunMin = Math.min(needNum + 4, needHunMin);
            }
            return;
        }
        else{
        	String stringForPu = "";
            int cardId1 = getCardIdFromArray(intArray, arrayNum);
            //第一个自己一扑
            if (needNum + 2 < needHunMin) {
            	
            	if (isLogForCheckPu) {
                    stringForPu = "第一个自己一扑---扑出去:";
                    stringForPu += getNameFromCardId(cardId1);
                    System.out.println(stringForPu);
                }
            	
                intArray[cardId1] -= 1;
                getNeedHunNumToBePu(intArray, arrayNum, needNum + 2, isLogForCheckPu);
                intArray[cardId1] += 1;
                
                if (isLogForCheckPu) {
                    stringForPu = "第一个自己一扑---拿回来:";
                    stringForPu += getNameFromCardId(cardId1);
                    System.out.println(stringForPu);
                }
            }
            
            //第一个和其他一个一扑
            if (needNum + 1 < needHunMin) {
                //11
                if (intArray[cardId1] > 1) {
                	
                	if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---扑出去:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1);
                        System.out.println(stringForPu);
                    }
                	
                    intArray[cardId1] -= 2;
                    getNeedHunNumToBePu(intArray, arrayNum, needNum + 1, isLogForCheckPu);
                    intArray[cardId1] += 2;
                    
                    if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---拿回來:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1);
                        System.out.println(stringForPu);
                    }
                }
                //12
                int nextIndex = cardId1 + 1;
                if (nextIndex < arrayNum && intArray[nextIndex] > 0) {
                	
                	if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---扑出去:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(nextIndex);
                        System.out.println(stringForPu);
                    }
                	
                    intArray[cardId1] -= 1;
                    intArray[nextIndex] -= 1;
                    getNeedHunNumToBePu(intArray, arrayNum, needNum + 1, isLogForCheckPu);
                    intArray[cardId1] += 1;
                    intArray[nextIndex] += 1;
                    
                    if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---拿回来:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(nextIndex);
                        System.out.println(stringForPu);
                    }
                }
                //13
                nextIndex = cardId1 + 2;
                if (nextIndex < arrayNum && intArray[nextIndex] > 0) {
                	
                	if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---扑出去:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(nextIndex);
                        System.out.println(stringForPu);
                    }
                	
                    intArray[cardId1] -= 1;
                    intArray[nextIndex] -= 1;
                    getNeedHunNumToBePu(intArray, arrayNum, needNum + 1, isLogForCheckPu);
                    intArray[cardId1] += 1;
                    intArray[nextIndex] += 1;
                    
                    if (isLogForCheckPu) {
                        stringForPu = "第一个和其他一个一扑---拿回来:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(nextIndex);
                        System.out.println(stringForPu);
                    }
                    
                }
                
                //黑风
                if (arrayNum == 4) {
                    //14
                    nextIndex = cardId1 + 3;
                    if (nextIndex < arrayNum && intArray[nextIndex] > 0) {
                    	
                    	if (isLogForCheckPu) {
                            stringForPu = "第一个和其他一个一扑---扑出去:";
                            stringForPu += getNameFromCardId(cardId1);
                            stringForPu += "、";
                            stringForPu += getNameFromCardId(nextIndex);
                            System.out.println(stringForPu);
                        }
                    	
                        intArray[cardId1] -= 1;
                        intArray[nextIndex] -= 1;
                        getNeedHunNumToBePu(intArray, arrayNum, needNum + 1, isLogForCheckPu);
                        intArray[cardId1] += 1;
                        intArray[nextIndex] += 1;
                        
                        if (isLogForCheckPu) {
                            stringForPu = "第一个和其他一个一扑---拿回来:";
                            stringForPu += getNameFromCardId(cardId1);
                            stringForPu += "、";
                            stringForPu += getNameFromCardId(nextIndex);
                            System.out.println(stringForPu);
                        }
                    }
                }
            }
            
            //第一个和其他两个一扑
            //111
            if (intArray[cardId1] > 2) {
            	
            	if (isLogForCheckPu) {
                    stringForPu = "第一个和其他两个一扑---扑出去:";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1);
                    System.out.println(stringForPu);
                }
            	
                intArray[cardId1] -= 3;
                getNeedHunNumToBePu(intArray, arrayNum, needNum, isLogForCheckPu);
                intArray[cardId1] += 3;
                
                if (isLogForCheckPu) {
                    stringForPu = "第一个和其他两个一扑---拿回来:";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1);
                    System.out.println(stringForPu);
                }
            }
            //123
            if (cardId1 + 2 < arrayNum && intArray[cardId1 + 1] > 0 && intArray[cardId1 + 2] > 0 ) {
            	
            	if (isLogForCheckPu) {
                    stringForPu = "第一个和其他两个一扑---扑出去:";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1 + 1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1 + 2);
                    System.out.println(stringForPu);
                }
            	
                intArray[cardId1] -= 1;
                intArray[cardId1 + 1] -= 1;
                intArray[cardId1 + 2] -= 1;
                getNeedHunNumToBePu(intArray, arrayNum, needNum, isLogForCheckPu);
                intArray[cardId1] += 1;
                intArray[cardId1 + 1] += 1;
                intArray[cardId1 + 2] += 1;
                
                if (isLogForCheckPu) {
                    stringForPu = "第一个和其他两个一扑---拿回来:";
                    stringForPu += getNameFromCardId(cardId1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1 + 1);
                    stringForPu += "、";
                    stringForPu += getNameFromCardId(cardId1 + 2);
                    System.out.println(stringForPu);
                }
            }
            
            //黑风
            if (arrayNum == 4) {
                //124
                if (cardId1 + 3 < arrayNum && intArray[cardId1 + 1] > 0 && intArray[cardId1 + 3] > 0 ) {
                	
                	if (isLogForCheckPu) {
                        stringForPu = "第一个和其他两个一扑---扑出去:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 3);
                        System.out.println(stringForPu);
                    }
                	
                    intArray[cardId1] -= 1;
                    intArray[cardId1 + 1] -= 1;
                    intArray[cardId1 + 3] -= 1;
                    getNeedHunNumToBePu(intArray, arrayNum, needNum, isLogForCheckPu);
                    intArray[cardId1] += 1;
                    intArray[cardId1 + 1] += 1;
                    intArray[cardId1 + 3] += 1;
                    
                    if (isLogForCheckPu) {
                        stringForPu = "第一个和其他两个一扑---拿回来:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 3);
                        System.out.println(stringForPu);
                    }
                }
                //134
                if (cardId1 + 3 < arrayNum && intArray[cardId1 + 2] > 0  && intArray[cardId1 + 3] > 0) {
                	
                	if (isLogForCheckPu) {
                        stringForPu = "第一个和其他两个一扑---扑出去:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 2);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 3);
                        System.out.println(stringForPu);
                    }
                	
                    intArray[cardId1] -= 1;
                    intArray[cardId1 + 2] -= 1;
                    intArray[cardId1 + 3] -= 1;
                    getNeedHunNumToBePu(intArray, arrayNum, needNum, isLogForCheckPu);
                    intArray[cardId1] += 1;
                    intArray[cardId1 + 2] += 1;
                    intArray[cardId1 + 3] += 1;
                    
                    if (isLogForCheckPu) {
                        stringForPu = "第一个和其他两个一扑---拿回来:";
                        stringForPu += getNameFromCardId(cardId1);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 2);
                        stringForPu += "、";
                        stringForPu += getNameFromCardId(cardId1 + 3);
                        System.out.println(stringForPu);
                    }
                }
            }
        }
    }

    /**
     *  检测是否可以胡牌
     *
     *  @param intArray        检测数组
     *  @param arrayNum        数组长度
     *  @param elseHunNum      剩余混数量
     *  @param isLogForCheckPu 是否输出log
     *
     *  @return 返回是否可以胡牌
     */
    public boolean cardsCanHu(int[] intArray, int arrayNum, int elseHunNum, boolean isLogForCheckPu){
        int cardNum = getCardsNumFormArray(intArray, arrayNum);
        
        if (cardNum <= 0)
        {
            if (elseHunNum < 2){
                return false;
            }else{
                return true;
            }
        }
        
        for (int i = 0; i < arrayNum; i++) {
            int cardsNum = intArray[i];
            if (cardsNum == 0) {
                if (elseHunNum >= 2) {
                    elseHunNum -= 2;
                    needHunMin = 4;
                    getNeedHunNumToBePu(intArray, arrayNum, 0, isLogForCheckPu);
                    if (needHunMin <= elseHunNum) {
                        return true;
                    }
                    elseHunNum += 2;
                }
            }else if (cardsNum == 1){
                if (elseHunNum >= 1) {
                    elseHunNum -= 1;
                    intArray[i] -= 1;
                    needHunMin = 4;
                    getNeedHunNumToBePu(intArray, arrayNum, 0, isLogForCheckPu);
                    if (needHunMin <= elseHunNum) {
                        return true;
                    }
                    intArray[i] += 1;
                    elseHunNum += 1;
                }
            }else{
                intArray[i] -= 2;
                needHunMin = 4;
                getNeedHunNumToBePu(intArray, arrayNum, 0, isLogForCheckPu);
                if (needHunMin <= elseHunNum) {
                    return true;
                }
                intArray[i] += 2;
            }
        }
        return false;
    }

    /**
     *  获取数组中存在的牌id
     *
     *  @param intArray 检测数组
     *  @param arrayNum 数组长度
     *
     *  @return 返回牌id
     */
    public int getCardIdFromArray(int[] intArray, int arrayNum){
        int cardId = 0;
        for (int i = 0; i < arrayNum; i++) {
            if (intArray[i] > 0) {
                cardId = i;
                break;
            }
        }
        return cardId;
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
                stringName = "發财";
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