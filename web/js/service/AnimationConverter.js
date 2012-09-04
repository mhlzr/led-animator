/**
 * @author Matthieu Holzer
 */

var AnimationConverter = function() {
		
	function getPointsArray(frame){
		var data = null;
		var color, index;
		
		if(frame.type===2){
		
			for(var x=0;x<frame.ledsPerAxis;x++){
				for(var y=0;y<frame.ledsPerAxis;y++){
				
					color = frame.get2DPoint(x,y);
					if(color !== "#000000"){
						
						if(data === null) data =[];
						
						index = getColorIndexInArray(data, color);
						
						if(index >= 0) data[index].coordinates.push(x+","+y) ;
						else data.push( { colors: [color], coordinates : [x+","+y] } );
					}
				}
			}
		}
		//.replace("#","0x")
		//3D
		else{
				for(var x=0;x<frame.ledsPerAxis;x++){
					for(var y=0;y<frame.ledsPerAxis;y++){
						for(var z=0;z<frame.ledsPerAxis;z++){
							color = frame.get3DPoint(x,y,z);
							if(color !== "#000000"){
						
								if(data === null) data =[];
						
								index = getColorIndexInArray(data, color);
						
								if(index >= 0) data[index].coordinates.push(x+","+y+","+z) ;
								else data.push( { colors: [color], coordinates : [x+","+y+","+z] } );
							}
						}
					}
				}
			}
		
		
		//console.log(data);
		
		return data;
		
		
				

	}
	
	
	function getColorIndexInArray(array, color){
	
		for(var i=0;i<array.length;i++){
			if(array[i].colors[0] == color) return i;
		}
		return -1;
	
	}
	
	return {
	
		convertAnimationToJSON : function(animation){
		
			var jsonObject = {};
			
			jsonObject.author = animation.author;
			jsonObject.title = animation.title;
			jsonObject.type = (animation.type === 2) ? "2d" : "3d";
			jsonObject.ledsPerAxis = animation.ledsPerAxis;
			jsonObject.repeat = true;
			
			var frameArray = [];
			var frame;
			
			for(var i=0;i<animation.getFrameCount();i++){
			
				frame = animation.getFrameById(i);

				frameArray[i] = {};
				frameArray[i].duration = frame.duration;
				frameArray[i].clearOnEnter = frame.clearOnEnter;			
				
				var pointData = getPointsArray(frame);
				if(pointData != null) frameArray[i].points = pointData;
				
				
			}
			
			jsonObject.frames = frameArray;
				
		
			return JSON.stringify(jsonObject);
			
		},
		
		
		convertJSONToAnimation: function(json){
			//JSON.parse(string);
		}
	}
	
}();