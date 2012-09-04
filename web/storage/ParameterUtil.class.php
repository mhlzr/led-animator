<?php
class ParameterUtil {

	public static function exists($req, $params) {
		for ($i = 0; $i < sizeof($params); $i++) {
			if (!array_key_exists($params[$i], $req) && !isset($req[$params[$i]])) {
				return false;
			}
		}
		return true;
	}

	public static function isString($val) {
		if (!is_null($val) && is_string($val) && !empty($val)) {
			return true;
		}

		return false;
	}
	
	public static function convertString($str){
		return mysql_real_escape_string((addslashes($str)));
	}

}
?>