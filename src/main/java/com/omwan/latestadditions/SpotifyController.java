package com.omwan.latestadditions;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Autowired
    @Qualifier("spotify")
    private SpotifyApi spotifyApi;

    @RequestMapping(method = RequestMethod.GET, value = "/init")
    @ResponseBody
    public void initialize(HttpServletResponse response) {
        List<String> scopes = Arrays.asList(
                "playlist-read-private",
                "playlist-modify-private",
                "playlist-modify-public",
                "playlist-read-collaborative");

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(String.join(", ", scopes))
                .show_dialog(true)
                .build();
        URI redirectURI = authorizationCodeUriRequest.execute();

        try {
            response.sendRedirect(redirectURI.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not redirect to permissions page", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/accesstoken")
    public void getToken(@RequestParam(name = "code") String token, HttpServletResponse response) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(token)
                .build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            response.sendRedirect("/api/spotify/playlists");
        } catch (IOException | SpotifyWebApiException e) {
            throw new RuntimeException("Could not retrieve auth code credentials", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/userinfo")
    @ResponseBody
    public User getUserInfo() {
        try {
            return spotifyApi.getCurrentUsersProfile().build().execute();
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to retrieve user info", e);
        }
    }
}
