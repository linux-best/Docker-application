from flask import Flask, jsonify
import psycopg2
import redis
import os

app = Flask(__name__)

# Redis
redis_client = redis.Redis(host="redis", port=6379, decode_responses=True)

# Postgres connection
def get_db_connection():
    return psycopg2.connect(
        dbname=os.getenv("POSTGRES_DB"),
        user=os.getenv("POSTGRES_USER"),
        password=os.getenv("POSTGRES_PASSWORD"),
        host="db"
    )

@app.route("/")
def index():
    # Try cache
    cached = redis_client.get("greeting")
    if cached:
        return jsonify({"msg": cached, "source": "cache"})

    # Fetch from DB
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute("SELECT 'Hello from PostgreSQL!'")
    result = cur.fetchone()[0]
    conn.close()

    # Save to cache
    redis_client.setex("greeting", 10, result)

    return jsonify({"msg": result, "source": "database"})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)

