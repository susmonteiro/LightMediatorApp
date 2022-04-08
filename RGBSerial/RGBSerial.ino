#include <string.h>
int red_light_pin= 11;
int green_light_pin = 10;
int blue_light_pin = 9;
void setup() {
  Serial.begin(9600);
  pinMode(red_light_pin, OUTPUT);
  pinMode(green_light_pin, OUTPUT);
  pinMode(blue_light_pin, OUTPUT);
  RGB_color(255, 255, 255);
}

//
//  >> Note: add space to end
//

void loop() {
  if(Serial.available()){
      String input = Serial.readStringUntil('\n');
      Serial.println(input);
      int rgb[3];
      int indexer = 0;
      int numberStart = 0;
      for (int i = 0; i<input.length(); i++) {
//        Serial.println(input[i]);
        if(input[i] == ' ' || input[i] == '\n') {
             rgb[indexer] = input.substring(numberStart, i).toInt();
//             Serial.println(input.substring(numberStart, i)+ "I: "+ i);
             numberStart = i+1;
             indexer += 1;
        }
      }
      Serial.println("Stored:");
      Serial.println(rgb[0]);
      Serial.println(rgb[1]);
      Serial.println(rgb[2]);
      RGB_color(rgb[0], rgb[1], rgb[2]);

    }
}

void RGB_color(int red_light_value, int green_light_value, int blue_light_value)
 {
  analogWrite(red_light_pin, red_light_value);
  analogWrite(green_light_pin, green_light_value);
  analogWrite(blue_light_pin, blue_light_value);
}
