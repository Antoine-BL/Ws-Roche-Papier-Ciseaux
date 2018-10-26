class WebSocketClient {
    constructor(readFrom, displayTo){
        this.readFrom = readFrom;
        this.displayTo = displayTo;
        this.stompClient = null;
    }

    static get COMMAND_PREFIX() {
        return '/';
    }

    static get PARAM_SEPARATOR() {
        return ' ';
    }

    static serializeMessage(message){
        return JSON.stringify({
            texte: message
        });
    }

    static serializeCommand(commande){
        console.log(commande);
        return JSON.stringify({
            parametres: commande.params,
            typeCommande: commande.name.toUpperCase(),
            de: {
                id: 1,
                courriel: "admin@admin.ca",
                motPasse: "admin",
                alias: "admin",
                role: "VENERABLE",
                groupe: "BLANC",
            }
        });
    }

    connect(ws, callback) {
        const socket = new SockJS(ws);
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, callback);
    }

    disconnect(callback) {
        this.stompClient.disconnect();
        this.stompClient = null;
        callback();
    }

    showMessage(message, displayTo, cssClass) {
        displayTo.append( message.texte + '<br/>');
    }

    get isCommand() {
        try {
            return this.read().indexOf(WebSocketClient.COMMAND_PREFIX) === 0;
        } catch (e) {
            return false;
        }
    }

    readCommand() {
        if (!this.isCommand) throw 'not a command!';
        const input = this.read();

        const commandNoPrefix = input.substring(WebSocketClient.COMMAND_PREFIX.length);
        const params = commandNoPrefix.split(WebSocketClient.PARAM_SEPARATOR);

        const COMMAND_NAME = 0;

        return {
            name: params[COMMAND_NAME],
            params: params.slice(COMMAND_NAME + 1)
        };
    }

    sendCommandTo(topic) {
        this.stompClient.send(
            topic,
            {},
            WebSocketClient.serializeCommand(this.readCommand())
        );

        this.clear();
    }

    sendTo(topic) {
        this.stompClient.send(
            topic,
            {},
            WebSocketClient.serializeMessage(this.read())
        );

        this.clear();
    }

    clear() {
        this.readFrom.val('')
    }

    read() {
        return this.readFrom.val();
    }

    subscribeTo(topic, cssClass) {
        this.stompClient.subscribe(topic, this.generateSubscribeHandler(cssClass));
    }

    generateSubscribeHandler(cssClass) {
        const fct = this.showMessage;
        const displayTo = this.displayTo;
        return function (message) {
            fct(JSON.parse(message.body), displayTo, cssClass);
        }
    }
}