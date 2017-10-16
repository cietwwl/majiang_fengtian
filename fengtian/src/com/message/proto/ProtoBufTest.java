package com.message.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.message.proto.Message.command;

/**
 * Created by IntelliJ IDEA 14.
 * User: karl.zhao
 * Time: 2015/12/25 0025.
 */
public class ProtoBufTest {

    public static void main(String[] args) {
        Message.command.Builder builder = Message.command.newBuilder();
        builder.setName("Karldfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdf");
        command command = builder.build();
        byte[] buf = command.toByteArray();
        try {
            Message.command person2 =Message.command .parseFrom(buf);
            System.out.println(person2.getName());

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println(buf.toString());

    }
}
