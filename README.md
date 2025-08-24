# Blitz AI

Blitz AI is a modern Android application built with Jetpack Compose that utilizes [Groq Cloud](https://console.groq.com/docs/quickstart) to deliver lightning-fast AI responses with advanced features like custom instructions, markdown rendering, and intelligent code block handling.


## Screenshots
<div style="display: flex; justify-content: space-between;">
  <img src="assets/screenshots/one.png" width="90" alt="Screenshot 1"/>
  <img src="assets/screenshots/two.png" width="90" alt="Screenshot 1"/>
  <img src="assets/screenshots/three.png" width="90" alt="Screenshot 3"/>
</div>


## Features

- **Ridiculously Fast**: Responses happen in real time—no more long waits for answers.
- **Super Accurate**: Delivers better results than competitors, so you're not left second-guessing.
- **Groq Cloud Power**: Backed by the speed and efficiency of Groq Cloud for optimized performance.
- **Modern Material 3 UI**: Beautiful, responsive interface with card-based settings and smooth animations.
- **Custom Instructions**: Set personalized system instructions that persist across conversations.
- **Markdown Support**: Rich text rendering with code blocks, headings, bold, italic, and inline code.
- **Code Block Copy**: One-click copy functionality with visual feedback for all code snippets.
- **Think Blocks**: Collapsible reasoning sections that auto-hide when AI finishes thinking.
- **Model Selection**: Easy dropdown to choose from the latest Groq models.
- **Persistent Settings**: All preferences saved locally with DataStore.

## Tech Stack

- **Kotlin 2.1.0** - Modern programming language for Android
- **Jetpack Compose 1.8.0** - Declarative UI toolkit
- **Material 3 1.4.0** - Latest Material Design components
- **Android Gradle Plugin 8.7.3** - Build system
- **Hilt 2.54** - Dependency injection
- **Room 2.6.1** - Local database
- **Retrofit 2.11.0** - HTTP client
- **DataStore 1.1.1** - Preferences storage
- **Navigation Compose 2.8.5** - In-app navigation

## Setup

1. Clone the repository
2. Add your Groq API key in Settings
3. Select your preferred model from the dropdown
4. Optionally customize system instructions
5. Start chatting!

## Download

<a href="https://github.com/krishnassh/blitz-ai/releases/latest" target="_blank">
  <img src="assets/github.png" width="300" alt="Get it on Github"/>
</a>


## Discussions 


<a href="https://t.me/blitzzAI" target="_blank">
  <img src="assets/telegram.png" width="260" alt="Telegram Discussions"/>
</a>


## Supported Models

Blitz AI now includes a convenient dropdown menu with the latest Groq models. Here are the currently supported models:

### Stable Models
| Model               | Developer         | ID                          |
|---------------------|-------------------|-----------------------------|
| **Llama 3.1 8B**    | Meta              | `llama-3.1-8b-instant`     |
| **Llama 3.3 70B**   | Meta              | `llama-3.3-70b-versatile`  |
| **Llama Guard 4 12B**| Meta             | `meta-llama/llama-guard-4-12b` |
| **GPT OSS 120B**    | OpenAI            | `openai/gpt-oss-120b`       |
| **GPT OSS 20B**     | OpenAI            | `openai/gpt-oss-20b`        |

### Preview Models
| Model               | Developer         | ID                          |
|---------------------|-------------------|-----------------------------|
| **DeepSeek R1 70B** | DeepSeek/Meta     | `deepseek-r1-distill-llama-70b` |
| **Llama 4 Maverick**| Meta             | `meta-llama/llama-4-maverick-17b-128e-instruct` |
| **Llama 4 Scout**   | Meta              | `meta-llama/llama-4-scout-17b-16e-instruct` |
| **Kimi K2**         | Moonshot AI       | `moonshotai/kimi-k2-instruct` |
| **Qwen 3 32B**      | Alibaba Cloud     | `qwen/qwen3-32b`            |

### Audio Models
| Model               | Developer         | ID                          |
|---------------------|-------------------|-----------------------------|
| **Whisper Large v3**| OpenAI           | `whisper-large-v3`          |
| **Whisper Large v3 Turbo**| OpenAI     | `whisper-large-v3-turbo`    |

The complete and up-to-date list of models is available at [Groq Console](https://console.groq.com/docs/models).

<br/>

## Comparison between models

### Speed
<img src="assets/speed.png" width="300" alt="Speed Comparison"/>
<br/>

### Quality
<br/>

<img src="assets/quality.png" width="300" alt="Quality Comparison"/>

### Price

<br/>

<img src="assets/price.png" width="300" alt="Price Comparison"/>
