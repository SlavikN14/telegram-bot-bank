# Finance Bot

The Finance Bot is a Telegram bot designed to help you with currency exchange rates and monitor your financial state. It also has features to calculate your income and expenses. This README provides a comprehensive guide on the setup, usage, and deployment of the Finance Bot.

<a href="https://github.com/Wectro20/KotlinPetProject/actions/workflows/gradle.yml">
<img src="https://github.com/Wectro20/KotlinPetProject/actions/workflows/gradle.yml/badge.svg" alt="build"> 
<br>
</a>
## Features

- Provides real-time currency exchange rates.
- Monitors your financial state.
- Calculates your income and expenses.

## Prerequisites

Before you start using the Finance Bot, make sure you have the following prerequisites:

- A Telegram account.
- A bot token from the [BotFather](https://core.telegram.org/bots#botfather) on Telegram.
- Java Development Kit (JDK) 17
- Gradle (for building the application)
- Docker and Docker Compose (for containerized deployment)
- MongoDB (can be set up locally or using Docker)

## Getting Started

To get started with the Finance Bot, follow these steps:

1. Clone this repository to your local machine.

2. Create a Telegram bot and obtain the API token from the BotFather on Telegram.

3. Set up the API token in a file named `application.yaml` with the following content:
   ```
   bot:
    token: your_bot_token
    username: your_bot_name
   ```
Or using ###VMOptions:
`-Dbot.token = put_your_token_here -Dbot.username = put_your_bot_name_here`
