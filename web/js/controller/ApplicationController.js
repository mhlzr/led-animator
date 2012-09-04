/**
 * @author Matthieu Holzer
 */
 
var TOOL = {
	"POINTTOOL" : 1,
	"LINETOOL" : 2,
	"PLANETOOL" : 3,
	"CUBETOOL" : 4,
	"COLORPICKER" : 5,
	"ERASER" : 6			
};

var ApplicationController = (function(previewContainer, timelineContainer) {
	

			
	var _mode,
		_ledsPerAxis,
		_previewContainer = previewContainer[0],
		_preview = null,
		_canvas,
		_ctx,
		_currentFrame,
		_timeline,
		_webSocketService,
		_isConnected = false;


	var _currentTool = TOOL.POINTTOOL;
	var _animation;
	
		
	function init(){
				
		//Remove existing canvas
		$("#preview-container").empty().append('<canvas width="400" height="400"></canvas>');

		_preview = _timeline = null;

		_webSocketService = new WebSocketService();

			
		_currentFrame = new Frame(_mode, _ledsPerAxis);
		_animation.addFrame(_currentFrame);

		_canvas = $("#preview-container canvas")[0];
		_ctx = _canvas.getContext("2d");

		
		if(_mode==2){
			_preview = Preview2D.init(_canvas,_previewContainer).setFrame(_currentFrame);
			_preview.draw();
		}
		else{
			_preview = Preview3D.init(_canvas,_previewContainer,_ledsPerAxis).setFrame(_currentFrame);
			_canvas = $("#preview-container canvas")[0];
			_ctx = _canvas.getContext("2d");
			_preview.draw();
			}

				
		_timeline = new Timeline(_animation, timelineContainer);
		_currentFrame.thumbnailString = _canvas.toDataURL("image/png");
		
		_timeline.draw();
		
		$(_previewContainer).bind('pointSelected', function(event) {

		switch(_currentTool){
			case TOOL.POINTTOOL:
				var color = ($("#randomColor:checked").length==1) ? "random" : $("#color").val();
				if(_mode==2) _currentFrame.set2DPoint(event.x, event.y, color);
				else _currentFrame.set3DPoint(event.x,event.y, event.z, color);
				break;
			case TOOL.COLORPICKER:
				var color = (_mode===2) ? _currentFrame.get2DPoint(event.x, event.y) : _currentFrame.get3DPoint(event.x, event.y, event.z);
				if(color !== "#000000" && color !== "random"){ 
					$("#color").val(color);
					$("#color").css("background-color",color);
				}
				break;
			case TOOL.ERASER :
				if(_mode==2) _currentFrame.set2DPoint(event.x, event.y, "#000000");
				else _currentFrame.set3DPoint(event.x,event.y, event.z, "#000000");
				break;
			default : break;
			}
		
				
					
		_animation.updateFrame(_currentFrame);
		_preview.draw();
		_currentFrame.thumbnailString = _canvas.toDataURL("image/png");
		_timeline.update();
					
	});
	
	}
	
	return {
	
	
		createAnimation : function(author, title, mode, ledsPerAxis){
			_mode = mode;
			_ledsPerAxis = ledsPerAxis;
			
			_animation = new Animation(author,title,_mode,_ledsPerAxis);

			init();
		},

	
	
		currentFrame : function () {return _currentFrame},
			
		onKeyDown : function(event){
			
		var keyCommandMap = [
			{ "key" : $.ui.keyCode.LEFT, "ctrl" : false, "fn" : function() {console.log("LEFT")} },
			{ "key" : $.ui.keyCode.RIGHT,   "ctrl" : false, "fn" : function() {console.log("RIGHT")} },
			{ "key" : 32, "ctrl": false, "fn": controller.addFrame} , //SPACE
			{ "key" : 46, "ctrl": false, "fn": controller.removeFrame} , //DELETE
			{ "key" : 80, "ctrl": true, "fn": controller.playCurrentAnimation} , //CTRL + p
			{ "key" : 83, "ctrl": true, "fn": controller.saveCurrentAnimation} , //CTRL + s
			{ "key" : 78, "ctrl": true, "fn" : function() {console.log("NEW ANIMATION")} }, //CTRL + n
		];
		
			for(var i = 0;i<keyCommandMap.length;i++){
				if(event.keyCode == keyCommandMap[i]["key"] && event.ctrlKey == keyCommandMap[i]["ctrl"]){
					event.preventDefault();	
					keyCommandMap[i]["fn"].call();
					continue;
				}
			}

		},
		
		
		updatePreview: function(){
			_preview.draw();
		},
		
		onFrameSelect: function(event){
			_currentFrame = event.selectedFrame;
			_animation.setCurrentFrame(_currentFrame);
			_preview.setFrame(_currentFrame);
			_preview.draw();
			_timeline.draw();
			
			$("#frame-duration").val(_currentFrame.duration);	
					
				//TODO
					//De-/Activate the removeFrame-Button
					if(_animation.getFrameCount() > 1)
						$("#animation-controls-removeframe").button("enable");
					else 
						$("#animation-controls-removeframe").button("disable");
		},
		
		addFrame: function (event){
				
				_currentFrame.thumbnailString = _canvas.toDataURL("image/png");
				_currentFrame = new Frame(_mode, _ledsPerAxis);
				_animation.addFrame(_currentFrame);
				
				_animation.setCurrentFrame(_currentFrame);
				_preview.setFrame(_currentFrame);
								
				_preview.draw();
				_currentFrame.thumbnailString = _canvas.toDataURL("image/png");
				_timeline.draw();
		},
		
		removeFrame: function(){
					_animation.removeFrame(_currentFrame);
					_currentFrame = _animation.getFrameById(0);
					_preview.setFrame(_currentFrame);
					_preview.draw();
					_currentFrame.thumbnailString = _canvas.toDataURL("image/png");
					_timeline.draw();
		},
		
		updateFrameSettings: function(event){
		
			_currentFrame.duration = parseInt($("#frame-duration").val());
			_currentFrame.clearOnEnter =  ( $("#frame-clearOnEnter:checked").val() !== undefined)  ? true : false;	
			_animation.setCurrentFrame(_currentFrame);

		},
		
		playCurrentAnimation: function(){
			
			if(!_isConnected){
				_webSocketService.connect(
					function(){_isConnected = true;}, 
					function(data){ console.log(data)}, 
					function(){alert("Can't connect to WebSocketServer.")}
				);
			}
				
			_webSocketService.send(AnimationConverter.convertAnimationToJSON(_animation));
		},
		
		playAnimationFromURL: function (url){
			_webSocketService.send("http://dl.dropbox.com/u/959008/animations/example3D.json");
		},
		
		saveCurrentAnimation: function(){
		
			var animationJSON = AnimationConverter.convertAnimationToJSON(_animation);
			var methodName = "add";
			
			if(_animation.getHasBeenSaved()){ methodName = "update"; };
				
			$.ajax({
				type : "PUT",
				url: "storage/" + methodName + "/" + _animation.author + "/" + _animation.title + "/",
				dataType : "json",
				data : {
					author : _animation.author,
					title :  _animation.title,
					type : _animation.type,
					json : animationJSON,
					thumbnail : _animation.getFrameById(0).thumbnailString
					},
				success: function(response){
					if(response.status == "false"){
						console.log("Failed");
					}
					else _animation.setHasBeenSaved(true);
				},
				error : function(xhr, status, error){
					console.log(status);
				}
			});
		},
		
		changeTool: function(event){
			$(event.delegateTarget).children().removeClass("active");
			$(event.target.parentElement).addClass("active");
			_currentTool = TOOL[event.target.parentElement.attributes.title.value.toUpperCase().replace("-","")];
		}

	}
	
});