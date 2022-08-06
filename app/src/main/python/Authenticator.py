from firebase_admin import credentials
from os.path import dirname, join
from firebase_admin import db
import firebase_admin

serviceAccountKey = join(dirname(__file__), "path/to/serviceAccountKey.json")

cred = credentials.Certificate(serviceAccountKey)

def get_phone_number(voter_id):

    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://wevote-2c7c4-default-rtdb.asia-southeast1.firebasedatabase.app/',
        'databaseAuthVariableOverride': {
            'uid': 'voter-authenticator'
        }
    })
    ref = db.reference('/phoneNumber/{}'.format(voter_id))
    data = ref.get()
    firebase_admin.delete_app(firebase_admin.get_app())
    return data