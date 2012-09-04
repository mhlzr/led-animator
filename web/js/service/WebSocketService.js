/**
 * @author Matthieu Holzer
 */
function WebSocketService() {

    "use strict";

    this.readValues = function () {

        this.connection = null;
        this.webSocketSupported = Modernizr.websockets;
        this.webSocketIsConnected = false;
		this.useAjaxFallback = false;

        this.webSocketEnabled = ko.observable( !! $.jStorage.get("webSocketEnabled", "true"));
        this.webSocketHost = ko.observable($.jStorage.get("webSocketHost", "localhost"));
        this.webSocketPath = ko.observable($.jStorage.get("webSocketPath", "/webSocket"));
        this.webSocketScheme = ko.observable($.jStorage.get("webSocketScheme", "ws"));
        this.webSocketPort = ko.observable($.jStorage.get("webSocketPort", "8080"));
    };

    this.readValues();

    this.connect = function (onSuccess, onMessage, onError) {
		


        if (this.webSocketSupported && this.webSocketEnabled()) {

            var connectionString = this.webSocketScheme() + "://" + this.webSocketHost() + ":" + this.webSocketPort() + this.webSocketPath();

            if (window.WebSocket) {
                this.connection = new WebSocket(connectionString);
            } else if (window.MozWebSocket) {
                this.connection = new MozWebSocket(connectionString);
            } else {
                this.connection = null;
				return this;
            }


            this.connection.onmessage = onMessage;

            this.connection.onerror = function () {
                onError.call();
				this.useAjaxFallback = true;
                this.webSocketIsConnected = false;
                this.connection = null;
            };

            this.connection.onopen = function () {
                onSuccess.call();
                this.webSocketIsConnected = true;
            };

        }
		else{
			this.useAjaxFallback = true;
		}

        return this;

    };


    this.disconnect = function () {
		
		if(this.useAjaxFallback) return this;
		
        this.connection.close();
        this.connection = null;
        this.webSocketIsConnected = false;
        return this;
    };

    this.saveSettingsToLocalStorage = function () {

        if (!$.jStorage.storageAvailable()) {
            return this;
        }

        $.jStorage.set("webSocketEnabled", this.webSocketEnabled());
        $.jStorage.set("webSocketHost", this.webSocketHost());
        $.jStorage.set("webSocketPath", this.webSocketPath());
        $.jStorage.set("webSocketScheme", this.webSocketScheme());
        $.jStorage.set("webSocketPort", this.webSocketPort());

        return this;
    };

    this.resetDefaultSettings = function () {
        $.jStorage.flush();
        this.readValues();

        return this;
    };

    this.send = function (data) {
		
		if(this.useAjaxFallback){
			this.sendViaAjax(data);
		}
		
        if (!this.webSocketEnabled()) {
            return this;
        }

        if (this.connection === null) {
            this.connect();
			return this;
        }

        if (this.webSocketEnabled() && this.connection.readyState === this.connection.OPEN) {
            this.connection.send(data);
        }

        return this;
    };
	
	this.sendViaAjax = function(dataToSend){
		
		$.ajax({
				type : "POST",
				url: "http://" + this.webSocketHost + ":8080/ajax",
				dataType : "json",
				data : {
					data : dataToSend
					},
				success: function(response){

				},
				error : function(xhr, status, error){
					
				}
			});
	}
}