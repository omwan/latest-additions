app.controller('controller', ['$scope', '$http', function ($scope, $http) {

    $scope.playlists = null;

    var _getPlaylists = function () {
        $http.get("/api/spotify/playlists").then(function (response) {
            $scope.playlists = response.data;
        });
    };

    $scope.loadMorePlaylists = function () {
        if ($scope.playlists) {
            $http.get("/api/spotify/playlists", {
                "params": {
                    "offset": $scope.playlists.offset + $scope.playlists.limit,
                    "limit": $scope.playlists.limit
                }
            }).then(function (response) {
                var origPlaylists = $scope.playlists.items;
                $scope.playlists = response.data;
                origPlaylists.push.apply(origPlaylists, $scope.playlists.items);
                $scope.playlists.items = origPlaylists;
            })
        } else {
            return null
        }
    };

    _getPlaylists();

}]);