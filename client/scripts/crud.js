/**
 * Create a new CRUD resource.
 * @param url URL to the resource.
 * @param children Resource paths of children.
 * @constructor
 */
function CRUDResource(url, children) {
    /**
     * Absolute URL of the REST resource.
     */
    this.url = url;

    // Map the children resources
    children = children || [];
    var resource = this;
    children.forEach(function (element) {
        resource[element] = new CRUDResource(url + "/" + element);
    });

    /**
     * Build an async request.
     * @param method Request method.
     * @param parameters Request parameters.
     * @param suffix URL suffix for the request.
     */
    this.request = function (method, parameters, suffix) {
        // Make sure that suffix if valid
        if(suffix)
            suffix = "/" + suffix;
        else
            suffix = "";

        var resource = this;
        return new Promise(function (resolve, reject) {

            var request = new XMLHttpRequest();

            // Set the request parameters
            var body = "";
            if(method != "GET") {
                request.open(method, resource.url + suffix, true);
                request.setRequestHeader("Content-type", "application/json");
                body = JSON.stringify(parameters);
            }
            else {
                suffix += "?";
                Object.keys(parameters).forEach(function (key) {
                    suffix += encodeURI(key) + "=" + encodeURI(parameters[key]) + "&";
                });
                suffix = suffix.substring(0, suffix.length - 1);
                request.open(method, resource.url + suffix, true);
            }

            request.onload = function () {
                // An error returned by the server
                if(this.status >= 400) {
                    //if(this.getResponseHeader("Content-type") == "application/json")
                        reject(JSON.parse(this.responseText));
                    //else
                    //    reject(new ResourceError(this.status, this.responseText));
                    return;
                }

                // Command was successful
                //var response = this.getResponseHeader("Content-type") == "application/json" ? JSON.parse(this.responseText) : this.responseText;
                var response = {};
                if(this.responseText.trim() != "")
                    response = JSON.parse(this.responseText);
                resolve(response);
            };
            request.onerror = function () {
                reject(new NetworkError());
            };

            request.send(body);
        });
    };

    /**
     * Create a new resource.
     * @param parameters Creation options.
     */
    this.create = function (parameters) {
        return this.request("POST", parameters);
    };

    /**
     * Retrieve the existing resource.
     * @param id Unique identifier of the resource.
     * @param parameters Retrieval parameters.
     */
    this.retrieve = function (id, parameters) {
        return this.request("GET", parameters, id);
    };

    /**
     * Update the existing resource.
     * @param id Unique identifier of the resource.
     * @param parameters Update parameters.
     */
    this.update = function (id, parameters) {
        return this.request("PUT", parameters, id);
    };

    /**
     * Remove the existing resource.
     * @param id Unique identifier of the resource.
     */
    this.delete = function (id) {
        return this.request("DELETE", {}, id);
    };
}

/**
 * Create a new network error.
 * @constructor
 */
function NetworkError() {
    this.name = "NetworkError";
}
NetworkError.prototype = Object.create(Error.prototype);

/**
 * Create a new resource error.
 * @param code HTTP Status code.
 * @param message Error message.
 * @constructor
 */
function ResourceError(code, message) {
    this.name = "ResourceError";

    /**
     * HTTP Status code for the error.
     */
    this.code = code;
    /**
     * Message returned by the server along with the error.
     */
    this.message = message;
}
ResourceError.prototype = Object.create(Error.prototype);