
# 🚀 J4Fluxer (Java For Fluxer)

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Beta-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**J4Fluxer** is a high-performance, asynchronous, and event-driven Java library developed for the [Fluxer](https://fluxer.app) platform.

It simplifies Fluxer bot development by managing **REST API** requests and **Gateway (WebSocket)** connections automatically.

---

## ✨ Features

*   ⚡ **Fully Asynchronous:** Non-blocking requests using `CompletableFuture`.
*   🔌 **Gateway Support:** Handles Heartbeats, Identification, and Reconnection automatically.
*   📡 **Event-Driven:** Easy event handling with `ListenerAdapter`.
*   🛠 **Object-Oriented:** Maps JSON data to Java objects (Guild, Channel, Message, User).
*   🛡 **Secure:** Automatic authorization and header management.

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
<dependencies>
  <dependency>
	    <groupId>com.github.why-shiro</groupId>
	    <artifactId>J4Fluxer</artifactId>
	    <version>Tag</version>
	</dependency>
</dependencies>
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
	        implementation 'com.github.why-shiro:J4Fluxer:Tag'
	}
```

---

## 🚀 Quick Start

Here is a simple "Ping-Pong" bot example.

```java
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerBuilder;
import com.j4fluxer.hooks.ListenerAdapter;
import com.j4fluxer.events.message.MessageReceivedEvent;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize the Bot
        Fluxer bot = FluxerBuilder.create("YOUR_BOT_TOKEN_HERE").build();

        // 2. Add Event Listener
        bot.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                // Ignore messages from bots (including self)
                if (event.getMessage().getAuthor().isBot()) return;

                System.out.println("Received: " + event.getContent());

                // Reply to !ping command
                if (event.getContent().equals("!ping")) {
                    event.getChannel().sendMessage("Pong! 🏓").submit();
                }
            }
        });
    }
}
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
```
