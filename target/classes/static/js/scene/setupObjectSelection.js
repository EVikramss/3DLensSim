let setupObjectSelection = function($scope) {

	let boxHelper = new THREE.BoxHelper();
	$scope.scene.add(boxHelper);
	$scope.boxHelper = boxHelper;

	const controls = new THREE.TransformControls($scope.camera, $scope.renderer.domElement);
	$scope.scene.add(controls);
	controls.addEventListener('mouseUp', function(event) {
		$scope.controls.enabled = true;
		$scope.selectableObjectMethods.get($scope.selectedObject)($scope, event, $scope.selectedObject);
	});
	controls.addEventListener('mouseDown', function(event) {
		$scope.controls.enabled = false;
	});
	$scope.transformControls = controls;
	
	// Add event listener for mouse click
	window.addEventListener('click', onMouseClick, false);
	function onMouseClick(event) {
		// Calculate mouse position in normalized device coordinates (-1 to +1) for both components
		$scope.mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
		$scope.mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;

		// Update the raycaster with the camera and mouse position
		$scope.raycaster.setFromCamera($scope.mouse, $scope.camera);

		// Calculate objects intersecting the raycaster
		const intersects = $scope.raycaster.intersectObjects($scope.selectableObjects);

		if (intersects.length > 0) {
			// assuming intersection sorted by distance, select the 1st one
			$scope.selectedObject = intersects[0].object;
			boxHelper.setFromObject($scope.selectedObject);
			boxHelper.visible = true;
			controls.enabled = true;
			controls.attach($scope.selectedObject);
			// console.log('Selected object:', $scope.selectedObject);
		} else {
			boxHelper.visible = false;
			controls.detach();
			controls.enabled = false;
		}
	}
};

let manualObjectSelect = function($scope, selectedObject) {
	$scope.selectedObject = selectedObject;
	$scope.boxHelper.setFromObject($scope.selectedObject);
	$scope.boxHelper.visible = true;
	$scope.transformControls.enabled = true;
	$scope.transformControls.attach($scope.selectedObject);
};
