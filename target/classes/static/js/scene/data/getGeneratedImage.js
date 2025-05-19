var dragControlName = 'imaginPlaneDragControl';

let generateImagePlane = function($scope) {

	// Create a plane geometry for now
	const geometry = new THREE.PlaneGeometry(3.0, 3.0);
	const material = new THREE.MeshBasicMaterial({ color: 0x000000 });
	const plane = new THREE.Mesh(geometry, material);
	plane.position.set(0.0, 0.0, 10.0);
	plane.rotation.y = Math.PI;
	$scope.scene.add(plane);
	$scope.imagingPlane = plane;
	$scope.imagingMaterial = 'BASIC';
	$scope.addToSelectableObject(plane, moveImagePlane);
	plane.name = 'ImagePlane';

	dragFunction = function(event) {
		event.object.position.x = 0; // Constrain X position
		event.object.position.y = 0; // Constrain Y position
		$scope.controls.enabled = false;
	};

	dragStartFunction = function(event) {
		event.object.material.opacity = 0.5;
	};

	dragEndFunction = function(event) {
		event.object.material.opacity = 1.0;
		$scope.controls.enabled = true;
		$scope[dragControlName].enabled = false;

		handleImagePlaneDrag($scope, event);
	};

	// setupDragControlForObj($scope, dragControlName, plane, dragFunction, dragStartFunction, dragEndFunction);
};

let moveImagePlane = function($scope, event, object) {
	var json = { x: object.position.x, y: object.position.y, z: object.position.z };
	$scope.invokePost('./updateSensorPosition', json)
		.then(function(data) {
			var byteArr = data.data[1];
			drawImageTextureOnImgPlane($scope, byteArr);
		}, function(error) {
			//$scope[dragControlName].enabled = true;
		});
};

let handleImagePlaneDrag = function($scope, event) {
	var json = { x: 0.0, y: 0.0, z: event.object.position.z };
	$scope.invokePost('./updateSensorPosition', json)
		.then(function(data) {
			var byteArr = data.data[1];
			drawImageTextureOnImgPlane($scope, byteArr);
		}, function(error) {
			$scope[dragControlName].enabled = true;
		});
};

let getGeneratedImage = function($scope) {
	urlForPage = './data/getGeneratedImage';
	$scope.invoke('GET', urlForPage).then(function success(data) {
		var position = data.data[0];
		var byteArr = data.data[1];
		var xsize = data.data[2][0];
		var ysize = data.data[2][1];

		setImagePosition($scope, position);
		setImageSize($scope, xsize, ysize);
		drawImageTextureOnImgPlane($scope, byteArr);
	}, function success(error) {

	});
};

let drawImageTextureOnImgPlane = function($scope, byteArr) {
	var binaryString = atob(byteArr);
	var bytes = new Uint8Array(binaryString.length);
	for (var i = 0; i < binaryString.length; i++) {
		bytes[i] = binaryString.charCodeAt(i);
	}

	const blob = new Blob([bytes], { type: 'image/jpeg' });
	const imageUrl = URL.createObjectURL(blob);

	const textureLoader = new THREE.TextureLoader();
	textureLoader.load(imageUrl, function(texture) {
		if ($scope.imagingMaterial == 'BASIC') {
			$scope.imagingMaterial = 'TEXTURE';
			const newMaterial = new THREE.MeshBasicMaterial({ map: texture });
			newMaterial.side = THREE.DoubleSide;
			$scope.imagingPlane.material.dispose();
			$scope.imagingPlane.material = newMaterial;
		}

		$scope.imagingPlane.material.map = texture;
		$scope.imagingPlane.material.needsUpdate = true;
		//$scope[dragControlName].enabled = true;
	});
};

let setImagePosition = function($scope, position) {
	$scope.imagingPlane.position.set(position.x, position.y, position.z);
};

let setImageSize = function($scope, xsize, ysize) {
	const newGeometry = new THREE.PlaneGeometry(xsize, ysize);
	$scope.imagingPlane.geometry.dispose();
	$scope.imagingPlane.geometry = newGeometry;
};

