let app = angular.module('Debug', []);

app.controller('debug', function($http, $scope) {

	let invoke = function(methodArg, urlArg) {
		return $http({
			method: methodArg,
			url: urlArg
		});
	};

	let initialize = function() {
		// setup scene, camera & renderer
		
	};


	initialize();
});