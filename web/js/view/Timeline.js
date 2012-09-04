/**
 * @author Matthieu Holzer
 */
var Timeline = (function (animation, container) {

    var _animation = animation;
    var _container = container;
    var _currentFrameId = 0;

    function createImage(id) {
        var img;
        img = document.createElement("img");
        img.src = _animation.getFrameById(id).thumbnailString;
        img.width = img.height = 100;
        return img;
    }

    return {

        update: function () {
            $(_container).children().eq(_currentFrameId).attr("src",_animation.getFrameById(_currentFrameId).thumbnailString);
        },

        draw: function () {

            $(_container).empty();

            for (var i = 0; i < _animation.getFrameCount(); i++) {
                $(_container).append(createImage(i));
            }

            $(_container).on("click", function (event) {

				event.stopImmediatePropagation();
				_currentFrameId = $(event.target).index();
				
				$(this).children().removeClass("active");
				$(this).children().eq(_currentFrameId).addClass("active");
					
                $(this).trigger({
                        type: "frameSelected",
                        selectedFrame: _animation.getFrameById(_currentFrameId),
                    });
					
					
				
				
                });
				
				


        }
    }

});