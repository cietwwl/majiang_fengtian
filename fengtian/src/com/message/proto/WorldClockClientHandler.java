/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.message.proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.worldclock.WorldClockProtocol.Continent;
import io.netty.example.worldclock.WorldClockProtocol.LocalTime;
import io.netty.example.worldclock.WorldClockProtocol.LocalTimes;
import io.netty.example.worldclock.WorldClockProtocol.Location;
import io.netty.example.worldclock.WorldClockProtocol.Locations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import com.message.proto.Message.command;

public class WorldClockClientHandler extends SimpleChannelInboundHandler<command> {

	private static final Pattern DELIM = Pattern.compile("/");

	// Stateful properties
	private volatile Channel channel;
	private final BlockingQueue<command> answer = new LinkedBlockingQueue<command>();

	public WorldClockClientHandler() {
		super(false);
	}

	public String getLocalTimes(Collection<String> cities) {
		command.Builder builder = command.newBuilder();
		builder.setName("我是客户端！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		channel.writeAndFlush(builder.build());
		command command;
		boolean interrupted = false;
		for (;;) {
			try {
				command = answer.take();
				break;
			} catch (InterruptedException ignore) {
				interrupted = true;
			}
		}
		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		List<String> result = new ArrayList<String>();
		String name = command.getName();
		return name;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		channel = ctx.channel();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, command msg) throws Exception {
		System.out.println("服务器发送的数据是:"+msg);
		System.out.println("服务器发送的数据是:"+msg.getName());
		answer.add(msg);
	}
}
