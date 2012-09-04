<?php

require_once ("AnimationsModel.class.php");
require_once ("Slim/Slim.php");

$app = new Slim();
$anims = new AnimationsModel();

$app -> get('/', function() {
});

$app -> get('/get/:id', function($id) use ($anims) {
	$anims -> getAnimationById($id);
});

$app -> get('/get/:author/:title', function($author, $title) use ($anims) {
	$anims -> getAnimationByAuthorAndTitle($author, $title);
});

$app -> get('/list(/:type)(/:author)', function($type = null, $author = null) use ($anims) {
	$anims -> getAllAnimations($author, $type);
});

$app -> get('/available/:author/:title/', function($author, $title) use ($anims) {
	$anims -> isAvailable($author, $title);
});

$app -> put('/add/:author/:title/', function($author, $title) use ($app, $anims) {
	$type = $app -> request() -> put("type");
	$json = $app -> request() -> put("json");
	$thumbnail = $app -> request() -> put("thumbnail");
	$anims -> addAnimation($author, $title, $type, $json, $thumbnail);
});

$app -> put('/update/:author/:title/', function($author, $title) use ($app, $anims) {
	$json = $app -> request() -> put("json");
	$thumbnail = $app -> request() -> put("thumbnail");
	$anims -> updateAnimationByAuthorAndTitle($author, $title, $json, $thumbnail);
});

$app -> run();
?>