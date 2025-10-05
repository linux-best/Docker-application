FROM python:3.10-slim

WORKDIR /app

COPY app/requierments.txt .

RUN pip3 install --no-cache-dir -r requierments.txt

COPY app/ .

CMD [ "python3","application.py"]


