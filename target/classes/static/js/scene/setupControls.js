let setupControls = function($scope) {
	// setup input control
	$scope.controls = new THREE.OrbitControls($scope.camera, $scope.renderer.domElement);
	$scope.controls.enableDamping = true;
	$scope.controls.dampingFactor = 0.25;
	$scope.controls.screenSpacePanning = false;
	$scope.controls.minDistance = 1;
	$scope.controls.maxDistance = 500;

	$scope.controls.update();
};