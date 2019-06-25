import requests
import argparse

# ap = argparse.ArgumentParser()
# ap.add_argument("-i", "--image", required=True, help="path to input image file")
# args = vars(ap.parse_args())

# img = args['image']
def detect(img_path):
    s = requests.Session()
    r = s.put('http://45.32.39.232:8030/', params={'another_string': img_path})
    r = s.get('http://45.32.39.232:8030/')
    return r.text