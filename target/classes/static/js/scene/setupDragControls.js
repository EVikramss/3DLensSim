var dragControlRegistrationMap = new Map();

let setupDragControlForObj = function($scope, name, object, dragFunction, dragStartFunction, dragEndFunction) {

	// Create DragControls
	const dragControls = new THREE.DragControls([object], $scope.camera, $scope.renderer.domElement);
	dragControls.addEventListener('drag', dragFunction);
	dragControls.addEventListener('dragstart', dragStartFunction);
	dragControls.addEventListener('dragend', dragEndFunction);

	if (dragControlRegistrationMap.has(name)) {
		console.log(`${name} is already registered in dragControlRegistrationMap.`);
	} else {
		dragControlRegistrationMap.set(name, true);
		$scope[name] = dragControls;
	}
};