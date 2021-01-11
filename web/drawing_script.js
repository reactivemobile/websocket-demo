function connect(ip_address) {

    var canvas = document.getElementById('canvas');
    var status = document.getElementById('status');

    if (canvas.getContext) {
        var context = canvas.getContext('2d');

        status.innerHTML = "Connecting";
        var webSocket = new WebSocket("ws://" + ip_address + ":8080/");

        var lineIndex = 0
        var scale = 0

        webSocket.onopen = function(frame) {
            webSocket.send("Ready")
            status.innerHTML = "Connected to " + ip_address + ":8080";
        };

        webSocket.onmessage = function(frame) {
            handleFrame(frame)
            return false
        }

        webSocket.onclose = function(frame) {
            console.log("Socket disconnected " + frame)
            status.innerHTML = "Socket was disconnected";
        };
    };

    function handleFrame(frame) {
        var content = frame.data

        if (content.startsWith("start")) {
            handleStartFrame(content)
        } else
        if (content == "clear") {
            clearCanvas();
            lineIndex = 0;
        } else
        if (content == "pointer_lifted") {
            lineIndex = 0;
        } else {
            var point = content.split(",")
            var x = point[0] * scale
            var y = point[1] * scale

            if (lineIndex == 0) {
                context.beginPath();
                context.moveTo(x, y)
            } else {
                context.lineTo(x, y);
                context.stroke()
            }

            lineIndex++
        }
    }

    // Scale the canvas to match ratio of device screen
    function handleStartFrame(content) {
        var deviceCanvasSize = content.split(",");
        var width = deviceCanvasSize[1];
        var height = deviceCanvasSize[2];
        console.log("Device canvas is " + width + " x " + height);

        if (parent.innerHeight < height) {
            scale = Math.min(parent.innerWidth / width, parent.innerHeight / height) / 2
        } else {
            scale = Math.min(width / parent.innerWidth, height / parent.innerHeight) / 2
        }

        canvas.width = width * scale;
        canvas.height = height * scale;

        clearCanvas()

        console.log("Ready");
    }

    function clearCanvas() {
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = 'rgba(155,155,255,1.0)';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = 'rgba(255,155,155,1.0)';
        context.beginPath();
    }
}