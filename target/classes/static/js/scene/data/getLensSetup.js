let getLensSetup = function($scope) {
	let urlForPage = './data/getParentComposite?xdiv=' + $scope.compositeXDIV + 'f&ydiv=' + $scope.compositeYDIV + 'f';
	$scope.invoke('GET', urlForPage).then(function success(data) {
		var setupData = data.data;
		var setupPosition = setupData.position;
		var childObjects = setupData.childObjects;
		drawChildObjects($scope, childObjects, setupPosition);
	}, function success(error) {

	});
};

let drawChildObjects = function($scope, objects, setupPosition) {
	var color = [0x01a0f0, 0xf0a0a0, 0xa0f110];
	for (var counter = 0; counter < objects.length; counter++) {
		var object = objects[counter];
		var childPosition = object.position;
		var childObjects = object.childObjects;

		// childPosition.x += setupPosition.x;
		// childPosition.y += setupPosition.y;
		// childPosition.z += setupPosition.z;

		drawChildObjects($scope, childObjects, childPosition);
		
		drawObject($scope, object, childPosition, color[counter % 3]);
	}
};

let drawObject = function($scope, object, position, color) {
	var objectType = object.typeEnum;
	var uuid = object.uuid;
	var objectAttributes = object.attributes;

	switch (objectType) {
		case 'CIRCULAR_APERTURE':
			drawCircularAperture($scope, position, uuid, objectAttributes);
			break;
		case 'OBJECT_BOUNDARY_3DSURFACE':
			drawObjectBoundary3dSurface($scope, position, uuid, objectAttributes, object, color);
			break;
	}
};