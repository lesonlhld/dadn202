<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Autoload data from CSE_BBC server</title>

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">

	<!-- jQuery library -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>


	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<style>
		.grid-container {
			display: grid;
			grid-template-columns: auto auto auto;
			grid-gap: 5px;
			background-color: #FFFFFF;
			padding: 10px;
		}

		.grid-container>div {
			background-color: rgba(33, 150, 243, 0.2);
			text-align: center;
			padding: 20px 0;
			font-size: 30px;
		}
	</style>
</head>

<?php
define('TIMEZONE', 'Asia/Ho_Chi_Minh');
date_default_timezone_set(TIMEZONE);
// echo "Time loaded: " . (new \DateTime())->format('Y-m-d H:i:s') . "<br/>";

function multi_thread_curl($urlArray, $optionArray, $nThreads)
{

	//Group your urls into groups/threads.
	$curlArray = array_chunk($urlArray, $nThreads, $preserve_keys = true);

	//Iterate through each batch of urls.
	$ch = 'ch_';
	foreach ($curlArray as $threads) {

		//Create your cURL resources.
		foreach ($threads as $thread => $value) {

			${$ch . $thread} = curl_init();

			curl_setopt_array(${$ch . $thread}, $optionArray); //Set your main curl options.
			curl_setopt(${$ch . $thread}, CURLOPT_URL, $value); //Set url.

		}

		//Create the multiple cURL handler.
		$mh = curl_multi_init();

		//Add the handles.
		foreach ($threads as $thread => $value) {

			curl_multi_add_handle($mh, ${$ch . $thread});
		}

		$active = null;

		//execute the handles.
		do {

			$mrc = curl_multi_exec($mh, $active);
		} while ($mrc == CURLM_CALL_MULTI_PERFORM);

		while ($active && $mrc == CURLM_OK) {

			if (curl_multi_select($mh) != -1) {
				do {

					$mrc = curl_multi_exec($mh, $active);
				} while ($mrc == CURLM_CALL_MULTI_PERFORM);
			}
		}

		//Get your data and close the handles.
		foreach ($threads as $thread => $value) {

			$results[$thread] = curl_multi_getcontent(${$ch . $thread});

			curl_multi_remove_handle($mh, ${$ch . $thread});
		}

		//Close the multi handle exec.
		curl_multi_close($mh);
	}


	return $results;
}


function get_value()
{
	//Add whatever options here. The CURLOPT_URL is left out intentionally.
	//It will be added in later from the url array.
	$optionArray = array(

		CURLOPT_USERAGENT        => 'Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0', //Pick your user agent.
		CURLOPT_RETURNTRANSFER   => TRUE,
		CURLOPT_TIMEOUT          => 10

	);

	//Create an array of your urls.
	$urlArray = array(
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-led/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-speaker/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-lcd/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-button/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-touch/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-traffic/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-temp-humid',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-magnetic/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-soil/',
		'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-drv/',

		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-relay/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-sound/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-light/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-infrared/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-servo/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-accelerometer/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-time/',
		'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-gas/'

	);

	//Play around with this number and see what works best.
	//This is how many urls it will try to do at one time.
	$nThreads = 20;

	//To use run the function.
	$results = multi_thread_curl($urlArray, $optionArray, $nThreads);

	$showValue = array("Time loaded" => (new \DateTime())->format('Y-m-d H:i:s'));
	foreach ($results as $page) {
		$json = json_decode($page);
		$value = $json->last_value;
		$json = json_decode($value);
		$name = $json->name;
		$data = $json->data;
		$showValue[$name] = $data;
	}

	return $showValue;
}
header("Refresh:2;");
?>

<body>
	<?php
	$value = get_value();
	?>
	<div class="container">
		<div style="padding-left: 1%;">
			<h3>Time Loaded: <?= $value["Time loaded"] ?> <a href="#" onclick="window.location.reload()"><i class="fa fa-refresh" aria-hidden="true"></i></a></h3>

		</div>
		<div class="grid-container">
			<div id="LED">
				<h4>LED</h4><?= !isset($value["LED"]) ? "Error" : $value["LED"] ?>
			</div>
			<div id="SPEAKER">
				<h4>SPEAKER</h4><?= !isset($value["SPEAKER"]) ? "Error" : $value["SPEAKER"] ?>
			</div>
			<div id="LCD">
				<h4>LCD</h4><?= !isset($value["LCD"]) ? "Error" : $value["LCD"] ?>
			</div>
			<div id="BUTTON">
				<h4>BUTTON</h4><?= !isset($value["BUTTON"]) ? "Error" : $value["BUTTON"] ?>
			</div>
			<div id="TOUCH">
				<h4>TOUCH</h4><?= !isset($value["TOUCH"]) ? "Error" : $value["TOUCH"] ?>
			</div>
			<div id="TRAFFIC">
				<h4>TRAFFIC</h4><?= !isset($value["TRAFFIC"]) ? "Error" : $value["TRAFFIC"] ?>
			</div>
			<div id="TEMP-HUMID">
				<h4>TEMP-HUMID</h4><?= !isset($value["TEMP-HUMID"]) ? "Error" : $value["TEMP-HUMID"] ?>
			</div>
			<div id="MAGNETIC">
				<h4>MAGNETIC</h4><?= !isset($value["MAGNETIC"]) ? "Error" : $value["MAGNETIC"] ?>
			</div>
			<div id="SOIL">
				<h4>SOIL</h4><?= !isset($value["SOIL"]) ? "Error" : $value["SOIL"] ?>
			</div>
			<div id="DRV_PWM">
				<h4>DRV_PWM</h4><?= !isset($value["DRV_PWM"]) ? "Error" : $value["DRV_PWM"] ?>
			</div>
			<div id="RELAY">
				<h4>RELAY</h4><?= !isset($value["RELAY"]) ? "Error" : $value["RELAY"] ?>
			</div>
			<div id="SOUND">
				<h4>SOUND</h4><?= !isset($value["SOUND"]) ? "Error" : $value["SOUND"] ?>
			</div>
			<div id="LIGHT">
				<h4>LIGHT</h4><?= !isset($value["LIGHT"]) ? "Error" : $value["LIGHT"] ?>
			</div>
			<div id="INFRARED">
				<h4>INFRARED</h4><?= !isset($value["INFRARED"]) ? "Error" : $value["INFRARED"] ?>
			</div>
			<div id="SERVO">
				<h4>SERVO</h4><?= !isset($value["SERVO"]) ? "Error" : $value["SERVO"] ?>
			</div>
			<div id="ACCELEROMETER">
				<h4>ACCELEROMETER</h4><?= !isset($value["ACCELEROMETER"]) ? "Error" : $value["ACCELEROMETER"] ?>
			</div>
			<div id="TIME">
				<h4>TIME</h4><?= !isset($value["TIME"]) ? "Error" : $value["TIME"] ?>
			</div>
			<div id="GAS">
				<h4>GAS</h4><?= !isset($value["GAS"]) ? "Error" : $value["GAS"] ?>
			</div>
		</div>
	</div>
</body>

</html>