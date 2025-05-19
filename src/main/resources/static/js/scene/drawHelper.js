let setupBasic = function($scope) {
	setupThreeJSScene($scope);
	reDrawSetup($scope);
	setupControls($scope);
	setupObjectSelection($scope);
};

let reDrawSetup = function($scope) {
	setupAxis($scope);
	setupGrid($scope);
	setupLabelsForAxis($scope);
};

let reDrawOnLineDeletion = function($scope) {
	setupAxis($scope);
	setupGrid($scope);
};

let deleteLines = function($scope) {
	for (let i = $scope.scene.children.length - 1; i >= 0; i--) {
		const child = $scope.scene.children[i];
		if (child instanceof THREE.Line) {
			$scope.scene.remove(child);
		}
	}
	reDrawOnLineDeletion($scope);
};

let getAllSetup = function($scope) {
	getLensSetup($scope);
	getRayData($scope);
	getGeneratedImage($scope);
};

function sleep(ms) {
	return new Promise(resolve => setTimeout(resolve, ms));
}

async function sleepFunction(duration) {
	await sleep(duration);
}

let reDrawOnCompositeUpdate = function($scope) {
	// wait for 2 second and refetch rays and image data
	// sleepFunction(2000);
	deleteLines($scope);
	getRayData($scope);
	getGeneratedImage($scope);
}