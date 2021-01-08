function connect(ip_address) {

    var canvas = document.getElementById('canvas');

    if (canvas.getContext) {
        var webSocket = new WebSocket("ws://" + ip_address + ":8080/");

        var context = canvas.getContext('2d');
        var status = document.getElementById('status')
        status.innerHTML = "Connecting";

        webSocket.onopen = function(frame) {
            status.innerHTML = "Connected";
            clearCanvas();
        };

        var indexOfFrames = 0
        var previousX = 0
        var previousY = 0

        var phoneCanvasWidth = 0
        var phoneCanvasHeight = 0

        var scale = 0

        webSocket.onmessage = function(frame) {
            var content = frame.data

            if (content.startsWith("start")) {
                var deviceCanvasSize = content.split(",");
                var width = deviceCanvasSize[1];
                var height = deviceCanvasSize[2];
                console.log("Device canvas is " + width + " x " + height);
                scale = Math.min(width / parent.innerWidth, height / parent.innerHeight) / 4

                canvas.width = width * scale;
                canvas.height = height * scale;

                clearCanvas()

                return
            }

            if (content == "clear") {
                clearCanvas();
                indexOfFrames = 0;
                return;
            }

            if (content == "pointer_lifted") {
                indexOfFrames = 0;
                return;
            }

            var point = content.split(",")
            var x = point[0] * scale
            var y = point[1] * scale

            if (indexOfFrames == 0) {
                context.beginPath();
                context.moveTo(x, y)
            } else {
                context.lineTo(x, y);
                context.stroke()
            }

            indexOfFrames++
        }
        webSocket.onclose = function(frame) {
            status.innerHTML = "Connection closed";
        };
    };

    function clearCanvas() {
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = 'rgba(155,155,255,1.0)';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.fillStyle = 'rgba(255,155,155,1.0)';
        context.beginPath();
    }
}