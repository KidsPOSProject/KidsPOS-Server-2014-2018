# KidsPOS Server

子ども向けPOSシステムのサーバーアプリケーション（2014-2018年版）

## 概要

KidsPOS Serverは、教育目的で設計された子ども向けPOSシステムのバックエンドサービスです。シンプルなRESTful APIを提供し、商品管理、売上管理、スタッフ管理などの基本的なPOS機能を実装しています。

## 技術スタック

- **Java 8**
- **Jersey** (JAX-RS実装) - RESTful Webサービス
- **Jetty** - 組み込みWebサーバー
- **Gradle** - ビルドツール

## 主な機能

- **商品管理** - 商品の登録、編集、削除
- **商品ジャンル管理** - 商品カテゴリの管理
- **売上管理** - 売上データの記録と管理
- **スタッフ管理** - スタッフ情報の管理
- **店舗管理** - 店舗情報の設定
- **印刷機能** - レシートや商品ラベルの印刷

## プロジェクト構造

```
src/main/java/info/nukoneko/kidspos/
├── Launcher.java           # アプリケーションエントリーポイント
├── api/                    # REST APIエンドポイント
│   ├── Item.java          # 商品API
│   ├── ItemGenre.java     # 商品ジャンルAPI
│   ├── Sales.java         # 売上API
│   ├── Staff.java         # スタッフAPI
│   ├── Store.java         # 店舗API
│   └── Top.java           # トップページAPI
├── print/                  # 印刷関連機能
│   ├── ItemPrintObject.java
│   ├── ItemPrintable.java
│   ├── KPPrintable.java
│   ├── PrintManager.java
│   └── PrintableInsets.java
└── util/                   # ユーティリティ
    └── KPLogger.java      # ログ管理
```

## ビルドと実行

### 必要要件

- Java 8以降
- Gradle

### ビルド

```bash
./gradlew build
```

### 実行

```bash
./gradlew war
java -jar build/libs/KidsPOS-Server-2014-2018-1.0.3.war
```

アプリケーションはポート9500で起動し、自動的にデフォルトブラウザで開きます。

## API仕様

サーバーは以下のエンドポイントでRESTful APIを提供します：

- `/api/item` - 商品管理
- `/api/itemgenre` - 商品ジャンル管理
- `/api/sales` - 売上管理
- `/api/staff` - スタッフ管理
- `/api/store` - 店舗管理

## 依存関係

- [KidsPOS4j](https://github.com/KidsPOSProject/KidsPOS4j) v1.0.3 - 共通ライブラリ
- Jersey 2.26-b02 - RESTfulフレームワーク
- Jetty 9.4.1 - Webサーバー
- JavaEE API 7.0

## ライセンス

このプロジェクトのライセンス情報については、プロジェクトオーナーにお問い合わせください。

## 開発者向け情報

### テスト実行

```bash
./gradlew test
```

### 開発サーバー起動

```bash
./gradlew run
```

## 貢献

プルリクエストは歓迎します。大きな変更の場合は、まずissueを開いて変更内容について議論してください。

## 連絡先

プロジェクトに関するお問い合わせは、GitHubのissueをご利用ください。