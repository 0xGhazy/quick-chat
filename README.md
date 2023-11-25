![banner](https://github.com/0xGhazy/0xGhazy/assets/60070427/d49f1c12-1e4b-45fe-90d8-b3100de67f81)

<div align="center">

# Mini-Discord

![](https://img.shields.io/badge/VSCode-0078D4?style=for-the-badge&logo=visual%20studio%20code&logoColor=white) ![](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white) ![](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) ![](https://img.shields.io/badge/Canva-%2300C4CC.svg?&style=for-the-badge&logo=Canva&logoColor=white) ![](https://img.shields.io/badge/Markdown-000000?style=for-the-badge&logo=markdown&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

</div>

## Overview
Mini-Discord is a multi-threaded console Java application that handles multiple client requests and conversations at the same time. it consists of main two parts `Client Application` and `Server Application`. In the following section, I'll describe how they work internally and show the features with screenshots.

## About the server

### Overview

![server](https://github.com/0xGhazy/0xGhazy/assets/60070427/c29903d3-af3a-4f0e-b7ec-ca1e6392d39e)

The server is the main part of the Mini Discord application. it starts to listen on the specified port and connect with clients to handle communication and requests.

the most important role of the server is handling multiple requests simultaneously, each client has its own `Client Handler` which is responsible for sending and receiving messages, and commands from and to the client.

**What happens under the hood?**
- A new client opens the client from its end, this will inform the server that a new lobby client is active now.
- The server will accept the new client request, then create a new `client handler` and start a new thread for this handler and start it.
```java
    // ---- code snippet ---- 
    Socket clientSocket = serverSocket.accept();
    // generate a temporary name for lobby clients.
    String tempUsername = "lobby-client-" + DateTimeHandler.timestampNow();
    logger.logThis("info", "Listening for client requests. . .");
    logger.logThis("request", "A new lobby client request is accepted");
    ClientHandler clientHandler = new ClientHandler(clientSocket, tempUsername);
    Thread thread = new Thread(clientHandler);
    thread.start();
    // ---- code snippet ---- 
```

- By Client handler constructor take the `socket`, and `username` as arguments to initialize the client handler object by setting the username for this, and the most important role here is to add `this` -newly created- to the `clientsList` which hold all current active handlers.

```java
class ClientHandler implements Runnable {

    /* ---- code snippet ---- */

    // shared by all handlers
    private static ArrayList<ClientHandler> clientsList = new ArrayList<>();

    public ClientHandler(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            clientsList.add(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---- code snippet ---- */

}
```

- By starting the client thread, it will trigger the Overrided `run` method from the `Runnable` interface. which will handle the incoming client requests by the specified flag in the request message.

- Each client request to be handled by the handler must consist of FLAG, DELIMITER, and PAYLAOD. handler will parse it choose which function will be executed and replay it to the client.

Parsing incoming commands from the client:

```java
    // reading commands from the client side.
    command = bufferedReader.readLine();
    String[] commandArray = command.split(Settings.DELIMITER);
    String flag = commandArray[0];
    String payload = commandArray[1];
```

Choose which function will be executed:

```java
    if(flag.equals("[RESET]")) 
    {
        logger.logThis("request", "Reset password request is captured");
        Credentials credentials = credentialsService.deSerialize(payload);
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        // hashing the new password
        password = HashingService.getSha256(password);
        String result = databaseAPI.updatePassword(username, password);
        logger.logThis("info", "Account updated [" + result + "]");
        sendMessage(this, result);
    }
```

### Internal flags
The clients send the user message that applies the following structure

```
FLAG DELIMITER PAYLOAD
```

**Note**: No spaces between them, for example of `[VALIDATE]` usage.
```
[VALIDATE]>hossam
   |      |    |
   |      | (Username)
   |  (DELIMITER)
 (FLAG)
```
Here is a list of all available and handled flags on the server side with their descriptions.
| Flag | Server usage | Followed By | 
| :--: | :-- | :-- |
| `[SIGNUP]` | Signup a new user | Serialized `User` object |
| `[LOAD]` | Read the user from the database then return it to the client | Username |
| `[RESET]` | Reset user password | Serialized `Credentials` object |
| `[VALIDATE]` | Validate if the username is exist in database | Username |
| `[UPDATE]` | Update the user words and conversations | Serialized `User` object |
| `[SNAPSHOT]` | Ask the server to return conversation | Username |
| `[AUTH]` | Authorize user in login process | Serialized `Credentials` object |
| `[JOIN]` | Add user to the chat room | Username |
| `[BROADCAST]` | Send message to all peers in chat room | "{sendername}~{message}" |
| `[LEAVE]` | inform all users a member has left | Username |


### Server project directory architecture

| Files in | Description |
|:--:|:--|
|database | The database API responsible for dealing with the database and tasks such as *` adding new user`*, *` update user statistics`*, etc.|
| model | the main object classes that the server and the client exchange together |
| server | contains the server backend logic such as *`running the server`*, and *`handling incoming requests`* |
| service | all needed functionality by the `models` such as *`serialization`*, *`deserialization`*, and *`hashing`* function|
| utils | all utility functions such as logging, colored output, banners, etc.|
| Settings.java | all settings needed by the server is here such as *`port`*, *`host`*, *`database connection`*, etc.|

### Features & Screenshots

- **Secured stored credentials**
All confidential data is stored hashed in the database using the `SHA256` algorithm. from `guava` library

![hashed passwords](https://github.com/0xGhazy/0xGhazy/assets/60070427/318141ed-1b2d-4749-8b19-e42df3658bc7)

```xml
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>32.1.3-jre</version>
    </dependency>
```

```java
public static String getSha256(String plainPassword) throws NoSuchAlgorithmException {
    return Hashing.sha256()
                .hashString(plainPassword, StandardCharsets.UTF_8)
                .toString();
    }
```


- **Logging events**
I have implemented logging events handler `/utils/Logger.java` to enable logging events and verbosity to make it easier to know what is happening right now with each event.

![server ver](https://github.com/0xGhazy/0xGhazy/assets/60070427/3affbbf8-43ea-41c1-a65e-853a9b9c6945)


- **Settings class**
in the `Settings.java` you can specify the settings of the server such as the following.

```java
public class Settings {
    public static final Integer PORT = 5000;
    public static final String DATABASE_USERNAME = "_USERNAME_";
    public static final String DATABASE_PASSWORD = "_PASSWORD_";
    private static final String DATABASE_NAME = "DATABASE_NAME";
    public static final String DATABASE_URI = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    // this property must be set in the client too
    // it's the DELIMITER between flag and payload.
    public static final String DELIMITER = ">";
}
```

## About the Client

### Overview
![carbon](https://github.com/0xGhazy/0xGhazy/assets/60070427/38f2b9fb-9bfc-42ad-988a-588568c3ea0d)

The client is the second important part of the application, by running the `client` it will try to connect to the server on the specified host and port number in the `Settings.java`. seeing the `prompt` means you can ask the server for the actions you need in your current mode. 

The client has 3 main modes:
| Mode | Description | How to active? |
| :--: | :--         | :--:          |
| Lobby | The default mode when opening the client | default |
| Active | The mode loaded after login, your profile is loaded here to be ready for dumping | `/login` |
| Chatting | Mode in the chat room, in which you send messages to all peers |  `/join` |


Use `/help` to get the current available commands for `Lobby`, and `Active` modes.

### Client project directory architecture

| Files in | Description |
|:--:|:--|
| model | The main object classes that the server and the client exchange together |
| network | Connection handlers such as *`client connection handler`*, and *`message listener`* |
| service | All needed functionality by the `models` such as *`serialization`*, *`deserialization`*, *`hashing`*, *`reporting`* functions |
| ui | All ui modes and functions such as colored output, banners, and *`login`*, *`signup`*, and *` reset password`*, etc.|
| utils | All utility functions such as date and time, and used validations.|
| Settings.java | All settings needed by the client is here such as *`port`*, *`host`*, and *`delimiter`*, etc.|


### Features & Screenshots

- **Signup**
Passwords are taken from the user via `console.readPassword()` which allows you to take passwords hidden, with no echo in the console. Username validation also happens during the filling of account data.
![signup-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/d2e90d26-5e93-4fac-8cff-415258b1534d)

- **Signin**

![login-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/32f40284-6ca9-4e12-b3ab-bca597b345f9)

- **Reset Password**

![reset-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/c316bdc6-7d71-4a63-934e-9b004eaf3942)

- **Join Chat**
Users try to join the room using `/join` command in the active mode. if the user faces any problem he will try to use the command again until joining the room. All messages will be broadcast to the peers in the room.

if the user uses `/leave` or `bye bye` command he will leave the room and then ask the server for a snapshot of the conversation till the moment he leaves.

![chat-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/6a31d491-01da-4839-b3b9-0acb58cf939c)

There are 4 file dumps that happened here

- `/{username}/conversation-temp.txt` contains this conversation message.
- `/{username}/conversation-total.txt` contains all-time conversation words
- `/{username}/words-temp.txt` contains these conversation words and their frequency. 
- `/{username}/words-total.txt` contains all-time words and their frequency.

![user-dump-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/1b259ab8-2af2-43fb-ab94-7c7e594d6f7b)

- **Visualize your statistics**

![visual-gif](https://github.com/0xGhazy/0xGhazy/assets/60070427/af06e499-73fa-43f6-a973-08b35bfa258b)

- **help commands**

![help](https://github.com/0xGhazy/0xGhazy/assets/60070427/eae7949e-7dd4-4ed0-b264-15f828760b44)


## Installation guidelines

1. First clone this repository
```shell
git clone https://github.com/0xGhazy/mini-discord.git
```

2- Create a new database with the following command

```sql
CREATE DATABASE `mini_discord`
```

3- Create User table inside the database
```sql
CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `secQ` varchar(255) NOT NULL,
  `secA` varchar(255) NOT NULL,
  `words` blob,
  `conversations` blob,
  PRIMARY KEY (`username`)
)
```

4- Open the server project directory with your favorite IDE and run the server first. Make sure that the server is running like the following image:

In my case, IntelliJ IDE has downloaded all dependencies, so make sure your pom.xml file dependencies are downloaded successfully in the client and server code.

![server run ](https://github.com/0xGhazy/0xGhazy/assets/60070427/6feb44c1-315f-43dd-abcf-bed589e86877)

5- Open the client project directory with your favorite IDE and run the client. **Using Visual Code is preferred because of its integrated terminal that allows us to use the console module for better password taking**

![client](https://github.com/0xGhazy/0xGhazy/assets/60070427/8a7695d4-d97c-4311-9fa0-e3e8ff993d6b)

Happy chatting and dumping 🎉😊

---
#### Old related projects

[0xGhazy/fateh-framework](https://github.com/0xGhazy/Fateh-Framework)
Is a free, open-source tool targeting Windows-systems Based on HTTP reversed shell. This tool helps you to generate Fully UnDetectable (FUD) HTTP reversed shell With many features that you will find mentioned below. that was programmed primarily for educational and self-challenging purpose.

