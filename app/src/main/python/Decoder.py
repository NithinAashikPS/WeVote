import jwt
import json


def decode(cipher_text, key):
    return jwt.decode(cipher_text, key, algorithms=["HS256"])


def encode(data, key):
    # asd = json.dumps(data)
    # print(asd)
    return jwt.encode(json.loads(data), key, algorithm="HS256")
