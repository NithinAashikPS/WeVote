import cv2
import numpy as np
import urllib.request
import face_recognition

def encode_face(face):

    try:
        encode = face_recognition.face_encodings(face)[0]
        return encode
    except:
        return encode_face(cv2.rotate(face, cv2.cv2.ROTATE_90_CLOCKWISE))

def match_face(camera_image_bytes, voter_image_link):

    camera_image = bytearray(camera_image_bytes)
    camera_image_decoded = cv2.imdecode(np.frombuffer(camera_image, np.uint8), -1)
    camera_image_encoding = encode_face(camera_image_decoded)

    voter_image = face_recognition.load_image_file(urllib.request.urlopen(voter_image_link))
    voter_image_encoding = encode_face(voter_image)

    results = face_recognition.compare_faces([voter_image_encoding], camera_image_encoding, 0.7)

    return results[0]