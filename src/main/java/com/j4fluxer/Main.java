package com.j4fluxer;

import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerBuilder;
import com.j4fluxer.hooks.ListenerAdapter;
import com.j4fluxer.events.message.MessageReceivedEvent;

public class Main {
    private static final String token = System.getenv("FluxerToken");

    static void main(String[] args) {

        Fluxer fluxer = FluxerBuilder.create(token).build();

        fluxer.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                System.out.println("Incoming Message: " + event.getContent());

                if (event.getContent().equals("!ping")) {
                    event.getChannel().sendMessage("Pong! 🏓")
                            .submit()
                            .thenAccept(message -> System.out.println("✅ Reply Sent: ID " + message.getId()))
                            .exceptionally(e -> {
                                System.err.println("❌ An error occurred while sending message: ");
                                e.printStackTrace();
                                return null;
                            });
                }
            }
        });

        System.out.println("Bot Init Completed!");
    }
}