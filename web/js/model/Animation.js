/**
 * @author Matthieu Holzer
 */

var Animation = (function(author, title, type, ledsPerAxis) {
	
	var _author = author;
	var _title = title;
	var _type = type;
	var _ledsPerAxis = ledsPerAxis;
	
	var _frames = [];
	var _currentFrameId = 0;
	var _hasBeenSaved = false;
	
	function getFrameIndex(frame){
		return $.inArray(frame, _frames);
	}
	
	return {
	
		author : _author,
		title: _title,
		type: _type,
		ledsPerAxis : _ledsPerAxis,
	
		getFrameCount : function(){
			return _frames.length
		},
	
		addFrame : function(frame){
			_frames.push(frame);
		},
		
		removeFrame : function(frame){
			if(_frames.length<=1) return;
			
			var index = getFrameIndex(frame);
			if(index>=0 && index <_frames.length)
				_frames.splice( index,1);
		},
		
		updateFrame : function(frame){
			_frames[_currentFrameId] = frame;
		},
		
		getFrameById : function(id){
			if(id >= _frames.length || id < 0) return 0;
			else return _frames[id];
		},
		
		setCurrentFrame : function(frame){
			_currentFrameId  = getFrameIndex(frame);
		},
		
		getHasBeenSaved : function(){
			return _hasBeenSaved;
		},
				
		setHasBeenSaved : function(val){
			_hasBeenSaved = val;
		},
		
		dump : function(){
			console.log(_frames);
		}
	}
	
});