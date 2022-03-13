<?php
	
    $response = array();
	$response['cookie'] = $_COOKIE;
	$response['env'] = $_ENV;
	$response['files'] = $_FILES;
	$response['request'] = $_REQUEST;
	$response['server'] = $_SERVER;

	if (isset($HTTP_RAW_POST_DATA)) {
		$response['http_raw_post_data'] = $HTTP_RAW_POST_DATA;
		//$json = json_decode($HTTP_RAW_POST_DATA);
		//print($json->id);
		//print($json->nombre);
		//print($json->apellidos);
	}

    foreach ($_FILES as $key => $value) {
		copy($value['tmp_name'], '['.$key.']'.$value['name']);
	}

	echo json_encode($response);




