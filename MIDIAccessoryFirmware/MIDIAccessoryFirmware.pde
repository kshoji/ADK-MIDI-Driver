#include <Max3421e.h>
#include <Usb.h>
#include "AndroidAccessory_silent.h"

#define PARAM_BUFFER_SIZE (128)
#define STAT_LED_1 (7)
#define STAT_LED_2 (6)

AndroidAccessory acc("kshoji",
"MidiShield",
"DemoKit Arduino Board",
"1.0",
"https://github.com/kshoji/ADK-MIDI-Driver",
"0000000000000000");

void setup() {
  acc.powerOn();

  pinMode(STAT_LED_1, OUTPUT);
  pinMode(STAT_LED_2, OUTPUT);
  digitalWrite(STAT_LED_1, LOW);
  digitalWrite(STAT_LED_2, LOW);
  Serial.begin(31250);
  digitalWrite(STAT_LED_1, HIGH);
  digitalWrite(STAT_LED_2, HIGH);

}

byte msg[PARAM_BUFFER_SIZE];
int length = 0;
void loop() {
  if (acc.isConnected()) {
    // Receive commands
    while ((length = acc.read(msg, sizeof(msg), 1)) > 0) {
      digitalWrite(STAT_LED_1, LOW);
      Serial.write(msg, length);
      digitalWrite(STAT_LED_1, HIGH);
    }

    // Send commands
    length = 0;
    while (Serial.available() && length < PARAM_BUFFER_SIZE) {
      msg[length] = Serial.read();
      length++;
    }
    if (length) {
      digitalWrite(STAT_LED_2, LOW);
      acc.write(msg, length);
      digitalWrite(STAT_LED_2, HIGH);
    }
  }
}


