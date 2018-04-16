/**
 * Main controller for application.
 */
app.controller('controller', ['$scope', '$http', '$mdDialog', 'rest', '$mdToast',
    function ($scope, $http, $mdDialog, rest, $mdToast) {

    $scope.playlists = null;
    $scope.selectedPlaylists = [];
    $scope.playlistSort = "$index";
    $scope.playlistSortReverse = false;
    $scope.playlistFilter = "";
    $scope.playlistDetails = {};
    $scope.existingPlaylists = [];

    $scope.submissionForm = {
        playlistName: "Latest Additions",
        numTracks: 25,
        overwriteExisting: false,
        description: null,
        isPublic: false,
        isCollaborative: false,
        playlistUris: {},
        playlistToOverwrite: null
    };

    /**
     * Get first page of playlists for the current user.
     */
    var _getPlaylists = function () {
        var successHandler = function (response) {
            if (response !== "") {
                $scope.playlists = response.data;
            }
        };
        
        // var url = "../mock_responses/playlists.json"
        var url = "/api/playlist/userplaylists";
        rest.getData(url, null, successHandler,
            "Unable to retrieve user playlists");
    };

    /**
     * Get next page of playlists for the current user.
     */
    $scope.loadMorePlaylists = function () {
        if ($scope.playlists) {
            var successHandler = function (response) {
                if (response.data !== null || response.data !== "") {
                    var origPlaylists = $scope.playlists.items;
                    $scope.playlists = response.data;
                    origPlaylists.push.apply(origPlaylists, $scope.playlists.items);
                    $scope.playlists.items = origPlaylists;
                }
            };

            var params = {
                "offset": $scope.playlists.offset + $scope.playlists.limit,
                "limit": $scope.playlists.limit
            };

            rest.getData("/api/playlist/userplaylists", params, successHandler,
                "Unable to retrieve next page of user playlists");
        } else {
            return null;
        }
    };

    /**
     * Open the playlist details dialog for the given playlist URI.
     * @param uri   playlist uri
     * @param event click event for modal
     */
    $scope.getPlaylistDetails = function (uri, event) {
        $mdDialog.show({
            locals: {
                uri: uri,
                playlistDetails: $scope.playlistDetails
            },
            controller: PlaylistDetailsDialogController,
            templateUrl: '../templates/playlist-details.tpl.html',
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose: true
        });
    };

    /**
     * Controller for playlist details dialog.
     * @param $scope            parent scope
     * @param $mdDialog         dialog module
     * @param playlistDetails   playlist details mapping for caching
     * @param uri               playlist URI to display details for
     */
    function PlaylistDetailsDialogController($scope, $mdDialog, playlistDetails, uri) {
        $scope.hide = function () {
            $mdDialog.hide();
        };

        if (uri in playlistDetails) {
            $scope.playlist = playlistDetails[uri];
        } else {
            var successHandler = function (response) {
                if (response.data !== null || response.data !== "") {
                    playlistDetails[uri] = response.data;
                    $scope.playlist = response.data;
                }
            };

            rest.getData("/api/playlist/details", {"uri": uri}, successHandler,
                "Unable to retrieve details for playlist");
        }
    }

    /**
     * Toggle selection of checkbox list.
     * @param item currently selected item
     * @param list list of previously selected items
     */
    $scope.toggle = function (item, list) {
        var idx = list.indexOf(item);
        if (idx > -1) {
            list.splice(idx, 1);
        } else {
            list.push(item);
        }
    };

    /**
     * Check if the given item exists in the list.
     * @param item          item to check
     * @param list          containing list
     * @returns {boolean}   whether or not the item exists
     */
    $scope.exists = function (item, list) {
        return list.indexOf(item) > -1;
    };

    /**
     * Submit the form in its current state to the build endpoint.
     */
    $scope.submitForm = function (event) {
        $scope.selectedPlaylists.forEach(function (playlist) {
            $scope.submissionForm.playlistUris[playlist.uri] = playlist.tracks.total;
        });

        var description = $scope.submissionForm;
        if (description === "" || description === null) {
            var playlistNames = $scope.selectedPlaylists
                .map(function (playlist) { return playlist.name; })
                .join(", ");
            $scope.submissionForm.description = _formatString("Autogenerated playlist containing "
            + "last added tracks from the following playlists: {0}. See source code at: " +
                "https://github.com/omwan/latest-addition)", [playlistNames]);
        }

        var successHandler = function (response) {
            $mdDialog.show({
                locals: {
                    url: response.data.playlistUrl,
                    tracks: response.data.tracklistPreview
                },
                controller: CreationSuccessDialogController,
                templateUrl: '../templates/creation-success.tpl.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true
            });
        };

        rest.postData("/api/playlist/build", null, $scope.submissionForm, successHandler,
            "Unable to create playlist with given parameters");

        $scope.submissionForm.description = null;
    };

    /**
     * Controller for playlist creation success dialog.
     * @param $scope            parent scope
     * @param $mdDialog         dialog module
     * @param url               link to playlist
     */
    function CreationSuccessDialogController($scope, $mdDialog, url, tracks) {
        $scope.url = url;
        $scope.tracks = tracks;

        $scope.hide = function () {
            $mdDialog.hide();
        };
    }

    /**
     * Get the existing saved playlists for the current user.
     */
    var _getExistingPlaylists = function () {
        var successHandler = function (response) {
            $scope.existingPlaylists = response.data;
            if ($scope.existingPlaylists.length > 0) {
                $scope.submissionForm.playlistToOverwrite = $scope.existingPlaylists[0].uri;
            }
        };

        // var url = "../mock_responses/existing_playlists.json"
        var url = "/api/playlist/existing";
        rest.getData(url, null, successHandler,
            "Unable to retrieve existing playlists for current user");
    };

    /**
     * String format util function.
     * @param               template string to format
     * @param               replacements string replacement arguments
     * @returns {string}    formatted string
     */
    var _formatString = function (template, replacements) {
        var replaceFunction = function (match, number) {
            if (typeof replacements[number] !== 'undefined') {
                return replacements[number];
            } else {
                return match;
            }
        };

        return template.replace(/{(\d+)}/g, replaceFunction);
    };

    /**
     * Initialize the application scope.
     */
    var _init = function () {
        if ($scope.playlists === null || $scope.playlists === "") {
            _getPlaylists();
            _getExistingPlaylists();
        }
    };

    _init();
}]);