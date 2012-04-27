Android ADK MIDI Driver
====

ADK MIDI Driver using Android Open Accessory

- Runs on GingerBread devices.
- Supports the lagacy MIDI devices; like sequencers, or instruments.

Restriction
----
- Currently, this library can connect only one accessory.

Requirement
----
- Android : OS version 2.3.4(API Level 10) or higher. Accssory support by manufacturer also needed.
- Arduino compatible ADK board (Accessory Development Kit Board)
- Sparkfun's MIDI Shield ( ,or compatible Arduino shield)
- MIDI device

Device Connection
----
    Android [USB B port]------[USB A port] ADK + MIDI Shield [MIDI IN/OUT port] ------ MIDI device

Projects
----
- Library Project  
 - ADKMIDIDriver : The driver for connecting a MIDI device with ADK.

- Accessory firmware
 - MIDIAccessoryFirmware : The firmware for ADK board.

- Sample Project
 - ADKMIDIDriverSample : The sample implementation of the synthesizer / MIDI event logger.

Library Project Usage
----
Project setup

- Clone the library project.
- Import the library project into Eclipse workspace, and build it.
- Create new Android Project. And add the library project to the project.
- Override `jp.kshoji.driver.adkmidi.activity.AbstractMidiActivity`.
- Modify the AndroidManifest.xml file's application tag.
 - Add **uses-library** com.android.future.usb.accessory 
- Modify the AndroidManifest.xml file's activity tag.
 - Add **intent-filter** android.hardware.usb.action.USB_ACCESSORY_ATTACHED and **meta-data** to the overridden Activity.
 - Activity's **launchMode** must be "singleTask".
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


MIDI event receiving

- Implement the MIDI event handling method (named `"onMidi..."`) to receive MIDI events.

MIDI event sending

- Call AbstractMidiActivity's `getMidiOutputDevice()` method to get the instance on `MIDIOutputDevice`.
 - And call the instance's method (named `"onMidi..."`) to send MIDI events.

License
----
[Apache License, Version 2.0][Apache]
[Apache]: http://www.apache.org/licenses/LICENSE-2.0