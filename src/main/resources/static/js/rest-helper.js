/**
 * Service to modularize making ajax rest calls.
 */
app.service('rest', ['$http', '$mdToast', '$rootScope', 
    function ($http, $mdToast, $rootScope) {

    /**
     * Make a rest call for the following parameters; handles showing loading spinner
     * while request is being made, and showing toast upon rest failure.
     *
     * @param method         request method (eg: GET, POST, PUT, DELETE)
     * @param url            url to call
     * @param params         parameters to append to request url
     * @param data           request body
     * @param successHandler function to be executed upon success
     * @param errorMessage   error message to be shown in toast upon failure
     */
    var _executeHttpRequest = function (method, url, params, data, successHandler, errorMessage) {
        $rootScope.isLoading = true;
        $http({
            'method': method,
            'url': url,
            'params': params,
            'data': data
        }).then(successHandler, function () {
            $mdToast.show($mdToast.simple()
                .textContent(errorMessage)
                .position("bottom left")
                .hideDelay(3000));
        }).finally(function () {
            $rootScope.isLoading = false;
        });
    };

    /**
     * Make a GET request for the given parameters.
     * @param url            url to call
     * @param params         parameters to append to request url
     * @param successHandler function to be executed upon success
     * @param errorMessage   error message to be shown in toast upon failure
     */
    this.getData = function (url, params, successHandler, errorMessage) {
        _executeHttpRequest('GET', url, params, null, successHandler, errorMessage);
    };

    /**
     * Make a POST request for the given parameters.
     * @param url            url to call
     * @param params         parameters to append to request url
     * @param data           request body
     * @param successHandler function to be executed upon success
     * @param errorMessage   error message to be shown in toast upon failure
     */
    this.postData = function (url, params, data, successHandler, errorMessage) {
        _executeHttpRequest('POST', url, params, data, successHandler, errorMessage);
    };

    /**
     * Make a DELETE request for the given parameters.
     * @param url            url to call
     * @param params         parameters to append to request url
     * @param successHandler function to be executed upon success
     * @param errorMessage   error message to be shown in toast upon failure
     */
    this.deleteData = function (url, params, successHandler, errorMessage) {
        _executeHttpRequest('DELETE', url, params, null, successHandler, errorMessage);
    };
}]);