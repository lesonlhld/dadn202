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

		#TEMP-HUMID, #BUTTON {
			background-color: #ffcccc;
		}

		a:link {
			color: black;
			background-color: transparent;
			text-decoration: none;
		}

		a:visited {
			color: black;
			background-color: transparent;
			text-decoration: none;
		}

		a:hover {
			color: red;
			background-color: transparent;
			text-decoration: none;
		}

		a:active {
			color: yellow;
			background-color: transparent;
			text-decoration: underline;
		}
	</style>
</head>

<?php
define('TIMEZONE', 'Asia/Ho_Chi_Minh');
date_default_timezone_set(TIMEZONE);
// echo "Time loaded: " . (new \DateTime())->format('Y-m-d H:i:s') . "<br/>";

error_reporting(0);
ini_set('display_errors', 0);

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
	$urlAPIArray = array(
		'LED' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-led/',
		'SPEAKER' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-speaker/',
		'LCD' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-lcd/',
		'BUTTON' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-button/',
		'TOUCH' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-touch/',
		'TRAFFIC' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-traffic/',
		'TEMP-HUMID' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-temp-humid',
		'MAGNETIC' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-magnetic/',
		'SOIL' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-soil/',
		'DRV_PWM' => 'https://io.adafruit.com/api/v2/CSE_BBC/feeds/bk-iot-drv/',

		'RELAY' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-relay/',
		'SOUND' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-sound/',
		'LIGHT' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-light/',
		'INFRARED' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-infrared/',
		'SERVO' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-servo/',
		'ACCELEROMETER' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-accelerometer/',
		'TIME' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-time/',
		'GAS' => 'https://io.adafruit.com/api/v2/CSE_BBC1/feeds/bk-iot-gas/'
	);

	//Play around with this number and see what works best.
	//This is how many urls it will try to do at one time.
	$nThreads = 20;

	//To use run the function.
	$results = multi_thread_curl($urlAPIArray, $optionArray, $nThreads);

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

if (!isset($_GET["pause"]) || $_GET["pause"] == "false") {
	header("Refresh:2;");
}
?>

