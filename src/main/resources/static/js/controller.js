app.controller('controller', ['$scope', '$http', '$mdDialog', 
    function ($scope, $http, $mdDialog) {

    $scope.playlists = null;
    $scope.selectedPlaylists = [];

    $scope.playlistSort = "$index";
    $scope.playlistSortReverse = false;

    $scope.playlistFilter = "";

    $scope.playlistDetails = {};

    $scope.submissionForm = {
        playlistName: "Latest Additions",
        numTracks: 25,
        overwriteExisting: false
    }

    var _getPlaylists = function () {
        $http.get("/api/spotify/playlists")
        .then(function (response) {
            if (response !== "") {
                $scope.playlists = response.data;
            }
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
                if (response.data !== null || response.data !== "") {
                    var origPlaylists = $scope.playlists.items;
                    $scope.playlists = response.data;
                    origPlaylists.push.apply(origPlaylists, $scope.playlists.items);
                    $scope.playlists.items = origPlaylists;
                }
            });
        } else {
            return null;
        }
    };

    $scope.getPlaylistDetails = function (uri, event) {
        $mdDialog.show({
            locals: {
                uri: uri,
                playlistDetails: $scope.playlistDetails
            },
            controller: DialogController,
            templateUrl: '../templates/playlist-details.tpl.html',
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose:true
        });
    };

    function DialogController($scope, $mdDialog, playlistDetails, uri) {
        $scope.hide = function() {
            $mdDialog.hide();
        };

        if (uri in playlistDetails) {
            $scope.playlist = playlistDetails[uri];
        } else {
            $http.get("/api/spotify/playlistdetails", {
                "params": {
                    "uri": uri
                }
            })
            .then(function (response) {
                if (response.data !== null || response.data !== "") {
                    playlistDetails[uri] = response.data;
                    $scope.playlist = response.data;
                }
            });
        }
    }

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


    if ($scope.playlists === null || $scope.playlists === "") {
        _getPlaylists();
    }

}]);