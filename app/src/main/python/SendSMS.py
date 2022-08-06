from twilio.rest import Client

account_sid = "ACd77f50bac51de168a14dd9f2cc60481f"
auth_token = "ece5dd6d665f84d9de582d57543644cc"


def send(to, msg):
    print("JHHFGJHFGJG")
    print(to)
    print(msg)
    client = Client(account_sid, auth_token)
    asd = client.messages.create(
        to=to,
        from_="+17579977060",
        body=msg)
    print(asd)