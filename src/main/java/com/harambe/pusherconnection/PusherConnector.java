package com.harambe.pusherconnection;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

/**
 * Created by Jann on 12.09.2016.
 */
public class PusherConnector implements Runnable {

    private String message;
    private PrivateChannel channel;



    /**
     * returns the last message received from pusher
     * deletes the message from Connector
     */
    public String getMessage(){
        String response = message;
        message = null;
        return response;
    }

    @Override
    public void run() {

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(new Authorizer() {
            /*
            TODO: Uses only predefined keys at the moment.
             */
            @Override
            public String authorize(String channel, String socketId) throws AuthorizationFailureException {
                com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher("249870", "082f8d2ee2e06acd7c98", "fc29424e4ab1e692a42b");
                String response = pusher.authenticate(socketId,channel);
                //System.out.println(response);
                return response;
            }
        });
        Pusher pusher = new Pusher("082f8d2ee2e06acd7c98", options);

        pusher.connect();

        channel = pusher.subscribePrivate("private-channel");


        try {
            //Logic for MoveToAgent Event from Server
            channel.bind("MoveToAgent", new PrivateChannelEventListener() {
                @Override
                public void onSubscriptionSucceeded(String s) {
                 //   System.out.println(s);
                 //   System.out.println("onSubscriptionSucceeded()");
                }

                @Override
                public void onAuthenticationFailure(String s, Exception e) {
                  //  System.out.println(s);
                  //  System.out.println("onSubscriptionFailure()");
                }

                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                  //  System.out.println(data);
                    message = data;
                }
            });
        }catch (Exception e){
            System.out.println("Exception!");
            e.printStackTrace();
        }



        while(!channel.isSubscribed()){
            //wait for finished subscription
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        while(channel.isSubscribed()){
            //busy waiting for channel events
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };


        //System.out.println("End");
    }

    public void sendTurn(int col){
        channel.trigger("client-event", "{\"move\": \"" + col + "\"}");
    }
}
