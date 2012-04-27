Android ADK MIDI �h���C�o
====

Android Open Accessory���g����ADK MIDI�̃h���C�o�ł��B

- Android GingerBread(2.3.4)�̒[���Ŏg���܂��B
- �V�[�P���T��y��ȂǁA���K�V�[��MIDI�@����T�|�[�g

����
----
- ��x��1��ADK�����ڑ��ł��܂���B

�K�v�Ȃ���
----
- Android : OS�o�[�W����2.3.4�ȍ~(API Level 10)�ŁA�A�N�Z�T���̃��C�u�������񋟂���Ă��邱�ƁB
- Arduino�݊���ADK�{�[�h(Accessory Development Kit Board)
- Sparkfun��MIDI�V�[���h(�������́A������Arduino�V�[���h)
- MIDI�@��

�f�o�C�X�̐ڑ�
----
    Android [USB B�|�[�g]------[USB A�|�[�g] ADK + MIDI�V�[���h [MIDI IN/OUT�|�[�g] ------ MIDI�@��

�v���W�F�N�g
----
- ���C�u����  
 - ADKMIDIDriver : �{���C�u����

- �A�N�Z�T��
 - MIDIAccessoryFirmware : ADK�{�[�h�p�t�@�[���E�F�A

- �T���v��
 - ADKMIDIDriverSample : ���C�u�������g���Ď��������A�V���Z�T�C�U�EMIDI�C�x���g���K�[�̗�

���C�u�����v���W�F�N�g�̎g����
----
�v���W�F�N�g�̐ݒ�

- ���C�u�����v���W�F�N�g��clone���܂��B
- Eclipse�̃��[�N�X�y�[�X�Ƀ��C�u�����v���W�F�N�g���C���|�[�g���A�r���h���܂��B
- �V����Android Project���쐬���A�v���W�F�N�g�̃��C�u�����Ƀ��C�u�����v���W�F�N�g�� �ǉ����܂��B
- `jp.kshoji.driver.adkmidi.activity.AbstractMidiActivity` ���I�[�o�[���C�h����Activity�����܂��B
- AndroidManifest.xml �t�@�C���� application �^�O��ύX���܂��B
 - **uses-library** com.android.future.usb.accessory ��ǉ����܂��B
- AndroidManifest.xml �t�@�C���� activity �^�O��ύX���܂��B
 - **intent-filter** android.hardware.usb.action.USB_DEVICE_ATTACHED �� **meta-data** �� �I�[�o�[���C�h���� Activity �ɑ΂��Ēǉ����܂��B
 - Activity�� **launchMode** �� "singleTask" �ɂ��܂��B
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


MIDI �C�x���g�̎�M

- MIDI�C�x���g���������郁�\�b�h (`"onMidi..."` �Ƃ������O)���������܂��B

MIDI �C�x���g�̑��M

- `MIDIOutputDevice`�̃C���X�^���X���擾���邽�߂ɁAAbstractMidiActivity ��`getMidiOutputDevice()`���\�b�h���Ăт܂��B
 - ���̃C���X�^���X��`"onMidi..."` �Ƃ������O�̃��\�b�h���ĂԂ�MIDI�C�x���g�����M�ł��܂��B

���C�Z���X
----
[Apache License, Version 2.0][Apache]
[Apache]: http://www.apache.org/licenses/LICENSE-2.0