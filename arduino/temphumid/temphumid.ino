#include <DHT11.h>  //아두이노 온습도센서 DHT11을 사용하기위해 라이브러리를 불러옵니다.

DHT11 dht11(A0);  /*불러온 라이브러리 안에 몇번 PIN에서 데이터값이 나오는지
                     설정해줘야 합니다. 아날로그 0번 PIN인 A0으로 설정했습니다.
*/
int RED = 3;

void setup()
{
  Serial.begin(9600); /*온습도값을 PC모니터로 확인하기위해 시리얼 통신을
                         설정해 줍니다.
*/
  pinMode(RED,OUTPUT);
}

void loop()
{
  float temp, humi; /*온습도 값이 저장될 변수를 만들어줍니다. 온습도값이
                      소수점이기때문에 float변수를 사용했습니다.
*/
  int result = dht11.read(humi, temp); /*DHT.h 함수안에 dht11이라는 메소드를 사용해서
                                        현재 온습도 값을 자동으로 계산해줍니다.
                                        계산후 현재 온습도가 데이터가 나오는지 아닌지
                                        판단한 리턴값을 result 변수에 저장해줍니다.
                                        dht11메소드 에서는 온습도가 잘 감지되면 0이라는
                                        리턴값을 보내줍니다.
*/

  if (result == 0)  /*온습도가 잘측정이되서 result변수에 0이라는 값이 들어오면
                      if문이 실행됩니다.
*/
  {
    Serial.print("temperature:");
    Serial.print(temp); //온도값이 출력됩니다.
    Serial.print(" humidity:");
    Serial.print(humi); //습도값이 출력됩니다.
    Serial.println();
  }
  else
  {
    Serial.println();
    Serial.print("Error No :"); //result 값이 0이 아니라 다른숫자가 저장이되면 출력됩니다.
    Serial.print(result);
    Serial.println();
  }

  if(temp >= 20 && humi >= 39 ){
    digitalWrite(RED,HIGH);
  }
  else{
    digitalWrite(RED,LOW);
  }


  delay(DHT11_RETRY_DELAY); /*일반적인 딜레이 값이 아니라 DHT11에서 권장하는
                              딜레이함수를 사용해줘야 정상적인 값이 나옵니다.
*/

}
