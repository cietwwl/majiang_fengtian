package com.zxz.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test3 {
	public static void main(String[] args) {
		List<Integer> arrayList = new ArrayList<>();
		arrayList.add(102);
		arrayList.add(101);
		arrayList.add(100);
		arrayList.add(110);
		arrayList.add(113);
		arrayList.add(108);
		arrayList.add(105);
		arrayList.add(106);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Integer integer : arrayList) {
			Integer card = integer/4;
			if(map.containsKey(card)){//判断是否已经有该数值，如有，则将次数加1
                map.put(card, map.get(card).intValue() + 1);
            }else{
                map.put(card, 1);
            }
		}
		System.out.println(map);
	}
	/*public static boolean IsCanHU(List<Integer> mah, Integer ID)
	{
	    List<Integer> pais = new ArrayList<Integer>(mah);
	 
	    pais.add(ID);
	    //只有两张牌
	    if (pais.size() == 2)
	    {
	        return pais.get(0) == pais.get(1);
	    }
	 
	    //先排序
	    Collections.sort(pais); 
	    //依据牌的顺序从左到右依次分出将牌
	    for (int i = 0; i < pais.size(); i++)
	    {
	        List<Integer> paiT = new ArrayList<Integer>(pais);
	        List<Integer> ds = pais.findAll(delegate (Integer d)
	        {
	            return pais.get(i) == d;
	        });
	 
	        //判断是否能做将牌
	        if (ds.size() >= 2)
	        {
	            //移除两张将牌
	            paiT.remove(pais.get(i));
	            paiT.remove(pais.get(i));
	 
	            //避免重复运算 将光标移到其他牌上
	            i += ds.size();
	 
	            if (HuPaiPanDin(paiT))
	            {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	 
	private static boolean HuPaiPanDin(List<Integer> mahs)
	{
	    if (mahs.size() == 0)
	    {
	        return true;
	    }
	 
	    List<Integer> fs = mahs.findAll(delegate (Integer a){
	    	return mahs.get(0) == a;
	    	
	    });
	 
	    //组成克子
	    if (fs.size() == 3)
	    {
	        mahs.remove(mahs.get(0));
	        mahs.remove(mahs.get(0));
	        mahs.remove(mahs.get(0));
	 
	        return HuPaiPanDin(mahs);
	    }
	    else
	    { //组成顺子
	        if (mahs.contains(mahs.get(0) + 1) && mahs.contains(mahs.get(0) + 2))
	        {
	            mahs.remove(mahs.get(0) + 2);
	            mahs.remove(mahs.get(0) + 1);
	            mahs.remove(mahs.get(0));
	 
	            return HuPaiPanDin(mahs);
	        }
	        return false;
	    }
	}
*/}
