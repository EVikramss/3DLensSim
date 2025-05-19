let invokeSolver = function($scope) {
	let urlForPage = './generateSolution';
	$scope.invoke('GET', urlForPage).then(function success(data) {
		reDrawOnCompositeUpdate($scope);
	}, function success(error) {

	});
};