package com.harambe.pusherinterface;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

/**
 * Created by Jann on 12.09.2016.
 */
public class PusherInterface {

    public static void main(String[] args) {
        Pusher pusher = new Pusher("39124c1234210fc081c6");

        Channel channel = pusher.subscribe("test_channel");

        channel.bind("my_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data);
            }
        });

        pusher.connect();

        System.out.println("Test");

        while(true);


    }
}
