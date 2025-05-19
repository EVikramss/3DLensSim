let getRayData = function($scope) {
	let urlForPage = './data/getSampledRays?maxValues=100';
	$scope.invoke('GET', urlForPage).then(function success(data) {
		var lineDataArr = data.data;
		var material = new THREE.LineBasicMaterial({ color: 0x0000ff });

		for (let i = 0; i < lineDataArr.length; i++) {
			var lineData = lineDataArr[i];

			for (let j = 0; j < lineData.points.length; j = j + 2) {
				const points = [];
				var point1 = lineData.points[j];
				var point2 = lineData.points[j + 1];

				points.push(new THREE.Vector3(point1.x * $scope.scale, point1.y * $scope.scale, point1.z * $scope.scale));
				points.push(new THREE.Vector3(point2.x * $scope.scale, point2.y * $scope.scale, point2.z * $scope.scale));

				if (j == 0) {
					material = new THREE.LineBasicMaterial({ color: 0xffa500 });
				} else if (j == 2) {
					material = new THREE.LineBasicMaterial({ color: 0x00ff00 });
				}

				const geometry = new THREE.BufferGeometry().setFromPoints(points);
				const line = new THREE.Line(geometry, material);
				$scope.scene.add(line);
			}
		}

	}, function success(error) {

	});
};