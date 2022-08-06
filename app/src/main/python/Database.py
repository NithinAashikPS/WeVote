from firebase_admin import credentials
from os.path import dirname, join
from firebase_admin import db
import firebase_admin

serviceAccountKey = join(dirname(__file__), "path/to/serviceAccountKey.json")

cred = credentials.Certificate(serviceAccountKey)

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://wevote-2c7c4-default-rtdb.asia-southeast1.firebasedatabase.app/',
    'databaseAuthVariableOverride': {
        'uid': 'get-party'
    }
})


def get_party(party_id):
    ref = db.reference('/parties/{}'.format(party_id))
    data = ref.get()
    return data


def get_candidate(candidate_id):
    ref = db.reference('/candidates/{}'.format(candidate_id))
    data = ref.get()
    return data


def get_voter_detail(uid):
    ref = db.reference('/voters/{}'.format(uid))
    data = ref.get()
    return data['voterName']


def get_epic_number(uid):
    ref = db.reference('/voters/{}'.format(uid))
    data = ref.get()
    return data['epicNumber']


def get_voter_photo(uid):
    ref = db.reference('/voters/{}'.format(uid))
    data = ref.get()
    return data['photo']


def get_election_details(candidate_id, party_id):
    data = [get_party(party_id), get_candidate(candidate_id)]
    return data


def get_candidate_and_party(candidate_id):
    candidate = get_candidate(candidate_id)
    party = get_party(candidate['party'])
    data = [party, candidate]
    return data
