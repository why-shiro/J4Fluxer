## ✅ DONE LIST (Completed Features)

**1. Core Architecture**
*   [x] **RestAction System:** `queue()`, `submit()`, and `complete()` methods implemented with `CompletableFuture`.
*   [x] **Gateway Connection:** WebSocket connection (`wss://...`), Identify payload (Bot prefix), and Heartbeat (Keep-alive) logic.
*   [x] **Event System:** `ListenerAdapter` and `Event` dispatcher implemented.
*   [x] **Authorization:** Automatic `Bot <TOKEN>` header handling in `Requester`.

**2. Messaging**
*   [x] **Send Message:** Basic text messages.
*   [x] **Edit Message:** Updating content via PATCH.
*   [x] **Delete Message:** Deleting via DELETE.
*   [x] **Reactions:** Adding and Removing emojis (Standard & Custom).
*   [x] **Mentions:** Parsing user mentions from message content.

**3. Moderation Tools**
*   [x] **Kick:** Removing a member.
*   [x] **Ban / Unban:** Permanent ban, Tempban (with duration), and removing bans.
*   [x] **Timeout:** Muting a member for a specific duration.
*   [x] **Role Management:** Adding and removing roles from members.

**4. Entities & Data**
*   [x] **User Profiles:** Fetching Bio, Banner, Avatar, and Pronouns.
*   [x] **Guilds:** Fetching Guild info by ID.
*   [x] **Members:** Parsing Member objects with Role IDs.
*   [x] **Channels:** Basic structure for Text and Voice channels.

**5. Events Implemented**
*   [x] `ReadyEvent`
*   [x] `MessageReceivedEvent`, `MessageUpdateEvent`, `MessageDeleteEvent`
*   [x] `MessageReactionAddEvent`
*   [x] `GuildJoinEvent`, `GuildLeaveEvent`
*   [x] `GuildMemberJoin`, `Leave`, `Update`
*   [x] `GuildBanEvent`
*   [x] `TypingStartEvent`

---

## 📝 TO-DO LIST (Pending & Next Steps)

### 🚨 Priority 1: Immediate Tasks
*   [ ] **Get Category:** Implement `getCategory()` in `GuildChannel` using `parent_id`.
*   [ ] **Channel Modification:** Implement `setName()` and `setPosition()` in `AbstractChannel`.
*   [ ] **Invites:** Implement `createInvite()` to generate join links.
*   [ ] **Permission Overwrites:** Refine the `upsertPermissionOverride` logic to be user-friendly.

### 🚀 Priority 2: Cache Optimization
*   [ ] **Channel Cache:** Store Channels inside the `GuildImpl` object (Map<String, Channel>) so `getTextChannelById` doesn't need to create a new instance every time.
*   [ ] **Role Cache:** Store Roles inside `GuildImpl` so we can get role permissions easily without iterating every time.
*   [ ] **Member Cache:** Store `Member` objects to avoid API calls for simple data.

### 🛡️ Priority 3: Stability & Robustness
*   [ ] **Rate Limiter:** Currently, we just print "429" to the console. Need a bucket system that automatically pauses the request thread and retries after the cooldown.
*   [ ] **Reconnect Logic:** If the internet drops, `GatewayClient` should automatically try to reconnect (Resume Session).

### 🔮 Priority 4: Advanced Features
*   [ ] **Voice Audio:** Connecting to Voice Channels via UDP and sending Audio (Opus).
*   [ ] **Sharding:** If a bot is in 1000+ servers, one connection isn't enough. We need a ShardingManager.

### 📄 Documentation
*   [ ] **Examples:** Create a folder with example bots (ModerationBot.java, MusicBot.java, etc.).

---
