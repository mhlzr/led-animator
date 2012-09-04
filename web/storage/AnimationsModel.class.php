<?php

require_once ("DatabaseModel.class.php");
require_once ("JSONService.class.php");
require_once ("Config.class.php");

class AnimationsModel {

	private $db_connection;

	public function __construct() {
		$this -> db_connection = new DatabaseModel();
	}

	public function __destruct() {
		$this -> db_connection = null;
	}

	public function getAllAnimations($author, $type) {
		JSONService::printAsJSON($this -> db_connection -> getAnimations($author, $type));
	}

	public function isAvailable($author, $title) {
		JSONService::printStatus($this -> db_connection -> isAvailable($author, $title));
	}

	public function getAnimationByAuthorAndTitle($author, $title) {
		JSONService::printAsJSON($this -> readFile(Config::ANIMATION_PATH . $this -> db_connection -> getAnimationUniqueIdByAuthorAndTitle($author, $title) . ".json"));
	}

	public function getAnimationById($id) {
		JSONService::printAsJSON($this -> readFile(Config::ANIMATION_PATH . $this -> db_connection -> getAnimationUniqueIdById($id) . ".json"));
	}

	public function addAnimation($author, $title, $type, $json, $thumbnail) {

		if (!$this -> db_connection -> isAvailable($author, $title)) {
			JSONService::printStatus(false);
			return;
		}

		$uniqid = uniqid();

		$jsonFileOp = $this -> saveJSONData($uniqid, $json);
		$thumbnailOp = $this -> saveThumbnailData($uniqid, $thumbnail);
		$dbOp = $this -> db_connection -> insertAnimation($type, $author, $title, $uniqid);

		JSONService::printStatus($jsonFileOp && $thumbnailOp && $dbOp);

	}

	public function updateAnimationByAuthorAndTitle($author, $title, $json, $thumbnail) {

		//if animation does not exist, don't update
		if ($this -> db_connection -> isAvailable($author, $title)) {
			JSONService::printStatus(false);
			return;
		}

		$uniqid = $this -> db_connection -> getAnimationUniqueIdByAuthorAndTitle($author, $title);
		$jsonFileOp = $this -> saveJSONData($uniqid, $json);
		$thumbnailOp = $this -> saveThumbnailData($uniqid, $thumbnail);

		JSONService::printStatus($jsonFileOp && $thumbnailOp);
	}

	private function saveJSONData($uniqid, $json) {
		return $this -> saveFile(Config::ANIMATION_PATH . $uniqid . ".json", $json);
	}

	private function saveThumbnailData($uniqid, $thumb) {
		$thumbnailUrl = Config::THUMBNAIL_PATH . $uniqid . ".png";
		$saveOP = $this -> saveFile($thumbnailUrl, $this -> convertImageData($thumb));
		$resizeOp = $this -> resizeImageFile($thumbnailUrl);
		return $saveOP && $resizeOp;
	}

	private function convertImageData($imgData) {
		$imgData = str_replace('data:image/png;base64,', '', $imgData);
		$imgData = str_replace(' ', '+', $imgData);
		return base64_decode($imgData);
	}

	private function resizeImageFile($url) {
		//get original wxh
		list($width, $height) = getimagesize($url);

		// Load
		$thumb = imagecreatetruecolor(Config::THUMBNAIL_WIDTH, Config::THUMBNAIL_HEIGHT);
		imagealphablending($thumb, false);
		imagesavealpha($thumb, true);

		$source = imagecreatefrompng($url);

		// Resize
		imagealphablending($source, true);
		imagecopyresampled($thumb, $source, -1, -1, -1, -1, Config::THUMBNAIL_WIDTH + 1, Config::THUMBNAIL_HEIGHT + 1, $width, $height);

		// Output
		imagepng($thumb, $url, 0);
		imagedestroy($thumb);

		return true;
	}

	private function readFile($fileName) {
		if (!file_exists($fileName)) {
			return false;
		}

		$handle = fopen($fileName, "r");
		return file_get_contents($fileName);
	}

	private function saveFile($fileName, $content) {
		// if (file_exists($fileName)) {
		// return false;
		// }

		//file_put_contents

		$handle = fopen($fileName, "w");

		if (!fwrite($handle, $content)) {
			fclose($handle);
			return false;
		}

		fclose($handle);

		return true;
	}

}
?>
