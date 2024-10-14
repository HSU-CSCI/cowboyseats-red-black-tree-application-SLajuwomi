#!/usr/bin/env python3
# By Todd Dole
# generates random seat requests and deletions




from random import randint
import requests
import json



def convert_to_base62(base10_number):
    # Code adapted from https://www.askpython.com/python/examples/convert-base10-integer-base64
    base62_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    base62_representation = ""

    while base10_number > 0:
        remainder = base10_number % 62
        base62_representation = base62_chars[remainder] + base62_representation
        base10_number //= 62
    return base62_representation


def generatePerson():
    gendernum = randint(1, 101)
    url = "https://api.namefake.com/english-united-states/"
    headers = {
        'accept':'application/json'
        }

    if gendernum <= 50:
        params = {
            'gender': 'female'
        }
        url+="female/"
    else:
        params = {
            'gender': 'male'
        }
        url+="male/"
    response = requests.get(url, headers=headers)
    #print(response)

    if response.status_code == 200:
        data = json.loads(response.text)
    else:
        return -1




    return data['name']

if __name__ == '__main__':

    names = []



    for i in range(20000):
        breakit = False
        commandcode = randint(1, 101)
        if commandcode<80:
            try:
                name = generatePerson()
            except Exception as e:
                breakit = True

            if (breakit):
                i-=1
                continue
            print("a,"+name+","+str(randint(1,101))+","+str(randint(1,101)))
            names.append(name)
        else:
            del_index = randint(0,len(names)-1)
            print("d,"+names[del_index])
            del names[del_index]

