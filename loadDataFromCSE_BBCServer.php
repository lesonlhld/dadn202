<?php
define('TIMEZONE', 'Asia/Ho_Chi_Minh');
date_default_timezone_set(TIMEZONE);
echo "Time loaded: " . (new \DateTime())->format('Y-m-d H:i:s') . "<br/>";

function multi_thread_curl($urlArray, $optionArray, $nThreads) {

  //Group your urls into groups/threads.
  $curlArray = array_chunk($urlArray, $nThreads, $preserve_keys = true);

  //Iterate through each batch of urls.
  $ch = 'ch_';
  foreach($curlArray as $threads) {      

      //Create your cURL resources.
      foreach($threads as $thread=>$value) {

      ${$ch . $thread} = curl_init();

        curl_setopt_array(${$ch . $thread}, $optionArray); //Set your main curl options.
        curl_setopt(${$ch . $thread}, CURLOPT_URL, $value); //Set url.

        }

      //Create the multiple cURL handler.
      $mh = curl_multi_init();

      //Add the handles.
      foreach($threads as $thread=>$value) {

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
      foreach($threads as $thread=>$value) {

      $results[$thread] = curl_multi_getcontent(${$ch . $thread});

      curl_multi_remove_handle($mh, ${$ch . $thread});

      }

      //Close the multi handle exec.
      curl_multi_close($mh);

  }


  return $results;

} 



//Add whatever options here. The CURLOPT_URL is left out intentionally.
//It will be added in later from the url array.
$optionArray = array(

  CURLOPT_USERAGENT        => 'Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0',//Pick your user agent.
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


foreach($results as $page){
	
/*
  $dom = new DOMDocument();
  @$dom->loadHTML($page);
  $xpath = new DOMXPath($dom);
  $hrefs = $xpath->evaluate("/html/body//a");

  for($i = 0; $i < $hrefs->length; $i++){

    $href = $hrefs->item($i);
    $url = $href->getAttribute('href');
    $url = filter_var($url, FILTER_SANITIZE_URL);
    // validate url
    if(!filter_var($url, FILTER_VALIDATE_URL) === false){
    echo '<a href="'.$url.'">'.$url.'</a><br />';
    }

  }*/
	$json = json_decode($page);
	$value = $json->last_value;
	$json = json_decode($value);
	$name = $json->name;
	$data = $json->data;
	
	echo "Name: " . $name . ", Data: " . $data . "<br/>";
}

echo (new \DateTime())->format('Y-m-d H:i:s') . "<br/>";
?>