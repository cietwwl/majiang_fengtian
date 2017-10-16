package com.zxz.utils;

import com.zxz.config.utils.Config;

public class RoomNumberUtil {

	private static Config config = Config.getConfig();
	
	/**得到一个房间号
	 * @return
	 */
	public synchronized static int getOneRoomNumber(){
		int interval = config.getInterval();
		int random = ((int)(Math.random()*10)+1)+interval;
		config.setInterval(random);
		return interval;
	}
	
}
