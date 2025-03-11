# News Reader App

## Overview
The **News Reader App** is an Android application that fetches the top 10 articles from the [Hacker News API](https://hacker-news.firebaseio.com/) and stores their **titles, URLs, and raw HTML content** locally. This allows users to **read the articles offline** without requiring an internet connection for linked resources.

## Features
- Fetches **top 10 news stories** from Hacker News API.
- Stores **title, URL, and raw HTML content** in a local SQLite database.
- Allows **offline reading** after stories are retrieved.
- Users can press the **"Load Stories" button** to fetch new articles.
- Simple and lightweight user interface for seamless reading.

## How It Works
1. When the app is opened, it checks whether stories are already stored.
2. If not, it downloads the latest top 10 stories from Hacker News.
3. The app **saves the title, URL, and raw HTML content** to a local database.
4. Users can open and read saved stories offline (without external resources requiring an internet connection).
5. To refresh the news list, users can press the **"Load Stories"** button to fetch new articles.

## Installation
1. Clone the repository or download the APK.
2. Install the app on your Android device.
3. Ensure you have an **active internet connection** for the first fetch.
4. Start reading your favorite news offline!

## Demo
Watch the full demonstration on YouTube: [Click Here](YOUR_YOUTUBE_LINK_HERE)

## API Reference
- **Hacker News API**: [https://hacker-news.firebaseio.com/](https://hacker-news.firebaseio.com/)

## License
This project is open-source. Feel free to modify and improve it!

