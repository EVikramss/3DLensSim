let drawCircularAperture = function($scope, position, uuid, attributes) {
	const radius = attributes.radius;
	const segments = 32;

	var mainRadius = 2.0;
	var innerRadius = 1.6;

	const circleGeometry = new THREE.RingGeometry(mainRadius, innerRadius, segments);
	const material = new THREE.MeshBasicMaterial({ color: 0x0077ff });
	material.side = THREE.DoubleSide;
	const aperture = new THREE.Mesh(circleGeometry, material);
	aperture.position.set(position.x, position.y, position.z);
	$scope.scene.add(aperture);
	aperture.name = 'Aperture';

	let moveAperture = function($scope, event, object) {
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
	$scope.addToSelectableObject(aperture, moveAperture);

	drawApertureControl($scope, aperture, mainRadius, innerRadius, uuid, radius, segments);
};

let drawApertureControl = function($scope, aperture, mainRadius, innerRadius, uuid, fillRadius, segments) {

	// add aperture control as a cylinder & attach to aperture object
	const cylinderGeometry = new THREE.CylinderGeometry(0.1, 0.1, 0.1, 32);
	const cylinderMaterial = new THREE.MeshBasicMaterial({ color: 0xff0000 });
	const apertureControl = new THREE.Mesh(cylinderGeometry, cylinderMaterial);
	aperture.add(apertureControl);

	// calculate limits of aperture control positions
	var controlRadius = mainRadius + 0.01;
	var apertureControlInitAngle = 45;
	var cosVal = Math.cos(apertureControlInitAngle * (Math.PI / 180));
	var sinVal = Math.sin(apertureControlInitAngle * (Math.PI / 180));
	var ymaxVal = aperture.position.y + (controlRadius * sinVal);
	var yminVal = -ymaxVal;
	var currentApertureRadius = fillRadius;

	// function to translate to y position of aperture control given opening radius
	let translateFillRadiusToY = function(fillRadius) {
		return ymaxVal - (((ymaxVal - yminVal) / innerRadius) * (innerRadius - fillRadius));
	};

	// function to translate to opening radius given y position of aperture control
	let translateYToFillRadius = function(yval) {
		return innerRadius - (((ymaxVal - yval) * innerRadius) / (ymaxVal - yminVal));
	};

	let getXPosGivenYPos = function(yval) {
		var ratio = yval / controlRadius;
		ratio = Math.sqrt(1 - (ratio * ratio));
		return aperture.position.x - (controlRadius * ratio);
	};

	// set position of aperture control as per given initial opening radius
	var currentYPos = translateFillRadiusToY(fillRadius);
	apertureControl.position.set(getXPosGivenYPos(currentYPos), currentYPos, 0);

	// block in aperture fill as per given opening radius
	var apertureFillGeom = new THREE.RingGeometry(innerRadius, fillRadius, segments);
	var apertureFillMaterial = new THREE.MeshBasicMaterial({ color: 0x0033ff });
	apertureFillMaterial.side = THREE.DoubleSide;
	var apertureFill = new THREE.Mesh(apertureFillGeom, apertureFillMaterial);
	apertureFill.position.set(0, 0, 0);
	$scope.scene.add(apertureFill);
	aperture.add(apertureFill);

	// on drag constrain the position of aperture control
	apertureControlDragFunction = function(event) {
		event.object.position.z = 0; // Constrain Z position
		$scope.controls.enabled = false;

		var currPos = event.object.position.y;
		if (currPos > ymaxVal) {
			event.object.position.y = ymaxVal;
		} else if (currPos < yminVal) {
			event.object.position.y = yminVal;
		}

		currentYPos = event.object.position.y;
		event.object.position.x = getXPosGivenYPos(event.object.position.y);

		/* Remove and draw aperture fill - start*/
		$scope.scene.remove(apertureFill);
		aperture.remove(apertureFill);
		apertureFill.geometry.dispose();
		apertureFill.material.dispose();
		apertureFill = null;

		currentApertureRadius = translateYToFillRadius(event.object.position.y);
		apertureFillGeom = new THREE.RingGeometry(innerRadius, currentApertureRadius, segments);
		apertureFillMaterial = new THREE.MeshBasicMaterial({ color: 0x0033ff });
		apertureFillMaterial.side = THREE.DoubleSide;
		apertureFill = new THREE.Mesh(apertureFillGeom, apertureFillMaterial);
		apertureFill.position.set(0, 0, 0);
		$scope.scene.add(apertureFill);
		aperture.add(apertureFill);
		/* Remove and draw aperture fill - end*/
	};

	apertureControlDragStartFunction = function(event) {
		event.object.material.opacity = 0.5;
	};

	// on drag end send aperture opening to server
	apertureControlDragEndFunction = function(event) {
		event.object.material.opacity = 1.0;
		$scope.controls.enabled = true;

		/* Remove and draw aperture fill - end*/
		var json = {
			"uuid": uuid,
			"attributes": {
				"radius": currentApertureRadius
			}
		};

		$scope.invokePost('./updateComposite', json)
			.then(function(data) {
				$scope[uuid].enabled = true;
				reDrawOnCompositeUpdate($scope);
			}, function(error) {
				$scope[uuid].enabled = true;
			});
	};

	setupDragControlForObj($scope, uuid, apertureControl, apertureControlDragFunction, apertureControlDragStartFunction, apertureControlDragEndFunction);
};