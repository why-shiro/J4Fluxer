# 🚀 J4Fluxer (Java For Fluxer)

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Alpha-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
[![](https://jitpack.io/v/why-shiro/J4Fluxer.svg)](https://jitpack.io/#why-shiro/J4Fluxer)

**J4Fluxer** is a high-performance, asynchronous, and event-driven Java library developed for the [Fluxer.app](https://fluxer.app) platform.

It simplifies Fluxer bot development by managing **REST API** requests and **Gateway (WebSocket)** connections automatically, offering a developer experience similar to JDA.

---

## ✨ Features

*   ⚡ **Fully Asynchronous:** Non-blocking requests using `CompletableFuture` and `queue()`.
*   🔌 **Gateway Support:** Handles Heartbeats, Identification, and Reconnection automatically.
*   📡 **Event-Driven:** Easy event handling with `ListenerAdapter`.
*   🛠 **Object-Oriented:** Maps JSON data to Java objects (Guild, Channel, Message, User).
*   🛡 **Secure:** Automatic authorization and header management.
*   🧠 **Smart Caching:** Reduces API calls by caching Guilds and Channels.

---

## 📦 Installation

You can install J4Fluxer using **JitPack**.

### Maven

1. Add the JitPack repository to your `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Add the dependency:
```xml
<dependency>
	    <groupId>com.github.why-shiro</groupId>
	    <artifactId>J4Fluxer</artifactId>
	    <version>alpha-0.1.4</version>
	</dependency>
```

### Gradle
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
	        implementation 'com.github.why-shiro:J4Fluxer:alpha-0.1.4'
	}
```

---

## 🚀 Quick Start

Here is a simple "Ping-Pong" bot example to get you started.

```java
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerBuilder;
import com.j4fluxer.hooks.ListenerAdapter;
import com.j4fluxer.events.message.MessageReceivedEvent;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize the Bot with your token
        Fluxer bot = FluxerBuilder.create("YOUR_BOT_TOKEN_HERE").build();

        // 2. Add Event Listener
        bot.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                // Ignore messages from bots (including self) to prevent loops
                if (event.getMessage().getAuthor().isBot()) return;

                System.out.println("Received: " + event.getContent());

                // Reply to !ping command
                if (event.getContent().equals("!ping")) {
                    event.getChannel().sendMessage("Pong! 🏓").queue();
                }
            }
        });
    }
}
```

---

## 💡 Code Examples

Here are examples of how to use J4Fluxer's advanced features.

### 🛡️ Moderation System

**Kick a Member:**
```java
// Kicks a user by ID
guild.kickMember("USER_ID").queue(
    success -> System.out.println("User kicked!"),
    error -> System.err.println("Failed to kick: " + error.getMessage())
);
```

**Ban a Member:**
```java
// Permanent Ban
guild.banMember("USER_ID", "Spamming").queue();

// Temporary Ban (Delete 1 day of messages, Ban for 1 hour)
guild.banMember("USER_ID", 1, 3600, "Violated rules").queue();
```

**Timeout (Mute) & Unmute:**
```java
// Mute for 60 seconds
guild.timeoutMember("USER_ID", 60).queue();

// Remove Mute
guild.removeTimeout("USER_ID").queue();
```

---

### 📂 Channel Management

**Create Categories and Channels:**
```java
// Create a Category, then create channels inside it
guild.createCategory("General Area").queue(category -> {
    
    // Create Text Channel inside the new Category
    category.createTextChannel("chat").queue();
    
    // Create Voice Channel inside the new Category
    category.createVoiceChannel("voice").queue();
    
    System.out.println("Category " + category.getName() + " created!");
});
```

**Modify Channel Settings:**
```java
TextChannel channel = event.getChannel();

channel.setName("new-name").queue();      // Rename
channel.setTopic("Managed by J4Fluxer").queue(); // Set Topic
channel.setSlowmode(5).queue();           // Set 5s Slowmode
```

**Create Invite:**
```java
channel.createInvite().queue(code -> {
    channel.sendMessage("Join here: https://fluxer.app/invite/" + code).queue();
});
```

---

### 🏷️ Role Management

**Add or Remove Roles:**
```java
String userId = "USER_ID";
String roleId = "ROLE_ID";

// Give Role
guild.addRoleToMember(userId, roleId).queue();

// Remove Role
guild.removeRoleFromMember(userId, roleId).queue();
```

**Check Permissions:**
```java
Member member = event.getMessage().getMember();
Role modRole = guild.getRoleById("ROLE_ID");

if (member.getRoleIds().contains(modRole.getId())) {
    // User has the role
}
```

---

### 🎭 Reactions (Emojis)

**Add & Remove Reactions:**
```java
// Add Standard Emoji
message.addReaction("🔥").queue();

// Add Custom Fluxer Emoji (Format: name:id)
message.addReaction("jeb_:1472587217585434637").queue();

// Remove Bot's Own Reaction
message.removeReaction("🔥").queue();
```

---

### ⏱️ Scheduled Tasks (QueueAfter)

You can schedule actions to happen in the future without blocking the main thread.

```java
// Send a message and delete it after 5 seconds
channel.sendMessage("This message will self-destruct in 5 seconds... 💣")
       .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
```

---

### 🟢 Bot Presence

Change how your bot appears to other users.

```java
// Set status to Online (Green)
bot.setStatus(OnlineStatus.ONLINE);

// Set status to Do Not Disturb (Red)
bot.setStatus(OnlineStatus.DND);

// Set status to Idle (Yellow)
bot.setStatus(OnlineStatus.IDLE);
```

---

### 🕵️ User Profiles

Fetch detailed information about a user that isn't available in standard messages (like Banner or Bio).

```java
guild.retrieveMemberProfile("USER_ID").queue(profile -> {
    System.out.println("User Bio: " + profile.getBio());
    System.out.println("Banner URL: " + profile.getBannerUrl());
    System.out.println("Joined At: " + profile.getJoinedAt());
});
```

---

## 🎧 Supported Events

Your bot can listen to and react to the following events:

| Event Class | Description |
| :--- | :--- |
| `ReadyEvent` | Triggered when the bot successfully logs in. |
| `MessageReceivedEvent` | Triggered when a new message is received. |
| `MessageUpdateEvent` | Triggered when a message is edited. |
| `MessageDeleteEvent` | Triggered when a message is deleted. |
| `MessageBulkDeleteEvent` | Triggered when multiple messages are deleted. |
| `MessageReactionAddEvent`| Triggered when a user reacts to a message. |
| `GuildJoinEvent` | Triggered when the bot joins a new server. |
| `GuildMemberJoinEvent` | Triggered when a user joins a server. |
| `TypingStartEvent` | Triggered when a user starts typing. |
| `GuildBanEvent` | Triggered when a user is banned or unbanned. |

---

## 🛠 Tech Stack

*   **OkHttp 3:** For HTTP requests.
*   **Java-WebSocket:** For Gateway connection.
*   **Jackson:** For JSON processing.

---

## 🤝 Contributing

This project is Open Source! If you'd like to contribute:
1.  Fork the repository.
2.  Create a new branch (`feature/new-feature`).
3.  Commit and Push your changes.
4.  Open a Pull Request (PR).

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---
*Developed with ❤️ by NeoStellar Team*
