/**
 * @author Matthieu Holzer
 */

var Frame = (function(type,ledsPerAxis) {
	
	var MAX_LEDS_PER_AXIS = 100;
	
	var _type = type,
		_ledsPerAxis = ledsPerAxis,
		_thumbnailString,
		_ledMatrix;

	if(_type === 2 || _type === 3) _type = type;
	else throw new Error("Dimension must be 2 or 3");
			
	if(ledsPerAxis <= MAX_LEDS_PER_AXIS) _ledsPerAxis = ledsPerAxis;
	else throw new Error("Too many LEDs per Axis");
		
	//1. Dimension
	_ledMatrix = [_ledsPerAxis];
	
	//2. Dimension	
	for(var i = 0;i<_ledsPerAxis;i++){
		_ledMatrix[i] = [_ledsPerAxis];
	}

	//3. Dimension	
	if(_type === 3){
		for(var j=0;j<_ledsPerAxis;j++){
			for(var k=0;k<_ledsPerAxis;k++){
				_ledMatrix[j][k] = [_ledsPerAxis];
			}
		}
	}	

	if(_type == 2) fill2D(null);
	else fill3D(null);
	
	function fill2D(value){
			for(var x=0;x<_ledsPerAxis;x++){
				for(var y=0;y<_ledsPerAxis;y++){
					_ledMatrix[x][y] = value;
				}
			}
	}
		
	function fill3D(value){
			for(var j=0;j<_ledsPerAxis;j++){
				for(var k=0;k<_ledsPerAxis;k++){
					_ledMatrix[j][k][0] = value;
				}
			}
		}
		
		
	
	return {
	
		type: _type,
		duration: 1000,
		ledsPerAxis : _ledsPerAxis,
		clearOnEnter:  true,
		thumbnailString : _thumbnailString,
			
			
		get2DPoint: function(x,y){
			if(x < _ledsPerAxis && y < _ledsPerAxis){
				if(_ledMatrix[x][y] != null) return _ledMatrix[x][y];
				else return "#000000";
			}
			else throw new RangeError("x || y too big");
		},
		
		get3DPoint: function(x,y,z){
			if(x < _ledsPerAxis && y < _ledsPerAxis && z < _ledsPerAxis){
				if(_ledMatrix[x][y][z] !=null) return _ledMatrix[x][y][z];
				else return "#000000";
				}
			else throw new RangeError("x || y || z too big");
		},
		
		set2DPoint : function(x,y,color){
			if(x < _ledsPerAxis && y < _ledsPerAxis)
			{
				_ledMatrix[x][y] = color;
			}
			else throw new RangeError("x || y too big");
		},
		
		set3DPoint : function(x,y,z,color){
			_ledMatrix[x][y][z] = color;
		}

	}
	
});