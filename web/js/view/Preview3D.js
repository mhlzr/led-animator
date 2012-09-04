/**
 * @author Matthieu Holzer
 */

var Preview3D = function() {
	
	var _frame,_ledsPerAxis, _container,_canvas,_width,_height,_ctx;
	var _camera, _scene, _projector, _renderer;
	
	var _mouseSensitive = false;
	
	var SPACING = 5;
	
	var _radius = 70;
	var _theta = 0;
	var _beta = 0;

	
	var _objects = [];
	
	return {
	
		init : function( canvas, container, ledsPerAxis){
		
			_ledsPerAxis = ledsPerAxis;
			_canvas = canvas;
			_container = container;
			_width = $(canvas).width();
			_height = $(canvas).height();
			
			//Three.js generates the canvas-Element itself
			$(_canvas).remove();
						
			_camera = new THREE.PerspectiveCamera(60, _width/_height, 1, 10000 );
			_camera.position.set( 0, 0, 400 );

			_scene = new THREE.Scene();

			_scene.add( _camera );

			var radius = 20;
			var segments, rings = 20;
				
			if(!Modernizr.webgl){
				segments = rings = 6;
			}
				
			var geometry = new THREE.SphereGeometry(radius, segments, rings );

			
			for ( var x = 0; x < _ledsPerAxis; x ++ ) {
				for(var y=0; y<_ledsPerAxis; y++){
					for(var z=0;z<_ledsPerAxis; z++){	
					
						var object = new THREE.Mesh( geometry, new THREE.MeshLambertMaterial( {color: 0x000000 }) );
					
						object.position.x = (-80*_ledsPerAxis)/2+ x * 80 + 40;
						object.position.y = (-80*_ledsPerAxis)/2+ y * 80 + 40;
						object.position.z = (-80*_ledsPerAxis)/2+ z * 80 + 40;
						
						//custom
						object.x = x;
						object.y = y;
						object.z = z;

						object.random = false;
						
						_scene.add( object );

						_objects.push( object );
					}
					}
				}

				var light = new THREE.SpotLight();
					light.position.set( _width/2, _height/2, 500 );
					_scene.add(light);
			
				_projector = new THREE.Projector();

				if(Modernizr.webgl){
					_renderer = new THREE.WebGLRenderer({ preserveDrawingBuffer: true });
				}
				else{
					_renderer = new THREE.CanvasRenderer();
				}
				
				_canvas = $(container+ " canvas");
				
				_renderer.setSize( _width, _height);

				_container.appendChild( _renderer.domElement );
				
				_container.addEventListener( 'mousedown', this.onContainerMouseDown, false );
				_container.addEventListener( 'mousemove', this.onContainerMouseMove, false );
				_container.addEventListener( 'dblclick', this.onContainerDblClick, false ); 
				
			
			_renderer.render(_scene, _camera);
			
			return this;
		},
		
		setFrame : function(frame){
			_frame = frame;
			return this;
		},

		
		onContainerDblClick: function(event){
				event.preventDefault();
				_mouseSensitive = !_mouseSensitive;
		},
			

			
		onContainerMouseMove: function(event){
				if(_mouseSensitive){
					_theta = -((_width/2) - event.clientX);
					_beta = (_height/2) - event.clientY;
				}
		},
			

		onContainerMouseDown: function( event ) {

				event.preventDefault();

				var vector = new THREE.Vector3( ( (event.pageX - this.offsetLeft) / _width) * 2 - 1, - ( (event.pageY - this.offsetTop) / _height ) * 2 + 1, 0.5 );
				_projector.unprojectVector( vector, _camera );

				var ray = new THREE.Ray( _camera.position, vector.subSelf( _camera.position ).normalize() );

				var intersects = ray.intersectObjects( _objects );

				if ( intersects.length > 0 ) {

					var xCoord = intersects[ 0 ].object.x;
					var yCoord = intersects[ 0 ].object.y;
					var zCoord = intersects[ 0 ].object.z;
					
					$(_container).trigger({type:"pointSelected", x : xCoord, y: yCoord, z: zCoord});

				}

		},
		
		draw : function(){

			
			var ledCount = _frame.ledsPerAxis;
			var x,y,z;
			var color;
			
			for(var i=0;i<_objects.length;i++){
			
				
			
				x = _objects[i].x;
				y = _objects[i].y;
				z = _objects[i].z;
				
				color = _frame.get3DPoint(x,y,z);
				
				if(color == "random"){
					
				
				_objects[i].material = new THREE.MeshLambertMaterial( {color : 0xFFFFFF*Math.random() });


					//doesn't work, screen gets redrawn to fast
					//_objects[i].material =  new THREE.MeshLambertMaterial( {map: THREE.ImageUtils.loadTexture("img/random.png", {}, function(){_renderer.render(_scene,_camera)})} );
					//_objects[i].material.needsUpdate = true;
					
					//_renderer.render(_scene, _camera);
					//return;
					//}


				}
				else{
					if(_objects[i].material.color != parseInt(color.substr(1,6), 16)){
						_objects[i].material = new THREE.MeshLambertMaterial( {color : parseInt(color.substr(1,6), 16) });
					}
				}

			}

			_camera.position.x = _radius * Math.sin( _theta * Math.PI / 360 );
			_camera.position.y = _radius * Math.sin( _beta * Math.PI / 360 );
			_camera.lookAt( _scene.position );

			_renderer.render(_scene, _camera);

		}
		
	}
	
}();