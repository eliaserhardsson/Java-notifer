var net = require('net');
var http = require("http");
var url = require('url');

var headline = 0;
var text = 0;

var server = net.createServer(function(socket) {
	socket.setEncoding('utf8')

	setInterval(function () {
		if (headline != 0 && text != 0) {
			console.log("send");
			socket.write(headline + "|" + text + "|2000\r\n");
			headline = 0;
			text = 0;
		}
	}, 200);
});

server.listen(1337, '127.0.0.1');

var httpServer = http.createServer(function(req, res) {
	var query = url.parse(req.url, true).query;
	if (query.headline != "" && query.text != "") {
		headline = query.headline;
		text = query.text;
  	res.writeHead(200, {"Content-Type": 'text/plain; charset=utf-8'});
		res.end("sent");
	} else {
		res.end("error");
	}
  //response.end('nästa medelande!|eller hur|2000\r\n');
});

httpServer.listen(8081);
