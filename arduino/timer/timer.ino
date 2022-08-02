
unsigned long timeVal = 0; //이전시간
unsigned long millisTime = 0; //현재시간
unsigned long countTime = 0; //카운트시작시간
int d1, d2, d3, d4;//자리 숫자
boolean state = true;//타이머 동작 제어 
 
void setup()
{
  Serial.begin(9600);
  
  timeVal=0;
}
 
void loop()
{
  if(state==true){ //카운트 시작
    if(millis()-timeVal>=10){ //1초단위로 출력
      timeVal=millis();
      millisTime = (millis()-countTime)/10;
      d1 = millisTime%60; //1의 자리
      d2 = (millisTime/60)%60;//10의 자리
      d3 = (millisTime/3600)%60;//100의 자리

      Serial.print(d3);
      Serial.print(" : ");
      Serial.print(d2);
      Serial.print(" : ");
      Serial.println(d1);       
    }
  } 
}
