package com.harambe.communication;

import com.harambe.game.SessionVars;
import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

/**
 * TODO Insert documentation here.
 */
public class PusherConnector implements Runnable {

    private String message;
    private PrivateChannel channel;
    private long repeatTime = 100;



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
                com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher(SessionVars.app_id, SessionVars.key, SessionVars.secret);
                String response = pusher.authenticate(socketId,channel);
                //System.out.println(response);
                return response;
            }
        });
        Pusher pusher = new Pusher(SessionVars.key, options);

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
                Thread.sleep(repeatTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        while(channel.isSubscribed()){
            //busy waiting for channel events
            try {
                Thread.sleep(repeatTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //System.out.println("End");
    }

    public void sendTurn(int col){
        channel.trigger("client-event", "{\"move\": \"" + col + "\"}");
    }
}
