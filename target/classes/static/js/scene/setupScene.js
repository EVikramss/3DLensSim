let setupThreeJSScene = function($scope) {
	$scope.scene = new THREE.Scene();
	$scope.scene.background = new THREE.Color(0xffffff);
	$scope.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
	$scope.renderer = new THREE.WebGLRenderer();
	$scope.renderer.setSize(0.9 * window.innerWidth, 0.9 * window.innerHeight);
	document.body.appendChild($scope.renderer.domElement);

	// Position the camera
	$scope.camera.position.set(-5, 5, -5);

	// Create a raycaster and mouse vector
	$scope.raycaster = new THREE.Raycaster();
	$scope.mouse = new THREE.Vector2();
};