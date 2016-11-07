package com.harambe.communication;

import com.harambe.game.SessionVars;
import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * TODO Insert documentation here.
 */
public class PusherConnector implements Runnable {

    private String message;
    private PrivateChannel channel;


    /**
     * returns the last message received from pusher
     * deletes the message from Connector
     */
    public String getMessage() {
        String response = message;
        message = null;
        return response;
    }

    @Override
    public void run() {

        Properties prop = new Properties();
        InputStream input = null;
        String app_id = new String();
        String key = new String();
        String secret = new String();

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);


            app_id = prop.getProperty("app_id");
            key = prop.getProperty("key");
            secret = prop.getProperty("secret");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        PusherOptions options = new PusherOptions();
        String finalApp_id = app_id;
        String finalKey = key;
        String finalSecret = secret;
        System.out.println(app_id);
        System.out.println(key);
        System.out.println(secret);
        /*
        options.setAuthorizer(new Authorizer() {
            @Override
            public String authorize(String channel, String socketId) throws AuthorizationFailureException {
                com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher(finalApp_id, finalKey, finalSecret);
                String response = pusher.authenticate(socketId, channel);
                return response;
            }
        });
        */

        options.setAuthorizer((channel1, socketId) -> {
            com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher(finalApp_id, finalKey, finalSecret);
            String response = pusher.authenticate(socketId, channel1);
            System.out.println(response);
            return response;
        });

        System.out.println("BEHIND LAMDA!!");
        Pusher pusher = new Pusher(finalKey, options);

        pusher.connect();

        channel = pusher.subscribePrivate("private-channel");


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
//                  System.out.println(data);
                message = data;
            }
        });
    }

    public void sendTurn(int col) {
        channel.trigger("client-event", "{\"move\": \"" + col + "\"}");
    }
}
