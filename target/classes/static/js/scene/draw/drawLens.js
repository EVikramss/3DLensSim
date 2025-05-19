let drawObjectBoundary3dSurface = function($scope, position, uuid, attributes, object, objectColor) {

	const regionString = attributes.region;
	const regionArray = regionString.split(',').map(Number);

	/*var maxX = boundedRegion.maxX;
	var minX = boundedRegion.minX;
	var maxY = boundedRegion.maxY;
	var minY = boundedRegion.minY;
	var maxZ = boundedRegion.maxZ;
	var minZ = boundedRegion.minZ;*/
	var maxX = regionArray[0];
	var minX = regionArray[1];
	var maxY = regionArray[2];
	var minY = regionArray[3];
	var maxZ = regionArray[4];
	var minZ = regionArray[5];

	// assume that the position of the object is defined at the point where it crosses the origin or z=0
	// boundary is also assumed to pass through this point

	var width = Math.abs(maxX - minX);
	var height = Math.abs(maxY - minY);
	var depth = Math.abs(maxZ - minZ);

	var widthOffset = (maxX + minX) / 2;
	var heightOffset = (maxY + minY) / 2;
	var depthOffset = (maxZ + minZ) / 2;

	const geometry = new THREE.BoxGeometry(width, height, depth);
	const material = new THREE.MeshBasicMaterial({ color: objectColor, opacity: 0.4, transparent: true });
	const rectangle = new THREE.Mesh(geometry, material);
	rectangle.position.set(position.x + widthOffset, position.y + heightOffset, position.z + depthOffset);
	rectangle.name = '3dSurface (' + uuid + ')';
	$scope.scene.add(rectangle);
	
	drawSurfaceMesh($scope, object.mesh.xValList, object.mesh.yValList, object.mesh.zValList, object.mesh.indices, rectangle, objectColor);

	let moveObject = function($scope, event, object) {
		var json = {
			"uuid": uuid,
			"position": {
				"x": object.position.x,
				"y": object.position.y,
				"z": object.position.z
			}
		};

		$scope.invokePost('./updateComposite', json)
			.then(function(data) {
				reDrawOnCompositeUpdate($scope);
			}, function(error) {

			});
	};
	$scope.addToSelectableObject(rectangle, moveObject);
};

let drawSurfaceMesh = function($scope, x, y, z, indices, boundingBox, objectColor) {
	const vertices = [];
	for (let i = 0; i < x.length; i++) {
		vertices.push(x[i], y[i], z[i]);
	}
	
	const geometry = new THREE.BufferGeometry();
	geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
	geometry.setIndex(indices);
	
	// const material = new THREE.MeshBasicMaterial({ color: objectColor, wireframe: false, opacity: 0.5, transparent: true });
	const material = new THREE.MeshBasicMaterial({ color: objectColor, wireframe: true});
	const mesh = new THREE.Mesh(geometry, material);
	$scope.scene.add(mesh);
	boundingBox.add(mesh);
};