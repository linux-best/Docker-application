from flask import Flask,render_template
from flask_socketio import SocketIO,send


def code():
    app = Flask(__name__)
    app.config["SECRET_KEY"] = "secret!"
    socketio = SocketIO(app,cors_allowed_origins="*",message_queue="redis://redis:6379/0")
    
    @app.route("/")
    def index():
        return "Realtime chat api is running !"
    
    @socketio.on("message")
    def handle_message(msg):
        print(f"Recieved message: {msg}")
        send(msg ,broadcast=True)
    
    if __name__ == "__main__" :
        socketio.run(app,host="0.0.0.0",port=5150)
    

