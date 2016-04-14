import json
import urllib2
import sys

data = {}

ip = sys.argv[1]

fake_image = []
for i in range(784):
    fake_image.append(0.0)

data['x'] = fake_image

req = urllib2.Request("http://" + ip + "/svm_predict")
req.add_header('Content-Type', 'application/json')

response = urllib2.urlopen(req, json.dumps(data))
