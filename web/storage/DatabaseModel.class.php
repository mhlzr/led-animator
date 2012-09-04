<?php

require_once 'Config.class.php';

class DatabaseModel {

	//CONFIG
	private $db_host = "localhost";
	private $db_name = "ledanimationstorage";
	private $db_user = "root";
	private $db_pass = "";

	private $database;

	public function connect() {
		$this -> database = new mysqli($this -> db_host, $this -> db_user, $this -> db_pass, $this -> db_name);

		if (mysqli_connect_errno() === 0) {
		} else {
			echo "DATABASE-CONNECTION FAILED";
		}
		return $this;
	}

	public function getAnimations($author, $type) {

		$this -> connect();

		$stmt;

		if ($author === null) {
			if ($type === null) {
				$stmt = $this -> database -> prepare("SELECT id,type,author,title,uniqid FROM animations ORDER BY date DESC");
			} else {
				$stmt = $this -> database -> prepare("SELECT id,type,author,title,uniqid FROM animations WHERE type = ? ORDER BY date DESC");
				$stmt -> bind_param("i", $type);
			}

		} else {
			if ($type === null) {
				$stmt = $this -> database -> prepare("SELECT id,type,author,title,uniqid FROM animations WHERE author = ? ORDER BY date DESC");
				$stmt -> bind_param("s", $author);
			} else {
				$stmt = $this -> database -> prepare("SELECT id,type,author,title,uniqid FROM animations WHERE author = ? AND type = ? ORDER BY date DESC");
				$stmt -> bind_param("si", $author, $type);
			}

		}

		$stmt -> bind_result($r_id, $r_type, $r_author, $r_title, $r_uniqid);
		$stmt -> execute();

		$result = new stdClass();
		$result -> data = array();

		$i = 0;
		$current;
		while ($stmt -> fetch()) {
			$current = $result -> data[$i++] = new stdClass;
			$current -> id = $r_id;
			$current -> author = $r_author;
			$current -> title = $r_title;
			$current -> type = $r_type;
			$current -> jsonUrl = Config::ANIMATION_PATH . $r_uniqid . ".json";
			$current -> thumbnailUrl = Config::THUMBNAIL_PATH . $r_uniqid . ".png";
		}

		$stmt -> close();
		$this -> disconnect();

		if (sizeof($result -> data) == 0)
			$result = null;

		return $result;

	}

	public function insertAnimation($type, $author, $title, $uniqid) {

		$this -> connect();
		$stmt = $this -> database -> prepare("INSERT INTO animations (type, author, title, uniqid) VALUES(?,?,?,?)");
		$stmt -> bind_param("ssss", $type, $author, $title, $uniqid);
		$stmt -> execute();
		$stmt -> close();
		$this -> disconnect();

		return true;
	}

	public function getAnimationUniqueIdByAuthorAndTitle($author, $title) {
		$this -> connect();
		$stmt = $this -> database -> prepare("SELECT uniqid FROM animations WHERE author=? AND title=? LIMIT 1");
		$stmt -> bind_param("ss", $author, $title);
		$stmt -> bind_result($r_uniqid);
		$stmt -> execute();

		$stmt -> fetch();
		$stmt -> close();
		$this -> disconnect();

		return $r_uniqid;
	}

	public function getAnimationUniqueIdById($id) {
		$this -> connect();
		$stmt = $this -> database -> prepare("SELECT uniqid FROM animations WHERE id=? LIMIT 1");
		$stmt -> bind_param("i", $id);
		$stmt -> bind_result($r_uniqid);
		$stmt -> execute();

		$stmt -> fetch();
		$stmt -> close();
		$this -> disconnect();

		return $r_uniqid;
	}

	public function isAvailable($author, $title) {
		$this -> connect();
		$stmt = $this -> database -> prepare("SELECT count(*) FROM animations WHERE author=? AND title=? LIMIT 1");
		$stmt -> bind_param("ss", $author, $title);
		$stmt -> bind_result($count);
		$stmt -> execute();

		$count = 0;
		while ($stmt -> fetch()) {
			$count++;
		}

		$stmt -> close();
		$this -> disconnect();

		return ($count - 1 == 0) ? true : false;
	}

	public function disconnect() {
		//$this -> database -> close();
	}

}
?>