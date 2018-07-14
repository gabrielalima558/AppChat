var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io').listen(http);


io.sockets.on('connection', function(socket) {
	console.log('conectado: ' + socket.id);

        socket.emit('mensagem_cliente', socket.id + ' conectado');
        socket.broadcast.emit('mensagem_cliente', socket.id + ' conectado');

        socket.on('mensagem_servidor', function(data) {
                console.log('recebido: ' + data);

                socket.emit('mensagem_cliente', data);
                socket.broadcast.emit('mensagem_cliente', data);
            });

        socket.on('disconnect', function() {
                console.log('desconectado: ' + socket.id);
            });

});

app.get('/', function(req, res) {
	res.sendFile(__dirname + '\\index.html');
});

http.listen(8100, function() {
	console.log('Servidor funcionando!');
});