/**
 * @author Matthieu Holzer
 */

var Preview2D = function() {
	
	var _frame,
		_canvas,
		_previewContainer,
		_width,
		_height,
		_ctx,
		_radius;
	
	var PADDING = 8;
	var SPACING = 5;
	
	return {
	
		init : function(canvas, previewContainer){
			_canvas = canvas;
			_previewContainer = previewContainer;
			_width = $(canvas).width();
			_height = $(canvas).height();
			_ctx = _canvas.getContext("2d");
			
		
			canvas.addEventListener("click", this.clickHandler, false);
			
			return this;
		},
		
		setFrame : function(frame){
			_frame = frame;
			return this;
		},
		
		clickHandler: function(event){
		
			var mouseX = event.pageX - this.offsetLeft;
            var mouseY = event.pageY - this.offsetTop;
			
			//find out if click was in a circle and in which
			if(mouseX > PADDING && mouseY > PADDING && mouseX < _width-PADDING && 
				mouseY < _height-PADDING && mouseX){
			
				//calculate the row and column, e.g.: 1,3
				var row = Math.floor((mouseX - PADDING) / (1+_radius*2+SPACING));
				var column = Math.floor((mouseY - PADDING) / (1+_radius*2+SPACING));
				
				//where is the center of that circle
				var xOrigin = PADDING + _radius + row*(1+_radius*2+SPACING);
				var yOrigin = PADDING + _radius + column*(1+_radius*2+SPACING);
				
				//was the interaction inside the circle?
				var distance = Math.sqrt((xOrigin - mouseX) * (xOrigin - mouseX) + (yOrigin - mouseY) * (yOrigin - mouseY));
				
				if(distance<=_radius) $(_previewContainer).trigger({type:"pointSelected", x : row, y: column});
			}
			
			},
		
		draw : function(){
			
			if(_ctx == null) return;
			
			var ledCount = _frame.ledsPerAxis;
			
			this.clear();
			
			_radius = (_width-( PADDING*2 + SPACING*(ledCount-1)))/ledCount/2;
					
			var color;
			for(var x=0;x<ledCount;x++){
				for(var y=0;y<ledCount;y++){
					
					color = _frame.get2DPoint(x,y);
					
					if(color == "random"){

					var img = new Image();
					img.src = "img/random.png";
					color = _ctx.createPattern(img, "repeat");

					}
					
					_ctx.fillStyle = color;
					_ctx.strokeStyle = "#000000";
					_ctx.beginPath();
					_ctx.arc( PADDING + _radius + x*(1+_radius*2+SPACING), PADDING + _radius+ y*(1+_radius*2+SPACING), _radius, 2*Math.PI, false);
					_ctx.fill();
					_ctx.stroke();
					_ctx.closePath();
				}
			}

		},
		
		clear : function(){
			if(_ctx == null) return;
			_ctx.fillStyle = "#f0f0f0";
			_ctx.fillRect(5,5,_width-5,_height-5);
		}
		
	}
	
}();