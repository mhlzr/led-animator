/*
 Client for the LED Animation
 handles 2D & 3D Commands
 */

#include <Arduino.h>
#include <Rainbowduino.h>

const byte MAX_LEDS_PER_AXIS = 8;
const byte TOKEN_HEADER_START = 0xFF;
const byte TOKEN_NEXT_FRAME = 0xAA;
const byte TOKEN_BLANK_DISPLAY = 0x00;

byte ledsPerAxis = MAX_LEDS_PER_AXIS;
byte dimension; //2:2D, 3:3D

byte headBuffer[3];
byte headBufferIndex = 0;
boolean incomingDataIsCommand = true;

byte incomingData[6];
byte incomingDataIndex = 0;

byte frameBuffer3D[MAX_LEDS_PER_AXIS][MAX_LEDS_PER_AXIS][MAX_LEDS_PER_AXIS][3];

void setup()
{
  Serial.begin(28800);
  Rb.init();
}



void loop()
{
  if (Serial.available() <= 0) {
    return;
  }

  byte incomingByte = Serial.read();

  if(incomingByte == TOKEN_HEADER_START || incomingDataIsCommand){
    searchForCommand(incomingByte);
    return;
  }
  else{
    //Pixeldata
    incomingData[incomingDataIndex] = incomingByte;
    incomingDataIndex++;

    //Pixel komplett
    if(incomingDataIndex==6){
      processPixel();
      incomingDataIndex = 0;
    }

  }

}

void updateDisplay(){

  int i, x, y, z = 0;

  if(dimension == 2){
    for (i = 0; i < ledsPerAxis*ledsPerAxis; ++i) {
      x = i / ledsPerAxis;
      y = i % ledsPerAxis;
      Rb.setPixelXY(y, x, frameBuffer3D[x][y][0][0], frameBuffer3D[x][y][0][1], frameBuffer3D[x][y][0][2]);
    }
  }

  else if(dimension == 3){
    for (x = 0; x < ledsPerAxis; x++) {
      for (y = 0; y < ledsPerAxis; y++) {
        for (z = 0; z < ledsPerAxis; z++) {
          Rb.setPixelZXY(z, x, y, frameBuffer3D[x][y][z][0], frameBuffer3D[x][y][z][1], frameBuffer3D[x][y][z][2]);
        }
      }
    }

  }

  incomingDataIndex = 0;
  
  clearframeBuffer3D();
}

void blankDisplay(){
  Rb.blankDisplay();
}

void processPixel(){

  byte x = incomingData[0];
  byte y = incomingData[1];
  byte z = incomingData[2];

  byte r = incomingData[3];
  byte g = incomingData[4];
  byte b = incomingData[5];

  if(dimension == 2){
    z = 0;
  }

  frameBuffer3D[x][y][z][0] = r;
  frameBuffer3D[x][y][z][1] = g;
  frameBuffer3D[x][y][z][2] = b;

}


void searchForCommand(byte incomingByte){

  //Buffer nicht voll
  if(headBufferIndex<3){

    //Token vorhanden
    if(incomingByte == TOKEN_HEADER_START){
      incomingDataIsCommand = true;
      memset(headBuffer,0,3);
      headBufferIndex = 0;
    }

    headBuffer[headBufferIndex] = incomingByte; 
    headBufferIndex++;


    //Buffer ist voll
    if(headBufferIndex==3 && headBuffer[0] == TOKEN_HEADER_START){

      if(headBuffer[1] == TOKEN_NEXT_FRAME) updateDisplay();
      else if (headBuffer[1] == TOKEN_BLANK_DISPLAY) blankDisplay();
      else{
        dimension = headBuffer[1];
        ledsPerAxis = headBuffer[2];

        //alle Daten zurück auf 0 setzen
        clearframeBuffer3D();


      }

      memset(headBuffer,0,3);
      headBufferIndex=0;
      incomingDataIsCommand = false;
    }

    return;
  }

  //Löschen und von vorne
  else{
    memset(headBuffer,0,3);
    headBufferIndex=0;
    incomingDataIsCommand = false;
    return;
  }
}


void clearframeBuffer3D(){

  int x, y, z = 0;

  for (x = 0; x < ledsPerAxis; x++) {
    for (y = 0; y < ledsPerAxis; y++) {
      for (z = 0; z < ledsPerAxis; z++) {
        frameBuffer3D[x][y][z][0] = frameBuffer3D[x][y][z][1] = frameBuffer3D[x][y][z][2] = 0;
      }
    }
  }
}






























