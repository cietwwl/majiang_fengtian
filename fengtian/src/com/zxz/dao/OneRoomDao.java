package com.zxz.dao;

import java.util.HashMap;

import org.junit.Test;

import com.zxz.algorithm.Room;
import com.zxz.domain.OneRoom;


public class OneRoomDao extends BaseDao{
	
	
	public static int roomNumber = 10000;
	static OneRoomDao oneRoomDao;
	
	private OneRoomDao() {
	}
	
	public static OneRoomDao getInstance(){
		if(oneRoomDao!=null){
			return oneRoomDao;
		}else{
			synchronized (OneRoomDao.class) {
				oneRoomDao = new OneRoomDao();
				return oneRoomDao;
			}
		}
	}
	
	/**锟斤拷锟揭伙拷锟斤拷锟斤拷锟�
	 * @param room
	 * @return
	 */
	public int saveRoom(OneRoom room){
//		insert("OneRoom.save", room);
//		return room.getId();
		return (int)super.queryForObject("OneRoom.createOneRoom2",room);
	}
	
	
	public static void main(String[] args) {
//		OneRoom oneRoom = new OneRoom();
//		oneRoom.setTotal(8);
//		oneRoom.setZhama(3);
//		OneRoomDao oneRoomDao = new OneRoomDao();
//		oneRoomDao.saveRoom(oneRoom);
		OneRoomDao oneRoomDao = new OneRoomDao();
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", 100589);//锟斤拷锟斤拷锟�
		map.put("userId", 19);//锟矫伙拷id
		map.put("totalGame", 16);//锟杰撅拷锟斤拷
		map.put("type", 1);//锟斤拷锟斤拷 1锟斤拷锟斤拷值0,锟斤拷员锟斤拷值 3
		map.put("total",0);//锟斤拷值锟侥凤拷锟斤拷锟斤拷锟斤拷
		int userCard = oneRoomDao.userConsumeCard(map);
		System.out.println("锟斤拷剩:"+userCard);
	}
	
	
	/**锟矫伙拷锟斤拷锟窖凤拷锟斤拷 
	 * @param map
	 * @return
	 */
	public int userConsumeCard(HashMap<String, Object> map){
		return (int)super.queryForObject("OneRoom.userConsumeRoomCard", map);
	}
	
	@Test
	public void testConsumeCard(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("roomId", 100589);//锟斤拷锟斤拷锟�
		map.put("userId", 19);//锟矫伙拷id
		map.put("totalGame", 16);//锟杰撅拷锟斤拷
		map.put("type", 1);//锟斤拷锟斤拷 1锟斤拷锟斤拷值0,锟斤拷员锟斤拷值 3
		map.put("total",0);//锟斤拷值锟侥凤拷锟斤拷锟斤拷锟斤拷
		int userConsumeCard = userConsumeCard(map);
		System.out.println("剩锟洁房锟斤拷锟斤拷:"+userConsumeCard);
	}
	
	
}
