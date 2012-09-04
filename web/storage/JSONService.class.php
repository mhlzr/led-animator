<?php
class JSONService {

	public static function printAsJSON($jsonObject) {

		header('Content-type: application/json');

		if (is_string($jsonObject)) {
			echo $jsonObject;
		} else {
			echo(json_encode($jsonObject));
		}

	}

	public static function printStatus($boolean) {
		JSONService::printAsJSON(array("status" => ($boolean) ? "true" : "false"));
	}

}
?>