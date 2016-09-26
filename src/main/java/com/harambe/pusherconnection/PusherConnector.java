package com.harambe.pusherconnection;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

/**
 * Created by Jann on 12.09.2016.
 */
public class PusherConnector {

    public static void main(String[] args) {
        PusherOptions options = new PusherOptions();
        options.setAuthorizer(new Authorizer() {
            /*
            TODO: Uses only predefined keys at the moment.
             */
            @Override
            public String authorize(String channel, String socketId) throws AuthorizationFailureException {
                com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher("249870", "082f8d2ee2e06acd7c98", "fc29424e4ab1e692a42b");
                String response = pusher.authenticate(socketId,channel);
                System.out.println(response);
                return response;
            }
        });
        Pusher pusher = new Pusher("082f8d2ee2e06acd7c98", options);

        pusher.connect();

        PrivateChannel channel = pusher.subscribePrivate("private-channel");


        try {
            //Logic for MoveToAgent Event from Server
            channel.bind("MoveToAgent", new PrivateChannelEventListener() {
                @Override
                public void onSubscriptionSucceeded(String s) {
                    System.out.println(s);
                    System.out.println("onSubscriptionSucceeded()");
                }

                @Override
                public void onAuthenticationFailure(String s, Exception e) {
                    System.out.println(s);
                    System.out.println("onSubscriptionFailure()");
                }

                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                    System.out.println(data);
                    //insert move logic here
                    channel.trigger("client-event", "{\"move\": \"" + "1" + "\"}");
                }
            });
        }catch (Exception e){
            System.out.println("Exception!");
            e.printStackTrace();
        }


        System.out.println("Test");

        while(!channel.isSubscribed()){
            //wait for finished subscription
        }


        while(channel.isSubscribed()){
            //busy waiting for channel events
        };


        System.out.println("End");


    }
}
