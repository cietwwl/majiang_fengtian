package io.netty.channel;

import io.netty.util.AttributeKey;

import java.util.List;

import org.json.JSONObject;

import com.zxz.controller.RoomManager;
import com.zxz.dao.UserDao;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.utils.TypeUtils;

public class CustomerInboundHandler extends ChannelInboundHandlerAdapter {
	/**
	 * 客户端打电话 关闭游戏 会触发该方法
	 */
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//channel失效处理,客户端下线或者强制退出等任何情况都触发这个方法
		//向其他玩家发送该用户掉线
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		if(roomId!=null){
			OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
			UserDao userDao = UserDao.getInstance();
			if(oneRoom!=null){
				User modifyUser = new User();
				modifyUser.setId(user.getId());
				modifyUser.setRoomId(roomId);
				userDao.modifyUser(user.getId(),roomId);//记录下用户的房间号
			}
			JSONObject dropLine = new JSONObject();
			dropLine.put("m", "drop");
			if(user.getDirection()!=null){
				dropLine.put("userDir", TypeUtils.getUserDir(user.getDirection()));
			}else{
				dropLine.put("userDir", 0);
			}
			dropLine.put("type", 1);
			//通知其他用户改用户掉线
			if(oneRoom!=null){
				List<User> userList = oneRoom.getUserList();
				for(int i=0;i<userList.size();i++){
					User us = userList.get(i);
					if(us.getId()!=user.getId()){
						us.getIoSession().write(dropLine.toString());
					}else{
						us.setDropLine(true);
					}
				}
			}
			
		}
		
		super.channelInactive(ctx);
    }
}
