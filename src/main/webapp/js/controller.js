app.controller('controller', ['$scope', '$http', function ($scope, $http) {

    $scope.playlists = null;
    $scope.selectedPlaylists = [];

    $scope.playlistSort = "$index";
    $scope.playlistSortReverse = false;

    $scope.playlistFilter = "";

    var _getPlaylists = function () {
        $http.get("/api/spotify/playlists")
        .then(function (response) {
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
           })
           .then(function (response) {
               var origPlaylists = $scope.playlists.items;
               $scope.playlists = response.data;
               origPlaylists.push.apply(origPlaylists, $scope.playlists.items);
               $scope.playlists.items = origPlaylists;
           });
        } else {
            return null;
        }
    };

    $scope.toggle = function (item, list) {
        var idx = list.indexOf(item);
        if (idx > -1) {
            list.splice(idx, 1);
        }
        else {
            list.push(item);
        }
    };

    $scope.exists = function (item, list) {
        return list.indexOf(item) > -1;
    };

    _getPlaylists();

}]);