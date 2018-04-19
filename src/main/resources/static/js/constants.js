/**
 * Constants containing REST API endpoints to be called by Angular controller.
 */
app.constant('endpoints', {
    GET_PLAYLISTS: "/api/playlists",
    BUILD_PLAYLIST: "/api/playlists",
    GET_PLAYLIST_DETAILS: "/api/playlists/{0}",
    GET_EXISTING_PLAYLISTS: "/api/saved",
    DELETE_EXISTING_PLAYLIST: "/api/saved/{0}"
});