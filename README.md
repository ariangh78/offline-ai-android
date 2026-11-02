# Offline AI on Android

Run AI on your Android phone with **no internet**. A Kotlin + Jetpack Compose app that talks to **Gemini Nano** on-device through ML Kit's GenAI Prompt API.

From the code8 video: **Run AI on Android With NO Internet (Kotlin + Compose)** — subscribe to [@code8lab](https://www.youtube.com/@code8lab).

## How it works

1. `checkStatus()` asks AICore if Gemini Nano is on this phone.
2. If it's downloadable, `download()` fetches it once.
3. `generateContentStream()` streams the answer into a `StateFlow`, and Compose draws it.

Your prompt goes to AICore on the device, not to a server.

## Run it

- Android Studio, JDK 17
- A phone that supports Gemini Nano (min SDK 26, locked bootloader)
- Open the project, run `app`, then try it in airplane mode.

If the app says "This phone can't run Gemini Nano", your device isn't supported.

## Limits

- Only some devices have Gemini Nano.
- Keep input under about 4,000 tokens.
- AICore applies a per-app quota.

The Prompt API is in beta. Check the [docs](https://developers.google.com/ml-kit/genai/prompt/android/get-started) for the newest `genai-prompt` version.
