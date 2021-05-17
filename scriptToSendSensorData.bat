@echo off
if NOT exist "C:\Program Files\mosquitto" (
	echo "mosquitto is not installed"
	exit
)

cd "C:\Program Files\mosquitto"

REM mosquitto_pub -h io.adafruit.com -p 1883 -u lesonlhld -P aio_WwjF84LarniCFSKcbFyhnFEFnqlG -t leson0108/feeds/sensor

:Loop
echo "Automatically send data every 5 minutes ..."
@echo off
set /a temperature = (%RANDOM%*20/32768) + 15
set /a humidity = (%RANDOM%*35/32768) + 65
set randomData=%temperature%-%humidity%

echo %randomData%
mosquitto_pub -h io.adafruit.com -p 1883 -u lesonlhld -P aio_WwjF84LarniCFSKcbFyhnFEFnqlG -t leson0108/feeds/sensor -m "%randomData%"
timeout 300
GOTO :Loop