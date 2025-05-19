let app = angular.module('LensSim', []);

app.controller('home', function($http, $scope) {

	// global settings and maps
	$scope.scale = 1.0;
	$scope.selectableObjectsMap = new Map();
	$scope.selectableObjects = [];
	$scope.selectableObjectMethods = new Map();
	$scope.compositeXDIV = 0.1;
	$scope.compositeYDIV = 0.1;

	$scope.invoke = function(methodArg, urlArg) {
		return $http({
			method: methodArg,
			url: urlArg,
			withCredentials: true
		});
	};

	$scope.addToSelectableObject = function(object, objectFunction) {
		$scope.selectableObjects.push(object);
		$scope.selectableObjectMethods.set(object, objectFunction);
	};

	$scope.invokePost = function(url, data) {
		return $http({
			method: 'POST',
			url: url,
			data: data,
			withCredentials: true
		});
	};

	$scope.solve = function() {
		invokeSolver($scope);
	};

	$scope.selectObject = function(object) {
		manualObjectSelect($scope, object);
	};

	let animateScene = function() {
		requestAnimationFrame(animateScene);
		$scope.controls.update();
		$scope.renderer.render($scope.scene, $scope.camera);
	};

	let initialize = function() {
		setupBasic($scope);
		animateScene($scope);
		generateImagePlane($scope);
		getAllSetup($scope);
	};

	initialize();
});