<body>
	<?php
	$value = get_value();

	$urlArray = array(
		'LED' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-led/',
		'SPEAKER' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-speaker/',
		'LCD' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-lcd/',
		'BUTTON' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-button/',
		'TOUCH' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-touch/',
		'TRAFFIC' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-traffic/',
		'TEMP-HUMID' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-temp-humid',
		'MAGNETIC' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-magnetic/',
		'SOIL' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-soil/',
		'DRV_PWM' => 'https://io.adafruit.com/CSE_BBC/feeds/bk-iot-drv/',

		'RELAY' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-relay/',
		'SOUND' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-sound/',
		'LIGHT' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-light/',
		'INFRARED' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-infrared/',
		'SERVO' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-servo/',
		'ACCELEROMETER' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-accelerometer/',
		'TIME' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-time/',
		'GAS' => 'https://io.adafruit.com/CSE_BBC1/feeds/bk-iot-gas/'
	);
	?>
	<div class="container">
		<div style="padding-left: 1%;">
			<h3>
				Time Loaded: <?= $value["Time loaded"] ?>
				<a href="javascript:window.location.reload(true)" onclick=""> <i class="fa fa-refresh" aria-hidden="true"></i></a>

				<?php
				if (!isset($_GET["pause"]) || $_GET["pause"] == "false") {
				?>
					<a href='#' onclick='window.location.href = window.location.href.split("?")[0] + "?pause=true"'> <i class="fa fa-pause" aria-hidden="true"></i></a>

				<?php } else {
				?>
					<a href='#' onclick='window.location.href = window.location.href.split("?")[0] + "?pause=false"'> <i class="fa fa-play" aria-hidden="true"></i></a>
				<?php }
				?>
			</h3>

		</div>
		<div class="grid-container">
			<div id="LED">
				<h4><a href='<?= $urlArray["LED"] ?>' target="_blank">LED</a></h4><?= !isset($value["LED"]) ? "Error" : $value["LED"] ?>
			</div>
			<div id="SPEAKER">
				<h4><a href='<?= $urlArray["SPEAKER"] ?>' target="_blank">SPEAKER</a></h4><?= !isset($value["SPEAKER"]) ? "Error" : $value["SPEAKER"] ?>
			</div>
			<div id="LCD">
				<h4><a href='<?= $urlArray["LCD"] ?>' target="_blank">LCD</a></h4><?= !isset($value["LCD"]) ? "Error" : $value["LCD"] ?>
			</div>
			<div id="BUTTON">
				<h4><a href='<?= $urlArray["BUTTON"] ?>' target="_blank">BUTTON</a></h4><?= !isset($value["BUTTON"]) ? "Error" : $value["BUTTON"] ?>
			</div>
			<div id="TOUCH">
				<h4><a href='<?= $urlArray["TOUCH"] ?>' target="_blank">TOUCH</a></h4><?= !isset($value["TOUCH"]) ? "Error" : $value["TOUCH"] ?>
			</div>
			<div id="TRAFFIC">
				<h4><a href='<?= $urlArray["TRAFFIC"] ?>' target="_blank">TRAFFIC</a></h4><?= !isset($value["TRAFFIC"]) ? "Error" : $value["TRAFFIC"] ?>
			</div>
			<div id="TEMP-HUMID">
				<h4><a href='<?= $urlArray["TEMP-HUMID"] ?>' target="_blank">TEMP-HUMID</a></h4><?= !isset($value["TEMP-HUMID"]) ? "Error" : $value["TEMP-HUMID"] ?>
			</div>
			<div id="MAGNETIC">
				<h4><a href='<?= $urlArray["MAGNETIC"] ?>' target="_blank">MAGNETIC</a></h4><?= !isset($value["MAGNETIC"]) ? "Error" : $value["MAGNETIC"] ?>
			</div>
			<div id="SOIL">
				<h4><a href='<?= $urlArray["SOIL"] ?>' target="_blank">SOIL</a></h4><?= !isset($value["SOIL"]) ? "Error" : $value["SOIL"] ?>
			</div>
			<div id="DRV_PWM">
				<h4><a href='<?= $urlArray["DRV_PWM"] ?>' target="_blank">DRV_PWM</a></h4><?= !isset($value["DRV_PWM"]) ? "Error" : $value["DRV_PWM"] ?>
			</div>
			<div id="RELAY">
				<h4><a href='<?= $urlArray["RELAY"] ?>' target="_blank">RELAY</a></h4><?= !isset($value["RELAY"]) ? "Error" : $value["RELAY"] ?>
			</div>
			<div id="SOUND">
				<h4><a href='<?= $urlArray["SOUND"] ?>' target="_blank">SOUND</a></h4><?= !isset($value["SOUND"]) ? "Error" : $value["SOUND"] ?>
			</div>
			<div id="LIGHT">
				<h4><a href='<?= $urlArray["LIGHT"] ?>' target="_blank">LIGHT</a></h4><?= !isset($value["LIGHT"]) ? "Error" : $value["LIGHT"] ?>
			</div>
			<div id="INFRARED">
				<h4><a href='<?= $urlArray["INFRARED"] ?>' target="_blank">INFRARED</a></h4><?= !isset($value["INFRARED"]) ? "Error" : $value["INFRARED"] ?>
			</div>
			<div id="SERVO">
				<h4><a href='<?= $urlArray["SERVO"] ?>' target="_blank">SERVO</a></h4><?= !isset($value["SERVO"]) ? "Error" : $value["SERVO"] ?>
			</div>
			<div id="ACCELEROMETER">
				<h4><a href='<?= $urlArray["ACCELEROMETER"] ?>' target="_blank">ACCELEROMETER</a></h4><?= !isset($value["ACCELEROMETER"]) ? "Error" : $value["ACCELEROMETER"] ?>
			</div>
			<div id="TIME">
				<h4><a href='<?= $urlArray["TIME"] ?>' target="_blank">TIME</a></h4><?= !isset($value["TIME"]) ? "Error" : $value["TIME"] ?>
			</div>
			<div id="GAS">
				<h4><a href='<?= $urlArray["GAS"] ?>' target="_blank">GAS</a></h4><?= !isset($value["GAS"]) ? "Error" : $value["GAS"] ?>
			</div>
		</div>
	</div>
</body>

</html>