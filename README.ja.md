Android ADK MIDI ドライバ
====

Android Open Accessoryを使ったADK MIDIのドライバです。

- Android GingerBread(2.3.4)の端末で使えます。
- シーケンサや楽器など、レガシーなMIDI機器をサポート

制限
----
- 一度に1つのADKしか接続できません。

必要なもの
----
- Android : OSバージョン2.3.4以降(API Level 10)で、アクセサリのライブラリが提供されていること。
- Arduino互換のADKボード(Accessory Development Kit Board)
- SparkfunのMIDIシールド(もしくは、同等のArduinoシールド)
- MIDI機器

デバイスの接続
----
    Android [USB Bポート]------[USB Aポート] ADK + MIDIシールド [MIDI IN/OUTポート] ------ MIDI機器

プロジェクト
----
- ライブラリ  
 - ADKMIDIDriver : 本ライブラリ

- アクセサリ
 - MIDIAccessoryFirmware : ADKボード用ファームウェア

- サンプル
 - ADKMIDIDriverSample : ライブラリを使って実装した、シンセサイザ・MIDIイベントロガーの例

ライブラリプロジェクトの使い方
----
プロジェクトの設定

- ライブラリプロジェクトをcloneします。
- Eclipseのワークスペースにライブラリプロジェクトをインポートし、ビルドします。
- 新しいAndroid Projectを作成し、プロジェクトのライブラリにライブラリプロジェクトを 追加します。
- `jp.kshoji.driver.adkmidi.activity.AbstractMidiActivity` をオーバーライドしたActivityを作ります。
- AndroidManifest.xml ファイルの application タグを変更します。
 - **uses-library** com.android.future.usb.accessory を追加します。
- AndroidManifest.xml ファイルの activity タグを変更します。
 - **intent-filter** android.hardware.usb.action.USB_DEVICE_ATTACHED と **meta-data** を オーバーライドした Activity に対して追加します。
 - Activityの **launchMode** を "singleTask" にします。
- 

    <application
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name" >
        <uses-library android:name="com.android.future.usb.accessory" />
    
        <activity
            android:name=".MyMidiMainActivity"
            android:label="@string/app_name"
            android:launchMode="singleTask" >
            <intent-filter>
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" />
            </intent-filter>
    
            <meta-data
                android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED"
                android:resource="@xml/device_filter" />
        </activity>
    </application>


MIDI イベントの受信

- MIDIイベントを処理するメソッド (`"onMidi..."` という名前)を実装します。

MIDI イベントの送信

- `MIDIOutputDevice`のインスタンスを取得するために、AbstractMidiActivity の`getMidiOutputDevice()`メソッドを呼びます。
 - そのインスタンスの`"onMidi..."` という名前のメソッドを呼ぶとMIDIイベントが送信できます。

ライセンス
----
[Apache License, Version 2.0][Apache]
[Apache]: http://www.apache.org/licenses/LICENSE-2.0