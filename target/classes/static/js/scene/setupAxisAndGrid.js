let setupAxis = function($scope) {

	// setup axes
	$scope.axesHelper = new THREE.AxesHelper(5);
	$scope.scene.add($scope.axesHelper);
};

let setupGrid = function($scope) {

	// Create the grid on the YZ plane
	$scope.gridHelper = new THREE.GridHelper(300, 300, 0x000000, 0x000000);
	$scope.gridHelper.material.opacity = 0.5;
	$scope.gridHelper.material.transparent = true;
	$scope.gridHelper.material.dashSize = 0.1;
	$scope.gridHelper.material.gapSize = 0.1;
	$scope.scene.add($scope.gridHelper);
};

let setupLabelsForAxis = function($scope) {
	// Add labels for axes
	$scope.loader = new THREE.FontLoader();
	$scope.loader.load('./fonts/helvetiker_regular.typeface.json', function(font) {
		const createLabel = (text, position) => {
			const textGeometry = new THREE.TextGeometry(text, {
				font: font,
				size: 0.2,
				height: 0.05,
			});
			const textMaterial = new THREE.MeshBasicMaterial({ color: 0x000000 });
			const mesh = new THREE.Mesh(textGeometry, textMaterial);
			mesh.position.copy(position);
			$scope.scene.add(mesh);
		};

		createLabel('X', new THREE.Vector3(5, 0, 0));
		createLabel('Y', new THREE.Vector3(0, 5, 0));
		createLabel('Z', new THREE.Vector3(0, 0, 5));
	});
};